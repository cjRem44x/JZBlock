// Copyright 2024 JZBlock Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// ============================================================================
// engine.zig - Core Game Logic
// ============================================================================
// Manages all game mechanics including entities, waves, combat, economy,
// and game state transitions.
// ============================================================================

const std = @import("std");
const rl = @import("raylib");
const config = @import("config.zig");
const GameState = @import("game_state.zig").GameState;
const Player = @import("player.zig").Player;
const Zombie = @import("zombie.zig").Zombie;
const Lazar = @import("lazar.zig").Lazar;
const Direction = @import("lazar.zig").Direction;

// Use the managed ArrayList variant that stores the allocator
const ZombieList = std.ArrayList(Zombie);
const LazarList = std.ArrayList(Lazar);

pub const Engine = struct {
    // Allocator for dynamic lists
    allocator: std.mem.Allocator,

    // Entities
    player: Player,
    zombies: std.ArrayListUnmanaged(Zombie),
    lazars: std.ArrayListUnmanaged(Lazar),

    // Random number generator (must store the PRNG itself, not just the interface)
    prng: std.Random.DefaultPrng,

    // Wave system
    wave: i32,
    zombie_count: i32, // Zombies to spawn per wave
    kills: i32,

    // Economy
    currency: i32, // ZZZ
    upgrade_cost: i32,
    damage_multiplier: i32, // lazar_f

    // Ammo system
    ammo: i32,
    max_ammo: i32,
    reloading: bool,
    reload_timer: f32,

    // Healing
    heal_timer: f32,

    // Dead zombie cleanup
    cleanup_timer: f32,

    // Fire rate
    fire_timer: f32,
    fire_cooldown: f32, // Decreases with upgrades

    // Countdown timer
    countdown_timer: f32,
    countdown_active: bool,

    // Game state
    state: *GameState,

    pub fn init(allocator: std.mem.Allocator, state: *GameState) !Engine {
        const seed: u64 = blk: {
            var s: u64 = undefined;
            std.posix.getrandom(std.mem.asBytes(&s)) catch {
                s = @intCast(std.time.milliTimestamp());
            };
            break :blk s;
        };

        var engine = Engine{
            .allocator = allocator,
            .player = Player.init(),
            .zombies = .{},
            .lazars = .{},
            .prng = std.Random.DefaultPrng.init(seed),
            .wave = 1,
            .zombie_count = 1,
            .kills = 0,
            .currency = 0,
            .upgrade_cost = config.UPGRADE_COST,
            .damage_multiplier = 1,
            .ammo = config.LAZAR_AMMO,
            .max_ammo = config.LAZAR_AMMO,
            .reloading = false,
            .reload_timer = 0.0,
            .heal_timer = 0.0,
            .cleanup_timer = 0.0,
            .fire_timer = 0.0,
            .fire_cooldown = config.FIRE_COOLDOWN,
            .countdown_timer = @floatFromInt(config.COUNTDOWN_SECONDS),
            .countdown_active = true,
            .state = state,
        };

        // Spawn first wave
        try engine.spawnWave();

        return engine;
    }

    pub fn deinit(self: *Engine) void {
        self.zombies.deinit(self.allocator);
        self.lazars.deinit(self.allocator);
    }

    pub fn update(self: *Engine, delta: f32) !void {
        // Handle countdown
        if (self.countdown_active) {
            self.countdown_timer -= delta;
            if (self.countdown_timer <= 0.0) {
                self.countdown_active = false;
            }
            return; // Don't update game during countdown
        }

        // Update fire cooldown
        if (self.fire_timer > 0.0) {
            self.fire_timer -= delta;
        }

        // Update player
        self.player.update(delta);

        // Update zombies
        for (self.zombies.items) |*zombie| {
            zombie.update(delta);
            zombie.chase(&self.player, delta);
            zombie.keepInBounds();
        }

        // Apply zombie separation (anti-bunching)
        for (self.zombies.items) |*zombie| {
            for (self.zombies.items) |*other| {
                zombie.applySeparation(other);
            }
        }

        // Update lazars
        for (self.lazars.items) |*lazar| {
            lazar.update(delta);
        }

        // Check collisions
        self.checkZombiePlayerCollisions(delta);
        self.checkLazarZombieCollisions();

        // Clean up hit lazars and off-screen lazars
        self.cleanupLazars();

        // Check for zombie deaths and award currency
        self.processZombieDeaths();

        // Clean up dead zombies periodically
        self.cleanup_timer += delta;
        if (self.cleanup_timer >= 4.0) {
            self.cleanupDeadZombies();
            self.cleanup_timer = 0.0;
        }

        // Handle healing
        if (self.player.health < config.PLAYER_HEALTH and self.player.isAlive()) {
            self.heal_timer += delta;
            if (self.heal_timer >= config.HEAL_INTERVAL) {
                self.player.heal(config.HEAL_AMOUNT);
                self.heal_timer = 0.0;
            }
        }

        // Handle reloading
        if (self.reloading) {
            self.reload_timer += delta;
            if (self.reload_timer >= config.RELOAD_TIME) {
                self.ammo = self.max_ammo;
                self.reloading = false;
                self.reload_timer = 0.0;
            }
        }

        // Check for wave completion
        if (self.countAliveZombies() == 0) {
            try self.nextWave();
        }

        // Check for player death
        if (!self.player.isAlive()) {
            self.state.* = .game_over;
        }

        // Handle input
        try self.handleInput();
    }

    fn handleInput(self: *Engine) !void {
        // Shooting with arrow keys (with fire rate limiting)
        if (self.fire_timer <= 0.0) {
            var shot = false;
            if (rl.isKeyDown(.up)) {
                try self.blast(.up);
                shot = true;
            } else if (rl.isKeyDown(.down)) {
                try self.blast(.down);
                shot = true;
            } else if (rl.isKeyDown(.left)) {
                try self.blast(.left);
                shot = true;
            } else if (rl.isKeyDown(.right)) {
                try self.blast(.right);
                shot = true;
            }
            if (shot) {
                self.fire_timer = self.fire_cooldown;
            }
        }

        // Reload with CTRL
        if (rl.isKeyPressed(.left_control) or rl.isKeyPressed(.right_control)) {
            self.reload();
        }

        // Upgrade with U
        if (rl.isKeyPressed(.u)) {
            self.upgrade();
        }

        // Pause with ESC
        if (rl.isKeyPressed(.escape)) {
            self.state.* = .paused;
        }
    }

    fn checkZombiePlayerCollisions(self: *Engine, delta: f32) void {
        _ = delta;
        const player_rect = self.player.getRect();

        for (self.zombies.items) |*zombie| {
            if (zombie.isCollidingWith(player_rect) and zombie.canHitPlayer()) {
                self.player.takeDamage(config.ZOMBIE_DAMAGE);
                zombie.resetHitTimer();
                self.heal_timer = 0.0; // Reset heal timer on damage
            }
        }
    }

    fn checkLazarZombieCollisions(self: *Engine) void {
        for (self.lazars.items) |*lazar| {
            if (lazar.is_hit) continue;

            const lazar_rect = lazar.getRect();

            for (self.zombies.items) |*zombie| {
                if (zombie.is_dead) continue;

                if (rl.checkCollisionRecs(lazar_rect, zombie.getRect())) {
                    // Deal damage based on multiplier
                    zombie.takeDamage(lazar.damage * self.damage_multiplier);
                    lazar.hit();
                    break;
                }
            }
        }
    }

    fn cleanupLazars(self: *Engine) void {
        var i: usize = 0;
        while (i < self.lazars.items.len) {
            const lazar = &self.lazars.items[i];
            if (lazar.is_hit or lazar.isOffScreen()) {
                _ = self.lazars.swapRemove(i);
            } else {
                i += 1;
            }
        }
    }

    fn processZombieDeaths(self: *Engine) void {
        for (self.zombies.items) |*zombie| {
            if (zombie.health <= 0 and !zombie.is_dead) {
                zombie.die();
                self.kills += 1;
                self.currency += config.KILL_REWARD;
            }
        }
    }

    fn cleanupDeadZombies(self: *Engine) void {
        var i: usize = 0;
        while (i < self.zombies.items.len) {
            if (self.zombies.items[i].is_dead) {
                _ = self.zombies.swapRemove(i);
            } else {
                i += 1;
            }
        }
    }

    fn countAliveZombies(self: *Engine) i32 {
        var count: i32 = 0;
        for (self.zombies.items) |zombie| {
            if (!zombie.is_dead) count += 1;
        }
        return count;
    }

    fn spawnWave(self: *Engine) !void {
        // Calculate extra health for this wave
        const extra_health = (self.wave - 1) * config.ZOMBIE_HEALTH_INC;

        var i: i32 = 0;
        while (i < self.zombie_count) : (i += 1) {
            const zombie = Zombie.initWithHealth(self.prng.random(), extra_health);
            try self.zombies.append(self.allocator, zombie);
        }
    }

    fn nextWave(self: *Engine) !void {
        self.wave += 1;

        // Calculate next wave zombie count (1.5x previous)
        if (self.zombie_count == 1) {
            self.zombie_count = 2;
        } else {
            self.zombie_count = @min(
                self.zombie_count + @divTrunc(self.zombie_count, 2),
                config.MAX_ZOMBIES,
            );
        }

        try self.spawnWave();
    }

    pub fn blast(self: *Engine, dir: Direction) !void {
        if (self.ammo <= 0 or self.reloading) return;

        // Spawn lazar at player center
        const lazar = Lazar.init(
            self.player.getCenterX() - config.LAZAR_SIZE / 2.0,
            self.player.getCenterY() - config.LAZAR_SIZE / 2.0,
            dir,
        );

        try self.lazars.append(self.allocator, lazar);
        self.ammo -= 1;
    }

    pub fn reload(self: *Engine) void {
        if (self.ammo >= self.max_ammo or self.reloading) return;
        if (self.ammo > 0) return; // Only reload when empty (like Java version)

        self.reloading = true;
        self.reload_timer = 0.0;
    }

    pub fn upgrade(self: *Engine) void {
        if (self.currency >= self.upgrade_cost) {
            self.currency -= self.upgrade_cost;
            self.damage_multiplier *= 2;
            self.upgrade_cost *= 2;
            self.max_ammo *= 2;

            // Increase fire rate (reduce cooldown, with minimum cap)
            self.fire_cooldown = @max(config.FIRE_COOLDOWN_MIN, self.fire_cooldown * config.FIRE_RATE_MULTIPLIER);
        }
    }

    pub fn restart(self: *Engine) !void {
        // Clear entities
        self.zombies.clearRetainingCapacity();
        self.lazars.clearRetainingCapacity();

        // Reset player
        self.player.reset();

        // Reset wave system
        self.wave = 1;
        self.zombie_count = 1;
        self.kills = 0;

        // Reset economy
        self.currency = 0;
        self.upgrade_cost = config.UPGRADE_COST;
        self.damage_multiplier = 1;

        // Reset ammo
        self.ammo = config.LAZAR_AMMO;
        self.max_ammo = config.LAZAR_AMMO;
        self.reloading = false;
        self.reload_timer = 0.0;

        // Reset timers
        self.heal_timer = 0.0;
        self.cleanup_timer = 0.0;
        self.fire_timer = 0.0;
        self.fire_cooldown = config.FIRE_COOLDOWN;
        self.countdown_timer = @floatFromInt(config.COUNTDOWN_SECONDS);
        self.countdown_active = true;

        // Spawn first wave
        try self.spawnWave();

        // Set state to in_game
        self.state.* = .in_game;
    }

    pub fn pause(self: *Engine) void {
        self.state.* = .paused;
    }

    pub fn unpause(self: *Engine) void {
        self.state.* = .in_game;
    }

    pub fn draw(self: *const Engine) void {
        // Draw entity shadows first (depth layer)
        self.drawShadows();

        // Draw lazars (behind other entities)
        for (self.lazars.items) |*lazar| {
            lazar.draw();
        }

        // Draw zombies with glow
        for (self.zombies.items) |*zombie| {
            zombie.draw();
        }

        // Draw player with glow
        self.player.draw();
        self.drawPlayerGlow();
    }

    fn drawShadows(self: *const Engine) void {
        const shadow_offset: i32 = 6;
        const shadow_color = config.ENTITY_SHADOW;

        // Player shadow
        rl.drawRectangle(
            @as(i32, @intFromFloat(self.player.x)) + shadow_offset,
            @as(i32, @intFromFloat(self.player.y)) + shadow_offset,
            @as(i32, @intFromFloat(self.player.size)),
            @as(i32, @intFromFloat(self.player.size)),
            shadow_color,
        );

        // Zombie shadows
        for (self.zombies.items) |zombie| {
            rl.drawRectangle(
                @as(i32, @intFromFloat(zombie.x)) + shadow_offset,
                @as(i32, @intFromFloat(zombie.y)) + shadow_offset,
                @as(i32, @intFromFloat(zombie.size)),
                @as(i32, @intFromFloat(zombie.size)),
                shadow_color,
            );
        }
    }

    fn drawPlayerGlow(self: *const Engine) void {
        const glow_size: i32 = 4;
        const glow_color = config.PLAYER_GLOW;

        rl.drawRectangle(
            @as(i32, @intFromFloat(self.player.x)) - glow_size,
            @as(i32, @intFromFloat(self.player.y)) - glow_size,
            @as(i32, @intFromFloat(self.player.size)) + glow_size * 2,
            @as(i32, @intFromFloat(self.player.size)) + glow_size * 2,
            glow_color,
        );
    }

    pub fn getLazarPower(self: *const Engine) i32 {
        return config.LAZAR_DAMAGE * self.damage_multiplier;
    }

    pub fn getCountdown(self: *const Engine) i32 {
        if (!self.countdown_active) return 0;
        const val: i32 = @intFromFloat(@ceil(self.countdown_timer));
        return @max(0, val);
    }

    pub fn isCountdownActive(self: *const Engine) bool {
        return self.countdown_active;
    }
};

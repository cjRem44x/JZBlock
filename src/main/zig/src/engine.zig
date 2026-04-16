// JZBlock — engine.zig
// Core game logic: entities, waves, combat, economy, blood effects.

const std         = @import("std");
const rl          = @import("raylib");
const config      = @import("config.zig");
const GameState   = @import("game_state.zig").GameState;
const Player      = @import("player.zig").Player;
const Zombie      = @import("zombie.zig").Zombie;
const Lazar       = @import("lazar.zig").Lazar;
const Direction   = @import("lazar.zig").Direction;
const BloodSystem = @import("blood.zig").BloodSystem;

pub const Engine = struct {
    allocator: std.mem.Allocator,

    player:  Player,
    zombies: std.ArrayListUnmanaged(Zombie),
    lazars:  std.ArrayListUnmanaged(Lazar),

    prng: std.Random.DefaultPrng,

    // Wave / economy
    wave:               i32,
    zombie_count:       i32,
    kills:              i32,
    currency:           i32,
    upgrade_cost:       i32,
    damage_multiplier:  i32,

    // Ammo
    ammo:         i32,
    max_ammo:     i32,
    reloading:    bool,
    reload_timer: f32,

    // Timers
    heal_timer:    f32,
    cleanup_timer: f32,
    fire_timer:    f32,
    fire_cooldown: f32,

    // Countdown
    countdown_timer:  f32,
    countdown_active: bool,

    // Blood effects
    blood: BloodSystem,

    // Game state (pointer to external state in main.zig)
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
            .allocator         = allocator,
            .player            = Player.init(),
            .zombies           = .{},
            .lazars            = .{},
            .prng              = std.Random.DefaultPrng.init(seed),
            .wave              = 1,
            .zombie_count      = 1,
            .kills             = 0,
            .currency          = 0,
            .upgrade_cost      = config.UPGRADE_COST,
            .damage_multiplier = 1,
            .ammo              = config.LAZAR_AMMO,
            .max_ammo          = config.LAZAR_AMMO,
            .reloading         = false,
            .reload_timer      = 0.0,
            .heal_timer        = 0.0,
            .cleanup_timer     = 0.0,
            .fire_timer        = 0.0,
            .fire_cooldown     = config.FIRE_COOLDOWN,
            .countdown_timer   = @floatFromInt(config.COUNTDOWN_SECONDS),
            .countdown_active  = true,
            .blood             = BloodSystem.init(),
            .state             = state,
        };

        try engine.spawnWave();
        return engine;
    }

    pub fn deinit(self: *Engine) void {
        self.zombies.deinit(self.allocator);
        self.lazars.deinit(self.allocator);
    }

    pub fn update(self: *Engine, delta: f32) !void {
        if (self.countdown_active) {
            self.countdown_timer -= delta;
            if (self.countdown_timer <= 0.0) self.countdown_active = false;
            return;
        }

        if (self.fire_timer > 0.0) self.fire_timer -= delta;

        self.player.update(delta);

        for (self.zombies.items) |*zombie| {
            zombie.update(delta);
            zombie.chase(&self.player, delta);
            zombie.keepInBounds();
        }
        for (self.zombies.items) |*zombie| {
            for (self.zombies.items) |*other| zombie.applySeparation(other);
        }

        for (self.lazars.items) |*lazar| lazar.update(delta);

        self.checkZombiePlayerCollisions();
        self.checkLazarZombieCollisions();
        self.cleanupLazars();
        self.processZombieDeaths();

        self.cleanup_timer += delta;
        if (self.cleanup_timer >= 4.0) {
            self.cleanupDeadZombies();
            self.cleanup_timer = 0.0;
        }

        if (self.player.health < config.PLAYER_HEALTH and self.player.isAlive()) {
            self.heal_timer += delta;
            if (self.heal_timer >= config.HEAL_INTERVAL) {
                self.player.heal(config.HEAL_AMOUNT);
                self.heal_timer = 0.0;
            }
        }

        if (self.reloading) {
            self.reload_timer += delta;
            if (self.reload_timer >= config.RELOAD_TIME) {
                self.ammo         = self.max_ammo;
                self.reloading    = false;
                self.reload_timer = 0.0;
            }
        }

        if (self.countAliveZombies() == 0) try self.nextWave();

        if (!self.player.isAlive()) self.state.* = .game_over;

        self.blood.update(delta);

        try self.handleInput();
    }

    fn handleInput(self: *Engine) !void {
        if (self.fire_timer <= 0.0) {
            var shot = false;
            if (rl.isKeyDown(.up))    { try self.blast(.up);    shot = true; }
            else if (rl.isKeyDown(.down))  { try self.blast(.down);  shot = true; }
            else if (rl.isKeyDown(.left))  { try self.blast(.left);  shot = true; }
            else if (rl.isKeyDown(.right)) { try self.blast(.right); shot = true; }
            if (shot) self.fire_timer = self.fire_cooldown;
        }

        if (rl.isKeyPressed(.left_control) or rl.isKeyPressed(.right_control)) self.reload();
        if (rl.isKeyPressed(.u)) self.upgrade();

        // ESC pauses
        if (rl.isKeyPressed(.escape)) self.state.* = .paused;
    }

    fn checkZombiePlayerCollisions(self: *Engine) void {
        const player_rect = self.player.getRect();
        for (self.zombies.items) |*zombie| {
            if (zombie.isCollidingWith(player_rect) and zombie.canHitPlayer()) {
                self.player.takeDamage(zombie.damage);
                zombie.resetHitTimer();
                self.heal_timer = 0.0;
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
                    zombie.takeDamage(lazar.damage * self.damage_multiplier);
                    lazar.hit();
                    self.blood.spawnSplatter(self.prng.random(),
                        zombie.x + zombie.size * 0.5, zombie.y + zombie.size * 0.5);
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
            } else i += 1;
        }
    }

    fn processZombieDeaths(self: *Engine) void {
        for (self.zombies.items) |*zombie| {
            if (zombie.health <= 0 and !zombie.is_dead) {
                self.kills    += 1;
                self.currency += zombie.kill_reward;
                self.blood.spawnPool(self.prng.random(),
                    zombie.x + zombie.size * 0.5, zombie.y + zombie.size * 0.5, zombie.size);
                zombie.die();
            }
        }
    }

    fn cleanupDeadZombies(self: *Engine) void {
        var i: usize = 0;
        while (i < self.zombies.items.len) {
            if (self.zombies.items[i].is_dead) _ = self.zombies.swapRemove(i)
            else i += 1;
        }
    }

    fn countAliveZombies(self: *Engine) i32 {
        var count: i32 = 0;
        for (self.zombies.items) |zombie| if (!zombie.is_dead) { count += 1; };
        return count;
    }

    fn bossCount(wave: i32, start_wave: i32, max: i32) i32 {
        if (wave < start_wave) return 0;
        return @min(wave - start_wave + 1, max);
    }

    fn spawnWave(self: *Engine) !void {
        const extra = (self.wave - 1) * config.ZOMBIE_HEALTH_INC;

        var i: i32 = 0;
        while (i < self.zombie_count) : (i += 1) {
            try self.zombies.append(self.allocator, Zombie.initWithHealth(self.prng.random(), extra));
        }

        // Boss tiers
        const n_basic = bossCount(self.wave, config.BOSS_BASIC_START_WAVE, config.BOSS_BASIC_MAX);
        const n_med   = if (self.wave >= config.BOSS_MED_START_WAVE)
            bossCount(self.wave, config.BOSS_MED_START_WAVE, config.BOSS_MED_MAX) else 0;
        const n_hard  = if (self.wave >= config.BOSS_HARD_START_WAVE)
            bossCount(self.wave, config.BOSS_HARD_START_WAVE, config.BOSS_HARD_MAX) else 0;

        var b: i32 = 0;
        while (b < n_basic) : (b += 1)
            try self.zombies.append(self.allocator, Zombie.initBoss(self.prng.random(), .basic, extra));
        var m: i32 = 0;
        while (m < n_med) : (m += 1)
            try self.zombies.append(self.allocator, Zombie.initBoss(self.prng.random(), .medium, extra));
        var h: i32 = 0;
        while (h < n_hard) : (h += 1)
            try self.zombies.append(self.allocator, Zombie.initBoss(self.prng.random(), .hard, extra));
    }

    fn nextWave(self: *Engine) !void {
        self.wave += 1;
        self.zombie_count = if (self.zombie_count == 1) 2
            else @min(self.zombie_count + @divTrunc(self.zombie_count, 2), config.MAX_ZOMBIES);
        try self.spawnWave();
    }

    pub fn blast(self: *Engine, dir: Direction) !void {
        if (self.ammo <= 0 or self.reloading) return;
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
        if (self.ammo > 0) return;
        self.reloading    = true;
        self.reload_timer = 0.0;
    }

    pub fn upgrade(self: *Engine) void {
        if (self.currency >= self.upgrade_cost) {
            self.currency          -= self.upgrade_cost;
            self.damage_multiplier *= 2;
            self.upgrade_cost      *= 2;
            self.max_ammo          *= 2;
            self.fire_cooldown      = @max(config.FIRE_COOLDOWN_MIN,
                                          self.fire_cooldown * config.FIRE_RATE_MULTIPLIER);
        }
    }

    pub fn restart(self: *Engine) !void {
        self.zombies.clearRetainingCapacity();
        self.lazars.clearRetainingCapacity();
        self.blood = BloodSystem.init();
        self.player.reset();
        self.wave              = 1;
        self.zombie_count      = 1;
        self.kills             = 0;
        self.currency          = 0;
        self.upgrade_cost      = config.UPGRADE_COST;
        self.damage_multiplier = 1;
        self.ammo              = config.LAZAR_AMMO;
        self.max_ammo          = config.LAZAR_AMMO;
        self.reloading         = false;
        self.reload_timer      = 0.0;
        self.heal_timer        = 0.0;
        self.cleanup_timer     = 0.0;
        self.fire_timer        = 0.0;
        self.fire_cooldown     = config.FIRE_COOLDOWN;
        self.countdown_timer   = @floatFromInt(config.COUNTDOWN_SECONDS);
        self.countdown_active  = true;
        self.state.*           = .in_game;
        try self.spawnWave();
    }

    pub fn pause(self: *Engine) void {
        self.state.* = .paused;
    }

    pub fn unpause(self: *Engine) void {
        self.state.* = .in_game;
    }

    pub fn draw(self: *const Engine) void {
        self.blood.drawPools();     // pools under everything
        self.drawShadows();
        for (self.lazars.items) |*lazar| lazar.draw();
        for (self.zombies.items) |*zombie| zombie.draw();
        self.player.draw();
        self.drawPlayerGlow();
        self.blood.drawParticles(); // splatter on top
    }

    fn drawShadows(self: *const Engine) void {
        const shadow_offset: i32 = 6;
        const shadow_color = config.ENTITY_SHADOW;

        rl.drawRectangle(
            @as(i32, @intFromFloat(self.player.x)) + shadow_offset,
            @as(i32, @intFromFloat(self.player.y)) + shadow_offset,
            @as(i32, @intFromFloat(self.player.size)),
            @as(i32, @intFromFloat(self.player.size)),
            shadow_color,
        );
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
        rl.drawRectangle(
            @as(i32, @intFromFloat(self.player.x)) - glow_size,
            @as(i32, @intFromFloat(self.player.y)) - glow_size,
            @as(i32, @intFromFloat(self.player.size)) + glow_size * 2,
            @as(i32, @intFromFloat(self.player.size)) + glow_size * 2,
            config.PLAYER_GLOW,
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

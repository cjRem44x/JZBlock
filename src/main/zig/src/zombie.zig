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
// zombie.zig - Zombie Entity
// ============================================================================
// Zombie enemy that chases the player with simple AI and separation behavior.
// ============================================================================

const std = @import("std");
const rl = @import("raylib");
const config = @import("config.zig");
const Player = @import("player.zig").Player;

pub const Zombie = struct {
    // Position (floats for smooth movement)
    x: f32,
    y: f32,

    // Dimensions
    size: f32,

    // Stats
    health: i32,
    speed: f32,

    // Appearance
    color: rl.Color,

    // State
    is_dead: bool,
    last_hit_time: f32, // Time since last hit to player

    pub fn init(rand: std.Random) Zombie {
        const screen_w: f32 = @floatFromInt(config.SCREEN_WIDTH);
        const screen_h: f32 = @floatFromInt(config.SCREEN_HEIGHT);

        // Random spawn position within screen bounds
        const x = rand.float(f32) * (screen_w - config.ZOMBIE_SIZE);
        const y = rand.float(f32) * (screen_h - config.ZOMBIE_SIZE);

        return .{
            .x = x,
            .y = y,
            .size = config.ZOMBIE_SIZE,
            .health = config.ZOMBIE_HEALTH,
            .speed = config.ZOMBIE_SPEED,
            .color = config.ZOMBIE_COLOR,
            .is_dead = false,
            .last_hit_time = config.ZOMBIE_HIT_COOLDOWN, // Can hit immediately
        };
    }

    pub fn initWithHealth(rand: std.Random, extra_health: i32) Zombie {
        var z = init(rand);
        z.health += extra_health;
        return z;
    }

    pub fn update(self: *Zombie, delta: f32) void {
        // Update hit cooldown
        if (self.last_hit_time < config.ZOMBIE_HIT_COOLDOWN) {
            self.last_hit_time += delta;
        }
    }

    pub fn chase(self: *Zombie, player: *const Player, delta: f32) void {
        if (self.is_dead) return;

        // Calculate direction vector to player
        const dx = player.x - self.x;
        const dy = player.y - self.y;

        // Calculate distance
        const len = @sqrt(dx * dx + dy * dy);
        if (len == 0.0) return;

        // Normalize and scale by speed
        const ux = dx / len;
        const uy = dy / len;

        self.x += ux * self.speed * delta;
        self.y += uy * self.speed * delta;
    }

    pub fn applySeparation(self: *Zombie, other: *const Zombie) void {
        if (self.is_dead or other.is_dead) return;
        if (self == other) return;

        const distX = other.x - self.x;
        const distY = other.y - self.y;
        const dist = @sqrt(distX * distX + distY * distY);

        // Push apart if too close (1.5x zombie size)
        if (dist < self.size * 1.5 and dist > 0.0) {
            self.x -= distX * 0.05;
            self.y -= distY * 0.05;
        }
    }

    pub fn keepInBounds(self: *Zombie) void {
        const screen_w: f32 = @floatFromInt(config.SCREEN_WIDTH);
        const screen_h: f32 = @floatFromInt(config.SCREEN_HEIGHT);

        if (self.x < 0) self.x = 0;
        if (self.x + self.size > screen_w) self.x = screen_w - self.size;
        if (self.y < 0) self.y = 0;
        if (self.y + self.size > screen_h) self.y = screen_h - self.size;
    }

    pub fn draw(self: *const Zombie) void {
        const x: i32 = @intFromFloat(self.x);
        const y: i32 = @intFromFloat(self.y);
        const sz: i32 = @intFromFloat(self.size);

        // Draw glow effect if alive
        if (!self.is_dead) {
            const glow_size: i32 = 3;
            rl.drawRectangle(x - glow_size, y - glow_size, sz + glow_size * 2, sz + glow_size * 2, config.ZOMBIE_GLOW);
        }

        // Draw main body
        rl.drawRectangle(x, y, sz, sz, self.color);

        // Draw highlight (top-left edge for 3D effect)
        if (!self.is_dead) {
            rl.drawRectangle(x, y, sz, 3, config.ZOMBIE_HIGHLIGHT);
            rl.drawRectangle(x, y, 3, sz, config.ZOMBIE_HIGHLIGHT);
        }

        // Draw angry face
        const eye_color = config.ZOMBIE_EYE;
        const eye_glow = config.ZOMBIE_EYE_GLOW;
        const mouth_color = config.ZOMBIE_MOUTH;

        if (!self.is_dead) {
            // Angry eyes - positioned in upper third
            const eye_size: i32 = @max(3, @divTrunc(sz, 10));
            const eye_y = y + @divTrunc(sz, 4);
            const left_eye_x = x + @divTrunc(sz, 4);
            const right_eye_x = x + sz - @divTrunc(sz, 4) - eye_size;

            // Eye glow
            rl.drawRectangle(left_eye_x - 2, eye_y - 2, eye_size + 4, eye_size + 4, eye_glow);
            rl.drawRectangle(right_eye_x - 2, eye_y - 2, eye_size + 4, eye_size + 4, eye_glow);

            // Red eyes
            rl.drawRectangle(left_eye_x, eye_y, eye_size, eye_size, eye_color);
            rl.drawRectangle(right_eye_x, eye_y, eye_size, eye_size, eye_color);

            // Angry eyebrows (slanted inward)
            const brow_color = config.ZOMBIE_EYEBROW;
            // Left eyebrow - slants down toward center
            rl.drawRectangle(left_eye_x - 2, eye_y - 4, eye_size + 4, 2, brow_color);
            rl.drawRectangle(left_eye_x + eye_size, eye_y - 6, 3, 2, brow_color);
            // Right eyebrow - slants down toward center
            rl.drawRectangle(right_eye_x - 2, eye_y - 4, eye_size + 4, 2, brow_color);
            rl.drawRectangle(right_eye_x - 3, eye_y - 6, 3, 2, brow_color);

            // Jagged mouth / grimace
            const mouth_y = y + @divTrunc(sz * 2, 3);
            const mouth_width = @divTrunc(sz, 2);
            const mouth_x = x + @divTrunc(sz, 4);

            // Dark mouth opening
            rl.drawRectangle(mouth_x, mouth_y, mouth_width, 4, mouth_color);

            // Jagged teeth
            const tooth_color = config.ZOMBIE_TEETH;
            var tx: i32 = mouth_x;
            while (tx < mouth_x + mouth_width) : (tx += 4) {
                rl.drawRectangle(tx, mouth_y, 2, 3, tooth_color);
            }
        } else {
            // Dead face - X eyes
            const eye_y = y + @divTrunc(sz, 4);
            const left_eye_x = x + @divTrunc(sz, 4);
            const right_eye_x = x + sz - @divTrunc(sz, 4) - 4;
            const dead_color = config.ZOMBIE_DEAD_FACE;

            // X eyes (two diagonal lines each)
            // Left eye X
            rl.drawLine(left_eye_x, eye_y, left_eye_x + 5, eye_y + 5, dead_color);
            rl.drawLine(left_eye_x + 5, eye_y, left_eye_x, eye_y + 5, dead_color);
            // Right eye X
            rl.drawLine(right_eye_x, eye_y, right_eye_x + 5, eye_y + 5, dead_color);
            rl.drawLine(right_eye_x + 5, eye_y, right_eye_x, eye_y + 5, dead_color);

            // Flat line mouth (dead)
            const mouth_y = y + @divTrunc(sz * 2, 3);
            const mouth_width = @divTrunc(sz, 2);
            const mouth_x = x + @divTrunc(sz, 4);
            rl.drawRectangle(mouth_x, mouth_y, mouth_width, 2, dead_color);
        }
    }

    pub fn getRect(self: *const Zombie) rl.Rectangle {
        return .{
            .x = self.x,
            .y = self.y,
            .width = self.size,
            .height = self.size,
        };
    }

    pub fn isCollidingWith(self: *const Zombie, rect: rl.Rectangle) bool {
        return rl.checkCollisionRecs(self.getRect(), rect);
    }

    pub fn takeDamage(self: *Zombie, amount: i32) void {
        self.health -= amount;
        if (self.health <= 0) {
            self.die();
        }
    }

    pub fn die(self: *Zombie) void {
        self.is_dead = true;
        self.speed = 0;
        self.color = config.ZOMBIE_DEAD_COLOR;
    }

    pub fn canHitPlayer(self: *const Zombie) bool {
        return !self.is_dead and self.last_hit_time >= config.ZOMBIE_HIT_COOLDOWN;
    }

    pub fn resetHitTimer(self: *Zombie) void {
        self.last_hit_time = 0.0;
    }
};

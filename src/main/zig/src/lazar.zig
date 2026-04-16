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
// lazar.zig - Projectile Entity
// ============================================================================
// Player projectile that travels in a straight line and damages zombies.
// ============================================================================

const rl = @import("raylib");
const config = @import("config.zig");

pub const Direction = enum {
    up,
    down,
    left,
    right,
};

pub const Lazar = struct {
    // Position (floats for smooth movement)
    x: f32,
    y: f32,

    // Dimensions
    size: f32,

    // Movement
    speed: f32,
    direction: Direction,

    // Appearance
    color: rl.Color,
    shadow_color: rl.Color,

    // State
    is_hit: bool,
    damage: i32,

    pub fn init(start_x: f32, start_y: f32, dir: Direction) Lazar {
        return .{
            .x = start_x,
            .y = start_y,
            .size = config.pwf(config.LAZAR_SIZE),
            .speed = config.pwf(config.LAZAR_SPEED),
            .direction = dir,
            .color = config.LAZAR_COLOR,
            .shadow_color = config.LAZAR_SHADOW,
            .is_hit = false,
            .damage = config.LAZAR_DAMAGE,
        };
    }

    pub fn update(self: *Lazar, delta: f32) void {
        // Move in the specified direction
        switch (self.direction) {
            .up => self.y -= self.speed * delta,
            .down => self.y += self.speed * delta,
            .left => self.x -= self.speed * delta,
            .right => self.x += self.speed * delta,
        }
    }

    pub fn draw(self: *const Lazar) void {
        const x: i32 = @intFromFloat(self.x);
        const y: i32 = @intFromFloat(self.y);
        const sz: i32 = @intFromFloat(self.size);

        // Neon glow - multiple layers for intense effect
        // Outer glow (largest, most transparent)
        const glow1: i32 = config.pw(12);
        rl.drawRectangle(x - glow1, y - glow1, sz + glow1 * 2, sz + glow1 * 2, config.LAZAR_GLOW_OUTER);

        // Middle glow
        const glow2: i32 = config.pw(8);
        rl.drawRectangle(x - glow2, y - glow2, sz + glow2 * 2, sz + glow2 * 2, config.LAZAR_GLOW_MID);

        // Inner glow
        const glow3: i32 = config.pw(4);
        rl.drawRectangle(x - glow3, y - glow3, sz + glow3 * 2, sz + glow3 * 2, config.LAZAR_GLOW_INNER);

        // Draw neon trail based on direction
        const trail_length: i32 = config.pw(20);
        const trail_width: i32 = sz - 2;
        switch (self.direction) {
            .up => {
                rl.drawRectangleGradientV(x + 1, y + sz, trail_width, trail_length, config.LAZAR_TRAIL_BRIGHT, config.LAZAR_TRAIL_FADE);
            },
            .down => {
                rl.drawRectangleGradientV(x + 1, y - trail_length, trail_width, trail_length, config.LAZAR_TRAIL_FADE, config.LAZAR_TRAIL_BRIGHT);
            },
            .left => {
                rl.drawRectangleGradientH(x + sz, y + 1, trail_length, trail_width, config.LAZAR_TRAIL_BRIGHT, config.LAZAR_TRAIL_FADE);
            },
            .right => {
                rl.drawRectangleGradientH(x - trail_length, y + 1, trail_length, trail_width, config.LAZAR_TRAIL_FADE, config.LAZAR_TRAIL_BRIGHT);
            },
        }

        // Draw shadow (offset)
        rl.drawRectangle(x + 2, y + 2, sz, sz, self.shadow_color);

        // Draw main body with bright neon color
        rl.drawRectangle(x, y, sz, sz, self.color);

        // Draw bright white-pink core
        if (sz > 4) {
            rl.drawRectangle(x + 2, y + 2, sz - 4, sz - 4, config.LAZAR_CORE);

            // Brightest center
            if (sz > 6) {
                rl.drawRectangle(x + 3, y + 3, sz - 6, sz - 6, config.LAZAR_CORE_BRIGHT);
            }
        }

        // Draw border for extra definition
        rl.drawRectangleLines(x, y, sz, sz, config.LAZAR_BORDER);
    }

    pub fn getRect(self: *const Lazar) rl.Rectangle {
        return .{
            .x = self.x,
            .y = self.y,
            .width = self.size,
            .height = self.size,
        };
    }

    pub fn isOffScreen(self: *const Lazar) bool {
        const screen_w: f32 = @floatFromInt(config.SCREEN_WIDTH);
        const screen_h: f32 = @floatFromInt(config.SCREEN_HEIGHT);

        return self.x < 0 or
            self.x > screen_w or
            self.y < 0 or
            self.y > screen_h;
    }

    pub fn hit(self: *Lazar) void {
        self.is_hit = true;
        // Visual explosion effect - double size and turn white
        self.size *= 2;
        self.color = config.LAZAR_BLOW_COLOR;
        self.shadow_color = config.LAZAR_BLOW_COLOR;
    }
};

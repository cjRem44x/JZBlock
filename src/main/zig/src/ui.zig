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
// ui.zig - Heads-Up Display (HUD)
// ============================================================================
// Renders all UI elements including health, kills, wave, currency, ammo,
// stats, pause screen, and game over screen.
// Uses text-only labels (no emojis) for clean, reliable rendering.
// ============================================================================

const std = @import("std");
const rl = @import("raylib");
const config = @import("config.zig");
const Engine = @import("engine.zig").Engine;
const GameState = @import("game_state.zig").GameState;

// Simple UI button for pause menu
const PauseButton = struct {
    x: i32,
    y: i32,
    width: i32,
    height: i32,
    text: [:0]const u8,

    fn isHovered(self: *const PauseButton) bool {
        const mx = rl.getMouseX();
        const my = rl.getMouseY();
        return mx >= self.x and mx <= self.x + self.width and
            my >= self.y and my <= self.y + self.height;
    }

    fn isClicked(self: *const PauseButton) bool {
        return self.isHovered() and rl.isMouseButtonPressed(.left);
    }

    fn draw(self: *const PauseButton) void {
        const hovered = self.isHovered();

        // Glow effect when hovered
        if (hovered) {
            rl.drawRectangle(self.x - 4, self.y - 4, self.width + 8, self.height + 8, config.MENU_BUTTON_GLOW);
        }

        // Button background
        const bg_color = if (hovered) config.MENU_BUTTON_HOVER else config.MENU_BUTTON_COLOR;
        rl.drawRectangle(self.x, self.y, self.width, self.height, bg_color);

        // Border
        rl.drawRectangleLines(self.x, self.y, self.width, self.height, config.MENU_TEXT_COLOR);

        // Text centered
        const text_width = rl.measureText(self.text, 28);
        const text_x = self.x + @divTrunc(self.width - text_width, 2);
        const text_y = self.y + @divTrunc(self.height - 28, 2);

        rl.drawText(self.text, text_x + 2, text_y + 2, 28, config.SHADOW_COLOR);
        rl.drawText(self.text, text_x, text_y, 28, config.MENU_TEXT_COLOR);
    }
};

pub const UI = struct {
    // Buffer for formatted strings
    text_buf: [64]u8 = undefined,

    // Pause menu buttons
    resume_button: PauseButton,
    menu_button: PauseButton,

    // Animation timer for effects
    anim_time: f32 = 0.0,

    pub fn init() UI {
        const button_width: i32 = 220;
        const button_height: i32 = 50;
        const center_x = @divTrunc(config.SCREEN_WIDTH - button_width, 2);

        return .{
            .resume_button = .{
                .x = center_x,
                .y = config.SCREEN_HEIGHT - 180,
                .width = button_width,
                .height = button_height,
                .text = "Resume",
            },
            .menu_button = .{
                .x = center_x,
                .y = config.SCREEN_HEIGHT - 110,
                .width = button_width,
                .height = button_height,
                .text = "Main Menu",
            },
        };
    }

    pub fn update(self: *UI, delta: f32) void {
        self.anim_time += delta;
        if (self.anim_time > 1000.0) self.anim_time = 0.0;
    }

    pub fn drawHUD(self: *UI, engine: *const Engine) void {
        // Draw countdown if active
        if (engine.isCountdownActive()) {
            self.drawCountdown(engine);
            return;
        }

        self.drawHealth(engine);
        self.drawKills(engine);
        self.drawWave(engine);
        self.drawCurrency(engine);
        self.drawAmmo(engine);
        self.drawStats(engine);
        self.drawReloadPrompt(engine);
        self.drawBoostIndicator(engine);
    }

    fn drawCountdown(self: *UI, engine: *const Engine) void {
        const count = engine.getCountdown();

        // Calculate fade alpha based on countdown progress
        const total_time: f32 = @floatFromInt(config.COUNTDOWN_SECONDS);
        const progress = engine.countdown_timer / total_time;
        const controls_alpha: u8 = @intFromFloat(@max(0.0, @min(255.0, progress * 255.0)));

        // Draw semi-transparent overlay
        rl.drawRectangle(0, 0, config.SCREEN_WIDTH, config.SCREEN_HEIGHT, config.OVERLAY_DARK);

        // Pulsing light rays from center
        self.drawLightRays(0.3);

        if (count > 0) {
            const text = std.fmt.bufPrintZ(&self.text_buf, "{d}", .{count}) catch "?";
            const text_width = rl.measureText(text, 200);
            const x = @divTrunc(config.SCREEN_WIDTH - text_width, 2);
            const y = @divTrunc(config.SCREEN_HEIGHT, 2) - 100;

            // Pulsing glow intensity
            const pulse = @sin(self.anim_time * 5.0) * 0.3 + 0.7;
            const glow_alpha: u8 = @intFromFloat(100.0 * pulse);

            // Draw shadow
            rl.drawText(text, x + 4, y + 4, 200, config.SHADOW_COLOR);
            // Draw glow
            rl.drawText(text, x - 2, y - 2, 200, rl.Color{ .r = 255, .g = 255, .b = 100, .a = glow_alpha });
            // Draw main text
            rl.drawText(text, x, y, 200, config.KEYBINDS_COLOR);
        } else {
            const text = "GO!";
            const text_width = rl.measureText(text, 150);
            const x = @divTrunc(config.SCREEN_WIDTH - text_width, 2);
            const y = @divTrunc(config.SCREEN_HEIGHT, 2) - 75;

            rl.drawText(text, x + 4, y + 4, 150, config.SHADOW_COLOR);
            rl.drawText(text, x - 2, y - 2, 150, config.COUNTDOWN_GO_GLOW);
            rl.drawText(text, x, y, 150, config.ZOMBIE_COLOR);
        }

        // Draw "Get Ready!" subtitle
        const subtitle = "Get Ready!";
        const sub_width = rl.measureText(subtitle, 40);
        rl.drawText(subtitle, @divTrunc(config.SCREEN_WIDTH - sub_width, 2), @divTrunc(config.SCREEN_HEIGHT, 2) + 80, 40, config.MENU_TEXT_COLOR);

        // Draw controls with fading effect
        drawControlsWithAlpha(controls_alpha);
    }

    fn drawLightRays(self: *UI, intensity: f32) void {
        const center_x: f32 = @as(f32, @floatFromInt(config.SCREEN_WIDTH)) / 2.0;
        const center_y: f32 = @as(f32, @floatFromInt(config.SCREEN_HEIGHT)) / 2.0;
        const num_rays: i32 = 12;
        const max_length: f32 = 600.0;

        var i: i32 = 0;
        while (i < num_rays) : (i += 1) {
            const angle = (@as(f32, @floatFromInt(i)) / @as(f32, @floatFromInt(num_rays))) * std.math.pi * 2.0 + self.anim_time * 0.2;
            const ray_length = max_length * (0.7 + @sin(self.anim_time * 2.0 + @as(f32, @floatFromInt(i))) * 0.3);

            const end_x = center_x + @cos(angle) * ray_length;
            const end_y = center_y + @sin(angle) * ray_length;

            const alpha: u8 = @intFromFloat(40.0 * intensity);

            // Draw ray as thick line with gradient effect
            rl.drawLineEx(
                .{ .x = center_x, .y = center_y },
                .{ .x = end_x, .y = end_y },
                3.0,
                rl.Color{ .r = 255, .g = 255, .b = 200, .a = alpha },
            );
        }
    }

    fn drawControlsWithAlpha(alpha: u8) void {
        const keybinds = [_][:0]const u8{
            "CONTROLS",
            "_______________",
            "Move: [W][A][S][D]",
            "Shoot: Arrow Keys",
            "Reload: [CTRL]",
            "Upgrade Weapon: [U]",
            "Speed Boost: [SPACE]",
            "Pause/Resume: [ESC]",
        };

        const start_y: i32 = @divTrunc(config.SCREEN_HEIGHT, 2) + 130;
        const text_color = rl.Color{ .r = config.KEYBINDS_COLOR.r, .g = config.KEYBINDS_COLOR.g, .b = config.KEYBINDS_COLOR.b, .a = alpha };
        const shadow_color = rl.Color{ .r = 0, .g = 0, .b = 0, .a = @min(150, alpha) };

        for (keybinds, 0..) |text, i| {
            const text_width = rl.measureText(text, 22);
            const x = @divTrunc(config.SCREEN_WIDTH - text_width, 2);
            const y = start_y + @as(i32, @intCast(i)) * 26;

            rl.drawText(text, x + 1, y + 1, 22, shadow_color);
            rl.drawText(text, x, y, 22, text_color);
        }
    }

    fn drawHealth(self: *UI, engine: *const Engine) void {
        const health = engine.player.health;
        const text = std.fmt.bufPrintZ(&self.text_buf, "HP: {d}", .{health}) catch "HP: ???";

        // Pulsing effect when low health
        var color = config.HEALTH_COLOR;
        if (health <= 30) {
            const pulse = @sin(self.anim_time * 8.0) * 0.5 + 0.5;
            color.a = @intFromFloat(155.0 + 100.0 * pulse);
        }

        // Glow when low health
        if (health <= 30) {
            rl.drawRectangle(15, 15, 150, 50, config.LOW_HEALTH_GLOW);
        }

        rl.drawText(text, 22, 22, 40, config.SHADOW_COLOR);
        rl.drawText(text, 20, 20, 40, color);
    }

    fn drawKills(self: *UI, engine: *const Engine) void {
        const text = std.fmt.bufPrintZ(&self.text_buf, "Kills: {d}", .{engine.kills}) catch "Kills: ???";
        const text_width = rl.measureText(text, 40);
        const x = config.SCREEN_WIDTH - text_width - 20;

        rl.drawText(text, x + 2, 22, 40, config.SHADOW_COLOR);
        rl.drawText(text, x, 20, 40, config.KILL_COLOR);
    }

    fn drawWave(self: *UI, engine: *const Engine) void {
        const text = std.fmt.bufPrintZ(&self.text_buf, "Wave {d}", .{engine.wave}) catch "Wave ???";
        const text_width = rl.measureText(text, 40);
        const x = @divTrunc(config.SCREEN_WIDTH - text_width, 2);

        // Subtle rainbow effect on wave text
        const hue = @mod(self.anim_time * 50.0, 360.0);
        const wave_color = hsvToRgb(hue, 0.6, 1.0);

        rl.drawText(text, x + 2, 22, 40, config.SHADOW_COLOR);
        rl.drawText(text, x, 20, 40, wave_color);
    }

    fn drawCurrency(self: *UI, engine: *const Engine) void {
        const text = std.fmt.bufPrintZ(&self.text_buf, "$ {d}", .{engine.currency}) catch "$ ???";
        const text_width = rl.measureText(text, 40);
        const x = config.SCREEN_WIDTH - text_width - 20;
        const y = config.SCREEN_HEIGHT - 50;

        // Pulsing glow effect
        const pulse = @sin(self.anim_time * 3.0) * 0.3 + 0.7;
        const glow_alpha: u8 = @intFromFloat(30.0 * pulse);

        rl.drawRectangle(x - 12, y - 7, text_width + 24, 54, rl.Color{ .r = 255, .g = 0, .b = 240, .a = glow_alpha });
        rl.drawRectangleLines(x - 10, y - 5, text_width + 20, 50, config.CURRENCY_COLOR);

        rl.drawText(text, x + 2, y + 2, 40, config.SHADOW_COLOR);
        rl.drawText(text, x, y, 40, config.CURRENCY_COLOR);
    }

    fn drawAmmo(self: *UI, engine: *const Engine) void {
        const text = std.fmt.bufPrintZ(&self.text_buf, "Ammo: {d}", .{engine.ammo}) catch "Ammo: ???";
        const text_width = rl.measureText(text, 40);
        const x = config.SCREEN_WIDTH - text_width - 20;
        const y = config.SCREEN_HEIGHT - 110;

        rl.drawText(text, x + 2, y + 2, 40, config.SHADOW_COLOR);
        rl.drawText(text, x, y, 40, config.AMMO_COLOR);
    }

    fn drawStats(self: *UI, engine: *const Engine) void {
        const power = engine.getLazarPower();
        const text = std.fmt.bufPrintZ(&self.text_buf, "Power: {d} | Upgrade: ${d} [U]", .{ power, engine.upgrade_cost }) catch "Power: ???";

        rl.drawText(text, 22, config.SCREEN_HEIGHT - 48, 25, config.SHADOW_COLOR);
        rl.drawText(text, 20, config.SCREEN_HEIGHT - 50, 25, config.STATS_COLOR);
    }

    fn drawReloadPrompt(self: *UI, engine: *const Engine) void {
        if (engine.ammo <= 0 and !engine.reloading) {
            const text = "Press [CTRL] to reload";
            const text_width = rl.measureText(text, 25);
            const x = config.SCREEN_WIDTH - text_width - 30;
            const y = config.SCREEN_HEIGHT - 170;

            // Pulsing glow
            const pulse = @sin(self.anim_time * 6.0) * 0.5 + 0.5;
            const glow_alpha: u8 = @intFromFloat(30.0 + 40.0 * pulse);

            rl.drawRectangle(x - 7, y - 7, text_width + 14, 39, rl.Color{ .r = 200, .g = 255, .b = 200, .a = glow_alpha });
            rl.drawRectangleLines(x - 5, y - 5, text_width + 10, 35, config.RELOAD_PROMPT_COLOR);
            rl.drawText(text, x, y, 25, config.RELOAD_PROMPT_COLOR);
        } else if (engine.reloading) {
            const text = "Reloading...";
            const text_width = rl.measureText(text, 25);
            const x = config.SCREEN_WIDTH - text_width - 30;
            const y = config.SCREEN_HEIGHT - 170;

            rl.drawText(text, x, y, 25, config.RELOAD_PROMPT_COLOR);
        }
    }

    fn drawBoostIndicator(self: *UI, engine: *const Engine) void {
        const player = &engine.player;

        if (player.boost_active) {
            const text = "BOOST!";
            const text_width = rl.measureText(text, 30);
            const x = @divTrunc(config.SCREEN_WIDTH - text_width, 2);

            // Intense pulsing glow
            const pulse = @sin(self.anim_time * 10.0) * 0.5 + 0.5;
            const glow_alpha: u8 = @intFromFloat(80.0 + 100.0 * pulse);

            rl.drawText(text, x - 2, 68, 30, rl.Color{ .r = 255, .g = 255, .b = 100, .a = glow_alpha });
            rl.drawText(text, x + 2, 72, 30, config.OVERLAY_DARK);
            rl.drawText(text, x, 70, 30, config.KEYBINDS_COLOR);
        } else if (!player.can_boost) {
            const text = "Boost recharging...";
            const text_width = rl.measureText(text, 20);
            rl.drawText(text, @divTrunc(config.SCREEN_WIDTH - text_width, 2), 70, 20, config.AMMO_COLOR);
        }
    }

    pub fn updatePaused(self: *UI, state: *GameState) void {
        if (self.resume_button.isClicked() or rl.isKeyPressed(.escape)) {
            state.* = .in_game;
        }
        if (self.menu_button.isClicked()) {
            state.* = .main_menu;
        }
    }

    pub fn drawPaused(self: *UI) void {
        // Semi-transparent overlay with gradient
        rl.drawRectangle(0, 0, config.SCREEN_WIDTH, config.SCREEN_HEIGHT, config.PAUSED_BG);

        // Ambient light effect
        self.drawAmbientGlow();

        // Pause message with glow
        const pause_text = "PAUSED";
        const pause_width = rl.measureText(pause_text, 80);
        const pause_x = @divTrunc(config.SCREEN_WIDTH - pause_width, 2);
        const pause_y: i32 = 80;

        // Pulsing glow
        const pulse = @sin(self.anim_time * 2.0) * 0.3 + 0.7;
        const glow_alpha: u8 = @intFromFloat(60.0 * pulse);

        rl.drawText(pause_text, pause_x - 3, pause_y - 3, 80, rl.Color{ .r = 137, .g = 0, .b = 255, .a = glow_alpha });
        rl.drawText(pause_text, pause_x + 4, pause_y + 4, 80, config.SHADOW_DARK);
        rl.drawText(pause_text, pause_x, pause_y, 80, config.PAUSE_COLOR);

        // Keybindings
        const keybinds = [_][:0]const u8{
            "KEYBINDINGS",
            "_______________",
            "Move: [W][A][S][D]",
            "Shoot: Arrow Keys",
            "Reload: [CTRL]",
            "Upgrade Weapon: [U]",
            "Speed Boost: [SPACE]",
            "Pause/Resume: [ESC]",
            "Restart When Dead: [R]",
        };

        const start_y: i32 = 180;
        for (keybinds, 0..) |text, i| {
            const text_width = rl.measureText(text, 25);
            const x = @divTrunc(config.SCREEN_WIDTH - text_width, 2);
            const y = start_y + @as(i32, @intCast(i)) * 32;

            rl.drawText(text, x + 2, y + 2, 25, config.SHADOW_COLOR);
            rl.drawText(text, x, y, 25, config.KEYBINDS_COLOR);
        }

        // Draw buttons
        self.resume_button.draw();
        self.menu_button.draw();

        // Rando Racing hint
        const rando_hint = "[B] Rando Racing";
        const rando_width = rl.measureText(rando_hint, 22);
        rl.drawText(rando_hint,
            @divTrunc(config.SCREEN_WIDTH - rando_width, 2),
            config.SCREEN_HEIGHT - 60, 22, config.CURRENCY_COLOR);
    }

    fn drawAmbientGlow(self: *UI) void {
        // Corner glows
        const pulse = @sin(self.anim_time * 1.5) * 0.3 + 0.7;
        const alpha: u8 = @intFromFloat(25.0 * pulse);

        // Top-left purple glow
        rl.drawRectangleGradientEx(
            .{ .x = 0, .y = 0, .width = 300, .height = 300 },
            rl.Color{ .r = 137, .g = 0, .b = 255, .a = alpha },
            config.TRANSPARENT,
            config.TRANSPARENT,
            config.TRANSPARENT,
        );

        // Bottom-right green glow
        rl.drawRectangleGradientEx(
            .{ .x = @as(f32, @floatFromInt(config.SCREEN_WIDTH)) - 300, .y = @as(f32, @floatFromInt(config.SCREEN_HEIGHT)) - 300, .width = 300, .height = 300 },
            config.TRANSPARENT,
            config.TRANSPARENT,
            rl.Color{ .r = 0, .g = 255, .b = 100, .a = alpha },
            config.TRANSPARENT,
        );
    }

    pub fn drawGameOver(self: *UI, engine: *const Engine) void {
        // Dark red background with vignette effect
        rl.clearBackground(config.GAME_OVER_BG);

        // Draw vignette
        drawVignette();

        // Flickering red ambient light
        const flicker = @sin(self.anim_time * 8.0) * 0.2 + 0.8;
        const flicker_alpha: u8 = @intFromFloat(20.0 * flicker);
        rl.drawRectangle(0, 0, config.SCREEN_WIDTH, config.SCREEN_HEIGHT, rl.Color{ .r = 255, .g = 0, .b = 0, .a = flicker_alpha });

        // Draw final stats
        self.drawKills(engine);
        self.drawWave(engine);

        // Game over message with multiple layers
        const go_text = "GAME OVER";
        const go_width = rl.measureText(go_text, 100);
        const go_x = @divTrunc(config.SCREEN_WIDTH - go_width, 2);
        const go_y = @divTrunc(config.SCREEN_HEIGHT, 2) - 50;

        // Pulsing red glow
        const pulse = @sin(self.anim_time * 4.0) * 0.5 + 0.5;
        const glow_alpha: u8 = @intFromFloat(60.0 + 60.0 * pulse);

        rl.drawText(go_text, go_x + 6, go_y + 6, 100, config.SHADOW_DARK);
        rl.drawText(go_text, go_x - 3, go_y - 3, 100, rl.Color{ .r = 255, .g = 50, .b = 50, .a = glow_alpha });
        rl.drawText(go_text, go_x, go_y, 100, config.GAME_OVER_COLOR);

        // Restart prompt
        const restart_text = "Press [R] to restart";
        const restart_width = rl.measureText(restart_text, 40);
        const restart_x = @divTrunc(config.SCREEN_WIDTH - restart_width, 2);
        const restart_y = @as(i32, @intFromFloat(@as(f32, @floatFromInt(config.SCREEN_HEIGHT)) * 0.75));

        rl.drawText(restart_text, restart_x + 2, restart_y + 2, 40, config.SHADOW_COLOR);
        rl.drawText(restart_text, restart_x, restart_y, 40, config.RESTART_COLOR);
    }
};

// HSV to RGB conversion for rainbow effects
fn hsvToRgb(h: f32, s: f32, v: f32) rl.Color {
    const c = v * s;
    const x = c * (1.0 - @abs(@mod(h / 60.0, 2.0) - 1.0));
    const m = v - c;

    var r: f32 = 0;
    var g: f32 = 0;
    var b: f32 = 0;

    if (h < 60) {
        r = c;
        g = x;
    } else if (h < 120) {
        r = x;
        g = c;
    } else if (h < 180) {
        g = c;
        b = x;
    } else if (h < 240) {
        g = x;
        b = c;
    } else if (h < 300) {
        r = x;
        b = c;
    } else {
        r = c;
        b = x;
    }

    return rl.Color{
        .r = @intFromFloat((r + m) * 255),
        .g = @intFromFloat((g + m) * 255),
        .b = @intFromFloat((b + m) * 255),
        .a = 255,
    };
}

// Draw a vignette effect (darker edges)
fn drawVignette() void {
    var i: i32 = 0;
    while (i < 8) : (i += 1) {
        const alpha: u8 = @intCast(@min(255, i * 15));
        const inset = i * 50;

        rl.drawRectangle(0, 0, config.SCREEN_WIDTH, inset, rl.Color{ .r = 0, .g = 0, .b = 0, .a = alpha });
        rl.drawRectangle(0, config.SCREEN_HEIGHT - inset, config.SCREEN_WIDTH, inset, rl.Color{ .r = 0, .g = 0, .b = 0, .a = alpha });
        rl.drawRectangle(0, 0, inset, config.SCREEN_HEIGHT, rl.Color{ .r = 0, .g = 0, .b = 0, .a = alpha });
        rl.drawRectangle(config.SCREEN_WIDTH - inset, 0, inset, config.SCREEN_HEIGHT, rl.Color{ .r = 0, .g = 0, .b = 0, .a = alpha });
    }
}

// Draw background with gradient and lighting effects
pub fn drawBackgroundEffects(anim_time: f32) void {
    // Subtle grid pattern
    var x: i32 = 0;
    while (x < config.SCREEN_WIDTH) : (x += 50) {
        rl.drawLine(x, 0, x, config.SCREEN_HEIGHT, config.GRID_COLOR);
    }
    var y: i32 = 0;
    while (y < config.SCREEN_HEIGHT) : (y += 50) {
        rl.drawLine(0, y, config.SCREEN_WIDTH, y, config.GRID_COLOR);
    }

    // Animated ambient lighting
    const pulse = @sin(anim_time * 0.5) * 0.3 + 0.7;
    const ambient_alpha: u8 = @intFromFloat(15.0 * pulse);

    // Corner gradients for depth
    const corner_size: i32 = 200;
    rl.drawRectangleGradientH(0, 0, corner_size, config.SCREEN_HEIGHT, rl.Color{ .r = 137, .g = 0, .b = 255, .a = ambient_alpha }, config.TRANSPARENT);
    rl.drawRectangleGradientH(config.SCREEN_WIDTH - corner_size, 0, corner_size, config.SCREEN_HEIGHT, config.TRANSPARENT, rl.Color{ .r = 0, .g = 255, .b = 100, .a = ambient_alpha });

    // Top/bottom gradients
    rl.drawRectangleGradientV(0, 0, config.SCREEN_WIDTH, corner_size, rl.Color{ .r = 0, .g = 0, .b = 0, .a = 50 }, config.TRANSPARENT);
    rl.drawRectangleGradientV(0, config.SCREEN_HEIGHT - corner_size, config.SCREEN_WIDTH, corner_size, config.TRANSPARENT, rl.Color{ .r = 0, .g = 0, .b = 0, .a = 50 });
}

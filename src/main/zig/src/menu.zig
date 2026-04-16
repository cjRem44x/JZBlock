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
// menu.zig - Main Menu Screen
// ============================================================================
// Displays the main menu with background image and Play, Settings, Quit buttons.
// ============================================================================

const rl = @import("raylib");
const config = @import("config.zig");
const GameState = @import("game_state.zig").GameState;

pub const Button = struct {
    x: i32,
    y: i32,
    width: i32,
    height: i32,
    text: [:0]const u8,
    hovered: bool = false,

    pub fn init(x: i32, y: i32, width: i32, height: i32, text: [:0]const u8) Button {
        return .{
            .x = x,
            .y = y,
            .width = width,
            .height = height,
            .text = text,
            .hovered = false,
        };
    }

    pub fn update(self: *Button) bool {
        const mouse_x = rl.getMouseX();
        const mouse_y = rl.getMouseY();

        self.hovered = mouse_x >= self.x and
            mouse_x <= self.x + self.width and
            mouse_y >= self.y and
            mouse_y <= self.y + self.height;

        return self.hovered and rl.isMouseButtonPressed(.left);
    }

    pub fn draw(self: *const Button) void {
        // Draw button background with transparency
        const bg_color = if (self.hovered) config.MENU_BUTTON_HOVER else config.MENU_BUTTON_COLOR;

        rl.drawRectangle(self.x, self.y, self.width, self.height, bg_color);

        // Draw button border with glow when hovered
        const gp = config.pw(2);
        if (self.hovered) {
            rl.drawRectangle(self.x - gp, self.y - gp, self.width + gp * 2, self.height + gp * 2, config.MENU_BUTTON_GLOW);
        }
        rl.drawRectangleLines(self.x, self.y, self.width, self.height, config.MENU_TEXT_COLOR);

        // Draw button text (centered)
        const fsz = config.ph(30);
        const text_width = rl.measureText(self.text, fsz);
        const text_x = self.x + @divTrunc(self.width - text_width, 2);
        const text_y = self.y + @divTrunc(self.height - fsz, 2);

        // Shadow
        rl.drawText(self.text, text_x + 2, text_y + 2, fsz, config.SHADOW_COLOR);
        // Main text
        rl.drawText(self.text, text_x, text_y, fsz, config.MENU_TEXT_COLOR);
    }
};

pub const Menu = struct {
    play_button: Button,
    settings_button: Button,
    quit_button: Button,
    bg_texture: rl.Texture2D,
    texture_loaded: bool,

    pub fn init() Menu {
        const button_width: i32 = config.pw(250);
        const button_height: i32 = config.ph(55);
        const center_x = @divTrunc(config.SCREEN_WIDTH - button_width, 2);
        const button_spacing: i32 = config.ph(70);
        const start_y: i32 = config.ph(450);

        // Try to load the background image
        const texture = rl.loadTexture("../../../res/img/main_menu_img.png") catch {
            return .{
                .play_button = Button.init(center_x, start_y, button_width, button_height, "Play"),
                .settings_button = Button.init(center_x, start_y + button_spacing, button_width, button_height, "Settings"),
                .quit_button = Button.init(center_x, start_y + button_spacing * 2, button_width, button_height, "Quit"),
                .bg_texture = undefined,
                .texture_loaded = false,
            };
        };

        return .{
            .play_button = Button.init(center_x, start_y, button_width, button_height, "Play"),
            .settings_button = Button.init(center_x, start_y + button_spacing, button_width, button_height, "Settings"),
            .quit_button = Button.init(center_x, start_y + button_spacing * 2, button_width, button_height, "Quit"),
            .bg_texture = texture,
            .texture_loaded = true,
        };
    }

    pub fn deinit(self: *Menu) void {
        if (self.texture_loaded) {
            rl.unloadTexture(self.bg_texture);
        }
    }

    pub fn update(self: *Menu, state: *GameState) bool {
        // Update buttons and check for clicks
        if (self.play_button.update()) {
            state.* = .in_game;
            return true;
        }

        if (self.settings_button.update()) {
            state.* = .settings;
            return true;
        }

        if (self.quit_button.update()) {
            return false; // Signal to close window
        }

        return true;
    }

    pub fn draw(self: *const Menu) void {
        // Draw background image or fallback color
        if (self.texture_loaded) {
            // Scale image to fit screen
            const scale_x = @as(f32, @floatFromInt(config.SCREEN_WIDTH)) / @as(f32, @floatFromInt(self.bg_texture.width));
            const scale_y = @as(f32, @floatFromInt(config.SCREEN_HEIGHT)) / @as(f32, @floatFromInt(self.bg_texture.height));
            const scale = @max(scale_x, scale_y);

            const dest_width = @as(f32, @floatFromInt(self.bg_texture.width)) * scale;
            const dest_height = @as(f32, @floatFromInt(self.bg_texture.height)) * scale;
            const dest_x = (@as(f32, @floatFromInt(config.SCREEN_WIDTH)) - dest_width) / 2.0;
            const dest_y = (@as(f32, @floatFromInt(config.SCREEN_HEIGHT)) - dest_height) / 2.0;

            rl.drawTextureEx(self.bg_texture, .{ .x = dest_x, .y = dest_y }, 0.0, scale, rl.Color.white);

            // Darken overlay for better text visibility
            rl.drawRectangle(0, 0, config.SCREEN_WIDTH, config.SCREEN_HEIGHT, config.OVERLAY_MENU);
        } else {
            rl.clearBackground(config.MENU_BG);
        }

        // Title with glow effect
        const t_fsz = config.ph(120);
        const title = "JZBlock";
        const title_width = rl.measureText(title, t_fsz);
        const title_x = @divTrunc(config.SCREEN_WIDTH - title_width, 2);
        const title_y: i32 = config.ph(100);

        // Glow layers
        rl.drawText(title, title_x - 3, title_y - 3, t_fsz, config.MENU_TITLE_GLOW);
        rl.drawText(title, title_x + 3, title_y + 3, t_fsz, config.SHADOW_COLOR);
        // Main title
        rl.drawText(title, title_x, title_y, t_fsz, config.PLAYER_COLOR);

        // Subtitle
        const s_fsz = config.ph(35);
        const subtitle = "Zombie Survival";
        const subtitle_width = rl.measureText(subtitle, s_fsz);
        const sub_x = @divTrunc(config.SCREEN_WIDTH - subtitle_width, 2);
        rl.drawText(subtitle, sub_x + 2, config.ph(232), s_fsz, config.SHADOW_COLOR);
        rl.drawText(subtitle, sub_x, config.ph(230), s_fsz, config.ZOMBIE_COLOR);

        // Draw buttons
        self.play_button.draw();
        self.settings_button.draw();
        self.quit_button.draw();

        // Version/credits at bottom
        const v_fsz = config.ph(18);
        const version = "v0.5 - Zig Edition";
        const ver_width = rl.measureText(version, v_fsz);
        rl.drawText(version, @divTrunc(config.SCREEN_WIDTH - ver_width, 2), config.SCREEN_HEIGHT - config.ph(30), v_fsz, config.MENU_VERSION_COLOR);
    }
};

// Settings screen
pub const Settings = struct {
    back_button: Button,

    pub fn init() Settings {
        const button_width: i32 = config.pw(200);
        const button_height: i32 = config.ph(50);

        return .{
            .back_button = Button.init(config.pw(50), config.SCREEN_HEIGHT - config.ph(80), button_width, button_height, "Back"),
        };
    }

    pub fn update(self: *Settings, state: *GameState) void {
        if (self.back_button.update() or rl.isKeyPressed(.escape)) {
            state.* = .main_menu;
        }
    }

    pub fn draw(self: *const Settings) void {
        rl.clearBackground(config.MENU_BG);

        // Title
        const t_fsz = config.ph(80);
        const title = "Settings";
        const title_width = rl.measureText(title, t_fsz);
        rl.drawText(title, @divTrunc(config.SCREEN_WIDTH - title_width, 2), config.ph(60), t_fsz, config.MENU_TEXT_COLOR);

        // Placeholder text
        const p_fsz = config.ph(30);
        const placeholder = "Settings coming soon...";
        const ph_width = rl.measureText(placeholder, p_fsz);
        rl.drawText(placeholder, @divTrunc(config.SCREEN_WIDTH - ph_width, 2), @divTrunc(config.SCREEN_HEIGHT, 2), p_fsz, config.KEYBINDS_COLOR);

        // Controls info
        const ct_fsz = config.ph(25);
        const controls_title = "Current Controls:";
        const ct_width = rl.measureText(controls_title, ct_fsz);
        rl.drawText(controls_title, @divTrunc(config.SCREEN_WIDTH - ct_width, 2), config.ph(250), ct_fsz, config.MENU_TEXT_COLOR);

        const controls = [_][:0]const u8{
            "Move: [W][A][S][D]",
            "Shoot: Arrow Keys",
            "Reload: [CTRL]",
            "Upgrade: [U]",
            "Boost: [SPACE]",
            "Pause: [ESC]",
        };

        const c_fsz = config.ph(22);
        for (controls, 0..) |text, i| {
            const text_width = rl.measureText(text, c_fsz);
            const x = @divTrunc(config.SCREEN_WIDTH - text_width, 2);
            const y: i32 = config.ph(290) + @as(i32, @intCast(i)) * config.ph(30);
            rl.drawText(text, x, y, c_fsz, config.KEYBINDS_COLOR);
        }

        self.back_button.draw();
    }
};

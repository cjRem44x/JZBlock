// ============================================================================
// player.zig - Player Entity
// ============================================================================
// Player character with movement, speed boost system, and bounds checking.
// ============================================================================

const rl = @import("raylib");
const config = @import("config.zig");

pub const Player = struct {
    // Position (floats for smooth movement)
    x: f32,
    y: f32,

    // Dimensions
    size: f32,

    // Stats
    health: i32,
    speed: f32,
    base_speed: f32,

    // Appearance
    color: rl.Color,

    // Speed boost system
    boost_active: bool = false,
    boost_timer: f32 = 0.0,
    recharge_timer: f32 = 0.0,
    can_boost: bool = true,

    pub fn init() Player {
        const screen_w: f32 = @floatFromInt(config.SCREEN_WIDTH);
        const screen_h: f32 = @floatFromInt(config.SCREEN_HEIGHT);

        return .{
            .x = (screen_w / 2.0) - (config.PLAYER_SIZE / 2.0),
            .y = (screen_h / 2.0) - (config.PLAYER_SIZE / 2.0),
            .size = config.PLAYER_SIZE,
            .health = config.PLAYER_HEALTH,
            .speed = config.PLAYER_SPEED,
            .base_speed = config.PLAYER_SPEED,
            .color = config.PLAYER_COLOR,
            .boost_active = false,
            .boost_timer = 0.0,
            .recharge_timer = 0.0,
            .can_boost = true,
        };
    }

    pub fn reset(self: *Player) void {
        const screen_w: f32 = @floatFromInt(config.SCREEN_WIDTH);
        const screen_h: f32 = @floatFromInt(config.SCREEN_HEIGHT);

        self.x = (screen_w / 2.0) - (config.PLAYER_SIZE / 2.0);
        self.y = (screen_h / 2.0) - (config.PLAYER_SIZE / 2.0);
        self.health = config.PLAYER_HEALTH;
        self.speed = config.PLAYER_SPEED;
        self.base_speed = config.PLAYER_SPEED;
        self.boost_active = false;
        self.boost_timer = 0.0;
        self.recharge_timer = 0.0;
        self.can_boost = true;
    }

    pub fn update(self: *Player, delta: f32) void {
        // Handle movement input (WASD keys only - arrow keys are for shooting)
        var dx: f32 = 0.0;
        var dy: f32 = 0.0;

        if (rl.isKeyDown(.w)) dy -= 1.0;
        if (rl.isKeyDown(.s)) dy += 1.0;
        if (rl.isKeyDown(.a)) dx -= 1.0;
        if (rl.isKeyDown(.d)) dx += 1.0;

        // Normalize diagonal movement
        const len = @sqrt(dx * dx + dy * dy);
        if (len > 0.0) {
            dx /= len;
            dy /= len;
        }

        // Apply movement
        self.x += dx * self.speed * delta;
        self.y += dy * self.speed * delta;

        // Bounds checking
        self.keepInBounds();

        // Update speed boost
        self.updateBoost(delta);
    }

    fn updateBoost(self: *Player, delta: f32) void {
        // Handle recharge
        if (!self.can_boost and !self.boost_active) {
            self.recharge_timer += delta;
            if (self.recharge_timer >= config.BOOST_RECHARGE) {
                self.can_boost = true;
                self.recharge_timer = 0.0;
            }
        }

        // Activate boost on SPACE
        if (rl.isKeyDown(.space) and self.can_boost and !self.boost_active) {
            self.boost_active = true;
            self.speed = self.base_speed * config.BOOST_MULTIPLIER;
            self.boost_timer = 0.0;
            self.can_boost = false;
        }

        // Update boost duration
        if (self.boost_active) {
            self.boost_timer += delta;

            // End boost on timer or key release
            if (self.boost_timer >= config.BOOST_DURATION or !rl.isKeyDown(.space)) {
                self.boost_active = false;
                self.speed = self.base_speed;
                self.recharge_timer = 0.0;
            }
        }
    }

    pub fn keepInBounds(self: *Player) void {
        const screen_w: f32 = @floatFromInt(config.SCREEN_WIDTH);
        const screen_h: f32 = @floatFromInt(config.SCREEN_HEIGHT);

        if (self.x < 0) self.x = 0;
        if (self.x + self.size > screen_w) self.x = screen_w - self.size;
        if (self.y < 0) self.y = 0;
        if (self.y + self.size > screen_h) self.y = screen_h - self.size;
    }

    pub fn draw(self: *const Player) void {
        const x: i32 = @intFromFloat(self.x);
        const y: i32 = @intFromFloat(self.y);
        const sz: i32 = @intFromFloat(self.size);

        // Draw main body
        rl.drawRectangle(x, y, sz, sz, self.color);

        // Draw highlight (top-left edge for 3D effect)
        rl.drawRectangle(x, y, sz, 4, config.PLAYER_HIGHLIGHT);
        rl.drawRectangle(x, y, 4, sz, config.PLAYER_HIGHLIGHT);

        // Draw darker edge (bottom-right for 3D depth)
        rl.drawRectangle(x, y + sz - 4, sz, 4, config.PLAYER_SHADOW);
        rl.drawRectangle(x + sz - 4, y, 4, sz, config.PLAYER_SHADOW);

        // Draw face
        const eye_color = config.PLAYER_EYE_WHITE;
        const pupil_color = config.PLAYER_PUPIL;
        const mouth_color = config.PLAYER_MOUTH;

        // Eyes - positioned in upper third
        const eye_size: i32 = @max(4, @divTrunc(sz, 8));
        const eye_y = y + @divTrunc(sz, 4);
        const left_eye_x = x + @divTrunc(sz, 4);
        const right_eye_x = x + sz - @divTrunc(sz, 4) - eye_size;

        // Eye whites
        rl.drawRectangle(left_eye_x, eye_y, eye_size + 2, eye_size + 2, eye_color);
        rl.drawRectangle(right_eye_x, eye_y, eye_size + 2, eye_size + 2, eye_color);

        // Pupils
        rl.drawRectangle(left_eye_x + 1, eye_y + 1, eye_size, eye_size, pupil_color);
        rl.drawRectangle(right_eye_x + 1, eye_y + 1, eye_size, eye_size, pupil_color);

        // Smile - simple curved appearance using rectangles
        const mouth_y = y + @divTrunc(sz * 2, 3);
        const mouth_width = @divTrunc(sz, 2);
        const mouth_x = x + @divTrunc(sz, 4);

        // Main smile line
        rl.drawRectangle(mouth_x, mouth_y, mouth_width, 3, mouth_color);
        // Smile corners going up
        rl.drawRectangle(mouth_x - 2, mouth_y - 2, 4, 3, mouth_color);
        rl.drawRectangle(mouth_x + mouth_width - 2, mouth_y - 2, 4, 3, mouth_color);
    }

    pub fn getRect(self: *const Player) rl.Rectangle {
        return .{
            .x = self.x,
            .y = self.y,
            .width = self.size,
            .height = self.size,
        };
    }

    pub fn takeDamage(self: *Player, amount: i32) void {
        self.health -= amount;
        if (self.health < 0) self.health = 0;
    }

    pub fn heal(self: *Player, amount: i32) void {
        self.health += amount;
        if (self.health > config.PLAYER_HEALTH) {
            self.health = config.PLAYER_HEALTH;
        }
    }

    pub fn isAlive(self: *const Player) bool {
        return self.health > 0;
    }

    pub fn getCenterX(self: *const Player) f32 {
        return self.x + self.size / 2.0;
    }

    pub fn getCenterY(self: *const Player) f32 {
        return self.y + self.size / 2.0;
    }
};

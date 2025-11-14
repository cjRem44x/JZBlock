const rl = @import("raylib");

pub const Player_Entity = struct {
    // stats
    health: i32,

    // tracking
    x: i32,
    y: i32,
    width: i32,
    height: i32,
    color: rl.Color,
    vx: i32,
    vy: i32,
    speed: i32,

    pub fn init(health: i32, x: i32, y: i32, width: i32, height: i32, color: rl.Color, vx: i32, vy: i32, speed: i32) Player_Entity {
        return .{
            .health = health,
            .x = x,
            .y = y,
            .width = width,
            .height = height,
            .color = color,
            .vx = vx,
            .vy = vy,
            .speed = speed,
        };
    }

    pub fn move(self: *Player_Entity) void {
        if (rl.isKeyDown(rl.KeyboardKey.a)) {
            self.x -= (self.vx * self.speed);
        } else if (rl.isKeyDown(rl.KeyboardKey.d)) {
            self.x += (self.vx * self.speed);
        } else if (rl.isKeyDown(rl.KeyboardKey.w)) {
            self.y -= (self.vy * self.speed);
        } else if (rl.isKeyDown(rl.KeyboardKey.s)) {
            self.y += (self.vy * self.speed);
        }
    }

    pub fn draw(self: *Player_Entity) void {
        rl.drawRectangle(self.x, self.y, self.width, self.height, self.color);
    }
};

const rl = @import("raylib");

// WINDOW PROP //
//
const title = "JZBlock ϫⲍⲃⲗⲟⲕ";
const screen_width = 1550;
const screen_height = 950;
const FPS = 60;

// COLORS //
//
const wind_bg = rl.Color{ .r = 16, .g = 16, .b = 36, .a = 255 };

// KINEMATIC BODIES //
//
const body_size = 32;

// DRIVER //
//
pub fn main() !void {
    rl.initWindow(screen_width, screen_height, title);
    defer rl.closeWindow();
    rl.setTargetFPS(FPS);

    while (!rl.windowShouldClose()) {
        rl.beginDrawing();
        defer rl.endDrawing();

        rl.clearBackground(wind_bg);
    }
}

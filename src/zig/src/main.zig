const rl = @import("raylib");

const title = "JZBlock ϫⲍⲃⲗⲟⲕ";
const screen_width = 1550;
const screen_height = 950;
const FPS = 60;

pub fn main() !void {
    rl.initWindow(screen_width, screen_height, title);
    defer rl.closeWindow();
    rl.setTargetFPS(FPS);

    while (!rl.windowShouldClose()) {
        rl.beginDrawing();
        defer rl.endDrawing();

        rl.clearBackground(.black);
    }
}

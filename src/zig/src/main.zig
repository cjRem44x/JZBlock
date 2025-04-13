const rl = @import("raylib");
const std = @import("std");
const print = std.debug.print;

const title = "JZBlock ϫⲍⲃⲗⲟⲕ";
const original_width = 1550;
const original_height = 950;
const original_screen_width = 1920;
const original_screen_height = 1080;
const FPS = 60;

pub fn main() !void {
    // Get the current screen resolution (monitor 0)
    const monitor_width = rl.getMonitorWidth(0);
    const monitor_height = rl.getMonitorHeight(0);
    print("Monitor width: {d}, Monitor height: {d}\n", .{ monitor_width, monitor_height });

    // Initialize window with computed size
    rl.initWindow(original_width, original_height, title);
    defer rl.closeWindow();

    // Set FPS and high DPI flag
    rl.setTargetFPS(FPS);

    // Main game loop
    while (!rl.windowShouldClose()) {
        rl.beginDrawing();
        defer rl.endDrawing();

        rl.clearBackground(.black);
        // Optionally add any more drawing code here...
    }
}


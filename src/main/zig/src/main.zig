const std = @import("std");
const c = @cImport({
    @cInclude("stdio.h");
    @cInclude("stdlib.h");
});
const rl = @import("raylib");
const u = @import("util.zig");
const print = std.debug.print;

// Window Properties
const GAME_TITLE = "JZBlock";
const FPS = 60;

// Assuming monitor is 1080p
var screen_width: f32 = 800.0;
var screen_height: f32 = 600.0;
var pixel_width: f32 = 1.0;
var pixel_height: f32 = 1.0;

pub fn main() !void {
    rl.initWindow(u.toInt(screen_width), u.toInt(screen_height), GAME_TITLE);
    defer rl.closeWindow();
    rl.setTargetFPS(FPS);
    // compute for non-1080p monitors
    u.compute_game_size(&screen_width, &screen_height, &pixel_width, &pixel_height);
    print("[LOG] Pixel Size => {d}x{d}\n", .{ pixel_width, pixel_height });

    while (!rl.windowShouldClose()) {
        rl.beginDrawing();
        defer rl.endDrawing();
        rl.clearBackground(.black);
    }
}

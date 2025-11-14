const std = @import("std");
const c = @cImport({
    @cInclude("stdio.h");
    @cInclude("stdlib.h");
});
const rl = @import("raylib");

pub fn main() !void {
    _ = c.printf("Hello World from Zig!\n");
}

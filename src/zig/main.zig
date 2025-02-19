const std = @import("std");
const print = std.debug.print;

pub fn main() !void {
    strout("hello world");
}

fn strout(s: []const u8) void {
    print("{s}", .{s});
}

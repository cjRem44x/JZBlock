const std = @import("std"); // Import the standard library for basic utilities.
const print = std.debug.print; // Alias for the debug print function from the standard library.

pub fn main() !void {
    strout("hello world"); // Call the custom `strout` function to print "hello world".
}

fn strout(s: []const u8) void {
    // Print the string `s` using Zig's formatted string syntax.
    // `{s}` is a placeholder for the string, and `.{s}` passes the value.
    print("{s}", .{s});
}

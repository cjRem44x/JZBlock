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

const rl = @import("raylib");

pub fn toFlo(n: i32) f32 {
    return @as(f32, @floatFromInt(n));
}

pub fn toInt(n: f32) i32 {
    return @as(i32, @intFromFloat(n));
}

pub fn compute_game_size(sw: *f32, sh: *f32, pw: *f32, ph: *f32) void {
    const mw = monitor_width();
    const mh = monitor_height();

    if (mw > 1920 or mw < 1920 and mh < 1080 or mh > 1080) {
        const r_w = toFlo(mw) / 1920.0;
        const r_h = toFlo(mh) / 1080.0;

        pw.* *= r_w;
        ph.* *= r_h;
        sw.* *= pw.*;
        sh.* *= ph.*;
    }
}

pub fn monitor_width() i32 {
    return rl.getMonitorWidth(rl.getCurrentMonitor());
}

pub fn monitor_height() i32 {
    return rl.getMonitorHeight(rl.getCurrentMonitor());
}

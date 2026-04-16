// JZBlock — rando_racing.zig
// Pause-screen gambling minigame.
// 8 rainbow lanes, bet currency on a horse, watch the race, collect payout.

const std    = @import("std");
const rl     = @import("raylib");
const config = @import("config.zig");

fn pw(n: f32) i32 { return config.pw(n); }
fn ph(n: f32) i32 { return config.ph(n); }

// ── Tunables ──────────────────────────────────────────────────────────────────

const N_LANES:        usize = 8;
const TRACK_LEN:      f32   = 1000.0;
const TICK_INTERVAL:  f32   = 0.05;  // 20 ticks / sec
const SPEED_MIN:      i32   = 1;
const SPEED_MAX:      i32   = 10;
const MIN_BET:        i32   = 100;
const BET_STEP_SMALL: i32   = 100;
const BET_STEP_LARGE: i32   = 1000;
const RESULT_HOLD:    f32   = 4.0;   // seconds to display result screen

// Payout multipliers (x100 to avoid floats in the currency path)
const PAY_1ST: i32 = 200; // 2.0x
const PAY_2ND: i32 = 150; // 1.5x
const PAY_3RD: i32 = 125; // 1.25x

// ── Colors ────────────────────────────────────────────────────────────────────

const LANE_COLORS = [N_LANES]rl.Color{
    .{ .r = 255, .g = 30,  .b = 30,  .a = 255 }, // red
    .{ .r = 255, .g = 110, .b = 0,   .a = 255 }, // orange
    .{ .r = 240, .g = 220, .b = 0,   .a = 255 }, // yellow
    .{ .r = 30,  .g = 200, .b = 40,  .a = 255 }, // green
    .{ .r = 0,   .g = 190, .b = 255, .a = 255 }, // cyan
    .{ .r = 40,  .g = 60,  .b = 255, .a = 255 }, // blue
    .{ .r = 140, .g = 0,   .b = 255, .a = 255 }, // violet
    .{ .r = 255, .g = 0,   .b = 180, .a = 255 }, // pink
};

const LANE_NAMES = [N_LANES][:0]const u8{
    "Red", "Orange", "Yellow", "Green",
    "Cyan", "Blue", "Violet", "Pink",
};

const COL_TRACK_BG     = rl.Color{ .r = 10,  .g = 2,  .b = 2,  .a = 230 };
const COL_OVERLAY      = rl.Color{ .r = 0,   .g = 0,  .b = 0,  .a = 180 };
const COL_PANEL        = rl.Color{ .r = 18,  .g = 3,  .b = 2,  .a = 240 };
const COL_PANEL_BORDER = rl.Color{ .r = 120, .g = 20, .b = 0,  .a = 200 };
const COL_TEXT         = rl.Color{ .r = 255, .g = 200, .b = 160, .a = 255 };
const COL_DIM          = rl.Color{ .r = 140, .g = 80,  .b = 50,  .a = 180 };
const COL_GOLD         = rl.Color{ .r = 255, .g = 200, .b = 0,   .a = 255 };
const COL_WIN          = rl.Color{ .r = 60,  .g = 220, .b = 60,  .a = 255 };
const COL_LOSE         = rl.Color{ .r = 255, .g = 40,  .b = 30,  .a = 255 };
const COL_CURSOR       = rl.Color{ .r = 255, .g = 255, .b = 255, .a = 255 };

// ── State machine ─────────────────────────────────────────────────────────────

pub const Phase = enum { idle, bet_amount, bet_lane, racing, result };

// ── Struct ────────────────────────────────────────────────────────────────────

pub const RandoRacing = struct {
    phase:        Phase,
    positions:    [N_LANES]f32,
    finished:     [N_LANES]bool,
    finish_order: [N_LANES]usize,
    finish_count: usize,

    bet_amount:   i32,
    bet_lane:     usize,
    tick_timer:   f32,
    result_timer: f32,
    payout:       i32,
    prng:         std.Random.DefaultPrng,

    pub fn init() RandoRacing {
        const seed: u64 = @intCast(std.time.milliTimestamp());
        return .{
            .phase        = .idle,
            .positions    = [_]f32{0.0} ** N_LANES,
            .finished     = [_]bool{false} ** N_LANES,
            .finish_order = [_]usize{0} ** N_LANES,
            .finish_count = 0,
            .bet_amount   = MIN_BET,
            .bet_lane     = 0,
            .tick_timer   = 0.0,
            .result_timer = 0.0,
            .payout       = 0,
            .prng         = std.Random.DefaultPrng.init(seed),
        };
    }

    // ── Open from pause screen ────────────────────────────────────────────────

    pub fn open(self: *RandoRacing) void {
        if (self.phase == .idle) self.phase = .bet_amount;
    }

    pub fn isOpen(self: *const RandoRacing) bool {
        return self.phase != .idle;
    }

    // ── Update ────────────────────────────────────────────────────────────────

    pub fn update(self: *RandoRacing, delta: f32, currency: *i32) void {
        switch (self.phase) {
            .idle       => {},
            .bet_amount => self.updateBetAmount(currency),
            .bet_lane   => self.updateBetLane(),
            .racing     => self.updateRacing(delta, currency),
            .result     => {
                self.result_timer -= delta;
                if (self.result_timer <= 0.0 or
                    rl.isKeyPressed(.enter) or
                    rl.isKeyPressed(.space)) {
                    self.phase = .idle;
                }
            },
        }
    }

    fn updateBetAmount(self: *RandoRacing, currency: *const i32) void {
        const large = rl.isKeyDown(.left_shift) or rl.isKeyDown(.right_shift);
        const step  = if (large) BET_STEP_LARGE else BET_STEP_SMALL;

        if (rl.isKeyPressed(.up) or rl.isKeyPressed(.w))
            self.bet_amount += step;
        if (rl.isKeyPressed(.down) or rl.isKeyPressed(.s)) {
            self.bet_amount -= step;
            if (self.bet_amount < MIN_BET) self.bet_amount = MIN_BET;
        }
        if (self.bet_amount > currency.*) self.bet_amount = @max(MIN_BET, currency.*);
        if (self.bet_amount < MIN_BET)    self.bet_amount = MIN_BET;

        if (rl.isKeyPressed(.enter) or rl.isKeyPressed(.space)) {
            if (currency.* >= self.bet_amount) self.phase = .bet_lane;
        }
        if (rl.isKeyPressed(.escape)) self.phase = .idle;
    }

    fn updateBetLane(self: *RandoRacing) void {
        if (rl.isKeyPressed(.left) or rl.isKeyPressed(.a)) {
            if (self.bet_lane > 0) self.bet_lane -= 1;
        }
        if (rl.isKeyPressed(.right) or rl.isKeyPressed(.d)) {
            if (self.bet_lane < N_LANES - 1) self.bet_lane += 1;
        }
        if (rl.isKeyPressed(.enter) or rl.isKeyPressed(.space)) {
            self.startRace();
        }
        if (rl.isKeyPressed(.escape)) self.phase = .bet_amount;
    }

    fn startRace(self: *RandoRacing) void {
        for (&self.positions) |*p| p.* = 0.0;
        for (&self.finished)  |*f| f.* = false;
        self.finish_count = 0;
        self.tick_timer   = 0.0;
        self.phase        = .racing;
    }

    fn updateRacing(self: *RandoRacing, delta: f32, currency: *i32) void {
        self.tick_timer += delta;
        if (self.tick_timer < TICK_INTERVAL) return;
        self.tick_timer = 0.0;

        const rand = self.prng.random();
        for (0..N_LANES) |i| {
            if (self.finished[i]) continue;
            const speed: f32 = @floatFromInt(SPEED_MIN + rand.intRangeLessThan(i32, 0, SPEED_MAX - SPEED_MIN + 1));
            self.positions[i] += speed;
            if (self.positions[i] >= TRACK_LEN) {
                self.positions[i] = TRACK_LEN;
                self.finished[i]  = true;
                self.finish_order[self.finish_count] = i;
                self.finish_count += 1;
            }
        }

        if (self.finish_count == N_LANES) {
            const place = self.placeOf(self.bet_lane);
            const mul   = switch (place) {
                0 => PAY_1ST,
                1 => PAY_2ND,
                2 => PAY_3RD,
                else => 0,
            };
            currency.* -= self.bet_amount;
            if (mul > 0) {
                const winnings = @divTrunc(self.bet_amount * mul, 100);
                currency.* += winnings;
                self.payout = winnings - self.bet_amount;
            } else {
                self.payout = -self.bet_amount;
            }
            self.result_timer = RESULT_HOLD;
            self.phase = .result;
        }
    }

    fn placeOf(self: *const RandoRacing, lane: usize) usize {
        for (0..self.finish_count) |i| {
            if (self.finish_order[i] == lane) return i;
        }
        return N_LANES;
    }

    // ── Draw ──────────────────────────────────────────────────────────────────

    pub fn draw(self: *const RandoRacing, sw: i32, sh: i32) void {
        switch (self.phase) {
            .idle       => {},
            .bet_amount => self.drawBetAmount(sw, sh),
            .bet_lane   => self.drawBetLane(sw, sh),
            .racing     => self.drawRace(sw, sh),
            .result     => self.drawResult(sw, sh),
        }
    }

    fn panelRect(sw: i32, sh: i32, w: i32, h: i32) struct { x: i32, y: i32 } {
        return .{ .x = @divTrunc(sw - w, 2), .y = @divTrunc(sh - h, 2) };
    }

    fn drawOverlay(sw: i32, sh: i32) void {
        rl.drawRectangle(0, 0, sw, sh, COL_OVERLAY);
    }

    fn drawPanel(x: i32, y: i32, w: i32, h: i32) void {
        rl.drawRectangle(x, y, w, h, COL_PANEL);
        rl.drawRectangleLines(x, y, w, h, COL_PANEL_BORDER);
    }

    fn centered(text: [:0]const u8, font_size: i32, y: i32, sw: i32, col: rl.Color) void {
        const tw = rl.measureText(text, font_size);
        rl.drawText(text, @divTrunc(sw - tw, 2), y, font_size, col);
    }

    fn drawBetAmount(self: *const RandoRacing, sw: i32, sh: i32) void {
        drawOverlay(sw, sh);
        const panw = pw(500);
        const panh = ph(280);
        const p = panelRect(sw, sh, panw, panh);
        drawPanel(p.x, p.y, panw, panh);

        centered("RANDO RACING", ph(28), p.y + ph(20), sw, COL_GOLD);
        centered("How much to bet?", ph(20), p.y + ph(68), sw, COL_TEXT);

        var buf: [64]u8 = undefined;
        const amt_str = std.fmt.bufPrintZ(&buf, "${}", .{self.bet_amount}) catch "$???";
        centered(amt_str, ph(36), p.y + ph(110), sw, COL_GOLD);

        centered("UP/W = +100  |  Shift = x10", ph(16), p.y + ph(162), sw, COL_DIM);
        centered("DOWN/S = -100", ph(16), p.y + ph(184), sw, COL_DIM);
        centered("[Enter/Space] Confirm    [Esc] Cancel", ph(16), p.y + ph(222), sw, COL_DIM);
    }

    fn drawBetLane(self: *const RandoRacing, sw: i32, sh: i32) void {
        drawOverlay(sw, sh);
        const panw = pw(620);
        const panh = ph(320);
        const p = panelRect(sw, sh, panw, panh);
        drawPanel(p.x, p.y, panw, panh);

        centered("CHOOSE YOUR LANE", ph(24), p.y + ph(16), sw, COL_GOLD);

        var buf: [64]u8 = undefined;
        const amt_str = std.fmt.bufPrintZ(&buf, "Bet: ${}", .{self.bet_amount}) catch "Bet: $???";
        centered(amt_str, ph(18), p.y + ph(52), sw, COL_TEXT);

        const swatch_w = pw(52);
        const swatch_h = ph(52);
        const gap      = pw(8);
        const row_w: i32 = @as(i32, N_LANES) * (swatch_w + gap) - gap;
        const row_x: i32 = @divTrunc(sw - row_w, 2);
        const row_y: i32 = p.y + ph(90);

        for (0..N_LANES) |i| {
            const ix: i32 = @intCast(i);
            const sx = row_x + ix * (swatch_w + gap);
            const col = LANE_COLORS[i];

            rl.drawRectangle(sx, row_y, swatch_w, swatch_h,
                rl.Color{ .r = @divTrunc(col.r, 4), .g = @divTrunc(col.g, 4),
                           .b = @divTrunc(col.b, 4), .a = 200 });
            rl.drawRectangle(sx + pw(4), row_y + ph(4), swatch_w - pw(8), swatch_h - ph(8), col);

            if (i == self.bet_lane) {
                rl.drawRectangleLines(sx - pw(2), row_y - ph(2), swatch_w + pw(4), swatch_h + ph(4), COL_CURSOR);
                rl.drawRectangleLines(sx - pw(3), row_y - ph(3), swatch_w + pw(6), swatch_h + ph(6), COL_CURSOR);
            }

            const name = LANE_NAMES[i];
            const nw = rl.measureText(name, ph(13));
            rl.drawText(name, sx + @divTrunc(swatch_w - nw, 2), row_y + swatch_h + ph(6), ph(13), COL_DIM);
        }

        const sel_col = LANE_COLORS[self.bet_lane];
        var nbuf: [64]u8 = undefined;
        const sel_str = std.fmt.bufPrintZ(&nbuf, "Selected: {s}", .{LANE_NAMES[self.bet_lane]}) catch "Selected";
        centered(sel_str, ph(20), row_y + swatch_h + ph(30), sw, sel_col);

        centered("LEFT/RIGHT to choose   [Enter] Start Race   [Esc] Back", ph(15),
            p.y + panh - ph(36), sw, COL_DIM);
    }

    fn drawRace(self: *const RandoRacing, sw: i32, sh: i32) void {
        drawOverlay(sw, sh);

        const panw: i32 = sw - pw(120);
        const panh: i32 = @as(i32, N_LANES) * ph(56) + ph(80);
        const p = panelRect(sw, sh, panw, panh);
        drawPanel(p.x, p.y, panw, panh);

        centered("RANDO RACING", ph(22), p.y + ph(12), sw, COL_GOLD);

        const track_x: i32 = p.x + pw(90);
        const track_w: i32 = panw - pw(110);
        const lane_h   = ph(44);
        const lane_gap = ph(12);

        for (0..N_LANES) |i| {
            const ix: i32 = @intCast(i);
            const ty = p.y + 50 + ix * (lane_h + lane_gap);
            const col = LANE_COLORS[i];

            rl.drawText(LANE_NAMES[i], p.x + pw(8), ty + @divTrunc(lane_h - ph(18), 2), ph(16),
                if (i == self.bet_lane) col else COL_DIM);

            rl.drawRectangle(track_x, ty, track_w, lane_h,
                rl.Color{ .r = @divTrunc(col.r, 6), .g = @divTrunc(col.g, 6),
                           .b = @divTrunc(col.b, 6), .a = 200 });

            const frac = self.positions[i] / TRACK_LEN;
            const filled: i32 = @intFromFloat(@as(f32, @floatFromInt(track_w)) * frac);
            if (filled > 0)
                rl.drawRectangle(track_x, ty, filled, lane_h, col);

            if (filled > pw(4))
                rl.drawRectangle(track_x + filled - pw(4), ty + ph(2), pw(8), lane_h - ph(4),
                    rl.Color{ .r = 255, .g = 255, .b = 255, .a = 200 });

            if (i == self.bet_lane)
                rl.drawRectangleLines(track_x - 2, ty - 2, track_w + 4, lane_h + 4, COL_CURSOR);
        }
    }

    fn drawResult(self: *const RandoRacing, sw: i32, sh: i32) void {
        drawOverlay(sw, sh);
        const panw = pw(520);
        const panh = ph(300);
        const p = panelRect(sw, sh, panw, panh);
        drawPanel(p.x, p.y, panw, panh);

        const place = self.placeOf(self.bet_lane);
        const won   = place < 3;
        const place_str: [:0]const u8 = switch (place) {
            0 => "1st Place! (2x)",
            1 => "2nd Place! (1.5x)",
            2 => "3rd Place! (1.25x)",
            else => "No payout",
        };

        centered("RACE OVER", ph(28), p.y + ph(20), sw, COL_GOLD);

        const lane_col = LANE_COLORS[self.bet_lane];
        var lbuf: [64]u8 = undefined;
        const lane_str = std.fmt.bufPrintZ(&lbuf, "Your lane: {s}", .{LANE_NAMES[self.bet_lane]}) catch "Your lane";
        centered(lane_str, ph(20), p.y + ph(68), sw, lane_col);
        centered(place_str, ph(22), p.y + ph(104), sw, if (won) COL_WIN else COL_LOSE);

        var pbuf: [64]u8 = undefined;
        const net_str = if (self.payout >= 0)
            std.fmt.bufPrintZ(&pbuf, "+${}", .{self.payout}) catch "+$???"
        else
            std.fmt.bufPrintZ(&pbuf, "-${}", .{-self.payout}) catch "-$???";
        centered(net_str, ph(32), p.y + ph(144), sw, if (self.payout >= 0) COL_WIN else COL_LOSE);

        centered("Top 3:", ph(16), p.y + ph(192), sw, COL_DIM);
        var rbuf: [64]u8 = undefined;
        var ox: i32 = @divTrunc(sw - pw(360), 2);
        const top = @min(self.finish_count, 3);
        for (0..top) |r| {
            const lane = self.finish_order[r];
            const rc = LANE_COLORS[lane];
            const rstr = std.fmt.bufPrintZ(&rbuf, "{}. {s}", .{ r + 1, LANE_NAMES[lane] }) catch "?";
            rl.drawText(rstr, ox, p.y + ph(214), ph(16), rc);
            ox += pw(120);
        }

        centered("[Enter/Space] Close", ph(16), p.y + panh - ph(36), sw, COL_DIM);
    }
};

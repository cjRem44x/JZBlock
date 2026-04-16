// JZBlock — zombie.zig
// Zombie enemy with three boss tiers.

const std    = @import("std");
const rl     = @import("raylib");
const config = @import("config.zig");
const Player = @import("player.zig").Player;

pub const BossTier = enum { none, basic, medium, hard };

pub const Zombie = struct {
    x:             f32,
    y:             f32,
    size:          f32,
    health:        i32,
    max_health:    i32,
    speed:         f32,
    color:         rl.Color,
    is_dead:       bool,
    last_hit_time: f32,
    boss_tier:     BossTier,
    damage:        i32,
    kill_reward:   i32,

    // ── Constructors ─────────────────────────────────────────────────────────

    pub fn init(rand: std.Random) Zombie {
        const screen_w: f32 = @floatFromInt(config.SCREEN_WIDTH);
        const screen_h: f32 = @floatFromInt(config.SCREEN_HEIGHT);
        const sz = config.pwf(config.ZOMBIE_SIZE);
        return .{
            .x             = rand.float(f32) * (screen_w - sz),
            .y             = rand.float(f32) * (screen_h - sz),
            .size          = sz,
            .health        = config.ZOMBIE_HEALTH,
            .max_health    = config.ZOMBIE_HEALTH,
            .speed         = config.pwf(config.ZOMBIE_SPEED),
            .color         = config.ZOMBIE_COLOR,
            .is_dead       = false,
            .last_hit_time = config.ZOMBIE_HIT_COOLDOWN,
            .boss_tier     = .none,
            .damage        = config.ZOMBIE_DAMAGE,
            .kill_reward   = config.KILL_REWARD,
        };
    }

    pub fn initWithHealth(rand: std.Random, extra_health: i32) Zombie {
        var z = init(rand);
        z.health     += extra_health;
        z.max_health  = z.health;
        return z;
    }

    pub fn initBoss(rand: std.Random, tier: BossTier, extra_health: i32) Zombie {
        var z = init(rand);
        z.boss_tier = tier;
        switch (tier) {
            .basic => {
                z.health      = config.ZOMBIE_HEALTH * config.BOSS_BASIC_HP_MUL + extra_health;
                z.damage      = @intFromFloat(@as(f32, config.ZOMBIE_DAMAGE) * config.BOSS_BASIC_DMG_MUL);
                z.kill_reward = config.KILL_REWARD * config.BOSS_BASIC_CASH_MUL;
                z.size        = config.pwf(config.BOSS_BASIC_SIZE);
                z.speed       = config.pwf(config.BOSS_BASIC_SPEED);
                z.color       = config.BOSS_BASIC_COLOR;
            },
            .medium => {
                z.health      = config.ZOMBIE_HEALTH * config.BOSS_MED_HP_MUL + extra_health;
                z.damage      = @intFromFloat(@as(f32, config.ZOMBIE_DAMAGE) * config.BOSS_MED_DMG_MUL);
                z.kill_reward = config.KILL_REWARD * config.BOSS_MED_CASH_MUL;
                z.size        = config.pwf(config.BOSS_MED_SIZE);
                z.speed       = config.pwf(config.BOSS_MED_SPEED);
                z.color       = config.BOSS_MED_COLOR;
            },
            .hard => {
                z.health      = config.ZOMBIE_HEALTH * config.BOSS_HARD_HP_MUL + extra_health;
                z.damage      = @intFromFloat(@as(f32, config.ZOMBIE_DAMAGE) * config.BOSS_HARD_DMG_MUL);
                z.kill_reward = config.KILL_REWARD * config.BOSS_HARD_CASH_MUL;
                z.size        = config.pwf(config.BOSS_HARD_SIZE);
                z.speed       = config.pwf(config.BOSS_HARD_SPEED);
                z.color       = config.BOSS_HARD_COLOR;
            },
            .none => {},
        }
        z.max_health = z.health;
        return z;
    }

    // ── Update ────────────────────────────────────────────────────────────────

    pub fn update(self: *Zombie, delta: f32) void {
        if (self.last_hit_time < config.ZOMBIE_HIT_COOLDOWN) {
            self.last_hit_time += delta;
        }
    }

    pub fn chase(self: *Zombie, player: *const Player, delta: f32) void {
        if (self.is_dead) return;
        const dx  = player.x - self.x;
        const dy  = player.y - self.y;
        const len = @sqrt(dx * dx + dy * dy);
        if (len == 0.0) return;
        self.x += (dx / len) * self.speed * delta;
        self.y += (dy / len) * self.speed * delta;
    }

    pub fn applySeparation(self: *Zombie, other: *const Zombie) void {
        if (self.is_dead or other.is_dead) return;
        if (self == other) return;
        const distX = other.x - self.x;
        const distY = other.y - self.y;
        const dist  = @sqrt(distX * distX + distY * distY);
        if (dist < self.size * 1.5 and dist > 0.0) {
            self.x -= distX * 0.05;
            self.y -= distY * 0.05;
        }
    }

    pub fn keepInBounds(self: *Zombie) void {
        const screen_w: f32 = @floatFromInt(config.SCREEN_WIDTH);
        const screen_h: f32 = @floatFromInt(config.SCREEN_HEIGHT);
        if (self.x < 0)              self.x = 0;
        if (self.x + self.size > screen_w) self.x = screen_w - self.size;
        if (self.y < 0)              self.y = 0;
        if (self.y + self.size > screen_h) self.y = screen_h - self.size;
    }

    // ── Draw ─────────────────────────────────────────────────────────────────

    pub fn draw(self: *const Zombie) void {
        const x:  i32 = @intFromFloat(self.x);
        const y:  i32 = @intFromFloat(self.y);
        const sz: i32 = @intFromFloat(self.size);

        // Boss HP bar
        if (self.boss_tier != .none and !self.is_dead) {
            self.drawHpBar(x, y, sz);
        }

        // Glow
        if (!self.is_dead) {
            const glow_col: rl.Color = switch (self.boss_tier) {
                .none   => config.ZOMBIE_GLOW,
                .basic  => config.BOSS_BASIC_GLOW,
                .medium => config.BOSS_MED_GLOW,
                .hard   => config.BOSS_HARD_GLOW,
            };
            const g: i32 = switch (self.boss_tier) {
                .none   => config.pw(3),
                .basic  => config.pw(7),
                .medium => config.pw(11),
                .hard   => config.pw(16),
            };
            rl.drawRectangle(x - g, y - g, sz + g * 2, sz + g * 2, glow_col);
        }

        rl.drawRectangle(x, y, sz, sz, self.color);

        if (!self.is_dead) {
            // Highlight bevel
            rl.drawRectangle(x, y, sz, config.ph(3), config.ZOMBIE_HIGHLIGHT);
            rl.drawRectangle(x, y, config.pw(3), sz, config.ZOMBIE_HIGHLIGHT);

            // Boss border
            if (self.boss_tier != .none) {
                const bc: rl.Color = switch (self.boss_tier) {
                    .basic  => config.BOSS_BASIC_GLOW,
                    .medium => config.BOSS_MED_GLOW,
                    .hard   => config.BOSS_HARD_OUTLINE,
                    .none   => unreachable,
                };
                rl.drawRectangleLines(x, y, sz, sz, bc);
                if (self.boss_tier == .hard) {
                    rl.drawRectangleLines(x - 1, y - 1, sz + 2, sz + 2, config.BOSS_HARD_OUTLINE);
                }
            }

            // Face
            if (self.boss_tier != .none) {
                self.drawBossFace(x, y, sz);
            } else {
                const eye_size: i32 = @max(3, @divTrunc(sz, 10));
                const eye_y    = y + @divTrunc(sz, 4);
                const left_ex  = x + @divTrunc(sz, 4);
                const right_ex = x + sz - @divTrunc(sz, 4) - eye_size;

                rl.drawRectangle(left_ex  - 2, eye_y - 2, eye_size + 4, eye_size + 4, config.ZOMBIE_EYE_GLOW);
                rl.drawRectangle(right_ex - 2, eye_y - 2, eye_size + 4, eye_size + 4, config.ZOMBIE_EYE_GLOW);
                rl.drawRectangle(left_ex,  eye_y, eye_size, eye_size, config.ZOMBIE_EYE);
                rl.drawRectangle(right_ex, eye_y, eye_size, eye_size, config.ZOMBIE_EYE);

                rl.drawRectangle(left_ex  - 2, eye_y - 4, eye_size + 4, 2, config.ZOMBIE_EYEBROW);
                rl.drawRectangle(left_ex  + eye_size, eye_y - 6, 3, 2, config.ZOMBIE_EYEBROW);
                rl.drawRectangle(right_ex - 2, eye_y - 4, eye_size + 4, 2, config.ZOMBIE_EYEBROW);
                rl.drawRectangle(right_ex - 3, eye_y - 6, 3, 2, config.ZOMBIE_EYEBROW);

                const mouth_y = y + @divTrunc(sz * 2, 3);
                const mouth_w = @divTrunc(sz, 2);
                const mouth_x = x + @divTrunc(sz, 4);
                rl.drawRectangle(mouth_x, mouth_y, mouth_w, 4, config.ZOMBIE_MOUTH);
                var tx: i32 = mouth_x;
                while (tx < mouth_x + mouth_w) : (tx += 4) {
                    rl.drawRectangle(tx, mouth_y, 2, 3, config.ZOMBIE_TEETH);
                }
            }

            // Boss tier label
            if (self.boss_tier != .none) {
                const tag: [:0]const u8 = switch (self.boss_tier) {
                    .basic  => "B",
                    .medium => "M",
                    .hard   => "H",
                    .none   => unreachable,
                };
                const tag_col: rl.Color = switch (self.boss_tier) {
                    .basic  => config.BOSS_BASIC_GLOW,
                    .medium => config.BOSS_MED_GLOW,
                    .hard   => config.BOSS_HARD_GLOW,
                    .none   => unreachable,
                };
                const tsz: i32 = @max(10, @divTrunc(sz, 4));
                const tw = rl.measureText(tag, tsz);
                rl.drawText(tag, x + @divTrunc(sz - tw, 2), y + sz - tsz - 2, tsz, tag_col);
            }
        } else {
            // Dead face
            const eye_y    = y + @divTrunc(sz, 4);
            const left_ex  = x + @divTrunc(sz, 4);
            const right_ex = x + sz - @divTrunc(sz, 4) - 4;
            const dc = config.ZOMBIE_DEAD_FACE;

            rl.drawLine(left_ex, eye_y, left_ex + 5, eye_y + 5, dc);
            rl.drawLine(left_ex + 5, eye_y, left_ex, eye_y + 5, dc);
            rl.drawLine(right_ex, eye_y, right_ex + 5, eye_y + 5, dc);
            rl.drawLine(right_ex + 5, eye_y, right_ex, eye_y + 5, dc);

            const mouth_y = y + @divTrunc(sz * 2, 3);
            rl.drawRectangle(x + @divTrunc(sz, 4), mouth_y, @divTrunc(sz, 2), 2, dc);
        }
    }

    fn drawBossFace(self: *const Zombie, x: i32, y: i32, sz: i32) void {
        switch (self.boss_tier) {
            .basic => {
                // Green boss: slanted angular eyes + thick furrowed brow + wide jagged grin
                const eye_w: i32 = @max(5, @divTrunc(sz, 7));
                const eye_h: i32 = @max(3, @divTrunc(sz, 12));
                const eye_y = y + @divTrunc(sz, 4);
                const lex   = x + @divTrunc(sz, 5);
                const rex   = x + sz - @divTrunc(sz, 5) - eye_w;

                const eg = rl.Color{ .r = 255, .g = 255, .b = 0, .a = 120 };
                rl.drawRectangle(lex - 2, eye_y - 2, eye_w + 4, eye_h + 4, eg);
                rl.drawRectangle(rex - 2, eye_y - 2, eye_w + 4, eye_h + 4, eg);
                const ec = rl.Color{ .r = 220, .g = 220, .b = 0, .a = 255 };
                rl.drawRectangle(lex, eye_y, eye_w, eye_h, ec);
                rl.drawRectangle(rex, eye_y, eye_w, eye_h, ec);
                const bc = rl.Color{ .r = 0, .g = 60, .b = 0, .a = 255 };
                rl.drawRectangle(lex,                        eye_y - 5, @divTrunc(eye_w, 2), 3, bc);
                rl.drawRectangle(lex + @divTrunc(eye_w, 2), eye_y - 7, @divTrunc(eye_w, 2), 3, bc);
                rl.drawRectangle(rex + @divTrunc(eye_w, 2), eye_y - 5, @divTrunc(eye_w, 2), 3, bc);
                rl.drawRectangle(rex,                        eye_y - 7, @divTrunc(eye_w, 2), 3, bc);

                const mouth_y = y + @divTrunc(sz * 3, 5);
                const mouth_x = x + @divTrunc(sz, 6);
                const mouth_w = @divTrunc(sz * 2, 3);
                rl.drawRectangle(mouth_x, mouth_y, mouth_w, 4,
                    rl.Color{ .r = 0, .g = 20, .b = 0, .a = 255 });
                var tx: i32 = mouth_x;
                var up: bool = true;
                while (tx < mouth_x + mouth_w) : (tx += 5) {
                    const ty = if (up) mouth_y - 2 else mouth_y + 2;
                    rl.drawRectangle(tx, ty, 3, 5,
                        rl.Color{ .r = 160, .g = 220, .b = 160, .a = 255 });
                    up = !up;
                }
            },
            .medium => {
                // Purple boss: wide diamond eyes + sharp V-brows + fanged mouth
                const eye_r: i32 = @max(4, @divTrunc(sz, 9));
                const eye_y = y + @divTrunc(sz, 4);
                const lcx   = x + @divTrunc(sz, 4) + eye_r;
                const rcx   = x + sz - @divTrunc(sz, 4) - eye_r;

                const eg = rl.Color{ .r = 255, .g = 0, .b = 255, .a = 100 };
                rl.drawRectangle(lcx - eye_r - 2, eye_y - eye_r - 2, eye_r*2+4, eye_r*2+4, eg);
                rl.drawRectangle(rcx - eye_r - 2, eye_y - eye_r - 2, eye_r*2+4, eye_r*2+4, eg);
                const ec = rl.Color{ .r = 255, .g = 60, .b = 255, .a = 255 };
                rl.drawRectangle(lcx - eye_r, eye_y - @divTrunc(eye_r, 2), eye_r*2, eye_r, ec);
                rl.drawRectangle(lcx - @divTrunc(eye_r, 2), eye_y - eye_r, eye_r, eye_r*2, ec);
                rl.drawRectangle(rcx - eye_r, eye_y - @divTrunc(eye_r, 2), eye_r*2, eye_r, ec);
                rl.drawRectangle(rcx - @divTrunc(eye_r, 2), eye_y - eye_r, eye_r, eye_r*2, ec);
                rl.drawRectangle(lcx - 1, eye_y - 1, 3, 3, rl.Color{ .r = 0, .g = 0, .b = 0, .a = 255 });
                rl.drawRectangle(rcx - 1, eye_y - 1, 3, 3, rl.Color{ .r = 0, .g = 0, .b = 0, .a = 255 });

                const brow_c = rl.Color{ .r = 50, .g = 0, .b = 80, .a = 255 };
                const brow_y = eye_y - eye_r - 4;
                rl.drawRectangle(lcx - eye_r,     brow_y,   @divTrunc(eye_r*2, 3), 3, brow_c);
                rl.drawRectangle(lcx - eye_r + @divTrunc(eye_r*2,3), brow_y + 3, @divTrunc(eye_r*2,3)+1, 3, brow_c);
                rl.drawRectangle(rcx + @divTrunc(eye_r*2,3) - eye_r, brow_y,   @divTrunc(eye_r*2, 3), 3, brow_c);
                rl.drawRectangle(rcx - eye_r, brow_y + 3, @divTrunc(eye_r*2,3)+1, 3, brow_c);

                const mouth_y = y + @divTrunc(sz * 3, 5);
                const mouth_x = x + @divTrunc(sz, 5);
                const mouth_w = @divTrunc(sz * 3, 5);
                rl.drawRectangle(mouth_x, mouth_y, mouth_w, 4,
                    rl.Color{ .r = 20, .g = 0, .b = 20, .a = 255 });
                const fang_c = rl.Color{ .r = 220, .g = 180, .b = 255, .a = 255 };
                const fw = @max(3, @divTrunc(mouth_w, 6));
                const fang1_x = mouth_x + @divTrunc(mouth_w, 4) - @divTrunc(fw, 2);
                const fang2_x = mouth_x + @divTrunc(mouth_w * 3, 4) - @divTrunc(fw, 2);
                rl.drawRectangle(fang1_x, mouth_y, fw, fw + 3, fang_c);
                rl.drawRectangle(fang2_x, mouth_y, fw, fw + 3, fang_c);
            },
            .hard => {
                // Black/red boss: hollow X-eyes (glowing red crosses) + skull grin
                const eye_sz: i32 = @max(6, @divTrunc(sz, 7));
                const eye_y  = y + @divTrunc(sz, 4);
                const lex    = x + @divTrunc(sz, 5);
                const rex    = x + sz - @divTrunc(sz, 5) - eye_sz;

                const eg = rl.Color{ .r = 255, .g = 0, .b = 0, .a = 140 };
                rl.drawRectangle(lex - 3, eye_y - 3, eye_sz + 6, eye_sz + 6, eg);
                rl.drawRectangle(rex - 3, eye_y - 3, eye_sz + 6, eye_sz + 6, eg);
                const ec = rl.Color{ .r = 255, .g = 0, .b = 0, .a = 255 };
                const th: i32 = 2;
                rl.drawRectangle(lex,              eye_y,              eye_sz, th, ec);
                rl.drawRectangle(lex,              eye_y + eye_sz - th, eye_sz, th, ec);
                rl.drawRectangle(lex,              eye_y,              th, eye_sz, ec);
                rl.drawRectangle(lex + eye_sz - th, eye_y,             th, eye_sz, ec);
                rl.drawRectangle(rex,              eye_y,              eye_sz, th, ec);
                rl.drawRectangle(rex,              eye_y + eye_sz - th, eye_sz, th, ec);
                rl.drawRectangle(rex,              eye_y,              th, eye_sz, ec);
                rl.drawRectangle(rex + eye_sz - th, eye_y,             th, eye_sz, ec);

                const mouth_y = y + @divTrunc(sz * 3, 5);
                const mouth_x = x + @divTrunc(sz, 6);
                const mouth_w = @divTrunc(sz * 2, 3);
                rl.drawRectangle(mouth_x, mouth_y, mouth_w, @divTrunc(sz, 8),
                    rl.Color{ .r = 30, .g = 0, .b = 0, .a = 255 });
                const tooth_w: i32 = @max(2, @divTrunc(mouth_w, 9));
                const gap_w: i32   = @max(1, @divTrunc(mouth_w, 18));
                const tooth_h: i32 = @divTrunc(sz, 8);
                var tx: i32 = mouth_x + 1;
                while (tx + tooth_w <= mouth_x + mouth_w) : (tx += tooth_w + gap_w) {
                    rl.drawRectangle(tx, mouth_y, tooth_w, tooth_h,
                        rl.Color{ .r = 200, .g = 180, .b = 180, .a = 255 });
                }
                rl.drawRectangle(mouth_x, mouth_y + tooth_h, mouth_w, 2,
                    rl.Color{ .r = 200, .g = 0, .b = 0, .a = 180 });
            },
            .none => {},
        }
    }

    fn drawHpBar(self: *const Zombie, x: i32, y: i32, sz: i32) void {
        const bar_y   = y - config.ph(12);
        const bar_h   = config.ph(6);
        const frac    = @max(0.0, @as(f32, @floatFromInt(self.health)) /
                                   @as(f32, @floatFromInt(self.max_health)));
        const filled: i32 = @intFromFloat(@as(f32, @floatFromInt(sz)) * frac);
        rl.drawRectangle(x, bar_y, sz, bar_h, config.BOSS_HP_BG);
        if (filled > 0) rl.drawRectangle(x, bar_y, filled, bar_h, config.BOSS_HP_FILL);
        rl.drawRectangleLines(x, bar_y, sz, bar_h, config.BOSS_HP_BORDER);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    pub fn getRect(self: *const Zombie) rl.Rectangle {
        return .{ .x = self.x, .y = self.y, .width = self.size, .height = self.size };
    }

    pub fn isCollidingWith(self: *const Zombie, rect: rl.Rectangle) bool {
        return rl.checkCollisionRecs(self.getRect(), rect);
    }

    pub fn takeDamage(self: *Zombie, amount: i32) void {
        self.health -= amount;
        // die() is called by engine.processZombieDeaths (which awards kills/currency/blood)
    }

    pub fn die(self: *Zombie) void {
        self.is_dead = true;
        self.speed   = 0;
        self.color   = switch (self.boss_tier) {
            .none   => config.ZOMBIE_DEAD_COLOR,
            .basic  => config.BOSS_BASIC_DEAD,
            .medium => config.BOSS_MED_DEAD,
            .hard   => config.BOSS_HARD_DEAD,
        };
    }

    pub fn canHitPlayer(self: *const Zombie) bool {
        return !self.is_dead and self.last_hit_time >= config.ZOMBIE_HIT_COOLDOWN;
    }

    pub fn resetHitTimer(self: *Zombie) void {
        self.last_hit_time = 0.0;
    }
};

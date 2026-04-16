// JZBlock — blood.zig
// Blood splatter particles (on hit) and blood pools (on death).
// Both are fixed-size arrays — no allocator needed.

const std = @import("std");
const rl  = @import("raylib");

// ── Tunables ──────────────────────────────────────────────────────────────────

const MAX_PARTICLES:      usize = 512;
const MAX_POOLS:          usize = 128;
const PARTICLE_COUNT_MIN: u8   = 7;
const PARTICLE_COUNT_MAX: u8   = 14;
const PARTICLE_LIFE_MIN:  f32  = 0.25;
const PARTICLE_LIFE_MAX:  f32  = 0.55;
const PARTICLE_SPEED_MIN: f32  = 60.0;
const PARTICLE_SPEED_MAX: f32  = 220.0;
const GRAVITY:            f32  = 260.0;
const POOL_LIFE:          f32  = 5.0;
const POOL_FADE:          f32  = 1.2;  // fade-out window (seconds before despawn)
const N_BLOBS:            usize = 8;   // circles per pool

// ── Colors ────────────────────────────────────────────────────────────────────

const COL_SPLATTER = rl.Color{ .r = 180, .g = 0,  .b = 0,  .a = 255 };
const COL_POOL     = rl.Color{ .r = 90,  .g = 0,  .b = 0,  .a = 220 };

// ── Structs ───────────────────────────────────────────────────────────────────

const Particle = struct {
    x:        f32  = 0,
    y:        f32  = 0,
    vx:       f32  = 0,
    vy:       f32  = 0,
    size:     f32  = 0,
    life:     f32  = 0,
    max_life: f32  = 0,
    active:   bool = false,
};

const Blob = struct { dx: f32, dy: f32, r: f32 };

const Pool = struct {
    x:      f32  = 0,
    y:      f32  = 0,
    blobs:  [N_BLOBS]Blob = [_]Blob{.{ .dx = 0, .dy = 0, .r = 0 }} ** N_BLOBS,
    life:   f32  = 0,
    active: bool = false,
};

// ── System ────────────────────────────────────────────────────────────────────

pub const BloodSystem = struct {
    particles: [MAX_PARTICLES]Particle,
    pools:     [MAX_POOLS]Pool,

    pub fn init() BloodSystem {
        return .{
            .particles = [_]Particle{.{}} ** MAX_PARTICLES,
            .pools     = [_]Pool{.{}}     ** MAX_POOLS,
        };
    }

    pub fn update(self: *BloodSystem, delta: f32) void {
        for (&self.particles) |*p| {
            if (!p.active) continue;
            p.x  += p.vx * delta;
            p.y  += p.vy * delta;
            p.vy += GRAVITY * delta;
            p.life -= delta;
            if (p.life <= 0) p.active = false;
        }
        for (&self.pools) |*pool| {
            if (!pool.active) continue;
            pool.life -= delta;
            if (pool.life <= 0) pool.active = false;
        }
    }

    /// Spawn a spray of particles centred at (cx, cy).
    pub fn spawnSplatter(self: *BloodSystem, rand: std.Random, cx: f32, cy: f32) void {
        const count = PARTICLE_COUNT_MIN +
            rand.intRangeAtMost(u8, 0, PARTICLE_COUNT_MAX - PARTICLE_COUNT_MIN);
        var spawned: u8 = 0;
        for (&self.particles) |*p| {
            if (p.active or spawned >= count) continue;
            const angle = rand.float(f32) * std.math.pi * 2.0;
            const speed = PARTICLE_SPEED_MIN + rand.float(f32) * (PARTICLE_SPEED_MAX - PARTICLE_SPEED_MIN);
            const life  = PARTICLE_LIFE_MIN  + rand.float(f32) * (PARTICLE_LIFE_MAX  - PARTICLE_LIFE_MIN);
            p.* = .{
                .x        = cx + (rand.float(f32) - 0.5) * 10.0,
                .y        = cy + (rand.float(f32) - 0.5) * 10.0,
                .vx       = @cos(angle) * speed,
                .vy       = @sin(angle) * speed - 40.0,  // slight upward bias
                .size     = 2.0 + rand.float(f32) * 5.0,
                .life     = life,
                .max_life = life,
                .active   = true,
            };
            spawned += 1;
        }
    }

    /// Spawn an irregular blood pool at (cx, cy) sized relative to entity_size.
    pub fn spawnPool(self: *BloodSystem, rand: std.Random, cx: f32, cy: f32, entity_size: f32) void {
        for (&self.pools) |*pool| {
            if (pool.active) continue;
            pool.x    = cx;
            pool.y    = cy;
            pool.life = POOL_LIFE;
            pool.active = true;
            const r_base = entity_size * 0.38;
            for (&pool.blobs) |*blob| {
                blob.dx = (rand.float(f32) - 0.5) * entity_size * 0.9;
                blob.dy = (rand.float(f32) - 0.5) * entity_size * 0.6;
                blob.r  = r_base * (0.4 + rand.float(f32) * 0.9);
            }
            return;
        }
    }

    /// Draw pools (call BEFORE entities so they appear underneath).
    pub fn drawPools(self: *const BloodSystem) void {
        for (&self.pools) |*pool| {
            if (!pool.active) continue;
            const a: u8 = if (pool.life <= POOL_FADE)
                @intFromFloat((pool.life / POOL_FADE) * @as(f32, COL_POOL.a))
            else
                COL_POOL.a;
            const col = rl.Color{ .r = COL_POOL.r, .g = COL_POOL.g, .b = COL_POOL.b, .a = a };
            for (pool.blobs) |blob| {
                rl.drawCircle(
                    @intFromFloat(pool.x + blob.dx),
                    @intFromFloat(pool.y + blob.dy),
                    blob.r,
                    col,
                );
            }
        }
    }

    /// Draw splatter particles (call AFTER entities so they appear on top).
    pub fn drawParticles(self: *const BloodSystem) void {
        for (&self.particles) |*p| {
            if (!p.active) continue;
            const t   = @max(0.0, p.life / p.max_life);
            const a: u8 = @intFromFloat(t * @as(f32, COL_SPLATTER.a));
            const col = rl.Color{ .r = COL_SPLATTER.r, .g = COL_SPLATTER.g, .b = COL_SPLATTER.b, .a = a };
            const sz: i32 = @max(1, @as(i32, @intFromFloat(p.size * t + 1.0)));
            const x: i32  = @intFromFloat(p.x);
            const y: i32  = @intFromFloat(p.y);
            rl.drawRectangle(x - @divTrunc(sz, 2), y - @divTrunc(sz, 2), sz, sz, col);
        }
    }
};

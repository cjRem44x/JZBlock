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

// ============================================================================
// config.zig - Game Configuration and Constants
// ============================================================================
// Central configuration for all game settings including colors, speeds,
// damage values, and balance parameters. ALL tweakable values live here.
// ============================================================================

const rl = @import("raylib");

// ===========================
// WINDOW SETTINGS
// ===========================
pub const GAME_TITLE = "JZBlock";
pub const SCREEN_WIDTH: i32 = 1400;
pub const SCREEN_HEIGHT: i32 = 900;
pub const FPS: i32 = 60;

// ===========================
// ENTITY SIZES
// ===========================
pub const PLAYER_SIZE: f32 = 32.0;
pub const ZOMBIE_SIZE: f32 = 32.0;
pub const LAZAR_SIZE: f32 = 12.0;

// ===========================
// ENTITY SPEEDS (pixels per second)
// ===========================
pub const PLAYER_SPEED: f32 = 300.0;
pub const ZOMBIE_SPEED: f32 = 200.0;
pub const LAZAR_SPEED: f32 = 400.0;

// ===========================
// COMBAT BALANCE
// ===========================
pub const PLAYER_HEALTH: i32 = 100;
pub const ZOMBIE_HEALTH: i32 = 100;
pub const ZOMBIE_HEALTH_INC: i32 = 25; // HP increase per wave
pub const ZOMBIE_DAMAGE: i32 = 25;
pub const ZOMBIE_HIT_COOLDOWN: f32 = 1.0; // seconds
pub const LAZAR_DAMAGE: i32 = 25;

// ===========================
// AMMO & FIRE RATE
// ===========================
pub const LAZAR_AMMO: i32 = 8;
pub const RELOAD_TIME: f32 = 2.0; // seconds
pub const FIRE_COOLDOWN: f32 = 0.35; // seconds between shots (base)
pub const FIRE_COOLDOWN_MIN: f32 = 0.08; // minimum cooldown after upgrades
pub const FIRE_RATE_MULTIPLIER: f32 = 0.85; // cooldown multiplier per upgrade

// ===========================
// COUNTDOWN
// ===========================
pub const COUNTDOWN_SECONDS: i32 = 3;

// ===========================
// ECONOMY
// ===========================
pub const KILL_REWARD: i32 = 44;
pub const UPGRADE_COST: i32 = 400;

// ===========================
// WAVE SYSTEM
// ===========================
pub const MAX_ZOMBIES: i32 = 100;

// ===========================
// HEALING
// ===========================
pub const HEAL_AMOUNT: i32 = 25;
pub const HEAL_INTERVAL: f32 = 8.0; // seconds

// ===========================
// SPEED BOOST
// ===========================
pub const BOOST_MULTIPLIER: f32 = 2.0;
pub const BOOST_DURATION: f32 = 5.0; // seconds
pub const BOOST_RECHARGE: f32 = 5.0; // seconds

// =============================================================================
// COLORS - PLAYER
// =============================================================================
pub const PLAYER_COLOR = rl.Color{ .r = 137, .g = 0, .b = 255, .a = 255 }; // Purple
pub const PLAYER_HIGHLIGHT = rl.Color{ .r = 200, .g = 100, .b = 255, .a = 80 };
pub const PLAYER_SHADOW = rl.Color{ .r = 80, .g = 0, .b = 150, .a = 100 };
pub const PLAYER_GLOW = rl.Color{ .r = 137, .g = 0, .b = 255, .a = 40 };

// Player face
pub const PLAYER_EYE_WHITE = rl.Color{ .r = 255, .g = 255, .b = 255, .a = 255 };
pub const PLAYER_PUPIL = rl.Color{ .r = 50, .g = 50, .b = 80, .a = 255 };
pub const PLAYER_MOUTH = rl.Color{ .r = 255, .g = 255, .b = 255, .a = 255 };

// =============================================================================
// COLORS - ZOMBIE
// =============================================================================
pub const ZOMBIE_COLOR = rl.Color{ .r = 0, .g = 255, .b = 1, .a = 255 }; // Green
pub const ZOMBIE_DEAD_COLOR = rl.Color{ .r = 0, .g = 55, .b = 1, .a = 255 }; // Dark green
pub const ZOMBIE_GLOW = rl.Color{ .r = 0, .g = 255, .b = 1, .a = 30 };
pub const ZOMBIE_HIGHLIGHT = rl.Color{ .r = 100, .g = 255, .b = 100, .a = 60 };

// Zombie face
pub const ZOMBIE_EYE = rl.Color{ .r = 255, .g = 0, .b = 0, .a = 255 }; // Red angry eyes
pub const ZOMBIE_EYE_GLOW = rl.Color{ .r = 255, .g = 100, .b = 100, .a = 100 };
pub const ZOMBIE_MOUTH = rl.Color{ .r = 20, .g = 20, .b = 20, .a = 255 };
pub const ZOMBIE_EYEBROW = rl.Color{ .r = 30, .g = 80, .b = 30, .a = 255 };
pub const ZOMBIE_TEETH = rl.Color{ .r = 200, .g = 200, .b = 180, .a = 255 };
pub const ZOMBIE_DEAD_FACE = rl.Color{ .r = 80, .g = 80, .b = 80, .a = 255 };

// =============================================================================
// COLORS - LAZAR (PROJECTILE)
// =============================================================================
pub const LAZAR_COLOR = rl.Color{ .r = 255, .g = 0, .b = 159, .a = 255 }; // Pink
pub const LAZAR_SHADOW = rl.Color{ .r = 252, .g = 146, .b = 212, .a = 255 }; // Light pink
pub const LAZAR_BLOW_COLOR = rl.Color{ .r = 255, .g = 255, .b = 255, .a = 255 }; // White

// Neon glow layers (outer to inner)
pub const LAZAR_GLOW_OUTER = rl.Color{ .r = 255, .g = 0, .b = 255, .a = 15 };
pub const LAZAR_GLOW_MID = rl.Color{ .r = 255, .g = 50, .b = 255, .a = 30 };
pub const LAZAR_GLOW_INNER = rl.Color{ .r = 255, .g = 100, .b = 255, .a = 60 };

// Trail colors
pub const LAZAR_TRAIL_BRIGHT = rl.Color{ .r = 255, .g = 100, .b = 255, .a = 80 };
pub const LAZAR_TRAIL_FADE = rl.Color{ .r = 255, .g = 0, .b = 255, .a = 0 };

// Core colors
pub const LAZAR_CORE = rl.Color{ .r = 255, .g = 200, .b = 255, .a = 255 };
pub const LAZAR_CORE_BRIGHT = rl.Color{ .r = 255, .g = 255, .b = 255, .a = 255 };
pub const LAZAR_BORDER = rl.Color{ .r = 255, .g = 150, .b = 255, .a = 200 };

// =============================================================================
// COLORS - BACKGROUNDS
// =============================================================================
pub const BG_COLOR = rl.Color{ .r = 16, .g = 16, .b = 36, .a = 255 }; // Dark blue-gray
pub const GAME_OVER_BG = rl.Color{ .r = 38, .g = 10, .b = 0, .a = 255 }; // Dark red
pub const PAUSED_BG = rl.Color{ .r = 0, .g = 0, .b = 20, .a = 200 };
pub const MENU_BG = rl.Color{ .r = 0, .g = 0, .b = 0, .a = 255 }; // Black

// =============================================================================
// COLORS - UI TEXT
// =============================================================================
pub const HEALTH_COLOR = rl.Color{ .r = 255, .g = 100, .b = 100, .a = 255 }; // Light red
pub const GAME_OVER_COLOR = rl.Color{ .r = 255, .g = 0, .b = 0, .a = 255 }; // Red
pub const KILL_COLOR = rl.Color{ .r = 255, .g = 135, .b = 0, .a = 255 }; // Orange
pub const WAVE_COLOR = rl.Color{ .r = 255, .g = 0, .b = 0, .a = 255 }; // Red
pub const RESTART_COLOR = rl.Color{ .r = 255, .g = 0, .b = 0, .a = 255 }; // Red
pub const CURRENCY_COLOR = rl.Color{ .r = 255, .g = 0, .b = 240, .a = 255 }; // Magenta
pub const STATS_COLOR = rl.Color{ .r = 255, .g = 0, .b = 0, .a = 255 }; // Red
pub const PAUSE_COLOR = rl.Color{ .r = 255, .g = 0, .b = 0, .a = 255 }; // Red
pub const AMMO_COLOR = rl.Color{ .r = 200, .g = 255, .b = 200, .a = 255 }; // Light green
pub const KEYBINDS_COLOR = rl.Color{ .r = 255, .g = 233, .b = 0, .a = 255 }; // Yellow
pub const RELOAD_PROMPT_COLOR = rl.Color{ .r = 200, .g = 255, .b = 200, .a = 255 }; // Light green

// =============================================================================
// COLORS - UI EFFECTS
// =============================================================================
pub const SHADOW_COLOR = rl.Color{ .r = 0, .g = 0, .b = 0, .a = 150 };
pub const SHADOW_LIGHT = rl.Color{ .r = 0, .g = 0, .b = 0, .a = 100 };
pub const SHADOW_DARK = rl.Color{ .r = 0, .g = 0, .b = 0, .a = 200 };
pub const ENTITY_SHADOW = rl.Color{ .r = 0, .g = 0, .b = 0, .a = 80 };

// Overlays
pub const OVERLAY_DARK = rl.Color{ .r = 0, .g = 0, .b = 0, .a = 100 };
pub const OVERLAY_MENU = rl.Color{ .r = 0, .g = 0, .b = 0, .a = 120 };
pub const LOW_HEALTH_GLOW = rl.Color{ .r = 255, .g = 0, .b = 0, .a = 30 };
pub const GAME_OVER_FLICKER = rl.Color{ .r = 255, .g = 0, .b = 0, .a = 20 };
pub const GAME_OVER_GLOW = rl.Color{ .r = 255, .g = 50, .b = 50, .a = 100 };

// =============================================================================
// COLORS - COUNTDOWN & EFFECTS
// =============================================================================
pub const COUNTDOWN_GLOW = rl.Color{ .r = 255, .g = 255, .b = 100, .a = 100 };
pub const COUNTDOWN_GO_GLOW = rl.Color{ .r = 100, .g = 255, .b = 100, .a = 100 };
pub const LIGHT_RAY_COLOR = rl.Color{ .r = 255, .g = 255, .b = 200, .a = 40 };

// Ambient lighting
pub const AMBIENT_PURPLE = rl.Color{ .r = 137, .g = 0, .b = 255, .a = 25 };
pub const AMBIENT_GREEN = rl.Color{ .r = 0, .g = 255, .b = 100, .a = 25 };

// Grid
pub const GRID_COLOR = rl.Color{ .r = 30, .g = 30, .b = 50, .a = 50 };

// =============================================================================
// COLORS - MENU & BUTTONS
// =============================================================================
pub const MENU_BUTTON_COLOR = rl.Color{ .r = 40, .g = 40, .b = 60, .a = 200 };
pub const MENU_BUTTON_HOVER = rl.Color{ .r = 80, .g = 80, .b = 100, .a = 220 };
pub const MENU_BUTTON_GLOW = rl.Color{ .r = 137, .g = 0, .b = 255, .a = 100 };
pub const MENU_TEXT_COLOR = rl.Color{ .r = 255, .g = 255, .b = 255, .a = 255 }; // White
pub const MENU_VERSION_COLOR = rl.Color{ .r = 150, .g = 150, .b = 150, .a = 200 };
pub const MENU_TITLE_GLOW = rl.Color{ .r = 137, .g = 0, .b = 255, .a = 60 };

// =============================================================================
// COLORS - BOOST EFFECT
// =============================================================================
pub const BOOST_GLOW = rl.Color{ .r = 255, .g = 255, .b = 100, .a = 100 };

// =============================================================================
// COLORS - RELOAD PROMPT GLOW
// =============================================================================
pub const RELOAD_GLOW = rl.Color{ .r = 200, .g = 255, .b = 200, .a = 50 };

// =============================================================================
// UTILITY - Transparent black (for gradients)
// =============================================================================
pub const TRANSPARENT = rl.Color{ .r = 0, .g = 0, .b = 0, .a = 0 };

// =============================================================================
// BOSS TIERS
// =============================================================================
pub const BOSS_BASIC_START_WAVE: i32 = 8;
pub const BOSS_BASIC_MAX:        i32 = 5;
pub const BOSS_BASIC_HP_MUL:     i32 = 2;
pub const BOSS_BASIC_DMG_MUL:    f32 = 1.5;
pub const BOSS_BASIC_CASH_MUL:   i32 = 2;
pub const BOSS_BASIC_SIZE:       f32 = 80.0;
pub const BOSS_BASIC_SPEED:      f32 = 200.0;

pub const BOSS_MED_START_WAVE:   i32 = 15;
pub const BOSS_MED_MAX:          i32 = 3;
pub const BOSS_MED_HP_MUL:       i32 = 4;
pub const BOSS_MED_DMG_MUL:      f32 = 2.0;
pub const BOSS_MED_CASH_MUL:     i32 = 4;
pub const BOSS_MED_SIZE:         f32 = 100.0;
pub const BOSS_MED_SPEED:        f32 = 170.0;

pub const BOSS_HARD_START_WAVE:  i32 = 20;
pub const BOSS_HARD_MAX:         i32 = 2;
pub const BOSS_HARD_HP_MUL:      i32 = 8;
pub const BOSS_HARD_DMG_MUL:     f32 = 4.0;
pub const BOSS_HARD_CASH_MUL:    i32 = 8;
pub const BOSS_HARD_SIZE:        f32 = 120.0;
pub const BOSS_HARD_SPEED:       f32 = 140.0;

// Boss colors
pub const BOSS_BASIC_COLOR  = rl.Color{ .r = 30,  .g = 160, .b = 30,  .a = 255 }; // green
pub const BOSS_BASIC_DEAD   = rl.Color{ .r = 10,  .g = 50,  .b = 10,  .a = 255 };
pub const BOSS_BASIC_GLOW   = rl.Color{ .r = 40,  .g = 200, .b = 40,  .a = 45  };

pub const BOSS_MED_COLOR    = rl.Color{ .r = 110, .g = 0,   .b = 180, .a = 255 }; // purple
pub const BOSS_MED_DEAD     = rl.Color{ .r = 35,  .g = 0,   .b = 55,  .a = 255 };
pub const BOSS_MED_GLOW     = rl.Color{ .r = 150, .g = 0,   .b = 240, .a = 55  };

pub const BOSS_HARD_COLOR   = rl.Color{ .r = 8,   .g = 0,   .b = 0,   .a = 255 }; // near-black
pub const BOSS_HARD_DEAD    = rl.Color{ .r = 4,   .g = 0,   .b = 0,   .a = 255 };
pub const BOSS_HARD_GLOW    = rl.Color{ .r = 220, .g = 0,   .b = 0,   .a = 65  };
pub const BOSS_HARD_OUTLINE = rl.Color{ .r = 220, .g = 0,   .b = 0,   .a = 255 }; // red border

// Boss HP bar
pub const BOSS_HP_BG     = rl.Color{ .r = 15,  .g = 0,  .b = 0,  .a = 220 };
pub const BOSS_HP_FILL   = rl.Color{ .r = 255, .g = 55, .b = 0,  .a = 255 };
pub const BOSS_HP_BORDER = rl.Color{ .r = 90,  .g = 10, .b = 0,  .a = 200 };

// =============================================================================
// SCALE HELPERS (1920x1080 baseline)
// =============================================================================
pub fn pw(n: f32) i32 { return @intFromFloat(n * 1400.0 / 1920.0); }
pub fn ph(n: f32) i32 { return @intFromFloat(n * 900.0  / 1080.0); }

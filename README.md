# JZBlock

**A personal zombie survival game composed of blocks**

## 🎮 Overview

JZBlock is a top-down zombie survival game where players fight off waves of zombies using laser weapons. Version 0.5 represents a complete architectural rewrite, now featuring **dual language implementations** in both Java and Zig, giving developers the choice between JVM portability and native performance.

## ✨ Features

### Gameplay (Zig Version - Fully Playable)
- 🔫 **Laser Combat System** - Neon projectiles with glow effects and fire rate upgrades
- 🧟 **Wave-Based Survival** - Increasingly difficult zombie hordes (health scales per wave)
- 🎯 **Top-Down Tactical Gameplay** - Strategic positioning matters
- 💊 **Health & Ammo Management** - Magazine reloading, periodic healing drops
- 🎮 **Game States** - Main menu, settings, in-game, paused, and game over
- 💰 **Economy & Upgrades** - Earn currency from kills, upgrade fire rate and damage
- ⚡ **Speed Boost** - Rechargeable speed boost system
- 🎨 **Visual Effects** - Entity shadows, ambient lighting, grid backgrounds, glow effects

### Technical Architecture
- ⚡ **Dual Language Support** - Choose between Zig (performance) or Java (portability)
- 🎨 **Raylib Graphics** (Zig) - Modern, lightweight graphics framework
- 🖼️ **Swing/AWT** (Java) - Classic cross-platform windowing
- 🧩 **Modular Design** - Clean separation of concerns
- 🔄 **Cross-Platform** - Windows, Linux, and more

## 🏗️ Architecture

### Version 0.5

The v0.5 release is a **complete rewrite** with:
- **Zig + Raylib** as the primary, fully playable implementation
- **Java + Swing** as a secondary implementation (currently a stub, in development)
- Clean, modular architecture with centralized configuration
- Archived original Java implementation preserved at `src/main/archived/`

### Implementation Comparison

| Feature | Zig Implementation | Java Implementation |
|---------|-------------------|---------------------|
| **Status** | Fully playable | Stub (in development) |
| **Graphics** | Raylib | Swing/AWT (planned) |
| **Performance** | Native, zero-cost abstractions | JVM runtime |
| **Memory** | Manual management | Automatic GC |
| **Platform** | Compiled binaries | Cross-platform JVM |
| **Build** | `build.zig` | Standard `javac` |

## 🚀 Getting Started

### Prerequisites

**For Java Build:**
- Java Development Kit (JDK) 8 or later
- Any Java-compatible platform (Windows, Linux, macOS)

**For Zig Build:**
- [Zig Compiler](https://ziglang.org/download/) (latest version)
- Raylib (automatically managed by build.zig)

### Building the Project

#### Windows

```bash
# Navigate to scripts directory
cd scripts

# Run the Windows build script (interactive menu)
winbuild.bat
```

The script will prompt you to choose:
1. **Java** - Build using Java/Swing
2. **Zig** - Build using Zig/Raylib

#### Linux

```bash
# Navigate to scripts directory
cd scripts

# Make script executable (first time only)
chmod +x lxbuild.sh

# Run the Linux build script (interactive menu)
./lxbuild.sh
```

The script will prompt you to choose:
1. **Java** - Build using Java/Swing
2. **Zig** - Build using Zig/Raylib

### Running the Archived Version

To run the original complete game (from archived source):

```bash
cd scripts/archived

# Windows
winbuild.bat

# Linux
chmod +x lxbuild.sh
./lxbuild.sh
```

## 🎯 Development Roadmap

### Version 0.5 (Current)

**Zig Implementation (Fully Playable):**
- [x] Set up dual-language architecture
- [x] Implement Zig build system with Raylib
- [x] Establish build scripts for both implementations
- [x] Core game loop with state management
- [x] Entity system (Player, Zombie, Lazar)
- [x] Raylib rendering pipeline with visual effects
- [x] Input handling (WASD movement, arrow key shooting)
- [x] Player mechanics with speed boost system
- [x] Zombie AI with chase behavior and separation
- [x] Laser weapon system with ammo and reloading
- [x] Wave management with scaling difficulty
- [x] UI and HUD (health, ammo, kills, wave, currency, upgrades)
- [x] Menu system (main menu, settings, pause)
- [x] Economy system (kill rewards, weapon upgrades)
- [x] Countdown system between waves

**Java Implementation (Stub):**
- [x] Create modular Java foundation
- [x] OS detection utilities
- [x] Logging utility
- [ ] Port game loop from Zig
- [ ] Implement entity system
- [ ] Add Swing/AWT rendering pipeline

### Version 0.6 (Planned)
- [ ] Sound effects and music
- [ ] Settings screen implementation (currently placeholder)
- [ ] Java implementation feature parity with Zig

### Version 1.0 (Future)
- [ ] Additional weapons and enemies
- [ ] Level system
- [ ] Save/load functionality
- [ ] Performance optimizations

## 🛠️ Technology Stack

### Zig Implementation
- **Language:** [Zig](https://ziglang.org/)
- **Graphics:** [Raylib](https://www.raylib.com/)
- **Build System:** Zig build system
- **Focus:** Performance, native execution

### Java Implementation
- **Language:** Java 8+
- **Graphics:** Swing/AWT
- **Build System:** Standard Java compilation
- **Focus:** Cross-platform compatibility, ease of development

### Archived Version (Original)
- **Language:** Pure Java
- **Graphics:** Custom Swing/AWT rendering
- **Features:** Complete game with all mechanics

## 📚 Code Examples

### Running the Java Version

```java
// Main entry point - src/main/java/Main.java
public class Main {
    public static void main(String[] args) {
        //...
    }
}
```

### Running the Zig Version

```zig
// Main entry point - src/main/zig/src/main.zig
pub fn main() !void {
    //...
}
```

## 🤝 Contributing

This is a personal project, but feedback and suggestions are welcome! If you'd like to contribute:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📜 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## 🎮 Game Controls (Zig Version)

| Action | Key |
|--------|-----|
| Move Up | W |
| Move Down | S |
| Move Left | A |
| Move Right | D |
| Shoot Up | Up Arrow |
| Shoot Down | Down Arrow |
| Shoot Left | Left Arrow |
| Shoot Right | Right Arrow |
| Reload | CTRL |
| Upgrade Weapon | U |
| Speed Boost | SPACE |
| Pause / Resume | ESC |
| Restart (Game Over) | R |

## 🐛 Known Issues

- Java v0.5 implementation is a stub — run the Zig version for the full game experience
- Settings screen is a placeholder (controls display only)
- Archived version may have platform-specific rendering quirks

## 📧 Contact

**Developer:** cjRem44x  
**Repository:** [github.com/cjRem44x/JZBlock](https://github.com/cjRem44x/JZBlock)

## 🙏 Acknowledgments

- [Raylib](https://www.raylib.com/) - Amazing graphics library for Zig
- [Zig Programming Language](https://ziglang.org/) - Modern systems programming
- Java Community - For the robust JVM ecosystem

---

**Status:** 🎮 Zig version playable | Java version in development (v0.5)
**Language:** Zig (primary) & Java (in progress)
**Platform:** Cross-platform (Windows, Linux)
**Graphics:** Raylib (Zig) | Swing/AWT (Java — planned)

**Enjoy playing JZBlock!** 🎮🧟‍♂️⚡

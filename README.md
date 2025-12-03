# JZBlock

**A personal zombie survival game composed of blocks**

## 🎮 Overview

JZBlock is a top-down zombie survival game where players fight off waves of zombies using laser weapons. Version 0.5 represents a complete architectural rewrite, now featuring **dual language implementations** in both Java and Zig, giving developers the choice between JVM portability and native performance.

## ✨ Features

### Gameplay (Archived Version)
- 🔫 **Laser Combat System** - High-tech weaponry with visual effects
- 🧟 **Wave-Based Survival** - Increasingly difficult zombie hordes
- 🎯 **Top-Down Tactical Gameplay** - Strategic positioning matters
- 💊 **Health & Ammo Management** - Resource management mechanics
- 🎮 **Game States** - Menu, playing, game over, and paused states
- ⚙️ **Configurable Controls** - Customizable graphics and control settings

### Technical Architecture
- ⚡ **Dual Language Support** - Choose between Zig (performance) or Java (portability)
- 🎨 **Raylib Graphics** (Zig) - Modern, lightweight graphics framework
- 🖼️ **Swing/AWT** (Java) - Classic cross-platform windowing
- 🧩 **Modular Design** - Clean separation of concerns
- 🔄 **Cross-Platform** - Windows, Linux, and more

## 🏗️ Architecture

### Version 0.5 Rewrite

The current v0.5 branch is a **complete rewrite** focusing on:
- Building dual Zig and Java implementations side-by-side
- Establishing a clean, modular foundation
- Integrating Raylib for modern graphics (Zig)
- Maintaining backward compatibility with Java

### Implementation Comparison

| Feature | Zig Implementation | Java Implementation |
|---------|-------------------|---------------------|
| **Graphics** | Raylib | Swing/AWT |
| **Performance** | Native, zero-cost abstractions | JVM runtime |
| **Memory** | Manual management | Automatic GC |
| **Platform** | Compiled binaries | Cross-platform JVM |
| **Build** | `build.zig` | Standard Java build |

## 📁 Project Structure

```
```
```
JZBlock/
|
+---bin
+---build
|   \---archived
|           JZBlock.jar
|
+---docs
|       index.html
|
+---res
|   +---gimp
|   |       res.xcf
|   |
|   \---img
|           icon.png
|           main_menu_img.png
|
+---scripts
|   |   lxbuild.sh
|   |   winbuild.bat
|   |
|   \---archived
|           jar.bat
|           lxbuild.sh
|           winbuild.bat
|
\---src
    +---main
    |   +---archived
    |   +---java
    |   |   |   Main.java
    |   |   |
    |   |   +---launcher
    |   |   |       GameLauncher.java
    |   |   |
    |   |   \---util
    |   |           LocalSys.java
    |   |           Logging.java
    |   |
    |   \---zig
    |       |   build.zig
    |       |   build.zig.zon
    |       |
    |       +---.zig-cache
    |       +---src
    |       |       main.zig
    |       |       player.zig
    |       |       root.zig
    |       |       util.zig
    |       |
    |       \---zig-out
    |           \---bin
    |                   zig.exe
    |                   zig.pdb
    |
    \---test
        \---java
                MainTest.java

```

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

### Version 0.5 (Current - In Progress)
- [x] Set up dual-language architecture
- [x] Implement Zig build system with Raylib
- [x] Create modular Java foundation
- [x] Establish build scripts for both implementations
- [ ] Port core game loop to both languages
- [ ] Implement entity system in both languages
- [ ] Add rendering pipeline (Raylib for Zig, Swing for Java)
- [ ] Integrate input handling
- [ ] Re-implement player mechanics

### Version 0.6 (Planned)
- [ ] Zombie AI and spawning
- [ ] Laser weapon system
- [ ] Wave management
- [ ] UI and HUD
- [ ] Sound effects and music

### Version 1.0 (Future)
- [ ] Complete feature parity with archived version
- [ ] Performance optimizations
- [ ] Additional weapons and enemies
- [ ] Level system
- [ ] Save/load functionality

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
        Window window = new Window();
        // Game initialization
    }
}
```

### Running the Zig Version

```zig
// Main entry point - src/main/zig/src/main.zig
const std = @import("std");
const rl = @import("raylib");

pub fn main() !void {
    rl.InitWindow(800, 600, "JZBlock");
    defer rl.CloseWindow();
    
    // Game loop
    while (!rl.WindowShouldClose()) {
        // Update and render
    }
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

## 🎮 Game Controls (Archived Version)

| Action | Key |
|--------|-----|
| Move Up | W |
| Move Down | S |
| Move Left | A |
| Move Right | D |
| Shoot | Left Mouse Button |
| Pause | ESC |

## 🐛 Known Issues

- v0.5 is currently in active development - game loop not yet implemented
- Archived version may have platform-specific rendering quirks
- Zig implementation requires manual Raylib setup on some platforms

## 📧 Contact

**Developer:** cjRem44x  
**Repository:** [github.com/cjRem44x/JZBlock](https://github.com/cjRem44x/JZBlock)

## 🙏 Acknowledgments

- [Raylib](https://www.raylib.com/) - Amazing graphics library for Zig
- [Zig Programming Language](https://ziglang.org/) - Modern systems programming
- Java Community - For the robust JVM ecosystem

---

**Status:** 🚧 In Development (v0.5 Rewrite)  
**Language:** Java & Zig  
**Platform:** Cross-platform (Windows, Linux, macOS)  
**Graphics:** Raylib (Zig) | Swing/AWT (Java)

**Enjoy playing JZBlock!** 🎮🧟‍♂️⚡

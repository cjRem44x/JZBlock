# JZBlock ϫⲍⲃⲗⲟⲕ

A Java-based zombie survival game featuring laser combat and wave-based gameplay.

## Overview

JZBlock is a top-down zombie survival game where players fight off waves of zombies using laser weapons. The game is undergoing a major rewrite for version 0.5, with the original game code archived while core systems are being rebuilt.

## Current Status (v0.5)

This version focuses on rebuilding the core infrastructure:
- Cross-platform window management using Swing/AWT
- System detection and utilities
- Basic 2D entity framework for game objects
- Foundation for the rebuilt zombie survival game

## Project Structure

### Active Development
- **Main Entry Point**: `src/main/java/Main.java` - Application entry point
- **Window Management**: `src/main/java/Window.java` - Cross-platform window creation and management
- **System Utilities**: `src/main/java/LocalSys.java` - OS detection and system information
- **Game Framework**: `src/main/java/K2DBody.java` - Basic 2D entity/body class

### Archived Code
The original zombie survival game implementation (featuring player movement, laser combat, zombie AI, wave system, and game states) has been moved to `src/main/archived/` for reference while the codebase is being restructured.

## Build and Run

### Prerequisites
- Java Development Kit (JDK) 8 or later
- Apache License 2.0 compliance

### Building
Use the provided build scripts:
```bash
# Windows
build.bat

# Create JAR package
jar.bat
```

## Development

### Architecture
The v0.5 rewrite emphasizes:
- **Modularity**: Clean separation of concerns for game systems
- **Cross-platform compatibility**: Works on Windows, Linux, and other Java-supported platforms
- **Rebuilt Foundation**: Core systems for the zombie survival game
- **Simplicity**: Minimal dependencies, focusing on core Java libraries

### Key Features (v0.5)
- **Window.o**: Singleton window manager with configurable dimensions and properties
- **LocalSys.o**: System utilities including OS detection and directory management  
- **K2DBody**: Foundation class for 2D game entities (players, zombies, projectiles)

### Original Game Features (Archived)
- Top-down zombie survival gameplay
- Laser weapon combat system
- Wave-based zombie spawning
- Player health and ammunition management
- Game states (menu, playing, game over, paused)
- Configurable controls and graphics settings

## License

This project is licensed under the Apache License 2.0. See the `LICENSE` file for details.

Enjoy playing JZBlock!
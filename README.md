# JZBlock

A cross-platform Java-based game development framework.

## Overview

JZBlock is undergoing a major rewrite for version 0.5. The project has been restructured to provide a clean, modular foundation for Java-based game development with cross-platform window management and system utilities.

## Current Status (v0.5)

This version focuses on establishing core infrastructure:
- Cross-platform window management using Swing/AWT
- System detection and utilities
- Basic 2D body/entity framework
- Modular architecture for future game development

## Project Structure

### Active Development
- **Main Entry Point**: `src/main/java/Main.java` - Application entry point
- **Window Management**: `src/main/java/Window.java` - Cross-platform window creation and management
- **System Utilities**: `src/main/java/LocalSys.java` - OS detection and system information
- **Game Framework**: `src/main/java/K2DBody.java` - Basic 2D entity/body class

### Archived Code
The previous zombie survival game implementation has been moved to `src/main/archived/` for reference while the codebase is being restructured.

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
- **Modularity**: Clean separation of concerns
- **Cross-platform compatibility**: Works on Windows, Linux, and other Java-supported platforms
- **Extensibility**: Foundation for future game development features
- **Simplicity**: Minimal dependencies, focusing on core Java libraries

### Key Features
- **Window.o**: Singleton window manager with configurable dimensions and properties
- **LocalSys.o**: System utilities including OS detection and directory management
- **K2DBody**: Foundation class for 2D game entities with position, velocity, and rendering properties

## License

This project is licensed under the Apache License 2.0. See the `LICENSE` file for details.

Enjoy playing and developing JZBlock!
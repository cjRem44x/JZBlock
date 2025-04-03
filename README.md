# JZBlock

A personal zombie survival game composed of blocks.

## Overview

JZBlock is a 2D zombie survival game where the player must fend off waves of zombies using a blaster. The game features a simple block-based design, with mechanics such as health management, weapon upgrades, and zombie AI. The goal is to survive as long as possible while accumulating kills and upgrading your weapon.

## Gameplay

### Objective
- Survive waves of zombies.
- Kill zombies to earn points (`ZZZ`) and upgrade your weapon.
- Avoid getting hit by zombies to maintain your health.

### Controls
- **Movement**: Use `[W][A][S][D]` to move the player.
- **Shooting**: Use the arrow keys (`↑`, `↓`, `←`, `→`) to shoot in the respective direction.
- **Reload**: Press `[CTRL]` to reload your blaster.
- **Upgrade Weapon**: Press `[U]` to upgrade your blaster (requires sufficient `ZZZ` points).
- **Speed Boost**: Hold `[SPACE]` to temporarily double your movement speed.
- **Pause/Resume**: Press `[ESC]` to pause or resume the game.
- **Restart**: Press `[R]` to restart the game after dying.

### Game Features
- **Health System**: The player starts with 100 health points. Health decreases when hit by zombies and regenerates over time.
- **Blaster**: The player's weapon has limited ammo and requires reloading. Upgrades increase its power and ammo capacity.
- **Zombie AI**: Zombies chase the player and avoid bunching up. Their health and speed increase with each wave.
- **Wave System**: Zombies spawn in waves, with each wave introducing more zombies and tougher challenges.
- **Game Over**: The game ends when the player's health reaches zero.

## Developer Notes

### Project Structure
- **Core Game Logic**: Located in `src/java/core/Engine.java`.
- **Graphics and Rendering**: Managed by `src/java/graphics/Render.java` and `src/java/graphics/Window.java`.
- **User Interface**: Handled by `src/java/ui/UI.java`.
- **Input Handling**: Implemented in `src/java/input/Input.java`.
- **Game Entities**: Defined in `src/java/dat/Player.java`, `src/java/dat/Zomb.java`, and `src/java/dat/Lazar.java`.
- **Settings**: Configurable options are stored in `src/java/opt/Settings.java`.

### Build and Run
1. Use `build.bat` to compile and run the game:
   ```
   build.bat
   ```
2. Use `jar.bat` to package the game into a `.jar` file:
   ```
   jar.bat
   ```

### Customization
- Modify `Settings.java` to adjust game parameters such as screen size, FPS, and colors.
- Extend the `Engine` class to add new features or mechanics.
- Update the `UI` class to enhance the user interface.

### Future Enhancements
- Add new enemy types with unique behaviors.
- Introduce power-ups and special abilities.
- Implement a scoring system with leaderboards.
- Create a separate `Theme.java` file for customizable color themes.

Enjoy playing and developing JZBlock!
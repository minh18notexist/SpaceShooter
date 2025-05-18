# 🚀 SpaceShooter

A classic 2D space shooting game developed in **Kotlin** using **Android Studio**. Blast enemy ships, dodge attacks, and climb the score ladder!

## 🎮 Features

- Player spaceship with upgrade levels and triple-shot ability
- Enemy spaceship with difficulty scaling
- Explosions and sound effects for immersive gameplay
- Pause and resume functionality
- High score saving using SharedPreferences
- Clean and modular code

## 📸 Screenshots

*(Add your gameplay or UI screenshots here)*  
Example:
![image](https://github.com/user-attachments/assets/15056089-e73e-4e32-82d3-c30c4ff79a40)


## 🔧 How to Run

1. **Clone this repository**:
   ```bash
   git clone https://github.com/yourusername/SpaceShooter.git
   ```

2. **Open in Android Studio**:
   - File > Open > Select the cloned folder

3. **Build & Run**:
   - Use an emulator or a physical Android device
   - Min SDK version: (your project’s min SDK, e.g., 21)

## 🗂️ Project Structure

| File | Description |
|------|-------------|
| `MainActivity.kt` | Launches the game screen |
| `StartUp.kt` | Displays the start menu and high score |
| `SpaceShooter.kt` | Core game logic, drawing, input handling |
| `OurSpaceShip.kt` | Player's spaceship logic |
| `EnemySpaceShip.kt` | Enemy ship logic and upgrade system |
| `Shot.kt` | Handles laser shots |
| `Explosion.kt` | Handles explosion animation |
| `GameOver.kt` | Displays game over screen and final score |

## 🥇 Game Mechanics

- Collect points by shooting enemies
- Your ship upgrades at 50 and 100 points
- Enemies become harder at 55 and 110 points
- Lose the game when you run out of lives

## 📁 Assets Used

- All assets are stored in the `res/drawable` and `res/raw` folders
- Sounds: `laser_shoot.mp3`, `explosion_sound.mp3`, etc.
- Images: spaceship sprites, explosion frames, etc.

> All assets are for demo/testing purposes. Replace with your own or free-to-use assets before publishing to Play Store.

## 📜 License

This project is for learning and educational purposes. Feel free to use and modify it.

## 🙌 Acknowledgements

- Developed using [Android Studio](https://developer.android.com/studio)
- Inspired by classic arcade space shooter games

## 📬 Contact

For feedback or questions, feel free to open an issue or reach out on GitHub!

<div align="center">
  
# Raven XD
Raven B4, but for those who can't afford it.

Raven B4, but not only for Hypixel.

![2024-08-13_10 14 36](https://github.com/user-attachments/assets/36e8920e-336e-4b62-ae3f-6b9962920812)
![Screenshot 2024-08-13 084510](https://github.com/user-attachments/assets/85637f18-9d6b-47d9-9dc2-cb8594aff585)
</div>

## How do I install this client?

### Steps:

1. **Download the latest .jar file from the releases**:
   - Download from [https://github.com/XeContrast/Raven-XD-Reborn/releases](https://github.com/XeContrast/Raven-XD-Reborn/releases)
   - This client requires Forge 1.8.9.

2. **Put in mods folder of your .minecraft directory**:
   - If you don't already have this, please install Forge 1.8.9 by clicking installer, and clicking skip after 5 seconds (https://files.minecraftforge.net/net/minecraftforge/forge/index_1.8.9.html).
   - Once you've done that, open file explorer, and in the search bar type %Appdata%. Once you're there navigate to .minecraft, then finally the folder called "mods".
   - After you're there, all you have to do is drag the file that you just downloaded there.

3. **Launch Minecraft**:
   - Once your on this step, you are pretty much done. When it's done loading, if you have done it properly, it should have a background with a train and the string "Raven XD" in big white letters.
   - For more help, join the discord server and ask in #public-support.

## How to set up a Workspace

### Steps:


1. **Clone the repository**
2. **Run `./gradlew --debug setupDevWorkspace idea genIntellijRuns build`**
3. **Add runConfigurations by yourself**
4. **Run the client.**

## How do I build this client?

### Steps:

1. **Download and Install Java SDK version 8**:
   - Download from [Java SDK version 8](https://adoptium.net/en-GB/temurin/releases/?version=8).
   - Install it and set the `JAVA_HOME` environment variable to the installation directory.

2. **Verify Java Installation**:
   - Open a terminal or command prompt.
   - Run `java -version`.
   - Ensure the output contains `openjdk version "1.8.0_{...}"`.

3. **Install Gradle 4.7**:
   - Download from [Gradle 4.7](https://gradle.org/next-steps/?version=4.7&format=bin).
   - Follow the "Installing manually" guide on the [Gradle wiki](https://gradle.org/install).

4. **Clone the Repository**:
   - Run the following commands in the terminal or command prompt:
     ```bash
     git clone --recursive https://github.com/XeContrast/Raven-XD-Reborn.git
     cd Raven-XD
     ```

5. **Set Up the Project**:
   - Run the following commands to set up the Gradle wrapper and the development environment:
     ```bash
     gradlew.bat
     gradle wrapper
     gradlew build
     ```

6. **Build Success**:
   - If all steps complete without errors, the project is successfully built.


## Contribute

I don't need any

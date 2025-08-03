# MazeGenerator

A simple Java application that generates and visually solves a maze using a Swing GUI dialog.

## Features

- Maze generation using depth-first search (DFS)
- Animated maze solving using breadth-first search (BFS)
- Visual display of the maze and solution path in a dialog window
- Customizable maze size

## How to Run

1. **Compile:**
   ```sh
   javac -cp . src/Maze.java
   ```
2. **Run:**
   ```sh
   java -cp src Maze
   ```
   This will open a dialog window and animate the maze solution.

## Customization

- Change the maze size by editing the `main` method in `Maze.java`:
  ```java
  Maze maze = new Maze(15, 15); // Change 15, 15 to desired size
  ```
- Adjust animation speed by changing the sleep time in the animation thread.

## Requirements

- Java 8 or higher
- No external dependencies

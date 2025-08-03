
public class Main {

    public static void main(String[] args) {
        Maze maze = new Maze(10, 10); // Size of the maze
        maze.generate();
        maze.display(); // Display initial maze
        maze.solveAnimated(); // Animate solving
        System.out.println("Maze solved!");
        try {
            Thread.sleep(1000); // Pause for drama
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

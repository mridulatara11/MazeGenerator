// Maze.java

import java.awt.*;
import java.util.*;
import javax.swing.*;

public class Maze {

    private final int rows, cols;
    public final Cell[][] grid;
    private final Random rand = new Random();

    public Maze(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new Cell[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = new Cell(i, j);
            }
        }
    }

    public void generate() {
        Stack<Cell> stack = new Stack<>();
        Cell start = grid[0][0];
        start.visited = true;
        stack.push(start);

        while (!stack.isEmpty()) {
            Cell current = stack.peek();
            Cell next = getRandomUnvisitedNeighbor(current);

            if (next != null) {
                removeWall(current, next);
                next.visited = true;
                stack.push(next);
            } else {
                stack.pop();
            }
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j].visited = false;
            }
        }
    }

    private Cell getRandomUnvisitedNeighbor(Cell cell) {
        java.util.List<Cell> neighbors = new java.util.ArrayList<>();
        int r = cell.row;
        int c = cell.col;

        if (r > 0 && !grid[r - 1][c].visited) {
            neighbors.add(grid[r - 1][c]);
        }
        if (r < rows - 1 && !grid[r + 1][c].visited) {
            neighbors.add(grid[r + 1][c]);
        }
        if (c > 0 && !grid[r][c - 1].visited) {
            neighbors.add(grid[r][c - 1]);
        }
        if (c < cols - 1 && !grid[r][c + 1].visited) {
            neighbors.add(grid[r][c + 1]);
        }

        if (neighbors.isEmpty()) {
            return null;
        }
        return neighbors.get(rand.nextInt(neighbors.size()));
    }

    private void removeWall(Cell a, Cell b) {
        int dx = b.col - a.col;
        int dy = b.row - a.row;

        if (dx == 1) {
            a.walls[1] = false;
            b.walls[3] = false;
        } else if (dx == -1) {
            a.walls[3] = false;
            b.walls[1] = false;
        } else if (dy == 1) {
            a.walls[2] = false;
            b.walls[0] = false;
        } else if (dy == -1) {
            a.walls[0] = false;
            b.walls[2] = false;
        }
    }

    // Animated solver with callback for GUI
    public void solveAnimated(Runnable onStep) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j].visited = false;
                grid[i][j].inPath = false;
            }
        }

        Cell start = grid[0][0];
        Cell end = grid[rows - 1][cols - 1];

        Queue<Cell> queue = new LinkedList<>();
        Map<String, Cell> parent = new HashMap<>();

        start.visited = true;
        queue.add(start);
        parent.put(coord(start), null);

        while (!queue.isEmpty()) {
            Cell current = queue.poll();

            current.inPath = true;
            onStep.run();
            current.inPath = false;

            if (current == end) {
                break;
            }

            for (Cell neighbor : getAccessibleNeighbors(current)) {
                if (!neighbor.visited) {
                    neighbor.visited = true;
                    parent.put(coord(neighbor), current);
                    queue.add(neighbor);
                }
            }
        }

        Cell current = end;
        while (current != null) {
            current.inPath = true;
            onStep.run();
            Cell next = parent.get(coord(current));
            if (next == null) {
                break;
            }
            current = next;
        }
    }

    private String coord(Cell cell) {
        return cell.row + "," + cell.col;
    }

    private java.util.List<Cell> getAccessibleNeighbors(Cell cell) {
        java.util.List<Cell> neighbors = new java.util.ArrayList<>();
        int r = cell.row;
        int c = cell.col;

        if (r > 0 && !cell.walls[0]) {
            neighbors.add(grid[r - 1][c]);
        }
        if (r < rows - 1 && !cell.walls[2]) {
            neighbors.add(grid[r + 1][c]);
        }
        if (c > 0 && !cell.walls[3]) {
            neighbors.add(grid[r][c - 1]);
        }
        if (c < cols - 1 && !cell.walls[1]) {
            neighbors.add(grid[r][c + 1]);
        }

        return neighbors;
    }

    public void display() {
        for (int j = 0; j < cols; j++) {
            System.out.print("+---");
        }
        System.out.println("+");

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(grid[i][j].walls[3] ? "|" : " ");
                System.out.print(grid[i][j].inPath ? " * " : "   ");
            }
            System.out.println("|");

            for (int j = 0; j < cols; j++) {
                System.out.print("+");
                System.out.print(grid[i][j].walls[2] ? "---" : "   ");
            }
            System.out.println("+");
        }
    }

    public static class Cell {

        public int row, col;
        public boolean[] walls = {true, true, true, true}; // top, right, bottom, left
        public boolean visited = false;
        public boolean inPath = false;

        Cell(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Cell other = (Cell) obj;
            return row == other.row && col == other.col;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, col);
        }
    }

    // MazePanel for drawing the maze
    public static class MazePanel extends JPanel {

        private final Maze maze;
        private final int cellSize = 30;

        public MazePanel(Maze maze) {
            this.maze = maze;
            setPreferredSize(new Dimension(maze.cols * cellSize + 2, maze.rows * cellSize + 2));
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            for (int i = 0; i < maze.rows; i++) {
                for (int j = 0; j < maze.cols; j++) {
                    int x = j * cellSize;
                    int y = i * cellSize;
                    Cell cell = maze.grid[i][j];
                    if (cell.inPath) {
                        g.setColor(Color.GREEN);
                        g.fillRect(x, y, cellSize, cellSize);
                    } else if (cell.visited) {
                        g.setColor(new Color(220, 220, 220));
                        g.fillRect(x, y, cellSize, cellSize);
                    } else {
                        g.setColor(Color.WHITE);
                        g.fillRect(x, y, cellSize, cellSize);
                    }
                    g.setColor(Color.BLACK);
                    if (cell.walls[0]) {
                        g.drawLine(x, y, x + cellSize, y);
                    }
                    if (cell.walls[1]) {
                        g.drawLine(x + cellSize, y, x + cellSize, y + cellSize);
                    }
                    if (cell.walls[2]) {
                        g.drawLine(x, y + cellSize, x + cellSize, y + cellSize);
                    }
                    if (cell.walls[3]) {
                        g.drawLine(x, y, x, y + cellSize);
                    }
                }
            }
        }
    }

    // Main method to show dialog and animate
    public static void main(String[] args) {
        Maze maze = new Maze(15, 15);
        maze.generate();
        MazePanel panel = new MazePanel(maze);
        JDialog dialog = new JDialog((Frame) null, "Maze Solver", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.getContentPane().add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        new Thread(() -> {
            maze.solveAnimated(() -> {
                panel.repaint();
                try {
                    Thread.sleep(20);
                } catch (InterruptedException ignored) {
                }
            });
        }).start();
        dialog.setVisible(true);
    }
}

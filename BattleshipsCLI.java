import java.io.IOException;
import java.util.Scanner;

public class BattleshipsCLI {
    private static GameModel model;
    private static Scanner scanner;

    public BattleshipsCLI(GameModel model) {
        this.model = new GameModel();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("Press 1 to place ships onto the game board from a file");
        System.out.println("Press 2 to place ships randomly");
        String userInput = scanner.nextLine().trim();
        if (userInput.equals("1")) {
            loadShipsFromaFile();
        } else if (userInput.equals("2")) {
            model = new GameModel();
            System.out.println("Ships have been placed randomly");
        } else {
            System.out.println("Invalid input");
            return;
        }
            System.out.println("enter coordinates to fire the first shot");
            boolean gameRunning = true;
            while (gameRunning) {
                displayBoard();
                System.out.print("Enter coordinates: ");
                String input = scanner.nextLine().trim().toUpperCase();
                try {
                    int row = input.charAt(0) - 'A';
                    int col = Integer.parseInt(input.substring(1)) - 1;

                    if (row < 0 || row >= GameModel.BOARD_SIZE || col < 0 || col >= GameModel.BOARD_SIZE) {
                        System.out.println("The corodinates you entered are invalid.");
                        continue;
                    }

                    boolean hit = model.fireShot(row, col);

                    if (hit) {
                        System.out.println("A ship has been hit");
                    } else {
                        System.out.println("Missed");
                    }

                    if (model.isGameOver()) {
                        displayBoard();
                        System.out.println("Game Over! You sank all ships in " + model.getShotsFired() + " shots.");
                        gameRunning = false;
                    }
                } catch (Exception e) {
                    System.out.println("The corodinates you entered are invalid.");
                }
            }
        }


    private void loadShipsFromaFile() {
        System.out.println("Enter the file path to the ships file");
        String filePath = scanner.nextLine().trim();

        try {
            model.loadShipsfromaFile(filePath);
            System.out.println("Ships from a file loaded");
        } catch (IOException e) {
            System.out.println("The ships file could not be loaded");
        }
    }

    private static void displayBoard() {
        System.out.print("  ");
        for (int i = 0; i < GameModel.BOARD_SIZE; i++) {
            System.out.print(" " + (i + 1) + " ");
        }
        System.out.println();

        for (int i = 0; i < GameModel.BOARD_SIZE; i++) {
            System.out.print((char) ('A' + i) + " ");

            for (int j = 0; j < GameModel.BOARD_SIZE; j++) {
                GameModel.CellStates state = model.getCellState(i, j);

                switch (state) {
                    case HIT:
                        System.out.print(" H ");
                        break;
                    case MISS:
                        System.out.print(" M ");
                        break;
                    default:
                        System.out.print(" . ");
                }
            }
            System.out.println();
        }
    }
}
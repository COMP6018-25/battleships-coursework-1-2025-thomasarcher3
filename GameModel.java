import java.util.*;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;


public class GameModel extends Observable {
    public static final int BOARD_SIZE = 10;
    public static final int Carrier_Size = 5;
    public static final int BattleShip_Size = 4;
    public static final int Cruiser_Size = 3;
    public static final int Destroyer_Size = 2;

    public enum CellStates {
        EMPTY,
        SHIP,
        HIT,
        MISS
    }

    public enum ShipType {
        CARRIER(Carrier_Size, 1),
        BATTLESHIP(BattleShip_Size, 1),
        CRUISER(Cruiser_Size, 1),
        DESTROYER(Destroyer_Size, 2);

        private final int size;
        private final int count;

        ShipType(int size, int count) {
            this.size = size;
            this.count = count;
        }

        public int getSize() {
            return size;
        }

        public int getCount() {
            return count;
        }
    }

    private CellStates[][] board;
    private CellStates[][] playerView;
    private List<Ship> ships;
    private int shotsFired;
    private int shotsLeft;
    private int ShipsLeft;

    public GameModel() {
        initaliseGame();
    }

    private void initaliseGame() {
        board = new CellStates[BOARD_SIZE][BOARD_SIZE];
        playerView = new CellStates[BOARD_SIZE][BOARD_SIZE];
        ships = new ArrayList<>();
        shotsFired = 0;
        shotsLeft = BOARD_SIZE * BOARD_SIZE;
        for (int i = 0; i < BOARD_SIZE; i++ ) {
            for (int j = 0; j < BOARD_SIZE; j++ ) {
                board[i][j] = CellStates.EMPTY;
                playerView[i][j] = CellStates.EMPTY;
            }
        }
        CreateAndPlaceShips();
    }

    private void CreateAndPlaceShips() {
        Random random = new Random();
        int totalShips = 0;
        for (ShipType type : ShipType.values()) {
            for (int i = 0; i < type.getCount(); i++) {
                placeShipsRandomly(type.getSize());
                totalShips++;
            }
        }
        ShipsLeft = totalShips;
    }



    private void placeShipsRandomly(int shipSize) {
        Random random = new Random();
        boolean placed = false;
        while (!placed) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);
            boolean isHorizontal = random.nextBoolean();
            if (canPlaceShip(row, col, shipSize, isHorizontal)) {
                placeShip(row, col, shipSize, isHorizontal);
                placed = true;
            }
        }
    }

    private void placeShipsFromaFile(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;
        int totalShips = 0;
        while ((line = br.readLine()) != null) {
            if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                continue;
            }
            String[] parts = line.split(",");
            if (parts.length != 4) {
                continue;
            }

            try {
                ShipType shipType = ShipType.valueOf(parts[0].toUpperCase());
                int row = parts[1].toUpperCase().charAt(0) - 'A';
                int col = Integer.parseInt(parts[2]) - 1;
                boolean isHorizontal = parts[3].equalsIgnoreCase("H");
                if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
                    continue;
                }

                if (canPlaceShip(row, col, shipType.getSize(), isHorizontal)) {
                    placeShip(row, col, shipType.getSize(), isHorizontal);
                    totalShips++;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid ship type");
            }
        }

        br.close();

        if (totalShips == 0) {
            CreateAndPlaceShips();
        } else {
            ShipsLeft = totalShips;
        }
    }

    public void loadShipsfromaFile(String filePath) throws IOException {
        try {
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    board[i][j] = CellStates.EMPTY;
                    playerView[i][j] = CellStates.EMPTY;
                }
            }
            ships.clear();
            shotsFired = 0;
            shotsLeft = BOARD_SIZE * BOARD_SIZE;
            placeShipsFromaFile(filePath);
            setChanged();
            notifyObservers();
            System.out.println("Loading ships from file: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private boolean canPlaceShip(int row, int col, int size, boolean horizontal) {
        if(horizontal) {
            if (col + size > BOARD_SIZE) return false;
        } else {
            if (row + size > BOARD_SIZE) return false;
        }
        for (int r = Math.max(0, row - 1); r <= Math.min(BOARD_SIZE - 1, row + (horizontal ? 1 : size)); r++) {
            for (int c = Math.max(0, col - 1); c <= Math.min(BOARD_SIZE - 1, col + (horizontal ? size : 1)); c++) {
                if (board[r][c] == CellStates.SHIP) return false;
            }
        }

        return true;
    }

    private void placeShip(int row, int col, int size, boolean horizontal) {
        Ship ship = new Ship(size);
        ships.add(ship);

        for (int i = 0; i < size; i++) {
            int r = horizontal ? row : row + i;
            int c = horizontal ? col + i : col;
            board[r][c] = CellStates.SHIP;
            ship.addPosition(r, c);
        }
    }

    public boolean fireShot(int row, int col) {
        if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
            return false;
        }

        if (playerView[row][col] != CellStates.EMPTY) {
            return false;
        }

        shotsFired++;


        if (board[row][col] == CellStates.SHIP) {
            playerView[row][col] = CellStates.HIT;
            Ship hitShip = null;
            for (Ship ship : ships) {
                if (ship.isHit(row, col)) {
                    hitShip = ship;
                    break;
                }
            }

            if (hitShip != null && hitShip.isSunk()) {
                ShipsLeft = ShipsLeft - 1;
                setChanged();
                notifyObservers("HIT");
            }
            return true;
        } else {
            playerView[row][col] = CellStates.MISS;
            setChanged();
            notifyObservers("MISS");
            return false;
        }


    }

    public boolean isGameOver() {
        return ShipsLeft == 0;
    }

    public CellStates getCellState(int row, int col) {
        return playerView[row][col];
    }

    public int getShotsFired() {
        return shotsFired;
    }

    private class Ship {
        private int size;
        private Set<Position> positions;
        private Set<Position> hits;

        public Ship(int size) {
            this.size = size;
            this.positions = new HashSet<>();
            this.hits = new HashSet<>();
        }

        public void addPosition(int row, int col) {
            positions.add(new Position(row, col));
        }

        public boolean isHit(int row, int col) {
            Position pos = new Position(row, col);
            if (positions.contains(pos)) {
                hits.add(pos);
                return true;
            }
            return false;
        }

        public boolean isSunk() {
            return hits.size() == positions.size();
        }
    }


    private class Position {
        private int row;
        private int col;

        public Position(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Position)) return false;
            Position other = (Position) obj;
            return this.row == other.row && this.col == other.col;
        }

        @Override
        public int hashCode() {
            return row * 100 + col;
        }
    }


}
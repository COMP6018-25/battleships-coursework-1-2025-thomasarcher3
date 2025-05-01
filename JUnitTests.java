import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class JUnitTests {
    private GameModel gameModel;
    private String testFilePath;

    @BeforeEach
    public void setup() throws IOException {
        gameModel = new GameModel();
        testFilePath = "/Users/thomas/Documents/testShips.txt";
    }
    // This test tests that the carriers are placed correctly in the intended space on the board.
    @Test
    void testLoadShipsFromValidFile() throws IOException {
        gameModel.loadShipsfromaFile(testFilePath);
        assertEquals(GameModel.CellStates.SHIP, gameModel.getBoardState(0, 0), "Carrier should be placed at A1");
        assertEquals(GameModel.CellStates.SHIP, gameModel.getBoardState(0, 1), "Carrier should be placed at A2");
        assertEquals(GameModel.CellStates.SHIP, gameModel.getBoardState(0, 2), "Carrier should be placed at A3");
        assertEquals(GameModel.CellStates.SHIP, gameModel.getBoardState(0, 3), "Carrier should be placed at A4");
        assertEquals(GameModel.CellStates.SHIP, gameModel.getBoardState(0, 4), "Carrier should be placed at A5");
    }
// Test that when a shot is fired it is registered by the board and recognises the shot, and that a shot cannot be made twice to the same cell and that if a shot is fired that is out of bounds an error is shown.
    @Test
    void GameStateUpdateAfterShot() throws IOException {
        gameModel.loadShipsfromaFile(testFilePath);
        assertTrue(gameModel.fireShot(0,0), "Carrier is Placed here so should return true");
        assertFalse(gameModel.fireShot(0,0), "Shot has already been fired so user shouldnt be able to fire again");
        assertThrows(AssertionError.class, () -> {
            gameModel.fireShot(15,15);
        }, "Shot is fired out of bounds so should throw an error");
    }

    //Test that when all cells containing a ship are shot that the ship is recognised as sunk
    @Test
    void testShipHasSunk() throws IOException {
        //load the test file
        gameModel.loadShipsfromaFile(testFilePath);

        //Check that the ships have been palced
        assertEquals(GameModel.CellStates.SHIP, gameModel.getBoardState(0, 0), "Carrier is placed at A1");
        assertEquals(GameModel.CellStates.SHIP, gameModel.getBoardState(0, 1), "Carrier is placed at A2");
        assertEquals(GameModel.CellStates.SHIP, gameModel.getBoardState(0, 2), "Carrier is placed at A3");
        assertEquals(GameModel.CellStates.SHIP, gameModel.getBoardState(0, 3), "Carrier is placed at A4");
        assertEquals(GameModel.CellStates.SHIP, gameModel.getBoardState(0, 4), "Carrier is placed at A5");
        // Fire shots at the carrier
        assertTrue(gameModel.fireShot(0,0), "Shot is fired at A1");
        assertTrue(gameModel.fireShot(0,1), "Shot is fired at A2");
        assertTrue(gameModel.fireShot(0,2), "Shot is fired at A3");
        assertTrue(gameModel.fireShot(0,3), "Shot is fired at A4");
        assertTrue(gameModel.fireShot(0,4), "Shot is fired at A5");
        // Check that the ship has sunk
        assertTrue(gameModel.isShipSunk(0,0), "Check that the ship is sunk");

    }

}
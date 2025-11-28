// Main.java

import Models.PlayerModel;
import Models.BoardModel;

public class Main {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("    PLAYERMODEL placeShip() TESTS");
        System.out.println("========================================\n");

        // Create a player
        PlayerModel player = new PlayerModel();

        // TEST 1: Place ship on empty cell (should succeed)
        System.out.println("TEST 1: Placing ship on empty cell (0, 0)");
        boolean result1 = player.placeShip(0, 0);
        System.out.println("Expected: true, Got: " + result1);
        System.out.println(result1 ? "✓ PASS" : "✗ FAIL");
        System.out.println("\nBoard after first placement:");
        player.playerBoard.printBoard();

        // TEST 2: Place another ship on different empty cell
        System.out.println("\n\nTEST 2: Placing ship on empty cell (2, 3)");
        boolean result2 = player.placeShip(2, 3);
        System.out.println("Expected: true, Got: " + result2);
        System.out.println(result2 ? "✓ PASS" : "✗ FAIL");
        System.out.println("\nBoard after second placement:");
        player.playerBoard.printBoard();

        // TEST 3: Try to place ship on occupied cell (should fail)
        System.out.println("\n\nTEST 3: Placing ship on OCCUPIED cell (0, 0)");
        boolean result3 = player.placeShip(0, 0);
        System.out.println("Expected: false, Got: " + result3);
        System.out.println(!result3 ? "✓ PASS" : "✗ FAIL");
        System.out.println("\nBoard should be unchanged:");
        player.playerBoard.printBoard();

        // TEST 4: Place multiple ships in a row
        System.out.println("\n\nTEST 4: Placing horizontal line of ships");
        player.placeShip(5, 5);
        player.placeShip(5, 6);
        player.placeShip(5, 7);
        System.out.println("\nBoard with horizontal ship:");
        player.playerBoard.printBoard();

        // TEST 5: Try to overlap with existing ship
        System.out.println("\n\nTEST 5: Trying to place on occupied cell (5, 6)");
        boolean result5 = player.placeShip(5, 6);
        System.out.println("Expected: false, Got: " + result5);
        System.out.println(!result5 ? "✓ PASS" : "✗ FAIL");

        // TEST 6: Edge cases - corners (using indices 0-8)
        System.out.println("\n\nTEST 6: Testing corner placements");
        System.out.println("Placing at (0, 8) - top right");
        player.placeShip(0, 8);
        System.out.println("Placing at (8, 0) - bottom left");
        player.placeShip(8, 0);
        System.out.println("Placing at (8, 8) - bottom right");
        player.placeShip(8, 8);
        System.out.println("\nFinal board:");
        player.playerBoard.printBoard();

        // TEST 7: Check if all ships are placed
        System.out.println("\n\nTEST 7: Checking if all ships are placed");
        boolean allPlaced = player.allShipsPlaced();
        System.out.println("All ships placed: " + allPlaced);

        System.out.println("\n========================================");
        System.out.println("           TESTS COMPLETE");
        System.out.println("========================================");
    }
}
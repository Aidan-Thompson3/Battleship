// In Main.java
import Controllers.GameController;
import Models.ShipModel;

public class Main {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("    TESTING SHIP PLACEMENT");
        System.out.println("========================================\n");

        GameController game = new GameController();

        // Test 1: Place a valid horizontal ship
        System.out.println("TEST 1: Placing horizontal ship (length 5) at (0, 0)");
        boolean result1 = game.placeShip(3, 6, ShipModel.Orientation.HORIZONTAL, 2);
        System.out.println(result1 ? "✓ SUCCESS" : "✗ FAILED");
        game.getPlayer1().playerBoard.printBoard();

        // Test 2: Place a valid vertical ship
        System.out.println("\n\nTEST 2: Placing vertical ship (length 4) at (2, 2)");
        boolean result2 = game.placeShip(2, 2, ShipModel.Orientation.VERTICAL, 4);
        System.out.println(result2 ? "✓ SUCCESS" : "✗ FAILED");
        game.getPlayer1().playerBoard.printBoard();

        // Test 3: Try to place overlapping ship (should fail)
        System.out.println("\n\nTEST 3: Trying to place overlapping ship at (0, 0)");
        boolean result3 = game.placeShip(0, 0, ShipModel.Orientation.VERTICAL, 3);
        System.out.println(!result3 ? "✓ CORRECTLY REJECTED" : "✗ SHOULD HAVE FAILED");
        game.getPlayer1().playerBoard.printBoard();

        // Test 4: Try to place ship out of bounds (should fail)
        System.out.println("\n\nTEST 4: Trying to place ship out of bounds (8, 8) length 5");
        boolean result4 = game.placeShip(8, 8, ShipModel.Orientation.HORIZONTAL, 5);
        System.out.println(!result4 ? "✓ CORRECTLY REJECTED" : "✗ SHOULD HAVE FAILED");

        // Test 5: Place all ships for player 1
        System.out.println("\n\nTEST 5: Placing all remaining ships");
        game.placeShip(7, 0, ShipModel.Orientation.HORIZONTAL, 3);
        game.placeShip(9, 0, ShipModel.Orientation.HORIZONTAL, 2);
        System.out.println("Final board:");
        game.getPlayer1().playerBoard.printBoard();

        System.out.println("\n========================================");
        System.out.println("All ships placed: " + game.getPlayer1().allShipsPlaced());
        System.out.println("========================================");
    }
}
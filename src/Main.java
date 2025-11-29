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

        System.out.println("TEST 2: Placing horizontal ship (length 5) at (0, 0)");
        boolean result2 = game.placeShip(3, 6, ShipModel.Orientation.HORIZONTAL, 2);
        System.out.println(result1 ? "✓ SUCCESS" : "✗ FAILED");
        game.getPlayer1().playerBoard.printBoard();

    }
}
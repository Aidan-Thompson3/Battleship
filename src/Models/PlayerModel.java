package Models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class PlayerModel {

    public BoardModel playerBoard;
    public BoardModel opponentBoard;

    public List<ShipModel> ships = new ArrayList<>();
    private static final int REQUIRED_SHIPS = 5; // standard Battleship fleet size

    public PlayerModel() {
        playerBoard = new BoardModel();
        opponentBoard = new BoardModel();
    }

    public List<ShipModel> getShips() {
        return ships;
    }

    public int getRequiredShips() {
        return REQUIRED_SHIPS;
    }

    public boolean allShipsPlaced() {
        return ships.size() >= REQUIRED_SHIPS;
    }

    public boolean placeShip(List<CoordinatesModel> positions) {
        if (positions == null || positions.isEmpty()) {
            System.out.println("Cannot place ship: no coordinates selected");
            return false;
        }

        if (ships.size() >= REQUIRED_SHIPS) {
            System.out.println("All ships for this player are already placed.");
            return false;
        }

        ShipModel ship = new ShipModel(positions);
        if (!isValidPlacement(ship)) {
            return false;
        }

        // Mark on board
        markShipOnBoard(ship);

        // Add to fleet
        ships.add(ship);
        ship.setPlaced(true);

        System.out.println("Placed ship of length " + ship.getLength()
                + " with orientation " + ship.getOrientation());
        return true;
    }

    public boolean isValidPlacement(ShipModel ship) {
        List<CoordinatesModel> positions = ship.getPositions();

        // Bounds & overlap checks
        for (CoordinatesModel position : positions) {
            int row = position.getxCor();
            int col = position.getyCor();

            if (row < 0 || row >= playerBoard.getBoardRows()
                    || col < 0 || col >= playerBoard.getBoardColumns()) {
                System.out.println("Invalid: position (" + row + "," + col + ") is out of bounds");
                return false;
            }

            if (playerBoard.getCellState(row, col) != BoardModel.CellState.EMPTY) {
                System.out.println("Invalid: position (" + row + "," + col + ") already occupied");
                return false;
            }
        }

        // Contiguity check – ship must be a straight contiguous line
        if (!arePositionsContiguous(positions, ship.getOrientation())) {
            System.out.println("Invalid: ship cells must be contiguous and in a straight line");
            return false;
        }

        return true;
    }

    private boolean arePositionsContiguous(List<CoordinatesModel> positions,
                                           ShipModel.Orientation orientation) {
        List<CoordinatesModel> copy = new ArrayList<>(positions);

        if (orientation == ShipModel.Orientation.HORIZONTAL) {
            // All rows equal, columns consecutive
            int row = copy.get(0).getxCor();
            for (CoordinatesModel c : copy) {
                if (c.getxCor() != row) {
                    return false;
                }
            }
            Collections.sort(copy, Comparator.comparingInt(CoordinatesModel::getyCor));
            int startCol = copy.get(0).getyCor();
            for (int i = 0; i < copy.size(); i++) {
                if (copy.get(i).getyCor() != startCol + i) {
                    return false;
                }
            }
        } else {
            // VERTICAL – all columns equal, rows consecutive
            int col = copy.get(0).getyCor();
            for (CoordinatesModel c : copy) {
                if (c.getyCor() != col) {
                    return false;
                }
            }
            Collections.sort(copy, Comparator.comparingInt(CoordinatesModel::getxCor));
            int startRow = copy.get(0).getxCor();
            for (int i = 0; i < copy.size(); i++) {
                if (copy.get(i).getxCor() != startRow + i) {
                    return false;
                }
            }
        }

        return true;
    }

    public void markShipOnBoard(ShipModel ship) {
        for (CoordinatesModel position : ship.getPositions()) {
            int row = position.getxCor();
            int col = position.getyCor();
            playerBoard.setCellState(row, col, BoardModel.CellState.SHIP);
        }
    }

    // Optional console helper for non-GUI use
    public ShipModel.Orientation askOrientation() {
        Scanner scan = new Scanner(System.in);
        System.out.print("Orientation [H]orizontal / [V]ertical: ");
        String input = scan.nextLine().trim().toUpperCase();

        if ("V".equals(input)) {
            return ShipModel.Orientation.VERTICAL;
        } else if ("H".equals(input)) {
            return ShipModel.Orientation.HORIZONTAL;
        } else {
            System.out.println("Invalid input, defaulting to horizontal");
            return ShipModel.Orientation.HORIZONTAL;
        }
    }
}

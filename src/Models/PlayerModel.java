package Models;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PlayerModel {

    public BoardModel playerBoard;
    public BoardModel opponentBoard;

    List<ShipModel> ships = new ArrayList<>();

    public PlayerModel(){
        ships.add(new ShipModel(ShipModel.Orientation.VERTICAL,5,6));
        playerBoard = new BoardModel();
        opponentBoard = new BoardModel();
    }

    public boolean allShipsPlaced(){
        for(int i = 0; i<ships.size(); i++){
            if(ships.get(i).placed == false){
                return false;
            }
        }
        return true;
    }

    private boolean canPlaceShipHere(List<int[]> cells) {
        for (int[] cell : cells) {
            int row = cell[0];
            int col = cell[1];


            if (row < 0 || row >= 10 || col < 0 || col >= 10) {
                return false;
            }

            // Check if cell is empty
            if (playerBoard.getCellState(row, col) != BoardModel.CellState.EMPTY) {
                return false;
            }
        }
        return true;  // All cells are valid and empty
    }

    public boolean placeShip(int row, int col, ShipModel.Orientation orientation, int length){
        ShipModel ship = new ShipModel(orientation, row, col);
        ship.length = length;
        ship.Row = row;
        ship.Column = col;

        if (!isValidPlacement(ship)) {
            return false;
        }
        markShipOnBoard(ship);

        // Add to fleet
        ships.add(ship);
        ship.placed = true;

        return true;
    }

    public boolean isValidPlacement(ShipModel ship){
        for (int i = 0; i < ship.length; i++) {
            int row, col;

            if (ship.orientation == ShipModel.Orientation.HORIZONTAL) {
                row = ship.Row;
                col = ship.Column + i;  // Extends to the right
            } else {
                row = ship.Row + i;
                col = ship.Column;
            }

            //ship is within bounds
            if (row < 0 || row >= playerBoard.getBoardRows() || col < 0 || col >= playerBoard.getBoardColumns()) {
                return false;
            }


            if (playerBoard.getCellState(row, col) != BoardModel.CellState.EMPTY) {
                return false;
            }
        }
        return true;
    }
    public void markShipOnBoard(ShipModel ship){
        for (int i = 0; i < ship.length; i++) {
            int row, col;

            // Calculate which cell to mark based on orientation
            if (ship.orientation == ShipModel.Orientation.HORIZONTAL) {
                row = ship.Row;
                col = ship.Column + i;
            } else {
                row = ship.Row + i;
                col = ship.Column;
            }


            playerBoard.setCellState(row, col, BoardModel.CellState.SHIP);
        }
    }

    public ShipModel.Orientation askOrientation(){
        Scanner scan = new Scanner(System.in);
        String input;
        System.out.println("What orientation do you want to place your ship in? [H]orizontal or [V]ertical?: ");
        input = scan.nextLine();

        if(input.equals("V")){
            return ShipModel.Orientation.VERTICAL;
        }
        else if(input.equals("H")){
            return ShipModel.Orientation.HORIZONTAL;
        }
        else{
            System.out.println("Invalid input");
            return null;
        }
    }



}


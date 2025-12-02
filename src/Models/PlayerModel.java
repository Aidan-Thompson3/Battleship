package Models;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PlayerModel {

    public BoardModel playerBoard;
    public BoardModel opponentBoard;

    public List<ShipModel> ships = new ArrayList<>();
    private static final int REQUIRED_SHIPS = 5; // Require 5 ships to be placed

    public PlayerModel(){


        playerBoard = new BoardModel();
        opponentBoard = new BoardModel();
    }

    public List<ShipModel> getShips() {
        return ships;
    }

    public int getRequiredShips() {
        return REQUIRED_SHIPS;
    }

    public boolean allShipsPlaced(){
        return ships.size() >= REQUIRED_SHIPS;
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

    public boolean placeShip(List<CoordinatesModel> positions){
        if (positions == null || positions.isEmpty()) {
            System.out.println("Cannot place ship: No coordinates selected");
            return false;
        }

        ShipModel ship = new ShipModel(positions);
        if (!isValidPlacement(ship)) {
            return false;
        }
        markShipOnBoard(ship);

        // Add to fleet
        ships.add(ship);
        ship.setPlaced(true);

        return true;
    }

    public boolean isValidPlacement(ShipModel ship){
        // Check each position in the ship
        for (CoordinatesModel position : ship.getPositions()) {
            int row = position.getxCor();
            int col = position.getyCor();

            // Check if position is within bounds
            if (row < 0 || row >= playerBoard.getBoardRows() || col < 0 || col >= playerBoard.getBoardColumns()) {
                System.out.println("Invalid: Position (" + row + ", " + col + ") is out of bounds. Board is " + playerBoard.getBoardRows() + "x" + playerBoard.getBoardColumns());
                return false;
            }

            // Check if cell is empty
            if (playerBoard.getCellState(row, col) != BoardModel.CellState.EMPTY) {
                System.out.println("Invalid: Position (" + row + ", " + col + ") is already occupied");
                return false;
            }
        }
        return true;
    }



    public void markShipOnBoard(ShipModel ship){
        for (CoordinatesModel position : ship.getPositions()) {
            int row = position.getxCor();
            int col = position.getyCor();
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
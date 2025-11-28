package Models;

import java.util.ArrayList;
import java.util.List;

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

    public boolean placeShip(int xCor, int yCor){
        if(playerBoard.getCellState(xCor, yCor) == BoardModel.CellState.EMPTY) {
            playerBoard.setCellState(xCor, yCor, BoardModel.CellState.SHIP);
            System.out.println("Ship placed!");
            return true;
        }
        else{
            System.out.println("Ship not placed! Cell is not empty!");
            return false;
        }
    }

}


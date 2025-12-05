package Controllers;

import Models.CoordinatesModel;
import Models.PlayerModel;
import Models.ShipModel;
import Models.BoardModel;

import java.util.List;

public class GameController {

    private PlayerModel player1;
    private PlayerModel player2;
    private GamePhase currentPhase;
    private PlayerModel currentPlayer;

    public enum GamePhase{
        SETUP,
        PLAYER1_TURN,
        PLAYER2_TURN,
        GAME_OVER
    }

    public GameController(){
        initializeGame();
    }

    public void initializeGame(){
        currentPhase = GamePhase.SETUP;
        player1 = new PlayerModel();
        player2 = new PlayerModel();
        currentPlayer = player1;

        System.out.println("Game initialized!");
        System.out.println("Phase: " + currentPhase);
        System.out.println("Current player setting up: Player 1");
    }

    public boolean isSetupComplete() {
        return player1.allShipsPlaced() && player2.allShipsPlaced();
    }

    public boolean placeShip(List<CoordinatesModel> positions) {
        if (currentPhase != GamePhase.SETUP) {
            System.out.println("Cannot place ships outside of SETUP phase.");
            return false;
        }

        boolean success = currentPlayer.placeShip(positions);

        if (success) {
            if (currentPlayer.allShipsPlaced()) {
                if (currentPlayer == player1 && !player2.allShipsPlaced()) {
                    currentPlayer = player2;
                    System.out.println("Player 1 finished placing ships. Now Player 2 places ships.");
                } else if (player1.allShipsPlaced() && player2.allShipsPlaced()) {
                    System.out.println("All ships placed for both players. Starting battle!");
                    startBattle();
                }
            }
        }
        return success;
    }

    public void startBattle() {
        if (isSetupComplete()) {
            currentPhase = GamePhase.PLAYER1_TURN;
            currentPlayer = player1;
            System.out.println("Battle begins! Player 1 attacks first.");
        } else {
            System.out.println("Cannot start battle – not all ships are placed.");
        }
    }

    //attack
    public boolean attack(int row, int col) {
        if (currentPhase != GamePhase.PLAYER1_TURN &&
                currentPhase != GamePhase.PLAYER2_TURN) {
            System.out.println("Cannot attack – not in battle phase.");
            return false;
        }

        PlayerModel attacker = currentPlayer;
        PlayerModel defender = (currentPlayer == player1) ? player2 : player1;

        BoardModel defenderBoard = defender.playerBoard;
        BoardModel attackerView = attacker.opponentBoard;

        BoardModel.CellState state = defenderBoard.getCellState(row, col);
        boolean hit = false;

        if (state == BoardModel.CellState.EMPTY) {
            defenderBoard.setCellState(row, col, BoardModel.CellState.MISS);
            attackerView.setCellState(row, col, BoardModel.CellState.MISS);
            System.out.println(getPlayerName(attacker) + " missed at (" + row + "," + col + ").");
        } else if (state == BoardModel.CellState.SHIP) {
            defenderBoard.setCellState(row, col, BoardModel.CellState.HIT);
            attackerView.setCellState(row, col, BoardModel.CellState.HIT);
            System.out.println(getPlayerName(attacker) + " HIT at (" + row + "," + col + ")!");
            hit = true;

            if (isShipSunkAt(defender, row, col)) {
                System.out.println("A ship has been sunk!");
            }

            if (areAllShipsSunk(defender)) {
                currentPhase = GamePhase.GAME_OVER;
                System.out.println(getPlayerName(attacker) + " wins! All enemy ships sunk.");
            }
        } else {
            System.out.println("Cell (" + row + "," + col + ") has already been targeted.");
        }

        return hit;
    }


    public void endTurn() {
        if (currentPhase == GamePhase.GAME_OVER) {
            return;
        }

        if (currentPhase == GamePhase.PLAYER1_TURN) {
            currentPhase = GamePhase.PLAYER2_TURN;
            currentPlayer = player2;
        } else if (currentPhase == GamePhase.PLAYER2_TURN) {
            currentPhase = GamePhase.PLAYER1_TURN;
            currentPlayer = player1;
        }
    }

    private boolean isShipSunkAt(PlayerModel defender, int row, int col) {
        for (ShipModel ship : defender.getShips()) {
            boolean contains = false;
            for (CoordinatesModel pos : ship.getPositions()) {
                if (pos.getxCor() == row && pos.getyCor() == col) {
                    contains = true;
                    break;
                }
            }
            if (!contains) {
                continue;
            }

            boolean allHit = true;
            for (CoordinatesModel pos : ship.getPositions()) {
                BoardModel.CellState s =
                        defender.playerBoard.getCellState(pos.getxCor(), pos.getyCor());
                if (s != BoardModel.CellState.HIT && s != BoardModel.CellState.SUNK) {
                    allHit = false;
                    break;
                }
            }

            if (allHit) {
                for (CoordinatesModel pos : ship.getPositions()) {
                    defender.playerBoard.setCellState(
                            pos.getxCor(), pos.getyCor(), BoardModel.CellState.SUNK);
                }
                return true;
            }
        }
        return false;
    }

    private boolean areAllShipsSunk(PlayerModel player) {
        for (ShipModel ship : player.getShips()) {
            for (CoordinatesModel pos : ship.getPositions()) {
                BoardModel.CellState s =
                        player.playerBoard.getCellState(pos.getxCor(), pos.getyCor());
                if (s != BoardModel.CellState.HIT && s != BoardModel.CellState.SUNK) {
                    return false;
                }
            }
        }
        return true;
    }

    public void printPlayerShips(){
        String playerName = (currentPlayer == player1) ? "Player 1" : "Player 2";
        System.out.println("\n=== " + playerName + "'s Ships ===");
        System.out.println("Total ships placed: " + currentPlayer.ships.size());

        for (int i = 0; i < currentPlayer.ships.size(); i++) {
            ShipModel ship = currentPlayer.ships.get(i);
            System.out.println("\nShip #" + (i + 1) + ":");
            System.out.println("  Length: " + ship.length);
            System.out.println("  Orientation: " + ship.orientation);
            System.out.println("  Positions:");
            for (CoordinatesModel pos : ship.positions) {
                System.out.println("    - Row: " + pos.getxCor() + ", Col: " + pos.getyCor());
            }
        }
        System.out.println("\n=== " + playerName + "'s Board ===");
        currentPlayer.playerBoard.printBoard();
    }

    private String getPlayerName(PlayerModel p) {
        return (p == player1) ? "Player 1" : "Player 2";
    }

    public GamePhase getCurrentPhase() {
        return currentPhase;
    }

    public PlayerModel getCurrentPlayer() {
        return currentPlayer;
    }

    public PlayerModel getPlayer1() {
        return player1;
    }

    public PlayerModel getPlayer2() {
        return player2;
    }
}

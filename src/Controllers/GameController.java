package Controllers;
import Models.PlayerModel;
import Models.ShipModel;


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

    public boolean placeShip(int row, int col, ShipModel.Orientation orientation, int length) {
        //GameController checks "Are we in SETUP phase?"
        //GameController checks continues if currentPhase is the setup phase

        if (currentPhase != GamePhase.SETUP) {
            return false;
        }

        // Try to place it
        //Delegates to PlayerModel for validation of ship placement
        boolean success = currentPlayer.placeShip(row, col, orientation, length);

        if (success) {
            if (currentPlayer.allShipsPlaced()) {
                if (currentPlayer == player1 && !player2.allShipsPlaced()) {
                    // Switch to player 2
                    currentPlayer = player2;
                } else if (player1.allShipsPlaced() && player2.allShipsPlaced()) {
                    // Both done - start battle
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
        }
    }

    //Getters
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

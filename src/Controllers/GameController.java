package Controllers;
import Models.PlayerModel;
import java.util.Scanner;

public class GameController {

    private PlayerModel player1;
    private PlayerModel player2;

    public enum GamePhase{
        SETUP,
        PLAYER1_TURN,
        PLAYER2_TURN,
        GAME_OVER
    }

    public void initalizeGame(){
        Scanner scan = new Scanner(System.in);
        System.out.println("Player 1: ");
    }
    public void placeShip(){

    }

}

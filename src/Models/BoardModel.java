package Models;

public class BoardModel {
    public enum CellState{
        EMPTY,           // No ship, not attacked
        SHIP,            // Ship here, not attacked yet
        MISS,            // Attacked, no ship was here
        HIT,             // Attacked, ship was here
        SUNK             // Ship cell that is part of a fully-sunk ship
    }
    private CellState[][] boardArray;
    private int boardColumns;
    private int boardRows;

    public int getBoardColumns(){
        return boardColumns;
    }

    public int getBoardRows(){
        return boardRows;
    }

    public BoardModel(){
        boardColumns = 10;
        boardRows = 10;
        boardArray = new CellState[boardRows][boardColumns];
        for (int row = 0; row < boardRows; row++) {
            for (int col = 0; col < boardColumns; col++) {
                boardArray[row][col] = CellState.EMPTY;
            }
        }
    }

    public CellState getCellState(int row, int col){
        return boardArray[row][col];
    }
    public void setCellState(int row, int col, CellState state){
        boardArray[row][col] = state;
    }

    public boolean allCellsAre(CellState expectedState){
        for (int row = 0; row < boardRows; row++) {
            for (int col = 0; col < boardColumns; col++) {
                if(boardArray[row][col] != expectedState){
                    return false;
                }
            }
        }
        return true;
    }

    public void printBoard() {
        System.out.print("    ");
        for (int col = 0; col < boardColumns; col++) {
            System.out.print(col + " ");
        }
        System.out.println();
        System.out.println("   ----------------------");

        for (int row = 0; row < boardRows; row++) {
            System.out.print(row + " | ");
            for (int col = 0; col < boardColumns; col++) {
                CellState state = boardArray[row][col];
                System.out.print(getCellSymbol(state) + " ");
            }
            System.out.println("|");
        }
        System.out.println("   ----------------------");

        // Print legend
        System.out.println("\n   Legend: · = Empty  S = Ship  X = Hit/Sunk  O = Miss");
    }

    private String getCellSymbol(CellState state) {
        switch (state) {
            case EMPTY:
                return "·";
            case SHIP:
                return "S";
            case HIT:
            case SUNK:
                return "X";
            case MISS:
                return "O";
            default:
                return "?";
        }
    }

}

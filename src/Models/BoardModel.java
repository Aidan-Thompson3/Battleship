package Models;

public class BoardModel {
    public enum CellState{
        EMPTY,           // No ship, not attacked
        SHIP,            // Ship here, not attacked yet
        MISS,            // Attacked, no ship was here
        HIT,             // Attacked, ship was here
        SUNK
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
        boardColumns = 9;
        boardRows = 9;
        boardArray = new CellState[boardColumns][boardRows];
        for (int col = 0; col < boardColumns; col++) {
            for (int row = 0; row < boardColumns; row++) {
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
        for (int col= 0; col < boardColumns; col++) {
            for (int row = 0; row < boardColumns; row++) {
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
        System.out.println("   --------------------");

        for (int row = 0; row < boardRows; row++) {
            System.out.print(row + " | ");
            for (int col = 0; col < boardColumns; col++) {
                CellState state = boardArray[row][col];
                System.out.print(getCellSymbol(state) + " ");
            }
            System.out.println("|");
        }
        System.out.println("   --------------------");

        // Print legend
        System.out.println("\n   Legend: · = Empty  S = Ship  X = Hit  O = Miss");
    }

    private String getCellSymbol(CellState state) {
        switch (state) {
            case EMPTY:
                return "·";
            case SHIP:
                return "S";
            case HIT:
                return "X";
            case MISS:
                return "O";
            default:
                return "?";
        }
    }



}

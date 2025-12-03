package Models;

public class BoardModel {

    public enum CellState {
        EMPTY,   // No ship, not attacked
        SHIP,    // Ship here, not attacked yet
        MISS,    // Attacked, no ship was here
        HIT,     // Attacked, ship was here
        SUNK     // All cells of a ship have been hit
    }

    private final CellState[][] boardArray;
    private final int boardColumns;
    private final int boardRows;

    public BoardModel() {
        this.boardRows = 10;
        this.boardColumns = 10;
        this.boardArray = new CellState[boardRows][boardColumns];

        // Initialize all cells to EMPTY
        for (int r = 0; r < boardRows; r++) {
            for (int c = 0; c < boardColumns; c++) {
                boardArray[r][c] = CellState.EMPTY;
            }
        }
    }

    public int getBoardColumns() {
        return boardColumns;
    }

    public int getBoardRows() {
        return boardRows;
    }

    public CellState getCellState(int row, int col) {
        if (!inBounds(row, col)) {
            throw new IndexOutOfBoundsException("Row/Col out of bounds: (" + row + "," + col + ")");
        }
        return boardArray[row][col];
    }

    public void setCellState(int row, int col, CellState state) {
        if (!inBounds(row, col)) {
            throw new IndexOutOfBoundsException("Row/Col out of bounds: (" + row + "," + col + ")");
        }
        boardArray[row][col] = state;
    }

    private boolean inBounds(int row, int col) {
        return row >= 0 && row < boardRows && col >= 0 && col < boardColumns;
    }

    public boolean allCellsAre(CellState expectedState) {
        for (int r = 0; r < boardRows; r++) {
            for (int c = 0; c < boardColumns; c++) {
                if (boardArray[r][c] != expectedState) {
                    return false;
                }
            }
        }
        return true;
    }

    //Debug helper to print the board to the console.
    public void printBoard() {
        System.out.println("   0 1 2 3 4 5 6 7 8 9");
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
        System.out.println();
        System.out.println("   Legend: · = Empty  S = Ship  X = Hit  O = Miss");
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
            case SUNK:
                return "X"; // draw sunk ships the same as hit
            default:
                return "?";
        }
    }
}

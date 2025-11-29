package Models;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class ShipModel {
    public enum Color{
        BLACK,
        WHITE
    }
    public enum Orientation{
        HORIZONTAL,
        VERTICAL
    }
    protected Color color;
    protected Orientation orientation;
    protected int Row; //Starting point
    protected int Column; //Starting point
    protected char symbol;
    protected int length;
    protected boolean placed;

    public ShipModel(Orientation orientation, int row, int column){
        Random rand = new Random();
        this.color = color;
        this.Row = row;
        this.Column = column;
        this.length = rand.nextInt(5)+1;
        this.orientation = orientation;
        placed = false;
    }

    public int getRow(){
        return Row;
    }
    public int getColumn(){
        return Column;
    }

    /*public List<int[]> getOccupiedCells() { //returns possible cells for a ship to cover starting from its starting point
        List<int[]> cells = new ArrayList<>();

        if (orientation == Orientation.HORIZONTAL) {
            for (int i = 0; i < length; i++) {
                cells.add(new int[]{
                        Row,
                        Column + i
                });
            }
        } else { // VERTICAL
            for (int i = 0; i < length; i++) {
                cells.add(new int[]{Row + i, Column});
            }
        }
        return cells;
    }
    */

}

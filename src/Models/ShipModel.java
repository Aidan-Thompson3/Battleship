package Models;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class ShipModel {

    public enum Orientation{
        HORIZONTAL,
        VERTICAL
    }
    protected Orientation orientation;
    protected int Row; //Starting point
    protected int Column; //Starting point
    protected char symbol;
    protected int length;
    protected boolean placed;

    public ShipModel(Orientation orientation, int row, int column, int length){
        this.Row = row;
        this.Column = column;
        this.length = length;
        this.orientation = orientation;
        placed = false;
    }

    public int getRow(){
        return Row;
    }
    public int getColumn(){
        return Column;
    }

}

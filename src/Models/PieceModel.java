package Models;
import java.util.Random;


public class PieceModel {
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
    protected int Row;
    protected int Column;
    protected char symbol;
    protected int length;

    public PieceModel(Color color, Orientation orientation,int row, int column){
        Random rand = new Random();
        this.color = color;
        this.Row = row;
        this.Column = column;
        this.length = rand.nextInt(5)+1;
        this.orientation = orientation;
    }

}

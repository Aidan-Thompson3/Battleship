package Models;

public class CoordinatesModel {
    private int xCor;
    private int yCor;


    public CoordinatesModel(){
        xCor = -1;
        yCor = -1;
    }

    public CoordinatesModel(int x, int y){
        xCor = x;
        yCor = y;
    }

    public int getxCor(){
        return xCor;
    }

    public int getyCor(){
        return yCor;
    }
}

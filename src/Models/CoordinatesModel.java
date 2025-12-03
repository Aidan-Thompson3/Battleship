package Models;

public class CoordinatesModel {
    private int xCor;
    private int yCor;

    public CoordinatesModel() {
        this(-1, -1);
    }

    public CoordinatesModel(int x, int y) {
        this.xCor = x;
        this.yCor = y;
    }

    public int getxCor() {
        return xCor;
    }

    public int getyCor() {
        return yCor;
    }
}

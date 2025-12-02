package Models;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ShipModel {

    public enum Orientation{
        HORIZONTAL,
        VERTICAL
    }
    public Orientation orientation;
    public List<CoordinatesModel> positions;
    private Set<CoordinatesModel> hits;
    public int length;
    protected boolean placed;

    public ShipModel(List<CoordinatesModel> positions){
        this.positions = positions;
        this.length = positions.size();
        this.placed = false;

        // Determine orientation based on positions (if they form a line)
        if (positions.size() >= 2) {
            CoordinatesModel first = positions.get(0);
            CoordinatesModel second = positions.get(1);

            if (first.getxCor() == second.getxCor()) {
                this.orientation = Orientation.VERTICAL;
            } else {
                this.orientation = Orientation.HORIZONTAL;
            }
        }
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public List<CoordinatesModel> getPositions() {
        return positions;  // Returns the list of all coordinates the ship occupies
    }

    public int getLength() {
        return length;
    }

    public boolean isPlaced() {
        return placed;
    }

    public void setPlaced(boolean placed) {
        this.placed = placed;
    }
}
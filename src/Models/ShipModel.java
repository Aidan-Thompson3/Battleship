package Models;

import java.util.ArrayList;
import java.util.List;

public class ShipModel {

    public enum Orientation {
        HORIZONTAL,
        VERTICAL
    }

    public Orientation orientation;
    public List<CoordinatesModel> positions;
    public int length;
    protected boolean placed;

    public ShipModel(List<CoordinatesModel> positions) {
        if (positions == null || positions.isEmpty()) {
            throw new IllegalArgumentException("Ship must have at least one coordinate");
        }
        // Defensive copy
        this.positions = new ArrayList<>(positions);
        this.length = this.positions.size();
        this.orientation = deduceOrientation(this.positions);
        this.placed = false;
    }

    private Orientation deduceOrientation(List<CoordinatesModel> positions) {
        if (positions.size() == 1) {
            // A single–cell ship can be treated as horizontal by default
            return Orientation.HORIZONTAL;
        }

        boolean sameRow = true;
        boolean sameCol = true;

        int baseRow = positions.get(0).getxCor();
        int baseCol = positions.get(0).getyCor();

        for (CoordinatesModel pos : positions) {
            if (pos.getxCor() != baseRow) {
                sameRow = false;
            }
            if (pos.getyCor() != baseCol) {
                sameCol = false;
            }
        }

        if (sameRow && !sameCol) {
            return Orientation.HORIZONTAL;
        } else if (sameCol && !sameRow) {
            return Orientation.VERTICAL;
        } else if (sameRow && sameCol) {
            // All cells are the same – treat as length-1 horizontal
            return Orientation.HORIZONTAL;
        } else {
            throw new IllegalArgumentException("Ship coordinates must be in a straight line");
        }
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public List<CoordinatesModel> getPositions() {
        return positions;
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

package jmx.javachallenge.helper;

import javafx.scene.control.Cell;

/**
 * Created by megam on 2015. 11. 15..
 */
public class Tile {
    int unitID;
    CellType cellType;

    public Tile() {
        this.unitID = -1;
        cellType = CellType.UNKNOWN;
    }

    public void setCellType(CellType cellType) {
        this.cellType = cellType;
    }

    public void setBuilder(int unitID) {
        this.unitID = unitID;
    }

    public String toString() {
        return String.valueOf((cellType.getValue() >= 0 ? " " + cellType.getValue() : cellType.getValue())) + (unitID == -1 ? "[ ] " : "[" + unitID + "] ");
    }

    public int getCellType() {
        return cellType.getValue();
    }
}

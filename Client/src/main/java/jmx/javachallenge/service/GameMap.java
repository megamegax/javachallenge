package jmx.javachallenge.service;

import eu.loxon.centralcontrol.WsCoordinate;
import jmx.javachallenge.helper.Tile;
import jmx.javachallenge.helper.TileType;

/**
 * Created by Márton on 2015. 11. 21..
 */
public class GameMap {

    private Tile[][] myMap;
    private int xSize;
    private int ySize;

    /**
     * Inicializálja a térképet, üres infókkal
     *
     * @param xSize Vízszintes méret
     * @param ySize Függőleges méret
     */
    public GameMap(int xSize, int ySize) {
        this.xSize = xSize;
        this.ySize = ySize;
        myMap = new Tile[xSize][ySize];
        for (int y = 0; y < ySize; y++) {
            for (int x = 0; x < xSize; x++) {
                myMap[x][y] = new Tile();
            }
        }
    }

    /**
     * Visszaadja az adott mezőt, a szerver által használt koordináta rendszerben.
     *
     * @param x Vízszintes koordináta, bal oldalon 0, jobbra növekszik
     * @param y Függőleges koordináta, lent 0, felfelé növekszik
     * @return A Tile objektum
     */
    public Tile getMapTile(int x, int y) {
        if (x >= xSize || y >= ySize || x < 0 || y < 0) {
            Tile tile = new Tile();
            tile.setTileType(TileType.OBSIDIAN);
            return tile;
        }
        return myMap[x][y];
    }

    public Tile getMapTile(WsCoordinate coordinate) {
        return getMapTile(coordinate.getX(), coordinate.getY());
    }

    public int getXSize() {
        return xSize;
    }

    public int getYSize() {
        return ySize;
    }
}

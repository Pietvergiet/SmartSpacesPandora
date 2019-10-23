package smartSpaces.Pandora.Game.Map;

import java.util.ArrayList;

public class GameMap {
    private int xDim;
    private int yDim;
    private Boolean[][] mapGrid;
    private ArrayList<MapObject> objects;

    public GameMap (int x, int y) {
        xDim = x;
        yDim = y;
        mapGrid = new Boolean[x][y];
    }

    public void setVisible(int x, int y) {
        mapGrid[x][y] = true;
    }

    public void setInvisible(int x, int y) {
        mapGrid[x][y] = false;
    }

    public void addObject(MapObject object) {
        objects.add(object);
    }

    public ArrayList<Location> getVisibleList() {
        ArrayList<Location> visibleList = new ArrayList<>();
        for (int x = 0; x < xDim; x++) {
            for(int y = 0; y < yDim; y++){
                if (mapGrid[x][y]) {
                    visibleList.add(new Location(x, y));
                }
            }
        }
        return visibleList;
    }

    public ArrayList<Location> getInvisibleList() {
        ArrayList<Location> invisibleList = new ArrayList<>();
        for (int x = 0; x < xDim; x++) {
            for(int y = 0; y < yDim; y++){
                if (!mapGrid[x][y]) {
                    invisibleList.add(new Location(x, y));
                }
            }
        }
        return invisibleList;
    }

    public int[] getMapDimensions() {
        return new int[]{xDim, yDim};
    }
}

package smartSpaces.Pandora.Game.Map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import smartSpaces.Pandora.Game.Player;
import smartSpaces.Pandora.Location;

public class GameMap {
    private int xDim;
    private int yDim;
    private boolean[][] mapGrid;
    private HashMap<Location, MapObject> objects;
    private HashMap<Player, Location> players;

    /**
     * Initialises a {@link GameMap}
     * @param x The amount of rows
     * @param y The amount of columns
     */
    public GameMap (int x, int y) {
        xDim = x;
        yDim = y;
        mapGrid = new boolean[x][y];
        objects = new HashMap<>();
        players = new HashMap<>();
    }

    /**
     * Initialises a {@link GameMap}
     * @param dim The amount of rows and columns
     */
    public GameMap (int dim) {
        mapGrid = new boolean[dim][dim];
        objects = new HashMap<>();
        players = new HashMap<>();
    }

    /**
     * Sets a certain {@link Location} visible.
     * @param location The location
     */
    public void setVisible(Location location) {
        mapGrid[location.getX()][location.getY()] = true;
    }

    /**
     * Sets a certain {@link Location} invisible.
     * @param location The location
     */
    public void setInvisible(Location location) {
        mapGrid[location.getX()][location.getY()] = false;
    }

    public void setPlayerLocation(Location location, Player player) {
        players.put(player, location);
    }

    public HashMap<Player, Location> getPlayerLocations() {
        return players;
    }

    /**
     * Assigns an {@link MapObject} to a certain {@link Location} on the map.
     * This can also be used to change the {@link Location} if this {@link MapObject} already exists on the map.
     * @param object The {@link MapObject}
     * @param location The {@link Location}
     */
    public void addObject(MapObject object, Location location) {
        objects.put(location, object);
    }

    public ArrayList<MapObject> getObjects() {
        return new ArrayList<>(objects.values());
    }


    public void removeObject(Location location) {objects.remove(location); }

    /**
     * Returns the {@link Location} of a certain {@link MapObject}
     * @param object The {@link MapObject}
     * @return The {@link Location} or null it does not exist on the map
     */
    public Location getObjectLocation(MapObject object){
        for(Map.Entry<Location, MapObject> p : objects.entrySet()){
            if (p.getValue() == object) {
                return p.getKey();
            }
        }
        return null;
    }

    /**
     * Returns the {@link Location} of the {@link MapObject} with a certain {@link ObjectType}.
     * @param object The {@link MapObject}
     * @return The {@link Location} the first found {@link MapObject} of the given type.
     */
    public Location getObjectLocationFromType(ObjectType object){
        for(Map.Entry<Location, MapObject> p : objects.entrySet()){
            if (p.getValue().getObjectType() == object) {
                return p.getKey();
            }
        }
        return null;
    }

    /**
     * Return the {@link MapObject} on this {@link Location}
     * @param location The {@link Location}
     * @return The {@link MapObject} or null if there is no {@link MapObject}
     */
    public MapObject getObjectFromLocation(Location location) {
        return objects.get(location);
    }

    /**
     * Returns all the places on the map that are set to Visible
     * @return {@link ArrayList<Location>}
     */
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

    /**
     * Returns all the places on the map that are set to Invisible
     * @return {@link ArrayList<Location>}
     */
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

package smartSpaces.Pandora.Game.Tasks;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import smartSpaces.Pandora.Game.Map.MapObject;
import smartSpaces.Pandora.Location;

public class LocationTask extends Task {

    private Location location;
    private MapObject mapObject;
    private HashMap<MapObject, Location> locObj;
    private ArrayList<Location> locations;
    private ArrayList<MapObject> mapObjects;

    public LocationTask(Location location){
        super(TaskType.LOCATION);
        this.location = location;
        buildDescription();
    }

    public LocationTask(MapObject object){
        super(TaskType.LOCATION);
        mapObject = object;
        buildDescription();
    }

    public LocationTask(Location location, MapObject object){
        super(TaskType.LOCATION);
        this.location = location;
        mapObject = object;
        buildDescription();
    }

    public LocationTask(ArrayList<MapObject> list) {

        super(TaskType.LOCATION_CONCURRENT, true);
        //Log.i("LOCATIONTASKCONSTRUCTor", list.toString());
        this.mapObjects = list;

        buildDescription();
    }

    public LocationTask(ArrayList<Location> locations, ArrayList<MapObject> objects) {
        super(TaskType.LOCATION_CONCURRENT, true);
        this.locations = locations;
        this.mapObjects = objects;
        buildDescription();

    }

    public void buildDescription(){
        StringBuilder desc = new StringBuilder();
        if (super.getTaskType() == TaskType.LOCATION) {
//            desc.append("Occupy ");
//            if (mapObject == null) {
//                desc.append(location.getX());
//                desc.append(",");
//                desc.append(location.getY());
//            } else {
//                desc.append("the ");
//                desc.append(mapObject.getName());

            switch (mapObject.getObjectType()) {
                case SHEEP:
                    desc.append("Pet a sheep");
                    break;
                case HAYSTACK:
                    desc.append("Find the needle in a haystack");
                    break;
                default:
                    desc.append("Occupy the ");
                    desc.append(mapObject.getName());
            }

            super.description = desc.toString();
        } else if (super.getTaskType() == TaskType.LOCATION_CONCURRENT){
            desc.append("Occupy these places: ");
            if (mapObjects == null) {
                for (Location l : locations){
                    desc.append(l.getX());
                    desc.append(",");
                    desc.append(l.getY());
                    desc.append(" & ");
                }
                desc.deleteCharAt(desc.length()-1);
                desc.deleteCharAt(desc.length()-2);
            } else {
                for (MapObject b : mapObjects){
                    desc.append("the ");
                    desc.append(b.getName());
                    desc.append(" & ");
                    desc.deleteCharAt(desc.length()-1);
                }
            }
            super.description = desc.toString();
        }
    }

    public Location getCoordinates() {
        return location;
    }

    public MapObject getMapObject() {
        return mapObject;
    }

    public ArrayList<Location> getLocations() {
        return locations;
    }

    public ArrayList<MapObject> getMapObjects() {
        return mapObjects;
    }
}

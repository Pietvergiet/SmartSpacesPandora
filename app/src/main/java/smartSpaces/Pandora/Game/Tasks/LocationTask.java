package smartSpaces.Pandora.Game.Tasks;

public class LocationTask extends Task {

    private int xCoordinate;
    private int yCoordinate;
    private MapObject mapObject;

    public LocationTask(int[] coordinates){
        super(TaskTypes.TYPE_LOCATION);
        xCoordinate = coordinates[0];
        yCoordinate = coordinates[2];
        buildDescription();
    }

    public LocationTask(MapObject object){
        super(TaskTypes.TYPE_LOCATION);
        mapObject = object;
        buildDescription();
    }

    public LocationTask(int[] coordinates, MapObject object){
        super(TaskTypes.TYPE_LOCATION);
        xCoordinate = coordinates[0];
        yCoordinate = coordinates[2];
        mapObject = object;
        buildDescription();
    }

    @Override
    public void buildDescription(){
        String desc = "Occupy ";
        if (mapObject == null) {
            desc += xCoordinate + "," + yCoordinate;
        } else {
            desc += "the " + mapObject.getName();
        }
        description = desc;
    }

    public int[] getCoordinates() {
        return new int[]{xCoordinate, yCoordinate};
    }

    public MapObject getMapObject() {
        return mapObject;
    }

}

package smartSpaces.Pandora.Game.Map;

public class MapObject {
    private String name;
    private ObjectTypes objectType;

    public MapObject(ObjectTypes type) {
        objectType = type;
    }

    public String getName() {
        return name;
    }

    public int getResource() {
        return objectType.getResource();
    }
}


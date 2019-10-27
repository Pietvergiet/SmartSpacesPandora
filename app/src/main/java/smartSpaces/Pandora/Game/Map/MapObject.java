package smartSpaces.Pandora.Game.Map;

public class MapObject {
    private String name;
    private ObjectType objectType;

    public MapObject(ObjectType type) {
        objectType = type;
        name = objectType.toString();
    }

    public String getName() {
        return name;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public int getResource() {
        return objectType.getResource();
    }
}


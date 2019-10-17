package smartSpaces.Pandora.Game.Map;

public enum ObjectTypes {

    ROPE(1), LOCK(2), KEY(3);

    private int resource;

    // getter method
    public int getResource()
    {
        return this.resource;
    }

    // enum constructor - cannot be public or protected
    private ObjectTypes(int resource)
    {
        this.resource = resource;
    }
}

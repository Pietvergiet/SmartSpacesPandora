package smartSpaces.Pandora;

public class Location {
    int x;
    int y;

    public Location (int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isLocation(int x, int y) {
        return (getX() == x && getY() == y);
    }
}

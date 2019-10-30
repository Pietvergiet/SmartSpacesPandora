package smartSpaces.Pandora;

import androidx.annotation.NonNull;

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

    public boolean isLocation(Location location) {
        return (x == location.getX() && y == location.getY());
    }

    @NonNull
    @Override
    public String toString() {
        return "("+x+","+y+")";
    }
}

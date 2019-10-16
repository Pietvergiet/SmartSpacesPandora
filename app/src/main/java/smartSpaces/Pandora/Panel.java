package smartSpaces.Pandora;

import android.graphics.Color;

import java.util.Random;

public class Panel {
    private static String[] VERBS = {"rotate", "flip", "turn", "eat", "slip", "satisfy", "wipe", "itch", "offend", "peel", "box", "boot", "load", "chase", "open"};
    private static String[] OBJECTS = {"plate", "ethernet", "fog", "sticker", "gear", "rail", "cart", "pig", "path", "movie", "couch", "bagel", "kitten", "box"};

    // panel properties
    private int id;
    private int color;
    private String verb;
    private String object;

    public Panel(int id) {
        this.id = id;
        this.color = getRandomColor();
        this.verb = getRandomVerb();
        this.object = getRandomObject();
    }

    public int getId() {
        return this.id;
    }

    public int getColor() {
        return this.color;
    }

    public String getVerb() {
        return this.verb;
    }

    public String getObject() {
        return this.object;
    }

    public int getRandomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    public String getRandomObject() {
        Random rnd = new Random();
        return this.OBJECTS[rnd.nextInt(this.OBJECTS.length)];
    }

    public String getRandomVerb() {
        Random rnd = new Random();
        return this.VERBS[rnd.nextInt(this.VERBS.length)];
    }
}

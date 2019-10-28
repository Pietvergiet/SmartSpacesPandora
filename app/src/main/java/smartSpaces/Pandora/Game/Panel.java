package smartSpaces.Pandora.Game;

import android.graphics.Color;

import java.util.Random;

public class Panel {
    private static String[] VERBS = {"rotate", "flip", "turn", "eat", "slip", "satisfy", "wipe", "itch", "offend", "peel", "box", "boot", "load", "chase", "open"};
    private static String[] OBJECTS = {"plate", "ethernet", "fog", "sticker", "gear", "rail", "cart", "pig", "path", "movie", "couch", "bagel", "kitten", "box"};

    // panel properties
    private int id;
    private String verb;
    private String object;

    public Panel(int id) {
        this.id = id;
        this.verb = getRandomVerb();
        this.object = getRandomObject();
    }

    public Panel(int id, String verb, String object){
        this.id = id;
        this.verb = verb;
        this.object = object;
    }

    public int getId() {
        return this.id;
    }

    public String[] getOBJECTS() {
        return OBJECTS;
    }

    public String[] getVERBS() {
        return VERBS;
    }

    public String getVerb() {
        return this.verb;
    }

    public String getObject() {
        return this.object;
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

package smartSpaces.Pandora;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.IntStream;

import smartSpaces.Pandora.Game.Map.GameMap;
import smartSpaces.Pandora.Game.Map.MapObject;
import smartSpaces.Pandora.Game.Map.ObjectType;
import smartSpaces.Pandora.Game.Panel;
import smartSpaces.Pandora.Game.Player;
import smartSpaces.Pandora.Game.Tasks.LocationTask;
import smartSpaces.Pandora.Game.Tasks.MotionActivityType;

import static org.junit.Assert.*;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void LocationChecker(){
        Location location = new Location(1,1);
        Location location2 = new Location(1,2);
        assertEquals(location.isLocation(1,1), true);
        assertEquals(location2.isLocation(1,1), false);
        assertEquals(location.isLocation(1,2), false);
        assertEquals(location2.isLocation(1,2), true);
    }

    @Test
    public void GameMapChecker(){
        int x = 5;
        int y = 5;
        GameMap map = new GameMap(x,y);

        assertEquals(map.getMapDimensions().length, 2 );
        assertEquals(map.getMapDimensions()[0], x);
        assertEquals(map.getMapDimensions()[1], y);
        for (int i = 0; i < x; i++) {
            for(int j = 0; j < y; j++){
                Location location = new Location(i,j);
                map.setInvisible(location);
            }
        }
        assertEquals(map.getVisibleList().size(), 0);
        assertEquals(map.getInvisibleList().size(), x*y);
        for (int i = 0; i < x; i++) {
            for(int j = 0; j < y; j++){
                Location location = new Location(i,j);
                map.setVisible(location);
            }
        }
        assertEquals(map.getVisibleList().size(), x*y);
        assertEquals(map.getInvisibleList().size(), 0);

        GameMap mapdim = new GameMap(5);
        assertEquals(mapdim.getMapDimensions().length , 2);

    }

    @Test
    public void createPlayer(){
        Player player = new Player(1, false);
        assertEquals(1, player.getId());
        Location location = new Location(1,2);
        int x = 5;
        int y = 5;
        GameMap map = new GameMap(x,y);
        map.setPlayerLocation(location,player);
        HashMap<Player, Location> players =  map.getPlayerLocations();
        assertEquals(1, players.get(player).getX());
        assertEquals(2, players.get(player).getY());
    }

    @Test
    public  void createObjects(){
        //create location for adding object to the map
        Location location = new Location(1,2);
        int x = 5;
        int y = 5;

        //create map and object for on the map
        GameMap map = new GameMap(x,y);
        ObjectType object = ObjectType.LOCK;
        MapObject mobject = new MapObject(object);

        assertEquals(object.getResource(), 2);
        assertEquals(object.getMotionActivityType(), MotionActivityType.PICK_LOCK);
        assertEquals(object.isHazard(), false);

        //add object to the map
        map.addObject(mobject, location);
        assertEquals(mobject, map.getObjects().get(0));
        assertEquals(1,map.getObjects().size());

        //remove object from map
        map.removeObject(location);
        assertEquals(0,map.getObjects().size());

        //add object to map
        map.addObject(mobject, location);

        //get location by the object
        Location objectlocation = map.getObjectLocation(mobject);
        assertEquals(location, objectlocation);

        //get location by object type
        objectlocation = map.getObjectLocationFromType(object);
        assertEquals(location, objectlocation);

        //get mapobject from location
        MapObject objecttest = map.getObjectFromLocation(location);
        assertEquals(objecttest, mobject);
        assertEquals(objecttest.getResource(), 2);
        assertEquals(objecttest.getName(), "LOCK");

        //remove object
        map.removeObject(location);

        //checks if there are no objects on the map
        Location objectlocationn = map.getObjectLocation(mobject);
        assertEquals(null, objectlocationn);
        objectlocation = map.getObjectLocationFromType(object);
        assertEquals(null, objectlocation);
    }

    @Test
    public void  createPanel(){
        Panel panel = new Panel(1,"rotate", "plate");
        assertEquals(panel.getId(), 1);
        boolean randomword = Arrays.asList(panel.getVERBS()).contains(panel.getVerb());
        assertEquals(randomword, true);
        boolean randomobject = Arrays.asList(panel.getOBJECTS()).contains(panel.getObject());
        assertEquals(randomobject, true);
        Panel paneltwo = new Panel(2);
        boolean randomwordtest = Arrays.asList(panel.getVERBS()).contains(paneltwo.getVerb());
        boolean randomobjecttest = Arrays.asList(panel.getOBJECTS()).contains(panel.getObject());
        assertEquals(randomobjecttest, true);
        assertEquals(randomwordtest, true);

    }

    @Test
    public void createPlayerAndPanels(){
        Panel panel = new Panel(1);
        Panel panel2 = new Panel(2);
        Panel[] panels = new Panel[]{panel,panel2};
        Player player = new Player(1, false, panels);
        player.setPanels(panels);
        assertEquals(panels, player.getPanels());
        boolean isready = player.isReady();
        assertEquals(isready, true);
        Panel panel3 = new Panel(3);
        player.setPanel(1, panel3);
        assertEquals(player.getPanels().length, 2);


    }
}
package smartSpaces.Pandora;

import org.junit.Test;

import smartSpaces.Pandora.Game.Map.GameMap;

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
                map.setInvisible(i,j);
            }
        }
        assertEquals(map.getVisibleList().size(), 0);
        assertEquals(map.getInvisibleList().size(), x*y);
        for (int i = 0; i < x; i++) {
            for(int j = 0; j < y; j++){
                map.setVisible(i,j);
            }
        }
        assertEquals(map.getVisibleList().size(), x*y);
        assertEquals(map.getInvisibleList().size(), 0);

    }
}
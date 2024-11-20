package fr.univlille.s3.S302.model;

import fr.univlille.s3.S302.utils.DistanceEuclidienne;
import fr.univlille.s3.S302.utils.DistanceManhattan;
import fr.univlille.s3.S302.utils.Observable;
import fr.univlille.s3.S302.utils.Observer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestDataManager {
    private final static String PATH = "testIris.csv";
    private List<Iris> iris;
    private DataManager<Data> dataManager;

    @BeforeEach
    public void setUp() {
        iris = new ArrayList<>();
        dataManager = DataManager.getInstance();
    }

//    @Test
//    public void testGetInstance(){
//        assertNotNull(dataManager);
//        //assertEquals(dataManager, DataManager.getInstance());
//    }

//    @Test
//    public void testLoadData() {
//        dataManager.loadData(PATH);
//        assertEquals(4, dataManager.getDataList().size());
//        dataManager.loadData(null);
//    }

//    @Test
//    public void testGetDataList() {
//        assertEquals(new ArrayList<>(), dataManager.getDataList());
//        dataManager.loadData(PATH);
//        assertNotEquals(iris, dataManager.getDataList());
//    }

    @Test
    void testAddData() {
        assertEquals(4, dataManager.getDataList().size());
        Map<String, Number> maps = new HashMap<>();
        maps.put("Test", 1);
        maps.put("Test2", 2);
        maps.put("Test3", 3);
        maps.put("Test4", 4);
        dataManager.addData(maps);
        assertEquals(1, dataManager.getUserDataList().size());
    }

    @Test
    void testRemoveData() {
        Iris iris = new Iris();
        dataManager.addData(iris);
        assertEquals(5, dataManager.getDataList().size());
        dataManager.removeData(iris);
        assertEquals(4, dataManager.getDataList().size());
    }

    @Test
    void testAddUserData() {
        assertEquals(0, dataManager.getUserDataList().size());
        dataManager.addUserData(null);
        assertEquals(1, dataManager.getUserDataList().size());
    }

    @Test
    void testCategorizeData() {
        Iris test = new Iris();
        test.sepalWidth = 1.0;
        test.sepalLength = 1.0;
        test.petalWidth = 1.0;
        test.petalLength = 1.0;
        test.category = "Unknown";
        dataManager.addUserData(test);
        dataManager.categorizeData(new DistanceManhattan());
        assertEquals("1.2", test.category);
    }

    @Test
    void testGuessCategory() {
        Iris iri1 = new Iris();
        iri1.petalLength = 1.0;
        iri1.petalWidth = 1.0;
        iri1.sepalLength = 1.0;
        iri1.sepalWidth = 1.0;
        iri1.category = "1.0";
        iri1.species = "1";
        dataManager.addData(iri1);
        Map<String, Number> maps = new HashMap<>();
        maps.put("petalLength", 1.0);
        maps.put("petalWidth", 1.01);
        maps.put("sepalLength", 1.0);
        maps.put("sepalWidth", 1.0);
        maps.put("species", 1.0);
        maps.put("category", 1.0);

        // execute le test résulte un NullPointerException, execute pas a pas en débug test passe
        assertEquals("1.0", dataManager.guessCategory(maps, new DistanceEuclidienne()));
    }

    @Test
    void testGetNearestData() {
        DataManager<Data> dtM = DataManager.getInstance();
        Map<String, Number> maps = new HashMap<>();
        maps.put("Test", 1);
        FakeData fd1 = new FakeData(maps, "Test");
        maps.put("Test", 1.01);
        FakeData fd2 = new FakeData(maps, "Test");
        maps.put("Test", 0.9);
        FakeData fd3 = new FakeData(maps, "Test");
        dtM.addData(fd1);
        assertEquals(fd1, dtM.getNearestData(fd2, new DistanceEuclidienne()));
        dtM.addData(fd2);
        assertEquals(fd1, dtM.getNearestData(fd2, new DistanceEuclidienne()));
    }

    @Test
    void testIsUserData(){
        Iris findMe = new Iris();
        dataManager.addUserData(findMe);
        assertTrue(dataManager.isUserData(findMe));

        assertFalse(dataManager.isUserData(new Iris()));
    }

//    @Test
//    void attach() {
//        DataManager<Data> dtIris = DataManager.getInstance();
//        assertEquals(0, dtIris.getObservers().size());
//        dtIris.attach(new Observer<Iris>() {
//            @Override
//            public void update(Observable<Iris> ob) {}
//            @Override
//            public void update(Observable<Iris> ob, Iris elt) {}
//        });
//        assertEquals(1, dtIris.getObservers().size());
//    }

    @Test
    void testNotifyAllObservers() {
        final int[] count = {0};
        Observer dtIris = new Observer() {
            @Override
            public void update(Observable ob) {
                count[0]++;
            }
            @Override
            public void update(Observable ob, Object elt) {
                count[0]++;
            }

        };
        dataManager.attach(dtIris);
        dataManager.attach(dtIris);
        dataManager.notifyAllObservers();
        assertEquals(2, count[0]);
    }

    @Test
    void testNotifyAllObserversWithElement() {
        final int[] count = {0};
        Observer dtIris = new Observer() {
            @Override
            public void update(Observable ob) {
                count[0]++;
            }
            @Override
            public void update(Observable ob, Object elt) {
                count[0]++;
            }
        };
        dataManager.attach(dtIris);
        dataManager.attach(dtIris);
        dataManager.notifyAllObservers(null);
        assertEquals(2, count[0]);
    }

    @Test
    void getDataList() {
    }

    @Test
    void getUserDataList() {
    }

    @Test
    void valueOf() {
    }

    @Test
    void nextColor() {
    }

    @Test
    void createColor() {
    }
}

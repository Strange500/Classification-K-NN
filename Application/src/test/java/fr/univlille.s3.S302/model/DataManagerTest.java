package fr.univlille.s3.S302.model;

import fr.univlille.s3.S302.utils.DistanceEuclidienne;
import fr.univlille.s3.S302.utils.Observable;
import fr.univlille.s3.S302.utils.Observer;
import fr.univlille.s3.S302.model.Data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import java.util.*;

import static fr.univlille.s3.S302.model.DataLoader.preLoadClasses;
import static org.junit.jupiter.api.Assertions.*;

public class DataManagerTest {
    private final static String PATH = "iris.csv";
    private DataManager<Data> dataManager;

    @BeforeEach
    public void setUp() {
        DataLoader.registerHeader(Iris.class, "\"sepal.length\",\"sepal.width\",\"petal.length\",\"petal.width\",\"variety\"");
        dataManager = DataManager.getInstance();
        dataManager.reset();
        try {
            dataManager.loadData(PATH);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    @Test
    public void testGetInstance(){
        assertEquals(dataManager, DataManager.getInstance());
    }

    @Test
    public void testValueOf() {
        // TODO
    }

    @Test
    public void testGetDataList() {
        assertNotEquals(null, dataManager.getDataList());
    }

    @Test
    public void testLoadData() {
        dataManager.loadData(PATH);
        assertEquals(150, dataManager.getDataList().size());
    }

    @Test
    void classifyData() {
        // TODO, waiting method implementation
    }

    @Test
    void TestAttach() {
        DataManager<Data> dtData = DataManager.getInstance();
        Observer ob = new Observer() {
            @Override
            protected void update(Observable ob) {

            }

            @Override
            protected void update(Observable ob, Object elt) {

            }

        };
        dtData.attach(ob);
        assertTrue(dtData.getObservers().contains(ob));
    }

    @Test
    void testNotifyAllObservers() {
        final int[] count = {0};
        Observer dtData = new Observer() {
            @Override
            public void update(Observable ob) {
                count[0]++;
            }
            @Override
            public void update(Observable ob, Object elt) {
                count[0]++;
            }

        };
        dataManager.attach(dtData);
        dataManager.attach(dtData);
        dataManager.notifyAllObservers();
        assertEquals(2, count[0]);
    }

    @Test
    void testNotifyAllObserversWithElement() {
        final int[] count = {0};
        Observer dtData = new Observer() {
            @Override
            public void update(Observable ob) {
                count[0]++;
            }
            @Override
            public void update(Observable ob, Object elt) {
                count[0]++;
            }
        };
        dataManager.attach(dtData);
        dataManager.attach(dtData);
        dataManager.notifyAllObservers(null);
        assertEquals(2, count[0]);
    }



    @Test
    void testAddUserData() {
        Data data = new FakeData(new HashMap<>(), "Test");
        dataManager.addUserData(data);
        assertTrue(dataManager.getUserDataList().contains(data));
    }

    @Test
    void testRemoveUserData() {
        Data Data = new FakeData(new HashMap<>(), "Test");
        dataManager.addData(Data);
        assertTrue(dataManager.getDataList().contains(Data));
        dataManager.removeData(Data);
        assertFalse(dataManager.getDataList().contains(Data));
    }



    @Test
    void testAddData() {
        Data Data = new FakeData(new HashMap<>(), "Test");
        dataManager.addData(Data);
        assertTrue(dataManager.getDataList().contains(Data));
    }





    @Test
    void testCategorizeData() {
        //TODO
    }

    @Test
    void testGuessCategory()  {
        Data iri1 = new FakeData(new HashMap<>(){{
            put("petalWidth", 1.0);
            put("sepalLength", 1.0);
            put("sepalWidth", 1.0);
            put("species", 1.0);
        }}, "petalLength");
        assertEquals("5.0", dataManager.guessCategory(iri1.getAttributes(), new DistanceEuclidienne()));
    }

    @Test
    void testGetNearestDatas() {
        Data iri1 = new FakeData(new HashMap<>(){{
            put("petalWidth", 1.0);
            put("sepalLength", 1.0);
            put("sepalWidth", 1.0);
            put("species", 1.0);
        }}, "petalLength");
        List<Data> nearestData = dataManager.getNearestDatas(iri1, new DistanceEuclidienne(), 1);
        assertEquals(1, nearestData.size());
    }

    @Test
    void testIsUserData(){

        Data iri1 = new FakeData(new HashMap<>(){{
            put("petalWidth", 1.0);
            put("sepalLength", 1.0);
            put("sepalWidth", 1.0);
            put("species", 1.0);
        }}, "petalLength");
        Data iri2 = new FakeData(new HashMap<>(){{
            put("petalWidth", 1.0);
            put("sepalLength", 1.0);
            put("sepalWidth", 1.0);
            put("species", 2);
        }}, "petalLength");

        assertFalse(dataManager.isUserData(iri1));
        assertFalse(dataManager.isUserData(iri2));

        dataManager.addUserData(iri1.getAttributes());
        dataManager.addData(iri2);

        List<Data> dataList = dataManager.getDataList();
        dataList.add(0, dataManager.getUserDataList().get(0));

        assertTrue(dataManager.isUserData(dataList.get(0)));
        assertFalse(dataManager.isUserData(dataList.get(1)));


    }
}

package fr.univlille.s3.S302.model;

import fr.univlille.s3.S302.utils.Observable;
import fr.univlille.s3.S302.utils.Observer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class DataManagerTest {
    private final static String PATH = "testIris.csv";
    private List<Iris> iris;
    private DataManager dataManager;

    @BeforeEach
    public void setUp() {
        iris = new ArrayList<>();
        dataManager = new DataManager(iris);
    }

    @Test
    public void testGetDataList() {
        assertEquals(iris, dataManager.getDataList());
        dataManager.loadData(PATH);
        assertNotEquals(iris, dataManager.getDataList());
    }

    @Test
    public void testLoadData() {
        dataManager.loadData(PATH);
        assertEquals(4, dataManager.getDataList().size());
        dataManager.loadData(null);
    }

    @Test
    void classifyData() {
        // TODO, waiting method implementation
    }

    @Test
    void attach() {
        DataManager<Iris> dtIris = new DataManager();
        assertEquals(0, dtIris.getObservers().size());
        dtIris.attach(new Observer<Iris>() {
            @Override
            public void update(Observable<Iris> ob) {}
            @Override
            public void update(Observable<Iris> ob, Iris elt) {}
        });
        assertEquals(1, dtIris.getObservers().size());
    }

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
        dataManager.notifyAllObservers(new Iris(0,0,0,0,"Test"));
        assertEquals(2, count[0]);
    }

    @Test
    void addData() {
        assertEquals(0, dataManager.getDataList().size());
        Map<String, Number> maps = new HashMap<>();
        maps.put("Test", 1);
        maps.put("Test2", 2);
        maps.put("Test3", 3);
        maps.put("Test4", 4);
        dataManager.addData(maps);
        assertEquals(1, dataManager.getUserDataList().size());

        // ce addData affecte une autre liste donc une taille de 1 est normale
        dataManager.addData(new Iris(0, 0, 0, 0, "Test"));
        assertEquals(1, dataManager.getDataList().size());
    }

    @Test
    void testAddUserData() {
        assertEquals(0, dataManager.getUserDataList().size());
        dataManager.AddUserData(new Iris(0,0,0,0,"Test"));
        assertEquals(1, dataManager.getUserDataList().size());
    }

    @Test
    void testRemoveUserData() {
        Iris iri = new Iris(0, 0, 0, 0, "Test");
        dataManager.addData(iri);
        assertEquals(1, dataManager.getDataList().size());
        dataManager.removeData(iri);
        assertEquals(0, dataManager.getDataList().size());
    }

    @Test
    void testSetDataList() {
        assertEquals(0, dataManager.getDataList().size());
        ArrayList<Iris> irisArrayList = new ArrayList<>();
        irisArrayList.add(new Iris(0,0,0,0,"Test"));
        dataManager.setDataList(irisArrayList);
        assertEquals(1, dataManager.getDataList().size());
    }
}

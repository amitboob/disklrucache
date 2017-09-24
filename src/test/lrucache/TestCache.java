package test.lrucache;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.internal.collections.Pair;

import main.lrucache.Cache;

public class TestCache {

    CategoryCache catCache;
    StrCache strCache;

    //cache file on disk will be generated in current projects directory on disk
    final String DIR_PATH = "./testcache/";

    @BeforeTest
    public void beforeTest() {
        System.out.println("Prepare data");
        File directory = new File(DIR_PATH.concat("catCache"));
        catCache = CategoryCache.getCategoryCache(directory, 15);

        File strdirectory = new File(DIR_PATH.concat("strCache"));
        strCache = StrCache.getStrCache(strdirectory, 4);

    }

    @Test(priority = 0) // create cache first time
    public void testStringObject() {

        strCache.clearCache();
        System.out.println("Creating cache");
        strCache.printCache();
        strCache.put("A", "av");
        assertEquals("av", strCache.get("A"));

        strCache.put("B", "bv");
        strCache.put("C", "cv");
        strCache.put("D", "dv");
        strCache.put("E", "ev");

        assertNull(strCache.get("A"));

        assertNotNull(strCache.get("B"));

        strCache.put("A", "av");

        assertNull(strCache.get("C")); // check for lru order

        System.out.println("cache created");
        strCache.printCache();
    }

    @Test(priority = 1) // read from disk cache
    public void testDiskStringObject() {

        System.out.println("get created cache ");
        strCache.printCache();

        StrCache.clearMemoryCache();

        assertNull(strCache.get("C"));
        System.out.println("cache destroyed");
        strCache.printCache();

        File strdirectory = new File(DIR_PATH.concat("strCache"));
        strCache = StrCache.getStrCache(strdirectory, 4);

        assertNotNull(strCache.get("A"));
        assertNotNull(strCache.get("B"));
        System.out.println("get cache from disk");
        strCache.printCache();
    }

    @Test(priority = 2)
    public void testDiskStringThreadedObject() {

        strCache.clearCache();
        List<Pair<String, String>> inputList = new ArrayList<>();
        List<Pair<String, String>> outputList = new ArrayList<>();

        List<Boolean> lock = new ArrayList<>();

        inputList.add(new Pair<String, String>("AB", "t10"));
        inputList.add(new Pair<String, String>("AC", "t11"));
        inputList.add(new Pair<String, String>("AD", "t12"));
        inputList.add(new Pair<String, String>("BB", "t13"));
        inputList.add(new Pair<String, String>("BT", "t14"));
        inputList.add(new Pair<String, String>("TB", "t15"));

        Thread t1 = new Thread(new Runnable() {

            @Override
            public void run() {

                for (Pair<String, String> pair : inputList) {
                    synchronized (lock) {

                        try {
                            while (!lock.isEmpty()) {
                                lock.wait();
                            }
                            strCache.put(pair.first(), pair.second());

                            lock.add(true);
                            lock.notify();
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }

                }

            }
        });

        Thread t2 = new Thread(new Runnable() {

            @Override
            public void run() {

                for (Pair<String, String> pair : inputList) {

                    synchronized (lock) {

                        try {
                            while (lock.isEmpty()) {
                                lock.wait();
                            }
                            outputList.add(new Pair<String, String>(pair.first(), strCache.get(pair.first())));

                            lock.clear();
                            lock.notify();
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }

                }

            }
        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(4, strCache.size());

        assertEquals(inputList.size(), outputList.size());

        for (int i = 0; i < inputList.size(); i++) {
            assertEquals(inputList.get(i), outputList.get(i));
        }
    }

    @Test
    public void testNonStringValueObject() {

        ArrayList<Category> cats = TestUtils.getCats();
        ArrayList<ArrayList<SubCategory>> list = new ArrayList<>();

        for (int i = 0; i < cats.size(); i++) {

            ArrayList<SubCategory> subCats = TestUtils.getSubCats(cats.get(i));
            list.add(subCats);
            catCache.put(cats.get(i), subCats);
        }

        for (int i = 0; i < cats.size(); i++) {

            for (int j = 0; j < list.get(i).size(); j++) {
                assertEquals(list.get(i).get(j), catCache.get(cats.get(i)).get(j));
            }
        }

    }

}

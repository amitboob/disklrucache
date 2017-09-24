package test.lrucache;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.io.File;
import java.util.ArrayList;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import main.lrucache.Cache;

public class TestCache {

    Cache<Category, ArrayList<SubCategory>> catCache;
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

package test.lrucache;

import java.io.File;
import java.util.ArrayList;

import main.lrucache.DiskCache;

public class CategoryCache extends DiskCache<Category, ArrayList<SubCategory>> {

    private static CategoryCache CACHE;

    private CategoryCache(File directory, int size) {
        super(directory, size);
    }

    public static CategoryCache getCategoryCache(File directory, int size) {

        if (CACHE == null) {

            synchronized (CategoryCache.class) {
                if (CACHE == null) {
                    CACHE = new CategoryCache(directory, size);
                }
            }
        }

        return CACHE;
    }

    public static CategoryCache getCategoryCache() {
        return CACHE;
    }

}

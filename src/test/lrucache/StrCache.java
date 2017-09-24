package test.lrucache;

import java.io.File;

import main.lrucache.DiskCache;

public class StrCache extends DiskCache<String, String> {

    private static StrCache CACHE;

    private StrCache(File directory, int size) {
        super(directory, size);
    }

    public static StrCache getStrCache(File directory, int size) {

        if (CACHE == null) {

            synchronized (CategoryCache.class) {
                if (CACHE == null) {
                    CACHE = new StrCache(directory, size);
                }
            }
        }

        return CACHE;
    }

    public static StrCache getStrCache() {
        return CACHE;
    }

    public static void clearMemoryCache(){
        CACHE.clearCache();
        CACHE = null;
    }
}

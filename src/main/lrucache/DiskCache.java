package main.lrucache;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class DiskCache<K, V extends Serializable> implements Cache<K, V> {

    private static final String RECORDS_FILE = "records";
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private File cacheDirectory;
    private File recordsFile;
    private int maxSize; // max no of entries cache can have 
    private Map<Integer, V> cacheEntries;

    private Object fileSystemLock=new Object();

    public DiskCache(File directory, int size) {

        cacheDirectory = directory;
        maxSize = size;
        cacheEntries = new LinkedHashMap<>(maxSize+1, DEFAULT_LOAD_FACTOR, true);
        recordsFile = new File(directory, RECORDS_FILE);

        initializeCache();
    }

    @Override
    public synchronized V get(K key) {
        Objects.requireNonNull(key, "key cannot be null");

        Integer keyHash = key.hashCode();
        V value = cacheEntries.get(keyHash);

        if (value == null) {
            return null;
        } 

        writeRecord(OperationType.ACCESS, keyHash);
        return value;
    }

    @Override
    public synchronized void put(K key, V value) {

        Objects.requireNonNull(key, "key cannot be null");
        Objects.requireNonNull(value, "value cannot be null");

        Integer keyHash = key.hashCode();

        if (cacheEntries.size() >= maxSize) {

            Integer firstKey = cacheEntries.keySet().iterator().next();
            cacheEntries.remove(firstKey);

            File readfile = new File(cacheDirectory, Integer.toString(firstKey));
            readfile.delete();
            writeRecord(OperationType.REMOVE, firstKey);
        }

        File outputFile = new File(cacheDirectory, Integer.toString(keyHash));
        writeValueToFile(outputFile, value);
        writeRecord(OperationType.ACCESS, keyHash);

        cacheEntries.put(keyHash, value);
    }

    // async call to write the operation performed, so as to maintain LRU
    private void writeRecord(OperationType op, Integer key) {

        new Thread(new Runnable() {


            @Override
            public void run() {

                synchronized(fileSystemLock){

                    try (BufferedWriter out = new BufferedWriter(new FileWriter(recordsFile, true))) {
                        out.write(op.name() + " " + Integer.toString(key));
                        out.newLine();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }).start();


    }

    private void writeValueToFile(File file, V value) {

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {

            oos.writeObject(value);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @SuppressWarnings("unchecked")
    private void initializeCache() {

        //create directory if not presents
        cacheDirectory.mkdirs();

        try (BufferedReader in = new BufferedReader(new FileReader(recordsFile))) {

            String sCurrentLine;

            while ((sCurrentLine = in.readLine()) != null) {
                String[] content = sCurrentLine.split(" ");

                if (content[0].equals(OperationType.ACCESS.name())) {
                    File readfile = new File(cacheDirectory, content[1]);

                    int hashedValue = Integer.valueOf(readfile.getName());

                    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(readfile))) {

                        V value = (V) ois.readObject();
                        cacheEntries.put(hashedValue, value);

                    } catch (IOException e) {
                        System.out.println("File not found");
                    } catch (ClassNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

        } catch (IOException e1) {
            System.out.println("recordsFile not found");
        }

        try (FileWriter fw = new FileWriter(recordsFile);
                BufferedWriter bw = new BufferedWriter(new FileWriter(recordsFile, true))) {

            for (Integer key : cacheEntries.keySet()) {
                writeRecord(OperationType.ACCESS, key);
            }

        } catch (Exception ex) {

        }
    }

    @Override
    public void printCache() {

        for (Map.Entry<Integer, V> entry : cacheEntries.entrySet()) {
            System.out.println("Key =" + entry.getKey() + " Value=" + entry.getValue());
        }
    }

    public void clearCache(){
        cacheEntries.clear();
    }

    public int size(){
        return cacheEntries.size();
    }

    static enum OperationType {
        ACCESS, REMOVE // ACCESS denotes entry was accessed (get or put)
                       //REMOVE if entry is explicity removed aur removed due to lru
    }
}

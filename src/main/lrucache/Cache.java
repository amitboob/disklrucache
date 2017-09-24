package main.lrucache;

import java.io.Serializable;

public interface Cache<K, V extends Serializable> {

    V get(K key);

    void put(K key, V value);

    void printCache();

}
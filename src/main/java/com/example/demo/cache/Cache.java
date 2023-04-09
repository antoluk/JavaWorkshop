package com.example.demo.cache;

import java.util.*;

import com.example.demo.logic.SinIntegral;
import org.springframework.stereotype.Component;

@Component
public class Cache {

    private final Map<Integer, SinIntegral> cacheMap = new HashMap<>();

    public void put(Integer key, SinIntegral value) {
        cacheMap.put(key, value);
    }

    public SinIntegral get(Integer key) {
        return cacheMap.get(key);
    }

    public void remove(Integer key) {
        cacheMap.remove(key);
    }

    public void clear() {
        cacheMap.clear();
    }

    public Boolean contains(int value) {
        return this.cacheMap.containsKey(value);
    }
}

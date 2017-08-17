package ua.kyiv.kpi.fpm.km32.kohut.entity;

import ua.kyiv.kpi.fpm.km32.kohut.exception.NotExistKeyException;

/**
 * Created by i.kohut on 8/16/2017.
 */
public class HashMapOpenAddressing implements Map {

    private static final double LOAD_FACTOR = 0.75;

    private Integer[] keys;
    private long[] values;

    private int size;
    private int capacity;
    private int threshold;

    public HashMapOpenAddressing(int capacity) {
        this.capacity = capacity;
        this.keys = new Integer[capacity];
        this.values = new long[capacity];
        this.threshold = (int) (LOAD_FACTOR * capacity);
    }

    @Override
    public void put(int key, long value) {
        int keyHashCode = getHashCode(key);
        int keyOffset = getKeyOffset(key);

        while (this.keys[keyHashCode] != null) {

            // override the value of the key
            if (this.keys[keyHashCode] == key) {
                this.size--;
                break;
            }
            keyHashCode = (keyHashCode + keyOffset) % this.capacity;
        }

        this.keys[keyHashCode] = key;
        this.values[keyHashCode] = value;
        this.size++;

        if (this.size > this.threshold) {
            changeMapCapacity();
        }
    }

    @Override
    public long get(int key) throws NotExistKeyException {
        int keyHashCode = getHashCode(key);
        int keyOffset = getKeyOffset(key);

        while (this.keys[keyHashCode] != null) {

            if (this.keys[keyHashCode] == key) {
                return this.values[keyHashCode];
            }
            keyHashCode = (keyHashCode + keyOffset) % this.capacity;
        }

        throw new NotExistKeyException("Not found the value of the key " + key);
    }

    @Override
    public int size() {
        return this.size;
    }

    private void changeMapCapacity() {
        int newCapacity = getPrime(2 * this.capacity);

        Integer[] oldKeys = this.keys;
        long[] oldValues = this.values;

        this.keys = new Integer[newCapacity];
        this.values = new long[newCapacity];
        this.size = 0;
        this.capacity = newCapacity;
        this.threshold = (int) (LOAD_FACTOR * newCapacity);

        for (int i = 0; i < oldKeys.length; i++) {
            if (oldKeys[i] != null) {
                put(oldKeys[i], oldValues[i]);
            }
        }
    }

    private int getPrime(int number) {
        while (true) {
            if (isPrime(++number)) {
                return number;
            }
        }
    }

    private boolean isPrime(int number) {
        for (int j = 2; j*j <= number; j++) {
            if (number % j == 0) {
                return false;
            }
        }

        return true;
    }

    private int getHashCode(int key) {
        return (key & 0x7ffffff) % this.capacity;
    }

    private int getKeyOffset(int key) {
        return 5 - key % 5;
    }
}
package ua.kyiv.kpi.fpm.km32.kohut.domain;

import ua.kyiv.kpi.fpm.km32.kohut.exception.NotExistKeyException;

public class HashMapOpenAddressing implements Map {

    private static final double LOAD_FACTOR = 0.75;

    private final int freeKey = 0;

    private int[] keys;
    private long[] values;

    private long freeKeyValue;
    private boolean existFreeKey;

    private int size;
    private int capacity;
    private int threshold;

    public HashMapOpenAddressing(int capacity) {
        this.capacity = capacity;
        this.keys = new int[capacity];
        this.values = new long[capacity];
        this.threshold = (int) (LOAD_FACTOR * capacity);
    }

    @Override
    public void put(final int key, final long value) {
    	if (key == freeKey) {
    		freeKeyValue = value;

    		if (!existFreeKey) {
    			size++;
    			existFreeKey = true;
    		}

    		return;
    	}

        int keyHashCode = getHashCode(key);
        final int keyOffset = getKeyOffset(key);

        while (keys[keyHashCode] != freeKey) {

            // override the value of the key
            if (keys[keyHashCode] == key) {
                size--;
                break;
            }

            keyHashCode = (keyHashCode + keyOffset) % capacity;
        }

        keys[keyHashCode] = key;
        values[keyHashCode] = value;
        size++;

        if (size > threshold) {
            changeMapCapacity();
        }
    }

    @Override
    public long get(final int key) throws NotExistKeyException {
    	if (key == freeKey) {
    		if (existFreeKey) {
    			return freeKeyValue;
    		} else {
    			throw new NotExistKeyException("Map does not have the key: " + key);
    		}
    	}

        int keyHashCode = getHashCode(key);
        final int keyOffset = getKeyOffset(key);

        while (keys[keyHashCode] != freeKey) {

            if (keys[keyHashCode] == key) {
                return values[keyHashCode];
            }

            keyHashCode = (keyHashCode + keyOffset) % capacity;
        }

        throw new NotExistKeyException("Map does not have the key: " + key);
    }

    @Override
    public int size() {
        return size;
    }

    private void changeMapCapacity() {
        final int newCapacity = getPrimeNumberGreaterThan(2 * capacity);

        final int[] oldKeys = keys;
        final long[] oldValues = values;

        keys = new int[newCapacity];
        values = new long[newCapacity];
        size = existFreeKey ? 1 : 0;
        capacity = newCapacity;
        threshold = (int) (LOAD_FACTOR * newCapacity);

        for (int i = 0; i < oldKeys.length; i++) {
            if (oldKeys[i] != freeKey) {
                put(oldKeys[i], oldValues[i]);
            }
        }
    }

    private int getPrimeNumberGreaterThan(final int number) {

        int primeNumber = number;

        while (true) {
            if (isPrime(++primeNumber)) {
                return primeNumber;
            }
        }
    }

    private boolean isPrime(final int number) {
        for (int j = 2; j*j <= number; j++) {
            if (number % j == 0) {
                return false;
            }
        }

        return true;
    }

    private int getHashCode(final int key) {
        return (key & 0x7ffffff) % capacity;
    }

    private int getKeyOffset(final int key) {
        return 5 - key % 5;
    }
}
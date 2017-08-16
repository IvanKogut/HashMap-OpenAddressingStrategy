package ua.kyiv.kpi.fpm.km32.kohut.entity;

import ua.kyiv.kpi.fpm.km32.kohut.exception.NotExistKeyException;

/**
 * Created by i.kohut on 8/16/2017.
 */
public interface Map {

    void put(int key, long value);
    long get(int key) throws NotExistKeyException;
    int size();
}
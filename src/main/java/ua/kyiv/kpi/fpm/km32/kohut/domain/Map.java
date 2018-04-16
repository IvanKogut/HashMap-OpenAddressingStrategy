package ua.kyiv.kpi.fpm.km32.kohut.domain;

import ua.kyiv.kpi.fpm.km32.kohut.exception.NotExistKeyException;

public interface Map {

    void put(int key, long value);

    long get(int key) throws NotExistKeyException;

    int size();
}
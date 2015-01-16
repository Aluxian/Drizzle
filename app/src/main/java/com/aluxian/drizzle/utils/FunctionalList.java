package com.aluxian.drizzle.utils;

import java.util.ArrayList;
import java.util.List;

public class FunctionalList<E> {

    private List<E> mList;

    public FunctionalList(List<E> collection) {
        mList = collection;
    }

    @SuppressWarnings("Convert2streamapi")
    public <T> List<T> map(Function<E, T> mapper) {
        List<T> result = new ArrayList<>();

        for (E item : mList) {
            result.add(mapper.apply(item));
        }

        return result;
    }

    @SuppressWarnings("Convert2streamapi")
    public List<E> filter(Function<E, Boolean> mapper) {
        List<E> result = new ArrayList<>();

        for (E item : mList) {
            if (mapper.apply(item)) {
                result.add(item);
            }
        }

        return result;
    }

    public static interface Function<F, T> {
        public T apply(F input);
    }

}

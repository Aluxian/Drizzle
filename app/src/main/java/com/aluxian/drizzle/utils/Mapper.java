package com.aluxian.drizzle.utils;

import java.util.ArrayList;
import java.util.List;

public class Mapper {

    @SuppressWarnings("Convert2streamapi")
    public static <I, O> List<O> map(List<I> items, FunctionInOut<I, O> fn) {
        List<O> results = new ArrayList<>();

        for (I item : items) {
            results.add(fn.apply(item));
        }

        return results;
    }

}

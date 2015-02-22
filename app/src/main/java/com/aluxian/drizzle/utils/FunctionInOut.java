package com.aluxian.drizzle.utils;

public interface FunctionInOut<I, O> {

    /**
     * Apply the function with the given argument.
     *
     * @param arg An argument.
     */
    public O apply(I arg);

}

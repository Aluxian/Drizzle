package com.aluxian.drizzle.utils;

public interface FunctionAR<I, O> {

    /**
     * Apply the function with the given argument.
     *
     * @param arg An argument.
     */
    public O apply(I arg);

}

package com.vilya.farm.util.function;

@FunctionalInterface
public interface ThrowableFunction<T, R> {

  R apply(T t) throws Throwable;
}

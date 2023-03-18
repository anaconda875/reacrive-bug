package com.vilya.farm.util.function;

@FunctionalInterface
public interface ThrowableConsumer<T> {

  void accept(T t) throws Throwable;
}

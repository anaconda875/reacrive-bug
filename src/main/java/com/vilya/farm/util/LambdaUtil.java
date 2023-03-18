package com.vilya.farm.util;

import com.vilya.farm.util.function.ThrowableConsumer;
import com.vilya.farm.util.function.ThrowableFunction;

import java.util.function.Consumer;
import java.util.function.Function;

public class LambdaUtil {

  private LambdaUtil() {}

  public static <T> Consumer<T> asQuietConsumer(ThrowableConsumer<T> c) {
    return t -> {
      try {
        c.accept(t);
      } catch (Throwable ignored) {
      }
    };
  }

  public static <T> Consumer<T> asSneakyThrowConsumer(ThrowableConsumer<T> c) {
    return t -> {
      try {
        c.accept(t);
      } catch (Throwable e) {
        sneakyThrow(e);
      }
    };
  }

  public static <T, R> Function<T, R> asSneakyThrowFunction(ThrowableFunction<T, R> f) {
    return t -> {
      try {
        return f.apply(t);
      } catch (Throwable e) {
        return sneakyThrow(e);
      }
    };
  }

  static <E extends Throwable, R> R sneakyThrow(Throwable t) throws E {
    throw (E) t;
  }
}

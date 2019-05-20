package org.adbcj.support;


public interface OneArgFunction<TArgument, TReturn> {
  TReturn apply(TArgument arg);

  @SuppressWarnings("rawtypes")
  OneArgFunction ID_FUNCTION = arg -> arg;
}



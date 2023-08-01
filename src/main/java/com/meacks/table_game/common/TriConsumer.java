package com.meacks.table_game.common;

@FunctionalInterface
public interface TriConsumer<O, T, U> {
    void accept(O object, T t, U u);
}

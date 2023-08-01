package com.meacks.table_game.common.gameRules;

public interface IGameTriggers {
    public int getValue();
    public <E extends IGameTriggers> E fromValue(int value);
}

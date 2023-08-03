package com.meacks.table_game.common.gameRules;

public interface IGameTriggers {
    int getValue();
    
    <E extends IGameTriggers> E fromValue(int value);
}

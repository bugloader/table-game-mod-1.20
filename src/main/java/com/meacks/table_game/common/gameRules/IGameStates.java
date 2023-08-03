package com.meacks.table_game.common.gameRules;

public interface IGameStates {
    int getValue();
    <E extends IGameStates> E fromValue(int value);

}

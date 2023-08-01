package com.meacks.table_game.common.gameRules;

public interface IGameStates {
    public int getValue();
    public <E extends IGameStates> E fromValue(int value);

}

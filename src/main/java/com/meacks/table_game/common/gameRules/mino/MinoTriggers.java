package com.meacks.table_game.common.gameRules.mino;

import com.meacks.table_game.common.gameRules.IGameTriggers;

public enum MinoTriggers implements IGameTriggers {
    ENTER_CONFIG(0), CANCEL_START(10), GAME_START(20),
    NEXT_PEOPLE(30), ALL_READY(40), RUN_OUT_OF_CARDS(50),
    SHUFFLE_DONE(60), PLAYER_WIN(70), CALC_DONE(80);
    
    private final int value;
    
    MinoTriggers(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    public MinoTriggers fromValue(int value) {
        for (MinoTriggers trigger : MinoTriggers.values()) {
            if (trigger.value == value) {
                return trigger;
            }
        }
        throw new IllegalArgumentException("Unknown trigger: " + value);
    }
}

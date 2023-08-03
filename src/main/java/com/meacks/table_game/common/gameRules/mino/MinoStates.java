package com.meacks.table_game.common.gameRules.mino;

import com.meacks.table_game.common.gameRules.IGameStates;

public enum MinoStates implements IGameStates {
    // When no one is using desk
    STALE(0), // When game host is configuring game
    CONFIG(10), // When drawing initial cards
    INIT_DRAW(20), // When game is running
    GAME_ROUND(30), // When reshuffling cards
    RESHUFFLE(40), // When game ends, we calc scores
    CALC_SCORES(50);
    
    private final int value;
    
    MinoStates(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    
    @Override
    public MinoStates fromValue(int value) {
        for (MinoStates state : MinoStates.values()) {
            if (state.value == value) {
                return state;
            }
        }
        throw new IllegalArgumentException("Unknown state: " + value);
    }
    
    
}

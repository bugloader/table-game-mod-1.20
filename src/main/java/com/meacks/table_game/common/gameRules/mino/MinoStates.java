package com.meacks.table_game.common.gameRules.mino;

import com.meacks.table_game.common.gameRules.IGameStates;

public enum MinoStates implements IGameStates {
    // When no one is using desk
    STALE,
    // When game host is configuring game
    CONFIG,
    // When drawing initial cards
    INIT_DRAW,
    // When game is running
    GAME_ROUND,
    // When reshuffling cards
    RESHUFFLE,
    // When game ends, we calc scores
    CALC_SCORES,
}

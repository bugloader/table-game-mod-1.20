package com.meacks.table_game.engine;

import java.util.List;
import java.util.Optional;

/**
 * This class is the interface of all the game rules.
 * It defines several methods that is critical for defining a set of game rules using automata.
 * During one game, this class will be used to guiding the game loop.
 */
public abstract class GameRuleAutomata {
    protected List<TableGamePlayer> players;

    public GameRuleAutomata(List<TableGamePlayer> players){
        this.players = players;
    }

    public List<TableGamePlayer> getPlayers() {
        return players;
    }

    public abstract Optional<TableGamePlayer> getWinner();
    public abstract boolean isGameOver();
    public abstract GameRuleAutomata next();
    public abstract int getTurn();
    public abstract int getScore(TableGamePlayer player);
    public abstract void halt();
}

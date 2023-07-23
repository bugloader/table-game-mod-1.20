package com.meacks.table_game.games;

import com.meacks.table_game.engine.GameRuleAutomata;
import com.meacks.table_game.engine.TableGamePlayer;

import java.util.List;
import java.util.Optional;

public class MinoGameAutomata extends GameRuleAutomata {

    public MinoGameAutomata(List<TableGamePlayer> players) {
        super(players);
    }

    public List<TableGamePlayer> getPlayers() {
        return players;
    }

    public Optional<TableGamePlayer> getWinner() {
        return Optional.empty();
    }

    public boolean isGameOver() {
        return false;
    }

    public GameRuleAutomata next() {
        return this;
    }

    public int getTurn() {
        return 0;
    }

    public int getScore(TableGamePlayer player) {
        return 0;
    }

    public void halt() {

    }
}

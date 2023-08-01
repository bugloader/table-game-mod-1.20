package com.meacks.table_game.common.gameRules;

import nl.erasmusmc.mgz.parallelstateless4j.IStateMachineContext;
import nl.erasmusmc.mgz.parallelstateless4j.StateMachine;
import nl.erasmusmc.mgz.parallelstateless4j.configuration.MachineConfigurer;

public abstract class AbstractGameRule<S extends IGameStates,T extends IGameTriggers> {
    protected final StateMachine<S, T> stateMachine;
    public AbstractGameRule(IStateMachineContext<S, T> context) {
        this.stateMachine = new StateMachine<>(getMachineConfigurer(), context);
    }

    protected abstract MachineConfigurer<S, T> getMachineConfigurer();
}

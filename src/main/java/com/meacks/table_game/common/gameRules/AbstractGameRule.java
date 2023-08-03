package com.meacks.table_game.common.gameRules;

import nl.erasmusmc.mgz.parallelstateless4j.IStateMachineContext;
import nl.erasmusmc.mgz.parallelstateless4j.StateMachine;
import nl.erasmusmc.mgz.parallelstateless4j.configuration.MachineConfigurer;
import nl.erasmusmc.mgz.parallelstateless4j.delegates.StateAccessor;
import nl.erasmusmc.mgz.parallelstateless4j.delegates.StateMutator;

public abstract class AbstractGameRule<S extends IGameStates, T extends IGameTriggers> {
    protected final StateMachine<S, T> stateMachine;
    
    protected AbstractGameRule(StateAccessor<S> accessor,
                               StateMutator<S> mutator,
                               MachineConfigurer<S, T> configurer,
                               IStateMachineContext<S, T> context) {
        this.stateMachine = new StateMachine<>(accessor, mutator, configurer, context);
    }
    
    
}

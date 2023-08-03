package com.meacks.table_game.common.gameRules.mino;

import com.meacks.table_game.common.gameRules.AbstractGameRule;
import com.mojang.logging.LogUtils;
import nl.erasmusmc.mgz.parallelstateless4j.IStateMachineContext;
import nl.erasmusmc.mgz.parallelstateless4j.configuration.MachineConfigurer;
import nl.erasmusmc.mgz.parallelstateless4j.delegates.StateAccessor;
import nl.erasmusmc.mgz.parallelstateless4j.delegates.StateMutator;
import org.slf4j.Logger;

public class MinoGameRule extends AbstractGameRule<MinoStates, MinoTriggers> {
    
    private static final Logger LOGGER = LogUtils.getLogger();
    
    public MinoGameRule(StateAccessor<MinoStates> accessor,
                        StateMutator<MinoStates> mutator,
                        MachineConfigurer<MinoStates, MinoTriggers> configurer,
                        IStateMachineContext<MinoStates, MinoTriggers> context) {
        super(accessor, mutator, configurer, context);
    }
    
    public static MachineConfigurer<MinoStates, MinoTriggers> getConfigurer() {
        var configurer = new MachineConfigurer<MinoStates, MinoTriggers>();
        for (MinoStates state : MinoStates.values()) {
            configurer.configure(state);
        }
        configurer.setInitialState(MinoStates.STALE);
        
        configurer.configure(MinoStates.STALE)
                  .permit(MinoTriggers.ENTER_CONFIG, MinoStates.CONFIG);
        configurer.configure(MinoStates.CONFIG)
                  .permit(MinoTriggers.CANCEL_START, MinoStates.STALE);
        return configurer;
    }
}

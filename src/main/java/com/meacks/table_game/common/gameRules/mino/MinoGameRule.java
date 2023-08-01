package com.meacks.table_game.common.gameRules.mino;
import com.meacks.table_game.common.gameRules.AbstractGameRule;
import com.mojang.logging.LogUtils;
import nl.erasmusmc.mgz.parallelstateless4j.IStateMachineContext;
import nl.erasmusmc.mgz.parallelstateless4j.configuration.MachineConfigurer;
import org.slf4j.Logger;

public class MinoGameRule extends AbstractGameRule<MinoStates, MinoTriggers> {

    private static final Logger LOGGER = LogUtils.getLogger();

    public MinoGameRule(IStateMachineContext<MinoStates, MinoTriggers> context) {
        super(context);
    }

    @Override
    protected MachineConfigurer<MinoStates, MinoTriggers> getMachineConfigurer() {
        MachineConfigurer<MinoStates, MinoTriggers> config = new MachineConfigurer<>();
        return config;
    }
}

package com.meacks.table_game.common.gameRules.mino;
import com.mojang.logging.LogUtils;
import nl.erasmusmc.mgz.parallelstateless4j.configuration.MachineConfigurer;
import org.slf4j.Logger;

public class MinoGameRule {
    private MachineConfigurer<MinoStates, MinoTriggers> machineConfigurer;

    private static int count = 0;
    private static final Logger LOGGER = LogUtils.getLogger();
    public MinoGameRule(){
        this.machineConfigurer = new MachineConfigurer<>();
        LOGGER.error(count++ + "");
    }

}

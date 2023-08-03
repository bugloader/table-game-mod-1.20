package com.meacks.table_game.assets.blockEntities;

import com.meacks.table_game.assets.handlers.BlockEntityHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
public class MinoTableBlockEntity extends MinoCommonBlockEntity{
    public static final int RENDERING_CARD_NUM=10;
    public MinoTableBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state,RENDERING_CARD_NUM,BlockEntityHandler.minoTableBlockEntity.get());
    }
}
package com.meacks.table_game.assets.blockEntities;

import com.meacks.table_game.assets.handlers.BlockEntityHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SmallGameTableBlockEntity extends BlockEntity {
    public SmallGameTableBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityHandler.smallGameTableBlockEntity.get(), pos, state);
    }

}

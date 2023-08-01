package com.meacks.table_game.assets.blockEntities;

import com.meacks.table_game.assets.handlers.BlockEntityHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class UnoTableExtenderBlockEntity extends BlockEntity {
    public UnoTableExtenderBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityHandler.unoTableExtenderBlockEntity.get(), pos, state);
    }

}
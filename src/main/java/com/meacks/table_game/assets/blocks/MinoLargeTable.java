package com.meacks.table_game.assets.blocks;

import com.meacks.table_game.assets.blockEntities.MinoLargeTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MinoLargeTable extends MinoCommonTable {
    public MinoLargeTable() {super();}
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        return new MinoLargeTableBlockEntity(pos, blockState);
    }
}
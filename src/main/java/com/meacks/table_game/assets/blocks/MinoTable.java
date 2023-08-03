package com.meacks.table_game.assets.blocks;

import com.meacks.table_game.assets.blockEntities.MinoTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class MinoTable extends MinoCommonTable {
    public MinoTable() {super();}
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        return new MinoTableBlockEntity(pos, blockState);
    }
}

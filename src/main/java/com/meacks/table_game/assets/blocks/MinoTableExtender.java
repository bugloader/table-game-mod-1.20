package com.meacks.table_game.assets.blocks;

import com.meacks.table_game.assets.blockEntities.MinoTableExtenderBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MinoTableExtender extends BaseEntityBlock {
        private static VoxelShape shape = Block.box(0, 14, 0, 16, 15, 16);

        public MinoTableExtender() {
            super(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instabreak().instrument(NoteBlockInstrument.BELL));
        }

        @Override
        @SuppressWarnings("deprecation")
        public @NotNull VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
            return shape;
        }

        @Nullable
        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState blockState) {
            return new MinoTableExtenderBlockEntity(pos, blockState);
        }

        @Nullable
        @Override
        public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
            return super.getTicker(p_153212_, p_153213_, p_153214_);
        }

        @Nullable
        @Override
        public <T extends BlockEntity> GameEventListener getListener(ServerLevel p_221121_, T p_221122_) {
            return super.getListener(p_221121_, p_221122_);
        }
}

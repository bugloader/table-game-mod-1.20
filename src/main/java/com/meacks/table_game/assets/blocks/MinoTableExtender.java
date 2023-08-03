package com.meacks.table_game.assets.blocks;

import com.meacks.table_game.assets.blockEntities.MinoTableExtenderBlockEntity;
import com.meacks.table_game.assets.handlers.BlockHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MinoTableExtender extends BaseEntityBlock {
    private static final VoxelShape shape = Block.box(0, 14, 0, 16, 15, 16);
    
    public MinoTableExtender() {
        super(BlockBehaviour.Properties.of()
                                       .mapColor(MapColor.WOOD)
                                       .instabreak()
                                       .instrument(NoteBlockInstrument.BELL));
    }
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState blockState) {
        return new MinoTableExtenderBlockEntity(pos, blockState);
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_,
                                                                  BlockState p_153213_,
                                                                  BlockEntityType<T> p_153214_) {
        return super.getTicker(p_153212_, p_153213_, p_153214_);
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> GameEventListener getListener(ServerLevel p_221121_, T p_221122_) {
        return super.getListener(p_221121_, p_221122_);
    }
    
    public @NotNull InteractionResult use(@NotNull BlockState state,
                                          @NotNull Level level,
                                          @NotNull BlockPos pos,
                                          @NotNull Player player,
                                          @NotNull InteractionHand hand,
                                          @NotNull BlockHitResult hit) {
        BlockPos[] tempPoses = {pos.east(), pos.west(), pos.south(), pos.north(), pos.east().south(),
                                pos.east().north(), pos.west().south(), pos.west().north()};
        for (BlockPos tempPose : tempPoses) {
            if (BlockHandler.areSameBlockType(level.getBlockState(tempPose)
                                                   .getBlock(), BlockHandler.mino_table)) {
                return level.getBlockState(tempPose)
                            .getBlock()
                            .use(state, level, tempPose, player, hand, hit);
            }
            if (BlockHandler.areSameBlockType(level.getBlockState(tempPose)
                                                   .getBlock(), BlockHandler.mino_large_table)) {
                return level.getBlockState(tempPose)
                            .getBlock()
                            .use(state, level, tempPose, player, hand, hit);
            }
        }
        return InteractionResult.PASS;
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public @NotNull VoxelShape getShape(BlockState state,
                                        BlockGetter blockGetter,
                                        BlockPos pos,
                                        CollisionContext collisionContext) {
        return shape;
    }
}

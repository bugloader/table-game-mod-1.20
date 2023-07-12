package com.meacks.table_game.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;


public class SmallGameTable extends Block {
    private static VoxelShape shape = Block.box(0, 7, 0, 16, 9, 16);

    public SmallGameTable(){
        super(Properties.of().mapColor(MapColor.WOOD).instabreak().instrument(NoteBlockInstrument.BASEDRUM));
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        return shape;
    }
}

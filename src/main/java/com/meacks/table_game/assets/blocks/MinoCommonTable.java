package com.meacks.table_game.assets.blocks;

import com.meacks.table_game.assets.blockEntities.MinoCommonBlockEntity;
import com.meacks.table_game.assets.handlers.ItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.meacks.table_game.assets.items.MinoHandCard.getBasicStack;

public class MinoCommonTable extends BaseEntityBlock {
    private static final VoxelShape shape = Block.box(0, 14, 0, 16, 15, 16);

    public MinoCommonTable() {
        super(Properties.copy(Blocks.SPRUCE_WOOD).mapColor(MapColor.WOOD).instabreak()
                .noParticlesOnBreak().instrument(NoteBlockInstrument.BELL));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        return null;
    }

    @SuppressWarnings("deprecation")
    public @NotNull InteractionResult use(@NotNull BlockState state,
                                          @NotNull Level level,
                                          @NotNull BlockPos pos,
                                          @NotNull Player player,
                                          @NotNull InteractionHand hand,
                                          @NotNull BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (player.isShiftKeyDown()) {
            tryStartGame(level, pos);
            return InteractionResult.SUCCESS;
        } else {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.is(Items.CREEPER_HEAD)) {//0
                if (changeColor(0, level, pos, player)) stack.setCount(stack.getCount() - 1);
                return InteractionResult.SUCCESS;
            } else if (stack.is(Items.ORANGE_DYE)) {//2
                if (changeColor(2, level, pos, player)) stack.setCount(stack.getCount() - 1);
                return InteractionResult.SUCCESS;
            } else if (stack.is(Items.REDSTONE)) {//3
                if (changeColor(3, level, pos, player)) stack.setCount(stack.getCount() - 1);
                return InteractionResult.SUCCESS;
            } else if (stack.is(Items.DIAMOND)) {//1
                if (changeColor(1, level, pos, player)) stack.setCount(stack.getCount() - 1);
                return InteractionResult.SUCCESS;
            } else if (stack.is(Items.AIR)) {
                MinoCommonBlockEntity blockEntity = (MinoCommonBlockEntity) level.getBlockEntity(pos);
                assert blockEntity != null;
                if (blockEntity.inGame) tryGetInitialCard(player, hand, level, pos);
                else player.openMenu(blockEntity);
                return InteractionResult.SUCCESS;
            }
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter blockGetter,
                                        @NotNull BlockPos pos, @NotNull CollisionContext collisionContext) {
        return shape;
    }

    public static void tryStartGame(Level level, BlockPos blockPos) {
        if (level.isClientSide()) return;
        MinoCommonBlockEntity entity = (MinoCommonBlockEntity) level.getBlockEntity(blockPos);
        assert entity != null;
        if (!entity.inGame) entity.gameInitialize();
    }

    public static boolean changeColor(int clr, Level level, BlockPos blockPos, Player player) {
        MinoCommonBlockEntity entity = (MinoCommonBlockEntity) level.getBlockEntity(blockPos);
        assert entity != null;
        NonNullList<ItemStack> items = player.getInventory().items;
        for (ItemStack item : items)
            if (item.is(ItemHandler.mino_hand_card.get()) &&
                    entity.validSignature(item) && entity.validId(item)) return entity.changeColor(clr);
        return false;
    }

    public static void tryGetInitialCard(Player player, InteractionHand hand, Level level, BlockPos blockPos) {
        NonNullList<ItemStack> itemStacks = player.getInventory().items;
        if (level.isClientSide()) return;
        for (ItemStack itemStack : itemStacks) if (itemStack.is(ItemHandler.mino_hand_card.get())) return;
        MinoCommonBlockEntity entity = (MinoCommonBlockEntity) level.getBlockEntity(blockPos);
        assert entity != null;
        if (entity.stepInitialGive()) {
            ItemStack stack = entity.dealPlayerCards(7, getBasicStack());
            player.setItemInHand(hand, entity.signature(stack));
        }
    }

}
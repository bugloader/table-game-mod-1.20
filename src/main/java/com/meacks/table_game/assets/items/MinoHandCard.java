package com.meacks.table_game.assets.items;

import com.meacks.table_game.TableGameMod;
import com.meacks.table_game.assets.blockEntities.MinoLargeTableBlockEntity;
import com.meacks.table_game.assets.blockEntities.MinoTableBlockEntity;
import com.meacks.table_game.assets.handlers.BlockHandler;
import com.meacks.table_game.assets.handlers.ItemHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.meacks.table_game.assets.handlers.EntityHandler.summonFireWork;

public class MinoHandCard extends Item {

    //informational methods
    public static final String RESOURCE_PATH_ROOT = TableGameMod.MODID + ":textures/item/mino/mino";
    public static final String[] COLORS = {"creeper", "diamond", "ocelot", "redstone"};
    public static final String[] EXTRA_TYPES = {"draw2", "reverse", "skip"};
    public static final int CARD_TYPES_COUNT = 55;

    public static final String MINO_CARD_TRANS_PREFIX = "item." + TableGameMod.MODID + ".mino";
    //-3:0 uno
    //-2:1 wild
    //-1:2 wild-draw4
    //0~9:3 digit
    //10:4 draw2
    //11:5 reverse
    //12:6 skip
    public static final int[] CATEGORY = {0, 1, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 5, 6};

    public MinoHandCard() {
        super(new Item.Properties().stacksTo(1));
    }

    public static ItemStack getBasicStack() {
        ItemStack stack = ItemHandler.mino_hand_card.get().getDefaultInstance();
        stack.setTag(basicTag());
        return stack;
    }

    public static CompoundTag basicTag() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("index", 0);
        nbt.putInt("0", 1);
        for (int i = 1; i < CARD_TYPES_COUNT; i++) nbt.putInt(Integer.toString(i), 0);
        return nbt;
    }

    public static ItemStack popCard(@NotNull ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        int id = getCurrentCardId(stack);
        nbt.putInt(Integer.toString(id), nbt.getInt(Integer.toString(id)) - 1);
        stack.setTag(nbt);
        stack.setHoverName(Component.translatable(MINO_CARD_TRANS_PREFIX + getCurrentCardName(stack))
                .append(Component.translatable("table_game.text.comma_player", nbt.getInt("id"))));
        return stack;
    }

    public static int getCurrentCardId(ItemStack stack) {
        ArrayList<Integer> cardsList = basicCardList();
        CompoundTag nbt = stack.getOrCreateTag();
        int index = nbt.getInt("index");
        for (int i = 1; i < CARD_TYPES_COUNT; i++) {
            int currentNum = nbt.getInt(Integer.toString(i));
            if (currentNum != 0) cardsList.add(i);
        }
        if (index >= cardsList.size() || index < 0) {
            nbt.putInt("index", 0);
            index = 0;
            stack.setTag(nbt);
        }
        return cardsList.get(index);
    }

    public static String getCurrentCardName(ItemStack stack) {
        return getCardName(getCurrentCardId(stack));
    }

    public static ArrayList<Integer> basicCardList() {
        ArrayList<Integer> cardsList = new ArrayList<>();
        cardsList.add(0);
        return cardsList;
    }

    public static String getCardName(int id) {
        if (id == 0) return "";
        if (id == 1) return "-wild";
        if (id == 2) return "-wild-draw4";
        int colorId = (id - 3) / 13;
        int typeId = (id - 3) % 13;
        if (typeId < 10) return "-" + COLORS[colorId] + '-' + typeId;
        return "-" + COLORS[colorId] + '-' + EXTRA_TYPES[typeId - 10];
    }

    public static int getRemainCardNum(@NotNull ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        int num = 0;
        for (int i = 1; i < CARD_TYPES_COUNT; i++) num += nbt.getInt(Integer.toString(i));
        return num;
    }

    //-1,0~3
    public static int getCardColor(int id) {
        if (id < 3) return -1;
        return (id - 3) / 13;
    }

    public static boolean areSameType(int id1, int id2) {
        return getCardType(id1) == getCardType(id2);
    }

    //-3~-1: uno, wild, wild-draw4
    //0~9
    //10~12: draw2 reverse skip
    public static int getCardType(int id) {
        if (id < 3) return id - 3;
        return (id - 3) % 13;
    }

    public static boolean containingColor(int clr, ItemStack stack) {
        CompoundTag nbt = stack.getTag();//(id-3)/13
        assert nbt != null;
        for (int i = clr * 13 + 3; i < clr * 13 + 16; i++) if (nbt.getInt(Integer.toString(i)) != 0) return true;
        return false;
    }


    public static int getCardCategory(int id) {
        int type = getCardType(id);
        return CATEGORY[type + 3];
    }

    public static String getCurrentCardTexture(ItemStack stack) {
        return RESOURCE_PATH_ROOT + getCurrentCardName(stack) + ".png";
    }

    //testing methods
    public static ItemStack makeFullStackOfCard(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        if (nbt == null) nbt = basicTag();
        for (int i = 1; i < CARD_TYPES_COUNT; i++) nbt.putInt(Integer.toString(i), 1);
        stack.setTag(nbt);
        return stack;
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext useOnContext) {
        BlockPos clickedBlockPos = useOnContext.getClickedPos();
        Level level = useOnContext.getLevel();
        Block clickedBlock = level.getBlockState(clickedBlockPos)
                .getBlock();
        Player player = useOnContext.getPlayer();
        boolean placed = false;
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (player == null) return InteractionResult.PASS;
        if (BlockHandler.areSameBlockType(clickedBlock, BlockHandler.mino_table)) {
            placed = ((MinoTableBlockEntity) Objects.requireNonNull(level.getBlockEntity(clickedBlockPos))).useCard(
                    useOnContext);
        } else if (BlockHandler.areSameBlockType(clickedBlock, BlockHandler.mino_large_table)) {
            placed =
                    ((MinoLargeTableBlockEntity) Objects.requireNonNull(level.getBlockEntity(clickedBlockPos))).useCard(
                            useOnContext);
        } else if (BlockHandler.areSameBlockType(clickedBlock, BlockHandler.mino_table_extender)) {
            BlockPos[] tempPoses = {clickedBlockPos.east(), clickedBlockPos.west(), clickedBlockPos.south(),
                    clickedBlockPos.north(), clickedBlockPos.east().south(),
                    clickedBlockPos.east().north(), clickedBlockPos.west().south(),
                    clickedBlockPos.west().north()};
            for (BlockPos tempPose : tempPoses) {
                if (BlockHandler.areSameBlockType(level.getBlockState(tempPose)
                        .getBlock(), BlockHandler.mino_table)) {
                    placed = ((MinoTableBlockEntity) Objects.requireNonNull(level.getBlockEntity(tempPose))).useCard(
                            useOnContext);
                    break;
                }
                if (BlockHandler.areSameBlockType(level.getBlockState(tempPose)
                        .getBlock(), BlockHandler.mino_large_table)) {
                    placed =
                            ((MinoLargeTableBlockEntity) Objects.requireNonNull(level.getBlockEntity(tempPose))).useCard(
                                    useOnContext);
                    break;
                }
            }
        } else if (BlockHandler.areSameBlockType(clickedBlock, BlockHandler.small_game_table)) {
            //makeFullStackOfCard(useOnContext.getItemInHand());
            summonFireWork(useOnContext);
            //SoundHandler.playSound(useOnContext,0);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level,
                                                           @NotNull Player player,
                                                           @NotNull InteractionHand interactionHand) {
        ItemStack stack = player.getItemInHand(interactionHand);
        if (level.isClientSide) return InteractionResultHolder.success(stack);
        switchCard(stack, player.isShiftKeyDown());
        return InteractionResultHolder.success(stack);
    }

    //functional methods
    @Override
    public void appendHoverText(@NotNull ItemStack stack,
                                @Nullable Level level,
                                @NotNull List<Component> list,
                                @NotNull TooltipFlag flag) {
        ArrayList<Integer> cardsList = basicCardList();
        CompoundTag nbt = stack.getTag();
        if (nbt == null) {
            nbt = basicTag();
            stack.setTag(nbt);
        }
        int index = nbt.getInt("index");
        for (int i = 1; i < CARD_TYPES_COUNT; i++) {
            for (int j = 0; j < nbt.getInt(Integer.toString(i)); j++) {
                cardsList.add(i);
            }
        }
        int counter = 0;
        int last = 0;
        list.add(Component.literal("---------").withStyle(ChatFormatting.DARK_GRAY));
        for (int temp : cardsList) {
            if (last != temp) {
                last = temp;
                counter++;
            }
            if (counter == index) {
                list.add(Component.translatable(MINO_CARD_TRANS_PREFIX + getCardName(temp))
                        .withStyle(ChatFormatting.AQUA));
            } else {
                list.add(Component.translatable(MINO_CARD_TRANS_PREFIX + getCardName(temp)));
            }
        }
        super.appendHoverText(stack, level, list, flag);
    }

    public static void switchCard(ItemStack stack, boolean decrease) {
        CompoundTag nbt = stack.getTag();
        if (nbt == null) {
            nbt = basicTag();
        } else {
            int currentIndex = nbt.getInt("index");
            int counter = 1;
            for (int i = 1; i < CARD_TYPES_COUNT; i++) if (nbt.getInt(Integer.toString(i)) != 0) counter++;
            if (decrease) {
                nbt.putInt("index", (currentIndex + counter - 1) % counter);
            } else {
                nbt.putInt("index", (currentIndex + 1) % counter);
            }
        }
        stack.setTag(nbt);
        stack.setHoverName(Component.translatable(MINO_CARD_TRANS_PREFIX + getCurrentCardName(stack))
                .append(Component.translatable("table_game.text.comma_id", nbt.getInt("id"))));
    }

}

package com.meacks.table_game.items;

import com.meacks.table_game.handlers.BlockHandler;
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
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class MinoHandCard extends Item {

    public MinoHandCard(){
        super(new Item.Properties().stacksTo(1));
    }
    //informational methods
    public static final String RESOURCE_PATH_ROOT ="table_game:textures/item/mino/uno";
    public static final String[] COLORS = {"creeper","diamond","ocelot","redstone"};
    public static final String[] TYPES = {"draw2","reverse","skip"};

    public static final int CARDS_COUNT = 55;
    public static String getCardName(int id){
        if(id==0)return "";
        if(id==1)return "-wild";
        if(id==2)return "-wild-draw4";
        int colorId=(id-3)/13;
        int typeId=(id-3)%13;
        if(typeId<10) return "-"+ COLORS[colorId]+'-'+Integer.toString(typeId);
        return "-"+ COLORS[colorId]+'-'+TYPES[typeId-10];
    }

    public static ArrayList<Integer> basicCardList(){
        ArrayList<Integer> cardsList = new ArrayList<>();
        cardsList.add(0);
        return cardsList;
    }

    public static CompoundTag basicTag(){
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("index",0);
        nbt.putBoolean("0",true);
        for (int i = 1; i < CARDS_COUNT; i++) nbt.putBoolean(Integer.toString(i),false);
        return nbt;
    }

    public static int getCurrentCardId(ItemStack stack) {
        ArrayList<Integer> cardsList = basicCardList();
        CompoundTag nbt = stack.getTag();
        int id=0;
        if (nbt != null) {
            for (int i = 1; i < CARDS_COUNT; i++) if(nbt.getBoolean(Integer.toString(i))) cardsList.add(i);
            id = nbt.getInt("index");
        }
        stack.setTag(nbt);
        return cardsList.get(id);
    }
    public static String getCurrentCardName(ItemStack stack){
        return getCardName(getCurrentCardId(stack));
    }
    public static String getCurrentCardPath(ItemStack stack){return RESOURCE_PATH_ROOT+getCurrentCardName(stack)+".png";}
    //functional methods
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> list, @NotNull TooltipFlag flag) {
        ArrayList<Integer> cardsList = basicCardList();
        CompoundTag nbt = stack.getTag();
        if (nbt == null){
            nbt=basicTag();
            stack.setTag(nbt);
        }
        int index=nbt.getInt("index");
        for (int i = 1; i < CARDS_COUNT; i++) if(nbt.getBoolean(Integer.toString(i))) cardsList.add(i);
        for (int i = 0;i < cardsList.size();i++) {
            if(i==index) list.add(Component.translatable(getCardName(cardsList.get(i))).withStyle(ChatFormatting.AQUA));
            else list.add(Component.translatable(getCardName(cardsList.get(i))));
        }
        super.appendHoverText(stack,level,list,flag);
    }


    public void switchCard(ItemStack stack,boolean decrease){
        CompoundTag nbt = stack.getTag();
        if (nbt == null) nbt = basicTag();
        else {
            int currentIndex = nbt.getInt("index");
            int counter=1;
            for (int i = 1; i < CARDS_COUNT; i++) if(nbt.getBoolean(Integer.toString(i))) counter++;
            if(decrease) nbt.putInt("index",(currentIndex+counter-1)%counter);
            else nbt.putInt("index",(currentIndex+1)%counter);
        }
        stack.setTag(nbt);
        System.out.println("1:"+getCurrentCardId(stack));
        stack.setHoverName(Component.translatable(getCurrentCardName(stack)));
    }


    //testing methods
    public void makeFullStackOfCard(ItemStack stack){
        CompoundTag nbt = stack.getTag();
        if (nbt == null) nbt = basicTag();
        for (int i = 1; i < CARDS_COUNT; i++) nbt.putBoolean(Integer.toString(i),true);
        stack.setTag(nbt);
    }


    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext useOnContext) {
        BlockPos clickedBlockPos = useOnContext.getClickedPos();
        Level level = useOnContext.getLevel();
        Block clickedBlock = level.getBlockState(clickedBlockPos).getBlock();
        Player player = useOnContext.getPlayer();
        if(level.isClientSide) return InteractionResult.SUCCESS;
        if(player==null) return InteractionResult.PASS;
        if(BlockHandler.areSameBlockType(clickedBlock,Blocks.AIR)){

        } else if (BlockHandler.areSameBlockType(clickedBlock,BlockHandler.small_game_table.get())) {
            makeFullStackOfCard(useOnContext.getItemInHand());
            System.out.println(2);
        }
        return InteractionResult.SUCCESS;
    }
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player,
                                                           @NotNull InteractionHand interactionHand) {
        ItemStack stack=player.getItemInHand(interactionHand);
        if(level.isClientSide) return InteractionResultHolder.success(stack);
        switchCard(stack,player.isShiftKeyDown());
        System.out.println(1);
        return InteractionResultHolder.success(stack);
    }

}
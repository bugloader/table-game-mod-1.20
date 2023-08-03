package com.meacks.table_game.assets.blockEntities;

import com.meacks.table_game.assets.handlers.EntityHandler;
import com.meacks.table_game.assets.handlers.ItemHandler;
import com.meacks.table_game.assets.handlers.SoundHandler;
import com.meacks.table_game.assets.items.MinoHandCard;
import com.meacks.table_game.common.inventory.MinoTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Objects;

import static com.meacks.table_game.TableGameMod.random;
import static com.meacks.table_game.assets.items.MinoHandCard.*;

public class MinoCommonBlockEntity extends BaseContainerBlockEntity implements Container {
    public final int RENDERING_CARD_NUM;
    private NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
    
    public MinoCommonBlockEntity(BlockPos pos, BlockState state, int RENDERING_CARD_NUM, BlockEntityType blockEntityType
                                ) {
        super(blockEntityType, pos, state);
        this.RENDERING_CARD_NUM = RENDERING_CARD_NUM;
//        MinoGameRule rule = new MinoGameRule(
//                this::getState, this::setState, MinoGameRule.getConfigurer(), this
//        );
//        this.setGameRule(rule);
        initialize();
    }
    
    public void initialize() {
        CompoundTag tableNbt = getPersistentData();
        tableNbt.putInt("numPlaced", 0);
        tableNbt.putInt("drawDeckNum", 0);
        tableNbt.putInt("action", 1);
        tableNbt.putInt("id", 0);
        tableNbt.putInt("dir", 1);
        tableNbt.putInt("clr", -1);
        // will be true when click start
        tableNbt.putBoolean("inGame", false);
        // 0~9 be one set
        tableNbt.putInt("numCreeperSet", 2);//2
        tableNbt.putInt("numDiamondSet", 2);//2
        tableNbt.putInt("numOcelotSet", 2);//2
        tableNbt.putInt("numRedstoneSet", 2);//2
        // 4 colors 1 each be one set
        tableNbt.putInt("numSkipSet", 2);//2
        tableNbt.putInt("numReverseSet", 2);//2
        tableNbt.putInt("numDraw2Set", 1);//2
        tableNbt.putInt("numDraw4Set", 1);//1
        tableNbt.putInt("numWildSet", 1);//1
        saveAdditional(tableNbt);
    }
    
    public boolean shouldPlace() {
        return getPersistentData().getInt("action") == 2;
    }
    
    public boolean shouldDeal() {
        int action = getPersistentData().getInt("action");
        return action == 3 || action == 4;
    }
    
    public boolean shouldCheckColor() {
        CompoundTag tableNbt = getUpdateTag();
        return (tableNbt.getInt("action") == 7 || tableNbt.getInt("action") == 6) &&
               tableNbt.getInt("preId") != tableNbt.getInt("id");
    }
    
    public boolean validCallUno(ItemStack stack) {
        CompoundTag itemNbt = stack.getOrCreateTag();
        return itemNbt.getInt("id") == getPersistentData().getInt("preId") &&
               MinoHandCard.getCurrentCardId(stack) == 0 && getPersistentData().getBoolean("preShouldUno");
    }
    
    public boolean validCatchUno(ItemStack stack) {
        return MinoHandCard.getCurrentCardId(stack) == 0 && getPersistentData().getBoolean("preShouldUno") &&
               !getPersistentData().getBoolean("preUno");
    }
    
    public boolean chooseToChallenge(ItemStack stack) {
        return MinoHandCard.getCurrentCardId(stack) == 0;
    }
    
    public boolean validId(ItemStack stack) {
        CompoundTag cardNbt = stack.getTag();
        CompoundTag compoundTag = getPersistentData();
        assert cardNbt != null;
        return cardNbt.getInt("id") == compoundTag.getInt("id");
    }
    
    public boolean validCard(ItemStack stack) {
        if (MinoHandCard.getCurrentCardId(stack) == 0) return false;
        CompoundTag compoundTag = getPersistentData();
        int currentId = MinoHandCard.getCurrentCardId(stack);
        int numPlaced = compoundTag.getInt("numPlaced");
        int temp = numPlaced - 1;
        int id = compoundTag.getInt(Integer.toString(temp));
        while (MinoHandCard.getCardCategory(id) < 3) {
            temp--;
            if (temp == -1) return true;
            id = compoundTag.getInt(Integer.toString(temp));
        }
        int clr = MinoHandCard.getCardColor(MinoHandCard.getCurrentCardId(stack));
        return clr == -1 || clr == compoundTag.getInt("clr") || MinoHandCard.areSameType(currentId, id);
    }
    
    public boolean changeColor(int clr) {
        CompoundTag compoundTag = getPersistentData();
        int action = compoundTag.getInt("action");
        if (action == 5 || action == 6) {
            compoundTag.putInt("clr", clr);
            if (action == 5) {
                compoundTag.putInt("action", 2);
            } else {
                compoundTag.putInt("action", 6);
            }
            int peopleSize = compoundTag.getInt("peopleSize");
            compoundTag.putInt("id", (peopleSize + compoundTag.getInt("dir") + compoundTag.getInt("id")) % peopleSize);
            saveAdditional(compoundTag);
            postBlockUpdate();
            SoundHandler.playSound(level, getBlockPos(), 1);
            return true;
        }
        return false;
    }
    
    public void postBlockUpdate() {
        BlockState blockState = getBlockState();
        Objects.requireNonNull(this.getLevel())
               .sendBlockUpdated(this.getBlockPos(), blockState, blockState, 2);
    }
    
    public void callUno(UseOnContext useOnContext) {
        CompoundTag tableNbt = getPersistentData();
        tableNbt.putBoolean("preUno", true);
        tableNbt.putBoolean("preShouldUno", false);
        saveAdditional(tableNbt);
        postBlockUpdate();
        SoundHandler.playSound(useOnContext, 6);
    }
    
    public ItemStack dealPlayerCards(int n, ItemStack stack) {
        CompoundTag itemNbt = stack.getOrCreateTag();
        for (int i = 0; i < n; i++) {
            int temp = dealCard();
            itemNbt.putInt(Integer.toString(temp), itemNbt.getInt(Integer.toString(temp)) + 1);
        }
        stack.setTag(itemNbt);
        postBlockUpdate();
        return stack;
    }
    
    public ItemStack signature(ItemStack itemStack) {
        CompoundTag nbt = itemStack.getTag();
        CompoundTag tableNbt = getPersistentData();
        int id = tableNbt.getInt("id");
        assert nbt != null;
        nbt.putInt("pwd", tableNbt.getInt("pwd"));
        nbt.putInt("id", id);
        itemStack.setTag(nbt);
        if (tableNbt.getInt("action") == 0) {
            tableNbt.putInt("id", id + 1);
        } else {
            tableNbt.putInt("id", 0);
        }
        saveAdditional(tableNbt);
        postBlockUpdate();
        SoundHandler.playSound(level, getBlockPos(), 0);
        return itemStack;
    }
    
    public boolean validSignature(ItemStack itemStack) {
        return itemStack.getOrCreateTag()
                        .getInt("pwd") == getPersistentData().getInt("pwd");
    }
    
    public void gameInitialize() {
        CompoundTag tableNbt = getPersistentData();
        ItemStack stack = items.get(0);
        int peopleSize = 0;
        if (stack.is(ItemHandler.mino_card_shapes.get(0)
                                                 .get())) {
            CompoundTag itemNbt = stack.getTag();
            if (itemNbt != null) {
                tableNbt.putInt("numCreeperSet", itemNbt.getInt("numCreeperSet"));//2
                tableNbt.putInt("numDiamondSet", itemNbt.getInt("numDiamondSet"));//2
                tableNbt.putInt("numOcelotSet", itemNbt.getInt("numOcelotSet"));//2
                tableNbt.putInt("numRedstoneSet", itemNbt.getInt("numRedstoneSet"));//2
                // 4 colors 1 each be one set
                tableNbt.putInt("numSkipSet", itemNbt.getInt("numSkipSet"));//2
                tableNbt.putInt("numReverseSet", itemNbt.getInt("numReverseSet"));//2
                tableNbt.putInt("numDraw2Set", itemNbt.getInt("numDraw2Set"));//2
                tableNbt.putInt("numDraw4Set", itemNbt.getInt("numDraw4Set"));//1
                tableNbt.putInt("numWildSet", itemNbt.getInt("numWildSet"));//1
                peopleSize = itemNbt.getInt("peopleSize");//2
            }
        }
        makeFullDeckOfCard();
        tableNbt.putBoolean("preShouldUno", false);
        tableNbt.putBoolean("inGame", true);
        tableNbt.putInt("peopleSize", Math.max(2, peopleSize));
        tableNbt.putInt("action", 0);
        tableNbt.putInt("pwd", random.nextInt());
        saveAdditional(tableNbt);
        postBlockUpdate();
    }
    
    public void makeFullDeckOfCard() {
        CompoundTag tableNbt = getPersistentData();
        int deckNum = 0;
        CompoundTag nbt = new CompoundTag();
        // 0~9 be one set
        int numCreeperSet = tableNbt.getInt("numCreeperSet");
        int numDiamondSet = tableNbt.getInt("numDiamondSet");
        int numOcelotSet = tableNbt.getInt("numOcelotSet");
        int numRedstoneSet = tableNbt.getInt("numRedstoneSet");
        // 4 colors 1 each be one set
        int numSkipSet = tableNbt.getInt("numSkipSet");
        int numReverseSet = tableNbt.getInt("numReverseSet");
        int numDraw2Set = tableNbt.getInt("numDraw2Set");
        int numDraw4Set = tableNbt.getInt("numDraw4Set");
        int numWildSet = tableNbt.getInt("numWildSet");
        for (int i = 1; i < CARD_TYPES_COUNT; i++) {
            int typeNum = switch (getCardCategory(i)) {
                case 1 -> numWildSet;
                case 2 -> numDraw4Set;
                case 3 -> switch (getCardColor(i)) {
                    case 0 -> numCreeperSet;
                    case 1 -> numDiamondSet;
                    case 2 -> numOcelotSet;
                    case 3 -> numRedstoneSet;
                    default -> 0;
                };
                case 4 -> numDraw2Set;
                case 5 -> numReverseSet;
                case 6 -> numSkipSet;
                default -> 0;
            };
            nbt.putInt(Integer.toString(i), typeNum);
            deckNum += typeNum;
        }
        tableNbt.putInt("drawDeckNum", deckNum);
        tableNbt.put("deck", nbt);
        saveAdditional(tableNbt);
        postBlockUpdate();
        SoundHandler.playSound(level, getBlockPos(), 7);
    }
    
    public boolean shouldGameInitialize() {
        return !getPersistentData().getBoolean("inGame");
    }
    
    public boolean stepInitialGive() {
        CompoundTag tableNbt = getPersistentData();
        boolean result = tableNbt.getInt("action") == 0;
        int id = tableNbt.getInt("id");
        if (result && id == tableNbt.getInt("peopleSize") - 1) {
            tableNbt.putInt("action", 2);
            saveAdditional(tableNbt);
            placeInitialHeadCard();
        }
        return result;
    }
    
    public void placeInitialHeadCard() {
        int id = dealCard();
        int category = getCardCategory(id);
        placeCard(
            id,
            this.getBlockPos()
                .getX() + 0.5f,
            this.getBlockPos()
                .getZ() + 0.5f,
            0
                 );
        while (category == 1 || category == 6) {
            id = dealCard();
            category = getCardCategory(id);
            placeCard(
                id,
                this.getBlockPos()
                    .getX() + 0.5f,
                this.getBlockPos()
                    .getZ() + 0.5f,
                0
                     );
        }
        cardFunctioning_Auto(id);
    }
    
    public int dealCard() {
        CompoundTag currentNbt = this.getPersistentData();
        CompoundTag deckNbt = currentNbt.getCompound("deck");
        int currentSize = currentNbt.getInt("drawDeckNum");
        if (currentSize == 0) makeNewDeckOfCard();
        currentSize = currentNbt.getInt("drawDeckNum");
        int index = random.nextInt(0, currentSize);
        for (int i = 1; i < CARD_TYPES_COUNT; i++) {
            int temp = deckNbt.getInt(Integer.toString(i));
            index -= deckNbt.getInt(Integer.toString(i));
            if (index <= 0 && temp != 0) {
                deckNbt.putInt(Integer.toString(i), deckNbt.getInt(Integer.toString(i)) - 1);
                currentNbt.put("deck", deckNbt);
                currentNbt.putInt("drawDeckNum", currentSize - 1);
                saveAdditional(currentNbt);
                return i;
            }
        }
        return 0;
    }
    
    public void placeCard(int id, double x, double z, float r) {
        CompoundTag tableNbt = getPersistentData();
        int cardsPlacedNum = tableNbt.getInt("numPlaced");
        tableNbt.putInt(Integer.toString(cardsPlacedNum), id);
        tableNbt.putDouble(cardsPlacedNum + "x", x);
        tableNbt.putDouble(cardsPlacedNum + "z", z);
        tableNbt.putFloat(cardsPlacedNum + "r", r);
        tableNbt.putInt("numPlaced", cardsPlacedNum + 1);
        
        saveAdditional(tableNbt);
        //postBlockUpdate();
    }
    
    public void cardFunctioning_Auto(int id) {
        CompoundTag tableNbt = getPersistentData();
        int category = getCardCategory(id);
        Level level = this.level;
        BlockPos blockPos = this.getBlockPos();
        switch (category) {
            case 2 -> {
                tableNbt.putInt("action", 4);
                SoundHandler.playSound(level, blockPos, 2);
            }
            case 3 -> {
                tableNbt.putInt("action", 2);
                tableNbt.putInt("clr", getCardColor(id));
                SoundHandler.playSound(level, blockPos, 8);
            }
            case 4 -> {
                tableNbt.putInt("action", 3);
                tableNbt.putInt("clr", getCardColor(id));
                SoundHandler.playSound(level, blockPos, 1);
            }
            case 5 -> {
                tableNbt.putInt("action", 2);
                tableNbt.putInt("clr", getCardColor(id));
                tableNbt.putInt("dir", -tableNbt.getInt("dir"));
                SoundHandler.playSound(level, blockPos, 9);
            }
        }
        saveAdditional(tableNbt);
        postBlockUpdate();
    }
    
    public void makeNewDeckOfCard() {
        CompoundTag nbt = new CompoundTag();
        int[] cardsNum = new int[CARD_TYPES_COUNT];
        CompoundTag tableNbt = getPersistentData();
        int numPlaced = tableNbt.getInt("numPlaced");
        int depositNum = Math.max(numPlaced - RENDERING_CARD_NUM, 0);
        if (depositNum == 0) {
            SoundHandler.playSound(level, getBlockPos(), 10);
            initialize();
            postBlockUpdate();
            return;
        }
        double[][] cards = new double[depositNum][4];
        tableNbt.putInt("numPlaced", numPlaced - depositNum);
        for (int i = 0; i < depositNum; i++) {
            String temp = Integer.toString(i + depositNum);
            cardsNum[tableNbt.getInt(temp)]++;
            cards[i][0] = tableNbt.getInt(temp);
            cards[i][1] = tableNbt.getDouble(temp + "x");
            cards[i][2] = tableNbt.getDouble(temp + "z");
            cards[i][3] = tableNbt.getFloat(temp + "r");
        }
        for (int i = 0; i < depositNum; i++) {
            String temp = Integer.toString(i);
            tableNbt.putInt(temp, (int) cards[i][0]);
            tableNbt.putDouble(temp + "x", cards[i][1]);
            tableNbt.putDouble(temp + "z", cards[i][2]);
            tableNbt.putFloat(temp + "r", (float) cards[i][3]);
        }
        for (int i = 1; i < CARD_TYPES_COUNT; i++) nbt.putInt(Integer.toString(i), cardsNum[i]);
        tableNbt.putInt("drawDeckNum", depositNum);
        tableNbt.put("deck", nbt);
        saveAdditional(tableNbt);
        postBlockUpdate();
        //System.out.println("new draw num");
        //System.out.println(depositNum);
        //System.out.println(getPersistentData().getInt("drawDeckNum"));
        //System.out.println(tableNbt.getInt("drawDeckNum"));
        SoundHandler.playSound(level, getBlockPos(), 7);
    }
    
    public void giveColorItem(Player player) {
        player.getInventory()
              .add(Items.CREEPER_HEAD.getDefaultInstance());
        player.getInventory()
              .add(Items.REDSTONE.getDefaultInstance());
        player.getInventory()
              .add(Items.ORANGE_DYE.getDefaultInstance());
        player.getInventory()
              .add(Items.DIAMOND.getDefaultInstance());
    }
    
    public boolean isWon(@NotNull UseOnContext useOnContext) {
        return getRemainCardNum(useOnContext.getItemInHand()) == 1;
    }
    
    public void cardFunctioning(int id, @NotNull UseOnContext useOnContext) {
        CompoundTag tableNbt = getPersistentData();
        int category = getCardCategory(id);
        tableNbt.putInt(
            "preId",
            Objects.requireNonNull(useOnContext.getItemInHand()
                                               .getTag())
                   .getInt("id")
                       );
        tableNbt.putBoolean("preShouldUno", getRemainCardNum(useOnContext.getItemInHand()) == 2);
        tableNbt.putBoolean("preUno", false);
        switch (category) {
            case 1 -> {
                giveColorItem(Objects.requireNonNull(useOnContext.getPlayer()));
                tableNbt.putInt("action", 5);
                SoundHandler.playSound(useOnContext, 4);
            }
            case 2 -> {
                giveColorItem(Objects.requireNonNull(useOnContext.getPlayer()));
                tableNbt.putInt("action", 6);
                SoundHandler.playSound(useOnContext, 3);
            }
            case 3 -> {
                int peopleSize = tableNbt.getInt("peopleSize");
                tableNbt.putInt("action", 2);
                tableNbt.putInt("id", (peopleSize + tableNbt.getInt("id") + tableNbt.getInt("dir")) % peopleSize);
                tableNbt.putInt("clr", getCardColor(id));
                SoundHandler.playSound(useOnContext, 8);
            }
            case 4 -> {
                int peopleSize = tableNbt.getInt("peopleSize");
                tableNbt.putInt("action", 3);
                tableNbt.putInt("id", (peopleSize + tableNbt.getInt("id") + tableNbt.getInt("dir")) % peopleSize);
                tableNbt.putInt("clr", getCardColor(id));
                SoundHandler.playSound(useOnContext, 1);
            }
            case 5 -> {
                tableNbt.putInt("action", 2);
                tableNbt.putInt("clr", getCardColor(id));
                tableNbt.putInt("dir", -tableNbt.getInt("dir"));
                int peopleSize = tableNbt.getInt("peopleSize");
                tableNbt.putInt("id", (peopleSize + tableNbt.getInt("id") + tableNbt.getInt("dir")) % peopleSize);
                SoundHandler.playSound(useOnContext, 9);
            }
            case 6 -> {
                tableNbt.putInt("action", 2);
                tableNbt.putInt("clr", getCardColor(id));
                int peopleSize = tableNbt.getInt("peopleSize");
                tableNbt.putInt("id", (peopleSize + tableNbt.getInt("id") + 2 * tableNbt.getInt("dir")) % peopleSize);
                SoundHandler.playSound(useOnContext, 0);
            }
        }
        saveAdditional(tableNbt);
        //System.out.println("surrent id:");
        //System.out.println(getPersistentData().getInt("id"));
        //System.out.println("people size:");
        //System.out.println(getPersistentData().getInt("peopleSize"));
        postBlockUpdate();
    }
    
    public void missUnoPenalize() {
        CompoundTag compoundTag = getPersistentData();
        compoundTag.putInt("id", compoundTag.getInt("preId"));
        compoundTag.putInt("action", 3);
        compoundTag.putBoolean("preShouldUno", false);
        SoundHandler.playSound(level, getBlockPos(), 3);
        postBlockUpdate();
    }
    
    
    public boolean useCard(@NotNull UseOnContext useOnContext) {
        ///testing
        Vec3 clickedPos = useOnContext.getClickLocation();
        ItemStack stack = useOnContext.getItemInHand();
        assert stack.getTag() != null;
        if (shouldGameInitialize()) {
            return false;
        }
        //verify
        if (!validSignature(useOnContext.getItemInHand())) {
            Objects.requireNonNull(useOnContext.getPlayer())
                   .setItemInHand(useOnContext.getHand(), Items.AIR.getDefaultInstance());
            return false;
        }
        if (!validId(stack)) {
            //System.out.println("invalid id");
            if (validCallUno(stack)) {
                callUno(useOnContext);
                //System.out.println("called uno");
            } else if (validCatchUno(stack)) {
                missUnoPenalize();
                //System.out.println("caught missed uno");
            }
            return false;
        }
        if (validCatchUno(stack)) {
            missUnoPenalize();
            //System.out.println("caught missed uno");
            return false;
        }
        if (shouldDeal()) {
            int action = getPersistentData().getInt("action");
            //System.out.println("should deal");
            //System.out.println(action);
            ItemStack newStack = useOnContext.getItemInHand();
            CompoundTag tableNbt = getPersistentData();
            int peopleSize = tableNbt.getInt("peopleSize");
            switch (action) {
                case 3 -> {
                    newStack = dealPlayerCards(2, newStack);
                    tableNbt.putInt("id", (peopleSize + tableNbt.getInt("id") + tableNbt.getInt("dir")) % peopleSize);
                    tableNbt.putInt("action", 2);
                }
                case 4 -> {
                    newStack = dealPlayerCards(4, newStack);
                    tableNbt.putInt("id", (peopleSize + tableNbt.getInt("id") + tableNbt.getInt("dir")) % peopleSize);
                    tableNbt.putInt("action", 2);
                }
            }
            Objects.requireNonNull(useOnContext.getPlayer())
                   .setItemInHand(useOnContext.getHand(), newStack);
            saveAdditional(tableNbt);
            postBlockUpdate();
            SoundHandler.playSound(useOnContext, 0);
        } else if (shouldPlace()) {
            //System.out.println("should place");
            if (validCard(stack)) {
                //System.out.println("valid");
                int cardId = MinoHandCard.getCurrentCardId(useOnContext.getItemInHand());
                float degree = Objects.requireNonNull(useOnContext.getPlayer())
                                      .getViewYRot(0);
                placeCard(cardId, clickedPos.get(Direction.Axis.X), clickedPos.get(Direction.Axis.Z), degree);
                if (isWon(useOnContext)) {
                    int peopleSize = getPersistentData().getInt("peopleSize");
                    for (int i = 0; i < peopleSize; i++) EntityHandler.summonFireWork(useOnContext);
                    SoundHandler.playSound(useOnContext, 10);
                    Objects.requireNonNull(useOnContext.getPlayer())
                           .setItemInHand(useOnContext.getHand(), MinoHandCard.makeFullStackOfCard(stack));
                    ItemStack reword = ItemHandler.mino_card_shapes.get(random.nextInt(
                                                      0,
                                                      ItemHandler.mino_card_shapes.size()
                                                                                      ))
                                                                   .get()
                                                                   .getDefaultInstance();
                    CompoundTag nbt = new CompoundTag();
                    nbt.putInt("pwd", getPersistentData().getInt("pwd"));
                    nbt.putString("time", new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date()));
                    reword.setTag(nbt);
                    Objects.requireNonNull(useOnContext.getPlayer())
                           .addItem(reword);
                    initialize();
                    postBlockUpdate();
                    return true;
                }
                cardFunctioning(cardId, useOnContext);
                Objects.requireNonNull(useOnContext.getPlayer())
                       .setItemInHand(useOnContext.getHand(), popCard(stack));
            } else {
                ItemStack newStack = dealPlayerCards(1, useOnContext.getItemInHand());
                Objects.requireNonNull(useOnContext.getPlayer())
                       .setItemInHand(useOnContext.getHand(), newStack);
                CompoundTag tableNbt = getPersistentData();
                tableNbt.putInt("action", 8);
                saveAdditional(tableNbt);
                postBlockUpdate();
                
                SoundHandler.playSound(useOnContext, 0);
            }
        } else if (shouldCheckColor()) {
            //System.out.println("should check clr");
            CompoundTag tableNbt = getPersistentData();
            if (chooseToChallenge(stack)) tableNbt.putInt("action", 7);
            boolean has = containingColor(getPersistentData().getInt("clr"), stack);
            int action = getPersistentData().getInt("action");
            if (has) {
                switch (action) {
                    case 6 -> {
                        tableNbt.putInt("action", 1);
                        
                        SoundHandler.playSound(useOnContext, 0);
                    }
                    case 7 -> {
                        tableNbt.putInt("id", tableNbt.getInt("preId"));
                        tableNbt.putInt("action", 4);
                        
                        SoundHandler.playSound(useOnContext, 2);
                    }
                }
            } else {
                ItemStack newStack = useOnContext.getItemInHand();
                switch (action) {
                    case 6 -> {
                        newStack = dealPlayerCards(4, newStack);
                        
                        SoundHandler.playSound(useOnContext, 0);
                    }
                    case 7 -> {
                        newStack = dealPlayerCards(6, newStack);
                        
                        SoundHandler.playSound(useOnContext, 1);
                    }
                }
                tableNbt.putInt("action", 2);
                int peopleSize = tableNbt.getInt("peopleSize");
                tableNbt.putInt("id", (peopleSize + tableNbt.getInt("id") + tableNbt.getInt("dir")) % peopleSize);
                Objects.requireNonNull(useOnContext.getPlayer())
                       .setItemInHand(useOnContext.getHand(), newStack);
            }
            saveAdditional(tableNbt);
            postBlockUpdate();
        } else if (getPersistentData().getInt("action") == 8) {
            //System.out.println("after panelty");
            if (validCard(stack)) {
                int cardId = MinoHandCard.getCurrentCardId(useOnContext.getItemInHand());
                float degree = Objects.requireNonNull(useOnContext.getPlayer())
                                      .getViewYRot(0);
                placeCard(cardId, clickedPos.get(Direction.Axis.X), clickedPos.get(Direction.Axis.Z), degree);
                cardFunctioning(cardId, useOnContext);
                Objects.requireNonNull(useOnContext.getPlayer())
                       .setItemInHand(useOnContext.getHand(), popCard(stack));
            } else {
                CompoundTag tableNbt = getPersistentData();
                int peopleSize = tableNbt.getInt("peopleSize");
                tableNbt.putInt("id", (peopleSize + tableNbt.getInt("id") + tableNbt.getInt("dir")) % peopleSize);
                tableNbt.putInt("action", 2);
                saveAdditional(tableNbt);
                postBlockUpdate();
                
                SoundHandler.playSound(useOnContext, 0);
            }
        }
        return true;
    }
    
    
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, BlockEntity::getPersistentData);
    }
    
    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return this.getPersistentData();
    }
    
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag compoundtag = pkt.getTag();
        if (compoundtag != null) {
            this.load(compoundtag);
        }
    }
    
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(nbt, this.items);
    }
    
    @Override
    public int getContainerSize() {
        return this.items.size();
    }
    
    @Override
    public boolean isEmpty() {
        return this.items.stream()
                         .allMatch(ItemStack::isEmpty);
    }
    
    @Override
    public @NotNull ItemStack getItem(int slotId) {
        return this.items.get(slotId);
    }
    
    @Override
    public @NotNull ItemStack removeItem(int slotId, int count) {
        ItemStack itemStack = ContainerHelper.removeItem(this.items, slotId, count);
        if (!itemStack.isEmpty()) {
            this.setChanged();
        }
        
        return itemStack;
    }
    
    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slotId) {
        return ContainerHelper.takeItem(this.items, slotId);
    }
    
    @Override
    public void setItem(int slotId, @NotNull ItemStack itemStack) {
        this.items.set(slotId, itemStack);
        if (itemStack.getCount() > this.getMaxStackSize()) {
            itemStack.setCount(this.getMaxStackSize());
        }
        
        this.setChanged();
    }
    
    @Override
    public boolean stillValid(@NotNull Player player) {
        return Container.stillValidBlockEntity(this, player);
    }
    
    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        ContainerHelper.saveAllItems(nbt, this.items);
    }
    
    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("container.mino_game_table");
    }
    
    @Override
    protected @NotNull AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInv) {
        return new MinoTableMenu(containerId, playerInv, this);
    }
    
    @Override
    public void clearContent() {
        this.items.clear();
    }
}
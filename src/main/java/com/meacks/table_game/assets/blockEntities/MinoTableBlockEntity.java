package com.meacks.table_game.assets.blockEntities;

import com.meacks.table_game.assets.handlers.BlockEntityHandler;
import com.meacks.table_game.assets.handlers.EntityHandler;
import com.meacks.table_game.assets.handlers.SoundHandler;
import com.meacks.table_game.assets.items.MinoHandCard;
//import com.meacks.table_game.common.gameRules.mino.MinoGameRule;
//import com.meacks.table_game.common.gameRules.mino.MinoStates;
//import com.meacks.table_game.common.gameRules.mino.MinoTriggers;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.meacks.table_game.TableGameMod.random;
import static com.meacks.table_game.assets.items.MinoHandCard.*;

public class MinoTableBlockEntity extends BaseContainerBlockEntity implements Container {//GameTableBlockEntity<MinoStates, MinoTriggers, MinoGameRule> {
    public static final int RENDERING_CARD_NUM = 15;
    private NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

    public MinoTableBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityHandler.minoTableBlockEntity.get(), pos, state);
//        MinoGameRule rule = new MinoGameRule(
//                this::getState, this::setState, MinoGameRule.getConfigurer(), this
//        );
//        this.setGameRule(rule);
        initialize();
    }


    public boolean shouldPlace() {
        return getPersistentData().getInt("action") == 2;
    }

    public boolean shouldDeal() {
        int action = getPersistentData().getInt("action");
        return action == 1 || action == 3 || action == 4;
    }

    public boolean shouldCheckColor() {
        return getPersistentData().getInt("action") == 7 || getPersistentData().getInt("action") == 6;
    }

    public boolean validCatchUno(ItemStack stack) {
        return MinoHandCard.getCurrentCardId(stack) == 0 && getPersistentData().getBoolean("preShouldUno");
    }

    public boolean chooseToChallenge(ItemStack stack) {
        return MinoHandCard.getCurrentCardId(stack) == 0;
    }

    public boolean succeedColor(ItemStack stack) {
        return MinoHandCard.containingColor(getPersistentData().getInt("clr"), stack);
    }


    public boolean validId(ItemStack stack) {
        CompoundTag cardNbt = stack.getTag();
        CompoundTag compoundTag = getPersistentData();
        assert cardNbt != null;
        return cardNbt.getInt("id") != compoundTag.getInt("id");
    }

    public boolean validCard(ItemStack stack) {
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
        int clr = MinoHandCard.getCardColor(compoundTag.getInt(Integer.toString(temp)));
        return MinoHandCard.areSameColor(currentId, id) || MinoHandCard.areSameType(currentId, id);
    }

    public boolean changeColor(int clr) {
        CompoundTag compoundTag = getPersistentData();
        int action = compoundTag.getInt("action");
        if (action == 5 || action == 6) {
            compoundTag.putInt("clr", clr);
            saveAdditional(compoundTag);
            return true;
        }
        return false;
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

        SoundHandler.playSound(level, getBlockPos(), 7);
    }

    public void makeNewDeckOfCard() {
        CompoundTag nbt = new CompoundTag();
        int[] cardsNum = new int[CARD_TYPES_COUNT];
        CompoundTag tableNbt = getPersistentData();
        int numPlaced = tableNbt.getInt("numPlaced");
        int depositNum = Math.max(numPlaced - RENDERING_CARD_NUM, 0);
        if (depositNum == 0) return;
        tableNbt.putInt("numPlaced", numPlaced - depositNum);
        for (int i = 0; i < depositNum; i++) {
            cardsNum[tableNbt.getInt(Integer.toString(i))]++;
            nbt.putInt(Integer.toString(i), tableNbt.getInt(Integer.toString(i + depositNum)));
            tableNbt.putInt(Integer.toString(i) + "x", tableNbt.getInt(Integer.toString(i + depositNum) + "x"));
            tableNbt.putInt(Integer.toString(i) + "y", tableNbt.getInt(Integer.toString(i + depositNum) + "y"));
            tableNbt.putInt(Integer.toString(i) + "r", tableNbt.getInt(Integer.toString(i + depositNum) + "r"));
        }
        nbt.putInt("index", 0);
        nbt.putInt("0", 1);
        for (int i = 1; i < CARD_TYPES_COUNT; i++) nbt.putInt(Integer.toString(i), cardsNum[i]);
        tableNbt.putInt("drawDeckNum", depositNum);
        tableNbt.put("deck", nbt);
        this.saveAdditional(tableNbt);

        SoundHandler.playSound(level, getBlockPos(), 7);
    }

    public int dealCard() {
        CompoundTag currentNbt = this.getPersistentData();
        CompoundTag deckNbt = currentNbt.getCompound("deck");
        int currentSize = currentNbt.getInt("drawDeckNum");
        if (currentSize == 0) makeNewDeckOfCard();
        int index = random.nextInt(0, currentSize);
        for (int i = 1; i < CARD_TYPES_COUNT; i++) {
            int temp = deckNbt.getInt(Integer.toString(i));
            index -= deckNbt.getInt(Integer.toString(i));
            if (index <= 0 && temp != 0) {
                deckNbt.putInt(Integer.toString(i), deckNbt.getInt(Integer.toString(i)) - 1);
                currentNbt.put("deck", deckNbt);
                currentNbt.putInt("numGiven", currentNbt.getInt("numGiven") + 1);
                currentNbt.putInt("drawDeckNum", currentSize - 1);
                this.saveAdditional(currentNbt);
                return i;
            }
        }
        return 0;
    }

    public boolean canDealPlayerCards(int n) {
        return this.getPersistentData().getInt("drawDeckNum") >= n;
    }

    public ItemStack dealPlayerCards(int n, ItemStack stack) {
        CompoundTag itemNbt = stack.getOrCreateTag();
        for (int i = 0; i < n; i++) {
            int temp = dealCard();
            itemNbt.putInt(Integer.toString(temp), itemNbt.getInt(Integer.toString(temp)) + 1);
        }
        stack.setTag(itemNbt);
        return stack;
    }

    public ItemStack signature(ItemStack itemStack) {
        CompoundTag nbt = itemStack.getOrCreateTag();
        nbt.putInt("pwd", getPersistentData().getInt("pwd"));
        itemStack.setTag(nbt);
        return itemStack;
    }

    public boolean validSignature(ItemStack itemStack) {
        return itemStack.getOrCreateTag().getInt("pwd") == getPersistentData().getInt("pwd");
    }

    public void initialize() {
        CompoundTag tableNbt = getPersistentData();
        tableNbt.putInt("numPlaced", 0);
        tableNbt.putInt("numGiven", 0);
        // will be true when click start
        tableNbt.putBoolean("inGame", false);
        // 0~9 be one set
        tableNbt.putInt("numCreeperSet", 2);
        tableNbt.putInt("numDiamondSet", 2);
        tableNbt.putInt("numOcelotSet", 2);
        tableNbt.putInt("numRedstoneSet", 2);
        // 4 colors 1 each be one set
        tableNbt.putInt("numSkipSet", 2);
        tableNbt.putInt("numReverseSet", 2);
        tableNbt.putInt("numDraw2Set", 2);
        tableNbt.putInt("numDraw4Set", 1);
        tableNbt.putInt("numWildSet", 1);

    }

    public void GameInitialize(int peopleSize) {
        CompoundTag tableNbt = getPersistentData();
        makeFullDeckOfCard();
        tableNbt.putBoolean("inGame", true);
        tableNbt.putInt("peopleSize", peopleSize);
        tableNbt.putInt("action", 0);
        tableNbt.putInt("pwd", random.nextInt());
        saveAdditional(tableNbt);
        postBlockUpdate();
    }

    public boolean shouldGameInitialize() {
        return !getPersistentData().getBoolean("inGame");
    }

    public void placeInitialHeadCard() {
        int id = dealCard();
        placeCard(id, this.getBlockPos().getX() + 0.5f, this.getBlockPos().getZ() + 0.5f, 0);
        while (getCardCategory(id) < 3) {
            id = dealCard();
            placeCard(id, this.getBlockPos().getX() + 0.5f, this.getBlockPos().getZ() + 0.5f, 0);
        }
        cardFunctioning_Auto(id);
    }

    public boolean shouldInitialGive() {
        CompoundTag tableNbt = getPersistentData();
        boolean result = tableNbt.getInt("action") == 0;
        if (result && tableNbt.getInt("numGiven") / 7 == tableNbt.getInt("peopleSize")) {
            tableNbt.putInt("action", 2);
            this.saveAdditional(tableNbt);
            placeInitialHeadCard();
        }
        return result;
    }

    public void placeCard(int id, double x, double z, float r) {
        CompoundTag tableNbt = getPersistentData();
        int cardsPlacedNum = tableNbt.getInt("numPlaced");
        tableNbt.putInt(Integer.toString(cardsPlacedNum), id);
        tableNbt.putDouble(Integer.toString(cardsPlacedNum) + "x", x);
        tableNbt.putDouble(Integer.toString(cardsPlacedNum) + "z", z);
        tableNbt.putFloat(Integer.toString(cardsPlacedNum) + "r", r);
        tableNbt.putInt("numPlaced", cardsPlacedNum + 1);

        saveAdditional(tableNbt);
        //postBlockUpdate();
    }

    public void giveColorItem(Player player) {
        player.getInventory().add(Items.CREEPER_HEAD.getDefaultInstance());
        player.getInventory().add(Items.REDSTONE.getDefaultInstance());
        player.getInventory().add(Items.ORANGE_DYE.getDefaultInstance());
        player.getInventory().add(Items.DIAMOND.getDefaultInstance());
    }

    public boolean isWon(@NotNull UseOnContext useOnContext) {
        return getRemainCardNum(useOnContext.getItemInHand()) == 0;
    }

    public void cardFunctioning(int id, @NotNull UseOnContext useOnContext) {
        CompoundTag tableNbt = getPersistentData();
        int category = getCardCategory(id);
        tableNbt.putInt("preId",
                Objects.requireNonNull(useOnContext.getItemInHand().getTag()).getInt("id"));
        tableNbt.putBoolean("preShouldUno", getRemainCardNum(useOnContext.getItemInHand()) == 1);
        tableNbt.putBoolean("preUno", false);
        switch (category) {
            case 0 -> {
                tableNbt.putBoolean("preUno", true);
                SoundHandler.playSound(useOnContext, 6);
            }
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
                tableNbt.putInt("clr", getCardColor(id));
                SoundHandler.playSound(useOnContext, 8);
            }
            case 4 -> {
                tableNbt.putInt("action", 3);
                SoundHandler.playSound(useOnContext, 1);
            }
            case 5 -> {
                tableNbt.putInt("dir", -tableNbt.getInt("dir"));
                SoundHandler.playSound(useOnContext, 9);
            }
            case 6 -> {
                tableNbt.putInt("id", tableNbt.getInt("id") + tableNbt.getInt("dir"));
                SoundHandler.playSound(useOnContext, 0);
            }
        }
        tableNbt.putInt("id", tableNbt.getInt("id") + tableNbt.getInt("dir"));
        saveAdditional(tableNbt);
        postBlockUpdate();
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
                tableNbt.putInt("clr", getCardColor(id));
                SoundHandler.playSound(level, blockPos, 8);
            }
            case 4 -> {
                tableNbt.putInt("action", 3);
                SoundHandler.playSound(level, blockPos, 1);
            }
            case 5 -> {
                tableNbt.putInt("dir", -tableNbt.getInt("dir"));
                SoundHandler.playSound(level, blockPos, 9);
            }
            case 6 -> {
                tableNbt.putInt("id", tableNbt.getInt("id") + tableNbt.getInt("dir"));
                SoundHandler.playSound(level, blockPos, 0);
            }
        }
        tableNbt.putInt("id", tableNbt.getInt("id") + tableNbt.getInt("dir"));
        saveAdditional(tableNbt);
        postBlockUpdate();
        System.out.println(tableNbt.getInt("action"));
    }

    public void postBlockUpdate() {
        BlockState blockState = getBlockState();
        Objects.requireNonNull(this.getLevel()).sendBlockUpdated(this.getBlockPos(), blockState, blockState, 2);
    }

    public void notUnoPenalize() {
        CompoundTag compoundTag = getPersistentData();
        compoundTag.putInt("id", compoundTag.getInt("preId"));
        compoundTag.putInt("action", 3);
        compoundTag.putBoolean("preShouldUno", false);
        SoundHandler.playSound(level, getBlockPos(), 3);
    }


    public boolean useCard(@NotNull UseOnContext useOnContext) {
        Vec3 clickedPos = useOnContext.getClickLocation();
        ItemStack stack = useOnContext.getItemInHand();
        assert stack.getTag() != null;
        if (shouldGameInitialize()) {
            GameInitialize(stack.getTag().getInt("peopleSize"));
            System.out.println(stack.getTag().getInt("peopleSize"));
            return false;
        }
        //verify
        if (!validSignature(useOnContext.getItemInHand())) {
            Objects.requireNonNull(useOnContext.getPlayer()).setItemInHand(
                    useOnContext.getHand(), Items.AIR.getDefaultInstance());
            return false;
        }
        if (!validId(stack)) {
            if (validCatchUno(stack)) notUnoPenalize();
            return false;
        }
        if (shouldDeal()) {
            int action = getPersistentData().getInt("action");
            ItemStack newStack = useOnContext.getItemInHand();
            CompoundTag tableNbt = getPersistentData();
            switch (action) {
                case 1 -> {
                    newStack = dealPlayerCards(1, newStack);
                    getPersistentData().putInt("action", 8);
                }
                case 3 -> {
                    newStack = dealPlayerCards(2, newStack);
                    tableNbt.putInt("id", tableNbt.getInt("id") + tableNbt.getInt("dir"));
                }
                case 4 -> {
                    newStack = dealPlayerCards(4, newStack);
                    tableNbt.putInt("id", tableNbt.getInt("id") + tableNbt.getInt("dir"));
                }
            }
            Objects.requireNonNull(useOnContext.getPlayer()).setItemInHand(useOnContext.getHand(), newStack);
            saveAdditional(tableNbt);
        } else if (shouldPlace()) {
            if (validCard(stack)) {
                int cardId = MinoHandCard.getCurrentCardId(useOnContext.getItemInHand());
                float degree = Objects.requireNonNull(useOnContext.getPlayer()).getViewYRot(0);
                placeCard(cardId, clickedPos.get(Direction.Axis.X), clickedPos.get(Direction.Axis.Z), degree);
                Objects.requireNonNull(useOnContext.getPlayer()).setItemInHand(useOnContext.getHand(), popCard(stack));
                if (isWon(useOnContext)) {
                    EntityHandler.summonFireWork(useOnContext);
                    initialize();
                }
                cardFunctioning(cardId, useOnContext);
            } else {
                CompoundTag tableNbt = getPersistentData();
                tableNbt.putInt("action", 1);
                saveAdditional(tableNbt);
            }
        } else if (shouldCheckColor()) {
            boolean has = containingColor(getPersistentData().getInt("clr"), stack);
            int action = getPersistentData().getInt("action");
            CompoundTag tableNbt = getPersistentData();
            if (has) {
                switch (action) {
                    case 6 -> {
                        tableNbt.putInt("action", 1);
                    }
                    case 7 -> {
                        tableNbt.putInt("id", tableNbt.getInt("preId"));
                        tableNbt.putInt("action", 4);
                    }
                }
            } else {
                ItemStack newStack = useOnContext.getItemInHand();
                switch (action) {
                    case 6 -> {
                        newStack = dealPlayerCards(4, newStack);
                    }
                    case 7 -> {
                        newStack = dealPlayerCards(6, newStack);
                    }
                }
                tableNbt.putInt("action", 2);
                tableNbt.putInt("id", tableNbt.getInt("id") + tableNbt.getInt("dir"));
                Objects.requireNonNull(useOnContext.getPlayer()).setItemInHand(useOnContext.getHand(), newStack);
            }
            saveAdditional(tableNbt);
        } else if (getPersistentData().getInt("action") == 8) {
            if (validCard(stack)) {
                int cardId = MinoHandCard.getCurrentCardId(useOnContext.getItemInHand());
                float degree = Objects.requireNonNull(useOnContext.getPlayer()).getViewYRot(0);
                placeCard(cardId, clickedPos.get(Direction.Axis.X), clickedPos.get(Direction.Axis.Z), degree);
                Objects.requireNonNull(useOnContext.getPlayer()).setItemInHand(useOnContext.getHand(), popCard(stack));
                cardFunctioning(cardId, useOnContext);
            } else {
                CompoundTag tableNbt = getPersistentData();
                tableNbt.putInt("id", tableNbt.getInt("id") + tableNbt.getInt("dir"));
                tableNbt.putInt("action", 2);
                saveAdditional(tableNbt);
            }
        }
        //placing card
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

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        ContainerHelper.saveAllItems(nbt, this.items);
    }


    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(nbt, this.items);
    }

    @Override
    public boolean isEmpty() {
        return this.items.stream().allMatch(ItemStack::isEmpty);
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
    public void clearContent() {
        this.items.clear();
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("container.mino_game_table");
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInv) {
        return new MinoTableMenu(containerId, playerInv, this);
    }
}
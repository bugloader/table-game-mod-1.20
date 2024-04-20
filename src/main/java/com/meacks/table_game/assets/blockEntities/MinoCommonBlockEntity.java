package com.meacks.table_game.assets.blockEntities;

import com.meacks.table_game.assets.handlers.EntityHandler;
import com.meacks.table_game.common.inventory.MinoTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static com.meacks.table_game.TableGameMod.random;
import static com.meacks.table_game.assets.handlers.ItemHandler.mino_card_shapes;
import static com.meacks.table_game.assets.handlers.SoundHandler.playSound;
import static com.meacks.table_game.assets.items.MinoHandCard.*;

public class MinoCommonBlockEntity extends BaseContainerBlockEntity implements Container {
    public static final int CARD_SIZE_LIMIT = 800;
    public final int DISPLAY_CARD_LIMIT;
    public int[] placedCardId = new int[CARD_SIZE_LIMIT];
    public int[] drawDeckLeftCardsNum = new int[CARD_TYPES_COUNT];
    public float[] displayCardX, displayCardZ, displayCardR;
    public int numPlaced;
    public int drawDeckNum;
    public int action;
    public int pwd;
    public static int id;
    public int preId;
    public int dir;
    public int clr;
    public int peopleSize;
    public boolean inGame, preShouldUno, preUno;
    private NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
    private int count;

    public MinoCommonBlockEntity(BlockPos pos, BlockState state, int DISPLAY_CARD_LIMIT, BlockEntityType blockEntityType
    ) {
        super(blockEntityType, pos, state);
        this.DISPLAY_CARD_LIMIT = DISPLAY_CARD_LIMIT;
        displayCardZ = new float[DISPLAY_CARD_LIMIT];
        displayCardX = new float[DISPLAY_CARD_LIMIT];
        displayCardR = new float[DISPLAY_CARD_LIMIT];
//        MinoGameRule rule = new MinoGameRule(
//                this::getState, this::setState, MinoGameRule.getConfigurer(), this
//        );
//        this.setGameRule(rule);
        initialize();
    }


    public void initialize() {
        numPlaced = 0;
        drawDeckNum = 0;
        action = 1;
        id = 0;
        dir = 1;
        clr = -1;
        inGame = false;
    }

    public boolean shouldPlace() {
        return action == 2;
    }

    public boolean shouldDeal() {
        return action == 3 || action == 4;
    }

    public boolean shouldCheckColor() {
        return (action == 7 || action == 6) && preId != id;
    }

    public boolean validCallUno(ItemStack stack) {
        CompoundTag itemNbt = stack.getTag();
        return itemNbt != null &&
                itemNbt.getInt("id") == preId && getCurrentCardId(stack) == 0 && preShouldUno;
    }

    public boolean validCatchUno(ItemStack stack) {
        return getCurrentCardId(stack) == 0 && preShouldUno && !preUno && preId != id;
    }

    public boolean chooseToChallenge(ItemStack stack) {
        return getCurrentCardId(stack) == 0;
    }

    public boolean validId(ItemStack stack) {
        CompoundTag itemNbt = stack.getTag();
        return itemNbt != null && itemNbt.getInt("id") == id;
    }

    public boolean validCard(ItemStack stack) {
        if (getCurrentCardId(stack) == 0) return false;
        int currentCardId = getCurrentCardId(stack);
        int temp = numPlaced - 1;
        int id = placedCardId[temp];
        while (getCardCategory(id) < 3) {
            temp--;
            if (temp == -1) return true;
            id = placedCardId[temp];
        }
        int clr = getCardColor(getCurrentCardId(stack));
        return clr == -1 || clr == this.clr || areSameType(currentCardId, id);
    }

    public boolean changeColor(int clr) {
        if (action == 5 || action == 6) {
            this.clr = clr;
            action = action == 5 ? 2 : 7;
            moveToNextPlayer();
            postBlockUpdate();
            playSound(level, getBlockPos(), 1);
            return true;
        }
        return false;
    }

    public void postBlockUpdate() {
        BlockState blockState = getBlockState();
        assert level != null;
        setChanged();
        level.sendBlockUpdated(getBlockPos(), blockState, blockState, 2);
    }

    public void moveToNextPlayer() {
        id = (peopleSize + id + dir) % peopleSize;
    }

    public void callUno(UseOnContext useOnContext) {
        preUno = true;
        preShouldUno = false;
        postBlockUpdate();
        playSound(useOnContext, 6);
    }

    public ItemStack dealPlayerCards(int n, ItemStack stack) {
        CompoundTag itemNbt = stack.getOrCreateTag();
        for (int i = 0; i < n; i++) {
            int id = dealCard();
            if (id == 0) return stack;
            String temp = Integer.toString(id);
            itemNbt.putInt(temp, itemNbt.getInt(temp) + 1);
        }
        stack.setTag(itemNbt);
        postBlockUpdate();
        return stack;
    }

    public ItemStack signature(ItemStack itemStack) {
        CompoundTag nbt = itemStack.getTag();
        assert nbt != null;
        nbt.putInt("pwd", pwd);
        nbt.putInt("id", id);
        ListTag listTag = new ListTag();
        listTag.add(StringTag.valueOf("table_game:mino_table"));
        listTag.add(StringTag.valueOf("table_game:mino_large_table"));
        listTag.add(StringTag.valueOf("table_game:mino_table_extender"));
        nbt.put("CanPlaceOn", listTag);
        itemStack.setTag(nbt);
        id = action == 0 ? id + 1 : 0;
        postBlockUpdate();
        playSound(level, getBlockPos(), 0);
        return itemStack;
    }

    public boolean validSignature(ItemStack itemStack) {
        CompoundTag itemNbt = itemStack.getTag();
        return itemNbt != null && itemNbt.getInt("pwd") == pwd;
    }

    public void gameInitialize() {
        ItemStack stack = items.get(0);
        makeFullDeckOfCard(stack);
        preShouldUno = false;
        inGame = true;
        action = 0;
        pwd = random.nextInt();
        postBlockUpdate();
    }

    public void makeFullDeckOfCard(ItemStack stack) {
        int numCreeperSet = 2, numDiamondSet = 2, numOcelotSet = 2, numRedstoneSet = 2;
        int numSkipSet = 2, numReverseSet = 2, numDraw2Set = 1, numDraw4Set = 1, numWildSet = 1;
        if (stack.is(mino_card_shapes.get(0).get())) {
            CompoundTag itemNbt = stack.getTag();
            if (itemNbt != null) {
                numCreeperSet = itemNbt.getInt("numCreeperSet");
                numDiamondSet = itemNbt.getInt("numDiamondSet");
                numOcelotSet = itemNbt.getInt("numOcelotSet");
                numRedstoneSet = itemNbt.getInt("numRedstoneSet");
                numSkipSet = itemNbt.getInt("numSkipSet");
                numReverseSet = itemNbt.getInt("numReverseSet");
                numDraw2Set = itemNbt.getInt("numDraw2Set");
                numDraw4Set = itemNbt.getInt("numDraw4Set");
                numWildSet = itemNbt.getInt("numWildSet");
            }
        }
        peopleSize = Math.max(0 == numSkipSet + numDraw2Set + numDraw4Set ? 1 : 2
                , stack.getOrCreateTag().getInt("peopleSize"));
        // 0~9 be one set
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
            drawDeckLeftCardsNum[i] = typeNum;
            drawDeckNum += typeNum;
        }
        postBlockUpdate();
        playSound(level, getBlockPos(), 7);
    }

    public boolean stepInitialGive() {
        //System.out.println("stepInitGive");
        //System.out.println(action);
        //System.out.println(peopleSize);
        if (action == 0) {
            if (id == peopleSize - 1) {
                action = 2;
                placeInitialHeadCard();
            }
            return true;
        }
        return false;
    }

    public void placeInitialHeadCard() {
        int id = dealCard();
        int category = getCardCategory(id);
        placeCard(id, getBlockPos().getX() + 0.5f, getBlockPos().getZ() + 0.5f, 0);
        while (category == 1 || category == 6) {
            id = dealCard();
            category = getCardCategory(id);
            placeCard(id, getBlockPos().getX() + 0.5f, getBlockPos().getZ() + 0.5f, 0);
        }
        cardFunctioning_Auto(id);
    }

    public int dealCard() {
        if (drawDeckNum == 0) makeNewDeckOfCard();
        if (drawDeckNum == 0) return 0;
        int index = random.nextInt(0, drawDeckNum);
        for (int i = 1; i < CARD_TYPES_COUNT; i++) {
            int temp = drawDeckLeftCardsNum[i];
            index -= drawDeckLeftCardsNum[i];
            if (index <= 0 && temp != 0) {
                drawDeckLeftCardsNum[i]--;
                drawDeckNum--;
                return i;
            }
        }
        return 0;
    }

    public void placeCard(int id, float x, float z, float r) {
        if (numPlaced < DISPLAY_CARD_LIMIT) {
            displayCardX[numPlaced] = x;
            displayCardZ[numPlaced] = z;
            displayCardR[numPlaced] = r;
        } else {
            for (int i = 0; i < DISPLAY_CARD_LIMIT - 1; i++) {
                displayCardX[i] = displayCardX[i + 1];
                displayCardZ[i] = displayCardZ[i + 1];
                displayCardR[i] = displayCardR[i + 1];
            }
            displayCardX[DISPLAY_CARD_LIMIT - 1] = x;
            displayCardZ[DISPLAY_CARD_LIMIT - 1] = z;
            displayCardR[DISPLAY_CARD_LIMIT - 1] = r;
        }
        placedCardId[numPlaced++] = id;
    }

    public void cardFunctioning_Auto(int id) {
        int category = getCardCategory(id);
        BlockPos blockPos = this.getBlockPos();
        switch (category) {
            case 2 -> {
                action = 4;
                playSound(level, blockPos, 2);
            }
            case 3 -> {
                action = 2;
                clr = getCardColor(id);
                playSound(level, blockPos, 8);
            }
            case 4 -> {
                action = 3;
                clr = getCardColor(id);
                playSound(level, blockPos, 1);
            }
            case 5 -> {
                action = 2;
                clr = getCardColor(id);
                dir = -dir;
                playSound(level, blockPos, 9);
            }
        }
        postBlockUpdate();
    }

    public void makeNewDeckOfCard() {
        drawDeckLeftCardsNum = new int[CARD_TYPES_COUNT];
        int depositNum = Math.max(numPlaced - DISPLAY_CARD_LIMIT, 0);
        if (depositNum == 0) {
            playSound(level, getBlockPos(), 10);
            initialize();
            postBlockUpdate();
            return;
        }
        int[] cards = new int[CARD_SIZE_LIMIT];
        numPlaced -= depositNum;
        for (int i = 0; i < DISPLAY_CARD_LIMIT; i++) {
            int temp = i + depositNum;
            drawDeckLeftCardsNum[placedCardId[temp]]++;
            cards[i] = placedCardId[temp];
        }
        placedCardId = cards;
        drawDeckNum = depositNum;
        postBlockUpdate();
        playSound(level, getBlockPos(), 7);
    }

    public void giveColorItem(Player player) {
        Inventory inventory = player.getInventory();
        inventory.add(Items.CREEPER_HEAD.getDefaultInstance());
        inventory.add(Items.REDSTONE.getDefaultInstance());
        inventory.add(Items.ORANGE_DYE.getDefaultInstance());
        inventory.add(Items.DIAMOND.getDefaultInstance());
    }

    public boolean isWon(@NotNull ItemStack stack) {
        return getRemainCardNum(stack) == 1;
    }

    public void cardFunctioning(int id, @NotNull UseOnContext useOnContext) {
        ItemStack stack = useOnContext.getItemInHand();
        Player player = Objects.requireNonNull(useOnContext.getPlayer());
        int category = getCardCategory(id);
        preId = Objects.requireNonNull(stack.getTag()).getInt("id");
        preShouldUno = getRemainCardNum(stack) == 2;
        preUno = false;
        switch (category) {
            case 1 -> {
                giveColorItem(player);
                action = 5;
                playSound(useOnContext, 4);
            }
            case 2 -> {
                giveColorItem(player);
                action = 6;
                playSound(useOnContext, 3);
            }
            case 3 -> {
                action = 2;
                moveToNextPlayer();
                clr = getCardColor(id);
                playSound(useOnContext, 8);
            }
            case 4 -> {
                action = 3;
                moveToNextPlayer();
                clr = getCardColor(id);
                playSound(useOnContext, 1);
            }
            case 5 -> {
                action = 2;
                clr = getCardColor(id);
                dir = -dir;
                moveToNextPlayer();
                playSound(useOnContext, 9);
            }
            case 6 -> {
                action = 2;
                clr = getCardColor(id);
                moveToNextPlayer();
                moveToNextPlayer();
                playSound(useOnContext, 0);
            }
        }
        postBlockUpdate();
    }

    public void missUnoPenalize() {
        id = preId;
        action = 3;
        preShouldUno = false;
        postBlockUpdate();
        playSound(level, getBlockPos(), 3);
    }


    public boolean useCard(@NotNull UseOnContext useOnContext) {
        Vec3 clickedPos = useOnContext.getClickLocation();
        ItemStack stack = useOnContext.getItemInHand();
        Player player = Objects.requireNonNull(useOnContext.getPlayer());
        InteractionHand hand = useOnContext.getHand();
        assert stack.getTag() != null;
        if (!inGame) return false;
        if (!validSignature(stack)) {
            player.setItemInHand(hand, Items.AIR.getDefaultInstance());
            return false;
        }
        if (!validId(stack)) {
            if (validCallUno(stack)) callUno(useOnContext);
            else if (validCatchUno(stack)) missUnoPenalize();
            return false;
        }
        if (validCatchUno(stack)) {
            missUnoPenalize();
            return false;
        }
        if (shouldDeal()) {
            stack = action == 3 ? dealPlayerCards(2, stack) : dealPlayerCards(4, stack);
            action = 2;
            moveToNextPlayer();
            player.setItemInHand(useOnContext.getHand(), stack);
            postBlockUpdate();
            playSound(useOnContext, 0);
        } else if (shouldPlace()) {
            if (validCard(stack)) {
                int cardId = getCurrentCardId(stack);
                float degree = player.getViewYRot(0);
                placeCard(cardId, (float) clickedPos.get(Direction.Axis.X),
                        (float) clickedPos.get(Direction.Axis.Z), degree);
                if (isWon(stack)) {
                    playSound(useOnContext, 10);
                    player.setItemInHand(hand, makeFullStackOfCard(stack));
                    CompoundTag nbt = new CompoundTag();
                    nbt.putInt("pwd", pwd);
                    nbt.putString("time",
                            new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
                    for (int i = 0; i < peopleSize - 1; i++) {
                        ItemStack reword =
                                mino_card_shapes.get(random.nextInt(0, mino_card_shapes.size()))
                                        .get().getDefaultInstance();
                        reword.setTag(nbt);
                        player.addItem(reword);
                        EntityHandler.summonFireWork(useOnContext);
                    }
                    initialize();
                    postBlockUpdate();
                    return true;
                }
                cardFunctioning(cardId, useOnContext);
                player.setItemInHand(hand, popCard(stack));
            } else {
                player.setItemInHand(hand, dealPlayerCards(1, stack));
                action = 8;
                postBlockUpdate();
                playSound(useOnContext, 0);
            }
        } else if (shouldCheckColor()) {
            //System.out.println("should check clr");
            boolean has = containingColor(clr, stack);
            boolean challenge = chooseToChallenge(stack);
            if (has) {
                if (challenge) {
                    id = preId;
                    action = 4;
                    playSound(useOnContext, 1);
                } else {
                    action = 2;
                    playSound(useOnContext, 0);
                }
            } else {
                if (challenge) {
                    stack = dealPlayerCards(6, stack);
                    playSound(level, getBlockPos(), 2);
                } else {
                    stack = dealPlayerCards(4, stack);
                    playSound(useOnContext, 0);
                }
                action = 2;
                moveToNextPlayer();
                player.setItemInHand(useOnContext.getHand(), stack);
            }
            postBlockUpdate();
        } else if (action == 8) {
            //System.out.println("after panelty");
            if (validCard(stack)) {
                int cardId = getCurrentCardId(stack);
                float degree = player.getViewYRot(0);
                placeCard(cardId, (float) clickedPos.get(Direction.Axis.X),
                        (float) clickedPos.get(Direction.Axis.Z), degree);
                cardFunctioning(cardId, useOnContext);
                player.setItemInHand(useOnContext.getHand(), popCard(stack));
            } else {
                moveToNextPlayer();
                action = 2;
                postBlockUpdate();
                playSound(useOnContext, 0);
            }
        }
        return true;
    }

    public static CompoundTag serializeFloatArray(float[] array) {
        CompoundTag result = new CompoundTag();
        result.putInt("n", array.length);
        for (int i = 0; i < array.length; i++) result.putFloat(Integer.toHexString(i), array[i]);
        return result;
    }

    public static float[] deserializeFloatArray(CompoundTag nbt) {
        float[] result = new float[nbt.getInt("n")];
        for (int i = 0; i < result.length; i++) result[i] = nbt.getFloat(Integer.toHexString(i));
        return result;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag compoundTag = new CompoundTag();
        this.saveAdditional(compoundTag);
        return compoundTag;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MinoCommonBlockEntity entity) {
        entity.count += 1;
        if (entity.count > 40) {
            entity.count = 0;
            if (level != null && !level.isClientSide()) {
                List<ServerPlayer> list = getNearbyPlayers((ServerLevel) level,pos);
                for(ServerPlayer player : list) {
                    player.connection.send(new ClientboundSetActionBarTextPacket(Component.translatable("table_game.text.current_player_id").append(" : ").append(String.valueOf(id))));
                }
            }
        }
    }

    private static List<ServerPlayer> getNearbyPlayers(ServerLevel pLevel, BlockPos pPos) {
        Vec3 vec3 = Vec3.atCenterOf(pPos);
        Predicate<ServerPlayer> predicate = (player) -> player.position().closerThan(vec3, 4.0D);
        return pLevel.getPlayers(predicate.and(LivingEntity::isAlive).and(EntitySelector.NO_SPECTATORS));
    }

    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        placedCardId = nbt.getIntArray("placedCardId");
        drawDeckLeftCardsNum = nbt.getIntArray("drawDeckLeftCardsNum");
        displayCardX = deserializeFloatArray(nbt.getCompound("displayCardX"));
        displayCardZ = deserializeFloatArray(nbt.getCompound("displayCardZ"));
        displayCardR = deserializeFloatArray(nbt.getCompound("displayCardR"));
        numPlaced = nbt.getInt("numPlaced");
        drawDeckNum = nbt.getInt("drawDeckNum");
        action = nbt.getInt("action");
        pwd = nbt.getInt("pwd");
        id = nbt.getInt("id");
        preId = nbt.getInt("preId");
        dir = nbt.getInt("dir");
        clr = nbt.getInt("clr");
        peopleSize = nbt.getInt("peopleSize");
        inGame = nbt.getBoolean("inGame");
        preShouldUno = nbt.getBoolean("preShouldUno");
        preUno = nbt.getBoolean("preUno");
        //inventory
        items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(nbt, items);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putIntArray("placedCardId", placedCardId);
        nbt.putIntArray("drawDeckLeftCardsNum", drawDeckLeftCardsNum);
        nbt.put("displayCardX", serializeFloatArray(displayCardX));
        nbt.put("displayCardZ", serializeFloatArray(displayCardZ));
        nbt.put("displayCardR", serializeFloatArray(displayCardR));
        nbt.putInt("numPlaced", numPlaced);
        nbt.putInt("drawDeckNum", drawDeckNum);
        nbt.putInt("action", action);
        nbt.putInt("pwd", pwd);
        nbt.putInt("id", id);
        nbt.putInt("preId", preId);
        nbt.putInt("dir", dir);
        nbt.putInt("clr", clr);
        nbt.putInt("peopleSize", peopleSize);
        nbt.putBoolean("inGame", inGame);
        nbt.putBoolean("preShouldUno", preShouldUno);
        nbt.putBoolean("preUno", preUno);
        //inventory
        ContainerHelper.saveAllItems(nbt, items);
        //System.out.println("nbt" + nbt.size());
    }


    @Override
    public int getContainerSize() {
        return this.items.size();
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
        if (!itemStack.isEmpty()) setChanged();
        return itemStack;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slotId) {
        return ContainerHelper.takeItem(this.items, slotId);
    }

    @Override
    public void setItem(int slotId, @NotNull ItemStack itemStack) {
        this.items.set(slotId, itemStack);
        if (itemStack.getCount() > this.getMaxStackSize()) itemStack.setCount(this.getMaxStackSize());
        this.setChanged();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return Container.stillValidBlockEntity(this, player);
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
package com.meacks.table_game.assets.blockEntities;

import com.google.common.collect.Multimap;
import com.meacks.table_game.common.TriConsumer;
import com.meacks.table_game.common.gameRules.AbstractGameRule;
import com.meacks.table_game.common.gameRules.IGameStates;
import com.meacks.table_game.common.gameRules.IGameTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import nl.erasmusmc.mgz.parallelstateless4j.IContextPropertyListener;
import nl.erasmusmc.mgz.parallelstateless4j.IStateMachine;
import nl.erasmusmc.mgz.parallelstateless4j.IStateMachineContext;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class GameTableBlockEntity<S extends IGameStates, T extends IGameTriggers,
    Rule extends AbstractGameRule<S, T>>
    extends BlockEntity implements IStateMachineContext<S, T> {
    protected static final Map<Class<?>, TriConsumer<CompoundTag, String, Object>>
        TYPE_TO_PUT_FUNCTIONS_MAP;
    
    static {
        Map<Class<?>, TriConsumer<CompoundTag, String, Object>> setFunctions = new HashMap<>();
        setFunctions.put(double.class, (tag, key, value) -> tag.putDouble(key, (double) value));
        setFunctions.put(float.class, (tag, key, value) -> tag.putFloat(key, (float) value));
        setFunctions.put(int.class, (tag, key, value) -> tag.putInt(key, (int) value));
        setFunctions.put(long.class, (tag, key, value) -> tag.putLong(key, (long) value));
        setFunctions.put(short.class, (tag, key, value) -> tag.putShort(key, (short) value));
        setFunctions.put(byte.class, (tag, key, value) -> tag.putByte(key, (byte) value));
        setFunctions.put(boolean.class, (tag, key, value) -> tag.putBoolean(key, (boolean) value));
        setFunctions.put(String.class, (tag, key, value) -> tag.putString(key, (String) value));
        setFunctions.put(Tag.class, (tag, key, value) -> tag.put(key, (Tag) value));
        setFunctions.put(UUID.class, (tag, key, value) -> tag.putUUID(key, (UUID) value));
        setFunctions.put(byte[].class, (tag, key, value) -> tag.putByteArray(key, (byte[]) value));
        setFunctions.put(int[].class, (tag, key, value) -> tag.putIntArray(key, (int[]) value));
        setFunctions.put(long[].class, (tag, key, value) -> tag.putLongArray(key, (long[]) value));
        TYPE_TO_PUT_FUNCTIONS_MAP = Collections.unmodifiableMap(setFunctions);
    }
    
    protected IStateMachine<S, T> stateMachine;
    protected Rule gameRule;
    private S state;
    
    public GameTableBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }
    
    protected void setGameRule(Rule rule) {
        this.gameRule = rule;
    }
    
    public S getState() {
        return this.state.fromValue(getPersistentData().getInt("gameState"));
    }
    
    public void setState(S state) {
        this.state = state;
        CompoundTag tableNbt = getPersistentData();
        tableNbt.putInt("gameState", state.getValue());
        saveAdditional(tableNbt);
    }
    
    protected void commitBlockUpdate(CompoundTag tableNbt, @NotNull UseOnContext useOnContext) {
        saveAdditional(tableNbt);
        BlockState blockState = getBlockState();
        useOnContext.getLevel()
                    .sendBlockUpdated(this.getBlockPos(), blockState, blockState, 2);
    }
    
    @Override
    public Object getAttribute(String key) {
        return getPersistentData().get(key);
    }
    
    @Override
    public Object getAttribute(String key, Object initialValue) {
        CompoundTag data = getPersistentData();
        if (!data.contains(key)) {
            this.setAttribute(key, initialValue);
            return initialValue;
        }
        return data.get(key);
    }
    
    @Override
    public void setAttribute(String key, Object value) {
        if (TYPE_TO_PUT_FUNCTIONS_MAP.containsKey(checkNotNull(value).getClass())) {
            TYPE_TO_PUT_FUNCTIONS_MAP.get(value.getClass())
                                     .accept(getPersistentData(), key, value);
        } else {
            throw new IllegalArgumentException("Unsupported type: " + value.getClass()
                                                                           .getName());
        }
    }
    
    @Override
    public void setListeners(Multimap<String, IContextPropertyListener> contextPropertyListeners) {
    
    }
    
    @Override
    public void addListener(String key, IContextPropertyListener listener) {
    
    }
    
    @Override
    public void removeListener(String key, IContextPropertyListener listener) {
    
    }
    
    @Override
    public void removeListeners(String key) {
    
    }
    
    @Override
    public <R extends IStateMachine<S, T>> R getStateMachine() {
        try {
            return (R) this.stateMachine;
        } catch (ClassCastException e) {
            throw new IllegalStateException("State machine is not of the expected type", e);
        }
    }
    
    @Override
    public void setStateMachine(IStateMachine<S, T> stateMachine) {
        this.stateMachine = stateMachine;
    }
}

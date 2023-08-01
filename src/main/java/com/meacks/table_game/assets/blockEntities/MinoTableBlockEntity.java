package com.meacks.table_game.assets.blockEntities;

import com.meacks.table_game.assets.handlers.BlockEntityHandler;
import com.meacks.table_game.assets.items.MinoHandCard;
import com.meacks.table_game.common.gameRules.mino.MinoGameRule;
import com.meacks.table_game.common.gameRules.mino.MinoStates;
import com.meacks.table_game.common.gameRules.mino.MinoTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MinoTableBlockEntity extends GameTableBlockEntity<MinoStates, MinoTriggers, MinoGameRule> {
    /*
    random game number: number
    current round player id: id
    current giving amount 1,2,4: cga
    direction +-1: dir
    state of action {0: no game yet,
                     1: giving cards,
                     2: placing card,
                     3: getting card,
                     4: skipping,
                     5: color providing
    }: action
    color 0~4: clr
    cards placed: numPlaced
    108 card, each has id, x,z rotation along y: #,#x,#z,#r
    cards given: numGiven
    card stack: #s
     */
    public MinoTableBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityHandler.minoTableBlockEntity.get(), pos, state);
        MinoGameRule rule = new MinoGameRule(
                this::getState, this::setState, MinoGameRule.getConfigurer(), this
        );
        this.setGameRule(rule);
        initialize();
    }

    public boolean verifyCard() {
        return false;
    }

    public void initialize() {
        CompoundTag tableNbt = getPersistentData();
        tableNbt.putInt("numPlaced", 0);
        tableNbt.putInt("numGiven", 0);
        tableNbt.putInt("drawDeckNum", 108);
    }

    public void useCard(@NotNull UseOnContext useOnContext) {
        Vec3 clickedPos = useOnContext.getClickLocation();
        //verify

        //placing card
        int cardId = MinoHandCard.getCurrentCardId(useOnContext.getItemInHand());
        CompoundTag tableNbt = getPersistentData();
        int cardsPlacedNum = tableNbt.getInt("numPlaced");
        tableNbt.putInt(Integer.toString(cardsPlacedNum), cardId);
        tableNbt.putDouble(Integer.toString(cardsPlacedNum) + "x", clickedPos.get(Direction.Axis.X));
        tableNbt.putDouble(Integer.toString(cardsPlacedNum) + "z", clickedPos.get(Direction.Axis.Z));
        float degree = Objects.requireNonNull(useOnContext.getPlayer()).getViewYRot(0);
        tableNbt.putFloat(Integer.toString(cardsPlacedNum)+"r", degree);
        tableNbt.putInt("numPlaced",cardsPlacedNum+1);
        saveAdditional(tableNbt);
        BlockState blockState = getBlockState();
        useOnContext.getLevel().sendBlockUpdated(this.getBlockPos(), blockState, blockState, 2);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this,BlockEntity::getPersistentData);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        return this.getPersistentData();
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag compoundtag = pkt.getTag();
        if (compoundtag != null) {
            this.load(compoundtag);
        }
    }


}
package com.meacks.table_game.assets.items;

import com.meacks.table_game.assets.handlers.ItemHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CardShape extends Item {

    public CardShape(){
        super(new Properties().stacksTo(1));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> list, @NotNull TooltipFlag flag) {
        if(!stack.is(ItemHandler.mino_card_shapes.get(0).get())) {
            CompoundTag nbt = stack.getOrCreateTag();
            list.add(Component.translatable("Won from game: " + Integer.toHexString(nbt.getInt("pwd"))));
            list.add(Component.translatable("at time: " +
                    nbt.getString("time")).withStyle(ChatFormatting.AQUA));
        }
        super.appendHoverText(stack,level,list,flag);
    }
}

package com.meacks.table_game.assets.items;

import com.meacks.table_game.client.gui.MinoControlScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MinoControlCard extends Item {
    public MinoControlCard(Properties pProperties) {
        super(pProperties);
    }

    public MinoControlCard() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand interactionHand) {
        ItemStack stack = player.getItemInHand(interactionHand);
        if(player.isCrouching()) {
            Minecraft.getInstance().setScreen(new MinoControlScreen(stack));
        }
        return InteractionResultHolder.success(stack);
    }
}

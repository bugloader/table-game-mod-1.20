package com.meacks.table_game.client.event;

import com.meacks.table_game.TableGameMod;
import com.meacks.table_game.client.gui.MinoControlScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = TableGameMod.MODID)
public class UseControlCard {
    @SubscribeEvent
    public static void useControlCard(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack stack = player.getItemInHand(event.getHand());
        if (player.isShiftKeyDown()) {
            Minecraft.getInstance().setScreen(new MinoControlScreen(stack));
        }
    }
}

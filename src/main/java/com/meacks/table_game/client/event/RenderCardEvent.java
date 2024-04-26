package com.meacks.table_game.client.event;

import com.meacks.table_game.TableGameMod;
import com.meacks.table_game.assets.items.MinoHandCard;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = TableGameMod.MODID)
public class RenderCardEvent {
    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay() == VanillaGuiOverlay.HOTBAR.type()) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) {
                return;
            }
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() instanceof MinoHandCard) {
                GuiGraphics guiGraphics = event.getGuiGraphics();
                CompoundTag tag = stack.getOrCreateTag();
                int num = 8;
                int id = MinoHandCard.getCurrentCardIndex(stack);
                int playerId = tag.getInt("id");
                guiGraphics.fill((id % 8) * 40, 40 * (id / num),id * 40 + 35,45, Color.WHITE.getRGB());
                guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("table_game.text.player_id").append(" : ").append(String.valueOf(playerId)),10,40 * (id / num + 1) + 10,Color.WHITE.getRGB());
                ArrayList<ResourceLocation> textures = MinoHandCard.getAllCardTexture(stack).stream().map(ResourceLocation::new).collect(Collectors.toCollection(ArrayList::new));
                for (int i = 0; i < textures.size(); i++) {
                    int x = i % num;
                    ResourceLocation texture = textures.get(i);
                    guiGraphics.blit(texture, x * 40, 5 + 40 * (i / num), 0, 0, 0, 35, 35, 35, 35);
                }

            }
            RenderSystem.enableBlend();
        }
    }
}

package com.meacks.table_game.client.gui;

import com.meacks.table_game.common.inventory.MinoTableMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class MinoTableScreen extends AbstractContainerScreen<MinoTableMenu> {
    private static final ResourceLocation MINO_TABLE_LOCATION = new ResourceLocation(
        "table_game:textures/gui/game_table/mino_table.png");
    
    public MinoTableScreen(MinoTableMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        this.imageHeight = 166;
        this.imageWidth = 210;
        this.inventoryLabelY = this.imageHeight - 107;
    }
    
    @Override
    protected void init() {
        super.init();
        //ImageButton(x, y, width, height, xTexStart, yTexStart, yDiffTex, resource, texWidth, texHeight, onPress,
        // message)
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        int start_button_width = 39;
        int start_button_height = 14;
        int start_x = i + 85;
        int start_y = j + 148;
    }
    
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
    
    protected void renderBg(GuiGraphics p_281616_, float p_282737_, int p_281678_, int p_281465_) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        p_281616_.blit(MINO_TABLE_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }
}

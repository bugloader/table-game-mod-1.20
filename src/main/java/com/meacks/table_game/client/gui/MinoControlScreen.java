package com.meacks.table_game.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class MinoControlScreen extends Screen {
    ItemStack stack;
    EditBox numCreeperSet;
    EditBox numDiamondSet;
    EditBox numOcelotSet;
    EditBox numRedstoneSet;
    EditBox numSkipSet;
    EditBox numReverseSet;
    EditBox numDraw2Set;
    EditBox numDraw4Set;
    EditBox numWildSet;
    EditBox peopleSize;

    public MinoControlScreen(ItemStack stack) {
        super(Component.translatable("container.mino_control"));
        this.stack = stack;
    }
    public void init() {
        super.init();
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (button) ->
                this.onDone()).bounds(this.width / 2 - 125, 210, 250, 20).build());
        this.numCreeperSet = new EditBox(this.font,this.width / 2 - 125, this.height / 2 - 50, 50, 15, Component.translatable("container.mino_control.creeper_set"));
        this.numCreeperSet.setValue("2");
        this.addWidget(this.numCreeperSet);
        this.numDiamondSet = new EditBox(this.font, this.width / 2 - 55, this.height / 2 - 50, 50, 15, Component.translatable("container.mino_control.diamond_set"));
        this.numDiamondSet.setValue("2");
        this.addWidget(this.numDiamondSet);
        this.numOcelotSet = new EditBox(this.font, this.width / 2 + 15, this.height / 2 - 50, 50, 15, Component.translatable("container.mino_control.ocelot_set"));
        this.numOcelotSet.setValue("2");
        this.addWidget(this.numOcelotSet);
        this.numRedstoneSet = new EditBox(this.font, this.width /2 + 85, this.height / 2 - 50, 50, 15, Component.translatable("container.mino_control.redstone_set"));
        this.numRedstoneSet.setValue("2");
        this.addWidget(this.numRedstoneSet);
        this.numSkipSet = new EditBox(this.font, this.width / 2 - 125, this.height / 2, 50, 15, Component.translatable("container.mino_control.skip_set"));
        this.numSkipSet.setValue("2");
        this.addWidget(this.numSkipSet);
        this.numReverseSet = new EditBox(this.font, this.width / 2 - 55, this.height / 2, 50, 15, Component.translatable("container.mino_control.reverse_set"));
        this.numReverseSet.setValue("2");
        this.addWidget(this.numReverseSet);
        this.numDraw2Set = new EditBox(this.font, this.width / 2 + 15, this.height / 2, 50, 15, Component.translatable("container.mino_control.draw2_set"));
        this.numDraw2Set.setValue("1");
        this.addWidget(this.numDraw2Set);
        this.numDraw4Set = new EditBox(this.font, this.width / 2 + 85, this.height / 2, 50, 15, Component.translatable("container.mino_control.draw4_set"));
        this.numDraw4Set.setValue("1");
        this.addWidget(this.numDraw4Set);
        this.numWildSet = new EditBox(this.font, this.width / 2 - 125, this.height / 2 + 50, 50, 15, Component.translatable("container.mino_control.wild_set"));
        this.numWildSet.setValue("1");
        this.addWidget(this.numWildSet);
        this.peopleSize = new EditBox(this.font, this.width / 2 - 55, this.height / 2 + 50, 50, 15, Component.translatable("container.mino_control.people_size"));
        this.peopleSize.setValue("2");
        this.addWidget(this.peopleSize);
    }

    protected void onDone() {
        CompoundTag itemNbt = new CompoundTag();
        itemNbt.putInt("numCreeperSet", Integer.parseInt(this.numCreeperSet.getValue()));
        itemNbt.putInt("numDiamondSet", Integer.parseInt(this.numDiamondSet.getValue()));
        itemNbt.putInt("numOcelotSet", Integer.parseInt(this.numOcelotSet.getValue()));
        itemNbt.putInt("numRedstoneSet", Integer.parseInt(this.numRedstoneSet.getValue()));
        itemNbt.putInt("numSkipSet", Integer.parseInt(this.numSkipSet.getValue()));
        itemNbt.putInt("numReverseSet", Integer.parseInt(this.numReverseSet.getValue()));
        itemNbt.putInt("numDraw2Set", Integer.parseInt(this.numDraw2Set.getValue()));
        itemNbt.putInt("numDraw4Set", Integer.parseInt(this.numDraw4Set.getValue()));
        itemNbt.putInt("numWildSet", Integer.parseInt(this.numWildSet.getValue()));
        itemNbt.putInt("peopleSize", Integer.parseInt(this.peopleSize.getValue()));
        this.stack.setTag(itemNbt);
        Minecraft.getInstance().setScreen(null);
    }

    public void renderText(GuiGraphics guiGraphics){
        // 在每个文本框上方渲染一行文字
        int yOffset = -30;
        int textColor = Color.WHITE.getRGB();

        guiGraphics.drawString(this.font, this.numCreeperSet.getMessage(), this.width / 2 - 125, this.height / 2 + yOffset, textColor);
        guiGraphics.drawString(this.font, this.numDiamondSet.getMessage(), this.width / 2 - 55, this.height / 2 + yOffset, textColor);
        guiGraphics.drawString(this.font, this.numOcelotSet.getMessage(), this.width / 2 + 15, this.height / 2 + yOffset, textColor);
        guiGraphics.drawString(this.font, this.numRedstoneSet.getMessage(), this.width /2 + 85, this.height / 2 + yOffset, textColor);
        guiGraphics.drawString(this.font, this.numSkipSet.getMessage(), this.width / 2 - 125, this.height / 2 + yOffset + 50, textColor);
        guiGraphics.drawString(this.font, this.numReverseSet.getMessage(), this.width / 2 - 55, this.height / 2 + yOffset + 50, textColor);
        guiGraphics.drawString(this.font, this.numDraw2Set.getMessage(), this.width / 2 + 15, this.height / 2 + yOffset + 50, textColor);
        guiGraphics.drawString(this.font, this.numDraw4Set.getMessage(), this.width / 2 + 85, this.height / 2 + yOffset + 50, textColor);
        guiGraphics.drawString(this.font, this.numWildSet.getMessage(), this.width / 2 - 125, this.height / 2 + yOffset + 100, textColor);
        guiGraphics.drawString(this.font, this.peopleSize.getMessage(), this.width / 2 - 55, this.height / 2 + yOffset + 100, textColor);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
        renderText(guiGraphics);
        this.numCreeperSet.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
        this.numDiamondSet.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
        this.numOcelotSet.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
        this.numRedstoneSet.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
        this.numSkipSet.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
        this.numReverseSet.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
        this.numWildSet.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
        this.numDraw2Set.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
        this.numDraw4Set.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
        this.peopleSize.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
    }
}
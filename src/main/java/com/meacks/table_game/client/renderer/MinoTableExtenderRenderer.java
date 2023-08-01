package com.meacks.table_game.client.renderer;

import com.meacks.table_game.assets.blockEntities.UnoTableBlockEntity;
import com.meacks.table_game.assets.blockEntities.UnoTableExtenderBlockEntity;
import com.meacks.table_game.assets.handlers.ItemHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class UnoTableExtenderRenderer implements BlockEntityRenderer<UnoTableExtenderBlockEntity> {

    private final BlockEntityRendererProvider.Context context;

    public UnoTableExtenderRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull UnoTableExtenderBlockEntity tileEntityIn) {
        return false;
    }

    @Override
    public void render(@NotNull UnoTableExtenderBlockEntity tileEntityIn, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {




    }

}

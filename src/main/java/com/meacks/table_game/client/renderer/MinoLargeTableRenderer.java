package com.meacks.table_game.client.renderer;

import com.meacks.table_game.assets.blockEntities.MinoLargeTableBlockEntity;
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

import static com.meacks.table_game.assets.handlers.ItemHandler.mino_large_table;
import static com.meacks.table_game.assets.handlers.ItemHandler.mino_large_table_yellow;
import static com.meacks.table_game.client.renderer.CommonRenderingMethods.*;

public class MinoLargeTableRenderer implements BlockEntityRenderer<MinoLargeTableBlockEntity> {

    public MinoLargeTableRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(@NotNull MinoLargeTableBlockEntity tileEntityIn, float partialTick, PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        poseStack.pushPose();
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack stack = new ItemStack((tileEntityIn.inGame ? mino_large_table : mino_large_table_yellow).get());
        BakedModel bakedmodel = itemRenderer.getModel(stack, null, null, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(180f));
        poseStack.translate(-0.5f, 0.5f, -0.5f);
        itemRenderer.render(stack, ItemDisplayContext.FIXED, true, poseStack, bufferSource,
                combinedLight, combinedOverlay, bakedmodel);
        poseStack.popPose();
        renderingPlacedCards(tileEntityIn, poseStack, bufferSource, itemRenderer, combinedLight, combinedOverlay);
        renderingDrawDeck(tileEntityIn, poseStack, bufferSource, itemRenderer, combinedLight, combinedOverlay);
        renderingDepositDeck(tileEntityIn, poseStack, bufferSource, itemRenderer, combinedLight, combinedOverlay);
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull MinoLargeTableBlockEntity tileEntityIn) {
        return false;
    }

}

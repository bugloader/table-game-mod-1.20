package com.meacks.table_game.client.renderer;

import com.meacks.table_game.assets.blockEntities.UnoLargeTableBlockEntity;
import com.meacks.table_game.assets.blockEntities.UnoTableBlockEntity;
import com.meacks.table_game.assets.handlers.ItemHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class UnoLargeTableRenderer implements BlockEntityRenderer<UnoLargeTableBlockEntity> {

    private final BlockEntityRendererProvider.Context context;

    public UnoLargeTableRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull UnoLargeTableBlockEntity tileEntityIn) {
        return false;
    }

    public static final int RENDERING_CARD_NUM = 15;
    public void renderingPlacedCards(@NotNull UnoLargeTableBlockEntity tileEntityIn, PoseStack poseStack,
                                     MultiBufferSource bufferSource, ItemRenderer itemRenderer, int combinedLight,
                                     int combinedOverlay){
        CompoundTag tableNbt = tileEntityIn.getPersistentData();
        int cardsPlacedNum = tableNbt.getInt("numPlaced");
        for (int i = Math.max(0,cardsPlacedNum-RENDERING_CARD_NUM); i < cardsPlacedNum; i++) {
            double deltaX=tableNbt.getDouble(Integer.toString(i)+"x")-tileEntityIn.getBlockPos().getX();
            double deltaZ=tableNbt.getDouble(Integer.toString(i)+"z")-tileEntityIn.getBlockPos().getZ();
            float rotation = tableNbt.getFloat(Integer.toString(i)+"r");
            int id = tableNbt.getInt(Integer.toString(i));
            ItemStack stack = ItemHandler.mino_card_shapes.get(id).get().getDefaultInstance();
            BakedModel bakedmodel = itemRenderer.getModel(stack, null, null, 0);
            poseStack.pushPose();
            poseStack.mulPose(Axis.XP.rotationDegrees(90f));
            poseStack.translate(deltaX,deltaZ,-0.95f);
            poseStack.scale(0.2f,0.2f,0.2f);
            poseStack.mulPose(Axis.ZP.rotationDegrees(rotation));
            itemRenderer.render(stack, ItemDisplayContext.FIXED, true, poseStack, bufferSource, combinedLight,
                    combinedOverlay, bakedmodel);
            poseStack.popPose();
        }
    }

    public void renderingDeck(@NotNull UnoLargeTableBlockEntity tileEntityIn, PoseStack poseStack,
                                     MultiBufferSource bufferSource, ItemRenderer itemRenderer, int combinedLight,
                                     int combinedOverlay){
        CompoundTag tableNbt = tileEntityIn.getPersistentData();
        int deckNum = tableNbt.getInt("drawDeckNum");
        int cardsDrawNum = deckNum/10;
        float topHeight = (deckNum%10)*0.0085f;
        ItemStack stack = ItemHandler.mino_card_shapes.get(0).get().getDefaultInstance();
        BakedModel bakedmodel = itemRenderer.getModel(stack, null, null, 0);
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(90f));
        poseStack.translate(-0.8f,1.2f,-0.95f);
        poseStack.scale(0.2f,0.2f,0.2f);
        for (int i = 0; i < cardsDrawNum; i++) {
            poseStack.translate(0,0,-0.085f);
            itemRenderer.render(stack, ItemDisplayContext.FIXED, true, poseStack, bufferSource, combinedLight,
                    combinedOverlay, bakedmodel);
        }
        if(topHeight!=0){
            poseStack.translate(0,0,-topHeight);
            itemRenderer.render(stack, ItemDisplayContext.FIXED, true, poseStack, bufferSource, combinedLight,
                    combinedOverlay, bakedmodel);
        }
        poseStack.popPose();
    }

    public void renderingDepositDeck(@NotNull UnoLargeTableBlockEntity tileEntityIn, PoseStack poseStack,
                              MultiBufferSource bufferSource, ItemRenderer itemRenderer, int combinedLight,
                              int combinedOverlay){
        CompoundTag tableNbt = tileEntityIn.getPersistentData();
        int cardsPlacedNum = tableNbt.getInt("numPlaced");
        int depositNum = Math.max(0,cardsPlacedNum-RENDERING_CARD_NUM);
        int cardsDrawNum = depositNum/10;
        float topHeight = (depositNum%10)*0.0085f;
        ItemStack stack = ItemHandler.mino_card_shapes.get(0).get().getDefaultInstance();
        BakedModel bakedmodel = itemRenderer.getModel(stack, null, null, 0);
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(90f));
        poseStack.translate(-0.8f,0.8f,-0.95f);
        poseStack.scale(0.2f,0.2f,0.2f);
        for (int i = 0; i < cardsDrawNum; i++) {
            poseStack.translate(0,0,-0.085f);
            itemRenderer.render(stack, ItemDisplayContext.FIXED, true, poseStack, bufferSource, combinedLight,
                    combinedOverlay, bakedmodel);
        }
        if(topHeight!=0){
            poseStack.translate(0,0,-topHeight);
            itemRenderer.render(stack, ItemDisplayContext.FIXED, true, poseStack, bufferSource, combinedLight,
                    combinedOverlay, bakedmodel);
        }
        poseStack.popPose();
    }

    @Override
    public void render(@NotNull UnoLargeTableBlockEntity tileEntityIn, float partialTick, PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        poseStack.pushPose();
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack stack = new ItemStack(ItemHandler.uno_large_table.get());
        BakedModel bakedmodel = itemRenderer.getModel(stack, null, null, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(180f));
        poseStack.scale(1,1,1);
        poseStack.translate(0.5, 0.25, -0.25);
        itemRenderer.render(stack, ItemDisplayContext.FIXED, true, poseStack, bufferSource, combinedLight,
                combinedOverlay, bakedmodel);
        poseStack.popPose();
        renderingPlacedCards(tileEntityIn,poseStack,bufferSource,itemRenderer,combinedLight,combinedOverlay);
        renderingDeck(tileEntityIn,poseStack,bufferSource,itemRenderer,combinedLight,combinedOverlay);
        renderingDepositDeck(tileEntityIn,poseStack,bufferSource,itemRenderer,combinedLight,combinedOverlay);
    }

}

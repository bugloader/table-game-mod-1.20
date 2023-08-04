package com.meacks.table_game.client.renderer;

import com.meacks.table_game.assets.blockEntities.MinoCommonBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Random;

import static com.meacks.table_game.assets.handlers.ItemHandler.mino_card_shapes;

public class CommonRenderingMethods {
    static Random random = new Random();

    public static void renderingPlacedCards(@NotNull MinoCommonBlockEntity tileEntityIn,
                                            PoseStack poseStack,
                                            MultiBufferSource bufferSource,
                                            ItemRenderer itemRenderer,
                                            int combinedLight,
                                            int combinedOverlay) {
        int numPlaced = tileEntityIn.numPlaced;
        int DISPLAY_CARD_LIMIT = tileEntityIn.DISPLAY_CARD_LIMIT;
        int displayNum = Math.min(DISPLAY_CARD_LIMIT, numPlaced);
        int skippedNum = numPlaced - displayNum;
        for (int i = 0; i < displayNum; i++) {
            double deltaX = tileEntityIn.displayCardX[i] - tileEntityIn.getBlockPos().getX();
            double deltaZ = tileEntityIn.displayCardZ[i] - tileEntityIn.getBlockPos().getZ();
            float rotation = tileEntityIn.displayCardR[i];
            int id = tileEntityIn.placedCardId[i + skippedNum];
            ItemStack stack = mino_card_shapes.get(id).get().getDefaultInstance();
            if (i == numPlaced - 1) {
                stack.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 1);
                if (random.nextFloat() > 0.99f) {
                    double x = tileEntityIn.displayCardX[i];
                    double z = tileEntityIn.displayCardZ[i];
                    double y = tileEntityIn.getBlockPos().getY() + 1.5f;
                    Objects.requireNonNull(tileEntityIn.getLevel()).addParticle(ParticleTypes.ENCHANT,
                            x + random.nextFloat() / 10 - 0.05,
                            y + random.nextFloat() / 10,
                            z + random.nextFloat() / 10 - 0.05,
                            0, 0, 0);
                }
            }
            BakedModel bakedmodel = itemRenderer.getModel(stack, null, null, 0);
            poseStack.pushPose();
            poseStack.mulPose(Axis.XP.rotationDegrees(90f));
            poseStack.translate(deltaX, deltaZ, -0.95f);
            poseStack.scale(0.2f, 0.2f, 0.2f);
            poseStack.mulPose(Axis.ZP.rotationDegrees(rotation));
            itemRenderer.render(stack, ItemDisplayContext.FIXED, true, poseStack, bufferSource,
                    combinedLight, combinedOverlay, bakedmodel);
            poseStack.popPose();
        }
    }

    public static void renderingTableCardStack(int deckNum,
                                               PoseStack poseStack, MultiBufferSource bufferSource, ItemRenderer itemRenderer,
                                               int combinedLight, int combinedOverlay, int RENDERING_CARD_NUM,
                                               float translateX, float translateY, float translateZ) {
        int depositNum = Math.max(0, deckNum - RENDERING_CARD_NUM);
        int cardsDrawNum = depositNum / 10;
        float topHeight = (depositNum % 10) * 0.0075f;
        ItemStack stack = mino_card_shapes.get(0).get().getDefaultInstance();
        BakedModel bakedmodel = itemRenderer.getModel(stack, null, null, 0);
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(90f));
        poseStack.translate(translateX, translateY, translateZ);
        poseStack.scale(0.2f, 0.2f, 0.2f);
        for (int i = 0; i < cardsDrawNum; i++) {
            poseStack.translate(0, 0, -0.075f);
            itemRenderer.render(stack, ItemDisplayContext.FIXED, true, poseStack, bufferSource,
                    combinedLight, combinedOverlay, bakedmodel);
        }
        if (topHeight != 0) {
            poseStack.translate(0, 0, -topHeight);
            itemRenderer.render(stack, ItemDisplayContext.FIXED, true, poseStack, bufferSource,
                    combinedLight, combinedOverlay, bakedmodel);
        }
        poseStack.popPose();
    }

    public static void renderingDrawDeck(@NotNull MinoCommonBlockEntity tileEntityIn,
                                         PoseStack poseStack, MultiBufferSource bufferSource, ItemRenderer itemRenderer,
                                         int combinedLight, int combinedOverlay) {
        renderingTableCardStack(tileEntityIn.drawDeckNum, poseStack, bufferSource, itemRenderer,
                combinedLight, combinedOverlay, 0,
                -0.7f, 1.2f, -0.94f);
    }

    public static void renderingDepositDeck(@NotNull MinoCommonBlockEntity tileEntityIn,
                                            PoseStack poseStack, MultiBufferSource bufferSource, ItemRenderer itemRenderer,
                                            int combinedLight, int combinedOverlay) {
        renderingTableCardStack(tileEntityIn.numPlaced, poseStack, bufferSource, itemRenderer,
                combinedLight, combinedOverlay, tileEntityIn.DISPLAY_CARD_LIMIT,
                -0.7f, 0.8f, -0.94f);
    }
}

package com.meacks.table_game.client.renderer;

import com.meacks.table_game.assets.handlers.ItemHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Random;

public class CommonRenderingMethods {
    static Random random = new Random();

    public static void renderingPlacedCards(@NotNull BlockEntity tileEntityIn, PoseStack poseStack,
                                     MultiBufferSource bufferSource, ItemRenderer itemRenderer, int combinedLight,
                                     int combinedOverlay, int RENDERING_CARD_NUM){
        CompoundTag tableNbt = tileEntityIn.getPersistentData();
        int cardsPlacedNum = tableNbt.getInt("numPlaced");
        for (int i = Math.max(0,cardsPlacedNum-RENDERING_CARD_NUM); i < cardsPlacedNum; i++) {
            double deltaX=tableNbt.getDouble(i +"x")-tileEntityIn.getBlockPos().getX();
            double deltaZ=tableNbt.getDouble(i +"z")-tileEntityIn.getBlockPos().getZ();
            float rotation = tableNbt.getFloat(i +"r");
            int id = tableNbt.getInt(Integer.toString(i));
            ItemStack stack = ItemHandler.mino_card_shapes.get(id).get().getDefaultInstance();
            if(i==cardsPlacedNum-1){
                stack.enchant(Enchantments.ALL_DAMAGE_PROTECTION,1);
                if(random.nextFloat()>0.99f){
                    double x=tableNbt.getDouble(Integer.toString(i)+"x");
                    double z=tableNbt.getDouble(Integer.toString(i)+"z");
                    double y=tileEntityIn.getBlockPos().getY()+1.5f;
                    Objects.requireNonNull(tileEntityIn.getLevel()).addParticle(ParticleTypes.ENCHANT,
                            x+random.nextFloat()/10-0.05, y+random.nextFloat()/10,
                            z+random.nextFloat()/10-0.05, 0, 0, 0);
                }
            }
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

    public static void renderingDeck(@NotNull BlockEntity tileEntityIn, PoseStack poseStack,
                              MultiBufferSource bufferSource, ItemRenderer itemRenderer, int combinedLight,
                              int combinedOverlay){
        CompoundTag tableNbt = tileEntityIn.getPersistentData();
        int deckNum = tableNbt.getInt("drawDeckNum");
        int cardsDrawNum = deckNum/10;
        float topHeight = (deckNum%10)*0.008f;
        ItemStack stack = ItemHandler.mino_card_shapes.get(0).get().getDefaultInstance();
        BakedModel bakedmodel = itemRenderer.getModel(stack, null, null, 0);
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(90f));
        poseStack.translate(-0.7f,1.2f,-0.94f);
        poseStack.scale(0.2f,0.2f,0.2f);
        for (int i = 0; i < cardsDrawNum; i++) {
            poseStack.translate(0,0,-0.08f);
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

    public static void renderingDepositDeck(@NotNull BlockEntity tileEntityIn, PoseStack poseStack,
                                     MultiBufferSource bufferSource, ItemRenderer itemRenderer, int combinedLight,
                                     int combinedOverlay,int RENDERING_CARD_NUM){
        CompoundTag tableNbt = tileEntityIn.getPersistentData();
        int cardsPlacedNum = tableNbt.getInt("numPlaced");
        int depositNum = Math.max(0,cardsPlacedNum-RENDERING_CARD_NUM);
        int cardsDrawNum = depositNum/10;
        float topHeight = (depositNum%10)*0.008f;
        ItemStack stack = ItemHandler.mino_card_shapes.get(0).get().getDefaultInstance();
        BakedModel bakedmodel = itemRenderer.getModel(stack, null, null, 0);
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(90f));
        poseStack.translate(-0.7f,0.8f,-0.94f);
        poseStack.scale(0.2f,0.2f,0.2f);
        for (int i = 0; i < cardsDrawNum; i++) {
            poseStack.translate(0,0,-0.08f);
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
}

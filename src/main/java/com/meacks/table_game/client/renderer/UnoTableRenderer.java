package com.meacks.table_game.client.renderer;

import com.meacks.table_game.assets.blockEntities.SmallGameTableBlockEntity;
import com.meacks.table_game.assets.blockEntities.UnoTableBlockEntity;
import com.meacks.table_game.assets.handlers.ItemHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

public class UnoTableRenderer implements BlockEntityRenderer<UnoTableBlockEntity> {

    private final BlockEntityRendererProvider.Context context;

    public UnoTableRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull UnoTableBlockEntity tileEntityIn) {
        return false;
    }

    public void renderingPlacedCards(@NotNull UnoTableBlockEntity tileEntityIn, PoseStack poseStack,
                                     MultiBufferSource bufferSource, ItemRenderer itemRenderer, int combinedLight,
                                     int combinedOverlay){
        CompoundTag tableNbt = tileEntityIn.getPersistentData();
        int cardsPlacedNum = tableNbt.getInt("numPlaced");
        for (int i = 0; i < cardsPlacedNum; i++) {
            double deltaX=tableNbt.getDouble(Integer.toString(i)+"x")-tileEntityIn.getBlockPos().getX();
            double deltaZ=tableNbt.getDouble(Integer.toString(i)+"z")-tileEntityIn.getBlockPos().getZ();
            float rotation = tableNbt.getFloat(Integer.toString(i)+"r");
            int id = tableNbt.getInt(Integer.toString(i));
            ItemStack stack = ItemHandler.mino_card_shapes.get(id).get().getDefaultInstance();
            BakedModel bakedmodel = itemRenderer.getModel(stack, null, null, 0);
            poseStack.pushPose();
            poseStack.mulPose(Axis.XP.rotationDegrees(90f));
            poseStack.translate(deltaX,deltaZ,-0.95f);
            poseStack.scale(0.25f,0.25f,0.25f);
            poseStack.mulPose(Axis.ZN.rotationDegrees(rotation));
            itemRenderer.render(stack, ItemDisplayContext.FIXED, true, poseStack, bufferSource, combinedLight,
                    combinedOverlay, bakedmodel);
            poseStack.popPose();
            System.out.println(deltaX);
        }
    }

    @Override
    public void render(@NotNull UnoTableBlockEntity tileEntityIn, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {

        poseStack.pushPose();
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack stack = new ItemStack(ItemHandler.uno_table.get());
        BakedModel bakedmodel = itemRenderer.getModel(stack, null, null, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(180f));
        poseStack.scale(2,2,2);
        poseStack.translate(0.5, 0.25, -0.25);
        itemRenderer.render(stack, ItemDisplayContext.FIXED, true, poseStack, bufferSource, combinedLight,
                combinedOverlay, bakedmodel);
        poseStack.popPose();
        renderingPlacedCards(tileEntityIn,poseStack,bufferSource,itemRenderer,combinedLight,combinedOverlay);

    }

}

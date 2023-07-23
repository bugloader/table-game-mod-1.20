package com.meacks.table_game.renderer;

import com.meacks.table_game.blockEntities.SmallGameTableBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

import java.util.Deque;

public class SmallGameTableRenderer implements BlockEntityRenderer<SmallGameTableBlockEntity> {

    private final BlockEntityRendererProvider.Context context;
    public SmallGameTableRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull SmallGameTableBlockEntity smallGameTableBlockEntity) {
        return false;
    }

    @Override
    public void render(@NotNull SmallGameTableBlockEntity tileEntityIn, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {

        poseStack.pushPose();
        //poseStack.translate(1, 0, 0);

        BakedModel model = context.getBlockRenderDispatcher().getBlockModel(tileEntityIn.getBlockState());
        context.getBlockRenderDispatcher().getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(RenderType.translucent()), null, model,
                1, 1, 1, combinedLight, combinedOverlay, ModelData.EMPTY, RenderType.translucent());
        poseStack.popPose();
        poseStack.pushPose();
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack stack = new ItemStack(Items.DIAMOND);
        BakedModel bakedmodel = itemRenderer.getModel(stack, null, null,0);
        poseStack.mulPose(Axis.XP.rotationDegrees(90f));
        poseStack.mulPose(Axis.YP.rotationDegrees(2f));
        poseStack.translate(0.5,0.5,-0.5);
        itemRenderer.render(stack, ItemDisplayContext.FIXED,true,poseStack,bufferSource,combinedLight,combinedOverlay,bakedmodel);
        poseStack.popPose();


    }

}

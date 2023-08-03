package com.meacks.table_game.client.renderer;

import com.meacks.table_game.assets.blockEntities.MinoTableExtenderBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.jetbrains.annotations.NotNull;

public class MinoTableExtenderRenderer implements BlockEntityRenderer<MinoTableExtenderBlockEntity> {
    
    private final BlockEntityRendererProvider.Context context;
    
    public MinoTableExtenderRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }
    
    @Override
    public void render(@NotNull MinoTableExtenderBlockEntity tileEntityIn,
                       float partialTick,
                       PoseStack poseStack,
                       MultiBufferSource bufferSource,
                       int combinedLight,
                       int combinedOverlay) {
        
        
    }
    
    @Override
    public boolean shouldRenderOffScreen(@NotNull MinoTableExtenderBlockEntity tileEntityIn) {
        return false;
    }
    
}

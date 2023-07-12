package com.meacks.table_game.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;



@Mixin(ItemInHandRenderer.class)
public class MixinsItemInHandRenderer {


    @Inject(at=@At(value="HEAD"),method = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderMap(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/ItemStack;)V")
    private void renderMap(PoseStack p_109367_, MultiBufferSource p_109368_, int p_109369_, ItemStack p_109370_, CallbackInfo ci) {
        p_109367_.mulPose(Axis.YP.rotationDegrees(180.0F));
        p_109367_.mulPose(Axis.ZP.rotationDegrees(180.0F));
        p_109367_.scale(0.38F, 0.38F, 0.38F);
        p_109367_.translate(-0.5F, -0.5F, 0.0F);
        p_109367_.scale(0.0078125F, 0.0078125F, 0.0078125F);
        //Integer integer = MapItem.getMapId(p_109370_);p_234241_
        //MapItemSavedData mapitemsaveddata = MapItem.getSavedData(integer, this.minecraft.level);
        //VertexConsumer vertexconsumer = p_109368_.getBuffer(mapitemsaveddata == null ? MAP_BACKGROUND : MAP_BACKGROUND_CHECKERBOARD);


    }
}

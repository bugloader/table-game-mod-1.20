package com.meacks.table_game.client.mixin;

import com.meacks.table_game.assets.handlers.ItemHandler;
import com.meacks.table_game.assets.items.MinoHandCard;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ItemInHandRenderer.class)
public abstract class MixinsItemInHandRenderer {

    @Final
    @Shadow
    private Minecraft minecraft;

    @Inject(at = @At(value = "HEAD"),
            method = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderArmWithItem" +
                    "(Lnet/minecraft/client/player/AbstractClientPlayer;FFLnet/minecraft/world/InteractionHand;" +
                    "FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;" +
                    "Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            cancellable = true)
    private void renderArmWithItem(AbstractClientPlayer p_109372_, float p_109373_, float p_109374_,
                                   InteractionHand p_109375_, float p_109376_, ItemStack p_109377_,
                                   float p_109378_, PoseStack p_109379_, MultiBufferSource p_109380_,
                                   int p_109381_, CallbackInfo ci) {
        if (!p_109372_.isScoping()) {
            boolean flag = p_109375_ == InteractionHand.MAIN_HAND;
            HumanoidArm humanoidarm = flag ? p_109372_.getMainArm() : p_109372_.getMainArm()
                    .getOpposite();
            if (p_109377_.is(ItemHandler.mino_hand_card.get())) {
                this.table_game_mod_1_20$renderOneHandedMap(
                        p_109379_, p_109380_, p_109381_, p_109378_, humanoidarm, p_109376_, p_109377_);
                ci.cancel();
            }
        }
    }

    @Unique
    private void table_game_mod_1_20$renderOneHandedMap(PoseStack p_109354_,
                                                        MultiBufferSource p_109355_,
                                                        int p_109356_,
                                                        float p_109357_,
                                                        HumanoidArm p_109358_,
                                                        float p_109359_,
                                                        ItemStack p_109360_) {
        float f = p_109358_ == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        p_109354_.translate(f * 0.125F, -0.125F, 0.0F);
        assert this.minecraft.player != null;
        if (!this.minecraft.player.isInvisible()) {
            p_109354_.pushPose();
            p_109354_.mulPose(Axis.ZP.rotationDegrees(f * 10.0F));
            this.renderPlayerArm(p_109354_, p_109355_, p_109356_, p_109357_, p_109359_, p_109358_);
            p_109354_.popPose();
        }

        p_109354_.pushPose();
        p_109354_.translate(f * 0.51F, -0.08F + p_109357_ * -1.2F, -0.75F);
        float f1 = Mth.sqrt(p_109359_);
        float f2 = Mth.sin(f1 * (float) Math.PI);
        float f3 = -0.5F * f2;
        float f4 = 0.4F * Mth.sin(f1 * ((float) Math.PI * 2F));
        float f5 = -0.3F * Mth.sin(p_109359_ * (float) Math.PI);
        p_109354_.translate(f * f3, f4 - 0.3F * f2, f5);
        p_109354_.mulPose(Axis.XP.rotationDegrees(f2 * -45.0F));
        p_109354_.mulPose(Axis.YP.rotationDegrees(f * f2 * -30.0F));
        this.table_game_mod_1_20$renderCard(p_109354_, p_109355_, p_109356_, p_109360_);
        p_109354_.popPose();
    }

    @Shadow
    private void renderPlayerArm(PoseStack p_109347_,
                                 MultiBufferSource p_109348_,
                                 int p_109349_,
                                 float p_109350_,
                                 float p_109351_,
                                 HumanoidArm p_109352_) {
        throw new IllegalStateException("Mixin failed to shadow renderPlayerArm()");
    }

    //Main implementation
    @Unique
    private void table_game_mod_1_20$renderCard(PoseStack p_109367_,
                                                MultiBufferSource p_109368_,
                                                int p_109369_,
                                                ItemStack p_109370_) {
        p_109367_.mulPose(Axis.YP.rotationDegrees(180.0F));
        p_109367_.mulPose(Axis.ZP.rotationDegrees(180.0F));
        p_109367_.scale(0.3F, 0.3F, 0.3F);
        //p_109367_.translate(0F, 0F, 0.0F);
        p_109367_.scale(0.0078125F, 0.0078125F, 0.0078125F);
        VertexConsumer vertexconsumer =
                p_109368_.getBuffer(RenderType.text(new ResourceLocation(MinoHandCard.getCurrentCardTexture(
                        p_109370_))));
        Matrix4f matrix4f = p_109367_.last()
                .pose();
        vertexconsumer.vertex(matrix4f, -7.0F, 135.0F, 0.0F)
                .color(255, 255, 255, 255)
                .uv(0.0F, 1.0F)
                .uv2(p_109369_)
                .endVertex();
        vertexconsumer.vertex(matrix4f, 135.0F, 135.0F, 0.0F)
                .color(255, 255, 255, 255)
                .uv(1.0F, 1.0F)
                .uv2(p_109369_)
                .endVertex();
        vertexconsumer.vertex(matrix4f, 135.0F, -7.0F, 0.0F)
                .color(255, 255, 255, 255)
                .uv(1.0F, 0.0F)
                .uv2(p_109369_)
                .endVertex();
        vertexconsumer.vertex(matrix4f, -7.0F, -7.0F, 0.0F)
                .color(255, 255, 255, 255)
                .uv(0.0F, 0.0F)
                .uv2(p_109369_)
                .endVertex();
    }
}
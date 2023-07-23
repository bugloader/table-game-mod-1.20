package com.meacks.table_game.mixin;

import com.meacks.table_game.TableGameMod;
import com.meacks.table_game.handlers.ItemHandler;
import com.meacks.table_game.items.MinoHandCard;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.*;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ItemInHandRenderer.class)
public abstract class MixinsItemInHandRenderer {

    @Final
    @Shadow
    private Minecraft minecraft;
    @Shadow
    private ItemStack mainHandItem;
    @Shadow
    private ItemStack offHandItem;
    @Final
    @Shadow
    private EntityRenderDispatcher entityRenderDispatcher;

    @Shadow
    private float calculateMapTilt(float p_109313_) {
        throw new IllegalStateException("Mixin failed to shadow calculateMapTilt()");
    }
    @Shadow
    private void renderPlayerArm(PoseStack p_109347_, MultiBufferSource p_109348_, int p_109349_, float p_109350_,
                                 float p_109351_, HumanoidArm p_109352_) {
        throw new IllegalStateException("Mixin failed to shadow renderPlayerArm()");
    }

    //Main implementation
     private void renderMap(PoseStack p_109367_, MultiBufferSource p_109368_, int p_109369_, ItemStack p_109370_) {
        p_109367_.mulPose(Axis.YP.rotationDegrees(180.0F));
        p_109367_.mulPose(Axis.ZP.rotationDegrees(180.0F));
        p_109367_.scale(0.38F, 0.38F, 0.38F);
        p_109367_.translate(-0.5F, -0.5F, 0.0F);
        p_109367_.scale(0.0078125F, 0.0078125F, 0.0078125F);
        VertexConsumer vertexconsumer = p_109368_.getBuffer(RenderType.text(
                new ResourceLocation(MinoHandCard.getCurrentCardPath(p_109370_))));
        Matrix4f matrix4f = p_109367_.last().pose();
        vertexconsumer.vertex(matrix4f, -7.0F, 135.0F, 0.0F).color(255,
                255, 255, 255).uv(0.0F, 1.0F).uv2(p_109369_).endVertex();
        vertexconsumer.vertex(matrix4f, 135.0F, 135.0F, 0.0F).color(255,
                255, 255, 255).uv(1.0F, 1.0F).uv2(p_109369_).endVertex();
        vertexconsumer.vertex(matrix4f, 135.0F, -7.0F, 0.0F).color(255,
                255, 255, 255).uv(1.0F, 0.0F).uv2(p_109369_).endVertex();
        vertexconsumer.vertex(matrix4f, -7.0F, -7.0F, 0.0F).color(255,
                255, 255, 255).uv(0.0F, 0.0F).uv2(p_109369_).endVertex();
    }


    private void renderMapHand(PoseStack p_109362_, MultiBufferSource p_109363_, int p_109364_, HumanoidArm p_109365_) {
        RenderSystem.setShaderTexture(0, this.minecraft.player.getSkinTextureLocation());
        PlayerRenderer playerrenderer = (PlayerRenderer)
                this.entityRenderDispatcher.<AbstractClientPlayer>getRenderer(this.minecraft.player);
        p_109362_.pushPose();
        float f = p_109365_ == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        p_109362_.mulPose(Axis.YP.rotationDegrees(92.0F));
        p_109362_.mulPose(Axis.XP.rotationDegrees(45.0F));
        p_109362_.mulPose(Axis.ZP.rotationDegrees(f * -41.0F));
        p_109362_.translate(f * 0.3F, -1.1F, 0.45F);
        if (p_109365_ == HumanoidArm.RIGHT) {
            playerrenderer.renderRightHand(p_109362_, p_109363_, p_109364_, this.minecraft.player);
        } else {
            playerrenderer.renderLeftHand(p_109362_, p_109363_, p_109364_, this.minecraft.player);
        }

        p_109362_.popPose();
    }

    private void renderOneHandedMap(PoseStack p_109354_, MultiBufferSource p_109355_, int p_109356_, float p_109357_,
                                    HumanoidArm p_109358_, float p_109359_, ItemStack p_109360_) {
        float f = p_109358_ == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        p_109354_.translate(f * 0.125F, -0.125F, 0.0F);
        if (!this.minecraft.player.isInvisible()) {
            p_109354_.pushPose();
            p_109354_.mulPose(Axis.ZP.rotationDegrees(f * 10.0F));
            this.renderPlayerArm(p_109354_, p_109355_, p_109356_, p_109357_, p_109359_, p_109358_);
            p_109354_.popPose();
        }

        p_109354_.pushPose();
        p_109354_.translate(f * 0.51F, -0.08F + p_109357_ * -1.2F, -0.75F);
        float f1 = Mth.sqrt(p_109359_);
        float f2 = Mth.sin(f1 * (float)Math.PI);
        float f3 = -0.5F * f2;
        float f4 = 0.4F * Mth.sin(f1 * ((float)Math.PI * 2F));
        float f5 = -0.3F * Mth.sin(p_109359_ * (float)Math.PI);
        p_109354_.translate(f * f3, f4 - 0.3F * f2, f5);
        p_109354_.mulPose(Axis.XP.rotationDegrees(f2 * -45.0F));
        p_109354_.mulPose(Axis.YP.rotationDegrees(f * f2 * -30.0F));
        this.renderMap(p_109354_, p_109355_, p_109356_, p_109360_);
        p_109354_.popPose();
    }
    private void renderTwoHandedMap(PoseStack p_109340_, MultiBufferSource p_109341_, int p_109342_, float p_109343_,
                                    float p_109344_, float p_109345_) {
        float f = Mth.sqrt(p_109345_);
        float f1 = -0.2F * Mth.sin(p_109345_ * (float)Math.PI);
        float f2 = -0.4F * Mth.sin(f * (float)Math.PI);
        p_109340_.translate(0.0F, -f1 / 2.0F, f2);
        float f3 = this.calculateMapTilt(p_109343_);
        p_109340_.translate(0.0F, 0.04F + p_109344_ * -1.2F + f3 * -0.5F, -0.72F);
        p_109340_.mulPose(Axis.XP.rotationDegrees(f3 * -85.0F));
        if (!this.minecraft.player.isInvisible()) {
            p_109340_.pushPose();
            p_109340_.mulPose(Axis.YP.rotationDegrees(90.0F));
            this.renderMapHand(p_109340_, p_109341_, p_109342_, HumanoidArm.RIGHT);
            this.renderMapHand(p_109340_, p_109341_, p_109342_, HumanoidArm.LEFT);
            p_109340_.popPose();
        }

        float f4 = Mth.sin(f * (float)Math.PI);
        p_109340_.mulPose(Axis.XP.rotationDegrees(f4 * 20.0F));
        p_109340_.scale(2.0F, 2.0F, 2.0F);
        this.renderMap(p_109340_, p_109341_, p_109342_, this.mainHandItem);
    }


    @Inject(at = @At(value = "HEAD"), method = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderArmWithItem(Lnet/minecraft/client/player/AbstractClientPlayer;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", cancellable = true)
    private void renderArmWithItem(AbstractClientPlayer p_109372_, float p_109373_, float p_109374_,
                                   InteractionHand p_109375_, float p_109376_, ItemStack p_109377_, float p_109378_,
                                   PoseStack p_109379_, MultiBufferSource p_109380_, int p_109381_, CallbackInfo ci) {
        if (!p_109372_.isScoping()) {
            boolean flag = p_109375_ == InteractionHand.MAIN_HAND;
            HumanoidArm humanoidarm = flag ? p_109372_.getMainArm() : p_109372_.getMainArm().getOpposite();
            if (p_109377_.is(ItemHandler.mino_hand_card.get())) {
                if (flag && this.offHandItem.isEmpty()) {
                    this.renderTwoHandedMap(p_109379_, p_109380_, p_109381_, p_109374_, p_109378_, p_109376_);
                } else {
                    this.renderOneHandedMap(p_109379_, p_109380_, p_109381_, p_109378_, humanoidarm, p_109376_, p_109377_);
                }
                ci.cancel();
            }
        }
    }
}

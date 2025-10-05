package mods.samoclutch.gh1218.mixin.client;

import mods.samoclutch.gh1218.HorseRidingClientPlayer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Unique
    private float horseSideThrow = 1.5f;
    @Shadow
    protected void moveBy(float f, float g, float h) {}
    @Shadow
    private float clipToSpace(float max) {return max - 1;}
    @Shadow
    protected abstract void setRotation(float yaw, float pitch);
    @Shadow
    private float pitch;
    @Shadow
    private float yaw;

    @Inject(method = "update", at = @At("RETURN"))
    public void onUpdate(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickProgress, CallbackInfo ci) {
        // offsets third person camera by a little bit and rotates it to face the player's theoretical target
        if (thirdPerson && focusedEntity.hasVehicle() && focusedEntity.getVehicle() instanceof AbstractHorseEntity horseEntity) {
            // find the distance the camera has to the rider
            float h = horseEntity.getScale();
            float i = (float)horseEntity.getAttributeValue(EntityAttributes.CAMERA_DISTANCE);
            float f = 4.0F;
            float g = 1.0F;
            if (focusedEntity instanceof LivingEntity livingEntity) {
                g = livingEntity.getScale();
                f = (float)livingEntity.getAttributeValue(EntityAttributes.CAMERA_DISTANCE);
            }
            // amount behind the rider the camera is
            float displacement = Math.max(g * f, h * i);

            horseSideThrow = 2f;

            if (focusedEntity instanceof ClientPlayerEntity cpe) {
                if (((HorseRidingClientPlayer)cpe).getZoomInput()) {
                    this.moveBy((displacement -
                            (displacement /= 5)) // change the constant to get closer to the head
                            , 0.0f, 0.0f);
                    horseSideThrow = .75f;
                }
                if (((HorseRidingClientPlayer) cpe).getCameraPositionInput() == 1
                        || ((HorseRidingClientPlayer) cpe).getCameraPositionInput() == 3) {
                    horseSideThrow = 0;
                } else if (((HorseRidingClientPlayer) cpe).getCameraPositionInput() == 2) {
                    horseSideThrow *= -1;
                }
            }

            displacement = this.clipToSpace(displacement);

            // sidestep the camera
            this.moveBy(0, 0, this.clipToSpace(horseSideThrow));

            // rotate to face 3 blocks in front of the player
            this.setRotation(this.yaw - 90 + (float)(MathHelper.atan2(displacement + 3, horseSideThrow) * (180.0/3.14159262)), this.pitch);
        }
    }
}

package mods.samoclutch.gh1211.mixin.client;

import mods.samoclutch.gh1211.client.Gh1211Client;
import mods.samoclutch.gh1211.client.GhConfig;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow
    private BlockView area;

    @Shadow
    private Vec3d pos;

    @Shadow
    private boolean ready;
    @Shadow
    private Entity focusedEntity;
    @Shadow
    private boolean thirdPerson;
    @Shadow
    private float lastTickDelta;
    @Shadow
    private float lastCameraY;
    @Shadow
    private float cameraY;

    @Shadow
    protected void moveBy(float f, float g, float h) {}
    @Shadow
    protected abstract void setRotation(float yaw, float pitch);
    @Shadow
    protected void setPos(double x, double y, double z) {}
    @Shadow
    private float pitch;
    @Shadow
    private float yaw;

    @Inject(method = "update", at = @At("RETURN"), cancellable = true)
    public void onUpdate(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickProgress, CallbackInfo ci) {
        // offsets third person camera by a little bit and rotates it to face the player's theoretical target
        if (thirdPerson && focusedEntity.hasVehicle() && focusedEntity.getVehicle() instanceof AbstractHorseEntity) {
//            ((ClientPlayerEntity)focusedEntity).sendMessage(Text.literal("We in here"));
            // short circuit case code copy
            this.ready = true;
            this.area = area;
            this.focusedEntity = focusedEntity;
            this.thirdPerson = thirdPerson;
            this.lastTickDelta = tickProgress;

            this.setRotation(focusedEntity.getYaw(tickProgress), focusedEntity.getPitch(tickProgress));
            this.setPos(
                    MathHelper.lerp(tickProgress, focusedEntity.prevX, focusedEntity.getX()),
                    MathHelper.lerp(tickProgress, focusedEntity.prevY, focusedEntity.getY()) + MathHelper.lerp(tickProgress, this.lastCameraY, this.cameraY),
                    MathHelper.lerp(tickProgress, focusedEntity.prevZ, focusedEntity.getZ())
            );

            if (inverseView) {
                this.setRotation(this.yaw + 180.0F, -this.pitch);
            }

            // find the distance the camera has to the rider
            LivingEntity livingEntity = (LivingEntity) focusedEntity;
            float f = livingEntity.getScale();
            // amount behind the rider the camera is by default
            float displacement = 4.0f * f;

            float horseThrow;
            float horseLoft;

            if (Gh1211Client.zoomSticky) {
                displacement *= (float) GhConfig.cameraZoomFactor;
                horseThrow = (float) (displacement * GhConfig.cameraThrowZoomedFactor);
                horseLoft = (float) (displacement * GhConfig.cameraLoftZoomedFactor);
            } else {
                horseThrow = (float) (displacement * GhConfig.cameraThrowFactor);
                horseLoft = (float) (displacement * GhConfig.cameraLoftFactor);
            }
            if (Gh1211Client.overShoulderPosition % 2 == 1) {
                horseThrow = 0;
                if (Gh1211Client.overShoulderPosition == 3) {
                    horseLoft = 0;
                }
            } else if (Gh1211Client.overShoulderPosition == 2) {
                horseThrow *= -1;
            }

            Vec3d storePos = new Vec3d(this.pos.x, this.pos.y, this.pos.z);

            // acquire position (point of aim) blocks ahead

            this.moveBy((float) GhConfig.cameraPointOfAimDistance, 0, 0);
            Vec3d pointOfAim = new Vec3d(this.pos.x, this.pos.y, this.pos.z);

            this.setPos(storePos.x, storePos.y, storePos.z);

            this.moveBy(-displacement, horseLoft, horseThrow);

            // vector of small, identical numbers in the direction from the goal location to the player
            Vec3d bonusVec = new Vec3d(MathHelper.sign(storePos.x - this.pos.x),
                    MathHelper.sign(storePos.x - this.pos.x),
                    MathHelper.sign(storePos.x - this.pos.x)).multiply(.1);

            HitResult cameraCheck = this.area.raycast(new RaycastContext(storePos,
                    this.pos.subtract(bonusVec), // increase raycast range
                    RaycastContext.ShapeType.VISUAL,
                    RaycastContext.FluidHandling.NONE,
                    focusedEntity));

            if (cameraCheck.getType() != HitResult.Type.MISS) {
                Vec3d pokedPos = cameraCheck.getPos().add(bonusVec);
                this.setPos(pokedPos.x, pokedPos.y, pokedPos.z);
            }

            // rotate to face 3 blocks in front of the player
            this.setRotation(
                    (float) MathHelper.atan2(
                            pointOfAim.x - this.pos.x,
                            pointOfAim.z - this.pos.z
                    ) * -180f / 3.1415926f,

                    (float) (MathHelper.atan2(
                            this.pos.y - pointOfAim.y,
                            MathHelper.sqrt((float) (MathHelper.square(this.pos.z - pointOfAim.z)
                                    + MathHelper.square(this.pos.x - pointOfAim.x)))
                    ) * 180.0 / 3.1415926));
            ci.cancel();
        }
    }
}

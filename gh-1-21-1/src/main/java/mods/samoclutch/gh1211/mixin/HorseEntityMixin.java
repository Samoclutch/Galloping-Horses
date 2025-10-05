package mods.samoclutch.gh1211.mixin;

import mods.samoclutch.gh1211.HorseRidingClientPlayer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Math;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorseEntity.class)
public abstract class HorseEntityMixin extends AnimalEntity {
    // which run mode the horse is in
    // 0 - plot, 1 - trot, 2 - gallop
    @Unique
    private int runningState = 0;
    // maximum speed (percentage) at each running state
    @Unique
    private double[] maxSpeeds = {.35, .65, 1};
    // bottom of speed ranges
    @Unique
    private double[] minSpeeds = {0, .5, .8};
    // updates to show the current speed of the horse, between 0 and 1
    @Unique
    private double speed = 0;
    // how much your speed increases from holding a key each tick
    @Unique
    private final double acceleration = .025;
    // updates to show the current turn rate
    @Unique
    private float angularVelocity = 0;
    // speed at which angular velocity changes
    @Unique
    private final float baseAngularAcceleration = 1f;
    // difference in angles between input and current at which the horse doesn't turn at full speed
    @Unique
    private final float slowDownAngle = 20;
    // maximum turn rate
    @Unique
    private final float angularVelocityMax = 5f;
    // angle from keyboard input (normalized and biased)
    // {AS, S, DS}
    // {A, NO, D }
    // {AW, W, DW}
    @Unique
    private static final float[][] angleLookup = {{135, 180, 225}, {90, -1, 270}, {45, 0, 315}};

    protected HorseEntityMixin(EntityType<? extends AbstractHorseEntity> entityType, World world) {super(entityType, world);}

    @Inject(method = "getControlledMovementInput", at = @At("HEAD"), cancellable = true)
    protected void getControlledMovementInput(PlayerEntity controllingPlayer, Vec3d movementInput, CallbackInfoReturnable<Vec3d> cir) {
        // sideways & forwardSpeed : -1, 0, 1 (or less in certain conditions, player button inputs)

        // all this, client only

        if (this.getWorld().isClient) {
            // change running state (between plotting, trotting, and galloping),
            // immediately set speed to min speed for that state

            if (((HorseRidingClientPlayer) controllingPlayer).gallopingHorses$getGallopInput()
                    && runningState != 2
                    && !((HorseRidingClientPlayer) controllingPlayer).getBrakeInput()) {
                runningState++;
                this.speed = minSpeeds[runningState];
            }

            // a movement key is pressed and if speed is within expected bounds apply movement (any input increase speed)
            if ((Math.abs(controllingPlayer.sidewaysSpeed) > 0.01 || Math.abs(controllingPlayer.forwardSpeed) > 0.01)
                    && !((HorseRidingClientPlayer) controllingPlayer).getBrakeInput()) {
                this.speed = Math.min(speed + acceleration, maxSpeeds[runningState]);
            }

            // decay speed
            else {
                this.speed = Math.max(
                        this.speed - acceleration / (((HorseRidingClientPlayer) controllingPlayer).getBrakeInput()
                                ? .5 : 2), 0);
            }
            if (this.horizontalCollision) {
                this.speed = 0;
            }

            // find desire angle and difference from current angle
            float angleMod = angleLookup[MathHelper.sign(controllingPlayer.forwardSpeed) + 1][MathHelper.sign(controllingPlayer.sidewaysSpeed) + 1];
            float angleDifference;
            float angularAcceleration = baseAngularAcceleration;
            if (angleMod != -1) {
                angleDifference = MathHelper.subtractAngles(this.getYaw(), controllingPlayer.getYaw() + angleMod);
                if (MathHelper.abs(angleDifference) > 170) {
                    // near complete turn around, start buck, buff turn rate
                    speed = Math.max(speed - 10 * acceleration, 0);
                    angularAcceleration *= 2;
                }
            } else {
                angleDifference = 0;
            }

            // use angle difference to find desired angular velocity, then accelerate towards it (given enough difference)
            if (Math.abs(angleDifference) > 1) {
//        System.out.println("Angle Delta: " + angleDifference);
                int direction = MathHelper.sign(angleDifference);
                // easing function in here
                float targetVelocity = MathHelper.clamp(
                        angleDifference * angleDifference * direction * (angularVelocityMax / (slowDownAngle * slowDownAngle)),
                        -angularVelocityMax, angularVelocityMax);
                angularVelocity += angularAcceleration * MathHelper.sign(targetVelocity - angularVelocity);
            } else {
                angularVelocity = 0;
            }
//        System.out.println("Angular Velocity: " + angularVelocity);

            // if speed falls below state minimum, reduce state
            if (this.speed < minSpeeds[runningState] && runningState != 0) {
                runningState--;
                this.speed = maxSpeeds[runningState];
            }

            // horses can only move forward and back
            cir.setReturnValue(new Vec3d(0, 0, this.speed));
        }
    }

    @Inject(method = "getControlledRotation", at = @At("HEAD"), cancellable = true)
    protected void getControlledRotation(LivingEntity controllingPassenger, CallbackInfoReturnable<Vec2f> cir) {
        cir.setReturnValue(new Vec2f(controllingPassenger.getPitch()/2,
                MathHelper.wrapDegrees(this.getYaw() + this.angularVelocity)));
    }

    @Inject(method="putPlayerOnBack", at=@At("HEAD"))
    protected void putPlayerOnBack(PlayerEntity player, CallbackInfo ci) {
        this.speed = 0;
        this.angularVelocity = 0;
        this.runningState = 0;
    }
}
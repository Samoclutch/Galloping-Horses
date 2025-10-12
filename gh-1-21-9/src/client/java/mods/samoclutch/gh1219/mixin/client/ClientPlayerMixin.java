package mods.samoclutch.gh1219.mixin.client;

import com.mojang.authlib.GameProfile;
import mods.samoclutch.gh1219.HorseRidingClientPlayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerMixin extends AbstractClientPlayerEntity implements HorseRidingClientPlayer {
    @Unique
    boolean horseBrake;

    @Final
    @Shadow
    protected MinecraftClient client;

    public ClientPlayerMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
        horseBrake = false;
    }

    @Unique
    public boolean gallopingHorses$getGallopInput() {
        return this.client.options.sprintKey.wasPressed();
    }

    @Override
    public boolean getBrakeInput() {
        return horseBrake;
    }

    @Override
    public void setBrakeInput(boolean brake) {
        horseBrake = brake;
    }
}

package mods.samoclutch.gh1212.mixin.client;

import com.mojang.authlib.GameProfile;
import mods.samoclutch.gh1212.HorseRidingClientPlayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerMixin extends AbstractClientPlayerEntity implements HorseRidingClientPlayer {
    @Final
    @Shadow
    protected MinecraftClient client;

    public ClientPlayerMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Unique
    public boolean gallopingHorses$getGallopInput() {
        return this.client.options.sprintKey.wasPressed();
    }
}

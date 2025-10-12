package mods.samoclutch.gh1211.mixin.client;

import mods.samoclutch.gh1211.client.Gh1211Client;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.passive.AbstractHorseEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class CrossHairMixin {

    @Shadow @Final private MinecraftClient client;
    @Unique private boolean perspectiveChanged;

    @Inject(method = "renderCrosshair", at = @At("HEAD"))
    public void crosshairHead(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        perspectiveChanged = false;
        if (this.client.player != null && this.client.options != null && this.client.player.getVehicle() instanceof AbstractHorseEntity
                && !this.client.options.getPerspective().isFirstPerson()
                && !this.client.options.getPerspective().isFrontView()
                && !(Gh1211Client.overShoulderPosition == 3)) {
            perspectiveChanged = true;
            this.client.options.setPerspective(Perspective.FIRST_PERSON);
        }
    }
    @Inject(method = "renderCrosshair", at = @At("RETURN"))
    public void crosshairTail(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (perspectiveChanged) {
            this.client.options.setPerspective(Perspective.THIRD_PERSON_BACK);
        }
    }
}

package mods.samoclutch.gh1218.mixin.client;

import mods.samoclutch.gh1218.HorseRidingClientPlayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.passive.AbstractHorseEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class CrossHairMixin {

    @Shadow @Final private MinecraftClient client;

    @Shadow protected abstract void renderCrosshair(DrawContext context, RenderTickCounter tickCounter);

    @Inject(method = "render", at = @At("RETURN"))
    public void onRender(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (this.client.player != null && this.client.player.getVehicle() instanceof AbstractHorseEntity
                && !this.client.options.getPerspective().isFirstPerson()
                && !this.client.options.getPerspective().isFrontView()
                && !(((HorseRidingClientPlayer)this.client.player).getCameraPositionInput()%2 == 1)) {
            Perspective perspective = this.client.options.getPerspective();
            this.client.options.setPerspective(Perspective.FIRST_PERSON);
            this.renderCrosshair(context, tickCounter);
            this.client.options.setPerspective(perspective);
        }
    }
}

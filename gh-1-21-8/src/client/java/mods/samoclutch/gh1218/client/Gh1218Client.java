package mods.samoclutch.gh1218.client;

import mods.samoclutch.gh1218.HorseRidingClientPlayer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class Gh1218Client implements ClientModInitializer {

    private static KeyBinding horseBrake;
    private static KeyBinding horseZoom;
    private static boolean zoomSticky = false;
    private static KeyBinding cameraPosition;
    // 0 - left, [1 4] - center, 2 - right
    private int overShoulderPosition = 0;

    @Override
    public void onInitializeClient() {
        horseBrake = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.gallopinghorses.brake",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_N,
                "category.gallopinghorses.gallopinghorses"
        ));

        horseZoom = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.gallopinghorses.zoom",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.gallopinghorses.gallopinghorses"
        ));

        cameraPosition = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.gallopinghorses.camera",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_GRAVE_ACCENT,
                "category.gallopinghorses.gallopinghorses"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (horseZoom.wasPressed())
                zoomSticky = !zoomSticky;
            if(cameraPosition.wasPressed()) {
                overShoulderPosition++;
                overShoulderPosition %= 4;
            }
            if(client.player != null) {
                ((HorseRidingClientPlayer) (client.player)).setBrakeInput(horseBrake.isPressed());
                ((HorseRidingClientPlayer) (client.player)).setZoomInput(zoomSticky);
                ((HorseRidingClientPlayer) (client.player)).setCameraPositionInput(overShoulderPosition);
            }
        });
    }
}

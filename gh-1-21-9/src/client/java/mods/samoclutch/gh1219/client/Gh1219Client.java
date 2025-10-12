package mods.samoclutch.gh1219.client;

import mods.samoclutch.gh1219.HorseRidingClientPlayer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class Gh1219Client implements ClientModInitializer {

    private static KeyBinding horseBrake;
    private static KeyBinding horseZoom;
    public static boolean zoomSticky = false;
    private static KeyBinding cameraPosition;
    // 0 - left, [1 4] - center, 2 - right
    public static int overShoulderPosition = 0;


    @Override
    public void onInitializeClient() {
        KeyBinding.Category horsePlace = new KeyBinding.Category(Identifier.tryParse("category.gallopinghorses.keybinds"));
        horseBrake = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.gallopinghorses.brake",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_Z,
                horsePlace
        ));

        horseZoom = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.gallopinghorses.zoom",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                horsePlace
        ));

        cameraPosition = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.gallopinghorses.camera",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_GRAVE_ACCENT,
                horsePlace
        ));

        GhConfig.init();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (horseZoom.wasPressed())
                zoomSticky = !zoomSticky;
            if(cameraPosition.wasPressed()) {
                overShoulderPosition++;
                overShoulderPosition %= 4;
            }
            if(client.player != null) {
                ((HorseRidingClientPlayer) (client.player)).setBrakeInput(horseBrake.isPressed());
            }
        });
    }
}

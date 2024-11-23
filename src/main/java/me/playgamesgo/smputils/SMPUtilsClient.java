package me.playgamesgo.smputils;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class SMPUtilsClient implements ClientModInitializer {
    public static KeyBinding mapKeybind, forceHideKeybind;
    public static boolean renderMap = true, forceHide = false;

    @Override
    public void onInitializeClient() {
        Configs.init();

        mapKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.smputils.webmap",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                "category.smputils.binds"
        ));

        forceHideKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.smputils.forcehide",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                "category.smputils.binds"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (mapKeybind.wasPressed()) {
                if (!(client.currentScreen instanceof DynMap)) {
                    renderMap = false;
                    client.setScreen(new DynMap(Text.of("DynMap")));
                }
            }

            while (forceHideKeybind.wasPressed()) {
                forceHide = !forceHide;
            }
        });

        HudRenderer.render();
    }
}

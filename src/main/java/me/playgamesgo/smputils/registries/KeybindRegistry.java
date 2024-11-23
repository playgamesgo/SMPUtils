package me.playgamesgo.smputils.registries;

import lombok.Getter;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;

public final class KeybindRegistry {
    public static HashMap<Keybinds, KeyBinding> keybinds = new HashMap<>();

    @Getter
    public enum Keybinds {
        MAP("key.smputils.webmap", GLFW.GLFW_KEY_M, "category.smputils.binds"),
        FORCE_HIDE("key.smputils.forcehide", GLFW.GLFW_KEY_H, "category.smputils.binds"),
        CHANGE_POSITION("key.smputils.changeposition", GLFW.GLFW_KEY_O, "category.smputils.binds");

        private final String name;
        private final int key;
        private final String category;

        Keybinds(String name, int key, String category) {
            this.name = name;
            this.key = key;
            this.category = category;
        }
    }

    public static void registerKeybinds() {
        for (Keybinds keybind : Keybinds.values()) {
            register(keybind);
        }
    }

    private static void register(Keybinds keybind) {
        KeyBinding keyBinding = new KeyBinding(keybind.name, InputUtil.Type.KEYSYM, keybind.key, keybind.category);
        keybinds.put(keybind, keyBinding);
        KeyBindingHelper.registerKeyBinding(keyBinding);
    }
}

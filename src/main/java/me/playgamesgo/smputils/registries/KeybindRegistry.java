package me.playgamesgo.smputils.registries;

import lombok.Getter;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;

public final class KeybindRegistry {
    public static HashMap<Keybinds, KeyMapping> keybinds = new HashMap<>();
    public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("smputils", "binds"));

    @Getter
    public enum Keybinds {
        MAP("key.smputils.webmap", GLFW.GLFW_KEY_M, CATEGORY),
        FORCE_HIDE("key.smputils.forcehide", GLFW.GLFW_KEY_H, CATEGORY),
        CHANGE_POSITION("key.smputils.changeposition", GLFW.GLFW_KEY_O, CATEGORY);

        private final String name;
        private final int key;
        private final KeyMapping.Category category;

        Keybinds(String name, int key, KeyMapping.Category category) {
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
        KeyMapping keyBinding = new KeyMapping(keybind.name, InputConstants.Type.KEYSYM, keybind.key, keybind.category);
        keybinds.put(keybind, keyBinding);
        KeyMappingHelper.registerKeyMapping(keyBinding);
    }
}

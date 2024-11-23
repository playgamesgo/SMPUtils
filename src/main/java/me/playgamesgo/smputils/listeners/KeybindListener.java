package me.playgamesgo.smputils.listeners;

import me.playgamesgo.smputils.SMPUtilsClient;
import me.playgamesgo.smputils.registries.KeybindRegistry;
import me.playgamesgo.smputils.ui.DynMap;
import me.playgamesgo.smputils.utils.Configs;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;

public final class KeybindListener {
    private static final Map<KeybindRegistry.Keybinds, KeybindAction> keybindActions = new HashMap<>();

    public static void listenKeybinds() {
        initializeKeybindActions();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (MinecraftClient.getInstance() == null || MinecraftClient.getInstance().player == null) {
                return;
            }

            keybindActions.forEach((keybind, action) -> {
                while (KeybindRegistry.keybinds.get(keybind).wasPressed()) {
                    action.execute(MinecraftClient.getInstance().player);
                }
            });
        });
    }

    private static void initializeKeybindActions() {
        keybindActions.put(KeybindRegistry.Keybinds.MAP, player -> {
            if (!(MinecraftClient.getInstance().currentScreen instanceof DynMap)) {
                SMPUtilsClient.renderMap = false;
                MinecraftClient.getInstance().setScreen(new DynMap(Text.of("DynMap")));
            }
        });

        keybindActions.put(KeybindRegistry.Keybinds.FORCE_HIDE, player ->
                SMPUtilsClient.forceHide = !SMPUtilsClient.forceHide);

        keybindActions.put(KeybindRegistry.Keybinds.CHANGE_POSITION, player -> {
            switch (Configs.config.MiniMapPosition) {
                case TOP_LEFT:
                    Configs.config.MiniMapPosition = Configs.MiniMapPosition.TOP_RIGHT;
                    break;
                case TOP_RIGHT:
                    Configs.config.MiniMapPosition = Configs.MiniMapPosition.BOTTOM_RIGHT;
                    break;
                case BOTTOM_RIGHT:
                    Configs.config.MiniMapPosition = Configs.MiniMapPosition.BOTTOM_LEFT;
                    break;
                case BOTTOM_LEFT:
                    Configs.config.MiniMapPosition = Configs.MiniMapPosition.TOP_LEFT;
                    break;
            }
        });
    }

    @FunctionalInterface
    private interface KeybindAction {
        void execute(PlayerEntity player);
    }
}
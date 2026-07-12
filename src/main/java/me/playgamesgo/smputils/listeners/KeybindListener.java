package me.playgamesgo.smputils.listeners;

import me.playgamesgo.smputils.SMPUtilsClient;
import me.playgamesgo.smputils.registries.KeybindRegistry;
import me.playgamesgo.smputils.ui.WebMap;
import me.playgamesgo.smputils.utils.Config;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.Map;

public final class KeybindListener {
    private static final Map<KeybindRegistry.Keybinds, KeybindAction> keybindActions = new HashMap<>();

    public static void listenKeybinds() {
        initializeKeybindActions();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            keybindActions.forEach((keybind, action) -> {
                while (KeybindRegistry.keybinds.get(keybind).consumeClick()) {
                    action.execute(Minecraft.getInstance().player);
                }
            });
        });
    }

    private static void initializeKeybindActions() {
        keybindActions.put(KeybindRegistry.Keybinds.MAP, player -> {
            if (!(Minecraft.getInstance().gui.screen() instanceof WebMap)) {
                SMPUtilsClient.renderMap = false;
                Minecraft.getInstance().gui.setScreen(new WebMap(Component.literal("WebMap")));
            }
        });

        keybindActions.put(KeybindRegistry.Keybinds.FORCE_HIDE, player -> Config.setHideMinimap(!Config.isHideMinimap()));

        keybindActions.put(KeybindRegistry.Keybinds.CHANGE_POSITION, player -> {
            switch (Config.getMiniMapPos()) {
                case TOP_LEFT -> Config.setMiniMapPos(Config.MiniMapPosition.TOP_RIGHT);
                case TOP_RIGHT -> Config.setMiniMapPos(Config.MiniMapPosition.BOTTOM_RIGHT);
                case BOTTOM_RIGHT -> Config.setMiniMapPos(Config.MiniMapPosition.BOTTOM_LEFT);
                case BOTTOM_LEFT -> Config.setMiniMapPos(Config.MiniMapPosition.TOP_LEFT);
            }
        });
    }

    @FunctionalInterface
    private interface KeybindAction {
        void execute(Player player);
    }
}
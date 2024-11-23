package me.playgamesgo.smputils;

import me.playgamesgo.smputils.listeners.KeybindListener;
import me.playgamesgo.smputils.registries.KeybindRegistry;
import me.playgamesgo.smputils.ui.HudRenderer;
import me.playgamesgo.smputils.utils.Configs;
import net.fabricmc.api.ClientModInitializer;

public final class SMPUtilsClient implements ClientModInitializer {
    public static boolean renderMap = true, forceHide = false;

    @Override
    public void onInitializeClient() {
        Configs.init();

        KeybindRegistry.registerKeybinds();
        KeybindListener.listenKeybinds();

        HudRenderer.render();
    }
}

package me.playgamesgo.smputils.utils;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import lombok.Getter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

public final class Config {
    private static ConfigClassHandler<Config> HANDLER = ConfigClassHandler.createBuilder(Config.class)
            .id(Identifier.of("smputils", "config"))
                    .serializer(config -> GsonConfigSerializerBuilder.create(config)
                            .setPath(FabricLoader.getInstance().getConfigDir().resolve("smputils.json5"))
                            .setJson5(true)
                            .build())
                    .build();

    public enum MiniMapPosition {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }

    @Getter
    @SerialEntry
    private static String MapUrl = "https://map.clubsmp.net/#";

    @Getter
    @SerialEntry
    private static boolean hideMinimap = false;

    @Getter
    @SerialEntry
    private static MiniMapPosition MiniMapPos = MiniMapPosition.TOP_RIGHT;

    @Getter
    @SerialEntry
    private static int MiniMapWidth = 250;
    @Getter
    @SerialEntry
    private static int MiniMapHeight = 150;

    public static void init() {
        Config.HANDLER.load();
    }

    public static void setMapUrl(String mapUrl) {
        MapUrl = mapUrl;
        HANDLER.save();
    }

    public static void setHideMinimap(boolean hideMinimap) {
        Config.hideMinimap = hideMinimap;
        HANDLER.save();
    }

    public static void setMiniMapPos(MiniMapPosition miniMapPos) {
        MiniMapPos = miniMapPos;
        HANDLER.save();
    }

    public static void setMiniMapWidth(int miniMapWidth) {
        MiniMapWidth = miniMapWidth;
        HANDLER.save();
    }

    public static void setMiniMapHeight(int miniMapHeight) {
        MiniMapHeight = miniMapHeight;
        HANDLER.save();
    }
}

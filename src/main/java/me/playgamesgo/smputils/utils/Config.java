package me.playgamesgo.smputils.utils;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import lombok.Getter;
import me.playgamesgo.smputils.ui.HudRenderer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Map;

public final class Config {
    private static final ConfigClassHandler<Config> HANDLER = ConfigClassHandler.createBuilder(Config.class)
            .id(Identifier.fromNamespaceAndPath("smputils", "config"))
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

    public enum MapView {
        PERSPECTIVE("perspective"),
        FLAT("flat");
        @Getter private final String value;

        MapView(String value) {
            this.value = value;
        }
    }

    @Getter
    @SerialEntry
    private static String MapUrl = "https://map.clubsmp.net/#{world}:{x}:{y}:{z}:{scale}:0:0:0:{view_index}:{view}";

    @Getter
    @SerialEntry
    private static boolean hideMinimap = false;

    @Getter
    @SerialEntry
    private static MiniMapPosition MiniMapPos = MiniMapPosition.TOP_RIGHT;

    @Getter
    @SerialEntry
    private static MapView mapView = MapView.PERSPECTIVE;

    @Getter
    @SerialEntry
    private static int MiniMapWidth = 250;

    @Getter
    @SerialEntry
    private static int MiniMapHeight = 150;

    @Getter
    @SerialEntry
    private static int MiniMapScale = 300;

    @Getter
    @SerialEntry
    private static boolean showCoordinates = false;

    @Getter
    @SerialEntry
    private static Map<String, String> WorldAliases = Map.of(
            "overworld", "world",
            "the_nether", "world_the_nether",
            "the_end", "world_the_end"
    );

    @Getter
    @SerialEntry
    private static boolean flashbackWhitelistMode = false;

    @Getter
    @SerialEntry
    private static List<String> flashbackServers = List.of("localhost");

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

    public static void setMapView(MapView mapView) {
        Config.mapView = mapView;
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

    public static void setMiniMapScale(int miniMapScale) {
        MiniMapScale = miniMapScale;
        HANDLER.save();
    }

    public static void setShowCoordinates(boolean showCoordinates) {
        Config.showCoordinates = showCoordinates;
        HANDLER.save();
        HudRenderer.sendCss(true);
    }

    public static void setWorldAliases(Map<String, String> worldAliases) {
        WorldAliases = worldAliases;
        HANDLER.save();
    }

    public static void setFlashbackWhitelistMode(boolean whitelistMode) {
        Config.flashbackWhitelistMode = whitelistMode;
        HANDLER.save();
    }

    public static void setFlashbackServers(List<String> servers) {
        Config.flashbackServers = servers;
        HANDLER.save();
    }
}

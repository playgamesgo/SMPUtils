package me.playgamesgo.smputils.utils;

import me.fzzyhmstrs.fzzy_config.annotations.Comment;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import me.playgamesgo.smputils.ui.HudRenderer;
import net.minecraft.util.Identifier;

public final class Configs {
    public static Config config = ConfigApiJava.registerAndLoadConfig(Config::new, RegisterType.CLIENT);

    public enum MiniMapPosition {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }

    public static void init() {}


    public static class Config extends me.fzzyhmstrs.fzzy_config.config.Config {
        public String MapUrl = "https://map.clubsmp.net/#";

        @Comment("The position of the minimap on the screen, can be TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, or BOTTOM_RIGHT.")
        public Configs.MiniMapPosition MiniMapPosition = Configs.MiniMapPosition.TOP_RIGHT;

        public ValidatedInt MiniMapWidth = ValidatedInt.Companion.withListener(new ValidatedInt(200, 500, 100),
                integerValidatedField -> HudRenderer.resize());
        public ValidatedInt MiniMapHeight = ValidatedInt.Companion.withListener(new ValidatedInt(200, 500, 100),
                integerValidatedField -> HudRenderer.resize());

        public Config() {
            super(Identifier.of("smputils", "config"));
        }
    }
}

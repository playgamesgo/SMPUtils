package me.playgamesgo.smputils;

import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import net.minecraft.util.Identifier;

public class Configs {
    public static Config config = ConfigApiJava.registerAndLoadConfig(Config::new, RegisterType.CLIENT);

    public static void init() {}

    public static class Config extends me.fzzyhmstrs.fzzy_config.config.Config {
        public String mapUrl = "https://map.clubsmp.net/#";

        public Config() {
            super(Identifier.of("smputils", "config"));
        }
    }
}

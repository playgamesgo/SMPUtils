package me.playgamesgo.smputils;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import me.playgamesgo.smputils.utils.Config;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;

public final class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parentScreen -> {
            ConfigCategory.Builder builder = ConfigCategory.createBuilder()
                    .name(Text.literal("Settings"))
                    .group(OptionGroup.createBuilder()
                            .name(Text.literal("Settings"))
                            .option(Option.<String>createBuilder()
                                    .name(Text.literal("Map URL"))
                                    .binding(Config.getMapUrl(), Config::getMapUrl, Config::setMapUrl)
                                    .controller(StringControllerBuilder::create)
                                    .build())
                            .option(Option.<Boolean>createBuilder()
                                    .name(Text.literal("Hide Minimap"))
                                    .binding(Config.isHideMinimap(), Config::isHideMinimap, Config::setHideMinimap)
                                    .controller(TickBoxControllerBuilder::create)
                                    .build())
                            .option(Option.<Config.MiniMapPosition>createBuilder()
                                    .name(Text.literal("Minimap Position"))
                                    .description(OptionDescription.of(Text.literal("The position of the minimap on the screen, can be TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, or BOTTOM_RIGHT.")))
                                    .binding(Config.getMiniMapPos(), Config::getMiniMapPos, Config::setMiniMapPos)
                                    .controller(enumOption -> EnumControllerBuilder.create(enumOption)
                                            .enumClass(Config.MiniMapPosition.class))
                                    .build())
                            .option(Option.<Config.MapView>createBuilder()
                                    .name(Text.literal("Map View"))
                                    .description(OptionDescription.of(Text.literal("The view mode of the map, can be PERSPECTIVE or FLAT.")))
                                    .binding(Config.getMapView(), Config::getMapView, Config::setMapView)
                                    .controller(enumOption -> EnumControllerBuilder.create(enumOption)
                                            .enumClass(Config.MapView.class))
                                    .build())
                            .option(Option.<Integer>createBuilder()
                                    .name(Text.literal("Minimap width"))
                                    .binding(Config.getMiniMapWidth(), Config::getMiniMapWidth, Config::setMiniMapWidth)
                                    .controller(integerOption -> IntegerSliderControllerBuilder.create(integerOption)
                                            .range(100, 500).step(1))
                                    .build())
                            .option(Option.<Integer>createBuilder()
                                    .name(Text.literal("Minimap height"))
                                    .binding(Config.getMiniMapHeight(), Config::getMiniMapHeight, Config::setMiniMapHeight)
                                    .controller(integerOption -> IntegerSliderControllerBuilder.create(integerOption)
                                            .range(100, 500).step(1))
                                    .build())
                            .option(Option.<Integer>createBuilder()
                                    .name(Text.literal("Minimap scale"))
                                    .binding(Config.getMiniMapScale(), Config::getMiniMapScale, Config::setMiniMapScale)
                                    .controller(integerOption -> IntegerSliderControllerBuilder.create(integerOption)
                                            .range(0, 5000).step(50))
                                    .build())
                            .option(Option.<Boolean>createBuilder()
                                    .name(Text.literal("Show Coordinates"))
                                    .binding(Config.isShowCoordinates(), Config::isShowCoordinates, Config::setShowCoordinates)
                                    .controller(TickBoxControllerBuilder::create)
                                    .build())
                            .build());

            if (FabricLoader.getInstance().isModLoaded("flashback")) {
                builder.group(OptionGroup.createBuilder()
                        .name(Text.literal("Flashback"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Whitelist Mode"))
                                .description(OptionDescription.createBuilder()
                                        .text(Text.literal("If enabled, will allow recording on servers in the list instead of blocking them."))
                                        .build())
                                .binding(false, Config::isFlashbackWhitelistMode, Config::setFlashbackWhitelistMode)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .build());
                builder.group(ListOption.<String>createBuilder()
                        .name(Text.literal("Flashback Servers"))
                        .description(OptionDescription.createBuilder()
                                .text(Text.literal("The list of servers to block or allow recording on, depending on the whitelist mode setting."))
                                .build())
                        .binding(Config.getFlashbackServers(), Config::getFlashbackServers, Config::setFlashbackServers)
                        .initial(() -> "localhost")
                        .controller(StringControllerBuilder::create)
                        .build());
            }

            return YetAnotherConfigLib.createBuilder()
                    .title(Text.literal("SMPUtils Config"))
                    .category(builder.build())
                    .build()
                    .generateScreen(parentScreen);
        };
    }
}

package me.playgamesgo.smputils;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import dev.isxander.yacl3.config.v2.api.autogen.TickBox;
import dev.isxander.yacl3.gui.controllers.slider.IntegerSliderController;
import me.playgamesgo.smputils.utils.Config;
import net.minecraft.text.Text;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parentScreen -> YetAnotherConfigLib.createBuilder()
                .title(Text.literal("SMPUtils Config"))
                .category(ConfigCategory.createBuilder()
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
                                .build())
                        .build())
                .build()
                .generateScreen(parentScreen);
    }
}

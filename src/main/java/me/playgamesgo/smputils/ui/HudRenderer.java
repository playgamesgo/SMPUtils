package me.playgamesgo.smputils.ui;

import com.cinemamod.mcef.MCEF;
import com.cinemamod.mcef.MCEFBrowser;
import lombok.Getter;
import lombok.Setter;
import me.playgamesgo.smputils.SMPUtilsClient;
import me.playgamesgo.smputils.utils.Config;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

import java.util.concurrent.CompletableFuture;

public final class HudRenderer {
    @Getter @Setter private static MCEFBrowser browser;
    private static final Minecraft minecraft = Minecraft.getInstance();

    public static int[] getMinimapBounds() {
        int width = Config.getMiniMapWidth();
        int height = Config.getMiniMapHeight();

        int x1 = 0;
        int y1 = 0;
        switch (Config.getMiniMapPos()) {
            case TOP_RIGHT:
                x1 = minecraft.getWindow().getGuiScaledWidth() - width;
                break;
            case BOTTOM_LEFT:
                y1 = minecraft.getWindow().getGuiScaledHeight() - height;
                break;
            case BOTTOM_RIGHT:
                x1 = minecraft.getWindow().getGuiScaledWidth() - width;
                y1 = minecraft.getWindow().getGuiScaledHeight() - height;
                break;
            default: // TOP_LEFT
                break;
        }

        return new int[]{x1, y1, width, height};
    }

    public static boolean isPointInMinimap(double x, double y) {
        int[] bounds = getMinimapBounds();
        int x1 = bounds[0];
        int y1 = bounds[1];
        int width = bounds[2];
        int height = bounds[3];
        return x >= x1 && x < x1 + width && y >= y1 && y < y1 + height;
    }

    public static int minimapMouseX(double x) {
        int[] bounds = getMinimapBounds();
        return scaleX(x - bounds[0]);
    }

    public static int minimapMouseY(double y) {
        int[] bounds = getMinimapBounds();
        return scaleY(y - bounds[1]);
    }

    public static void render() {
        HudElementRegistry.attachElementBefore(VanillaHudElements.MOB_EFFECTS, Identifier.fromNamespaceAndPath("smputils", "minimap"), (context, tickCounter) -> {
            if (minecraft.player == null) return;

            String world = minecraft.player.level().dimension().identifier().getPath();
            world = Config.getWorldAliases().getOrDefault(world, world);
            String url = Config.getMapUrl()
                    .replace("{world}", world)
                    .replace("{x}", minecraft.player.blockPosition().getX() + "")
                    .replace("{y}", minecraft.player.blockPosition().getY() + "")
                    .replace("{z}", minecraft.player.blockPosition().getZ() + "")
                    .replace("{scale}", Config.getMiniMapScale() + "")
                    .replace("{view_index}", Config.getMapView().ordinal() + "")
                    .replace("{view}", Config.getMapView().getValue());
            if (browser == null) {
                boolean transparent = true;
                browser = MCEF.createBrowser(url, transparent);
                browser.resize(HudRenderer.scaleX(Config.getMiniMapWidth()), HudRenderer.scaleY(Config.getMiniMapHeight()));
                browser.setCursorChangeListener(cursorID -> {});

                CompletableFuture.runAsync(() -> {
                    try {
                        Thread.sleep(1000);
                        Minecraft.getInstance().executeBlocking(() -> sendCss(true));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });

            } else if (SMPUtilsClient.renderMap && !Config.isHideMinimap()) {
                browser.loadURL(url);
                browser.sendMouseMove(Config.getMiniMapWidth(), Config.getMiniMapHeight());

                int[] bounds = getMinimapBounds();
                int x1 = bounds[0];
                int y1 = bounds[1];
                int width = bounds[2];
                int height = bounds[3];

                Identifier textureLocation = browser.getTextureIdentifier();
                if (textureLocation == null) return;

                context.blit(
                        RenderPipelines.GUI_TEXTURED,
                        textureLocation,
                        x1, y1,
                        0, 0,
                        width, height,
                        width, height
                );

            }
        });
    }

    public static void resize() {
        if (browser != null) {
            browser.resize(HudRenderer.scaleX(Config.getMiniMapWidth()), HudRenderer.scaleY(Config.getMiniMapHeight()));
        }
    }

    public static int scaleX(double x) {
        return (int) (x * minecraft.getWindow().getGuiScale());
    }

    public static int scaleY(double y) {
        return (int) (y * minecraft.getWindow().getGuiScale());
    }

    public static void sendCss(boolean hideMenuButton) {
        if (browser != null) {
            String css = """
                    #zoom-buttons { display: none !important; }
                    .compass { display: none !important; }
                    .control-bar { background-color: transparent !important; }
                    """;
            if (!Config.isShowCoordinates()) {
                css += """
                        .position-input { display: none !important; }
                        """;
            }
            if (hideMenuButton) {
                css += """
                        .menu-button { display: none !important; }
                        """;
            }


            injectCss(browser, css);
        }
    }

    private static void injectCss(MCEFBrowser browser, String css) {
        String escapedCss = css
                .replace("\\", "\\\\")
                .replace("`", "\\`")
                .replace("${", "\\${");

        browser.executeJavaScript("""
                (() => {
                    const id = 'smputils-custom-css';
                    let style = document.getElementById(id);
                
                    if (!style) {
                        style = document.createElement('style');
                        style.id = id;
                        document.head.appendChild(style);
                    }
                
                    style.textContent = `%s`;
                })();
                """.formatted(escapedCss), "", 0);
    }
}

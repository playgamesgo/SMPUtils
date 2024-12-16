package me.playgamesgo.smputils.ui;

import com.cinemamod.mcef.MCEF;
import com.cinemamod.mcef.MCEFBrowser;
import com.mojang.blaze3d.systems.RenderSystem;
import me.playgamesgo.smputils.utils.Configs;
import me.playgamesgo.smputils.SMPUtilsClient;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;

public final class HudRenderer {
    private static MCEFBrowser browser;
    private static final MinecraftClient minecraft = MinecraftClient.getInstance();

    public static void render() {
        HudRenderCallback.EVENT.register((context, tickDeltaManager) -> {
            String url = Configs.config.MapUrl + minecraft.player.getWorld().getRegistryKey().getValue().toString().replace(":", "_") + ";flat;"
                    + minecraft.player.getBlockPos().getX() + ",64," + minecraft.player.getBlockPos().getZ() + ";3";
            if (browser == null && minecraft.player != null) {
                boolean transparent = true;
                browser = MCEF.createBrowser(url, transparent);
                browser.resize(HudRenderer.scaleX(Configs.config.MiniMapWidth.get()), HudRenderer.scaleY(Configs.config.MiniMapHeight.get()));
                browser.setCursorChangeListener(cursorID -> {});
            } else if (SMPUtilsClient.renderMap && !SMPUtilsClient.forceHide) {
                browser.loadURL(url);
                browser.sendMouseMove(Configs.config.MiniMapWidth.get(), Configs.config.MiniMapHeight.get());

                RenderSystem.disableDepthTest();
                RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
                RenderSystem.setShaderTexture(0, browser.getRenderer().getTextureID());
                Tessellator t = Tessellator.getInstance();
                BufferBuilder buffer = t.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

                int x1 = 0, y1 = 0, x2 = Configs.config.MiniMapWidth.get(), y2 = Configs.config.MiniMapHeight.get();
                switch (Configs.config.MiniMapPosition) {
                    case TOP_RIGHT:
                        x1 = minecraft.getWindow().getScaledWidth() - Configs.config.MiniMapWidth.get();
                        x2 = minecraft.getWindow().getScaledWidth();
                        break;
                    case BOTTOM_LEFT:
                        y1 = minecraft.getWindow().getScaledHeight() - Configs.config.MiniMapHeight.get();
                        y2 = minecraft.getWindow().getScaledHeight();
                        break;
                    case BOTTOM_RIGHT:
                        x1 = minecraft.getWindow().getScaledWidth() - Configs.config.MiniMapWidth.get();
                        x2 = minecraft.getWindow().getScaledWidth();
                        y1 = minecraft.getWindow().getScaledHeight() - Configs.config.MiniMapHeight.get();
                        y2 = minecraft.getWindow().getScaledHeight();
                        break;
                    default: // TOP_LEFT
                        break;
                }

                buffer.vertex(x1, y2, 0).texture(0.0f, 1.0f).color(255, 255, 255, 255);
                buffer.vertex(x2, y2, 0).texture(1.0f, 1.0f).color(255, 255, 255, 255);
                buffer.vertex(x2, y1, 0).texture(1.0f, 0.0f).color(255, 255, 255, 255);
                buffer.vertex(x1, y1, 0).texture(0.0f, 0.0f).color(255, 255, 255, 255);
                BufferRenderer.drawWithGlobalProgram(buffer.end());
                RenderSystem.setShaderTexture(0, 0);
                RenderSystem.enableDepthTest();
            }
        });
    }

    public static void resize() {
        if (browser != null) {
            browser.resize(HudRenderer.scaleX(Configs.config.MiniMapWidth.get()), HudRenderer.scaleY(Configs.config.MiniMapHeight.get()));
        }
    }

    public static int scaleX(double x) {
        return (int) (x * minecraft.getWindow().getScaleFactor());
    }

    public static int scaleY(double y) {
        return (int) (y * minecraft.getWindow().getScaleFactor());
    }
}

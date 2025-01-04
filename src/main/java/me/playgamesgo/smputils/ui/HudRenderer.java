package me.playgamesgo.smputils.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import me.playgamesgo.smputils.utils.Config;
import me.playgamesgo.smputils.SMPUtilsClient;
import net.ccbluex.liquidbounce.mcef.MCEF;
import net.ccbluex.liquidbounce.mcef.MCEFBrowser;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.*;

public final class HudRenderer {
    private static MCEFBrowser browser;
    private static final MinecraftClient minecraft = MinecraftClient.getInstance();

    public static void render() {
        HudRenderCallback.EVENT.register((context, tickDeltaManager) -> {
            String url = Config.getMapUrl() + minecraft.player.getWorld().getRegistryKey().getValue().toString().replace(":", "_") + ";flat;"
                    + minecraft.player.getBlockPos().getX() + ",64," + minecraft.player.getBlockPos().getZ() + ";3";
            if (browser == null && minecraft.player != null) {
                boolean transparent = true;
                browser = MCEF.INSTANCE.createBrowser(url, transparent, 30);
                browser.resize(HudRenderer.scaleX(Config.getMiniMapWidth()), HudRenderer.scaleY(Config.getMiniMapHeight()));
                browser.setCursorChangeListener(cursorID -> {});
            } else if (SMPUtilsClient.renderMap && !Config.isHideMinimap()) {
                browser.loadURL(url);
                browser.sendMouseMove(Config.getMiniMapWidth(), Config.getMiniMapHeight());

                RenderSystem.disableDepthTest();
                RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
                RenderSystem.setShaderTexture(0, browser.getRenderer().getTextureID());
                Tessellator t = Tessellator.getInstance();
                BufferBuilder buffer = t.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

                int x1 = 0, y1 = 0, x2 = Config.getMiniMapWidth(), y2 = Config.getMiniMapHeight();
                switch (Config.getMiniMapPos()) {
                    case TOP_RIGHT:
                        x1 = minecraft.getWindow().getScaledWidth() - Config.getMiniMapWidth();
                        x2 = minecraft.getWindow().getScaledWidth();
                        break;
                    case BOTTOM_LEFT:
                        y1 = minecraft.getWindow().getScaledHeight() - Config.getMiniMapHeight();
                        y2 = minecraft.getWindow().getScaledHeight();
                        break;
                    case BOTTOM_RIGHT:
                        x1 = minecraft.getWindow().getScaledWidth() - Config.getMiniMapWidth();
                        x2 = minecraft.getWindow().getScaledWidth();
                        y1 = minecraft.getWindow().getScaledHeight() - Config.getMiniMapHeight();
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
            browser.resize(HudRenderer.scaleX(Config.getMiniMapWidth()), HudRenderer.scaleY(Config.getMiniMapHeight()));
        }
    }

    public static int scaleX(double x) {
        return (int) (x * minecraft.getWindow().getScaleFactor());
    }

    public static int scaleY(double y) {
        return (int) (y * minecraft.getWindow().getScaleFactor());
    }
}

package me.playgamesgo.smputils;

import com.cinemamod.mcef.MCEF;
import com.cinemamod.mcef.MCEFBrowser;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;

public class HudRenderer {
    private static MCEFBrowser browser;
    private static final MinecraftClient minecraft = MinecraftClient.getInstance();
    public static final int width = 200, height = 200;

    public static void render() {
        HudRenderCallback.EVENT.register((context, tickDeltaManager) -> {
            String url = Configs.config.mapUrl + minecraft.player.getWorld().getRegistryKey().getValue().toString().replace(":", "_") + ";flat;"
                    + minecraft.player.getBlockPos().getX() + ",64," + minecraft.player.getBlockPos().getZ() + ";3";
            if (browser == null && minecraft.player != null) {
                boolean transparent = true;
                browser = MCEF.createBrowser(url, transparent);
                browser.resize(HudRenderer.scaleX(HudRenderer.width), HudRenderer.scaleY(HudRenderer.height));
                browser.setFocus(true);
            } else if (SMPUtilsClient.renderMap && !SMPUtilsClient.forceHide){
                browser.loadURL(url);

                RenderSystem.disableDepthTest();
                RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
                RenderSystem.setShaderTexture(0, browser.getRenderer().getTextureID());
                Tessellator t = Tessellator.getInstance();
                BufferBuilder buffer = t.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
                buffer.vertex(0, height, 0).texture(0.0f, 1.0f).color(255, 255, 255, 255);
                buffer.vertex(width, height, 0).texture(1.0f, 1.0f).color(255, 255, 255, 255);
                buffer.vertex(width, 0, 0).texture(1.0f, 0.0f).color(255, 255, 255, 255);
                buffer.vertex(0, 0, 0).texture(0.0f, 0.0f).color(255, 255, 255, 255);
                BufferRenderer.drawWithGlobalProgram(buffer.end());
                RenderSystem.setShaderTexture(0, 0);
                RenderSystem.enableDepthTest();
            }
        });
    }

    public static int scaleX(double x) {
        return (int) (x * minecraft.getWindow().getScaleFactor());
    }

    public static int scaleY(double y) {
        return (int) (y * minecraft.getWindow().getScaleFactor());
    }
}

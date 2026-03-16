package me.playgamesgo.smputils.ui;

import com.cinemamod.mcef.MCEF;
import com.cinemamod.mcef.MCEFBrowser;
import me.playgamesgo.smputils.utils.Config;
import me.playgamesgo.smputils.SMPUtilsClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class WebMap extends Screen {
    private static final int BROWSER_DRAW_OFFSET = 20;

    private MCEFBrowser browser;

    private final MinecraftClient minecraft = MinecraftClient.getInstance();

    public WebMap(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        if (browser == null && minecraft.player != null) {
            String world = minecraft.player.getEntityWorld().getRegistryKey().getValue().getPath();
            world = Config.getWorldAliases().getOrDefault(world, world);
            String url = Config.getMapUrl()
                    .replace("{world}", world)
                    .replace("{x}", minecraft.player.getBlockPos().getX() + "")
                    .replace("{y}", minecraft.player.getBlockPos().getY() + "")
                    .replace("{z}", minecraft.player.getBlockPos().getZ() + "")
                    .replace("{scale}", Config.getMiniMapScale() + "")
                    .replace("{view_index}", Config.getMapView().ordinal() + "")
                    .replace("{view}", Config.getMapView().getValue());
            boolean transparent = true;
            browser = MCEF.createBrowser(url, transparent);
            resizeBrowser();
        }
    }

    private int mouseX(double x) {
        return (int) ((x - BROWSER_DRAW_OFFSET) * minecraft.getWindow().getScaleFactor());
    }

    private int mouseY(double y) {
        return (int) ((y - BROWSER_DRAW_OFFSET) * minecraft.getWindow().getScaleFactor());
    }

    private int scaleX(double x) {
        return (int) ((x - BROWSER_DRAW_OFFSET * 2) * minecraft.getWindow().getScaleFactor());
    }

    private int scaleY(double y) {
        return (int) ((y - BROWSER_DRAW_OFFSET * 2) * minecraft.getWindow().getScaleFactor());
    }

    private void resizeBrowser() {
        if (width > 100 && height > 100) {
            browser.resize(scaleX(width), scaleY(height));
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        resizeBrowser();
    }

    @Override
    public void close() {
        browser.close();
        super.close();
        SMPUtilsClient.renderMap = true;
        HudRenderer.sendCss(true);
    }

    @Override
    public void render(DrawContext context, int i, int j, float f) {
        super.render(context, i, j, f);
        if (browser == null) return;

        Identifier textureLocation = browser.getTextureIdentifier();
        if (textureLocation == null) return;

        int drawWidth = width - BROWSER_DRAW_OFFSET * 2;
        int drawHeight = height - BROWSER_DRAW_OFFSET * 2;
        if (drawWidth <= 0 || drawHeight <= 0) return;

        context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                textureLocation,
                BROWSER_DRAW_OFFSET, BROWSER_DRAW_OFFSET,
                0, 0,
                drawWidth, drawHeight,
                drawWidth, drawHeight
        );
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        browser.sendMousePress(mouseX(click.x()), mouseY(click.y()), click.button());
        browser.setFocus(true);
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseReleased(Click click) {
        browser.sendMouseRelease(mouseX(click.x()), mouseY(click.y()), click.button());
        browser.setFocus(true);
        return super.mouseReleased(click);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        browser.sendMouseMove(mouseX(mouseX), mouseY(mouseY));
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        return super.mouseDragged(click, offsetX, offsetY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        browser.sendMouseWheel(mouseX(mouseX), mouseY(mouseY), verticalAmount, 0);
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        browser.sendKeyPress(input.key(), input.scancode(), input.modifiers());
        browser.setFocus(true);
        return super.keyPressed(input);
    }

    @Override
    public boolean keyReleased(KeyInput input) {
        browser.sendKeyRelease(input.key(), input.scancode(), input.modifiers());
        browser.setFocus(true);
        return super.keyReleased(input);
    }

    @Override
    public boolean charTyped(CharInput input) {
        if (input.codepoint() == (char) 0) return false;
        browser.sendKeyTyped((char) input.codepoint(), input.modifiers());
        browser.setFocus(true);
        return super.charTyped(input);
    }
}
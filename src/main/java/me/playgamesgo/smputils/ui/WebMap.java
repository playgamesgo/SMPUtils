package me.playgamesgo.smputils.ui;

import com.cinemamod.mcef.MCEF;
import com.cinemamod.mcef.MCEFBrowser;
import me.playgamesgo.smputils.utils.Config;
import me.playgamesgo.smputils.SMPUtilsClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public final class WebMap extends Screen {
    private static final int BROWSER_DRAW_OFFSET = 20;

    private MCEFBrowser browser;

    private final Minecraft minecraft = Minecraft.getInstance();

    public WebMap(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        if (browser == null && minecraft.player != null) {
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
            boolean transparent = true;
            browser = MCEF.createBrowser(url, transparent);
            resizeBrowser();
        }
    }

    private int mouseX(double x) {
        return (int) ((x - BROWSER_DRAW_OFFSET) * minecraft.getWindow().getGuiScale());
    }

    private int mouseY(double y) {
        return (int) ((y - BROWSER_DRAW_OFFSET) * minecraft.getWindow().getGuiScale());
    }

    private int scaleX(double x) {
        return (int) ((x - BROWSER_DRAW_OFFSET * 2) * minecraft.getWindow().getGuiScale());
    }

    private int scaleY(double y) {
        return (int) ((y - BROWSER_DRAW_OFFSET * 2) * minecraft.getWindow().getGuiScale());
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
    public void onClose() {
        browser.close();
        super.onClose();
        SMPUtilsClient.renderMap = true;
        HudRenderer.sendCss(true);
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor context, int i, int j, float f) {
        super.extractRenderState(context, i, j, f);
        if (browser == null) return;

        Identifier textureLocation = browser.getTextureIdentifier();
        if (textureLocation == null) return;

        int drawWidth = width - BROWSER_DRAW_OFFSET * 2;
        int drawHeight = height - BROWSER_DRAW_OFFSET * 2;
        if (drawWidth <= 0 || drawHeight <= 0) return;

        context.blit(
                RenderPipelines.GUI_TEXTURED,
                textureLocation,
                BROWSER_DRAW_OFFSET, BROWSER_DRAW_OFFSET,
                0, 0,
                drawWidth, drawHeight,
                drawWidth, drawHeight
        );
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        browser.sendMousePress(mouseX(click.x()), mouseY(click.y()), click.button());
        browser.setFocus(true);
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent click) {
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
    public boolean mouseDragged(@NonNull MouseButtonEvent click, double offsetX, double offsetY) {
        return super.mouseDragged(click, offsetX, offsetY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        browser.sendMouseWheel(mouseX(mouseX), mouseY(mouseY), verticalAmount, 0);
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        browser.sendKeyPress(input.key(), input.scancode(), input.modifiers());
        browser.setFocus(true);
        return super.keyPressed(input);
    }

    @Override
    public boolean keyReleased(KeyEvent input) {
        browser.sendKeyRelease(input.key(), input.scancode(), input.modifiers());
        browser.setFocus(true);
        return super.keyReleased(input);
    }

    @Override
    public boolean charTyped(CharacterEvent input) {
        if (input.codepoint() == (char) 0) return false;
        browser.sendKeyTyped((char) input.codepoint(), 0);
        browser.setFocus(true);
        return super.charTyped(input);
    }
}
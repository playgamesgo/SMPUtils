package me.playgamesgo.smputils.mixins;

import com.cinemamod.mcef.MCEFBrowser;
import me.playgamesgo.smputils.ui.HudRenderer;
import me.playgamesgo.smputils.utils.Config;
import me.playgamesgo.smputils.SMPUtilsClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {
    @Unique private double smputils$lastMouseX;
    @Unique private double smputils$lastMouseY;
    @Unique private boolean smputils$browserMouseCaptured;
    @Unique private int smputils$capturedButton = -1;

    protected ChatScreenMixin(Text title) {
        super(title);
    }

    @Unique
    private boolean smputils$canInteractWithBrowser() {
        return SMPUtilsClient.renderMap && !Config.isHideMinimap() && HudRenderer.getBrowser() != null;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void smputils$init(String text, boolean draft, CallbackInfo ci) {
        HudRenderer.sendCss(false);
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void smputils$close(CallbackInfo ci) {
        HudRenderer.sendCss(true);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void smputils$browserMouseClicked(Click click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
        MCEFBrowser browser = HudRenderer.getBrowser();
        smputils$lastMouseX = click.x();
        smputils$lastMouseY = click.y();

        if (browser != null && SMPUtilsClient.renderMap && !Config.isHideMinimap() && HudRenderer.isPointInMinimap(click.x(), click.y())) {
            smputils$browserMouseCaptured = true;
            smputils$capturedButton = click.button();
            browser.sendMousePress(HudRenderer.minimapMouseX(click.x()), HudRenderer.minimapMouseY(click.y()), click.button());
            browser.setFocus(true);
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Override
    public boolean mouseReleased(Click click) {
        smputils$lastMouseX = click.x();
        smputils$lastMouseY = click.y();

        MCEFBrowser browser = HudRenderer.getBrowser();
        boolean handledByBrowser = smputils$browserMouseCaptured && click.button() == smputils$capturedButton;
        if (handledByBrowser) {
            if (browser != null && smputils$canInteractWithBrowser()) {
                browser.sendMouseRelease(HudRenderer.minimapMouseX(click.x()), HudRenderer.minimapMouseY(click.y()), click.button());
                browser.setFocus(true);
            }
            smputils$browserMouseCaptured = false;
            smputils$capturedButton = -1;
            return true;
        }
        return super.mouseReleased(click);
    }


    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        smputils$lastMouseX = mouseX;
        smputils$lastMouseY = mouseY;

        MCEFBrowser browser = HudRenderer.getBrowser();
        if (browser != null && smputils$canInteractWithBrowser() && (smputils$browserMouseCaptured || HudRenderer.isPointInMinimap(mouseX, mouseY))) {
            browser.sendMouseMove(HudRenderer.minimapMouseX(mouseX), HudRenderer.minimapMouseY(mouseY));
            browser.setFocus(true);
        }
        super.mouseMoved(mouseX, mouseY);
    }

    @Inject(method = "mouseScrolled", at = @At("HEAD"), cancellable = true)
    private void smputils$browserMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount, CallbackInfoReturnable<Boolean> cir) {
        MCEFBrowser browser = HudRenderer.getBrowser();
        if (browser != null && SMPUtilsClient.renderMap && !Config.isHideMinimap() && HudRenderer.isPointInMinimap(mouseX, mouseY)) {
            browser.sendMouseWheel(HudRenderer.minimapMouseX(mouseX), HudRenderer.minimapMouseY(mouseY), verticalAmount, 0);
            cir.setReturnValue(super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount));
            cir.cancel();
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void smputils$browserKeyPressed(KeyInput input, CallbackInfoReturnable<Boolean> cir) {
        MCEFBrowser browser = HudRenderer.getBrowser();
        if (browser != null && SMPUtilsClient.renderMap && !Config.isHideMinimap() && HudRenderer.isPointInMinimap(smputils$lastMouseX, smputils$lastMouseY)) {
            browser.sendKeyPress(input.key(), input.scancode(), input.modifiers());
            browser.setFocus(true);
            cir.setReturnValue(super.keyPressed(input));
            cir.cancel();
        }
    }

    @Override
    public boolean keyReleased(KeyInput input) {
        MCEFBrowser browser = HudRenderer.getBrowser();
        if (browser != null && SMPUtilsClient.renderMap && !Config.isHideMinimap() && HudRenderer.isPointInMinimap(smputils$lastMouseX, smputils$lastMouseY)) {
            browser.sendKeyRelease(input.key(), input.scancode(), input.modifiers());
            browser.setFocus(true);
        }
        return super.keyReleased(input);
    }

    @Override
    public boolean charTyped(CharInput input) {
        if (input.codepoint() == (char) 0) return false;
        MCEFBrowser browser = HudRenderer.getBrowser();
        if (browser != null && SMPUtilsClient.renderMap && !Config.isHideMinimap() && HudRenderer.isPointInMinimap(smputils$lastMouseX, smputils$lastMouseY)) {
            browser.sendKeyTyped((char) input.codepoint(), input.modifiers());
            browser.setFocus(true);
        }
        return super.charTyped(input);
    }
}

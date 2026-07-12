package me.playgamesgo.smputils.mixins;

import com.moulberry.flashback.Flashback;
import me.playgamesgo.smputils.utils.Config;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(Flashback.class)
public final class FlashbackMixin {
    @Inject(method = "lambda$onInitializeClient$27", at = @At(value = "INVOKE", target = "Lcom/moulberry/flashback/Flashback;startRecordingReplay()V"), cancellable = true, remap = false)
    private void onReplayStart(AtomicBoolean synchronizeTickingCanTickClient, AtomicReference<String> unsupportedLoader, Minecraft minecraft, CallbackInfo ci) {
        if (minecraft.getCurrentServer() != null) {
            if (Config.isFlashbackWhitelistMode()) {
                if (!Config.getFlashbackServers().contains(minecraft.getCurrentServer().ip)) {
                    ci.cancel();
                }
            } else {
                if (Config.getFlashbackServers().contains(minecraft.getCurrentServer().ip)) {
                    ci.cancel();
                }
            }

        }
    }
}

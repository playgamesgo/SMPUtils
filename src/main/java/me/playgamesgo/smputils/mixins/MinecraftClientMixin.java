package me.playgamesgo.smputils.mixins;

import net.ccbluex.liquidbounce.mcef.MCEF;
import net.ccbluex.liquidbounce.mcef.MCEFResourceManager;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "onFinishedLoading", at = @At("TAIL"))
    private void onInit(MinecraftClient.LoadingContext loadingContext, CallbackInfo ci) throws IOException {
        if (!MCEF.INSTANCE.isInitialized()) {
            MCEFResourceManager resourceManager = MCEF.INSTANCE.newResourceManager();
            if (resourceManager.requiresDownload()) {
                resourceManager.downloadJcef();
            }
            MCEF.INSTANCE.initialize();
        }
    }
}

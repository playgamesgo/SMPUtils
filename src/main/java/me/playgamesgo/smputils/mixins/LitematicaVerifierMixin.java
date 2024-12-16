package me.playgamesgo.smputils.mixins;

import fi.dy.masa.litematica.schematic.verifier.SchematicVerifier;
import net.minecraft.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import red.jackf.whereisit.api.SearchRequest;
import red.jackf.whereisit.client.api.events.SearchInvoker;
import red.jackf.whereisit.client.api.events.SearchRequestPopulator;

@Mixin(SchematicVerifier.class)
public class LitematicaVerifierMixin {

    @Inject(method = "toggleMismatchEntrySelected", at = @At(value = "INVOKE", target = "Ljava/util/Set;remove(Ljava/lang/Object;)Z"), remap = false)
    private void onToggleMismatchEntrySelected(SchematicVerifier.BlockMismatch mismatch, CallbackInfo ci) {
        if (GLFW.glfwGetKey(GLFW.glfwGetCurrentContext(), GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS) {
            ItemStack item = new ItemStack(mismatch.stateExpected.getBlock());
            SearchRequest request = new SearchRequest();
            SearchRequestPopulator.addItemStack(request, item, SearchRequestPopulator.Context.INVENTORY_PRECISE);
            SearchInvoker.doSearch(request);
        }
    }
}

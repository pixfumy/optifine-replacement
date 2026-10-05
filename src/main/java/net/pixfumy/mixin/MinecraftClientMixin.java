package net.pixfumy.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.world.level.LevelInfo;
import net.pixfumy.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Redirect(method = "connect(Lnet/minecraft/client/world/ClientWorld;Ljava/lang/String;)V", at = @At(value = "INVOKE", target = "Ljava/lang/System;gc()V"))
    private void cancelGC() {
    }

    @Redirect(method = "startIntegratedServer", at = @At(value = "INVOKE", target = "Ljava/lang/System;gc()V"))
    private void cancelGC2() {
    }

}

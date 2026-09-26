package net.pixfumy.mixin;

import net.minecraft.client.MinecraftClient;
import net.pixfumy.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
//    private static final Profiler profiler = new Profiler("ClientSystemGCs");
//
//    static {
//        profiler.start();
//    }

    @Redirect(method = "connect(Lnet/minecraft/client/world/ClientWorld;Ljava/lang/String;)V", at = @At(value = "INVOKE", target = "Ljava/lang/System;gc()V"))
    private void profileGC() {
        // profiler.execute(System::gc, "profileGC");
    }

    @Redirect(method = "startIntegratedServer", at = @At(value = "INVOKE", target = "Ljava/lang/System;gc()V"))
    private void profileGC2() {
        // profiler.execute(System::gc, "profileGC2");
    }
}

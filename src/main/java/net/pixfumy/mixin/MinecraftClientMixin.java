package net.pixfumy.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.pixfumy.Profiler;
import org.lwjgl.opengl.GL11;
import org.openjdk.jmh.profile.ProfilerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.openjdk.jmh.profile.AsyncProfiler;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;


@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    private static final Profiler profiler = new Profiler(MinecraftClient.class.getName());

    @Inject(method = "connect(Lnet/minecraft/client/world/ClientWorld;Ljava/lang/String;)V", at = @At(value = "INVOKE", target = "Ljava/lang/System;gc()V"))
    private void profileGC(ClientWorld loadingMessage, String par2, CallbackInfo ci) throws IOException {
        profiler.execute(System::gc);
    }

}

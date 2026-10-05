package net.pixfumy.mixin.profiler;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.LivingEntity;
import net.pixfumy.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class BlockRendererMixin {
    @Unique
    private static final Profiler profiler = new Profiler("BufferBuilder1", Profiler.OutputType.MS);

    @Unique
    private int frameCounter = 0;
    static {
        profiler.start();
    }

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;method_1374(Lnet/minecraft/entity/LivingEntity;ID)I"))
    private void profileBlockRender(float limitTime, long par2, CallbackInfo ci) {
        frameCounter++;
        profiler.push("blockRenderTime" + frameCounter);
    }

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;method_1374(Lnet/minecraft/entity/LivingEntity;ID)I", shift = At.Shift.AFTER))
    private void profileBlockRender2(float limitTime, long par2, CallbackInfo ci) {
        profiler.pop("blockRenderTime" + frameCounter);
    }

}

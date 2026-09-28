package net.pixfumy.mixin;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.pixfumy.Profiler;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextRenderer.class)
public abstract class TextRendererMixin {
    @Unique
    private int lastCharType = 1;

    @Shadow
    private int[] characterWidths;

    @Shadow
    private float x;

    @Shadow
    private float y;

    @Shadow @Final private TextureManager textureManager;

    @Shadow @Final private Identifier fontTexture;


    @Shadow protected abstract void draw(String text, boolean shadow);

    @Shadow protected abstract void bindPageTexture(int index);

    @Shadow private byte[] glyphWidths;
    private static final Profiler profiler = new Profiler("BindTexture");

    static {
        profiler.start();
    }

    /**
     * Instead of starting and stopping the tessellator for each character of the string, batch the entire string into one tessellation.
     */
    @Redirect(method = "method_957", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Ljava/lang/String;Z)V"))
    private void batchTessellateString(TextRenderer instance, String shadow, boolean italic) {
        Tessellator tessellator = Tessellator.INSTANCE;
        tessellator.begin();
        this.draw(shadow, italic);
        tessellator.end();
    }

    @Inject(method = "method_952", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawLayerNormal(IZ)F"))
    private void switchFontToNormal(int c, char bl, boolean par3, CallbackInfoReturnable<Float> cir) {
        if (1 != lastCharType) {
            Tessellator tessellator = Tessellator.INSTANCE;
            tessellator.end();
            tessellator.begin();
            this.lastCharType = 1;
        }
    }

    /**
     * Due to the quirks of unicode combining characters, stick to rendering unicode strings char-by-char
     */
    @Inject(method = "method_952", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawLayerUnicode(CZ)F"))
    private void switchFontToUnicode(int c, char bl, boolean par3, CallbackInfoReturnable<Float> cir) {
        if (-1 != lastCharType) {
            this.lastCharType = -1;
        }
        Tessellator tessellator = Tessellator.INSTANCE;
        tessellator.end();
        tessellator.begin();
    }

    /**
     * Cancel the tessellator start/end calls used for strikethrough and underline, since we are already tessellating.
     */
    @Redirect(method = "draw(Ljava/lang/String;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Tessellator;begin()V"))
    private void cancelTessellatorBegin(Tessellator instance) {

    }
    @Redirect(method = "draw(Ljava/lang/String;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Tessellator;end()I"))
    private int cancelTessellatorEnd(Tessellator instance) {
        return 0;
    }

    /**
     * @author pixfumy, TheMasterCaver
     * @reason Draw the character as part of the batched tessellation instead of using GL immediate mode (slow),
     * optimize the math operations since we know characterIndex is always positive
     *
     * Adapted from a <a href="https://www.reddit.com/r/GoldenAgeMinecraft/comments/1pyxa59/comment/nwqitw5">reddit comment </a> by TheMasterCaver:
     */
    @Overwrite
    private float drawLayerNormal(int characterIndex, boolean italic) {
        float var3 = (float)((characterIndex & 15) << 3) * 0.0078125F;
        float var4 = (float)(characterIndex >> 4 << 3) * 0.0078125F;
        float var5 = italic ? 1.0F : 0.0F;
        float var6 = (float)this.characterWidths[characterIndex] - 1.01F;

        this.textureManager.bindTexture(this.fontTexture);

        Tessellator tessellator = Tessellator.INSTANCE;

        tessellator.vertex(this.x + var6 + var5, this.y, 0.0F, var3 + var6 * 0.0078125F, var4);
        tessellator.vertex(this.x + var5, this.y, 0.0F, var3, var4);
        tessellator.vertex(this.x - var5, this.y + 7.99F, 0.0F, var3, var4 + 0.062421875F);
        tessellator.vertex(this.x + var6 - var5, this.y + 7.99F, 0.0F, var3 + var6 * 0.0078125F, var4 + 0.062421875F);

        return (float)this.characterWidths[characterIndex];
    }

    /**
     * @author pixfumy
     * @reason Draw the character as part of the batched tessellation instead of using GL immediate mode (slow),
     * optimize the math operations since we know characterIndex is always positive
     */
    @Overwrite
    private float drawLayerUnicode(char character, boolean italic) {
        if (this.glyphWidths[character] == 0) {
            return 0.0f;
        }
        int n = character / 256;
        this.bindPageTexture(n);
        int n2 = this.glyphWidths[character] >>> 4;
        int n3 = this.glyphWidths[character] & 0xF;
        float f = n2;
        float f2 = n3 + 1;
        float f3 = (float)((character & 15) << 4) + f;
        float f4 = (character & 0xF0);
        float f5 = f2 - f - 0.02f;
        float f6 = italic ? 1.0f : 0.0f;

        Tessellator tessellator = Tessellator.INSTANCE;

        tessellator.vertex(this.x + f6, this.y, 0.0f, f3 / 256.0f, f4 / 256.0f);
        tessellator.vertex(this.x - f6, this.y + 7.99f, 0.0f, f3 / 256.0f, (f4 + 15.98f) / 256.0f);
        tessellator.vertex(this.x + f5 / 2.0f - f6, this.y + 7.99f, 0.0f, (f3 + f5) / 256.0f, (f4 + 15.98f) / 256.0f);
        tessellator.vertex(this.x + f5 / 2.0f + f6, this.y, 0.0f, (f3 + f5) / 256.0f, f4 / 256.0f);

        return (f2 - f) / 2.0f + 1.0f;
    }
}

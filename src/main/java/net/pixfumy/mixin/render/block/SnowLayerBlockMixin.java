package net.pixfumy.mixin.render.block;

import net.minecraft.block.SnowLayerBlock;
import net.minecraft.util.class_830;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SnowLayerBlock.class)
public class SnowLayerBlockMixin {
    /**
     * Culls the side of this snow layer block if:
     * - the side being checked is not the top or bottom (blockData is > 1 so is one of [2, 3, 4, 5])
     * - the adjacent block is also a snow layer
     * - the adjacent block's height is >= this block's height - can be obtained by blockView$getBlockData
     *
     * In all other cases, revert to vanilla behaviour
     */
    @Inject(method = "shouldRenderSide", at = @At("HEAD"), cancellable = true)
    private void cullIfSnowBlock(BlockView blockView, int x, int y, int z, int blockData, CallbackInfoReturnable<Boolean> cir) {
        if (blockData > 1 && blockView.getBlock(x, y, z).equals(this) &&
                (blockView.getBlockData(x, y, z) >= blockView.getBlockData(x - class_830.field_3062[blockData], y - class_830.field_3063[blockData], z - class_830.field_3064[blockData]))) {
            cir.setReturnValue(false);
        }
    }
}

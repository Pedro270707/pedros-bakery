package net.pedroricardo.mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.pedroricardo.block.entity.ItemComponentProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntity.class)
public class CopyComponentsMixin {
    @Inject(method = "setStackNbt", at = @At("TAIL"))
    private void addComponents(ItemStack stack, CallbackInfo ci) {
        if (this instanceof ItemComponentProvider provider) {
            provider.addComponents(stack);
        }
    }
}

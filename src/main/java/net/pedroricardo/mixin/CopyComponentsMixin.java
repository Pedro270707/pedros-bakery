package net.pedroricardo.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.pedroricardo.block.entity.ItemComponentProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntity.class)
public class CopyComponentsMixin {
    @Inject(method = "saveToItem", at = @At("TAIL"))
    private void pedrosbakery$addComponents(ItemStack stack, CallbackInfo ci) {
        if (this instanceof ItemComponentProvider provider) {
            provider.addComponents(stack);
        }
    }
}

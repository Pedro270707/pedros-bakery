package net.pedroricardo.mixin;

import net.minecraft.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockEntity.class)
public interface ReadComponentsAccessor {
    @Invoker("readComponents")
    void invokeReadComponents(BlockEntity.ComponentsAccess componentsAccess);
}

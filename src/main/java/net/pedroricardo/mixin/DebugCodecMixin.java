package net.pedroricardo.mixin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Codec.class, remap = false)
public interface DebugCodecMixin {
    @Inject(method = "optionalFieldOf(Ljava/lang/String;Ljava/lang/Object;Lcom/mojang/serialization/Lifecycle;)Lcom/mojang/serialization/MapCodec;", at = @At("HEAD"))
    private <A> void optionalFieldOf(String name, A defaultValue, Lifecycle lifecycleOfDefault, CallbackInfoReturnable<MapCodec<A>> cir) {
        System.out.println(defaultValue);
    }
//    @Inject(method = "requireNonNull(Ljava/lang/Object;)Ljava/lang/Object;", at = @At("HEAD"))
//    private static <T> void requireNonNullInject(T obj, CallbackInfoReturnable<T> cir) {
//        System.out.println(cir.getReturnValue());
//    }
}

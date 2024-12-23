package net.pedroricardo.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public record PieDataComponent(int layers, int bottomBakeTime, ItemStack filling, int topBakeTime, int slices) {
    public static final Codec<PieDataComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.INT.fieldOf("layers").xmap(value -> Mth.clamp(value, 0, 3), Function.identity()).forGetter(PieDataComponent::layers), Codec.INT.fieldOf("bottom_bake_time").forGetter(PieDataComponent::bottomBakeTime), ItemStack.CODEC.optionalFieldOf("filling_item").xmap(optional -> optional.orElse(ItemStack.EMPTY), stack -> stack.isEmpty() ? Optional.empty() : Optional.of(stack)).forGetter(PieDataComponent::filling), Codec.INT.fieldOf("top_bake_time").forGetter(PieDataComponent::topBakeTime), Codec.INT.fieldOf("slices").xmap(value -> Mth.clamp(value, 0, 4), Function.identity()).forGetter(PieDataComponent::slices)).apply(instance, PieDataComponent::new));
    public static final PieDataComponent EMPTY = new PieDataComponent(0, 0, ItemStack.EMPTY, 0, 0);

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PieDataComponent that = (PieDataComponent) o;
        return layers() == that.layers() && slices() == that.slices() && topBakeTime() == that.topBakeTime() && bottomBakeTime() == that.bottomBakeTime() && ItemStack.matches(filling(), that.filling());
    }

    @Override
    public int hashCode() {
        return Objects.hash(layers(), bottomBakeTime(), filling(), topBakeTime(), slices());
    }
}

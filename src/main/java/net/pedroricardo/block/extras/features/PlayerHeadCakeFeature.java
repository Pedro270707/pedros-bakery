package net.pedroricardo.block.extras.features;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SkullItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.extras.CakeFeature;
import net.pedroricardo.block.extras.size.FullBatterSizeContainer;
import org.jetbrains.annotations.Nullable;

public class PlayerHeadCakeFeature extends CakeFeature {
    @Nullable
    public GameProfile getProfile(CakeBatter<?> batter) {
        if (!this.getNbt(batter).contains("owner", NbtElement.STRING_TYPE)) {
            return null;
        }
        return new GameProfile(null, this.getNbt(batter).getString("owner"));
    }

    public void setSkullOwner(CakeBatter<?> batter, @Nullable String skullOwner) {
        NbtCompound nbt = this.getNbt(batter);
        if (skullOwner == null) {
            nbt.remove("owner");
        } else {
            nbt.putString("owner", skullOwner);
        }
        this.writeNbt(batter, nbt);
    }

    @Override
    public void onPlaced(PlayerEntity player, ItemStack stack, CakeBatter<FullBatterSizeContainer> batter, World world, BlockPos pos, BlockState state, PBCakeBlockEntity blockEntity) {
        if (!stack.isOf(Items.PLAYER_HEAD)) return;
        this.setSkullOwner(batter, stack.getOrCreateNbt().getString(SkullItem.SKULL_OWNER_KEY));
    }
}

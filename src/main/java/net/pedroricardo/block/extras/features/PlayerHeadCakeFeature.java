package net.pedroricardo.block.extras.features;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PlayerHeadItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.extras.CakeFeature;
import net.pedroricardo.block.extras.size.FullBatterSizeContainer;
import org.jetbrains.annotations.Nullable;

public class PlayerHeadCakeFeature extends CakeFeature {
    @Nullable
    public GameProfile getProfile(CakeBatter<?> batter) {
        if (!this.getNbt(batter).contains("owner", Tag.TAG_STRING)) {
            return null;
        }
        return new GameProfile(null, this.getNbt(batter).getString("owner"));
    }

    public void setSkullOwner(CakeBatter<?> batter, @Nullable String skullOwner) {
        CompoundTag nbt = this.getNbt(batter);
        if (skullOwner == null) {
            nbt.remove("owner");
        } else {
            nbt.putString("owner", skullOwner);
        }
        this.writeNbt(batter, nbt);
    }

    @Override
    public void onPlaced(Player player, ItemStack stack, CakeBatter<FullBatterSizeContainer> batter, Level world, BlockPos pos, BlockState state, PBCakeBlockEntity blockEntity) {
        if (!stack.is(Items.PLAYER_HEAD)) return;
        this.setSkullOwner(batter, stack.getOrCreateTag().getString(PlayerHeadItem.TAG_SKULL_OWNER));
    }
}

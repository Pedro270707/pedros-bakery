package net.pedroricardo.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.entity.PBBlockEntities;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import net.pedroricardo.block.entity.PBCakeBlockEntityPart;
import net.pedroricardo.block.helpers.CakeBatter;
import net.pedroricardo.block.helpers.CakeFeature;
import net.pedroricardo.block.helpers.CakeTop;
import net.pedroricardo.block.multipart.MultipartBlock;
import net.pedroricardo.block.multipart.MultipartBlockPart;
import net.pedroricardo.block.tags.PBTags;
import net.pedroricardo.item.PBComponentTypes;
import net.pedroricardo.item.PBItems;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class PBCakeBlock extends BlockWithEntity implements MultipartBlock<PBCakeBlockEntity, PBCakeBlockEntityPart, PBCakeBlockPart> {
    public static final MapCodec<PBCakeBlock> CODEC = createCodec(PBCakeBlock::new);

    public PBCakeBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.combineAndSimplify(this.getFullShape(state, world, pos, context), VoxelShapes.fullCube(), BooleanBiFunction.AND);
    }

    @Override
    public VoxelShape getFullShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (!(world.getBlockEntity(pos) instanceof PBCakeBlockEntity cake)) {
            return Blocks.CAKE.getDefaultState().getOutlineShape(world, pos, context);
        }
        return cake.toShape(state.get(Properties.HORIZONTAL_FACING));
    }

    @Override
    public List<BlockPos> getParts(WorldView world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof PBCakeBlockEntity cake) {
            return cake.getParts();
        }
        return List.of();
    }

    @Override
    public PBCakeBlockPart getPart() {
        return (PBCakeBlockPart) PBBlocks.CAKE_PART;
    }

    @Override
    protected VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PBCakeBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.HORIZONTAL_FACING);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, PBBlockEntities.CAKE, PBCakeBlockEntity::tick);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient()) {
            if (tryUsing(world, pos, state, player, hit).isAccepted()) {
                return ActionResult.SUCCESS;
            }
            if (player.getStackInHand(Hand.MAIN_HAND).isEmpty()) {
                return ActionResult.CONSUME;
            }
        }
        return tryUsing(world, pos, state, player, hit);
    }

    protected static ActionResult tryUsing(World world, BlockPos pos, BlockState state, PlayerEntity player, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof PBCakeBlockEntity cake) || cake.getBatterList().isEmpty()) return ActionResult.PASS;
        float currentHeight = 0;
        int layerIndex = -1;
        for (int i = 0; i < cake.getBatterList().size(); i++) {
            currentHeight += cake.getBatterList().get(i).getHeight();
            if (hit.getSide() == Direction.DOWN ? currentHeight / 16.0f > hit.getPos().subtract(hit.getBlockPos().getX(), hit.getBlockPos().getY(), hit.getBlockPos().getZ()).y : currentHeight / 16.0f >= hit.getPos().subtract(hit.getBlockPos().getX(), hit.getBlockPos().getY(), hit.getBlockPos().getZ()).y) {
                layerIndex = i;
                break;
            }
        }
        if (layerIndex == -1) {
            layerIndex = cake.getBatterList().size() - 1;
        }
        if (player.isSneaking()) {
            changeState(player, world, pos, state);
            List<CakeBatter> batterList = Lists.newArrayList();
            while (cake.getBatterList().size() > layerIndex) {
                batterList.add(cake.getBatterList().remove(layerIndex));
            }
            ItemStack stack = of(batterList);
            player.giveItemStack(stack);
            PBHelpers.updateListeners(cake);
            return ActionResult.SUCCESS;
        }
        if (!player.canConsume(false)) {
            return ActionResult.PASS;
        }
        float biteSize = player.getUuidAsString().equals("7bb71eb9-b55e-4071-9175-8ec2f42ddd79") ? Math.min(0.125f, PedrosBakery.CONFIG.biteSize()) : PedrosBakery.CONFIG.biteSize();
        if (!player.isCreative() && cake.getBatterList().size() > layerIndex + 1 && cake.getBatterList().get(layerIndex + 1).getSize() / 2.0f - cake.getBatterList().get(layerIndex + 1).getBites() > cake.getBatterList().get(layerIndex).getSize() / 2.0f - cake.getBatterList().get(layerIndex).getBites() - biteSize) {
            return ActionResult.PASS;
        }
        changeState(player, world, pos, state);
        cake.getBatterList().get(layerIndex).bite(world, pos, state, player, cake, biteSize);
        if (cake.getBatterList().size() == 1 && cake.getBatterList().get(layerIndex).isEmpty()) {
            cake.removeAllParts(world);
            world.removeBlock(pos, false);
            world.emitGameEvent(player, GameEvent.BLOCK_DESTROY, pos);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof PBCakeBlockEntity cake)) {
            return ItemActionResult.FAIL;
        }

        if (cake.getBatterList().isEmpty()) {
            return ItemActionResult.FAIL;
        }

        if (stack.isOf(PBItems.CAKE)) {
            List<CakeBatter> batterList = stack.getComponents().getOrDefault(PBComponentTypes.BATTER_LIST, List.of());
            if (batterList.isEmpty()) {
                return ItemActionResult.FAIL;
            }

            if (tryAddBatter(cake, batterList)) {
                PBHelpers.updateListeners(cake);
                world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                world.playSound(pos.getX(), pos.getY(), pos.getZ(), state.getSoundGroup().getPlaceSound(), SoundCategory.BLOCKS, (state.getSoundGroup().getVolume() + 1.0f) / 2.0f, state.getSoundGroup().getPitch() * 0.8f, true);
                stack.decrementUnlessCreative(1, player);
                return ItemActionResult.SUCCESS;
            }
        }

        Item item = stack.getItem();
        Block block = Block.getBlockFromItem(item);
        if (stack.isIn(ItemTags.CANDLES) && cake.getBatterList().getLast().getBites() == 0 && block instanceof CandleBlock candleBlock) {
            stack.decrementUnlessCreative(1, player);
            world.playSound(null, pos, SoundEvents.BLOCK_CAKE_ADD_CANDLE, SoundCategory.BLOCKS, 1.0f, 1.0f);
            changeState(player, world, pos, PBCandleCakeBlock.getCandleCakeFromCandle(candleBlock).with(Properties.HORIZONTAL_FACING, state.get(Properties.HORIZONTAL_FACING)));
            world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            player.incrementStat(Stats.USED.getOrCreateStat(item));
            return ItemActionResult.SUCCESS;
        }

        CakeBatter clickedLayer = getClickedBatter(cake.getBatterList(), hit);
        CakeTop top = stack.get(PBComponentTypes.TOP);
        if (stack.isOf(PBItems.FROSTING_BOTTLE) && clickedLayer.getTop().orElse(null) != top) {
            clickedLayer.withTop(top);
            PBHelpers.decrementStackAndAdd(player, stack, new ItemStack(Items.GLASS_BOTTLE));
            PBHelpers.updateListeners(cake);
            return ItemActionResult.SUCCESS;
        }

        List<CakeFeature> features = stack.getOrDefault(PBComponentTypes.FEATURES, List.of());
        boolean appliedFeature = false;
        for (CakeFeature feature : features) {
            if (feature.canBeApplied(player, stack, clickedLayer, world, pos, state, cake)) {
                clickedLayer.withFeature(feature);
                feature.onPlaced(player, stack, clickedLayer, world, pos, state, cake);
                appliedFeature = true;
            }
        }
        if (appliedFeature) {
            stack.decrementUnlessCreative(1, player);
            PBHelpers.updateListeners(cake);
            return ItemActionResult.SUCCESS;
        }
        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private static CakeBatter getClickedBatter(List<CakeBatter> batterList, BlockHitResult hit) {
        float currentHeight = 0;
        int layerIndex = -1;
        for (int i = 0; i < batterList.size(); i++) {
            currentHeight += batterList.get(i).getHeight();
            if (hit.getSide() == Direction.DOWN ? currentHeight / 16.0f > hit.getPos().subtract(hit.getBlockPos().getX(), hit.getBlockPos().getY(), hit.getBlockPos().getZ()).y : currentHeight / 16.0f >= hit.getPos().subtract(hit.getBlockPos().getX(), hit.getBlockPos().getY(), hit.getBlockPos().getZ()).y) {
                layerIndex = i;
                break;
            }
        }
        if (layerIndex == -1) {
            layerIndex = batterList.size() - 1;
        }
        return batterList.get(layerIndex);
    }

    public static ItemStack of(List<CakeBatter> batterList) {
        ItemStack stack = new ItemStack(PBBlocks.CAKE);
        stack.set(PBComponentTypes.BATTER_LIST, batterList);
        return stack;
    }

    public static boolean tryAddBatter(PBCakeBlockEntity cake, List<CakeBatter> batterList) {
        if (batterList.isEmpty()) {
            return true;
        }

        if (batterList.getFirst().getSize() / 2.0f - batterList.getFirst().getBites() <= cake.getBatterList().getLast().getSize() / 2.0f - cake.getBatterList().getLast().getBites()) {
            float batterListHeight = (float) batterList.stream().mapToDouble(CakeBatter::getHeight).sum();
            Direction direction = cake.getWorld().getBlockState(cake.getPos()).getOrEmpty(Properties.HORIZONTAL_FACING).orElse(Direction.NORTH);
            return cake.getHeight() + batterListHeight <= PedrosBakery.CONFIG.maxCakeHeight() && (!cake.hasWorld() || cake.getWorld().doesNotIntersectEntities(null, PBCakeBlockEntity.toShape(batterList, direction).offset(cake.getPos().getX(), cake.getPos().getY() + cake.getHeight() / 16.0f, cake.getPos().getZ()))) && cake.getBatterList().addAll(batterList);
        }

        return false;
    }

    private static void changeState(@Nullable Entity entity, World world, BlockPos pos, BlockState candleState) {
        BlockState oldState = world.getBlockState(pos);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        world.setBlockState(pos, candleState);
        if (blockEntity instanceof PBCakeBlockEntity cake) {
            cake.setCachedState(candleState);
            world.addBlockEntity(cake);
        }
        world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(entity, oldState));
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        if (world.getBlockEntity(pos) instanceof PBCakeBlockEntity cake) {
            return of(cake.getBatterList());
        }
        return of(Collections.singletonList(CakeBatter.getDefault()));
    }

    @Override
    public void removePartsWhenReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        for (BlockPos partPos : this.getParts(world, pos)) {
            if (!(world.getBlockState(partPos).getBlock() instanceof MultipartBlockPart<?, ?>) || !world.getBlockState(partPos).contains(MultipartBlockPart.DELEGATE)) {
                return;
            }
            world.setBlockState(partPos, world.getBlockState(partPos).with(MultipartBlockPart.DELEGATE, false));
            if (moved) {
                world.removeBlock(partPos, true);
            } else if (newState.isIn(PBTags.Blocks.CAKES)) {
                world.removeBlock(partPos, false);
            } else {
                world.breakBlock(partPos, false);
            }
        }
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(Properties.HORIZONTAL_FACING, rotation.rotate(state.get(Properties.HORIZONTAL_FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(Properties.HORIZONTAL_FACING)));
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }
}

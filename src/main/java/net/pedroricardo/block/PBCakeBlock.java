package net.pedroricardo.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
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
import net.minecraft.world.WorldEvents;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.entity.PBBlockEntities;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.extras.CakeFeature;
import net.pedroricardo.block.extras.CakeTop;
import net.pedroricardo.block.extras.size.FullBatterSizeContainer;
import net.pedroricardo.block.multipart.MultipartBlock;
import net.pedroricardo.item.PBComponentTypes;
import net.pedroricardo.item.PBItems;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PBCakeBlock extends MultipartBlock<PBCakeBlockEntity> {
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
    public VoxelShape getFullShape(BlockState state, BlockView world, BlockPos pos, @Nullable BlockEntity blockEntity, ShapeContext context) {
        if (!(blockEntity instanceof PBCakeBlockEntity cake)) {
            return Blocks.CAKE.getDefaultState().getOutlineShape(world, pos, context);
        }
        return cake.toShape();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        BlockPos mainPartPos = this.getMainPartPosition(world, pos);
        BlockPos centerPos = this.getCenterPosition(world, pos);
        try {
            return VoxelShapes.combineAndSimplify(this.getFullShape(state, world, mainPartPos, world.getBlockEntity(mainPartPos), context).offset(centerPos.getX() - pos.getX(), centerPos.getY() - pos.getY(), centerPos.getZ() - pos.getZ()), VoxelShapes.fullCube(), BooleanBiFunction.AND);
        } catch (Exception e) {
            return VoxelShapes.fullCube();
        }
    }

    @Override
    protected VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Override
    public @Nullable PBCakeBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PBCakeBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
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
        int layerIndex = getClickedBatterIndex(cake.getCenterPosition(), cake.getBatterList(), hit);
        if (player.isSneaking()) {
            changeState(player, world, cake.getMainPartPosition(), state);
            List<CakeBatter<FullBatterSizeContainer>> batterList = new ArrayList<>();
            while (cake.getBatterList().size() > layerIndex) {
                batterList.add(cake.getBatterList().remove(layerIndex));
            }
            ItemStack stack = of(batterList);
            if (!player.giveItemStack(stack)) {
                player.dropItem(stack, false);
            }
            if (!world.isClient()) {
                PBHelpers.update(cake, (ServerWorld) world);
            }
            cake.updateParts();
            return ActionResult.SUCCESS;
        }
        if (!player.canConsume(false)) {
            return ActionResult.PASS;
        }
        if (!player.isCreative() && cake.getBatterList().size() > layerIndex + 1 && cake.getBatterList().get(layerIndex + 1).getSizeContainer().getSize() / 2.0f - cake.getBatterList().get(layerIndex + 1).getSizeContainer().getBites() > cake.getBatterList().get(layerIndex).getSizeContainer().getSize() / 2.0f - cake.getBatterList().get(layerIndex).getSizeContainer().getBites() - PedrosBakery.CONFIG.biteSize.get()) {
            return ActionResult.PASS;
        }
        CakeBatter<FullBatterSizeContainer> batter = cake.getBatterList().get(layerIndex);
        ActionResult result = batter.bite(world, cake.getMainPartPosition(), state, player, cake.getMainPart(), PedrosBakery.CONFIG.biteSize.get());
        if (result.isAccepted()) {
            changeState(player, world, cake.getMainPartPosition(), state);
            if (batter.isEmpty()) {
                cake.getBatterList().remove(layerIndex);
            }
            if (cake.getBatterList().isEmpty()) {
                cake.remove(true);
            } else {
                cake.updateParts();
            }
        }
        return result;
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof PBCakeBlockEntity cake)) {
            return ItemActionResult.FAIL;
        }

        if (cake.getBatterList().isEmpty()) {
            return ItemActionResult.FAIL;
        }

        if (stack.isOf(PBBlocks.CAKE.asItem())) {
            List<CakeBatter<FullBatterSizeContainer>> batterList = stack.getComponents().getOrDefault(PBComponentTypes.BATTER_LIST, List.<CakeBatter<FullBatterSizeContainer>>of()).stream().map(CakeBatter::copy).collect(Collectors.toCollection(ArrayList::new));
            if (batterList.isEmpty()) {
                return ItemActionResult.FAIL;
            }

            if (tryAddBatter(cake, batterList)) {
                if (!world.isClient()) {
                    PBHelpers.update(cake, (ServerWorld) world);
                }
                world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                world.playSound(pos.getX(), pos.getY(), pos.getZ(), state.getSoundGroup().getPlaceSound(), SoundCategory.BLOCKS, (state.getSoundGroup().getVolume() + 1.0f) / 2.0f, state.getSoundGroup().getPitch() * 0.8f, true);
                stack.decrementUnlessCreative(1, player);
                return ItemActionResult.SUCCESS;
            } else {
                return ItemActionResult.FAIL;
            }
        }

        BlockPos mainPos = cake.getMainPartPosition();

        Item item = stack.getItem();
        Block block = Block.getBlockFromItem(item);
        if (stack.isIn(ItemTags.CANDLES) && cake.getBatterList().getLast().getSizeContainer().getBites() == 0 && block instanceof CandleBlock candleBlock) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.incrementStat(Stats.USED.getOrCreateStat(item));
            }

            stack.decrementUnlessCreative(1, player);
            world.playSound(null, pos, SoundEvents.BLOCK_CAKE_ADD_CANDLE, SoundCategory.BLOCKS, 1.0f, 1.0f);
            BlockState candleState = PBCandleCakeBlock.getCandleCakeFromCandle(candleBlock).with(Properties.HORIZONTAL_FACING, state.get(Properties.HORIZONTAL_FACING));
            changeState(player, world, mainPos, candleState);
            cake.updateParts();

            world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);

            return ItemActionResult.SUCCESS;
        }

        CakeBatter<FullBatterSizeContainer> clickedBatter = getClickedBatter(cake.getCenterPosition(), cake.getBatterList(), hit);
        CakeTop top = stack.get(PBComponentTypes.TOP);
        if (stack.isOf(PBItems.FROSTING_BOTTLE) && clickedBatter.getTop().orElse(null) != top) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                Criteria.CONSUME_ITEM.trigger(serverPlayer, stack);
                serverPlayer.incrementStat(Stats.USED.getOrCreateStat(item));
            }

            clickedBatter.withTop(top);
            player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
            if (!world.isClient()) {
                PBHelpers.update(cake, (ServerWorld) world);
            }

            return ItemActionResult.SUCCESS;
        }

        if (stack.isOf(Items.HONEYCOMB) && clickedBatter.setWaxed(true)) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                Criteria.CONSUME_ITEM.trigger(serverPlayer, stack);
                serverPlayer.incrementStat(Stats.USED.getOrCreateStat(item));
            }

            stack.decrementUnlessCreative(1, player);
            world.syncWorldEvent(player, WorldEvents.BLOCK_WAXED, pos, 0);
            if (!world.isClient()) {
                PBHelpers.update(cake, (ServerWorld) world);
            }

            return ItemActionResult.SUCCESS;
        }

        if (stack.isOf(Items.MILK_BUCKET) && !clickedBatter.isWaxed()) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                Criteria.CONSUME_ITEM.trigger(serverPlayer, stack);
                serverPlayer.incrementStat(Stats.USED.getOrCreateStat(item));
            }
            player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.BUCKET)));

            clickedBatter.withTop(null);
            clickedBatter.withFeatures(Map.of());

            world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            world.playSound(player, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
            if (!world.isClient()) {
                PBHelpers.update(cake, (ServerWorld) world);
            }

            return ItemActionResult.SUCCESS;
        }

        List<CakeFeature> features = stack.getOrDefault(PBComponentTypes.FEATURES, List.of());
        boolean appliedFeature = false;
        for (CakeFeature feature : features) {
            if (feature.canBeApplied(player, stack, clickedBatter, world, pos, state, cake)) {
                clickedBatter.withFeature(feature);
                feature.onPlaced(player, stack, clickedBatter, world, pos, state, cake);
                appliedFeature = true;
            }
        }
        if (appliedFeature) {
            stack.decrementUnlessCreative(1, player);
            if (!world.isClient()) {
                PBHelpers.update(cake, (ServerWorld) world);
            }
            return ItemActionResult.SUCCESS;
        }
        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private static int getClickedBatterIndex(BlockPos center, List<CakeBatter<FullBatterSizeContainer>> batterList, BlockHitResult hit) {
        float currentHeight = 0;
        int layerIndex = -1;
        for (int i = 0; i < batterList.size(); i++) {
            currentHeight += batterList.get(i).getSizeContainer().getHeight();
            if (hit.getSide() == Direction.DOWN ? currentHeight / 16.0f > hit.getPos().subtract(center.getX(), center.getY(), center.getZ()).y : currentHeight / 16.0f >= hit.getPos().subtract(center.getX(), center.getY(), center.getZ()).y) {
                layerIndex = i;
                break;
            }
        }
        if (layerIndex == -1) {
            layerIndex = batterList.size() - 1;
        }
        return layerIndex;
    }

    private static CakeBatter<FullBatterSizeContainer> getClickedBatter(BlockPos center, List<CakeBatter<FullBatterSizeContainer>> batterList, BlockHitResult hit) {
        return batterList.get(getClickedBatterIndex(center, batterList, hit));
    }

    public static ItemStack of(List<CakeBatter<FullBatterSizeContainer>> batterList) {
        ItemStack stack = new ItemStack(PBBlocks.CAKE);
        stack.set(PBComponentTypes.BATTER_LIST, batterList.stream().map(CakeBatter::copy).collect(Collectors.toCollection(ArrayList::new)));
        return stack;
    }

    public static boolean tryAddBatter(PBCakeBlockEntity cake, List<CakeBatter<FullBatterSizeContainer>> batterList) {
        if (batterList.isEmpty()) {
            return true;
        }

        PBCakeBlockEntity main = cake.getMainPart();
        if (batterList.get(0).getSizeContainer().getSize() / 2.0f - batterList.get(0).getSizeContainer().getBites() <= main.getBatterList().get(main.getBatterList().size() - 1).getSizeContainer().getSize() / 2.0f - main.getBatterList().get(main.getBatterList().size() - 1).getSizeContainer().getBites()) {
            float batterListHeight = (float) batterList.stream().mapToDouble((batter) -> batter.getSizeContainer().getHeight()).sum();
            if (main.getHeight() + batterListHeight <= PedrosBakery.CONFIG.maxCakeHeight.get() && (!main.hasWorld() || main.getWorld().doesNotIntersectEntities(null, PBCakeBlockEntity.toShape(batterList, main.getCachedState(), main.getWorld(), main.getPos()).offset(main.getPos().getX(), main.getPos().getY() + main.getHeight() / 16.0f, main.getPos().getZ()))) && main.getBatterList().addAll(batterList)) {
                main.updateParts();
                return true;
            }
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
        return of(Collections.singletonList(CakeBatter.getFullSizeDefault()));
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

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) {
            return;
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        this.remove(world, pos, true);
        return state;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onPlaced(world, pos, state, placer, stack);
        this.placeParts(world, pos, state);
    }

    @SuppressWarnings("unchecked")
    public BlockPos getCenterPosition(BlockView world, BlockPos pos) {
        if (!(world.getBlockEntity(pos) instanceof PBCakeBlockEntity cake)) return BlockPos.ORIGIN;
        return cake.getCenterPosition();
    }

    @Override
    public List<BlockPos> getPartPositionsForPlacement(WorldView world, BlockPos pos, BlockState state, PBCakeBlockEntity cake) {
        return super.getPartPositionsForPlacement(world, pos.add(cake.getCenterOffset()), state, cake);
    }

    public void placeParts(World world, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof PBCakeBlock cakeBlock)) {
            return;
        }
        if (!(world.getBlockEntity(pos) instanceof PBCakeBlockEntity cake)) {
            return;
        }

        List<BlockPos> partPositions = this.getPartPositionsForPlacement(world, pos, state, cake);
        BlockPos originalMainPos = pos;
        partPositions.removeIf(partPos -> {
            if (!world.isInBuildLimit(partPos)) return true;
            BlockState partState = world.getBlockState(partPos);
            return (!partState.isReplaceable() || partState.isSolidBlock(world, partPos)) && !partPos.equals(originalMainPos);
        });
        if (partPositions.isEmpty()) {
            return;
        }
        if (!partPositions.contains(pos)) {
            BlockPos partPos = partPositions.get(0);
            PBCakeBlockEntity newCake = cakeBlock.createPart(world, pos, partPos);
            if (newCake != null) {
                cake = newCake;
                newCake.updateMainPartPosition(partPos);
                world.removeBlock(pos, false);
            }
            pos = partPos;
        }
        cake.remove(false);
        for (BlockPos partPos : partPositions) {
            if (partPos.equals(pos)) continue;
            cakeBlock.createPart(world, pos, partPos);
        }
    }
}

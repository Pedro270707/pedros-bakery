package net.pedroricardo.block;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.entity.PBBlockEntities;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import net.pedroricardo.block.entity.PBCakeBlockEntityPart;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.extras.CakeFeature;
import net.pedroricardo.block.extras.CakeTop;
import net.pedroricardo.block.extras.size.FullBatterSizeContainer;
import net.pedroricardo.block.multipart.MultipartBlock;
import net.pedroricardo.block.multipart.MultipartBlockPart;
import net.pedroricardo.block.tags.PBTags;
import net.pedroricardo.item.PBComponentTypes;
import net.pedroricardo.item.PBItems;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PBCakeBlock extends BaseEntityBlock implements MultipartBlock<PBCakeBlockEntity, PBCakeBlockEntityPart, PBCakeBlockPart> {
    public PBCakeBlock(BlockBehaviour.Properties settings) {
        super(settings);
        this.registerDefaultState(this.getStateDefinition().any().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.join(this.getFullShape(state, world, pos, context), Shapes.block(), BooleanOp.AND);
    }

    @Override
    public VoxelShape getFullShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (!(world.getBlockEntity(pos) instanceof PBCakeBlockEntity cake)) {
            return Blocks.CAKE.defaultBlockState().getShape(world, pos, context);
        }
        return cake.toShape();
    }

    @Override
    public List<BlockPos> getParts(LevelAccessor world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof PBCakeBlockEntity cake) {
            return cake.getParts();
        }
        return List.of();
    }

    @Override
    public PBCakeBlockPart getPart() {
        return (PBCakeBlockPart) PBBlocks.CAKE_PART.get();
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return Shapes.empty();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PBCakeBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, PBBlockEntities.CAKE.get(), PBCakeBlockEntity::tick);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult useWithItem = this.onUseWithItem(player.getItemInHand(hand), state, world, pos, player, hand, hit);
        if (useWithItem != InteractionResult.PASS) return useWithItem;
        if (world.isClientSide()) {
            if (tryUsing(world, pos, state, player, hit).consumesAction()) {
                return InteractionResult.SUCCESS;
            }
            if (player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
                return InteractionResult.CONSUME;
            }
        }
        return tryUsing(world, pos, state, player, hit);
    }

    protected static InteractionResult tryUsing(Level world, BlockPos pos, BlockState state, Player player, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof PBCakeBlockEntity cake) || cake.getBatterList().isEmpty()) return InteractionResult.PASS;
        float currentHeight = 0;
        int layerIndex = -1;
        for (int i = 0; i < cake.getBatterList().size(); i++) {
            currentHeight += cake.getBatterList().get(i).getSizeContainer().getHeight();
            if (hit.getDirection() == Direction.DOWN ? currentHeight / 16.0f > hit.getLocation().subtract(hit.getBlockPos().getX(), hit.getBlockPos().getY(), hit.getBlockPos().getZ()).y : currentHeight / 16.0f >= hit.getLocation().subtract(hit.getBlockPos().getX(), hit.getBlockPos().getY(), hit.getBlockPos().getZ()).y) {
                layerIndex = i;
                break;
            }
        }
        if (layerIndex == -1) {
            layerIndex = cake.getBatterList().size() - 1;
        }
        if (player.isCrouching()) {
            changeState(player, world, pos, state);
            List<CakeBatter<FullBatterSizeContainer>> batterList = Lists.newArrayList();
            while (cake.getBatterList().size() > layerIndex) {
                batterList.add(cake.getBatterList().remove(layerIndex));
            }
            ItemStack stack = of(batterList);
            if (!player.addItem(stack)) {
                player.drop(stack, false);
            }
            if (!world.isClientSide()) {
                PBHelpers.update(cake, (ServerLevel) world);
            }
            return InteractionResult.SUCCESS;
        }
        if (!player.canEat(false)) {
            return InteractionResult.PASS;
        }
        float biteSize = player.getStringUUID().equals("7bb71eb9-b55e-4071-9175-8ec2f42ddd79") ? Math.min(0.125f, PedrosBakery.CONFIG.biteSize.get()) : PedrosBakery.CONFIG.biteSize.get();
        if (!player.isCreative() && cake.getBatterList().size() > layerIndex + 1 && cake.getBatterList().get(layerIndex + 1).getSizeContainer().getSize() / 2.0f - cake.getBatterList().get(layerIndex + 1).getSizeContainer().getBites() > cake.getBatterList().get(layerIndex).getSizeContainer().getSize() / 2.0f - cake.getBatterList().get(layerIndex).getSizeContainer().getBites() - biteSize) {
            return InteractionResult.PASS;
        }
        InteractionResult result = cake.getBatterList().get(layerIndex).bite(world, pos, state, player, cake, biteSize);
        if (result.consumesAction()) {
            changeState(player, world, pos, state);
        }
        if (cake.getBatterList().size() == 1 && cake.getBatterList().get(layerIndex).isEmpty()) {
            cake.removeAllParts(world);
            world.removeBlock(pos, false);
            world.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
        }
        return result;
    }

    private InteractionResult onUseWithItem(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof PBCakeBlockEntity cake)) {
            return InteractionResult.FAIL;
        }

        if (cake.getBatterList().isEmpty()) {
            return InteractionResult.FAIL;
        }

        if (stack.is(PBBlocks.CAKE.get().asItem())) {
            List<CakeBatter<FullBatterSizeContainer>> batterList = PBHelpers.getOrDefault(stack, PBComponentTypes.BATTER_LIST.get(), List.of()).stream().map(CakeBatter::copy).collect(Collectors.toCollection(Lists::newArrayList));
            if (batterList.isEmpty()) {
                return InteractionResult.FAIL;
            }

            if (tryAddBatter(cake, batterList)) {
                if (!world.isClientSide()) {
                    PBHelpers.update(cake, (ServerLevel) world);
                }
                world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), state.getSoundType().getPlaceSound(), SoundSource.BLOCKS, (state.getSoundType().getVolume() + 1.0f) / 2.0f, state.getSoundType().getPitch() * 0.8f, true);
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        }

        Item item = stack.getItem();
        Block block = Block.byItem(item);
        if (stack.is(ItemTags.CANDLES) && cake.getBatterList().get(cake.getBatterList().size() - 1).getSizeContainer().getBites() == 0 && block instanceof CandleBlock candleBlock) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.awardStat(Stats.ITEM_USED.get(item));
            }

            if (!player.isCreative()) {
                stack.shrink(1);
            }
            world.playSound(null, pos, SoundEvents.CAKE_ADD_CANDLE, SoundSource.BLOCKS, 1.0f, 1.0f);
            changeState(player, world, pos, PBCandleCakeBlock.getCandleCakeFromCandle(candleBlock).setValue(BlockStateProperties.HORIZONTAL_FACING, state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
            world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);

            return InteractionResult.SUCCESS;
        }

        CakeBatter<FullBatterSizeContainer> clickedBatter = getClickedBatter(cake.getBatterList(), hit);
        CakeTop top = PBHelpers.get(stack, PBComponentTypes.TOP.get());
        if (stack.is(PBItems.FROSTING_BOTTLE.get()) && clickedBatter.getTop().orElse(null) != top) {
            if (player instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
                serverPlayer.awardStat(Stats.ITEM_USED.get(item));
            }

            clickedBatter.withTop(top);
            player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
            if (!world.isClientSide()) {
                PBHelpers.update(cake, (ServerLevel) world);
            }

            return InteractionResult.SUCCESS;
        }

        if (stack.is(Items.HONEYCOMB) && clickedBatter.setWaxed(true)) {
            if (player instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
                serverPlayer.awardStat(Stats.ITEM_USED.get(item));
            }

            if (!player.isCreative()) {
                stack.shrink(1);
            }
            world.levelEvent(player, LevelEvent.PARTICLES_AND_SOUND_WAX_ON, pos, 0);
            if (!world.isClientSide()) {
                PBHelpers.update(cake, (ServerLevel) world);
            }

            return InteractionResult.SUCCESS;
        }

        if (stack.is(Items.MILK_BUCKET) && !clickedBatter.isWaxed()) {
            if (player instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
                serverPlayer.awardStat(Stats.ITEM_USED.get(item));
            }
            player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.BUCKET)));

            clickedBatter.withTop(null);
            clickedBatter.withFeatures(Map.of());

            world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            world.playSound(player, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
            if (!world.isClientSide()) {
                PBHelpers.update(cake, (ServerLevel) world);
            }

            return InteractionResult.SUCCESS;
        }

        List<CakeFeature> features = PBHelpers.getOrDefault(stack, PBComponentTypes.FEATURES.get(), PedrosBakery.ITEM_TO_FEATURES.getOrDefault(stack.getItem(), List.of()));
        boolean appliedFeature = false;
        for (CakeFeature feature : features) {
            if (feature.canBeApplied(player, stack, clickedBatter, world, pos, state, cake)) {
                clickedBatter.withFeature(feature);
                feature.onPlaced(player, stack, clickedBatter, world, pos, state, cake);
                appliedFeature = true;
            }
        }
        if (appliedFeature) {
            if (!player.isCreative()) {
                stack.shrink(1);
            }
            if (!world.isClientSide()) {
                PBHelpers.update(cake, (ServerLevel) world);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private static CakeBatter<FullBatterSizeContainer> getClickedBatter(List<CakeBatter<FullBatterSizeContainer>> batterList, BlockHitResult hit) {
        float currentHeight = 0;
        int layerIndex = -1;
        for (int i = 0; i < batterList.size(); i++) {
            currentHeight += batterList.get(i).getSizeContainer().getHeight();
            if (hit.getDirection() == Direction.DOWN ? currentHeight / 16.0f > hit.getLocation().subtract(hit.getBlockPos().getX(), hit.getBlockPos().getY(), hit.getBlockPos().getZ()).y : currentHeight / 16.0f >= hit.getLocation().subtract(hit.getBlockPos().getX(), hit.getBlockPos().getY(), hit.getBlockPos().getZ()).y) {
                layerIndex = i;
                break;
            }
        }
        if (layerIndex == -1) {
            layerIndex = batterList.size() - 1;
        }
        return batterList.get(layerIndex);
    }

    public static ItemStack of(List<CakeBatter<FullBatterSizeContainer>> batterList) {
        ItemStack stack = new ItemStack(PBBlocks.CAKE.get());
        PBHelpers.set(stack, PBComponentTypes.BATTER_LIST.get(), batterList.stream().map(CakeBatter::copy).collect(Collectors.toCollection(Lists::newArrayList)));
        return stack;
    }

    public static boolean tryAddBatter(PBCakeBlockEntity cake, List<CakeBatter<FullBatterSizeContainer>> batterList) {
        if (batterList.isEmpty()) {
            return true;
        }

        if (batterList.get(0).getSizeContainer().getSize() / 2.0f - batterList.get(0).getSizeContainer().getBites() <= cake.getBatterList().get(cake.getBatterList().size() - 1).getSizeContainer().getSize() / 2.0f - cake.getBatterList().get(cake.getBatterList().size() - 1).getSizeContainer().getBites()) {
            float batterListHeight = (float) batterList.stream().mapToDouble((batter) -> batter.getSizeContainer().getHeight()).sum();
            return cake.getHeight() + batterListHeight <= PedrosBakery.CONFIG.maxCakeHeight.get() && (!cake.hasLevel() || cake.getLevel().isUnobstructed(null, PBCakeBlockEntity.toShape(batterList, cake.getBlockState(), cake.getLevel(), cake.getBlockPos()).move(cake.getBlockPos().getX(), cake.getBlockPos().getY() + cake.getHeight() / 16.0f, cake.getBlockPos().getZ()))) && cake.getBatterList().addAll(batterList);
        }

        return false;
    }

    private static void changeState(@Nullable Entity entity, Level world, BlockPos pos, BlockState candleState) {
        BlockState oldState = world.getBlockState(pos);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        world.setBlockAndUpdate(pos, candleState);
        if (blockEntity instanceof PBCakeBlockEntity cake) {
            cake.setBlockState(candleState);
            world.setBlockEntity(cake);
        }
        world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(entity, oldState));
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
        if (world.getBlockEntity(pos) instanceof PBCakeBlockEntity cake) {
            return of(cake.getBatterList());
        }
        return of(Collections.singletonList(CakeBatter.getFullSizeDefault()));
    }

    @Override
    public void removePartsWhenReplaced(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        for (BlockPos partPos : this.getParts(world, pos)) {
            if (!(world.getBlockState(partPos).getBlock() instanceof MultipartBlockPart<?, ?>) || !world.getBlockState(partPos).hasProperty(MultipartBlockPart.DELEGATE)) {
                return;
            }
            world.setBlockAndUpdate(partPos, world.getBlockState(partPos).setValue(MultipartBlockPart.DELEGATE, false));
            if (moved) {
                world.removeBlock(partPos, true);
            } else if (newState.is(PBTags.Blocks.CAKES)) {
                world.removeBlock(partPos, false);
            } else {
                world.destroyBlock(partPos, false);
            }
        }
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(BlockStateProperties.HORIZONTAL_FACING, rotation.rotate(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);
        if (world.getBlockEntity(pos) instanceof PBCakeBlockEntity cake) {
            cake.readFrom(stack);
        }
    }
}

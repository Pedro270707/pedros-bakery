package net.pedroricardo.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
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
        this.setDefaultState(this.getStateManager().getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.combineAndSimplify(this.getFullShape(state, world, pos, context), VoxelShapes.fullCube(), BooleanBiFunction.AND);
    }

    @Override
    public VoxelShape getFullShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (!(world.getBlockEntity(pos) instanceof PBCakeBlockEntity cake)) {
            return Blocks.CAKE.getDefaultState().getOutlineShape(world, pos, context);
        }
        return cake.toShape();
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

    getShape

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
        return createTickerHelper(type, PBBlockEntities.CAKE, PBCakeBlockEntity::tick);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult useWithItem = this.onUseWithItem(player.getItemInHand(hand), state, world, pos, player, hand, hit);
        if (useWithItem != InteractionResult.PASS) return useWithItem;
        if (world.isClientSide()) {
            if (tryUsing(world, pos, state, player, hit).isAccepted()) {
                return InteractionResult.SUCCESS;
            }
            if (player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
                return InteractionResult.CONSUME;
            }
        }
        return tryUsing(world, pos, state, player, hit);
    }

    protected static ActionResult tryUsing(World world, BlockPos pos, BlockState state, PlayerEntity player, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof PBCakeBlockEntity cake) || cake.getBatterList().isEmpty()) return ActionResult.PASS;
        float currentHeight = 0;
        int layerIndex = -1;
        for (int i = 0; i < cake.getBatterList().size(); i++) {
            currentHeight += cake.getBatterList().get(i).getSizeContainer().getHeight();
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
            List<CakeBatter<FullBatterSizeContainer>> batterList = Lists.newArrayList();
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
            return ActionResult.SUCCESS;
        }
        if (!player.canConsume(false)) {
            return ActionResult.PASS;
        }
        float biteSize = player.getUuidAsString().equals("7bb71eb9-b55e-4071-9175-8ec2f42ddd79") ? Math.min(0.125f, PedrosBakery.CONFIG.biteSize.get()) : PedrosBakery.CONFIG.biteSize.get();
        if (!player.isCreative() && cake.getBatterList().size() > layerIndex + 1 && cake.getBatterList().get(layerIndex + 1).getSizeContainer().getSize() / 2.0f - cake.getBatterList().get(layerIndex + 1).getSizeContainer().getBites() > cake.getBatterList().get(layerIndex).getSizeContainer().getSize() / 2.0f - cake.getBatterList().get(layerIndex).getSizeContainer().getBites() - biteSize) {
            return ActionResult.PASS;
        }
        ActionResult result = cake.getBatterList().get(layerIndex).bite(world, pos, state, player, cake, biteSize);
        if (result.isAccepted()) {
            changeState(player, world, pos, state);
        }
        if (cake.getBatterList().size() == 1 && cake.getBatterList().get(layerIndex).isEmpty()) {
            cake.removeAllParts(world);
            world.removeBlock(pos, false);
            world.emitGameEvent(player, GameEvent.BLOCK_DESTROY, pos);
        }
        return result;
    }

    private ActionResult onUseWithItem(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof PBCakeBlockEntity cake)) {
            return ActionResult.FAIL;
        }

        if (cake.getBatterList().isEmpty()) {
            return ActionResult.FAIL;
        }

        if (stack.isOf(PBBlocks.CAKE.asItem())) {
            List<CakeBatter<FullBatterSizeContainer>> batterList = PBHelpers.getOrDefault(stack, PBComponentTypes.BATTER_LIST, List.of()).stream().map(CakeBatter::copy).collect(Collectors.toCollection(Lists::newArrayList));
            if (batterList.isEmpty()) {
                return ActionResult.FAIL;
            }

            if (tryAddBatter(cake, batterList)) {
                if (!world.isClient()) {
                    PBHelpers.update(cake, (ServerWorld) world);
                }
                world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                world.playSound(pos.getX(), pos.getY(), pos.getZ(), state.getSoundGroup().getPlaceSound(), SoundCategory.BLOCKS, (state.getSoundGroup().getVolume() + 1.0f) / 2.0f, state.getSoundGroup().getPitch() * 0.8f, true);
                if (!player.isCreative()) {
                    stack.decrement(1);
                }
                return ActionResult.SUCCESS;
            }
        }

        Item item = stack.getItem();
        Block block = Block.getBlockFromItem(item);
        if (stack.isIn(ItemTags.CANDLES) && cake.getBatterList().get(cake.getBatterList().size() - 1).getSizeContainer().getBites() == 0 && block instanceof CandleBlock candleBlock) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.incrementStat(Stats.USED.getOrCreateStat(item));
            }

            if (!player.isCreative()) {
                stack.decrement(1);
            }
            world.playSound(null, pos, SoundEvents.BLOCK_CAKE_ADD_CANDLE, SoundCategory.BLOCKS, 1.0f, 1.0f);
            changeState(player, world, pos, PBCandleCakeBlock.getCandleCakeFromCandle(candleBlock).with(Properties.HORIZONTAL_FACING, state.get(Properties.HORIZONTAL_FACING)));
            world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);

            return ActionResult.SUCCESS;
        }

        CakeBatter<FullBatterSizeContainer> clickedBatter = getClickedBatter(cake.getBatterList(), hit);
        CakeTop top = PBHelpers.get(stack, PBComponentTypes.TOP);
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

            return ActionResult.SUCCESS;
        }

        if (stack.isOf(Items.HONEYCOMB) && clickedBatter.setWaxed(true)) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                Criteria.CONSUME_ITEM.trigger(serverPlayer, stack);
                serverPlayer.incrementStat(Stats.USED.getOrCreateStat(item));
            }

            if (!player.isCreative()) {
                stack.decrement(1);
            }
            world.syncWorldEvent(player, WorldEvents.BLOCK_WAXED, pos, 0);
            if (!world.isClient()) {
                PBHelpers.update(cake, (ServerWorld) world);
            }

            return ActionResult.SUCCESS;
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

            return ActionResult.SUCCESS;
        }

        List<CakeFeature> features = PBHelpers.getOrDefault(stack, PBComponentTypes.FEATURES, PedrosBakery.ITEM_TO_FEATURES.getOrDefault(stack.getItem(), List.of()));
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
                stack.decrement(1);
            }
            if (!world.isClient()) {
                PBHelpers.update(cake, (ServerWorld) world);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    private static CakeBatter<FullBatterSizeContainer> getClickedBatter(List<CakeBatter<FullBatterSizeContainer>> batterList, BlockHitResult hit) {
        float currentHeight = 0;
        int layerIndex = -1;
        for (int i = 0; i < batterList.size(); i++) {
            currentHeight += batterList.get(i).getSizeContainer().getHeight();
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

    public static ItemStack of(List<CakeBatter<FullBatterSizeContainer>> batterList) {
        ItemStack stack = new ItemStack(PBBlocks.CAKE);
        PBHelpers.set(stack, PBComponentTypes.BATTER_LIST, batterList.stream().map(CakeBatter::copy).collect(Collectors.toCollection(Lists::newArrayList)));
        return stack;
    }

    public static boolean tryAddBatter(PBCakeBlockEntity cake, List<CakeBatter<FullBatterSizeContainer>> batterList) {
        if (batterList.isEmpty()) {
            return true;
        }

        if (batterList.get(0).getSizeContainer().getSize() / 2.0f - batterList.get(0).getSizeContainer().getBites() <= cake.getBatterList().get(cake.getBatterList().size() - 1).getSizeContainer().getSize() / 2.0f - cake.getBatterList().get(cake.getBatterList().size() - 1).getSizeContainer().getBites()) {
            float batterListHeight = (float) batterList.stream().mapToDouble((batter) -> batter.getSizeContainer().getHeight()).sum();
            return cake.getHeight() + batterListHeight <= PedrosBakery.CONFIG.maxCakeHeight.get() && (!cake.hasWorld() || cake.getWorld().doesNotIntersectEntities(null, PBCakeBlockEntity.toShape(batterList, cake.getCachedState(), cake.getWorld(), cake.getPos()).offset(cake.getPos().getX(), cake.getPos().getY() + cake.getHeight() / 16.0f, cake.getPos().getZ()))) && cake.getBatterList().addAll(batterList);
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
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        if (world.getBlockEntity(pos) instanceof PBCakeBlockEntity cake) {
            return of(cake.getBatterList());
        }
        return of(Collections.singletonList(CakeBatter.getFullSizeDefault()));
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
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(Properties.HORIZONTAL_FACING, rotation.rotate(state.get(Properties.HORIZONTAL_FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(Properties.HORIZONTAL_FACING)));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onPlaced(world, pos, state, placer, stack);
        if (world.getBlockEntity(pos) instanceof PBCakeBlockEntity cake) {
            cake.readFrom(stack);
        }
    }
}

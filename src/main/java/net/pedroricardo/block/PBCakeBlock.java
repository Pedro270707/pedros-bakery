package net.pedroricardo.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
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
import net.pedroricardo.block.helpers.CakeFeature;
import net.pedroricardo.block.helpers.CakeLayer;
import net.pedroricardo.block.entity.PBBlockEntities;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import net.pedroricardo.block.helpers.CakeTop;
import net.pedroricardo.block.multipart.MultipartBlock;
import net.pedroricardo.item.PBComponentTypes;
import net.pedroricardo.item.PBItems;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class PBCakeBlock extends BlockWithEntity implements MultipartBlock {
    public static final int MAX_CAKE_HEIGHT = 256;
    public static final int BITE_SIZE = 2;
    public static final int TICKS_UNTIL_BAKED = 2000;
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
        if (!(world.getBlockEntity(pos) instanceof PBCakeBlockEntity cake) || cake.getLayers().isEmpty()) return ActionResult.PASS;
        float currentHeight = 0;
        int layerIndex = -1;
        for (int i = 0; i < cake.getLayers().size(); i++) {
            currentHeight += cake.getLayers().get(i).getHeight();
            if (hit.getSide() == Direction.DOWN ? currentHeight / 16.0f > hit.getPos().subtract(hit.getBlockPos().getX(), hit.getBlockPos().getY(), hit.getBlockPos().getZ()).y : currentHeight / 16.0f >= hit.getPos().subtract(hit.getBlockPos().getX(), hit.getBlockPos().getY(), hit.getBlockPos().getZ()).y) {
                layerIndex = i;
                break;
            }
        }
        if (layerIndex == -1) {
            layerIndex = cake.getLayers().size() - 1;
        }
        if (player.isSneaking()) {
            changeState(world, pos, state);
            List<CakeLayer> layers = Lists.newArrayList();
            while (cake.getLayers().size() > layerIndex) {
                layers.add(cake.getLayers().remove(layerIndex));
            }
            ItemStack stack = of(layers);
            player.giveItemStack(stack);
            PBHelpers.updateListeners(cake);
            return ActionResult.SUCCESS;
        }
        if (!player.canConsume(false)) {
            return ActionResult.PASS;
        }
        if (!player.isCreative() && cake.getLayers().size() > layerIndex + 1 && cake.getLayers().get(layerIndex + 1).getSize() / 2.0f - cake.getLayers().get(layerIndex + 1).getBites() >= cake.getLayers().get(layerIndex).getSize() / 2.0f - cake.getLayers().get(layerIndex).getBites()) {
            return ActionResult.PASS;
        }
        changeState(world, pos, state);
        cake.getLayers().get(layerIndex).bite(world, pos, state, player, cake);
        if (cake.getLayers().size() == 1 && cake.getLayers().get(layerIndex).isEmpty()) {
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

        if (cake.getLayers().isEmpty()) {
            return ItemActionResult.FAIL;
        }

        if (stack.isOf(PBItems.CAKE)) {
            NbtCompound stackNbt = stack.getComponents().getOrDefault(DataComponentTypes.BLOCK_ENTITY_DATA, NbtComponent.DEFAULT).copyNbt();
            NbtList stackList = stackNbt.getList("layers", NbtElement.COMPOUND_TYPE);
            if (stackList.isEmpty()) {
                return ItemActionResult.FAIL;
            }

            List<CakeLayer> cakeLayers = stackList.stream().map(element -> element.getType() == NbtElement.COMPOUND_TYPE ? CakeLayer.fromNbt((NbtCompound) element) : CakeLayer.getEmpty()).toList();
            if (tryAddLayers(cake, cakeLayers)) {
                stack.decrementUnlessCreative(1, player);
                return ItemActionResult.SUCCESS;
            }
        }

        Item item = stack.getItem();
        Block block = Block.getBlockFromItem(item);
        if (stack.isIn(ItemTags.CANDLES) && cake.getLayers().getLast().getBites() == 0 && block instanceof CandleBlock candleBlock) {
            stack.decrementUnlessCreative(1, player);
            world.playSound(null, pos, SoundEvents.BLOCK_CAKE_ADD_CANDLE, SoundCategory.BLOCKS, 1.0f, 1.0f);
            changeState(world, pos, PBCandleCakeBlock.getCandleCakeFromCandle(candleBlock).with(Properties.HORIZONTAL_FACING, state.get(Properties.HORIZONTAL_FACING)));
            world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            player.incrementStat(Stats.USED.getOrCreateStat(item));
            return ItemActionResult.SUCCESS;
        }

        CakeLayer clickedLayer = getClickedLayer(cake.getLayers(), hit);
        CakeTop top = stack.get(PBComponentTypes.TOP);
        if (stack.isOf(PBItems.FROSTING_BOTTLE) && clickedLayer.getTop().orElse(null) != top) {
            clickedLayer.setTop(top);
            PBHelpers.decrementStackAndAdd(player, stack, new ItemStack(Items.GLASS_BOTTLE));
            PBHelpers.updateListeners(cake);
            return ItemActionResult.SUCCESS;
        }

        List<CakeFeature> features = stack.getOrDefault(PBComponentTypes.FEATURES, List.of());
        boolean appliedFeature = false;
        for (CakeFeature feature : features) {
            if (feature.canBeApplied(stack, clickedLayer, world, pos, state, cake)) {
                clickedLayer.addFeature(feature);
                feature.onPlaced(stack, clickedLayer, world, pos, state, cake);
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

    private static CakeLayer getClickedLayer(List<CakeLayer> layers, BlockHitResult hit) {
        float currentHeight = 0;
        int layerIndex = -1;
        for (int i = 0; i < layers.size(); i++) {
            currentHeight += layers.get(i).getHeight();
            if (hit.getSide() == Direction.DOWN ? currentHeight / 16.0f > hit.getPos().subtract(hit.getBlockPos().getX(), hit.getBlockPos().getY(), hit.getBlockPos().getZ()).y : currentHeight / 16.0f >= hit.getPos().subtract(hit.getBlockPos().getX(), hit.getBlockPos().getY(), hit.getBlockPos().getZ()).y) {
                layerIndex = i;
                break;
            }
        }
        if (layerIndex == -1) {
            layerIndex = layers.size() - 1;
        }
        return layers.get(layerIndex);
    }

    public ItemStack of(PBCakeBlockEntity blockEntity, List<CakeLayer> layers) {
        ItemStack stack = new ItemStack(this);
        NbtList list = new NbtList();
        for (CakeLayer layer : layers) {
            list.add(layer.toNbt(new NbtCompound()));
        }
        NbtCompound compound = new NbtCompound();
        compound.put("layers", list);
        BlockEntity.writeIdToNbt(compound, blockEntity.getType());
        stack.set(DataComponentTypes.BLOCK_ENTITY_DATA, NbtComponent.of(compound));
        return stack;
    }

    public static ItemStack of(List<CakeLayer> layers) {
        ItemStack stack = new ItemStack(PBItems.CAKE);
        NbtList list = new NbtList();
        for (CakeLayer layer : layers) {
            list.add(layer.toNbt(new NbtCompound()));
        }
        NbtCompound compound = new NbtCompound();
        compound.put("layers", list);
        BlockEntity.writeIdToNbt(compound, PBBlockEntities.CAKE);
        stack.set(DataComponentTypes.BLOCK_ENTITY_DATA, NbtComponent.of(compound));
        return stack;
    }

    public static boolean tryAddLayers(PBCakeBlockEntity cake, List<CakeLayer> layers) {
        if (layers.isEmpty()) {
            return true;
        }

        if (layers.getFirst().getSize() / 2.0f - layers.getFirst().getBites() <= cake.getLayers().getLast().getSize() / 2.0f - cake.getLayers().getLast().getBites()) {
            float layersHeight = (float) layers.stream().mapToDouble(CakeLayer::getHeight).sum();
            return cake.getHeight() + layersHeight <= MAX_CAKE_HEIGHT && cake.getLayers().addAll(layers);
        }

        return false;
    }

    private static void changeState(World world, BlockPos pos, BlockState candleState) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        world.setBlockState(pos, candleState);
        if (blockEntity instanceof PBCakeBlockEntity cake) {
            cake.setCachedState(candleState);
            world.addBlockEntity(cake);
        }
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        if (world.getBlockEntity(pos) instanceof PBCakeBlockEntity cake) {
            return this.of(cake, cake.getLayers());
        }
        return of(Collections.singletonList(CakeLayer.getDefault()));
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        removePartsWhenReplaced(state, world, pos, newState, moved);
        super.onStateReplaced(state, world, pos, newState, moved);
        world.updateListeners(pos, state, newState, Block.NOTIFY_ALL_AND_REDRAW);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }
}

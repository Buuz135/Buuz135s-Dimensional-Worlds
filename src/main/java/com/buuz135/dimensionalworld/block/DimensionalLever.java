package com.buuz135.dimensionalworld.block;

import com.buuz135.dimensionalworld.DimensionalWorld;
import com.buuz135.dimensionalworld.block.tile.DimensionalLeverTile;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;

import java.util.Optional;
import java.util.function.Function;

import static net.minecraft.state.properties.BlockStateProperties.*;

public class DimensionalLever extends Block implements ITileEntityProvider {

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    protected static final VoxelShape LEVER_NORTH_AABB = Block.makeCuboidShape(5.0D, 4.0D, 10.0D, 11.0D, 12.0D, 16.0D);
    protected static final VoxelShape LEVER_SOUTH_AABB = Block.makeCuboidShape(5.0D, 4.0D, 0.0D, 11.0D, 12.0D, 6.0D);
    protected static final VoxelShape LEVER_WEST_AABB = Block.makeCuboidShape(10.0D, 4.0D, 5.0D, 16.0D, 12.0D, 11.0D);
    protected static final VoxelShape LEVER_EAST_AABB = Block.makeCuboidShape(0.0D, 4.0D, 5.0D, 6.0D, 12.0D, 11.0D);
    protected static final VoxelShape FLOOR_Z_SHAPE = Block.makeCuboidShape(5.0D, 0.0D, 4.0D, 11.0D, 6.0D, 12.0D);
    protected static final VoxelShape FLOOR_X_SHAPE = Block.makeCuboidShape(4.0D, 0.0D, 5.0D, 12.0D, 6.0D, 11.0D);
    protected static final VoxelShape CEILING_Z_SHAPE = Block.makeCuboidShape(5.0D, 10.0D, 4.0D, 11.0D, 16.0D, 12.0D);
    protected static final VoxelShape CEILING_X_SHAPE = Block.makeCuboidShape(4.0D, 10.0D, 5.0D, 12.0D, 16.0D, 11.0D);

    private final ResourceLocation dimension;

    public DimensionalLever(ResourceLocation dimension) {
        super(Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.5F).sound(SoundType.WOOD));
        this.setDefaultState(this.stateContainer.getBaseState().with(HORIZONTAL_FACING, Direction.NORTH).with(POWERED, Boolean.valueOf(false)).with(FACE, AttachFace.WALL));
        this.dimension = dimension;
        this.setRegistryName(DimensionalWorld.MODID, dimension.getPath() + "_lever");
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult p_225533_6_) {
        if (worldIn.isRemote) {
            return ActionResultType.SUCCESS;
        } else {
            BlockState blockState = cycle(state, worldIn, pos);
            float f = blockState.get(POWERED) ? 0.6F : 0.5F;
            worldIn.playSound((PlayerEntity)null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, f);
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof DimensionalLeverTile){
                Optional<DimensionType> type = Registry.DIMENSION_TYPE.getValue(((DimensionalLeverTile) tileEntity).getDestinationType() != null ? ((DimensionalLeverTile) tileEntity).getDestinationType() : getDestination(worldIn));
                BlockPos destination = ((DimensionalLeverTile) tileEntity).getDestination();
                if (player != null && type.isPresent()){
                    ServerWorld destinationWorld = DimensionManager.getWorld(player.getServer(), type.get(), true, true);
                    if (destination == null || destinationWorld.getBlockState(destination).getBlock() != this){
                        destination = new BlockPos(pos.getX(), destinationWorld.getChunk(pos).getTopBlockY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()) + 1, pos.getZ());
                        destinationWorld.setBlockState(destination, this.getDefaultState().with(FACE, AttachFace.FLOOR));
                        TileEntity destinationTile = destinationWorld.getTileEntity(destination);
                        if (destinationTile instanceof DimensionalLeverTile){
                            ((DimensionalLeverTile) destinationTile).setDestination(pos);
                            ((DimensionalLeverTile) destinationTile).setDestinationType(worldIn.getDimension().getType().getRegistryName());
                        }
                        ((DimensionalLeverTile) tileEntity).setDestination(destination);
                    }
                    BlockPos finalDestination = destination;
                    player.changeDimension(type.get(), new ITeleporter() {
                        @Override
                        public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                            Entity repositionedEntity = repositionEntity.apply(false);
                            repositionedEntity.setPositionAndUpdate(finalDestination.getX() + 0.5, finalDestination.getY()+ 0.5, finalDestination.getZ()+ 0.5);
                            return repositionedEntity;
                        }
                    });
                }
            }
            return ActionResultType.SUCCESS;
        }
    }

    private ResourceLocation getDestination(World world){
        if (world.getDimension().getType().getRegistryName().equals(dimension)){
            return DimensionType.OVERWORLD.getRegistryName();
        }
        return dimension;
    }

    public BlockState cycle(BlockState p_226939_1_, World p_226939_2_, BlockPos p_226939_3_) {
        p_226939_1_ = p_226939_1_.cycle(POWERED);
        p_226939_2_.setBlockState(p_226939_3_, p_226939_1_, 3);
        return p_226939_1_;
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACE, HORIZONTAL_FACING, POWERED);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new DimensionalLeverTile();
    }


    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch ((AttachFace) state.get(FACE)) {
            case FLOOR:
                switch (state.get(HORIZONTAL_FACING).getAxis()) {
                    case X:
                        return FLOOR_X_SHAPE;
                    case Z:
                    default:
                        return FLOOR_Z_SHAPE;
                }
            case WALL:
                switch ((Direction) state.get(HORIZONTAL_FACING)) {
                    case EAST:
                        return LEVER_EAST_AABB;
                    case WEST:
                        return LEVER_WEST_AABB;
                    case SOUTH:
                        return LEVER_SOUTH_AABB;
                    case NORTH:
                    default:
                        return LEVER_NORTH_AABB;
                }
            case CEILING:
            default:
                switch (state.get(HORIZONTAL_FACING).getAxis()) {
                    case X:
                        return CEILING_X_SHAPE;
                    case Z:
                    default:
                        return CEILING_Z_SHAPE;
                }

        }
    }

    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        for(Direction direction : context.getNearestLookingDirections()) {
            BlockState blockstate;
            if (direction.getAxis() == Direction.Axis.Y) {
                blockstate = this.getDefaultState().with(FACE, direction == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR).with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing());
            } else {
                blockstate = this.getDefaultState().with(FACE, AttachFace.WALL).with(HORIZONTAL_FACING, direction.getOpposite());
            }

            if (blockstate.isValidPosition(context.getWorld(), context.getPos())) {
                return blockstate;
            }
        }

        return null;
    }
}
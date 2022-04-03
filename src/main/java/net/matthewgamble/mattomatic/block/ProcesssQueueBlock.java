package net.matthewgamble.mattomatic.block;

import net.matthewgamble.mattomatic.tileentity.ModTileEntities;
import net.matthewgamble.mattomatic.tileentity.ProcessQueueTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class ProcesssQueueBlock extends HorizontalBlock implements ITileEntityProvider
{
    public static final EnumProperty<Fullness> FULLNESS = BlockStateProperties.FULLNESS;

    public ProcesssQueueBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(FULLNESS, Fullness.EMPTY));
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader world)
    {
        return ModTileEntities.PROCESS_QUEUE_TILE.get().create();
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return ModTileEntities.PROCESS_QUEUE_TILE.get().create();
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> stateBuilder)
    {
        stateBuilder.add(FACING, FULLNESS);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean hasAnalogOutputSignal(BlockState state)
    {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getAnalogOutputSignal(BlockState state, World worldIn, BlockPos pos)
    {
        TileEntity tileEntity = worldIn.getBlockEntity(pos);
        if (!(tileEntity instanceof ProcessQueueTile)) {
            return 0;
        }

        ProcessQueueTile queueTile = (ProcessQueueTile) tileEntity;
        float queueLengthPercent = queueTile.getQueueLengthPercent();
        return MathHelper.floor(queueLengthPercent * 15.0F);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        if (world.isClientSide || player.isCrouching()) {
            return ActionResultType.SUCCESS;
        }

        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof ProcessQueueTile) {
            NetworkHooks.openGui((ServerPlayerEntity) player, (ProcessQueueTile) tileEntity, pos);
        }

        return ActionResultType.CONSUME;
    }

    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof ProcessQueueTile) {
                ProcessQueueTile processQueueTile = (ProcessQueueTile) tileEntity;
                processQueueTile.dropAllContents(world, pos);
            }
        }

        super.onRemove(state, world, pos, newState, isMoving);
    }
}

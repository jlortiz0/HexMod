package at.petrak.hexcasting.fabric.interop.computercraft;

import at.petrak.hexcasting.common.lib.HexItems;
import dan200.computercraft.shared.peripheral.diskdrive.DiskDriveState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class BlockFocusHolder extends Block implements EntityBlock {
    public static final BooleanProperty FULL = BlockStateProperties.OCCUPIED;
    public BlockFocusHolder(Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue(FULL, false));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BlockEntityFocusHolder(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BlockEntityFocusHolder be2) {
                Containers.dropContents(level, pos, NonNullList.of(be2.getItem()));
            }
            super.onRemove(state, level, pos, newState, moved);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BlockEntityFocusHolder be2) {
                if (!be2.getItem().isEmpty()) {
                    if (!player.addItem(be2.getItem()))
                        player.drop(be2.getItem(), false);
                    be2.setItem(ItemStack.EMPTY);
                    level.setBlock(pos, state.setValue(FULL, false), 2);
                } else {
                    if (player.getItemInHand(hand).is(HexItems.FOCUS)) {
                        be2.setItem(player.getItemInHand(hand));
                        player.setItemInHand(hand, ItemStack.EMPTY);
                        level.setBlock(pos, state.setValue(FULL, true), 2);
                    } else {
                        return InteractionResult.PASS;
                    }
                }
            } else {
                return InteractionResult.FAIL;
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(FULL);
    }
}

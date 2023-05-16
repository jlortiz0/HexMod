package at.petrak.hexcasting.fabric.interop.computercraft;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockEntityFocusHolder extends BlockEntity implements IPeripheralTile {
    private final SimpleContainer itemHandler = new SimpleContainer(1);
    private FocusHolderPeripheral peripheral;
    public BlockEntityFocusHolder(BlockPos $$1, BlockState $$2) {
        super(ComputerCraftInterop.FOCUS_HOLDER_ENTITY, $$1, $$2);
    }

    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.fromTag((ListTag) tag.get("inventory"));
    }

    public void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.createTag());
        super.saveAdditional(tag);
    }

    public ItemStack getItem() {
        return itemHandler.getItem(0);
    }

    public void setItem(ItemStack is) {
        itemHandler.setItem(0, is);
    }

    @Nullable
    @Override
    public IPeripheral getPeripheral(@NotNull Direction direction) {
        if (peripheral == null) peripheral = new FocusHolderPeripheral(this);
        return peripheral;
    }
}

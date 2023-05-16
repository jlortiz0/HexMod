package at.petrak.hexcasting.fabric.interop.computercraft;

import at.petrak.hexcasting.common.lib.HexBlocks;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.interop.HexInterop;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import static at.petrak.hexcasting.api.HexAPI.modLoc;

public class ComputerCraftInterop {
    public static final BlockFocusHolder FOCUS_HOLDER = Registry.register(Registry.BLOCK, modLoc("focus_holder"),
            new BlockFocusHolder(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PURPLE).sound(SoundType.DEEPSLATE_TILES)));
    public static final BlockItem FOCUS_HOLDER_ITEM = Registry.register(Registry.ITEM, modLoc("focus_holder"),
            new BlockItem(FOCUS_HOLDER, HexItems.props()));
    public static final BlockEntityType<BlockEntityFocusHolder> FOCUS_HOLDER_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, modLoc("focus_holder"),
            FabricBlockEntityTypeBuilder.create(BlockEntityFocusHolder::new, FOCUS_HOLDER).build());

    public static void init() {}

    public static boolean isActive() {
        return IXplatAbstractions.INSTANCE.isModPresent(HexInterop.Fabric.CC_ID);
    }
}

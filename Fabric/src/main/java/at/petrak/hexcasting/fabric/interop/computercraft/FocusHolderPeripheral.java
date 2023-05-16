package at.petrak.hexcasting.fabric.interop.computercraft;

import at.petrak.hexcasting.api.casting.iota.*;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.item.IotaHolderItem;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FocusHolderPeripheral implements IPeripheral {
    private final BlockEntityFocusHolder be;
    public FocusHolderPeripheral(BlockEntityFocusHolder be) {
        this.be = be;
    }

    @NotNull
    @Override
    public String getType() {
        return "focus";
    }

    @Nullable
    @Override
    public Object getTarget() {
        return be;
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        return this == other || other instanceof FocusHolderPeripheral fh && fh.be == be;
    }

    @LuaFunction
    public final boolean isWritable() {
        if (be.getItem().getItem() instanceof IotaHolderItem focus) {
            return focus.canWrite(be.getItem(), null);
        }
        return false;
    }

    @LuaFunction
    public final boolean isEmpty() {
        if (be.getItem().getItem() instanceof IotaHolderItem focus) {
            return focus.readIotaTag(be.getItem()) == null;
        }
        return true;
    }

    @LuaFunction(mainThread = true)
    public final void writePattern(String s) throws LuaException {
        if (!isWritable()) {
            throw new LuaException("No writable focus in holder");
        }
        if (s.length() < 1) {
            throw new LuaException("Cannot write an empty pattern");
        }
        ListIota ls;
        Iota i = ((IotaHolderItem) be.getItem().getItem()).readIota(be.getItem(), (ServerLevel) be.getLevel());
        if (i == null) {
            ls = null;
        } else if (i.getType() != HexIotaTypes.LIST) {
            throw new LuaException("Focus does not contain a list of patterns");
        } else {
            ls = (ListIota) i;
        }
        PatternIota pat;
        HexDir dir = switch (s.charAt(0)) {
            case 'w' -> HexDir.EAST;
            case 's' -> HexDir.WEST;
            case 'd' -> HexDir.SOUTH_WEST;
            case 'e' -> HexDir.SOUTH_EAST;
            case 'a' -> HexDir.NORTH_WEST;
            case 'q' -> HexDir.NORTH_EAST;
            default -> throw new LuaException("Invalid starting direction");
        };
        try {
            pat = new PatternIota(HexPattern.fromAngles(s.substring(1), dir));
        } catch (IllegalStateException e) {
            Throwable e2 = e;
            if (e.getCause() != null) e2 = e.getCause();
            throw new LuaException(e2.getMessage());
        }
        if (ls == null) {
            ls = new ListIota(Collections.singletonList(pat));
        } else {
            List<Iota> ls2 = new ArrayList<>();
            for (Iota i2 : ls.getList()) {
                ls2.add(i2);
            }
            ls2.add(pat);
            ls = new ListIota(ls2);
        }
        ((IotaHolderItem) be.getItem().getItem()).writeDatum(be.getItem(), ls);
    }

    @LuaFunction
    public final void clear() {
        if (be.getItem().getItem() instanceof IotaHolderItem focus) {
            focus.writeDatum(be.getItem(), null);
        }
    }

    private static Object convertIota(@NotNull Iota i) {
        IotaType<?> type = i.getType();
        if (type == HexIotaTypes.NULL || type == HexIotaTypes.GARBAGE) {
            return null;
        }
        if (type == HexIotaTypes.BOOLEAN) {
            return i.isTruthy();
        }
        if (type == HexIotaTypes.DOUBLE) {
            return ((DoubleIota) i).getDouble();
        }
        if (type == HexIotaTypes.VEC3) {
            Vec3 v = ((Vec3Iota) i).getVec3();
            return new Object[] { v.x, v.y, v.z };
        }
        if (type == HexIotaTypes.LIST) {
            ArrayList<Object> ls = new ArrayList<>();
            for (Iota i2 : i.subIotas()) {
                if (i2 == null) {
                    ls.add(null);
                    continue;
                }
                Object arr = convertIota(i2);
                ls.add(arr);
            }
            return new Object[] { ls.toArray() };
        }
        if (type == HexIotaTypes.PATTERN) {
            HexPattern p = ((PatternIota) i).getPattern();
            String s = String.valueOf(switch (p.component1()) {
                case NORTH_EAST -> 'q';
                case EAST -> 'w';
                case SOUTH_EAST -> 'e';
                case SOUTH_WEST -> 'd';
                case WEST -> 's';
                case NORTH_WEST -> 'a';
            });
            return s + p.anglesSignature();
        }
        if (type == HexIotaTypes.ENTITY) {
            return ((EntityIota) i).getEntity().getName().getString();
        }
        return i.display().getString();
    }

    @LuaFunction
    public final Object[] read() {
        if (be.getItem().getItem() instanceof IotaHolderItem focus) {
            Iota held = focus.readIota(be.getItem(), (ServerLevel) be.getLevel());
            if (held == null) {
                return null;
            }
            return new Object[] { convertIota(held) };
        }
        return null;
    }
}

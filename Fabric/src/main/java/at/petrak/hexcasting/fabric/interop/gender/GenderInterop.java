package at.petrak.hexcasting.fabric.interop.gender;

import at.petrak.hexcasting.interop.HexInterop;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

public class GenderInterop {
    public static void init() {
    }

    public static boolean isActive() {
        return IXplatAbstractions.INSTANCE.isModPresent(HexInterop.Fabric.GENDER_ID);
    }
}

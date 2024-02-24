package grauly.mt5.registers;

import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import grauly.mt5.entrypoints.MT5;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class ModPolymerModels {
    public static final PolymerModelData AWP = getWeaponModel("awp");
    public static final PolymerModelData DEAGLE = getWeaponModel("deagle");
    public static final PolymerModelData ENERGY_PISTOL = getWeaponModel("energy_pistol");
    public static final PolymerModelData ENERGY_RIFLE = getWeaponModel("energy_rifle");
    public static final PolymerModelData ENERGY_SNIPER = getWeaponModel("energy_sniper");
    public static final PolymerModelData GLOCK = getWeaponModel("glock");
    public static final PolymerModelData RAILGUN = getWeaponModel("railgun");
    public static final PolymerModelData ROCKET_LAUNCHER = getWeaponModel("rocket_launcher");
    public static final PolymerModelData SG = getWeaponModel("sg");
    public static final PolymerModelData VOID_RIFLE = getWeaponModel("void_rifle");

    private static PolymerModelData getWeaponModel(String modelPath) {
        return PolymerResourcePackUtils.requestModel(Items.CROSSBOW, new Identifier(MT5.MODID, "item/" + modelPath));
    }
}

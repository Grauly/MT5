package grauly.mt5.registers;

import grauly.mt5.ammotypes.BulletAmmoType;
import grauly.mt5.entrypoints.MT5;
import grauly.mt5.weapons.AmmoType;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModAmmoTypes {
    public static final AmmoType LOW_CAL_BULLETS = register(new BulletAmmoType(0.5f), "low_cal_bullet");
    public static final AmmoType MED_CAL_BULLETS = register(new BulletAmmoType(1f), "med_cal_bullet");


    private static AmmoType register(AmmoType ammoType, String id) {
        return Registry.register(ModRegistries.AMMO_TYPE_REGISTRY, new Identifier(MT5.MODID), ammoType);
    }
    public static void init() {
        //[Intentionally left blank]
    }
}

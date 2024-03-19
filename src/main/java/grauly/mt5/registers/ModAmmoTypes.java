package grauly.mt5.registers;

import grauly.mt5.ammotypes.*;
import grauly.mt5.entrypoints.MT5;
import grauly.mt5.weapons.AmmoType;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModAmmoTypes {
    public static final AmmoType LOW_CAL_BULLETS = register(new BulletAmmoType(0.5f), "low_cal_bullet");
    public static final AmmoType MED_CAL_BULLETS = register(new BulletAmmoType(1f), "med_cal_bullet");
    public static final AmmoType REFRACTION = register(new ReflectionAmmoType(3, 5, 5f), "refraction");
    public static final AmmoType CHASM_CASTER = register(new DelayedReflectionAmmoType(8, 5, 10f), "chasm_caster");
    public static final AmmoType DEMOLITION_CHARGE = register(new MiningChargeAmmoType(), "mining_demolition");
    public static final AmmoType MINIATURE_GRENADES = register(new ExplosiveAmmoType(1), "mini_explosive");

    private static AmmoType register(AmmoType ammoType, String id) {
        return Registry.register(ModRegistries.AMMO_TYPE_REGISTRY, new Identifier(MT5.MODID), ammoType);
    }
    public static void init() {
        //[Intentionally left blank]
    }
}

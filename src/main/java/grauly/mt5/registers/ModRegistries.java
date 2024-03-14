package grauly.mt5.registers;

import com.mojang.serialization.Lifecycle;
import grauly.mt5.entrypoints.MT5;
import grauly.mt5.throwables.GrenadeType;
import grauly.mt5.weapons.AmmoType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;

public class ModRegistries {
    public static final Identifier AMMO_TYPE_REGISTRY_ID = new Identifier(MT5.MODID, "ammo_type");
    public static final SimpleRegistry<AmmoType> AMMO_TYPE_REGISTRY = new SimpleRegistry<AmmoType>(RegistryKey.ofRegistry(AMMO_TYPE_REGISTRY_ID), Lifecycle.stable());
    public static final Identifier GRENADE_TYPE_REGISTRY_ID = new Identifier(MT5.MODID, "grenade_type");
    public static final Registry<GrenadeType> GRENADE_TYPE_REGISTRY = new SimpleRegistry<GrenadeType>(RegistryKey.ofRegistry(GRENADE_TYPE_REGISTRY_ID), Lifecycle.stable());


    public static void init() {
        //[Space Intentionally left blank]
    }
}

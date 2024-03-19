package grauly.mt5.registers;

import grauly.mt5.FragGrenadeType;
import grauly.mt5.entrypoints.MT5;
import grauly.mt5.throwables.GrenadeType;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModGrenadeTypes {

    public static final GrenadeType SHRAPNEL_GRENADE_TYPE = register(new FragGrenadeType(), "frag");

    protected static GrenadeType register(GrenadeType grenadeType, String id) {
        return Registry.register(ModRegistries.GRENADE_TYPE_REGISTRY, new Identifier(MT5.MODID, id), grenadeType);
    }
    public static void init() {
        //[Space Intentionally left Blank]
    }
}

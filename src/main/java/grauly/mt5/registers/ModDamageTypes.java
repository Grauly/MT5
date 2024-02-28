package grauly.mt5.registers;

import grauly.mt5.entrypoints.MT5;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModDamageTypes {
    public static final RegistryKey<DamageType> BULLET_DAMAGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(MT5.MODID, "bullet"));
    public static final RegistryKey<DamageType> TRIGONOMETRY_DAMAGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(MT5.MODID, "trigonometry"));
}

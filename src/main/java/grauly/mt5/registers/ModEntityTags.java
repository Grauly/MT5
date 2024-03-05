package grauly.mt5.registers;

import grauly.mt5.entrypoints.MT5;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModEntityTags {
    public static final TagKey<EntityType<?>> MINING_CHARGE_IMMUNE = TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(MT5.MODID, "mining_charge_immune"));
}

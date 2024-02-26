package grauly.mt5.registers;

import grauly.mt5.entrypoints.MT5;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModItemTags {
    public static final TagKey<Item> HIGH_CALIBER = TagKey.of(RegistryKeys.ITEM, new Identifier(MT5.MODID, "high_caliber"));
    public static final TagKey<Item> LOW_CALIBER = TagKey.of(RegistryKeys.ITEM, new Identifier(MT5.MODID, "low_caliber"));
    public static final TagKey<Item> HIGH_ENERGY = TagKey.of(RegistryKeys.ITEM, new Identifier(MT5.MODID,"high_energy"));
    public static final TagKey<Item> LOW_ENERGY = TagKey.of(RegistryKeys.ITEM, new Identifier(MT5.MODID, "low_energy"));
}

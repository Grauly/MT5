package grauly.mt5.registers;

import grauly.mt5.entrypoints.MT5;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModItemTags {
    public static final TagKey<Item> HIGH_CALIBER = tag( "high_caliber");
    public static final TagKey<Item> LOW_CALIBER = tag( "low_caliber");
    public static final TagKey<Item> PHYSICAL = tag("physical");
    public static final TagKey<Item> HIGH_ENERGY = tag("high_energy");
    public static final TagKey<Item> LOW_ENERGY = tag( "low_energy");
    public static final TagKey<Item> ENERGY = tag("energy");

    private static TagKey<Item> tag(String path) {
        return TagKey.of(RegistryKeys.ITEM, new Identifier(MT5.MODID,path));
    }
}

package grauly.mt5.registers;

import grauly.mt5.entrypoints.MT5;
import grauly.mt5.items.TestingItem;
import grauly.mt5.items.WeaponItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModItems {

    public static final Item TESTING_WAND = register(new TestingItem(new FabricItemSettings().rarity(Rarity.EPIC).fireproof()), "testing_item");
    public static final Item TESTING_RIFLE = register(new WeaponItem(new FabricItemSettings().rarity(Rarity.COMMON), 1, 35), "testing_rifle");


    private static Item register(Item item, String id) {
        return Registry.register(Registries.ITEM, new Identifier(MT5.MODID, id), item);
    }

    public static void registerItems() {
        //premium java bullshit, this will be empty, till I actually need to do something here, but I do need to interact with this class for it to load
    }
}

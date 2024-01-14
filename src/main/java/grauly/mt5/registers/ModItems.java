package grauly.mt5.registers;

import grauly.mt5.entrypoints.MT5;
import grauly.mt5.items.TestingItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModItems {

    public static final Item TESTING_WAND = register(new TestingItem(new FabricItemSettings().rarity(Rarity.EPIC).fireproof()), "testing_item");


    private static Item register(Item item, String id, ItemGroup group) {
        var registeredItem =  Registry.register(Registries.ITEM, new Identifier(MT5.MODID,id), item);
        var itemGroup = Registries.ITEM_GROUP.getKey(group);
        if(itemGroup.isPresent()) {
            ItemGroupEvents.modifyEntriesEvent(itemGroup.get()).register(content -> {
                content.add(registeredItem);
            });
        }
        return registeredItem;
    }
    private static Item register(Item item, String id) {
        return register(item,id,ModItemGroups.MAIN_GROUP);
    }

    public static void registerItems() {
        //premium java bullshit, this will be empty, till I actually need to do something here, but I do need to interact with this class for it to load
    }
}

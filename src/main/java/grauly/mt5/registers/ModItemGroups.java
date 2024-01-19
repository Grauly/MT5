package grauly.mt5.registers;

import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import grauly.mt5.entrypoints.MT5;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup MAIN_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModItems.TESTING_WAND))
            .displayName(Text.literal("MT5 Main"))
            .entries((displayContext, entries) -> {})
            .build();

    public static void registerItemGroups() {
        PolymerItemGroupUtils.registerPolymerItemGroup(new Identifier(MT5.MODID, "mt5_main"), MAIN_GROUP);
    }
}

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
            .displayName(Text.translatable("itemgroup.mt5.main"))
            .entries((displayContext, entries) -> {
                entries.add(ModItems.TESTING_RIFLE);
                entries.add(ModItems.TESTING_PISTOL);
                entries.add(ModItems.TESTING_CHARGE_RIFLE);
                entries.add(ModItems.TESTING_WAND);
                entries.add(ModItems.BULLET_AMMO);
                entries.add(ModItems.EXPLOSION_AMMO);
            })
            .build();
    public static final ItemGroup WEAPON_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModItems.TESTING_RIFLE))
            .displayName(Text.translatable("itemgroup.mt5.weapons"))
            .entries((displayContext, entries) -> {

            })
            .build();
    public static final ItemGroup AMMO_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModItems.BULLET_AMMO))
            .displayName(Text.translatable("itemgroup.mt5.ammo"))
            .entries((displayContext, entries) -> {
                entries.add(ModItems.BULLET_AMMO);
                entries.add(ModItems.EXPLOSION_AMMO);
            })
            .build();

    public static void registerItemGroups() {
        PolymerItemGroupUtils.registerPolymerItemGroup(new Identifier(MT5.MODID, "main"), MAIN_GROUP);
        PolymerItemGroupUtils.registerPolymerItemGroup(new Identifier(MT5.MODID, "weapons"), WEAPON_GROUP);
        PolymerItemGroupUtils.registerPolymerItemGroup(new Identifier(MT5.MODID,"ammo"), AMMO_GROUP);
    }
}

package grauly.mt5.datagen;

import grauly.mt5.registers.ModItems;
import grauly.mt5.weapons.WeaponItem;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;

import java.util.concurrent.CompletableFuture;

import static grauly.mt5.registers.ModItemTags.*;

public class MT5ItemTagGenerator extends FabricTagProvider.ItemTagProvider {
    public MT5ItemTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(LOW_CALIBER)
                .add(ModItems.BULLET_AMMO);
        getOrCreateTagBuilder(HIGH_CALIBER)
                .add(ModItems.EXPLOSION_AMMO);

        getOrCreateTagBuilder(PHYSICAL)
                .addTag(HIGH_CALIBER)
                .addTag(LOW_CALIBER);

        getOrCreateTagBuilder(LOW_ENERGY)
                .add(ModItems.REFLECTION_AMMO);
        getOrCreateTagBuilder(HIGH_ENERGY)
                .add(ModItems.DELAYED_REFLECTION_AMMO);

        getOrCreateTagBuilder(ENERGY)
                .addTag(HIGH_ENERGY)
                .addTag(LOW_ENERGY);


        getOrCreateTagBuilder(weaponTag(ModItems.SG))
                .addTag(PHYSICAL);
        getOrCreateTagBuilder(weaponTag(ModItems.GLOCK))
                .addTag(LOW_CALIBER);
        getOrCreateTagBuilder(weaponTag(ModItems.DEAGLE))
                .addTag(LOW_CALIBER);
        getOrCreateTagBuilder(weaponTag(ModItems.AWP))
                .addTag(PHYSICAL);
        getOrCreateTagBuilder(weaponTag(ModItems.TESTING_CHARGE_RIFLE))
                .addTag(PHYSICAL)
                .addTag(ENERGY);
        getOrCreateTagBuilder(weaponTag(ModItems.TESTING_PISTOL))
                .addTag(PHYSICAL)
                .addTag(ENERGY);
    }

    private TagKey<Item> weaponTag(Item weapon) {
        return ((WeaponItem) weapon).getOrCreateAllowTag();
    }
}

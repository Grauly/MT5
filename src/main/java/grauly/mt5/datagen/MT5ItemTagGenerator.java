package grauly.mt5.datagen;

import grauly.mt5.registers.ModItemTags;
import grauly.mt5.registers.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class MT5ItemTagGenerator extends FabricTagProvider.ItemTagProvider {
    public MT5ItemTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(ModItemTags.LOW_CALIBER)
                .add(ModItems.BULLET_AMMO);
        getOrCreateTagBuilder(ModItemTags.HIGH_CALIBER)
                .add(ModItems.EXPLOSION_AMMO);

        getOrCreateTagBuilder(ModItemTags.PHYSICAL)
                .addTag(ModItemTags.HIGH_CALIBER)
                .addTag(ModItemTags.LOW_CALIBER);
    }
}

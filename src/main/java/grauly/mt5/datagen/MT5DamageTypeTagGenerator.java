package grauly.mt5.datagen;

import grauly.mt5.registers.ModDamageTypes;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.DamageTypeTags;

import java.util.concurrent.CompletableFuture;

public class MT5DamageTypeTagGenerator extends FabricTagProvider<DamageType> {
    /**
     * Constructs a new {@link FabricTagProvider} with the default computed path.
     *
     * <p>Common implementations of this class are provided.
     *
     * @param output           the {@link FabricDataOutput} instance
     * @param registriesFuture the backing registry for the tag type
     */
    public MT5DamageTypeTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.DAMAGE_TYPE, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        this.getOrCreateTagBuilder(DamageTypeTags.AVOIDS_GUARDIAN_THORNS).add(ModDamageTypes.BULLET_DAMAGE);
        this.getOrCreateTagBuilder(DamageTypeTags.BYPASSES_COOLDOWN).add(ModDamageTypes.BULLET_DAMAGE);
        this.getOrCreateTagBuilder(DamageTypeTags.IS_PROJECTILE).add(ModDamageTypes.BULLET_DAMAGE);
    }
}

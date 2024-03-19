package grauly.mt5.registers;

import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import grauly.mt5.entrypoints.MT5;
import grauly.mt5.throwables.GrenadeProjectileEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntityTypes {

    public static final EntityType<GrenadeProjectileEntity> GRENADE = registerProjectile(GrenadeProjectileEntity::new, "grenade");


    private static <T extends ProjectileEntity> EntityType<T> registerProjectile(EntityType.EntityFactory<T> entity, String id) {
        EntityType<T> type = Registry.register(Registries.ENTITY_TYPE,
                new Identifier(MT5.MODID, id),
                FabricEntityTypeBuilder.create(
                                SpawnGroup.MISC,
                                entity
                        )
                        .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
                        .trackRangeBlocks(4)
                        .trackedUpdateRate(4)
                        .build()
        );
        PolymerEntityUtils.registerType(type);
        return type;
    }

    public static void init() {
        //[Space Intentionally left blank]
    }
}

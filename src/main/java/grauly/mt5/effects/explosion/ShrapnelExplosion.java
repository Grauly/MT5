package grauly.mt5.effects.explosion;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class ShrapnelExplosion extends ParametrizedFancyExplosion {


    public ShrapnelExplosion(float explosionPower, float explosionRange, Vec3d position, Vec3d direction, ServerWorld world) {
        super(explosionPower, explosionRange, position, direction, world);
    }

    public ShrapnelExplosion(float explosionPower, float entityDamage, float explosionRange, float entityRange, float visualRange, Vec3d visualDirection, Vec3d position, Vec3d direction, ServerWorld world, Entity explosionSourceEntity) {
        super(explosionPower, entityDamage, explosionRange, entityRange, visualRange, visualDirection, position, direction, world, explosionSourceEntity);
    }

    public ShrapnelExplosion(float explosionPower, float entityDamage, float explosionRange, float entityRange, float visualRange, Vec3d visualDirection, Vec3d position, Vec3d direction, ServerWorld world, Entity explosionSourceEntity, DamageSource damageSource) {
        super(explosionPower, entityDamage, explosionRange, entityRange, visualRange, visualDirection, position, direction, world, explosionSourceEntity, damageSource);
    }

    @Override
    public void explode() {

    }

    @Override
    public void visualize() {

    }
}

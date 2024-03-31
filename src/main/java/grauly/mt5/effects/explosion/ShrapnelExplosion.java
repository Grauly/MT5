package grauly.mt5.effects.explosion;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class ShrapnelExplosion extends ParametrizedFancyExplosion {

    public ShrapnelExplosion(float power, Vec3d position, Vec3d direction, ServerWorld world, Entity source) {
        super(power, position, direction, world, source);
    }

    public ShrapnelExplosion(float power, Vec3d position, Vec3d direction, ServerWorld world, Entity source, DamageSource damageSource) {
        super(power, position, direction, world, source, damageSource);
    }

    @Override
    public void explode() {

    }

    @Override
    public void visualize() {

    }
}

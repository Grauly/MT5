package grauly.mt5.effects.explosion;

import grauly.mt5.entrypoints.MT5;
import grauly.mt5.scheduler.FancyExplosionTask;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class HEExplosion extends ParametrizedFancyExplosion {

    public HEExplosion(float power, Vec3d position, Vec3d direction, ServerWorld world, Entity source) {
        super(power, position, direction, world, source);
    }

    public HEExplosion(float power, Vec3d position, Vec3d direction, ServerWorld world, Entity source, DamageSource damageSource) {
        super(power, position, direction, world, source, damageSource);
    }

    @Override
    protected double getPowerByDistance(double distance) {
        return power / Math.pow((distance + 1), 3);
    }

    @Override
    public void explode() {
        applyEffectsToBlocks(collectDestroyedBlocks());
        applyEffectsToEntities(collectAffectedEntities(entity -> true));
    }

    @Override
    public void visualize() {
        FancyExplosion.flash(world, position, 4);
        new FancyExplosionTask(world, position, direction, world.getBlockState(BlockPos.ofFloored(position.getX(), position.getY(), position.getZ()))).startTask(MT5.TASK_SCHEDULER, 0, 1);
    }
}

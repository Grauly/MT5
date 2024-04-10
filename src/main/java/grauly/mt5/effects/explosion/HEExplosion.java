package grauly.mt5.effects.explosion;

import grauly.mt5.entrypoints.MT5;
import grauly.mt5.scheduler.FancyExplosionTask;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class HEExplosion extends ParametrizedFancyExplosion {


    public HEExplosion(float explosionPower, float explosionRange, Vec3d position, Vec3d direction, ServerWorld world) {
        super(explosionPower, explosionRange, position, direction, world);
    }

    public HEExplosion(float explosionPower, float entityDamage, float explosionRange, float entityRange, float visualRange, Vec3d visualDirection, Vec3d position, Vec3d direction, ServerWorld world, Entity explosionSourceEntity) {
        super(explosionPower, entityDamage, explosionRange, entityRange, visualRange, visualDirection, position, direction, world, explosionSourceEntity);
    }

    public HEExplosion(float explosionPower, float entityDamage, float explosionRange, float entityRange, float visualRange, Vec3d visualDirection, Vec3d position, Vec3d direction, ServerWorld world, Entity explosionSourceEntity, DamageSource damageSource) {
        super(explosionPower, entityDamage, explosionRange, entityRange, visualRange, visualDirection, position, direction, world, explosionSourceEntity, damageSource);
    }

    @Override
    protected double getPowerByDistance(double delta) {
        return explosionPower * (1 - delta);
    }

    @Override
    public void explode() {
        BlockExplosionData data = collectDestroyedBlocks();
        visualDirection = data.cloudCenter().subtract(getBlockExplosionOrigin());
        applyEffectsToBlocks(data.blocks());
        applyEffectsToEntities(collectAffectedEntities(entity -> true));
    }

    @Override
    public void visualize() {
        FancyExplosion.flash(world, position, 4);
        new FancyExplosionTask(world, position, visualDirection, world.getBlockState(BlockPos.ofFloored(position.getX(), position.getY(), position.getZ()))).startTask(MT5.TASK_SCHEDULER, 0, 1);
    }
}

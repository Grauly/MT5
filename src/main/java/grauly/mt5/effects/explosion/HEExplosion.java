package grauly.mt5.effects.explosion;

import grauly.mt5.helpers.MathHelper;
import grauly.mt5.registers.ModSchedulers;
import grauly.mt5.scheduler.FancyExplosionTask;
import grauly.mt5.scheduler.SingleRunLaterLambdaTask;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class HEExplosion extends FancyExplosion {


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
        ExplosionEffects.flash(world, position, (int) (Math.floor(visualRange) * 2));
        new SingleRunLaterLambdaTask(() -> {
            double surface = MathHelper.sphereSurface(visualRange);
            int amountOfFragments = (int) (surface / 4);
            ExplosionEffects.parametricFragments(world, position, visualDirection, amountOfFragments, 15, visualRange * 4);
            return 0;
        }).startTask(ModSchedulers.VISUALS, 0, 1);
        new FancyExplosionTask(world, position, visualDirection, world.getBlockState(BlockPos.ofFloored(position.getX(), position.getY(), position.getZ()))).startTask(ModSchedulers.VISUALS, 0, 1);
    }
}

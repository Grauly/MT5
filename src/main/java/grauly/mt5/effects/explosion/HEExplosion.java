package grauly.mt5.effects.explosion;

import grauly.mt5.entrypoints.MT5;
import grauly.mt5.scheduler.FancyExplosionTask;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Set;

public class HEExplosion extends ParametrizedFancyExplosion {

    public HEExplosion(float power, Vec3d position, Vec3d direction, ServerWorld world, Entity source) {
        super(power, position, direction, world, source);
    }

    public HEExplosion(float power, Vec3d position, Vec3d direction, ServerWorld world, Entity source, DamageSource damageSource) {
        super(power, position, direction, world, source, damageSource);
    }

    @Override
    public void explode() {
        Set<BlockPos> destroyedBlocks = collectAffectedBlocks();
        destroyedBlocks.forEach((blockPos) -> {
            world.getBlockState(blockPos).getBlock().onDestroyedByExplosion(world, blockPos, dummyExplosion);
        });
        applyEffectsToEntities(collectAffectedEntities(entity -> true));
    }

    @Override
    public void visualize() {
        FancyExplosion.flash(world, position, 4);
        new FancyExplosionTask(world, position, direction, world.getBlockState(BlockPos.ofFloored(position.getX(),position.getY(),position.getZ()))).startTask(MT5.TASK_SCHEDULER,0,1);
    }
}
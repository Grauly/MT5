package grauly.mt5.effects.explosion;

import grauly.mt5.entrypoints.MT5;
import grauly.mt5.helpers.ParticleHelper;
import grauly.mt5.scheduler.FancyExplosionTask;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

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
            BlockState state = world.getBlockState(blockPos);
            for (int i = 0; i < 15; i++) {
                Vec3d pos = blockPos.toCenterPos().add(ThreadLocalRandom.current().nextFloat(-0.5f, 0.5f),ThreadLocalRandom.current().nextFloat(-0.5f,0.5f),ThreadLocalRandom.current().nextFloat(-0.5f,0.5f));
                ParticleHelper.spawnParticle(world, new BlockStateParticleEffect(ParticleTypes.BLOCK, state), pos,0,new Vec3d(0,0,0), 0.1f);
            }
            world.playSound(null, blockPos, state.getSoundGroup().getBreakSound(), SoundCategory.BLOCKS);
            state.onExploded(world, blockPos, dummyExplosion, (stack, pos) -> {});
        });
        applyEffectsToEntities(collectAffectedEntities(entity -> true));
    }

    @Override
    public void visualize() {
        FancyExplosion.flash(world, position, 4);
        new FancyExplosionTask(world, position, direction, world.getBlockState(BlockPos.ofFloored(position.getX(),position.getY(),position.getZ()))).startTask(MT5.TASK_SCHEDULER,0,1);
    }
}

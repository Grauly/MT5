package grauly.mt5.ammotypes;

import grauly.mt5.entrypoints.MT5;
import grauly.mt5.helpers.ParticleHelper;
import grauly.mt5.registers.ModSchedulers;
import grauly.mt5.scheduler.ReflectionTask;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class DelayedReflectionAmmoType extends ReflectionAmmoType {
    public DelayedReflectionAmmoType(int maxReflections, int basePierces, float baseDamage) {
        super(maxReflections, basePierces, baseDamage);
    }

    @Override
    public void doFireAction(LivingEntity shooter, ServerWorld world, Vec3d firingLocation, Vec3d direction) {
        ReflectionTask reflection = new ReflectionTask(0, maxReflections, 5, firingLocation, direction, shooter, baseDamage, this, world);
        reflection.startTask(ModSchedulers.MAIN, 0, 1);
    }

    @Override
    public void doTrailAction(ServerWorld world, Vec3d position, Vec3d trailDirection) {
        ParticleHelper.spawnParticle(world, ParticleTypes.SCULK_SOUL, position, 0, trailDirection, 0.1f, true);
    }

    @Override
    public float getMunitionSize() {
        return 3;
    }

    @Override
    public Identifier getIdentifier() {
        return new Identifier(MT5.MODID, "delayed_reflection");
    }
}

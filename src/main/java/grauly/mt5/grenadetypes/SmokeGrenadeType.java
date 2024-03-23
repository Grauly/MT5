package grauly.mt5.grenadetypes;

import grauly.mt5.helpers.ParticleHelper;
import grauly.mt5.throwables.GrenadeType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class SmokeGrenadeType implements GrenadeType {
    public static final int SMOKE_PER_BLOCK = 16;
    private final float smokeRange;

    public SmokeGrenadeType(float smokeRange) {
        this.smokeRange = smokeRange;
    }

    @Override
    public int getFuseTimeTicks() {
        return 35;
    }

    @Override
    public boolean explodeOnImpact() {
        return false;
    }

    @Override
    public void explode(ServerWorld world, Vec3d position, ServerPlayerEntity thrower) {
        double particleCount = ((4f / 3f) * Math.PI * Math.pow(smokeRange, 3));
        for (int i = 0; i < particleCount * SMOKE_PER_BLOCK; i++) {
            Vec3d deltaVector = new Vec3d(
                    ThreadLocalRandom.current().nextDouble(-1, 1),
                    ThreadLocalRandom.current().nextDouble(-1, 1),
                    ThreadLocalRandom.current().nextDouble(-1, 1)
            ).normalize().multiply(ThreadLocalRandom.current().nextDouble(0, smokeRange));
            ParticleHelper.spawnParticle(world,
                    ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
                    position.add(deltaVector),
                    0,
                    new Vec3d(0, 0, 0),
                    0,
                    true);
        }
    }
}

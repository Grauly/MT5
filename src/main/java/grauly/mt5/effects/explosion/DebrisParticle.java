package grauly.mt5.effects.explosion;

import grauly.mt5.helpers.ParticleHelper;
import net.minecraft.block.BlockState;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class DebrisParticle extends HeatedParticle {

    protected BlockState debrisState;

    public DebrisParticle(ServerWorld world, Vec3d initialPosition, Vec3d initialVelocity, float slowdownFactor, float initialTemperature, float cooling) {
        super(world, initialPosition, initialVelocity, slowdownFactor, initialTemperature, cooling);
    }

    public DebrisParticle(ServerWorld world, Vec3d initialPosition, Vec3d initialVelocity, float slowdownFactor, float initialTemperature, float cooling, int multiRuns) {
        super(world, initialPosition, initialVelocity, slowdownFactor, initialTemperature, cooling, multiRuns);
    }

    public DebrisParticle(ServerWorld world, Vec3d initialPosition, Vec3d initialVelocity, float slowdownFactor, float initialTemperature, float cooling, float variance) {
        super(world, initialPosition, initialVelocity, slowdownFactor, initialTemperature, cooling, variance);
    }

    public DebrisParticle(ServerWorld world, Vec3d initialPosition, Vec3d initialVelocity, float slowdownFactor, float initialTemperature, float cooling, float variance, int multiRuns) {
        super(world, initialPosition, initialVelocity, slowdownFactor, initialTemperature, cooling, variance, multiRuns);
    }

    public BlockState getDebrisState() {
        return debrisState;
    }

    public void setDebrisState(BlockState debrisState) {
        this.debrisState = debrisState;
    }

    @Override
    protected void visualize() {
        if(hitGround) return;
        ParticleHelper.spawnParticle(world,
                new BlockStateParticleEffect(ParticleTypes.BLOCK, debrisState),
                position,
                3,
                new Vec3d(0.5, 0.5, 0.5),
                0.1f);
    }
}

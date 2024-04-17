package grauly.mt5.effects.explosion.particle;

import grauly.mt5.helpers.ParticleHelper;
import net.minecraft.block.BlockState;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class DebrisParticle extends SimulatedParticle {

    protected BlockState debrisState;

    public DebrisParticle(ServerWorld world, Vec3d position, Vec3d velocity, float drag, BlockState debrisState) {
        super(world, GRAVITY, position, velocity, drag);
        this.debrisState = debrisState;
    }

    public DebrisParticle(ServerWorld world, Vec3d position, Vec3d velocity, float drag, BlockState debrisState, int multiRuns) {
        super(world, GRAVITY, position, velocity, drag, multiRuns);
        this.debrisState = debrisState;
    }

    @Override
    protected void visualize() {
        ParticleHelper.spawnParticle(world,
                new BlockStateParticleEffect(ParticleTypes.BLOCK, debrisState),
                position,
                3,
                new Vec3d(0.5, 0.5, 0.5),
                0.1f);
    }

    @Override
    protected void updateExtra() {
        //[Space intentionally left blank]
    }

    @Override
    protected boolean shouldDie() {
        //die when you came to the top
        return velocity.getY() < 0;
    }
}

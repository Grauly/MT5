package grauly.mt5.effects.explosion.particle;

import grauly.mt5.effects.explosion.ExplosionHeatVisualizers;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class TemperatureDisplayingParticle extends SimulatedParticle {

    protected float temperature;
    protected float cooling;

    public TemperatureDisplayingParticle(ServerWorld world, Vec3d position, Vec3d velocity, float drag, float temperature, float cooling) {
        super(world, GRAVITY, position, velocity, drag);
        this.temperature = temperature;
        this.cooling = cooling;
    }

    public TemperatureDisplayingParticle(ServerWorld world, Vec3d position, Vec3d velocity, float drag, float temperature, float cooling, int multiRuns) {
        super(world, GRAVITY, position, velocity, drag, multiRuns);
        this.temperature = temperature;
        this.cooling = cooling;
    }

    public TemperatureDisplayingParticle(ServerWorld world, Vec3d gravity, Vec3d position, Vec3d velocity, float drag, float temperature, float cooling, int multiRuns) {
        super(world, gravity, position, velocity, drag, multiRuns);
        this.temperature = temperature;
        this.cooling = cooling;
    }

    @Override
    protected void updateExtra() {
        temperature -= hasHitGround ? 1 : cooling;
    }

    @Override
    protected boolean shouldDie() {
        return temperature <= 0;
    }

    protected void visualize() {
        ExplosionHeatVisualizers.fieryDefaultVisuals.get((int) temperature).display(world, position, 1, true);
    }
}

package grauly.mt5.effects.explosion.particle;

import grauly.mt5.effects.explosion.ExplosionHeatVisualizers;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class TemperatureAwareParticle extends TemperatureDisplayingParticle {
    public static final Vec3d BASE_GRAVITY = new Vec3d(0, -0.2, 0);
    public static final Vec3d BASE_BUOYANCY = new Vec3d(0, 0.2f, 0);
    public static final float BASE_BUOYANCY_TEMP_MULTIPLIER = 0.25f;
    protected Vec3d buoyancy;
    protected float buoyancyTempMultiplier;

    public TemperatureAwareParticle(ServerWorld world, Vec3d position, Vec3d velocity, float drag, Vec3d gravity, float temperature, float cooling, Vec3d buoyancy, float buoyancyTempMultiplier, int multiRuns) {
        super(world, gravity, position, velocity, drag, temperature, cooling, multiRuns);
        this.buoyancy = buoyancy;
        this.buoyancyTempMultiplier = buoyancyTempMultiplier;
    }

    public TemperatureAwareParticle(ServerWorld world, Vec3d position, Vec3d velocity, float drag, float temperature, float cooling, int multiRuns) {
        super(world, BASE_GRAVITY, position, velocity, drag, temperature, cooling, multiRuns);
        this.buoyancy = BASE_BUOYANCY;
        this.buoyancyTempMultiplier = BASE_BUOYANCY_TEMP_MULTIPLIER;
    }

    @Override
    protected void updateVelocity() {
        velocity = velocity.add(gravity).add(buoyancy.multiply(Math.max(0, buoyancyTempMultiplier * (temperature)))).multiply(drag);
    }

    @Override
    protected void visualize() {
        ExplosionHeatVisualizers.fieryFancyVisuals.get((int) Math.floor(temperature)).display(world, position, 1, true);
    }
}

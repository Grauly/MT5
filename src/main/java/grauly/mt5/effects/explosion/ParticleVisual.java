package grauly.mt5.effects.explosion;

import grauly.mt5.helpers.ParticleHelper;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;
import java.util.Optional;

public class ParticleVisual {
    private final ParticleEffect effect;
    private final int amount;
    private final float speed;
    private final Vec3d delta;

    public ParticleVisual(ParticleEffect effect, int amount, float speed, Vec3d delta) {
        this.effect = effect;
        this.amount = amount;
        this.speed = speed;
        this.delta = delta;
    }

    public ParticleVisual(ParticleEffect effect, int amount, float speed) {
        this.effect = effect;
        this.amount = amount;
        this.speed = speed;
        delta = new Vec3d(0, 0, 0);
    }

    public void display(ServerWorld world, Vec3d position, boolean force) {
        ParticleHelper.spawnParticle(world, getEffect(), position, amount, delta, speed, force);
    }

    public void display(ServerWorld world, Vec3d position) {
        display(world, position, false);
    }

    public void display(ServerWorld world, Vec3d position, int overrideAmount, boolean force) {
        ParticleHelper.spawnParticle(world, getEffect(), position, amount == 0 ? 0 : overrideAmount, delta, speed, force);
    }

    public void display(ServerWorld world, Vec3d position, int overrideAmount) {
        display(world, position, overrideAmount, false);
    }

    public ParticleEffect getEffect() {
        return effect;
    }

    public int getAmount() {
        return amount;
    }

    public float getSpeed() {
        return speed;
    }

    public Optional<Vec3d> getDelta() {
        return Optional.ofNullable(delta);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (grauly.mt5.effects.explosion.ParticleVisual) obj;
        return Objects.equals(this.effect, that.effect) &&
                this.amount == that.amount &&
                Float.floatToIntBits(this.speed) == Float.floatToIntBits(that.speed) &&
                Objects.equals(this.delta, that.delta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(effect, amount, speed, delta);
    }

    @Override
    public String toString() {
        return "ParticleVisual[" +
                "effect=" + effect + ", " +
                "amount=" + amount + ", " +
                "speed=" + speed + ", " +
                "delta=" + delta + ']';
    }

}

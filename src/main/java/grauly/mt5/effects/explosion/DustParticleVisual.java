package grauly.mt5.effects.explosion;

import grauly.mt5.helpers.ParticleHelper;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

public class DustParticleVisual extends ParticleVisual {

    protected final float lowerSize;
    protected final float upperSize;
    protected final Color color;

    public DustParticleVisual(float lowerSize, float upperSize, Color color, int amount, float speed, Vec3d delta) {
        super(ParticleHelper.getDustParticle(color, lowerSize, upperSize), amount, speed, delta);
        this.lowerSize = lowerSize;
        this.upperSize = upperSize;
        this.color = color;
    }

    @Override
    public ParticleEffect getEffect() {
        return ParticleHelper.getDustParticle(color, lowerSize, upperSize);
    }
}

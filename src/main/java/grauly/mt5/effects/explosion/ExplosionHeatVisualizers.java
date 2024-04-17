package grauly.mt5.effects.explosion;

import grauly.mt5.helpers.structures.FallThroughMap;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

public class ExplosionHeatVisualizers {

    public static final FallThroughMap<ParticleVisual> fieryDefaultVisuals = new FallThroughMap<>();

    static {
        fieryDefaultVisuals.add(10, new ParticleVisual(ParticleTypes.FLAME, 3, 0.05f, new Vec3d(0.05, 0.05, 0.05)));
        fieryDefaultVisuals.add(7, new DustParticleVisual(0.25f, 0.75f, Color.ORANGE, 5, 1, new Vec3d(0.03, 0.03, 0.03)));
        fieryDefaultVisuals.add(3, new DustParticleVisual(0.25f, 0.75f, Color.DARK_GRAY, 3, 1, new Vec3d(0.03, 0.03, 0.03)));
        fieryDefaultVisuals.add(0, new ParticleVisual(ParticleTypes.ASH, 3, 0.5f, new Vec3d(0.01, 0.01, 0.01)));
        fieryDefaultVisuals.setDefaultElement(new ParticleVisual(ParticleTypes.CAMPFIRE_COSY_SMOKE, 0, 0.5f, new Vec3d(0, 0.03, 0)));
    }

    public static final FallThroughMap<ParticleVisual> fieryFancyVisuals = new FallThroughMap<>();

    static {
        fieryFancyVisuals.add(7, new ParticleVisual(ParticleTypes.FLAME, 3, 0.05f, new Vec3d(0.05, 0.05, 0.05)));
        fieryFancyVisuals.add(5, new DustParticleVisual(0.25f, 0.75f, Color.ORANGE, 5, 1, new Vec3d(0.03, 0.03, 0.03)));
        fieryFancyVisuals.add(3, new DustParticleVisual(0.25f, 0.75f, Color.DARK_GRAY, 3, 1, new Vec3d(0.03, 0.03, 0.03)));
        fieryFancyVisuals.add(0, new ParticleVisual(ParticleTypes.ASH, 3, 0.5f, new Vec3d(0.01, 0.01, 0.01)));
        fieryFancyVisuals.setDefaultElement(new ParticleVisual(ParticleTypes.CAMPFIRE_COSY_SMOKE, 0, 0.5f, new Vec3d(0, 0.03, 0)));
    }

}

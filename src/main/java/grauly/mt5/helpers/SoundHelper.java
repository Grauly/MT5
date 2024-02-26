package grauly.mt5.helpers;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

public class SoundHelper {
    protected ArrayList<SoundHelper> sounds = new ArrayList<>();
    protected SoundEvent sound;
    protected float mixFactor;
    protected float pitch;

    public SoundHelper(SoundEvent sound, float pitch) {
        this.sound = sound;
        this.mixFactor = 1;
        this.pitch = pitch;
    }

    public void play(ServerWorld world, Vec3d location, SoundCategory category, float volume, boolean useDistance) {
        world.playSound(location.getX(), location.getY(), location.getZ(), sound, category, volume * mixFactor, pitch, useDistance);
        for (SoundHelper sound : sounds) {
            sound.play(world, location, category, volume * sound.mixFactor, useDistance);
        }
    }

    public void play(ServerWorld world, Vec3d location, SoundCategory category, float volume) {
        play(world, location, category, volume, true);
    }

    public void setMixFactor(float newMixFactor) {
        this.mixFactor = newMixFactor;
    }

    public void add(SoundHelper sound, float mixFactor) {
        sound.setMixFactor(mixFactor);
        sounds.add(sound);
    }
}

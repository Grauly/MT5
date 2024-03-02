package grauly.mt5.ammotypes;

import grauly.mt5.effects.Splashes;
import grauly.mt5.entrypoints.MT5;
import grauly.mt5.helpers.ParticleHelper;
import grauly.mt5.helpers.ShotHelper;
import grauly.mt5.helpers.SoundHelper;
import grauly.mt5.registers.ModDamageTypes;
import grauly.mt5.weapons.AmmoType;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class BulletAmmoType implements AmmoType {
    private final float caliber;
    private final String IDOverride;

    public BulletAmmoType(float caliber, String idOverride) {
        this.caliber = caliber;
        IDOverride = idOverride;
    }

    public BulletAmmoType(float caliber) {
        this.caliber = caliber;
        IDOverride = null;
    }

    @Override
    public void doEntityImpact(Entity impacted, Entity shooter, Vec3d exactImpactLocation) {

    }

    @Override
    public void doEntityDamageImpact(LivingEntity entity, LivingEntity shooter, float distance, boolean headshot) {
        if (caliber <= 1) return;
        if (!(entity.getWorld() instanceof ServerWorld world)) return;
        if (caliber - 1 <= 0) return;
        entity.damage(new DamageSource(world.getDamageSources().registry.entryOf(getDamageType()), shooter, shooter), caliber - 1);
    }

    @Override
    public void doBlockImpact(ServerWorld world, Entity shooter, BlockPos blockPos, Vec3d exactImpact, Vec3d impactDirection) {
        Splashes.splashUp(world, exactImpact,  new BlockStateParticleEffect(ParticleTypes.BLOCK, world.getBlockState(blockPos)), 25);
        BlockSoundGroup blockSound = world.getBlockState(blockPos).getSoundGroup();
        new SoundHelper(blockSound.getBreakSound(),blockSound.getPitch()).play(world, exactImpact, SoundCategory.BLOCKS, 1f);
    }

    @Override
    public void doFireAction(LivingEntity shooter, ServerWorld world, Vec3d firingLocation, Vec3d direction) {
        firingLocation = firingLocation.add(ShotHelper.PARTICLE_OFFSET_AT_SHOOTER);
        Splashes.splash(world,firingLocation,direction.normalize(), ParticleTypes.FIREWORK,10,0.3f);
        SoundHelper shotSound = new SoundHelper(SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST,1.2f);
        shotSound.add(new SoundHelper(SoundEvents.BLOCK_PISTON_EXTEND,0.8f),0.1f);
        shotSound.add(new SoundHelper(SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1.2f),0.15f);
        shotSound.play(world, firingLocation, SoundCategory.PLAYERS, 10f);
    }

    @Override
    public boolean overrideFireAction() {
        return false;
    }

    @Override
    public void doTrailAction(ServerWorld world, Vec3d position, Vec3d trailDirection) {
        float sizeOffset = (float) Math.min(Math.max(0, Math.log(caliber)), 4);
        ParticleHelper.spawnParticle(world, new DustParticleEffect(
                        Vec3d.unpackRgb(new Color(0.3f, 0.3f, 0.3f).getRGB()).toVector3f(),
                        ThreadLocalRandom.current().nextFloat(0.15f + sizeOffset, 0.5f + sizeOffset)),
                position,
                0,
                trailDirection,
                0.1,
                true);
    }

    @Override
    public RegistryKey<DamageType> getDamageType() {
        return ModDamageTypes.BULLET_DAMAGE;
    }

    @Override
    public boolean overridesDamageLogic() {
        return false;
    }

    @Override
    public int getPierceAmount() {
        return (int) Math.floor(caliber / 2f);
    }

    @Override
    public float getMunitionSize() {
        return caliber;
    }

    @Override
    public Text getAmmoName() {
        return Text.translatable("mt5.ammotype.bullet");
    }

    @Override
    public boolean willDestroyBlock(Block block) {
        return false;
    }

    @Override
    public float getHeadShotMultiplier() {
        return (float) (1.5f * (5 * Math.log(caliber) + 1));
    }

    @Override
    public Identifier getIdentifier() {
        if (IDOverride == null) return new Identifier(MT5.MODID, "bullet");
        return new Identifier(MT5.MODID, IDOverride);
    }
}

package grauly.mt5.ammotypes;

import grauly.mt5.effects.Splashes;
import grauly.mt5.entrypoints.MT5;
import grauly.mt5.helpers.ParticleHelper;
import grauly.mt5.registers.ModDamageTypes;
import grauly.mt5.weapons.AmmoType;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
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
        Splashes.splash(world, exactImpact, impactDirection.multiply(-1), new BlockStateParticleEffect(ParticleTypes.BLOCK, world.getBlockState(blockPos)), 25);
    }

    @Override
    public void doFireAction(LivingEntity shooter, ServerWorld world, Vec3d firingLocation, Vec3d direction) {
        //TODO sounds
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

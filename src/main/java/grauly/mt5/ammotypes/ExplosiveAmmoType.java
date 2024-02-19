package grauly.mt5.ammotypes;

import grauly.mt5.entrypoints.MT5;
import grauly.mt5.helpers.ExplosionHelper;
import grauly.mt5.helpers.ParticleHelper;
import grauly.mt5.registers.ModDamageTypes;
import grauly.mt5.weapons.AmmoType;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class ExplosiveAmmoType implements AmmoType {
    private final float baseExplosionPower;

    public ExplosiveAmmoType(float baseExplosionPower) {
        this.baseExplosionPower = baseExplosionPower;
    }

    protected void doExplosionImpact(ServerWorld world, Vec3d impactLocation, Entity shooter) {
        ExplosionHelper.explode(world,impactLocation,baseExplosionPower,shooter);
    }

    @Override
    public void doEntityImpact(Entity impacted, Entity shooter, Vec3d exactImpactLocation) {
        if(!(impacted.getWorld() instanceof ServerWorld world)) return;
        doExplosionImpact(world,exactImpactLocation,shooter);
    }

    @Override
    public void doEntityDamageImpact(LivingEntity entity, LivingEntity shooter, float distance, boolean headshot) {

    }

    @Override
    public void doBlockImpact(ServerWorld world, Entity shooter, BlockPos blockPos, Vec3d exactImpact, Vec3d impactDirection) {
        doExplosionImpact(world,exactImpact,shooter);
    }

    @Override
    public void doFireAction(LivingEntity shooter, ServerWorld world, Vec3d firingLocation, Vec3d direction) {

    }

    @Override
    public boolean overrideFireAction() {
        return false;
    }

    @Override
    public void doTrailAction(ServerWorld world, Vec3d position, Vec3d trailDirection) {
        ParticleHelper.spawnParticle(world, new DustParticleEffect(
                        Vec3d.unpackRgb(new Color(0.3f, 0.3f, 0.3f).getRGB()).toVector3f(),
                        ThreadLocalRandom.current().nextFloat(0.15f, 0.5f)),
                position, 0, trailDirection, 0.5f);
    }

    @Override
    public RegistryKey<DamageType> getDamageType() {
        return ModDamageTypes.BULLET_DAMAGE;
    }

    @Override
    public boolean overridesDamageLogic() {
        return true;
    }

    @Override
    public int getPierceAmount() {
        return 0;
    }

    @Override
    public float getMunitionSize() {
        return 1.5f;
    }

    @Override
    public Text getAmmoName() {
        return Text.translatable("mt5.ammotype.explosive");
    }

    @Override
    public boolean willDestroyBlock(Block block) {
        return false;
    }

    @Override
    public float getHeadShotMultiplier() {
        return 3;
    }

    @Override
    public Identifier getIdentifier() {
        return new Identifier(MT5.MODID, "explosive");
    }
}

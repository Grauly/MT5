package grauly.mt5.ammotypes;

import grauly.mt5.entrypoints.MT5;
import grauly.mt5.helpers.ParticleHelper;
import grauly.mt5.registers.ModDamageTypes;
import grauly.mt5.weapons.AmmoType;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
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
    @Override
    public void doEntityImpact(Entity impacted) {

    }

    @Override
    public void doEntityDamageImpact(LivingEntity entity, LivingEntity shooter, float distance, boolean headshot) {

    }

    @Override
    public void doBlockImpact(ServerWorld world, BlockPos blockPos, Vec3d exactImpact, Vec3d impactDirection) {
        for (int i = 0; i < 25; i++) {
            ParticleHelper.spawnParticle(world,
                    new BlockStateParticleEffect(ParticleTypes.BLOCK, world.getBlockState(blockPos)),
                    exactImpact,
                    0,
                    impactDirection.multiply(-1).add((ThreadLocalRandom.current().nextFloat() * 2 - 1) * 0.1,
                            1,
                            (ThreadLocalRandom.current().nextFloat() * 2 - 1) * 0.1),
                    0.5);
        }
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
        world.spawnParticles(new DustParticleEffect(
                        Vec3d.unpackRgb(new Color(0.3f, 0.3f, 0.3f).getRGB()).toVector3f(),
                        ThreadLocalRandom.current().nextFloat(0.15f, 0.5f)),
                position.getX(),
                position.getY(),
                position.getZ(),
                0,
                trailDirection.getX(),
                trailDirection.getY(),
                trailDirection.getZ(),
                0.1);
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
        return 0;
    }

    @Override
    public float getMunitionSize() {
        return 1;
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
        return 1.5f;
    }

    @Override
    public Identifier getIdentifier() {
        return new Identifier(MT5.MODID, "bullet");
    }
}

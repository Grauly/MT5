package grauly.mt5.ammotypes;

import grauly.mt5.effects.Lines;
import grauly.mt5.entrypoints.MT5;
import grauly.mt5.helpers.MathHelper;
import grauly.mt5.helpers.ParticleHelper;
import grauly.mt5.helpers.RaycastHelper;
import grauly.mt5.registers.ModDamageTypes;
import grauly.mt5.weapons.AmmoType;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;

import static grauly.mt5.helpers.ShotHelper.WEAPON_LENIENCE;
import static grauly.mt5.helpers.ShotHelper.isHeadShot;

public class ReflectionAmmoType implements AmmoType {

    public static final float MAX_RANGE = 150;
    protected final int maxReflections;
    protected final int basePierces;
    protected final float baseDamage;

    public ReflectionAmmoType(int maxReflections, int basePierces, float baseDamage) {
        this.maxReflections = maxReflections;
        this.basePierces = basePierces;
        this.baseDamage = baseDamage;
    }


    @Override
    public void doEntityImpact(Entity impacted, Entity shooter, Vec3d exactImpactLocation) {

    }

    @Override
    public void doEntityDamageImpact(LivingEntity entity, LivingEntity shooter, float distance, boolean headshot) {

    }

    @Override
    public void doBlockImpact(ServerWorld world, Entity shooter, BlockPos blockPos, Vec3d exactImpact, Vec3d impactDirection) {

    }

    @Override
    public void doFireAction(LivingEntity shooter, ServerWorld world, Vec3d firingLocation, Vec3d direction) {
        for (int i = 0; i < maxReflections + 1; i++) {
            BlockHitResult blockHit = RaycastHelper.rayCastBlock(world, firingLocation, direction, MAX_RANGE);
            ArrayList<EntityHitResult> hitEntities = new ArrayList<>(RaycastHelper.rayCastEntities(world, firingLocation, blockHit.getPos(), WEAPON_LENIENCE, (entity -> !entity.getUuid().equals(shooter.getUuid()))));
            hurtFirstNEntities(hitEntities, shooter, firingLocation, direction, basePierces + i, baseDamage);
            if (hitEntities.size() > i + basePierces) break;
            Lines.line(firingLocation, blockHit.getPos(), world, ParticleTypes.ELECTRIC_SPARK);
            if (blockHit.getType() == HitResult.Type.MISS) break;
            firingLocation = blockHit.getPos();
            direction = MathHelper.getReflectionVector(direction, blockHit.getSide());
        }
    }

    protected void hurtFirstNEntities(ArrayList<EntityHitResult> entities, LivingEntity shooter, Vec3d shotStartLocation, Vec3d shotDirection, int n, float damage) {
        for (int i = 0; i < Math.min(n, entities.size()); i++) {
            Entity entity = entities.get(i).getEntity();
            if (!(entity.getWorld() instanceof ServerWorld serverWorld)) continue;
            float actualDamage = damage;
            if (entity instanceof LivingEntity livingEntity) {
                boolean headShot = isHeadShot(livingEntity, shotStartLocation, shotDirection, MAX_RANGE);
                doEntityDamageImpact(livingEntity, shooter, (float) shotStartLocation.distanceTo(entities.get(i).getPos()), headShot);
                if(headShot) damage *= getHeadShotMultiplier();
            }
            entities.get(i).getEntity().damage(new DamageSource(serverWorld.getDamageSources().registry.entryOf(getDamageType()), shooter, shooter), actualDamage);
        }
    }

    @Override
    public boolean overrideFireAction() {
        return true;
    }

    @Override
    public void doTrailAction(ServerWorld world, Vec3d position, Vec3d trailDirection) {
        ParticleHelper.spawnParticle(world, new DustParticleEffect(Vec3d.unpackRgb(Color.RED.getRGB()).toVector3f(), 0.3f), position, 0, trailDirection, 0.1, true);
    }

    @Override
    public RegistryKey<DamageType> getDamageType() {
        return ModDamageTypes.TRIGONOMETRY_DAMAGE;
    }

    @Override
    public boolean overridesDamageLogic() {
        return true;
    }

    @Override
    public int getPierceAmount() {
        return basePierces;
    }

    @Override
    public float getMunitionSize() {
        return 1.5f;
    }

    @Override
    public Text getAmmoName() {
        return Text.translatable("mt5.ammotype.reflection");
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
        return new Identifier(MT5.MODID, "reflection");
    }
}

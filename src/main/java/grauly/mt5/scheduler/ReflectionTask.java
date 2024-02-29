package grauly.mt5.scheduler;

import grauly.mt5.ammotypes.ReflectionAmmoType;
import grauly.mt5.effects.Lines;
import grauly.mt5.effects.Spheres;
import grauly.mt5.entrypoints.MT5;
import grauly.mt5.helpers.MathHelper;
import grauly.mt5.helpers.ParticleHelper;
import grauly.mt5.helpers.RaycastHelper;
import grauly.mt5.helpers.SoundHelper;
import grauly.mt5.weapons.AmmoType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

import static grauly.mt5.helpers.ShotHelper.WEAPON_LENIENCE;
import static grauly.mt5.helpers.ShotHelper.isHeadShot;

public class ReflectionTask extends Task {

    protected final int reflectionsDone;
    private final int maxReflections;
    private final int reflectionDelay;
    private final Vec3d startLocation;
    private final Vec3d direction;
    private final LivingEntity shooter;
    private final int basePierces;
    private final float baseDamage;
    private final AmmoType ammoType;
    private final ServerWorld world;

    public ReflectionTask(int reflectionsDone, int maxReflections, int reflectionDelay, Vec3d startLocation, Vec3d direction, LivingEntity shooter, float baseDamage, AmmoType ammoType, ServerWorld world) {
        this.reflectionsDone = reflectionsDone;
        this.maxReflections = maxReflections;
        this.reflectionDelay = reflectionDelay;
        this.startLocation = startLocation;
        this.direction = direction;
        this.shooter = shooter;
        this.basePierces = ammoType.getPierceAmount();
        this.baseDamage = baseDamage;
        this.ammoType = ammoType;
        this.world = world;
    }

    @Override
    public void run() {
        if(reflectionsDone >= maxReflections) {
            this.setCanceled(true);
            return;
        }
        doReflectionStartEffect();
        BlockHitResult blockHit = RaycastHelper.rayCastBlock(world, startLocation, direction, ReflectionAmmoType.MAX_RANGE);
        ArrayList<EntityHitResult> hitEntities = new ArrayList<>(RaycastHelper.rayCastEntities(world, startLocation, blockHit.getPos(), WEAPON_LENIENCE, (entity -> !entity.getUuid().equals(shooter.getUuid()))));
        hurtFirstNEntities(hitEntities, shooter, startLocation, direction, basePierces + reflectionsDone, baseDamage);
        if (hitEntities.size() > reflectionsDone + basePierces) return;
        Lines.line(startLocation, blockHit.getPos(), (pos, dir) -> ammoType.doTrailAction(world,pos,dir),3);
        if (blockHit.getType() == HitResult.Type.MISS) return;
        Vec3d newFiringLocation = blockHit.getPos();
        Vec3d newDirection = MathHelper.getReflectionVector(direction, blockHit.getSide());
        ReflectionTask nextReflection = new ReflectionTask(reflectionsDone + 1, maxReflections, reflectionDelay, newFiringLocation, newDirection, shooter, baseDamage, ammoType, world);
        nextReflection.startTask(MT5.TASK_SCHEDULER, reflectionDelay, 1);
        this.setCanceled(true);
    }

    protected void doReflectionStartEffect() {
        Spheres.icoSphere(startLocation, 0.7f, 2, (pos) -> {
            ParticleHelper.spawnParticle(world, ParticleTypes.SCULK_CHARGE_POP, pos, 0, pos.subtract(startLocation), 0.3f);
        });
        new SoundHelper(SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, 0.8f).play(world, startLocation, SoundCategory.PLAYERS, 1f);
    }
    protected void hurtFirstNEntities(ArrayList<EntityHitResult> entities, LivingEntity shooter, Vec3d shotStartLocation, Vec3d shotDirection, int n, float damage) {
        for (int i = 0; i < Math.min(n, entities.size()); i++) {
            Entity entity = entities.get(i).getEntity();
            if (!(entity.getWorld() instanceof ServerWorld serverWorld)) continue;
            float actualDamage = damage;
            if (entity instanceof LivingEntity livingEntity) {
                boolean headShot = isHeadShot(livingEntity, shotStartLocation, shotDirection, ReflectionAmmoType.MAX_RANGE);
                ammoType.doEntityDamageImpact(livingEntity, shooter, (float) shotStartLocation.distanceTo(entities.get(i).getPos()), headShot);
                if (headShot) damage *= ammoType.getHeadShotMultiplier();
            }
            entities.get(i).getEntity().damage(new DamageSource(serverWorld.getDamageSources().registry.entryOf(ammoType.getDamageType()), shooter, shooter), actualDamage);
        }

    }
}
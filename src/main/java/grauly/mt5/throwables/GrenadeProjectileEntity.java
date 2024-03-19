package grauly.mt5.throwables;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import grauly.mt5.helpers.MathHelper;
import grauly.mt5.helpers.ParticleHelper;
import grauly.mt5.registers.ModEntityTypes;
import grauly.mt5.registers.ModRegistries;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Objects;

public class GrenadeProjectileEntity extends ThrownItemEntity implements PolymerEntity {

    public static final String FUSE_KEY = "Fuse";
    public static final String GRENADE_TYPE_KEY = "Type";
    protected GrenadeType grenadeType;
    protected int fuse;

    public GrenadeProjectileEntity(EntityType<? extends ThrownItemEntity> entityType, World world, GrenadeType grenadeType) {
        super(entityType, world);
        this.grenadeType = grenadeType;
        fuse = grenadeType.getFuseTimeTicks();
    }

    public GrenadeProjectileEntity(World world, LivingEntity owner, GrenadeType grenadeType) {
        super(ModEntityTypes.GRENADE, owner, world);
        this.grenadeType = grenadeType;
        fuse = grenadeType.getFuseTimeTicks();
    }

    public GrenadeProjectileEntity(World world, double x, double y, double z, GrenadeType grenadeType) {
        super(ModEntityTypes.GRENADE, x, y, z, world);
        this.grenadeType = grenadeType;
    }

    public GrenadeProjectileEntity(EntityType<GrenadeProjectileEntity> grenadeProjectileEntityEntityType, World world) {
        super(grenadeProjectileEntityEntityType, world);
        grenadeType = null;
        fuse = -1;
    }

    @Override
    public void tick() {
        super.tick();
        grenadeType.tick(this);
        fuse--;
        if (fuse == 0) {
            explodeAtLocation(getPos());
        }
        if(!(getWorld() instanceof ServerWorld world)) return;
        ParticleHelper.spawnParticle(world, ParticleTypes.CRIT, getPos(), 0, getRotationVector(), 0.1f, true);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt(FUSE_KEY, fuse);
        nbt.putString(GRENADE_TYPE_KEY, Objects.requireNonNull(ModRegistries.GRENADE_TYPE_REGISTRY.getId(grenadeType)).toString());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        fuse = nbt.getInt(FUSE_KEY);
        grenadeType = ModRegistries.GRENADE_TYPE_REGISTRY.get(new Identifier(nbt.getString(GRENADE_TYPE_KEY)));
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        if(grenadeType.explodeOnImpact()) {
            explodeAtLocation(entityHitResult.getPos());
            return;
        }
        bounce(this.getMovementDirection());
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        if (grenadeType.explodeOnImpact()) {
            explodeAtLocation(blockHitResult.getPos());
            return;
        }
        bounce(blockHitResult.getSide());
    }

    protected void bounce(Direction impactDirection) {
        setVelocity(MathHelper.getReflectionVector(getVelocity(), impactDirection).multiply(grenadeType.getBounceVelocityMultiplier()));
        grenadeType.onBounce(this);
    }

    protected void explodeAtLocation(Vec3d location) {
        if (!(getWorld() instanceof ServerWorld world)) return;
        if (!(getOwner() instanceof ServerPlayerEntity player)) return;
        grenadeType.explode(world, location, player);
        this.kill();
    }

    @Override
    protected Item getDefaultItem() {
        return null;
    }

    @Override
    public EntityType<?> getPolymerEntityType(ServerPlayerEntity player) {
        return EntityType.SNOWBALL;
    }
}
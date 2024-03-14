package grauly.mt5.throwables;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import grauly.mt5.helpers.MathHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class GrenadeProjectileEntity extends ThrownItemEntity implements PolymerEntity {

    public static final String FUSE_KEY = "Fuse";
    protected final GrenadeType grenadeType;
    protected int fuse;

    public GrenadeProjectileEntity(EntityType<? extends ThrownItemEntity> entityType, World world, GrenadeType grenadeType) {
        super(entityType, world);
        this.grenadeType = grenadeType;
        fuse = grenadeType.getFuseTimeTicks();
    }

    public GrenadeProjectileEntity(World world, LivingEntity owner, GrenadeType grenadeType) {
        super(null, owner, world);
        this.grenadeType = grenadeType;
        fuse = grenadeType.getFuseTimeTicks();
    }

    @Override
    public void tick() {
        super.tick();

    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt(FUSE_KEY, fuse);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        fuse = nbt.getInt(FUSE_KEY);
    }

    public GrenadeProjectileEntity(World world, double x, double y, double z, GrenadeType grenadeType) {
        super(null, x, y, z, world);
        this.grenadeType = grenadeType;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        if(!(getWorld() instanceof ServerWorld world)) return;
        if(!(getOwner() instanceof ServerPlayerEntity player)) return;
        if(grenadeType.explodeOnImpact()) {
            grenadeType.explode(world, blockHitResult.getPos(), player);
            return;
        }
        setVelocity(MathHelper.getReflectionVector(getVelocity(), blockHitResult.getSide()).multiply(0.8f));
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

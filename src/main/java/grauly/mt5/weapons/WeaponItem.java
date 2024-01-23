package grauly.mt5.weapons;

import eu.pb4.polymer.core.api.item.PolymerItem;
import grauly.mt5.effects.Lines;
import grauly.mt5.helpers.ShotHelper;
import grauly.mt5.weapons.AmmoType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class WeaponItem extends Item implements PolymerItem {
    private final int customModelData;
    private final float maxRange;
    private final Function<Float, Integer> damageFunction;
    private final int baseDamage;
    public WeaponItem(Settings settings, int customModelData, float maxRange, int baseDamage) {
        super(settings);
        this.customModelData = customModelData;
        this.maxRange = maxRange;
        this.baseDamage = baseDamage;
        damageFunction = null;
    }

    public WeaponItem(Settings settings, int customModelData, float maxRange, Function<Float, Integer> damageFunction) {
        super(settings);
        this.customModelData = customModelData;
        this.damageFunction = damageFunction;
        this.maxRange = maxRange;
        this.baseDamage = -1;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        reSyncState(user, hand);
        shoot(world,user,null);
        return TypedActionResult.fail(user.getStackInHand(hand));
    }

    public void shoot(World world, LivingEntity shooter, AmmoType ammoType) {
        if (!(world instanceof ServerWorld serverWorld)) return;
        ammoType.doFireAction(serverWorld, shooter.getEyePos(), shooter.getRotationVector());
        var castResult = ShotHelper.rayCast(serverWorld,
                shooter.getEyePos(),
                shooter.getRotationVector(),
                maxRange,
                0.1f,
                entity -> entity.getUuid().equals(shooter.getUuid()),
                block -> false);
        var closestHit = castResult.getClosest(shooter.getEyePos());
        var endPos = shooter.getEyePos().add(shooter.getRotationVector().normalize().multiply(maxRange));
        if(closestHit instanceof BlockHitResult blockHitResult) {
            ammoType.doBlockImpact(serverWorld,blockHitResult.getBlockPos(),blockHitResult.getPos());
            endPos = blockHitResult.getPos();
        } else if (closestHit instanceof EntityHitResult entityHitResult) {
            ammoType.doEntityImpact(entityHitResult.getEntity());
            endPos = entityHitResult.getPos();
            if(entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
                applyDamage(livingEntity, shooter, (float) shooter.getEyePos().distanceTo(endPos),ammoType);
            }
        }
        Lines.line(shooter.getEyePos(),endPos,(pos,dir) -> {ammoType.doTrailAction(serverWorld,pos,dir);},5);
    }

    public float getWeaponDamage(float distance) {
        if(damageFunction != null) return damageFunction.apply(distance);

        float falloffStart = maxRange/2;
        if(distance < falloffStart) return baseDamage;

        return (float) (Math.cos((Math.PI/maxRange)*distance + falloffStart)*(baseDamage));
    }
    public void applyDamage(LivingEntity hit, LivingEntity shooter, float distance, AmmoType ammoType) {
        var weaponDamage = getWeaponDamage(distance);
        if(!ammoType.overridesDamageLogic()) hit.damage(new DamageSource(ammoType.getDamageType(),shooter,shooter),weaponDamage);
        ammoType.doEntityDamageImpact(hit, shooter, distance);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if(!(world instanceof ServerWorld serverWorld)) return;
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return customModelData;
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipContext context, @Nullable ServerPlayerEntity player) {
        var stack = PolymerItem.super.getPolymerItemStack(itemStack, context, player);
        CrossbowItem.setCharged(stack, true);
        stack.addHideFlag(ItemStack.TooltipSection.ADDITIONAL);
        return stack;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return Items.CROSSBOW;
    }

    protected void reSyncState(PlayerEntity user, Hand hand) {
        int slot;
        if (hand == Hand.MAIN_HAND) {
            slot = user.getInventory().selectedSlot;
        } else {
            //offhand
            slot = 40;
        }
        if (user instanceof ServerPlayerEntity serverPlayerEntity) {
            serverPlayerEntity.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-2, 0, slot, this.getDefaultStack()));
        }

    }
}

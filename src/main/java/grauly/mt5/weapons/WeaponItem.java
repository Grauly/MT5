package grauly.mt5.weapons;

import eu.pb4.polymer.core.api.item.PolymerItem;
import grauly.mt5.effects.Lines;
import grauly.mt5.entrypoints.MT5;
import grauly.mt5.helpers.ShotHelper;
import grauly.mt5.registers.ModAmmoTypes;
import grauly.mt5.scheduler.ReloadTask;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class WeaponItem extends Item implements PolymerItem {
    public static final String AMMO_ITEM_KEY = "LoadedAmmo";
    public static final String AMMO_CURRENT_KEY = "ShotsLeft";
    private final int customModelData;
    private final float maxRange;
    private final Function<Float, Integer> damageFunction;
    private final int baseDamage;
    private final float ammoSpace;
    private final float ammoConsumptionMultiplier;
    private final int reloadTimeTicks;

    public WeaponItem(Settings settings, int customModelData, float maxRange, int baseDamage, float ammoSpace, int reloadTimeTicks) {
        super(settings);
        this.customModelData = customModelData;
        this.maxRange = maxRange;
        this.baseDamage = baseDamage;
        this.damageFunction = null;
        this.ammoSpace = ammoSpace;
        this.ammoConsumptionMultiplier = 1;
        this.reloadTimeTicks = reloadTimeTicks;
    }

    public WeaponItem(Settings settings, int customModelData, float maxRange, Function<Float, Integer> damageFunction, float ammoSpace, float ammoConsumptionMultiplier, int reloadTimeTicks) {
        super(settings);
        this.customModelData = customModelData;
        this.damageFunction = damageFunction;
        this.maxRange = maxRange;
        this.baseDamage = -1;
        this.ammoSpace = ammoSpace;
        this.ammoConsumptionMultiplier = ammoConsumptionMultiplier;
        this.reloadTimeTicks = reloadTimeTicks;
    }

    public static int findFirstCompatibleAmmo(Inventory targetInventory, Predicate<AmmoType> ammoTypePredicate) {
        if (targetInventory instanceof PlayerInventory playerInventory) {
            if (playerInventory.getStack(PlayerInventory.OFF_HAND_SLOT).getItem() instanceof AmmoTypeItem ammoTypeItem) {
                if (ammoTypePredicate.test(ammoTypeItem.getAmmoType())) return PlayerInventory.OFF_HAND_SLOT;
            }
        }
        for (int i = 0; i < targetInventory.size(); i++) {
            var stack = targetInventory.getStack(i);
            if (stack.getItem() instanceof AmmoTypeItem ammoTypeItem) {
                if (ammoTypePredicate.test(ammoTypeItem.getAmmoType())) return i;
            }
        }
        return -1;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        var weaponStack = user.getStackInHand(hand);
        if (useAmmo(weaponStack)) {
            var ammoType = ((AmmoTypeItem) getLoadedMagazine(weaponStack).getItem()).getAmmoType();
            MT5.LOGGER.info("pew");
            shoot(world, user, ammoType);
        } else {
            MT5.LOGGER.info("ratch");
            reloadWeapon(weaponStack, user);
        }
        reSyncState(user, hand, weaponStack);
        return TypedActionResult.fail(weaponStack);
    }

    protected boolean canReload(ItemStack weaponStack, PlayerEntity user) {
        var loadedMag = getLoadedMagazine(weaponStack);
        if (!(loadedMag.getItem() instanceof AmmoTypeItem)) return canReloadFromInventory(user);
        if (AmmoTypeItem.getAmmo(loadedMag) <= 0) return canReloadFromInventory(user);
        return true;
    }

    protected boolean canReloadFromInventory(PlayerEntity user) {
        var firstFoundSlot = findFirstCompatibleAmmo(user.getInventory(), this::isAmmoTypeCompatible);
        return firstFoundSlot >= 0;
    }

    protected boolean isAmmoTypeCompatible(AmmoType ammoType) {
        //TODO actual checks
        return true;
    }

    protected void reloadWeapon(ItemStack weaponStack, PlayerEntity user) {
        if (!canReload(weaponStack, user)) {
            doEmptyFire(user);
            return;
        }
        var reloadTask = new ReloadTask(weaponStack, user);
        reloadTask.startTask(MT5.TASK_SCHEDULER, 0, 1);
    }

    protected void doEmptyFire(Entity entity) {
        //TODO effects
        MT5.LOGGER.info("clinck");
    }

    public boolean reload(ItemStack weaponStack, Entity user) {
        var loadedMag = getLoadedMagazine(weaponStack);
        var targetInventory = user instanceof ServerPlayerEntity player ? player.getInventory() : null;
        if (!(loadedMag.getItem() instanceof AmmoTypeItem)) return reloadFromInventory(weaponStack, targetInventory);
        if (AmmoTypeItem.getAmmo(loadedMag) <= 0) return reloadFromInventory(weaponStack, targetInventory);
        return reloadFromInternal(weaponStack, loadedMag);
    }

    protected boolean reloadFromInternal(ItemStack weaponStack, ItemStack loadedMag) {
        if (!(loadedMag.getItem() instanceof AmmoTypeItem magazineItem)) return false;
        var ammoToLoad = Math.min(getAmmoCapacityFor(magazineItem.getAmmoType()), AmmoTypeItem.getAmmo(loadedMag));
        AmmoTypeItem.changeAmmoAmount(loadedMag, -ammoToLoad);
        setLoadedMagazine(weaponStack, loadedMag);
        weaponStack.getNbt().putInt(AMMO_CURRENT_KEY, ammoToLoad);
        return true;
    }

    protected boolean reloadFromInventory(ItemStack weaponStack, Inventory targetInventory) {
        if (targetInventory == null) return true;
        var slot = findFirstCompatibleAmmo(targetInventory, this::isAmmoTypeCompatible);
        if (slot == -1) return false;
        var targetStack = targetInventory.getStack(slot).split(1);
        setLoadedMagazine(weaponStack, targetStack);
        return reloadFromInternal(weaponStack, targetStack);
    }

    protected boolean useAmmo(ItemStack weaponStack) {
        initIfNotPresent(weaponStack);
        var ammoLeft = weaponStack.getNbt().getInt(AMMO_CURRENT_KEY);
        if (ammoLeft > 0) {
            weaponStack.getNbt().putInt(AMMO_CURRENT_KEY, (int) Math.ceil(ammoLeft - 1 * ammoConsumptionMultiplier));
            return true;
        }
        return false;
    }

    protected ItemStack getLoadedMagazine(ItemStack weaponStack) {
        initIfNotPresent(weaponStack);
        return ItemStack.fromNbt(weaponStack.getNbt().getCompound(AMMO_ITEM_KEY));
    }

    protected void setLoadedMagazine(ItemStack weaponStack, ItemStack magazineStack) {
        initIfNotPresent(weaponStack);
        var magazineNbtCompound = new NbtCompound();
        magazineStack.writeNbt(magazineNbtCompound);
        weaponStack.getNbt().put(AMMO_ITEM_KEY, magazineNbtCompound);
    }

    protected void initIfNotPresent(ItemStack weaponStack) {
        if (!weaponStack.hasNbt()) {
            var itemStackNbt = new NbtCompound();
            ItemStack.EMPTY.writeNbt(itemStackNbt);
            weaponStack.getOrCreateNbt().put(AMMO_ITEM_KEY, itemStackNbt);
            weaponStack.getNbt().putInt(AMMO_CURRENT_KEY, 0);
        }
    }

    protected int getAmmoCapacityFor(AmmoType ammoType) {
        return (int) Math.floor(ammoSpace / ammoType.getMunitionSize());
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

    }

    protected void shoot(World world, LivingEntity shooter, AmmoType ammoType) {
        if (!(world instanceof ServerWorld serverWorld)) return;
        ammoType.doFireAction(shooter, serverWorld, shooter.getEyePos(), shooter.getRotationVector());
        if (ammoType.overrideFireAction()) return;
        doFireAction(serverWorld, shooter, shooter.getEyePos(), shooter.getRotationVector());
        if (ammoType.getPierceAmount() <= 0) {
            handleSingleShot(serverWorld, shooter, ammoType);
        } else {
            handleMultiShot(serverWorld, shooter, ammoType);
        }
    }

    protected void doFireAction(ServerWorld serverWorld, LivingEntity shooter, Vec3d fireLocation, Vec3d fireDirection) {
        //TODO play sounds here
    }

    protected void handleMultiShot(ServerWorld serverWorld, LivingEntity shooter, AmmoType ammoType) {
        var multiCastResult = ShotHelper.rayCastPierce(serverWorld,
                shooter.getEyePos(),
                shooter.getRotationVector(),
                maxRange,
                0.1f,
                entity -> !entity.getUuid().equals(shooter.getUuid()),
                block -> false);
        var relevantHits = multiCastResult.getHitsBeforeBlock(shooter.getEyePos()).stream().limit(ammoType.getPierceAmount() + 1).toList();
        var endPos = shooter.getEyePos();
        for (EntityHitResult hit : relevantHits) {
            ammoType.doEntityImpact(hit.getEntity());
            endPos = relevantHits.get(Math.max(ammoType.getPierceAmount() - 1, 0)).getPos();
            if (hit.getEntity() instanceof LivingEntity livingEntity) {
                applyDamage(livingEntity, shooter, (float) shooter.getEyePos().distanceTo(hit.getPos()), ammoType);
            }
        }
        if (relevantHits.size() < ammoType.getPierceAmount() + 1) {
            if (multiCastResult.hitBlock() != null) {
                endPos = multiCastResult.hitBlock().getPos();
                ammoType.doBlockImpact(serverWorld, multiCastResult.hitBlock().getBlockPos(), multiCastResult.hitBlock().getPos(), shooter.getRotationVector());
            }
        } else {
            endPos = shooter.getEyePos().add(shooter.getRotationVector().normalize().multiply(maxRange));
        }
        Lines.line(shooter.getEyePos(), endPos, (pos, dir) -> ammoType.doTrailAction(serverWorld, pos, dir), 5);
    }

    protected void handleSingleShot(ServerWorld serverWorld, LivingEntity shooter, AmmoType ammoType) {
        var castResult = ShotHelper.rayCast(serverWorld,
                shooter.getEyePos(),
                shooter.getRotationVector(),
                maxRange,
                0.1f,
                entity -> !entity.getUuid().equals(shooter.getUuid()),
                block -> false);
        var closestHit = castResult.getClosest(shooter.getEyePos());
        var endPos = shooter.getEyePos().add(shooter.getRotationVector().normalize().multiply(maxRange));
        if (closestHit instanceof BlockHitResult blockHitResult) {
            ammoType.doBlockImpact(serverWorld, blockHitResult.getBlockPos(), blockHitResult.getPos(), shooter.getRotationVector());
            endPos = blockHitResult.getPos();
        } else if (closestHit instanceof EntityHitResult entityHitResult) {
            ammoType.doEntityImpact(entityHitResult.getEntity());
            endPos = entityHitResult.getPos();
            if (entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
                applyDamage(livingEntity, shooter, (float) shooter.getEyePos().distanceTo(endPos), ammoType);
            }
        }
        Lines.line(shooter.getEyePos(), endPos, (pos, dir) -> {
            ammoType.doTrailAction(serverWorld, pos, dir);
        }, 5);
    }

    public float getWeaponDamage(float distance) {
        if (damageFunction != null) return damageFunction.apply(distance);

        float falloffStart = maxRange / 2;
        if (distance < falloffStart) return baseDamage;

        return (float) (Math.cos((Math.PI / maxRange) * distance + falloffStart) * (baseDamage));
    }

    public int getReloadTime() {
        return reloadTimeTicks;
    }

    public void applyDamage(LivingEntity hit, LivingEntity shooter, float distance, AmmoType ammoType) {
        var weaponDamage = getWeaponDamage(distance);
        if (!ammoType.overridesDamageLogic()) {
            if (hit.getWorld() instanceof ServerWorld serverWorld) {
                var damageType = serverWorld.getDamageSources().registry.entryOf(ammoType.getDamageType());
                hit.damage(new DamageSource(damageType, shooter, shooter), weaponDamage);
            }
        }
        ammoType.doEntityDamageImpact(hit, shooter, distance);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!(world instanceof ServerWorld serverWorld)) {
        }
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

    protected void reSyncState(PlayerEntity user, Hand hand, ItemStack weaponStack) {
        int slot;
        if (hand == Hand.MAIN_HAND) {
            slot = user.getInventory().selectedSlot;
        } else {
            //offhand
            slot = 40;
        }
        if (user instanceof ServerPlayerEntity serverPlayerEntity) {
            serverPlayerEntity.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-2, 0, slot, weaponStack));
        }

    }
}

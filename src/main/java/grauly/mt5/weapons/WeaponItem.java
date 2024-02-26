package grauly.mt5.weapons;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import grauly.mt5.effects.Lines;
import grauly.mt5.entrypoints.MT5;
import grauly.mt5.helpers.MathHelper;
import grauly.mt5.helpers.ShotHelper;
import grauly.mt5.helpers.SoundHelper;
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
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

public class WeaponItem extends Item implements PolymerItem {
    public static final String AMMO_ITEM_KEY = "LoadedAmmo";
    public static final String AMMO_CURRENT_KEY = "ShotsLeft";
    public static final String WEAPON_UUID = "WeaponUUID";
    public static final float WEAPON_LENIENCE = 0.1f;
    public static final float HEAD_SIZE_RADIUS = 0.125f;
    private final int customModelData;
    private final float maxRange;
    private final Function<Float, Integer> damageFunction;
    private final int baseDamage;
    private final float ammoSpace;
    private final float ammoConsumptionMultiplier;
    private final int reloadTimeTicks;
    private final int weaponPullCooldown;
    private final float weaponBaseSpread;
    private final int shotCooldown;
    private TagKey<Item> allowedAmmo;

    public WeaponItem(Settings settings, PolymerModelData polymerModel, float maxRange, int baseDamage, float ammoSpace, int reloadTimeTicks, int shotCooldownTicks, int weaponPullCooldown, float weaponBaseSpread) {
        super(settings);
        this.customModelData = polymerModel.value();
        this.maxRange = maxRange;
        this.baseDamage = baseDamage;
        this.weaponBaseSpread = weaponBaseSpread;
        this.damageFunction = null;
        this.ammoSpace = ammoSpace;
        this.ammoConsumptionMultiplier = 1;
        this.reloadTimeTicks = reloadTimeTicks;
        this.weaponPullCooldown = weaponPullCooldown;
        this.shotCooldown = shotCooldownTicks;
    }

    public WeaponItem(Settings settings, PolymerModelData polymerModel, float maxRange, Function<Float, Integer> damageFunction, float ammoSpace, float ammoConsumptionMultiplier, int reloadTimeTicks, int shotCooldownTicks, int weaponPullCooldown, float weaponBaseSpread) {
        super(settings);
        this.customModelData = polymerModel.value();
        this.damageFunction = damageFunction;
        this.maxRange = maxRange;
        this.weaponBaseSpread = weaponBaseSpread;
        this.baseDamage = -1;
        this.ammoSpace = ammoSpace;
        this.ammoConsumptionMultiplier = ammoConsumptionMultiplier;
        this.reloadTimeTicks = reloadTimeTicks;
        this.weaponPullCooldown = weaponPullCooldown;
        this.shotCooldown = shotCooldownTicks;
    }

    public static int findFirstCompatibleAmmo(Inventory targetInventory, Predicate<AmmoTypeItem> ammoTypePredicate) {
        if (targetInventory instanceof PlayerInventory playerInventory) {
            if (playerInventory.getStack(PlayerInventory.OFF_HAND_SLOT).getItem() instanceof AmmoTypeItem ammoTypeItem) {
                if (ammoTypePredicate.test(ammoTypeItem)) return PlayerInventory.OFF_HAND_SLOT;
            }
        }
        for (int i = 0; i < targetInventory.size(); i++) {
            var stack = targetInventory.getStack(i);
            if (stack.getItem() instanceof AmmoTypeItem ammoTypeItem) {
                if (ammoTypePredicate.test(ammoTypeItem)) return i;
            }
        }
        return -1;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack weaponStack = user.getStackInHand(hand);
        if (useAmmo(weaponStack)) {
            AmmoType ammoType = ((AmmoTypeItem) getLoadedMagazine(weaponStack).getItem()).getAmmoType();
            user.sendMessage(Text.literal("[").append(Text.of(String.valueOf(weaponStack.getNbt().getInt(AMMO_CURRENT_KEY)))).append("]"), true);
            shoot(world, user, ammoType);
        } else {
            reloadWeapon(weaponStack, user);
        }
        reSyncState(user, hand, weaponStack);
        return TypedActionResult.fail(weaponStack);
    }

    protected boolean canReload(ItemStack weaponStack, PlayerEntity user) {
        if (user.getItemCooldownManager().isCoolingDown(this)) return false;
        ItemStack loadedMag = getLoadedMagazine(weaponStack);
        if (!(loadedMag.getItem() instanceof AmmoTypeItem) || AmmoTypeItem.getAmmo(loadedMag) <= 0) return canReloadFromInventory(user);
        return true;
    }

    protected boolean canReloadFromInventory(PlayerEntity user) {
        int firstFoundSlot = findFirstCompatibleAmmo(user.getInventory(), this::isAmmoTypeCompatible);
        return firstFoundSlot >= 0;
    }

    protected boolean canSwapAmmoFromOffHand(PlayerEntity user, ItemStack loadedMagStack) {
        var firstFoundSlot = findFirstCompatibleAmmo(user.getInventory(), this::isAmmoTypeCompatible);
        if (firstFoundSlot != PlayerInventory.OFF_HAND_SLOT) return false;
        var potentialLoadAmmo = (AmmoTypeItem) user.getInventory().getStack(firstFoundSlot).getItem();
        var currentLoadedAmmo = (AmmoTypeItem) loadedMagStack.getItem();
        if (potentialLoadAmmo.getAmmoType().isSame(currentLoadedAmmo.getAmmoType())) return false;
        return isAmmoTypeCompatible(potentialLoadAmmo);
    }

    protected boolean isAmmoTypeCompatible(AmmoTypeItem ammoTypeItem) {
        if(allowedAmmo == null) {
            Identifier itemID = Registries.ITEM.getId(this);
            Identifier tagKeyID = new Identifier(MT5.MODID, "allowedAmmo/" + itemID.getPath());
            allowedAmmo = TagKey.of(RegistryKeys.ITEM,tagKeyID);
        }
        return ammoTypeItem.getDefaultStack().isIn(allowedAmmo);
    }

    public void reloadWeapon(ItemStack weaponStack, PlayerEntity user) {
        if (!canReload(weaponStack, user)) {
            doEmptyFire(user);
            return;
        }
        var reloadTask = new ReloadTask(weaponStack, user);
        reloadTask.startTask(MT5.TASK_SCHEDULER, 0, 1);
    }

    protected void doEmptyFire(Entity entity) {
        if(!(entity.getWorld() instanceof ServerWorld world)) return;
        SoundHelper emptyFireSound = new SoundHelper(SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON,1);
        emptyFireSound.play(world,entity.getEyePos(), SoundCategory.PLAYERS,1f);
    }

    public boolean reload(ItemStack weaponStack, Entity user) {
        if (!(user instanceof ServerPlayerEntity player)) return reloadNonPlayer(weaponStack, user);
        boolean success = reloadPlayer(weaponStack, player);
        if (success) player.sendMessage(Text.translatable("mt5.text.reloadcomplete"), true);
        return success;
    }

    protected boolean reloadNonPlayer(ItemStack weaponStack, Entity user) {
        ItemStack loadedMag = getLoadedMagazine(weaponStack);
        if (!(loadedMag.getItem() instanceof AmmoTypeItem ammoTypeItem)) return false;
        AmmoTypeItem.changeAmmoAmount(loadedMag, getAmmoCapacityFor(ammoTypeItem.getAmmoType()) - AmmoTypeItem.getAmmo(loadedMag));
        setLoadedMagazine(weaponStack, loadedMag);
        return true;
    }

    protected boolean reloadPlayer(ItemStack weaponStack, ServerPlayerEntity player) {
        var loadedMag = getLoadedMagazine(weaponStack);
        if (canSwapAmmoFromOffHand(player, loadedMag)) return swapActiveAmmo(weaponStack, player);
        if (!(loadedMag.getItem() instanceof AmmoTypeItem) || AmmoTypeItem.getAmmo(loadedMag) <= 0) return reloadFromInventory(weaponStack, player.getInventory());
        return reloadFromInternal(weaponStack, loadedMag);
    }

    protected boolean swapActiveAmmo(ItemStack weaponStack, PlayerEntity player) {
        var currentLoadedMag = getLoadedMagazine(weaponStack);
        var magToLoad = player.getInventory().getStack(PlayerInventory.OFF_HAND_SLOT).split(1);
        var dumpMag = currentLoadedMag.copy();
        AmmoTypeItem.changeAmmoAmount(dumpMag,
                Math.min(
                        AmmoTypeItem.getAmmo(currentLoadedMag) + weaponStack.getNbt().getInt(AMMO_CURRENT_KEY),
                        ((AmmoTypeItem) currentLoadedMag.getItem()).getCapacity()
                )
        );
        player.getInventory().offerOrDrop(dumpMag);
        return reloadFromInternal(weaponStack, magToLoad);
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
            weaponStack.getNbt().putUuid(WEAPON_UUID, UUID.randomUUID());
        }
    }

    protected int getAmmoCapacityFor(AmmoType ammoType) {
        return (int) Math.floor(ammoSpace / ammoType.getMunitionSize());
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        var ammoLeft = stack.getOrCreateNbt().getInt(AMMO_CURRENT_KEY);
        tooltip.add(Text.translatable("mt5.text.ammoleft").append(Text.literal(String.valueOf(ammoLeft))));
        tooltip.add(Text.empty());
        tooltip.add(Text.translatable("mt5.text.loadedmagazine"));
        var loadedMag = getLoadedMagazine(stack);
        loadedMag.getItem().appendTooltip(loadedMag, world, tooltip, context);
    }

    protected void shoot(World world, LivingEntity shooter, AmmoType ammoType) {
        if (!(world instanceof ServerWorld serverWorld)) return;
        var shotVector = getShotVector(shooter, shooter.getRotationVector());
        var shotLocation = shooter.getEyePos();
        ammoType.doFireAction(shooter, serverWorld, shotLocation, shotVector);
        if (shooter instanceof ServerPlayerEntity player) doWeaponShotCooldown(serverWorld, shooter);
        if (ammoType.overrideFireAction()) return;
        doFireAction(serverWorld, shooter, shooter.getEyePos(), shotVector);
        if (ammoType.getPierceAmount() <= 0) {
            handleSingleShot(serverWorld, shotLocation, shotVector, shooter, ammoType);
        } else {
            handleMultiShot(serverWorld, shotLocation, shotVector, shooter, ammoType);
        }
    }

    protected void doWeaponShotCooldown(ServerWorld serverWorld, LivingEntity shooter) {
        if (shooter instanceof ServerPlayerEntity player) player.getItemCooldownManager().set(this, shotCooldown);
    }

    protected void doFireAction(ServerWorld serverWorld, LivingEntity shooter, Vec3d fireLocation, Vec3d fireDirection) {
        //TODO play sounds here
    }

    protected void handleMultiShot(ServerWorld serverWorld, Vec3d shotLocation, Vec3d shotVector, LivingEntity shooter, AmmoType ammoType) {
        var multiCastResult = ShotHelper.rayCastPierce(serverWorld,
                shotLocation,
                shotVector,
                maxRange,
                WEAPON_LENIENCE,
                entity -> !entity.getUuid().equals(shooter.getUuid()),
                block -> false);
        var relevantHits = multiCastResult.getHitsBeforeBlock(shotLocation).stream().limit(ammoType.getPierceAmount() + 1).toList();
        var endPos = shotLocation;
        for (EntityHitResult hit : relevantHits) {
            ammoType.doEntityImpact(hit.getEntity(), shooter, hit.getPos());
            endPos = relevantHits.get(Math.max(ammoType.getPierceAmount() - 1, 0)).getPos();
            if (hit.getEntity() instanceof LivingEntity livingEntity) {
                applyDamage(livingEntity, shooter, (float) shotLocation.distanceTo(hit.getPos()), isHeadShot(livingEntity, shotLocation, shotVector), ammoType);
            }
        }
        if (relevantHits.size() < ammoType.getPierceAmount() + 1) {
            if (multiCastResult.hitBlock() != null) {
                endPos = multiCastResult.hitBlock().getPos();
                ammoType.doBlockImpact(serverWorld, shooter, multiCastResult.hitBlock().getBlockPos(), multiCastResult.hitBlock().getPos(), shotVector);
            }
        } else {
            endPos = shotLocation.add(shotVector.normalize().multiply(maxRange));
        }
        Lines.line(shotLocation, endPos, (pos, dir) -> ammoType.doTrailAction(serverWorld, pos, dir), 5);
    }

    protected void handleSingleShot(ServerWorld serverWorld, Vec3d shotLocation, Vec3d shotVector, LivingEntity shooter, AmmoType ammoType) {
        var castResult = ShotHelper.rayCast(serverWorld,
                shotLocation,
                shotVector,
                maxRange,
                WEAPON_LENIENCE,
                entity -> !entity.getUuid().equals(shooter.getUuid()),
                block -> false);
        var closestHit = castResult.getClosest(shotLocation);
        var endPos = shotLocation.add(shotVector.normalize().multiply(maxRange));
        if (closestHit instanceof BlockHitResult blockHitResult) {
            ammoType.doBlockImpact(serverWorld, shooter, blockHitResult.getBlockPos(), blockHitResult.getPos(), shotVector);
            endPos = blockHitResult.getPos();
        } else if (closestHit instanceof EntityHitResult entityHitResult) {
            ammoType.doEntityImpact(entityHitResult.getEntity(), shooter, entityHitResult.getPos());
            endPos = entityHitResult.getPos();
            if (entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
                applyDamage(livingEntity, shooter, (float) shotLocation.distanceTo(endPos), isHeadShot(livingEntity, shotLocation, shotVector), ammoType);
            }
        }
        Lines.line(shotLocation, endPos, (pos, dir) -> {
            ammoType.doTrailAction(serverWorld, pos, dir);
        }, 5);
    }

    protected boolean isHeadShot(LivingEntity hit, Vec3d shotOrigin, Vec3d shotVector) {
        Vec3d headBoxCenter = hit.getEyePos();
        Vec3d headBoxSize = new Vec3d(HEAD_SIZE_RADIUS, HEAD_SIZE_RADIUS, HEAD_SIZE_RADIUS);
        Box headBox = new Box(headBoxCenter.add(headBoxSize), headBoxCenter.subtract(headBoxSize)).expand(WEAPON_LENIENCE);
        Vec3d fullShotVector = shotVector.normalize().multiply(maxRange);
        Optional<Vec3d> headHit = headBox.raycast(shotOrigin, shotOrigin.add(fullShotVector));
        return headHit.isPresent();
    }

    protected Vec3d getShotVector(LivingEntity shooter, Vec3d baseVector) {
        float speedModifier = shooter instanceof ServerPlayerEntity player ? MT5.PLAYER_SPEED_TASK.getPlayerSpeed(player.getUuid()) : 5f; //TODO move to constants file
        var stabilityModifier = shooter.isSneaking() ? -3 : 0;
        stabilityModifier += shooter.isFallFlying() ? 5 : 0;
        stabilityModifier += shooter.isClimbing() ? 2 : 0;
        stabilityModifier += shooter.isOnGround() ? 0 : 5;
        return MathHelper.spreadShot(baseVector, Math.max(0, weaponBaseSpread + speedModifier + stabilityModifier));
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

    public void applyDamage(LivingEntity hit, LivingEntity shooter, float distance, boolean headshot, AmmoType ammoType) {
        if (!ammoType.overridesDamageLogic() && hit.getWorld() instanceof ServerWorld serverWorld) {
            var weaponDamage = getWeaponDamage(distance) * (headshot ? ammoType.getHeadShotMultiplier() : 1);
            var damageType = serverWorld.getDamageSources().registry.entryOf(ammoType.getDamageType());
            hit.damage(new DamageSource(damageType, shooter, shooter), weaponDamage);
        }
        ammoType.doEntityDamageImpact(hit, shooter, distance, headshot);
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
        int slot = hand == Hand.MAIN_HAND ? user.getInventory().selectedSlot : PlayerInventory.OFF_HAND_SLOT;
        if (user instanceof ServerPlayerEntity serverPlayerEntity) {
            serverPlayerEntity.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-2, 0, slot, weaponStack));
        }
    }

    public int getPullCooldown() {
        return weaponPullCooldown;
    }

    public UUID getWeaponUUID(ItemStack weaponStack) {
        initIfNotPresent(weaponStack);
        return weaponStack.getNbt().getUuid(WEAPON_UUID);
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return customModelData;
    }
}

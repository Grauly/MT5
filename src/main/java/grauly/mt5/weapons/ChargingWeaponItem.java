package grauly.mt5.weapons;

import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.awt.*;
import java.util.function.Function;

public class ChargingWeaponItem extends WeaponItem {
    public static final String CHARGED_KEY = "WeaponCharged";
    public static final int FILLED_BAR_COLOR = new Color(0, 255, 0).getRGB();
    public static final int UNFILLED_BAR_COLOR = new Color(35, 35, 35).getRGB();
    private final int chargeTimeTicks;
    private final boolean immediateRelease;

    public ChargingWeaponItem(Settings settings, PolymerModelData polymerModel, float maxRange, int baseDamage, float ammoSpace, int reloadTimeTicks, int shotCooldownTicks, int weaponPullCooldown, float weaponBaseSpread, int chargeTimeTicks, boolean immediateRelease) {
        super(settings, polymerModel, maxRange, baseDamage, ammoSpace, reloadTimeTicks, shotCooldownTicks, weaponPullCooldown, weaponBaseSpread);
        this.chargeTimeTicks = chargeTimeTicks;
        this.immediateRelease = immediateRelease;
    }

    public ChargingWeaponItem(Settings settings,PolymerModelData polymerModel, float maxRange, Function<Float, Integer> damageFunction, float ammoSpace, float ammoConsumptionMultiplier, int reloadTimeTicks, int shotCooldownTicks, int weaponPullCooldown, float weaponBaseSpread, int chargeTimeTicks, boolean immediateRelease) {
        super(settings, polymerModel, maxRange, damageFunction, ammoSpace, ammoConsumptionMultiplier, reloadTimeTicks, shotCooldownTicks, weaponPullCooldown, weaponBaseSpread);
        this.chargeTimeTicks = chargeTimeTicks;
        this.immediateRelease = immediateRelease;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        super.usageTick(world, user, stack, remainingUseTicks);
        if (user instanceof ServerPlayerEntity player) drawChargeBar(20, remainingUseTicks, player);
    }

    protected void drawChargeBar(int segments, int remainingTicks, ServerPlayerEntity player) {
        MutableText chargeBar = Text.literal("[");
        float completion = (float) (chargeTimeTicks + 1 - Math.max(0, remainingTicks)) / chargeTimeTicks;
        int filledBars = (int) Math.floor(completion * segments);
        for (int i = 0; i < segments; i++) {
            chargeBar.append(Text.literal("|").withColor(i < filledBars ? FILLED_BAR_COLOR : UNFILLED_BAR_COLOR));
        }
        chargeBar.append(Text.literal("]"));
        player.sendMessage(chargeBar, true);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack weaponStack = user.getStackInHand(hand);
        initIfNotPresent(weaponStack);
        if (weaponStack.getNbt().getInt(AMMO_CURRENT_KEY) <= 0) {
            reloadWeapon(weaponStack, user);
            return TypedActionResult.fail(weaponStack);
        }
        if (weaponStack.getNbt().getBoolean(CHARGED_KEY)) {
            weaponStack.getNbt().putBoolean(CHARGED_KEY, false);
            return super.use(world, user, hand);
        }
        user.setCurrentHand(hand);
        reSyncState(user, hand, weaponStack);
        return TypedActionResult.fail(user.getStackInHand(hand));
    }

    @Override
    protected void initIfNotPresent(ItemStack weaponStack) {
        super.initIfNotPresent(weaponStack);
        if (!weaponStack.getNbt().getBoolean(CHARGED_KEY)) weaponStack.getNbt().putBoolean(CHARGED_KEY, false);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        super.onStoppedUsing(stack, world, user, remainingUseTicks);
        if (remainingUseTicks > 0) return;
        if (immediateRelease && user instanceof ServerPlayerEntity player) {
            Hand usedHand = player.getMainHandStack().equals(stack) ? Hand.MAIN_HAND : Hand.OFF_HAND;
            super.use(world, player, usedHand);
        } else {
            stack.getNbt().putBoolean(CHARGED_KEY, true);
        }
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        stack.getNbt().putBoolean(CHARGED_KEY, true);
        if (immediateRelease && user instanceof ServerPlayerEntity player) {
            Hand usedHand = player.getMainHandStack().equals(stack) ? Hand.MAIN_HAND : Hand.OFF_HAND;
            super.use(world, player, usedHand);
        }
        return super.finishUsing(stack, world, user);
    }

    @Override
    public boolean isUsedOnRelease(ItemStack stack) {
        return stack.isOf(this);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return chargeTimeTicks;
    }
}

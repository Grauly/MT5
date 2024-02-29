package grauly.mt5.weapons;

import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import grauly.mt5.helpers.NetworkHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.function.Function;

public class AutomaticWeaponItem extends WeaponItem {

    public AutomaticWeaponItem(Settings settings, PolymerModelData polymerModel, float maxRange, int baseDamage, float ammoSpace, int reloadTimeTicks, int shotCooldownTicks, int weaponPullCooldown, float weaponBaseSpread) {
        super(settings, polymerModel, maxRange, baseDamage, ammoSpace, reloadTimeTicks, shotCooldownTicks, weaponPullCooldown, weaponBaseSpread);
    }

    public AutomaticWeaponItem(Settings settings, PolymerModelData polymerModel, float maxRange, Function<Float, Integer> damageFunction, float ammoSpace, float ammoConsumptionMultiplier, int reloadTimeTicks, int shotCooldownTicks, int weaponPullCooldown, float weaponBaseSpread) {
        super(settings, polymerModel, maxRange, damageFunction, ammoSpace, ammoConsumptionMultiplier, reloadTimeTicks, shotCooldownTicks, weaponPullCooldown, weaponBaseSpread);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        NetworkHelper.reSyncState(user, hand, user.getStackInHand(hand));
        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        super.usageTick(world, user, stack, remainingUseTicks);
        Hand hand = user.getMainHandStack().equals(stack) ? Hand.MAIN_HAND : Hand.OFF_HAND;
        if (user instanceof PlayerEntity player) super.use(world, player, hand);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 200;
    }
}

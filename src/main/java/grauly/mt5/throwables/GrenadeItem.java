package grauly.mt5.throwables;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GrenadeItem extends Item implements PolymerItem {

    protected final GrenadeType grenadeType;
    public GrenadeItem(Settings settings, GrenadeType grenadeType) {
        super(settings);
        this.grenadeType = grenadeType;
    }

    @Override
    public void onItemEntityDestroyed(ItemEntity entity) {
        if(!(entity.getWorld() instanceof ServerWorld world)) return;
        if(!(entity.getOwner() instanceof ServerPlayerEntity owner)) return;
        grenadeType.explode(world, entity.getPos(), owner);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack grenadeStack = user.getStackInHand(hand);
        if(world instanceof ServerWorld serverWorld) {
            //TODO play throw sound
            GrenadeProjectileEntity grenade = new GrenadeProjectileEntity(serverWorld, user, grenadeType);
            grenade.setItem(grenadeStack);
            grenade.setOwner(user);
            grenade.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 1.5f, 0f);
            user.getItemCooldownManager().set(this, 5);
            world.spawnEntity(grenade);
            grenadeType.onThrow(grenade);
        }
        if(!user.getAbilities().creativeMode) {
            grenadeStack.decrement(1);
        }
        return TypedActionResult.success(grenadeStack);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return Items.BLAZE_POWDER;
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return grenadeType.getCustomModelData();
    }
}

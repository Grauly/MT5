package grauly.mt5.weapons;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AmmoTypeItem extends Item implements PolymerItem {
    public static final String AMMO_LEFT_KEY = "AmmoLeft";
    private final AmmoType ammoType;
    private final int customModelData;
    private final int capacity;

    public AmmoTypeItem(AmmoType ammoType, int capacity, int customModelData) {
        super(new FabricItemSettings().rarity(Rarity.UNCOMMON).maxCount(16));
        this.ammoType = ammoType;
        this.customModelData = customModelData;
        this.capacity = capacity;
    }

    /**
     * changes the amount of ammo in this item, taking care not to go out of bounds
     *
     * @param stack  the itemStack
     * @param amount the amount to change by
     * @return true, if the change could successfully be made
     */
    public static boolean changeAmmoAmount(ItemStack stack, int amount) {
        if (!(stack.getItem() instanceof AmmoTypeItem ammoTypeItem)) return false;
        var ammoLeft = stack.getOrCreateNbt().getInt(AMMO_LEFT_KEY);
        if (ammoLeft + amount < 0) return false;
        if (ammoLeft + amount > ammoTypeItem.getCapacity()) return false;
        stack.getOrCreateNbt().putInt(AMMO_LEFT_KEY, (ammoLeft + amount));
        return true;
    }

    /**
     * retrieves the ammo left in this item
     *
     * @param stack the itemStack
     * @return the amount of ammo left
     */
    public static int getAmmo(ItemStack stack) {
        if (!(stack.getItem() instanceof AmmoTypeItem ammoTypeItem)) return -1;
        initAmmoIfNotPresent(stack);
        return stack.getOrCreateNbt().getInt(AMMO_LEFT_KEY);
    }

    public static void initAmmoIfNotPresent(ItemStack stack) {
        if (!(stack.getItem() instanceof AmmoTypeItem ammoTypeItem)) return;
        if (!stack.hasNbt()) {
            stack.getOrCreateNbt().putInt(AMMO_LEFT_KEY, ammoTypeItem.getCapacity());
        }
    }

    public AmmoType getAmmoType() {
        return ammoType;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.add(ammoType.getAmmoName().copy().append(Text.translatable("mt5.remaining")).append(Text.of(String.valueOf(getAmmo(stack)))));
    }

    @Override
    public void onItemEntityDestroyed(ItemEntity entity) {
        if (!(entity.getWorld() instanceof ServerWorld serverWorld)) return;
        if (!entity.isOnFire()) return;
        ammoType.doBlockImpact(serverWorld, entity.getBlockPos(), entity.getPos(), entity.getVelocity().multiply(-1));
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return Items.IRON_INGOT;
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return customModelData;
    }
}

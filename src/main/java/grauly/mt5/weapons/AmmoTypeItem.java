package grauly.mt5.weapons;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Rarity;
import org.jetbrains.annotations.Nullable;

public class AmmoTypeItem extends Item implements PolymerItem {
    private final AmmoType ammoType;
    private final int customModelData;

    public AmmoTypeItem(AmmoType ammoType, int customModelData) {
        super(new FabricItemSettings().rarity(Rarity.UNCOMMON).maxCount(16));
        this.ammoType = ammoType;
        this.customModelData = customModelData;
    }

    public AmmoType getAmmoType() {
        return ammoType;
    }

    @Override
    public void onItemEntityDestroyed(ItemEntity entity) {
        if (!(entity.getWorld() instanceof ServerWorld serverWorld)) return;
        if (!entity.isOnFire()) return;
        ammoType.doBlockImpact(serverWorld, entity.getBlockPos(), entity.getPos());
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

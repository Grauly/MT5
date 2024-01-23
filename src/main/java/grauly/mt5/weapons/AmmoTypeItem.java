package grauly.mt5.weapons;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Rarity;

public class AmmoTypeItem extends Item {
    private final AmmoType ammoType;

    public AmmoTypeItem(AmmoType ammoType) {
        super(new FabricItemSettings().rarity(Rarity.UNCOMMON).maxCount(16));
        this.ammoType = ammoType;
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
}

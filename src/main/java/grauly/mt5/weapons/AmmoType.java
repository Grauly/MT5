package grauly.mt5.weapons;

import net.minecraft.block.Block;

public interface AmmoType {
    void doEntityImpact();

    void doFireAction();

    float getRange();

    int getPierceAmount();

    boolean canPierceBlock(Block block);

    boolean willDestroyBlock(Block block);
    int getDamage(float range);
}

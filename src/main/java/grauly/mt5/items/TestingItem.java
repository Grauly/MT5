package grauly.mt5.items;

import eu.pb4.polymer.core.api.item.PolymerItem;
import grauly.mt5.effects.Spheres;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TestingItem extends Item implements PolymerItem {
    public TestingItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world instanceof ServerWorld serverWorld) {
            Spheres.icoSphere(user.getEyePos(), 5, 2, p -> serverWorld.spawnParticles(ParticleTypes.END_ROD, p.x, p.y, p.z, 0, 0, 0, 0, 0));
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return Items.BLAZE_ROD;
    }
}

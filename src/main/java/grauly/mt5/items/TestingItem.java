package grauly.mt5.items;

import eu.pb4.polymer.core.api.item.PolymerItem;
import grauly.mt5.effects.Primitives;
import net.minecraft.client.particle.FireworksSparkParticle;
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

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TestingItem extends Item implements PolymerItem {
    public TestingItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(world instanceof ServerWorld serverWorld) {
            Primitives.line(user.getEyePos(), user.getEyePos().add(user.getRotationVector().normalize().multiply(5)), serverWorld, new ArrayList<>(List.of(Color.BLUE, Color.CYAN)));
            Primitives.circle(user.getPos().add(0,0.5,0), 3, 32, serverWorld, new ArrayList<>(List.of(Color.RED, Color.ORANGE)));
            Primitives.circle(user.getPos().add(0,0.5,0), 3.5f, 32, serverWorld, ParticleTypes.DRAGON_BREATH);
            Primitives.circle(user.getEyePos().add(user.getRotationVector().multiply(0.5f)), user.getRotationVector(),0.7f,32,serverWorld,ParticleTypes.DRAGON_BREATH);
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return Items.BLAZE_ROD;
    }
}

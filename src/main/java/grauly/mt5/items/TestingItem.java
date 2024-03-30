package grauly.mt5.items;

import eu.pb4.polymer.core.api.item.PolymerItem;
import grauly.mt5.effects.Circles;
import grauly.mt5.effects.Lines;
import grauly.mt5.effects.Shockwave;
import grauly.mt5.effects.Splashes;
import grauly.mt5.effects.explosion.DebrisParticle;
import grauly.mt5.effects.explosion.FancyExplosion;
import grauly.mt5.effects.explosion.HeatedParticle;
import grauly.mt5.entrypoints.MT5;
import grauly.mt5.helpers.ParticleHelper;
import grauly.mt5.helpers.RaycastHelper;
import grauly.mt5.scheduler.FancyExplosionTask;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

public class TestingItem extends Item implements PolymerItem {
    public TestingItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!(world instanceof ServerWorld serverWorld)) return TypedActionResult.success(user.getStackInHand(hand));

        BlockHitResult result = RaycastHelper.rayCastBlock(serverWorld, user.getEyePos(), user.getRotationVector(), 50f);
        if (result.getType() == HitResult.Type.MISS) return TypedActionResult.success(user.getStackInHand(hand));

        Vec3d pos = result.getPos().add(0,0.1,0);
        Vec3d nor = user.getEyePos().subtract(result.getPos()).normalize();
        FancyExplosion.flash(serverWorld, pos, 4);
        new FancyExplosionTask(serverWorld, pos, nor, serverWorld.getBlockState(result.getBlockPos())).startTask(MT5.TASK_SCHEDULER,0,1);
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return Items.BLAZE_ROD;
    }
}

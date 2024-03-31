package grauly.mt5.items;

import eu.pb4.polymer.core.api.item.PolymerItem;
import grauly.mt5.effects.*;
import grauly.mt5.effects.explosion.DebrisParticle;
import grauly.mt5.effects.explosion.FancyExplosion;
import grauly.mt5.effects.explosion.HEExplosion;
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

       /* Vec3d pos = result.getPos().add(0,0.1,0);
        Vec3d nor = user.getEyePos().subtract(result.getPos()).normalize();
        FancyExplosion.flash(serverWorld, pos, 4);
        new FancyExplosionTask(serverWorld, pos, nor, serverWorld.getBlockState(result.getBlockPos())).startTask(MT5.TASK_SCHEDULER,0,1);*/
       /* Spheres.heightParametrizedFibonacciSphere(result.getPos().add(0,10,0), 5f,0.4f, 100 ,(pos) -> {
            ParticleHelper.spawnParticle(serverWorld, ParticleTypes.END_ROD, pos,0,new Vec3d(0,0,0),0);
        });*/
        new HEExplosion(20,result.getPos(), new Vec3d(0,1,0), serverWorld, null).setOff();
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return Items.BLAZE_ROD;
    }
}

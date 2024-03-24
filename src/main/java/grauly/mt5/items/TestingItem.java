package grauly.mt5.items;

import eu.pb4.polymer.core.api.item.PolymerItem;
import grauly.mt5.effects.Circles;
import grauly.mt5.effects.Shockwave;
import grauly.mt5.effects.Splashes;
import grauly.mt5.effects.explosion.DebrisParticle;
import grauly.mt5.effects.explosion.FancyExplosion;
import grauly.mt5.effects.explosion.HeatedParticle;
import grauly.mt5.entrypoints.MT5;
import grauly.mt5.helpers.ParticleHelper;
import grauly.mt5.helpers.RaycastHelper;
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
        Vec3d nor = user.getRotationVector().multiply(-1).normalize();
        /*//Fragments
        for (int i = 0; i < 15; i++) {
            float distribution = 0.5f;
            Vec3d velocityVector = new Vec3d(
                    ThreadLocalRandom.current().nextDouble(-distribution, distribution),
                    ThreadLocalRandom.current().nextDouble(0.01, 1),
                    ThreadLocalRandom.current().nextDouble(-distribution, distribution)
            ).normalize().multiply(ThreadLocalRandom.current().nextDouble(0.7, 1.5));
            HeatedParticle particle = new HeatedParticle(serverWorld, result.getPos().add(0, 0.1, 0), velocityVector, 0.95f, ThreadLocalRandom.current().nextInt(7, 10), 0.25f, 0f);
            particle.startTask(MT5.TASK_SCHEDULER, 0, 1);
        }*/
        FancyExplosion.fragments(serverWorld, pos, nor, 15);
        //Bloom
       /* for (int i = 0; i < 30; i++) {
            float distribution = 0.2f;
            Vec3d velocityVector = new Vec3d(
                    ThreadLocalRandom.current().nextDouble(-distribution, distribution),
                    ThreadLocalRandom.current().nextDouble(1, 1.5),
                    ThreadLocalRandom.current().nextDouble(-distribution, distribution)
            ).normalize().multiply(ThreadLocalRandom.current().nextDouble(1.2, 2));
            HeatedParticle particle = new HeatedParticle(serverWorld, result.getPos().add(0, 0.1, 0), velocityVector, 0.9f, ThreadLocalRandom.current().nextInt(10, 15), 0.5f, 3);
            particle.startTask(MT5.TASK_SCHEDULER, 0, 1);
        }*/
        FancyExplosion.bloom(serverWorld, pos, nor, 30);
        //Debris Bloom
       /* for (int i = 0; i < 15; i++) {
            float distribution = 0.2f;
            Vec3d velocityVector = new Vec3d(
                    ThreadLocalRandom.current().nextDouble(-distribution, distribution),
                    ThreadLocalRandom.current().nextDouble(1, 1.5),
                    ThreadLocalRandom.current().nextDouble(-distribution, distribution)
            ).normalize().multiply(ThreadLocalRandom.current().nextDouble(1.2, 2));
            DebrisParticle particle = new DebrisParticle(serverWorld, result.getPos().add(0, 0.1, 0), velocityVector, 0.9f, ThreadLocalRandom.current().nextInt(5, 7), 0.5f, 3);
            particle.setDebrisState(world.getBlockState(result.getBlockPos()));
            particle.startTask(MT5.TASK_SCHEDULER, 0, 1);
        }*/
        FancyExplosion.debrisBloom(serverWorld, pos, nor, 15, world.getBlockState(result.getBlockPos()));
        //Burst
        /*for (int i = 0; i < 150; i++) {
            float distribution = 0.9f;
            Vec3d velocityVector = new Vec3d(
                    ThreadLocalRandom.current().nextDouble(-distribution, distribution),
                    ThreadLocalRandom.current().nextDouble(0.1, 1),
                    ThreadLocalRandom.current().nextDouble(-distribution, distribution)
            ).normalize().multiply(ThreadLocalRandom.current().nextDouble(0.8, 2.8));
            HeatedParticle particle = new HeatedParticle(serverWorld, result.getPos().add(0, 0.1, 0), velocityVector, 0.6f, ThreadLocalRandom.current().nextInt(15, 20), 2.25f, 0, 4);
            particle.startTask(MT5.TASK_SCHEDULER, 0, 1);
        }*/
        FancyExplosion.burst(serverWorld, pos, nor, 150);
        //Smoke
       /* for (int i = 0; i < 50; i++) {
            float distribution = 3f;
            Vec3d velocityVector = new Vec3d(
                    ThreadLocalRandom.current().nextDouble(-distribution, distribution),
                    ThreadLocalRandom.current().nextDouble(0.1, 3),
                    ThreadLocalRandom.current().nextDouble(-distribution, distribution)
            ).normalize().multiply(ThreadLocalRandom.current().nextDouble(0, 3));
            ParticleHelper.spawnParticle(serverWorld, ParticleTypes.CAMPFIRE_COSY_SMOKE, result.getPos().add(velocityVector), 0, new Vec3d(0, 0.1f, 0), 0.2f);
        }*/
        FancyExplosion.smoke(serverWorld, pos, nor, 50);
        //Base
        //ParticleHelper.spawnParticle(serverWorld, ParticleTypes.FLASH, result.getPos(), 4, new Vec3d(0.7,1,0.7), 1f);

        //Shockwave
        //Shockwave.actualMovement(serverWorld, result.getPos().add(0,0.5,0), new Vec3d(0,1,0), 32, ParticleTypes.CLOUD, 1f);
       /* Shockwave.actualMovement(result.getPos().add(0,0.1,0), new Vec3d(0,1,0), 32, (pos, dir) -> {
            float distribution = 0.1f;
            dir = dir.add(ThreadLocalRandom.current().nextFloat(-distribution,distribution), ThreadLocalRandom.current().nextFloat(-distribution, distribution), ThreadLocalRandom.current().nextFloat(-distribution, distribution));
            ParticleHelper.spawnParticle(serverWorld, ParticleTypes.CLOUD, pos, 0, dir,1f);
        });*/
        FancyExplosion.shockwave(serverWorld, pos, nor, 1);
        //Shockwave.sphereActualMovement(serverWorld, result.getPos().add(0,0.1,0), ParticleTypes.CAMPFIRE_COSY_SMOKE, 1f);
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return Items.BLAZE_ROD;
    }
}

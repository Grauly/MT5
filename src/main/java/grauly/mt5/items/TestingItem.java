package grauly.mt5.items;

import eu.pb4.polymer.core.api.item.PolymerItem;
import grauly.mt5.effects.explosion.ExplosionEffects;
import grauly.mt5.effects.explosion.HEExplosion;
import grauly.mt5.helpers.RaycastHelper;
import grauly.mt5.registers.ModSchedulers;
import grauly.mt5.scheduler.SingleRunLaterLambdaTask;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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

public class TestingItem extends Item implements PolymerItem {
    public TestingItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!(world instanceof ServerWorld serverWorld)) return TypedActionResult.success(user.getStackInHand(hand));

        BlockHitResult result = RaycastHelper.rayCastBlock(serverWorld, user.getEyePos(), user.getRotationVector(), 50f);
        if (result.getType() == HitResult.Type.MISS) return TypedActionResult.success(user.getStackInHand(hand));

        Vec3d pos = result.getPos();
        Vec3d nor = user.getEyePos().subtract(result.getPos()).normalize();

        if (!user.isSneaking()) {
            //user.sendMessage(Text.of(String.valueOf(world.getBlockState(result.getBlockPos()).getBlock().getBlastResistance())));
            //ExplosionEffects.parametricFragments(serverWorld, pos, new Vec3d(0, 1, 0), 512, 15, 15);
            //ExplosionEffects.fancyBloom(serverWorld, pos, nor, 30);
            for (int i = 1; i < 10; i++) {
                int finalI = i;
                new SingleRunLaterLambdaTask(() -> {
                    user.sendMessage(Text.of("[" + finalI + "/9]: " + String.valueOf(finalI * 3)));
                    ExplosionEffects.parametrizedShockwave(serverWorld, pos, nor, finalI * 3, 2);
                    return 0;
                }).startTask(ModSchedulers.MAIN, finalI * 2* 20, 1);

            }
            //ExplosionEffects.parametrizedDebrisBloom(serverWorld, pos, new Vec3d(0, 1, 0), 30, Blocks.SAND.getDefaultState(), 5, 3);
            //ExplosionEffects.burst(serverWorld, pos, new Vec3d(0,1,0), 150);
            //ExplosionEffects.parametrizedBurst(serverWorld, pos, new Vec3d(0,1,0), 24, 5,150);
        } else {
            new HEExplosion(4, 3, result.getPos(), new Vec3d(0, 1, 0), serverWorld).setOff();
            //ExplosionEffects.fragments(serverWorld, pos, new Vec3d(0, 1, 0), 512);
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return Items.BLAZE_ROD;
    }
}

package grauly.mt5.helpers;

import net.minecraft.block.Block;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class ShotHelper {
    /**
     * Raycasts to get multiple hit entities
     *
     * @param world          the world this raycast takes place in
     * @param start          the start location
     * @param direction      the direction
     * @param length         the length of the raycast
     * @param raySize        the leniancy of the ray, i.e. how much can I miss. given in absolute blocks
     * @param ignoreEntities a predicate to ignore certain entities. usually should contain the player that shot
     * @param ignoreBlocks   a predicate to ignore certain blocks, commonly non solid blocks
     * @return SingleShotResult containing the closest hit entity, and the BlockHitResult. Both may be null in case of a non hit.
     */
    public static SingleShotResult rayCast(World world, Vec3d start, Vec3d direction, float length, float raySize, Predicate<Entity> ignoreEntities, Predicate<Block> ignoreBlocks) {
        Vec3d end = start.add(direction.normalize().multiply(length));
        Box searchBox = new Box(start, end);
        float foundDistance = length * length;
        Entity foundEntity = null;
        Vec3d foundHitLocation = null;
        for (Entity currentEntity : world.getOtherEntities(null, searchBox, ignoreEntities)) {
            Box entityBoundingBox = currentEntity.getBoundingBox().expand(raySize);
            Optional<Vec3d> hit = entityBoundingBox.raycast(start, end);
            if (hit.isEmpty()) continue;
            Vec3d foundHit = hit.get();
            float foundHitDistance = (float) foundHit.squaredDistanceTo(start);
            if (foundHitDistance > foundDistance) continue;
            foundDistance = foundHitDistance;
            foundEntity = currentEntity;
            foundHitLocation = foundHit;
        }
        EntityHitResult entityHitResult = new EntityHitResult(foundEntity, foundHitLocation);
        BlockHitResult blockHitResult = world.raycast(new RaycastContext(start, end, RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, ShapeContext.absent()));
        return new SingleShotResult(entityHitResult, blockHitResult);
    }

    /**
     * Raycasts to get multiple hit entities
     *
     * @param world          the world this raycast takes place in
     * @param start          the start location
     * @param direction      the direction
     * @param length         the length of the raycast
     * @param raySize        the leniancy of the ray, i.e. how much can I miss. given in absolute blocks
     * @param ignoreEntities a predicate to ignore certain entities. usually should contain the player that shot
     * @param ignoreBlocks   a predicate to ignore certain blocks, commonly non solid blocks
     * @return MultiShotResult containing a list of all hit entities, sorted by distance (closest first), and a BlockHitResult. The List will never be null, but may be empty, the BlockHitResult may be null in case of a non hit
     */
    public static MultiShotResult rayCastPierce(World world, Vec3d start, Vec3d direction, float length, float raySize, Predicate<Entity> ignoreEntities, Predicate<Block> ignoreBlocks) {
        Vec3d end = start.add(direction.normalize().multiply(length));
        Box searchBox = new Box(start, end);
        ArrayList<SinglePierceResult> pieceResults = new ArrayList<>();
        for (Entity currentEntity : world.getOtherEntities(null, searchBox, ignoreEntities)) {
            Box entityBoundingBox = currentEntity.getBoundingBox().expand(raySize);
            Optional<Vec3d> hit = entityBoundingBox.raycast(start, end);
            if (hit.isEmpty()) continue;
            Vec3d foundHit = hit.get();
            float distance = (float) foundHit.squaredDistanceTo(start);
            pieceResults.add(new SinglePierceResult(new EntityHitResult(currentEntity, foundHit), distance));
        }
        ArrayList<EntityHitResult> actualResults = new ArrayList<>(pieceResults.stream().sorted().map(SinglePierceResult::hitEntity).toList());
        BlockHitResult blockHitResult = world.raycast(new RaycastContext(start, end, RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, ShapeContext.absent()));
        return new MultiShotResult(actualResults, blockHitResult);
    }


    public record MultiShotResult(List<EntityHitResult> hitEntities, BlockHitResult hitBlock) {
        public List<EntityHitResult> getHitsBeforeBlock(Vec3d origin) {
           if(hitBlock == null) return hitEntities;
           if(hitEntities.isEmpty()) return hitEntities;
           var distance = hitBlock.getPos().squaredDistanceTo(origin);
           ArrayList<EntityHitResult> relevantHits = new ArrayList<>();
           for (EntityHitResult hit : hitEntities) {
               if(hit.getPos().squaredDistanceTo(origin) > distance) continue;
               relevantHits.add(hit);
           }
           return relevantHits;
        }
    }

    public record SingleShotResult(EntityHitResult hitEntity, BlockHitResult hitBlock) {
        public HitResult getClosest(Vec3d origin) {
            if(hitBlock == null && hitEntity == null) return null;
            if(hitBlock == null) return hitEntity;
            if(hitEntity == null) return hitBlock;
            var blockDist = origin.squaredDistanceTo(hitBlock.getPos());
            var entityDist = origin.squaredDistanceTo(hitEntity.getPos());
            return blockDist > entityDist ? hitEntity : hitBlock;
        }
    }

    public record SinglePierceResult(EntityHitResult hitEntity, float distance) implements Comparable<SinglePierceResult> {

        @Override
        public int compareTo(@NotNull ShotHelper.SinglePierceResult o) {
            return o.distance > distance ? -1 : 1;
        }
    }
}

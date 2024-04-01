package grauly.mt5.helpers;

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

public class RaycastHelper {
    /**
     * Raycasts to get multiple hit entities
     *
     * @param world          the world this raycast takes place in
     * @param start          the start location
     * @param direction      the direction
     * @param length         the length of the raycast
     * @param raySize        the leniancy of the ray, i.e. how much can I miss. given in absolute blocks
     * @param ignoreEntities a predicate to ignore certain entities. usually should contain the player that shot
     * @return SingleShotResult containing the closest hit entity, and the BlockHitResult. Both may be null in case of a non hit.
     */
    public static SingleShotResult rayCast(World world, Vec3d start, Vec3d direction, float length, float raySize, Predicate<Entity> ignoreEntities) {
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
        BlockHitResult blockHitResult = rayCastBlock(world, start, direction, length);
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
     * @return MultiShotResult containing a list of all hit entities, sorted by distance (closest first), and a BlockHitResult. The List will never be null, but may be empty, the BlockHitResult may be null in case of a non hit
     */
    public static MultiShotResult rayCastPierce(World world, Vec3d start, Vec3d direction, float length, float raySize, Predicate<Entity> ignoreEntities) {
        Vec3d end = start.add(direction.normalize().multiply(length));
        ArrayList<EntityHitResult> actualResults = new ArrayList<>(pierceCastEntities(world, start, end, raySize, ignoreEntities).stream().sorted().map(SinglePierceResult::hitEntity).toList());
        BlockHitResult blockHitResult = rayCastBlock(world, start, direction, length);
        return new MultiShotResult(actualResults, blockHitResult);
    }

    /**
     * Raycasts the given world for blocks
     *
     * @param world     the world this raycast takes place in
     * @param start     the start location
     * @param direction the direction
     * @param length    the length of the raycast
     * @return BlockHitResult with the hit block
     */
    public static BlockHitResult rayCastBlock(World world, Vec3d start, Vec3d direction, float length) {
        Vec3d end = start.add(direction.normalize().multiply(length));
        return world.raycast(new RaycastContext(start, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, ShapeContext.absent()));
    }

    /**
     * Checks if a line of sight exists for a point and a direction
     *
     * @param world     the world this takes place in
     * @param start     the start location of the line
     * @param direction the direction of the line
     * @param length    the length of the line
     * @return true if the line is unobstructed
     */
    public static boolean hasCollisionLineOfSight(World world, Vec3d start, Vec3d direction, float length) {
        Vec3d end = start.add(direction.normalize().multiply(length));
        return hasCollisionLineOfSight(world, start, end);
    }

    /**
     * Checks if a line of sight exists for two given points in a world
     *
     * @param world the world this check takes place in
     * @param start the start position of the line
     * @param end   the end position of the line
     * @return true if the line is unobstructed;
     */
    public static boolean hasCollisionLineOfSight(World world, Vec3d start, Vec3d end) {
        return world.raycast(new RaycastContext(start, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, ShapeContext.absent())).getType() == HitResult.Type.MISS;
    }

    /**
     * Raycasts the given world for entities
     *
     * @param world          the world this raycast takes place in
     * @param start          the start location
     * @param direction      the direction
     * @param length         the length of the raycast
     * @param raySize        the leniancy of the ray, i.e. how much can I miss. given in absolute blocks
     * @param ignoreEntities a predicate to ignore certain entities. usually should contain the player that shot
     * @return a list of EntityHitResults, sorted by distance to start point, with the closest being first
     */
    public static List<EntityHitResult> rayCastEntities(World world, Vec3d start, Vec3d direction, float length, float raySize, Predicate<Entity> ignoreEntities) {
        return pierceCastEntities(world, start, start.add(direction.normalize().multiply(length)), raySize, ignoreEntities).stream().sorted().map(singlePierceResult -> singlePierceResult.hitEntity).toList();
    }

    /**
     * Raycasts the given world for entities
     *
     * @param world          the world this raycast takes place in
     * @param start          the start location
     * @param end            the end position
     * @param raySize        the leniancy of the ray, i.e. how much can I miss. given in absolute blocks
     * @param ignoreEntities a predicate to ignore certain entities. usually should contain the player that shot
     * @return a list of EntityHitResults, sorted by distance to start point, with the closest being first
     */
    public static List<EntityHitResult> rayCastEntities(World world, Vec3d start, Vec3d end, float raySize, Predicate<Entity> ignoreEntities) {
        return pierceCastEntities(world, start, end, raySize, ignoreEntities).stream().sorted().map(singlePierceResult -> singlePierceResult.hitEntity).toList();
    }

    /**
     * Raycasts the given world for entities
     *
     * @param world          the world this raycast takes place in
     * @param start          the start location
     * @param end            the end position
     * @param raySize        the leniancy of the ray, i.e. how much can I miss. given in absolute blocks
     * @param ignoreEntities a predicate to ignore certain entities. usually should contain the player that shot
     * @return a list of SinglePierceResults, in no guaranteed order
     */
    public static List<SinglePierceResult> pierceCastEntities(World world, Vec3d start, Vec3d end, float raySize, Predicate<Entity> ignoreEntities) {
        Box searchBox = new Box(start, end);
        ArrayList<SinglePierceResult> hits = new ArrayList<>();
        for (Entity currentEntity : world.getOtherEntities(null, searchBox, ignoreEntities)) {
            Box entityBoundingBoxAdjusted = currentEntity.getBoundingBox().expand(raySize);
            Optional<Vec3d> hit = entityBoundingBoxAdjusted.raycast(start, end);
            if (hit.isEmpty()) continue;
            Vec3d actualHit = hit.get();
            float distance = (float) actualHit.squaredDistanceTo(start);
            hits.add(new SinglePierceResult(new EntityHitResult(currentEntity, actualHit), distance));
        }
        return hits;
    }


    public record MultiShotResult(List<EntityHitResult> hitEntities, BlockHitResult hitBlock) {
        public List<EntityHitResult> getHitsBeforeBlock(Vec3d origin) {
            if (hitBlock == null || hitEntities.isEmpty()) return hitEntities;
            var distance = hitBlock.getPos().squaredDistanceTo(origin);
            return new ArrayList<>(hitEntities.stream().filter(hitEntity -> hitEntity.getPos().squaredDistanceTo(origin) > distance).toList());
        }
    }

    public record SingleShotResult(EntityHitResult hitEntity, BlockHitResult hitBlock) {
        public HitResult getClosest(Vec3d origin) {
            if (hitBlock == null && hitEntity.getEntity() == null) return null;
            if (hitBlock == null) return hitEntity;
            if (hitEntity.getEntity() == null) return hitBlock;
            var blockDist = origin.squaredDistanceTo(hitBlock.getPos());
            var entityDist = origin.squaredDistanceTo(hitEntity.getPos());
            return blockDist > entityDist ? hitEntity : hitBlock;
        }
    }

    public record SinglePierceResult(EntityHitResult hitEntity,
                                     float distance) implements Comparable<SinglePierceResult> {

        @Override
        public int compareTo(@NotNull RaycastHelper.SinglePierceResult o) {
            return o.distance > distance ? -1 : 1;
        }
    }
}

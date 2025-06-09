package dev.ctrlneo.fairutils.client.modules.content.tasktracking.objectives;

import com.google.gson.JsonObject;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.TaskObjective;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.event.TaskProgressChangedEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

/**
 * An objective to visit a specific location
 */
public class LocationObjective extends TaskObjective {
    private final Vec3d targetLocation;
    private final double requiredRadius;
    private double closestDistance = Double.MAX_VALUE;
    private boolean visited = false;

    private Vec3d _lastTargetLocation;
    private double _lastRequiredRadius, _lastClosestDistance;

    public LocationObjective(UUID parentTaskId, String description, Vec3d targetLocation, double requiredRadius) {
        super(parentTaskId, description);
        this.targetLocation = targetLocation;
        this.requiredRadius = requiredRadius;
    }

    public LocationObjective(UUID parentTaskId, String description, BlockPos targetBlock, double requiredRadius) {
        this(parentTaskId, description, new Vec3d(targetBlock.getX() + 0.5, targetBlock.getY(), targetBlock.getZ() + 0.5),
                requiredRadius);
    }

    @Override
    public void checkCondition(PlayerEntity player) {
        if (isCompleted())
            return;

        Vec3d playerPos = player.getPos();
        double distance = playerPos.distanceTo(targetLocation);

        // Update closest distance for progress tracking
        if (distance < closestDistance) {
            closestDistance = distance;
            invalidateProgressCache();
        }

        // Check if player is within required radius
        if (distance <= requiredRadius) {
            visited = true;
            setCompleted(true);
        }

        if (!_lastTargetLocation.equals(targetLocation) || _lastClosestDistance != closestDistance || _lastRequiredRadius != requiredRadius) {
            TaskProgressChangedEvent.EVENT.invoker().onCallback(getParentTask());
        }

        _lastRequiredRadius = requiredRadius;
        _lastClosestDistance = closestDistance;
        _lastTargetLocation = targetLocation;
    }

    @Override
    public float getProgressPercentage() {
        if (visited || isCompleted())
            return 100.0f;

        // Calculate progress based on distance (capped at 95% until actually reached)
        double initialDistance = Math.max(requiredRadius * 10, 100); // Reasonable starting distance
        double progress = 100.0 * (1.0 - (Math.min(closestDistance, initialDistance) / initialDistance));

        // Cap progress at 95% until location is actually reached
        return getCachedOrCalculateProgress((float) Math.min(95.0, progress));
    }

    @Override
    public Text getProgressText() {
        if (visited) {
            return Text.literal("Reached");
        } else {
            return Text.literal(String.format("%.1f blocks away", closestDistance));
        }
    }

    @Override
    public void addPropertiesToJson(JsonObject json) {
        super.addPropertiesToJson(json);
        json.addProperty("x", targetLocation.x);
        json.addProperty("y", targetLocation.y);
        json.addProperty("z", targetLocation.z);
        json.addProperty("radius", requiredRadius);
        json.addProperty("visited", visited);
        json.addProperty("closestDistance", closestDistance);
    }

    public Vec3d getTargetLocation() {
        return targetLocation;
    }

    public double getRequiredRadius() {
        return requiredRadius;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
        invalidateProgressCache();
    }

    public boolean isVisited() {
        return visited;
    }

    public void setClosestDistance(double closestDistance) {
        this.closestDistance = closestDistance;
        invalidateProgressCache();
    }

    public double getClosestDistance() {
        return closestDistance;
    }
}
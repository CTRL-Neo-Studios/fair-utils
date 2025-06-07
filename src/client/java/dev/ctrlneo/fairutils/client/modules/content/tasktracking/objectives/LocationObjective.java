package dev.ctrlneo.fairutils.client.modules.content.tasktracking.objectives;

import dev.ctrlneo.fairutils.client.modules.content.tasktracking.TaskObjective;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * An objective to visit a specific location
 */
public class LocationObjective extends TaskObjective {
    private final Vec3d targetLocation;
    private final double requiredRadius;
    private double closestDistance = Double.MAX_VALUE;
    private boolean visited = false;
    
    public LocationObjective(String description, Vec3d targetLocation, double requiredRadius) {
        super(description);
        this.targetLocation = targetLocation;
        this.requiredRadius = requiredRadius;
    }
    
    public LocationObjective(String description, BlockPos targetBlock, double requiredRadius) {
        this(description, new Vec3d(targetBlock.getX() + 0.5, targetBlock.getY(), targetBlock.getZ() + 0.5), requiredRadius);
    }
    
    @Override
    public void checkCondition(PlayerEntity player) {
        if (isCompleted()) return;
        
        Vec3d playerPos = player.getPos();
        double distance = playerPos.distanceTo(targetLocation);
        
        // Update closest distance for progress tracking
        if (distance < closestDistance) {
            closestDistance = distance;
        }
        
        // Check if player is within required radius
        if (distance <= requiredRadius) {
            visited = true;
            setCompleted(true);
        }
    }
    
    @Override
    public float getProgressPercentage() {
        if (visited) return 100.0f;
        
        // Calculate progress based on distance (capped at 95% until actually reached)
        double initialDistance = Math.max(requiredRadius * 10, 100); // Reasonable starting distance
        double progress = 100.0 * (1.0 - (Math.min(closestDistance, initialDistance) / initialDistance));
        
        // Cap progress at 95% until location is actually reached
        return (float) Math.min(95.0, progress);
    }
    
    @Override
    public Text getProgressText() {
        if (visited) {
            return Text.literal("Reached");
        } else {
            return Text.literal(String.format("%.1f blocks away", closestDistance));
        }
    }
    
    public Vec3d getTargetLocation() {
        return targetLocation;
    }
    
    public double getRequiredRadius() {
        return requiredRadius;
    }
} 
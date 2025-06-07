package dev.ctrlneo.fairutils.client.modules.content.damageIndicator.utility;

import dev.ctrlneo.fairutils.client.config.FairUtilsConfig;
import net.minecraft.util.math.Vec3d;

public class DamageIndicator {
    private final boolean hasOrigin;
    private final Vec3d damageOrigin;
    private final float damage;
    private final long creationTime;

    public DamageIndicator(boolean hasOrigin, Vec3d damageOrigin, float damage, long creationTime) {
        this.hasOrigin = hasOrigin;
        this.damageOrigin = damageOrigin;
        this.damage = damage;
        this.creationTime = creationTime;
    }

    public Vec3d getDamageOrigin() {
        return damageOrigin;
    }

    public float calculateAngle(Vec3d playerPosition, Vec3d playerLookDirection) {
        if (!hasOrigin || damageOrigin == null) return 0;

        // Calculate direction from player to damage source
        Vec3d damageDirection = damageOrigin.subtract(playerPosition).normalize();

        // Get player's forward direction (normalized)
        Vec3d playerForward = playerLookDirection.normalize();

        // Calculate angle - FLIPPED to fix left/right issue
        double playerAngle = Math.atan2(playerForward.x, playerForward.z);
        double damageAngle = Math.atan2(damageDirection.x, damageDirection.z);

        // Calculate relative angle - NEGATED to fix the mirroring
        double relativeAngle = -(damageAngle - playerAngle);

        // Convert to degrees and normalize to -180 to 180
        double relativeDegrees = Math.toDegrees(relativeAngle);
        while (relativeDegrees > 180) relativeDegrees -= 360;
        while (relativeDegrees < -180) relativeDegrees += 360;

        return (float) relativeDegrees;
    }

    public float getDamage() {
        return damage;
    }

    public boolean hasOrigin() {
        return hasOrigin;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public float getAlpha() {
        long age = System.currentTimeMillis() - creationTime;
        if (age < 500) return 1.0f; // Full opacity for first 0.5 seconds
        return Math.max(0, 1.0f - (age - 500) / (FairUtilsConfig.get().damageIndicatorDuration * 1000F)); // Fade out over next 1.5 seconds
    }
}

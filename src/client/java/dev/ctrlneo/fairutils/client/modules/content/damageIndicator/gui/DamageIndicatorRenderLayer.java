package dev.ctrlneo.fairutils.client.modules.content.damageIndicator.gui;

import dev.ctrlneo.fairutils.client.config.FairUtilsConfig;
import dev.ctrlneo.fairutils.client.modules.content.damageIndicator.utility.ColorUtility;
import dev.ctrlneo.fairutils.client.modules.content.damageIndicator.utility.DamageIndicator;
import dev.ctrlneo.fairutils.client.modules.content.damageIndicator.utility.DamageIndicatorManager;
import dev.ctrlneo.fairutils.client.utility.Reference;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;

public class DamageIndicatorRenderLayer implements IdentifiedLayer {

    private static final Identifier DAMAGE_INDICATOR_TEXTURE = Reference.of("textures/gui/damage_indicator.png");
    private static final Identifier DAMAGE_INDICATOR_RING_TEXTURE = Reference.of("textures/gui/damage_indicator_ring.png");

    @Override
    public Identifier id() {
        return Reference.of("damage_indicator_hud");
    }

    @Override
    public void render(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null || client.options.hudHidden) {
            return;
        }

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;
        int textureWidth = 100;

        // Get player's look direction
        Vec3d playerLookDirection = client.player.getRotationVector();

        for (DamageIndicator indicator : DamageIndicatorManager.INSTANCE.getIndicators()) {
            float angle = indicator.calculateAngle(client.player.getPos(), playerLookDirection);
            float radians = (float)Math.toRadians(angle);
            float radians2 = (float)Math.toRadians(angle - 90F);

            // DON'T add 180 to fix the upside down issue
            Quaternionf quaternionf = new Quaternionf().rotationZ(radians);

            int distanceFromCenter = FairUtilsConfig.get().damageIndicatorDistance;
            float indicatorX = getOffsettedX(centerX, distanceFromCenter, radians2), indicatorY = getOffsettedY(centerY, distanceFromCenter, radians2);

            if (indicator.hasOrigin()) {
                context.getMatrices().push();
                context.getMatrices().translate(indicatorX, indicatorY, 0);
                context.getMatrices().multiply(quaternionf);
                context.getMatrices().translate(-textureWidth / 2F, -textureWidth / 2F, 0);
                context.drawTexture(RenderLayer::getGuiTextured, DAMAGE_INDICATOR_TEXTURE, 0, 0, 0, 0, textureWidth, textureWidth, textureWidth, textureWidth, ColorUtility.rgbaToInt(1, 0, 0, indicator.getAlpha()));
                context.getMatrices().pop();
            } else {
                context.getMatrices().push();
                context.getMatrices().translate(-textureWidth / 2F, -textureWidth / 2F, 0);
                context.drawTexture(RenderLayer::getGuiTextured, DAMAGE_INDICATOR_RING_TEXTURE, centerX, centerY, 0, 0, textureWidth, textureWidth, textureWidth, textureWidth, ColorUtility.rgbaToInt(1, 0, 0, indicator.getAlpha()));
                context.getMatrices().pop();
            }

            if (FairUtilsConfig.get().showDamageIndicatorNumber) {
                context.getMatrices().push();
                context.getMatrices().translate(getOffsettedX(centerX, distanceFromCenter - 5, radians2), getOffsettedY(centerY, distanceFromCenter - 5, radians2), 0);
                context.getMatrices().multiply(quaternionf);
                context.getMatrices().translate(-String.valueOf((int) indicator.getDamage()).length() / 2F, -textureWidth / 2F, 0);

                context.drawText(client.textRenderer, String.valueOf((int) indicator.getDamage()), 0, 0, ColorUtility.rgbaToInt(1, 0, 0, indicator.getAlpha()), false);
                context.getMatrices().pop();
            }

        }
    }

    public float getOffsettedX(int centerX, int distanceFromCenter, float radians2) {
        return centerX + (distanceFromCenter * (float)Math.cos(radians2));
    }

    public float getOffsettedY(int centerY, int distanceFromCenter, float radians2) {
        return centerY + (distanceFromCenter * (float)Math.sin(radians2));
    }
}
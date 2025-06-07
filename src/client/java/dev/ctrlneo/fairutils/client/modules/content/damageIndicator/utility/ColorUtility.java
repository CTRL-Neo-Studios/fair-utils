package dev.ctrlneo.fairutils.client.modules.content.damageIndicator.utility;

public class ColorUtility {

    /**
     * Converts RGBA float values (0.0-1.0) to a packed ARGB integer (0xAARRGGBB)
     *
     * @param r Red component (0.0-1.0)
     * @param g Green component (0.0-1.0)
     * @param b Blue component (0.0-1.0)
     * @param a Alpha component (0.0-1.0)
     * @return Packed ARGB integer (0xAARRGGBB)
     */
    public static int rgbaToInt(float r, float g, float b, float a) {
        // Clamp values between 0 and 1
        r = Math.max(0.0f, Math.min(1.0f, r));
        g = Math.max(0.0f, Math.min(1.0f, g));
        b = Math.max(0.0f, Math.min(1.0f, b));
        a = Math.max(0.0f, Math.min(1.0f, a));

        // Convert to 0-255 range and shift into position
        int red = (int) (r * 255 + 0.5f) << 16;
        int green = (int) (g * 255 + 0.5f) << 8;
        int blue = (int) (b * 255 + 0.5f);
        int alpha = (int) (a * 255 + 0.5f) << 24;

        return alpha | red | green | blue;
    }

}

package dev.ctrlneo.fairutils.client.lib.ui.util;

import net.minecraft.util.Identifier;

public class TemplateHelper {
    public static String resolveTemplateReference(String templateName, Identifier dataAssetIdentifier) {
        return String.format("%s@%s", templateName, dataAssetIdentifier.toString());
    }
}

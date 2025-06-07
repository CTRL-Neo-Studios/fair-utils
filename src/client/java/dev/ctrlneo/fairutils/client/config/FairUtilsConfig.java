package dev.ctrlneo.fairutils.client.config;

import dev.ctrlneo.fairutils.client.FairUtilsClient;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.utility.TaskOverlayPosition;
import dev.ctrlneo.fairutils.client.utility.Reference;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.FloatFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class FairUtilsConfig {
        public static ConfigClassHandler<FairUtilsConfig> HANDLER = ConfigClassHandler
                        .<FairUtilsConfig>createBuilder(FairUtilsConfig.class)
                        .id(Reference.config())
                        .serializer(playerUtilsConfigConfigClassHandler -> GsonConfigSerializerBuilder
                                        .create(playerUtilsConfigConfigClassHandler)
                                        .setPath(FabricLoader.getInstance().getConfigDir()
                                                        .resolve(FairUtilsClient.CONFIG_FILE))
                                        .setJson5(true)
                                        .build())
                        .build();

        public static FairUtilsConfig get() {
                return HANDLER.instance();
        }

        @SerialEntry
        public boolean damageIndicatorEnabled = true;

        @SerialEntry
        public boolean showDamageIndicatorNumber = true;

        @SerialEntry
        public int damageIndicatorDistance = 20;

        @SerialEntry
        public float damageIndicatorDuration = 2F;

        @SerialEntry
        public boolean taskTrackingEnabled = true;

        @SerialEntry
        public boolean showTaskTrackingOverlay = true;

        @SerialEntry
        public TaskOverlayPosition taskTrackingPosition = TaskOverlayPosition.TOP_RIGHT;

        @SerialEntry
        public int maxVisibleTasks = 5;

        public static Screen createConfigScreen(Screen parent) {
                return YetAnotherConfigLib.createBuilder()
                                .title(Text.translatable("fairutils"))
                                .category(ConfigCategory.createBuilder()
                                                .name(Text.translatable("fairutils.modules.damage_indicator.name"))
                                                .option(Option.<Boolean>createBuilder()
                                                                .name(Text.literal("Module Enabled"))
                                                                .binding(true, () -> get().damageIndicatorEnabled,
                                                                                newVal -> {
                                                                                        get().damageIndicatorEnabled = newVal;
                                                                                        HANDLER.save();
                                                                                })
                                                                .controller(TickBoxControllerBuilder::create)
                                                                .build())
                                                .option(Option.<Boolean>createBuilder()
                                                                .name(Text.literal("Show Damage Number"))
                                                                .binding(true, () -> get().showDamageIndicatorNumber,
                                                                                newVal -> {
                                                                                        get().showDamageIndicatorNumber = newVal;
                                                                                        HANDLER.save();
                                                                                })
                                                                .controller(TickBoxControllerBuilder::create)
                                                                .build())
                                                .option(Option.<Integer>createBuilder()
                                                                .name(Text.literal(
                                                                                "Damage Indicator Distance from Center"))
                                                                .binding(20, () -> get().damageIndicatorDistance,
                                                                                newVal -> {
                                                                                        get().damageIndicatorDistance = newVal;
                                                                                        HANDLER.save();
                                                                                })
                                                                .controller(IntegerFieldControllerBuilder::create)
                                                                .build())
                                                .option(Option.<Float>createBuilder()
                                                                .name(Text.literal("Damage Indicator Show Duration"))
                                                                .binding(2F, () -> get().damageIndicatorDuration,
                                                                                newVal -> {
                                                                                        get().damageIndicatorDuration = newVal;
                                                                                        HANDLER.save();
                                                                                })
                                                                .controller(FloatFieldControllerBuilder::create)
                                                                .build())
                                                .build())
                                .category(ConfigCategory.createBuilder()
                                                .name(Text.literal("Task Tracking"))
                                                .option(Option.<Boolean>createBuilder()
                                                                .name(Text.literal("Module Enabled"))
                                                                .binding(true, () -> get().taskTrackingEnabled,
                                                                                newVal -> {
                                                                                        get().taskTrackingEnabled = newVal;
                                                                                        HANDLER.save();
                                                                                })
                                                                .controller(TickBoxControllerBuilder::create)
                                                                .build())
                                                .option(Option.<Boolean>createBuilder()
                                                                .name(Text.literal("Show Task Overlay"))
                                                                .binding(true, () -> get().showTaskTrackingOverlay,
                                                                                newVal -> {
                                                                                        get().showTaskTrackingOverlay = newVal;
                                                                                        HANDLER.save();
                                                                                })
                                                                .controller(TickBoxControllerBuilder::create)
                                                                .build())
                                                .option(Option.<TaskOverlayPosition>createBuilder()
                                                                .name(Text.literal("Task Overlay Position"))
                                                                .binding(TaskOverlayPosition.TOP_RIGHT,
                                                                                () -> get().taskTrackingPosition,
                                                                                newVal -> {
                                                                                        get().taskTrackingPosition = newVal;
                                                                                        HANDLER.save();
                                                                                })
                                                                .controller(EnumControllerBuilder::create)
                                                                .build())
                                                .option(Option.<Integer>createBuilder()
                                                                .name(Text.literal("Maximum Visible Tasks"))
                                                                .binding(5, () -> get().maxVisibleTasks, newVal -> {
                                                                        get().maxVisibleTasks = newVal;
                                                                        HANDLER.save();
                                                                })
                                                                .controller(IntegerFieldControllerBuilder::create)
                                                                .build())
                                                .build())
                                .build()
                                .generateScreen(parent);
        }
}

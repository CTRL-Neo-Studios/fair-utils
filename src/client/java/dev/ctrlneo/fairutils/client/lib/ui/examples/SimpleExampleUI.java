package dev.ctrlneo.fairutils.client.lib.ui.examples;

import dev.ctrlneo.fairutils.client.lib.ui.Style;
import dev.ctrlneo.fairutils.client.lib.ui.UIManager;
import dev.ctrlneo.fairutils.client.lib.ui.components.Button;
import dev.ctrlneo.fairutils.client.lib.ui.components.Container;
import dev.ctrlneo.fairutils.client.lib.ui.components.Label;
import dev.ctrlneo.fairutils.client.lib.ui.components.TextInput;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

/**
 * A simple example UI to demonstrate basic component usage
 */
public class SimpleExampleUI {

    /**
     * Open the simple example UI
     */
    public static void open() {
        // Create the root container
        Container root = new Container();
        root.setStyle(new Style()
                .padding(20)
                .flexColumn()
                .gap(10));

        // Add a title
        Label title = new Label("Simple Example UI");
        title.setColor(0xFFFFFF00);
        title.setShadow(true);
        root.add(title);

        // Add a description
        Label description = new Label("This is a simple example of the UI library");
        description.setColor(0xFFFFFFFF);
        root.add(description);

        // Add a text input
        Label inputLabel = new Label("Enter your name:");
        inputLabel.setColor(0xFFFFFFFF);
        root.add(inputLabel);

        TextInput nameInput = new TextInput();
        nameInput.setText("Player");
        nameInput.setMaxLength(16);
        root.add(nameInput);

        // Add buttons
        Container buttonsContainer = new Container();
        buttonsContainer.setStyle(new Style()
                .gap(10)
                .justifyContent(Style.JustifyContent.CENTER));

        Button okButton = new Button("OK");
        okButton.setSize(100, 20);
        okButton.onClick(button -> {
            MinecraftClient.getInstance().player.sendMessage(
                    Text.literal("Hello, " + nameInput.getText() + "!"),
                    false);
            MinecraftClient.getInstance().setScreen(null);
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setSize(100, 20);
        cancelButton.onClick(button -> MinecraftClient.getInstance().setScreen(null));

        buttonsContainer.add(okButton);
        buttonsContainer.add(cancelButton);

        root.add(buttonsContainer);

        // Open the UI
        UIManager.getInstance().openScreen("Simple Example", root);
    }
}
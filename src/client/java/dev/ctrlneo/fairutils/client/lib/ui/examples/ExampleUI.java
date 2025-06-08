package dev.ctrlneo.fairutils.client.lib.ui.examples;

import dev.ctrlneo.fairutils.client.lib.ui.Style;
import dev.ctrlneo.fairutils.client.lib.ui.UIManager;
import dev.ctrlneo.fairutils.client.lib.ui.components.Button;
import dev.ctrlneo.fairutils.client.lib.ui.components.Container;
import dev.ctrlneo.fairutils.client.lib.ui.components.Dropdown;
import dev.ctrlneo.fairutils.client.lib.ui.components.ItemRenderer;
import dev.ctrlneo.fairutils.client.lib.ui.components.Label;
import dev.ctrlneo.fairutils.client.lib.ui.components.ScrollPanel;
import dev.ctrlneo.fairutils.client.lib.ui.components.TextInput;
import dev.ctrlneo.fairutils.client.lib.ui.components.Tooltip;
import dev.ctrlneo.fairutils.client.lib.ui.reactive.Ref;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

/**
 * An example UI that demonstrates how to use the GUI library
 */
public class ExampleUI {

    /**
     * Open the example UI
     */
    public static void open() {
        // Create reactive variables for the UI
        Ref<String> nameValue = Ref.of("Player");
        Ref<String> selectedOption = Ref.of("Option 1");

        // Create the root container
        Container root = new Container();
        root.setStyle(new Style()
                .padding(20)
                .flexColumn()
                .gap(10));

        // Add a title label
        Label titleLabel = new Label("Fair Utils Example UI");
        titleLabel.setStyle(new Style()
                .flexGrow(0)
                .alignItems(Style.AlignItems.CENTER));
        titleLabel.setColor(0xFFFFFF00)
                .setShadow(true);

        root.add(titleLabel);

        // Create a form container
        Container form = new Container();
        form.setStyle(new Style()
                .padding(10)
                .gap(5)
                .flexColumn());

        // Add a name field with label
        Container nameField = new Container();
        nameField.setStyle(new Style()
                .flexGrow(0)
                .gap(5));

        Label nameLabel = new Label("Name:")
                .setColor(0xFFFFFFFF);

        nameField.add(nameLabel);

        TextInput nameInput = new TextInput()
                .setText(nameValue.value())
                .setMaxLength(16)
                .onChange(nameValue::setValue);

        nameField.add(nameInput);
        form.add(nameField);

        // Add a dropdown
        Container dropdownField = new Container();
        dropdownField.setStyle(new Style()
                .flexGrow(0)
                .gap(5));

        Label optionsLabel = new Label("Options:");
        optionsLabel.setColor(0xFFFFFFFF);

        dropdownField.add(optionsLabel);

        Dropdown<String> dropdown = new Dropdown<String>()
                .addOption("Option 1", "Option 1")
                .addOption("Option 2", "Option 2")
                .addOption("Option 3", "Option 3")
                .setSelected(selectedOption.value())
                .onSelect(selectedOption::setValue);

        dropdownField.add(dropdown);
        form.add(dropdownField);

        // Add the form to the root container
        root.add(form);

        // Create a scrollable content area with items
        Container itemsContainer = new Container();
        itemsContainer.setStyle(new Style()
                .padding(5)
                .gap(10)
                .flexWrap());

        // Add some example items
        for (int i = 0; i < 20; i++) {
            ItemStack itemStack = null;

            switch (i % 5) {
                case 0:
                    itemStack = new ItemStack(Items.DIAMOND_SWORD);
                    break;
                case 1:
                    itemStack = new ItemStack(Items.GOLDEN_APPLE);
                    break;
                case 2:
                    itemStack = new ItemStack(Items.ENDER_PEARL);
                    break;
                case 3:
                    itemStack = new ItemStack(Items.NETHERITE_HELMET);
                    break;
                case 4:
                    itemStack = new ItemStack(Items.ENCHANTED_BOOK);
                    break;
            }

            if (itemStack != null) {
                ItemRenderer itemRenderer = new ItemRenderer(itemStack)
                        .setScale(2.0f);

                // Create a tooltip for the item
                Tooltip tooltip = new Tooltip(itemStack.getName())
                        .addLine("Click to select");

                // Create a container for the item with spacing
                Container itemContainer = new Container();
                itemContainer.setStyle(new Style()
                        .padding(5)
                        .width(40)
                        .height(40)
                        .alignItems(Style.AlignItems.CENTER)
                        .justifyContent(Style.JustifyContent.CENTER));

                itemContainer.add(itemRenderer);

                // Add the item to the container
                itemsContainer.add(itemContainer);
            }
        }

        // Create a scroll panel for the items
        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.setContent(itemsContainer);
        scrollPanel.setSize(0, 200);
        scrollPanel.setStyle(new Style()
                .widthFill()
                .flexGrow(1));

        // Add the scroll panel to the root container
        root.add(scrollPanel);

        // Create buttons container
        Container buttonsContainer = new Container();
        buttonsContainer.setStyle(new Style()
                .flexGrow(0)
                .gap(10)
                .justifyContent(Style.JustifyContent.CENTER));

        // Add a save button
        Button saveButton = new Button("Save");
        saveButton.setSize(100, 20);
        saveButton.onClick(button -> {
            MinecraftClient.getInstance().player.sendMessage(
                    Text.literal("Saved! Name: " + nameValue.value() + ", Option: " + selectedOption.value()),
                    false);
            MinecraftClient.getInstance().setScreen(null);
        });

        // Add a cancel button
        Button cancelButton = new Button("Cancel");
        cancelButton.setSize(100, 20);
        cancelButton.onClick(button -> MinecraftClient.getInstance().setScreen(null));

        // Add buttons to the container
        buttonsContainer.add(saveButton);
        buttonsContainer.add(cancelButton);

        // Add the buttons container to the root container
        root.add(buttonsContainer);

        // Open the UI
        UIManager.getInstance().openScreen("Example UI", root);
    }
}
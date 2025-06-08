# Fair Utils GUI Library

A minimalist, reactive UI library for Minecraft Fabric mods, inspired by modern web frameworks like Vue and TailwindCSS.

## Features

- **Flexible Layout System**: Use containers with flex-like layout options including direction, wrap, justification, and alignment
- **Reactive Data**: Bind UI components to reactive data that updates automatically when values change
- **Styled Components**: Comprehensive styling system with padding, margin, sizing, and positioning
- **Rich Component Library**: Built-in components like buttons, text inputs, dropdowns, labels, and more
- **Scrollable Containers**: Support for scrollable content areas
- **Item Rendering**: Specialized components for displaying Minecraft items with tooltips
- **Animation Support**: Framework for smooth animations using Minecraft's matrix transforms

## Components

- `Container`: A layout component that can hold other components with flexible arrangements
- `Button`: A clickable button with hover and press states
- `Label`: A text display component with alignment and styling options
- `TextInput`: An editable text field with validation and change events
- `Dropdown`: A dropdown selection component
- `ScrollPanel`: A scrollable container for content that exceeds available space
- `ItemRenderer`: A component for displaying Minecraft items with optional tooltips
- `Tooltip`: A tooltip component that appears when hovering over elements

## Usage

### Basic Example

```java
// Create a container
Container container = new Container();
container.setStyle(new Style()
    .padding(20)
    .flexColumn()
    .gap(10));

// Add a label
Label label = new Label("Hello, World!");
label.setColor(0xFFFFFFFF);
container.add(label);

// Add a button
Button button = new Button("Click Me");
button.onClick(btn -> {
    // Button click handler
    MinecraftClient.getInstance().player.sendMessage(Text.literal("Button clicked!"), false);
});
container.add(button);

// Open the UI
UIManager.getInstance().openScreen("My UI", container);
```

### Reactive Data

```java
// Create a reactive variable
Ref<String> nameValue = Ref.of("Player");

// Create a text input bound to the variable
TextInput nameInput = new TextInput();
nameInput.setText(nameValue.value());
nameInput.onChange(nameValue::setValue);

// Create a label that updates when the variable changes
Label nameLabel = new Label("");
nameValue.watch(value -> nameLabel.setText("Hello, " + value + "!"));
```

### Layout Example

```java
// Create a flex container with horizontal layout
Container row = new Container();
row.setStyle(new Style()
    .gap(10)
    .justifyContent(Style.JustifyContent.SPACE_BETWEEN)
    .alignItems(Style.AlignItems.CENTER));

// Add some components
row.add(new Label("Left"));
row.add(new Button("Middle"));
row.add(new Label("Right"));
```

## Styling

The `Style` class provides a fluent API for styling components:

```java
Style style = new Style()
    .padding(10)
    .margin(5, 10, 5, 10)
    .width(200)
    .height(30)
    .flexGrow(1)
    .flexColumn()
    .justifyContent(Style.JustifyContent.CENTER)
    .alignItems(Style.AlignItems.CENTER);
```

## Integration

To use this library in your mod:

1. Import the necessary classes
2. Create your UI components
3. Use the `UIManager` to open screens with your components

See the `SimpleExampleUI` class for a complete example of building a UI.

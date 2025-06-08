package dev.ctrlneo.fairutils.client.lib.ui;

import dev.ctrlneo.fairutils.client.lib.ui.screens.BaseScreen;
import net.minecraft.client.MinecraftClient;

import java.util.Objects;
import java.util.Stack;

public class UIManager {
    private static final Stack<BaseScreen> screens = new Stack<>();

    public static BaseScreen to(BaseScreen screen) {
        if (Objects.equals(getCurrent(), screen))
            return render(getCurrent());

        return render(screens.push(screen)); // Runs push() method first then returns the pushed stack result.
    }

    public static BaseScreen back() {
        BaseScreen screen = screens.pop();
        System.out.println(screen.getClass().getSimpleName() + " has been popped from the stack.");
        return render(screens.peek()); // Runs pop() method first then returns the popped stack result.
    }

    public static BaseScreen home() {
        screens.clear();
        return to(new BaseScreen());
    }

    public static BaseScreen render(BaseScreen screen){
        MinecraftClient.getInstance().setScreen(screen);
        if (screens.size() <= 0) {
//            MinecraftClient.getInstance().setScreen(new MainMenuScreen());
            return null;
        }
        return screen;
    }

    public static BaseScreen refresh() {
        return render(screens.peek());
    }

    public static void clear() {
        screens.clear();
    }

    public static void exit(){
        clear();
        MinecraftClient.getInstance().setScreen(null);
    }

    public static BaseScreen getCurrent() {
        if (screens.size() <= 0) return null;
        return screens.peek();
    }
}

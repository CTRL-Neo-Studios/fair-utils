package dev.ctrlneo.fairutils.client.modules.content.tasktracking.objectives;

import com.google.gson.JsonObject;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.TaskObjective;
import dev.ctrlneo.fairutils.client.modules.content.tasktracking.event.TaskProgressChangedEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

import java.util.UUID;

/**
 * An objective to collect a specific item type
 */
public class ItemCollectionObjective extends TaskObjective {
    private final Item targetItem;
    private final int targetAmount;
    private int currentAmount;

    public ItemCollectionObjective(UUID parentTaskId, String description, Item targetItem, int targetAmount) {
        super(parentTaskId, description);
        this.targetItem = targetItem;
        this.targetAmount = targetAmount;
        this.currentAmount = 0;
    }

    @Override
    public void checkCondition(PlayerEntity player) {
        if (isCompleted())
            return;

        int count = 0;
        for (ItemStack stack : player.getInventory().getMainStacks()) {
            if (!stack.isEmpty() && stack.isOf(targetItem)) {
                count += stack.getCount();
            }
        }

        // Also check offhand slot
        ItemStack offhand = player.getOffHandStack();
        if (!offhand.isEmpty() && offhand.isOf(targetItem)) {
            count += offhand.getCount();
        }

        // Only invalidate cache if the amount changed
        if (currentAmount != count) {
            currentAmount = count;
            invalidateProgressCache();
        }

        if (currentAmount >= targetAmount) {
            setCompleted(true);
        }

//        checkIfProgressChanged();
    }

    @Override
    public float getProgressPercentage() {
        if (isCompleted())
            return 100.0f;

        float progress = Math.min(100.0f, ((float) currentAmount / targetAmount) * 100.0f);
        return getCachedOrCalculateProgress(progress);
    }

    @Override
    public Text getProgressText() {
        return Text.literal(currentAmount + "/" + (targetAmount <= 0 ? "-" : targetAmount));
    }

    @Override
    public void addPropertiesToJson(JsonObject json) {
        super.addPropertiesToJson(json);
        json.addProperty("itemId", Registries.ITEM.getId(targetItem).toString());
        json.addProperty("targetAmount", targetAmount);
        json.addProperty("currentAmount", currentAmount);
    }

    public Item getTargetItem() {
        return targetItem;
    }

    public int getTargetAmount() {
        return targetAmount;
    }

    public int getCurrentAmount() {
        return currentAmount;
    }

    /**
     * Set the current amount for serialization purposes
     */
    public void setCurrentAmount(int currentAmount) {
        this.currentAmount = currentAmount;
        invalidateProgressCache();
    }
}
package dev.ctrlneo.fairutils.client.modules.content.tasktracking.objectives;

import dev.ctrlneo.fairutils.client.modules.content.tasktracking.TaskObjective;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

/**
 * An objective to collect a specific item type
 */
public class ItemCollectionObjective extends TaskObjective {
    private final Item targetItem;
    private final int targetAmount;
    private int currentAmount;
    
    public ItemCollectionObjective(String description, Item targetItem, int targetAmount) {
        super(description);
        this.targetItem = targetItem;
        this.targetAmount = targetAmount;
        this.currentAmount = 0;
    }
    
    @Override
    public void checkCondition(PlayerEntity player) {
        if (isCompleted()) return;
        
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
        
        currentAmount = count;
        
        if (currentAmount >= targetAmount) {
            setCompleted(true);
        }
    }
    
    @Override
    public float getProgressPercentage() {
        return Math.min(100.0f, ((float) currentAmount / targetAmount) * 100.0f);
    }
    
    @Override
    public Text getProgressText() {
        return Text.literal(currentAmount + "/" + targetAmount);
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
} 
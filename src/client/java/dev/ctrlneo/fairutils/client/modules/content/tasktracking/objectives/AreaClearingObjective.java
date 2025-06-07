package dev.ctrlneo.fairutils.client.modules.content.tasktracking.objectives;

import dev.ctrlneo.fairutils.client.modules.content.tasktracking.TaskObjective;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

/**
 * An objective to clear an area (mine blocks within a defined region)
 */
public class AreaClearingObjective extends TaskObjective {
    private final BlockPos startPos;
    private final BlockPos endPos;
    private final Set<Block> targetBlocks;
    private final boolean requireSpecificBlocks;

    private int totalBlocksToMine = 0;
    private int blocksRemaining = 0;
    private boolean initialized = false;
    private final Set<BlockPos> minedPositions = new HashSet<>();

    /**
     * Create an objective to clear all non-air blocks in an area
     */
    public AreaClearingObjective(String description, BlockPos startPos, BlockPos endPos) {
        super(description);
        this.startPos = startPos;
        this.endPos = endPos;
        this.targetBlocks = null;
        this.requireSpecificBlocks = false;
    }

    /**
     * Create an objective to clear specific blocks in an area
     */
    public AreaClearingObjective(String description, BlockPos startPos, BlockPos endPos, Set<Block> targetBlocks) {
        super(description);
        this.startPos = startPos;
        this.endPos = endPos;
        this.targetBlocks = targetBlocks;
        this.requireSpecificBlocks = true;
    }

    /**
     * Initialize the objective by counting blocks in the area
     */
    private void initialize(World world) {
        if (initialized)
            return;

        BlockPos minPos = new BlockPos(
                Math.min(startPos.getX(), endPos.getX()),
                Math.min(startPos.getY(), endPos.getY()),
                Math.min(startPos.getZ(), endPos.getZ()));

        BlockPos maxPos = new BlockPos(
                Math.max(startPos.getX(), endPos.getX()),
                Math.max(startPos.getY(), endPos.getY()),
                Math.max(startPos.getZ(), endPos.getZ()));

        totalBlocksToMine = 0;

        for (int x = minPos.getX(); x <= maxPos.getX(); x++) {
            for (int y = minPos.getY(); y <= maxPos.getY(); y++) {
                for (int z = minPos.getZ(); z <= maxPos.getZ(); z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    Block block = world.getBlockState(pos).getBlock();

                    if (requireSpecificBlocks) {
                        if (targetBlocks.contains(block)) {
                            totalBlocksToMine++;
                        }
                    } else if (!world.isAir(pos)) {
                        totalBlocksToMine++;
                    }
                }
            }
        }

        blocksRemaining = totalBlocksToMine;
        initialized = true;
    }

    @Override
    public void checkCondition(PlayerEntity player) {
        if (isCompleted())
            return;

        World world = player.getWorld();

        if (!initialized) {
            initialize(world);
        }

        // Create a bounding box and check blocks in the area
        BlockPos minPos = new BlockPos(
                Math.min(startPos.getX(), endPos.getX()),
                Math.min(startPos.getY(), endPos.getY()),
                Math.min(startPos.getZ(), endPos.getZ()));

        BlockPos maxPos = new BlockPos(
                Math.max(startPos.getX(), endPos.getX()),
                Math.max(startPos.getY(), endPos.getY()),
                Math.max(startPos.getZ(), endPos.getZ()));

        int currentBlocksRemaining = 0;

        for (int x = minPos.getX(); x <= maxPos.getX(); x++) {
            for (int y = minPos.getY(); y <= maxPos.getY(); y++) {
                for (int z = minPos.getZ(); z <= maxPos.getZ(); z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    Block block = world.getBlockState(pos).getBlock();

                    boolean isTargetBlock = requireSpecificBlocks
                            ? targetBlocks.contains(block)
                            : !world.isAir(pos);

                    if (isTargetBlock) {
                        currentBlocksRemaining++;
                    }
                }
            }
        }

        blocksRemaining = currentBlocksRemaining;

        if (blocksRemaining == 0) {
            setCompleted(true);
        }
    }

    @Override
    public float getProgressPercentage() {
        if (totalBlocksToMine == 0)
            return 0.0f;
        return ((float) (totalBlocksToMine - blocksRemaining) / totalBlocksToMine) * 100.0f;
    }

    @Override
    public Text getProgressText() {
        return Text.literal((totalBlocksToMine - blocksRemaining) + "/" + totalBlocksToMine);
    }

    public BlockPos getStartPos() {
        return startPos;
    }

    public BlockPos getEndPos() {
        return endPos;
    }

    /**
     * Get the box representing the area to clear
     */
    public Box getAreaBox() {
        return new Box(startPos.getX(), startPos.getY(), startPos.getZ(),
                endPos.getX() + 1, endPos.getY() + 1, endPos.getZ() + 1);
    }
}
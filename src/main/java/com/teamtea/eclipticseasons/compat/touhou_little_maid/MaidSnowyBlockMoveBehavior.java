package com.teamtea.eclipticseasons.compat.touhou_little_maid;

import com.github.tartaricacid.touhoulittlemaid.api.task.IFarmTask;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task.MaidMoveToBlockTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MaidSnowyBlockMoveBehavior extends MaidMoveToBlockTask {
    private final IFarmTask task;
    private BlockPos currentWorkPos = null;
    private int verticalSearchRange = 2;
    private final float movementSpeed;

    public MaidSnowyBlockMoveBehavior(IFarmTask task, float movementSpeed) {
        super(movementSpeed, 2);
        this.task = task;
        this.movementSpeed = movementSpeed;
    }

    @Override
    protected void start(ServerLevel worldIn, EntityMaid entityIn, long gameTimeIn) {
        this.searchForDestination2(worldIn, entityIn);
    }

    private BlockPos getWorkSearchPos(EntityMaid maid) {
        if (maid.hasRestriction()) {
            return this.currentWorkPos != null
                    && maid.isWithinRestriction(this.currentWorkPos) ?
                    this.currentWorkPos : maid.getRestrictCenter();
        } else {
            return maid.getOnPos();
        }
    }

    private boolean checkOwnerPos(EntityMaid maid, BlockPos mutableBlockPos) {
        if (maid.isHomeModeEnable()) {
            return true;
        } else {
            return maid.getOwner() != null
                    && mutableBlockPos.closerToCenterThan(maid.getOwner().position(), 8.0);
        }
    }

    protected final void searchForDestination2(ServerLevel worldIn, EntityMaid maid) {
        BlockPos centrePos = this.getWorkSearchPos(maid);
        int searchRange = (int) maid.getRestrictRadius();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

        for (int layer = 0; layer <= searchRange; layer++) {
            for (int x = -layer; x <= layer; x++) {
                for (int z = -layer; z <= layer; z++) {
                    if (Math.abs(x) < layer && Math.abs(z) < layer) continue;
                    for (int y : getys()) {
                        mutableBlockPos.setWithOffset(centrePos, x, y, z);
                        if (maid.isWithinRestriction(mutableBlockPos)
                                && this.shouldMoveTo(worldIn, maid, mutableBlockPos)
                                && this.checkPathReach(maid, mutableBlockPos)
                                && this.checkOwnerPos(maid, mutableBlockPos)) {
                            BehaviorUtils.setWalkAndLookTargetMemories(maid, mutableBlockPos, this.movementSpeed, 0);
                            maid.getBrain().setMemory((MemoryModuleType) InitEntities.TARGET_POS.get(), new BlockPosTracker(mutableBlockPos));
                            this.currentWorkPos = mutableBlockPos;
                            this.setNextCheckTickCount(5);
                            // ChatBubbleManger.addInnerChatText(maid, mutableBlockPos.toString() + "****" + mutableBlockPos.distToCenterSqr(maid.id()));

                            return;
                        }
                    }
                }
            }
        }

        this.currentWorkPos = null;
    }

    private @NotNull List<Integer> getys() {
        List<Integer> yValues = new ArrayList<>();
        for (int y = 0; y <= verticalSearchRange; y++) {
            if (y != 0) {
                yValues.add(-y);
            }
            yValues.add(y);
        }
        return yValues;
    }

    @Override
    protected boolean shouldMoveTo(ServerLevel worldIn, EntityMaid maid, BlockPos basePos) {
        BlockPos cropPos = basePos;
        BlockState cropState = worldIn.getBlockState(cropPos);
        return task.canHarvest(maid, cropPos, cropState);
    }
}

package com.teamtea.eclipticseasons.compat.touhou_little_maid;

import com.github.tartaricacid.touhoulittlemaid.api.task.IFarmTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitSounds;
import com.github.tartaricacid.touhoulittlemaid.util.SoundUtil;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.network.SimpleNetworkHandler;
import com.teamtea.eclipticseasons.common.network.message.BroomUseMessage;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class CleanSnowTask implements IFarmTask {

    public final static Map<GlobalPos, Long> hasCleanedPos = new LinkedHashMap<>();

    @Override
    public @NotNull ResourceLocation getUid() {
        return EclipticSeasons.rl("clean_snow");
    }

    @Override
    public @NotNull ItemStack getIcon() {
        return ItemRegistry.broom.get().getDefaultInstance();
    }

    @Override
    public SoundEvent getAmbientSound(EntityMaid maid) {
        return SoundUtil.environmentSound(maid, InitSounds.MAID_REMOVE_SNOW.get(), 0.5f);
    }


    @Override
    public @NotNull List<Pair<Integer, BehaviorControl<? super EntityMaid>>> createBrainTasks(@NotNull EntityMaid entityMaid) {
        MaidSnowyBlockMoveBehavior snowyBlockMoveBehavior = new MaidSnowyBlockMoveBehavior(this, 0.6F);
        MaidCleanSnowBehavior maidFarmPlantTask = new MaidCleanSnowBehavior(this);
        return Lists.newArrayList(new Pair[]{Pair.of(5, snowyBlockMoveBehavior), Pair.of(6, maidFarmPlantTask)});
    }

    @Override
    public boolean isSeed(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canHarvest(EntityMaid maid, BlockPos cropPos, BlockState cropState) {
        if (!hasBroom(maid)) return false;

        Level level = maid.level();
        BlockState blockState;
        BlockPos below;
        blockState = cropState;
        below = cropPos;
        boolean snowyBlock = !blockState.is(BlockTags.DIRT)
                && EclipticSeasonsApi.getInstance().isSnowyBlock(level, blockState, below);
        if (snowyBlock) {
            // ChatBubbleManger.addInnerChatText(maid, cropPos.toString()+"****"+cropPos.distToCenterSqr(maid.position()));
        }
        return snowyBlock&&!hasCleanedPos.containsKey( GlobalPos.of(level.dimension(),cropPos));
    }

    @Override
    public void harvest(EntityMaid maid, BlockPos cropPos, BlockState cropState) {
        Level level = maid.level();
        BlockPos below;
        BlockState blockState;
        blockState = cropState;
        below = cropPos;
        hasCleanedPos.put(GlobalPos.of(level.dimension(), cropPos), level.getGameTime());
        // if (CommonConfig.Map.delayedUpdates.get()) {
        //     ServerMapFixer.addPlanner(level, blockState, blockState, below, level.getGameTime(), below.getY(), true);
        // } else
        {
            if (maid.getOwner() instanceof ServerPlayer serverPlayer) {
                SimpleNetworkHandler.send(serverPlayer, new BroomUseMessage(below, level.getGameTime()));
            }
        }
    }

    @Override
    public boolean canPlant(EntityMaid maid, BlockPos basePos, BlockState baseState, ItemStack seed) {
        return false;
    }

    @Override
    public ItemStack plant(EntityMaid maid, BlockPos basePos, BlockState baseState, ItemStack seed) {
        return seed;
    }

    public List<Pair<String, Predicate<EntityMaid>>> getConditionDescription(EntityMaid maid) {
        return Collections.singletonList(Pair.of("has_broom", this::hasBroom));
    }

    private boolean hasBroom(EntityMaid maid) {
        return maid.getItemInHand(InteractionHand.MAIN_HAND).is(ItemRegistry.broom.get());
    }
}

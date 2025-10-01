package com.teamtea.eclipticseasons.common.block;

import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class IceOrSnowCauldronBlock extends AbstractCauldronBlock {

    public IceOrSnowCauldronBlock(Properties properties) {
        super(properties, Map.of());
        registerDefaultState(defaultBlockState());
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        return Items.CAULDRON.getDefaultInstance();
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        Block block = state.getBlock();
        ItemStack stack = player.getItemInHand(hand);
        if (block == BlockRegistry.snow_cauldron.get() && stack.is(ItemTags.SHOVELS)) {
            return givePlayerResult(stack, new ItemStack(Items.SNOWBALL, 4), Blocks.SNOW.defaultBlockState(), level, pos, player);
        } else if (block == BlockRegistry.ice_cauldron.get() && stack.is(ItemTags.PICKAXES)) {
            return givePlayerResult(stack, new ItemStack(Items.ICE), Blocks.ICE.defaultBlockState(), level, pos, player);
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean isFull(BlockState pState) {
        return true;
    }


    protected static @NotNull InteractionResult givePlayerResult(ItemStack stack, ItemStack result, BlockState state, Level level, BlockPos pos, Player player) {
        if (!level.isClientSide) {
            ItemHandlerHelper.giveItemToPlayer(player, result);
            player.awardStat(Stats.USE_CAULDRON);
            player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
            level.setBlockAndUpdate(pos, Blocks.CAULDRON.defaultBlockState());
            level.playSound(null, pos, state.getSoundType(level, pos, player)
                    .getHitSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    public static void handleChange(BlockState state, Level level, BlockPos pos, Biome.Precipitation precipitation) {
        if (CommonConfig.Debug.disableIceOrSnowCauldron.get()) return;
        if (precipitation == Biome.Precipitation.SNOW) {
            BlockState blockstate = null;
            if (state.getBlock() == Blocks.POWDER_SNOW_CAULDRON) {
                blockstate = BlockRegistry.snow_cauldron.get().defaultBlockState();
            } else if (state.getBlock() == Blocks.WATER_CAULDRON) {
                blockstate = BlockRegistry.ice_cauldron.get().defaultBlockState();
            }
            if (blockstate != null) {
                level.setBlockAndUpdate(pos, blockstate);
                level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(blockstate));
            }
        }
    }

    public static void init() {
        CauldronInteraction.EMPTY
                .put(Items.SNOW_BLOCK, (state, level, pos, player, hand, stack) -> {
                    fillEmptyCauldron(level, pos, player, hand, stack, BlockRegistry.snow_cauldron.get().defaultBlockState(), SoundEvents.SNOW_PLACE);
                    return InteractionResult.sidedSuccess(level.isClientSide());
                });
        CauldronInteraction.EMPTY
                .put(Items.ICE, (state, level, pos, player, hand, stack) -> {
                    fillEmptyCauldron(level, pos, player, hand, stack, BlockRegistry.ice_cauldron.get().defaultBlockState(), SoundEvents.GLASS_PLACE);
                    return InteractionResult.sidedSuccess(level.isClientSide());
                });
    }

    protected static void fillEmptyCauldron(Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack filledStack, BlockState state, SoundEvent soundEvent) {
        if (CommonConfig.Debug.disableIceOrSnowCauldron.get()) return;
        if (!level.isClientSide) {
            if (!player.isCreative()) filledStack.shrink(1);
            player.awardStat(Stats.FILL_CAULDRON);
            player.awardStat(Stats.ITEM_USED.get(filledStack.getItem()));
            level.setBlockAndUpdate(pos, state);
            level.playSound(null, pos, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(null, GameEvent.BLOCK_PLACE, pos);
        }
    }

    public Component getTip() {
        if (this == BlockRegistry.snow_cauldron.get()) {
            return Component.translatable("info.eclipticseasons.snow_cauldron.extraction");
        } else if (this == BlockRegistry.ice_cauldron.get()) {
            return Component.translatable("info.eclipticseasons.ice_cauldron.extraction");
        }
        return Component.empty();
    }
}

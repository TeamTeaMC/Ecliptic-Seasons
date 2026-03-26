package com.teamtea.eclipticseasons.common.item;

import com.teamtea.eclipticseasons.common.block.QuestCeilingHangingSignBlock;
import com.teamtea.eclipticseasons.common.block.QuestWallHangingSignBlock;
import com.teamtea.eclipticseasons.common.block.blockentity.QuestHangingSignBlockEntity;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.common.registry.ParticleRegistry;
import com.teamtea.eclipticseasons.config.ClientConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SignApplicator;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;

import java.util.List;
import java.util.function.Consumer;

public class QuestSignChangeItem extends Item implements SignApplicator {
    public QuestSignChangeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return super.useOn(context);
    }


    @Override
    public boolean tryApplyToSign(Level level, SignBlockEntity sign, boolean isFront, ItemStack item, Player player) {
        BlockPos pos = sign.getBlockPos();
        BlockState blockState = sign.getBlockState();
        Block block = blockState.getBlock();
        boolean set = false;
        if (block instanceof CeilingHangingSignBlock ceilingHangingSignBlock
                && !(block instanceof QuestCeilingHangingSignBlock)) {
            BlockState blockState1 = BlockRegistry.season_quest_ceiling_hanging_sign.get().defaultBlockState();
            for (Property property : ceilingHangingSignBlock.getStateDefinition().getProperties()) {
                blockState1 = blockState1.setValue(property, blockState.getValue(property));
            }
            if (!level.isClientSide())
                level.setBlockAndUpdate(pos, blockState1);
            set = true;
        } else if (block instanceof WallHangingSignBlock wallHangingSignBlock
                && !(block instanceof QuestWallHangingSignBlock)) {
            BlockState blockState1 = BlockRegistry.season_quest_wall_hanging_sign.get().defaultBlockState();
            for (Property property : wallHangingSignBlock.getStateDefinition().getProperties()) {
                blockState1 = blockState1.setValue(property, blockState.getValue(property));
            }
            level.setBlockAndUpdate(pos, blockState1);
            set = true;
        }
        if (set
                && level.getBlockEntity(pos) instanceof QuestHangingSignBlockEntity questHangingSignBlockEntity
                && block instanceof SignBlock signBlock) {
            questHangingSignBlockEntity.setSignType(signBlock);
        }


        if (level instanceof ServerLevel serverLevel) {
            RandomSource random = level.getRandom();
            Direction direction = Direction.DOWN;

            ColorParticleOption colorParticleOption = ColorParticleOption.create(ParticleRegistry.GREENHOUSE, 1, 1, 1);

            for (int i = 0; i < 12; i++) {
                double d0 = pos.getX() + (random.nextDouble() - 0.5) + 0.5;
                double d1 = pos.getY() + (random.nextDouble() - 0.5) + 1;
                double d2 = pos.getZ() + (random.nextDouble() - 0.5) + 0.5;

                double d3 = (random.nextDouble() - 0.5) * 0.4;

                {
                    double x = d0 + direction.getStepX() * d3;
                    double y = d1 + direction.getStepY() * d3;
                    double z = d2 + direction.getStepZ() * d3;

                    if (level.isEmptyBlock(new BlockPos((int) x, (int) (y), (int) z))) {

                        serverLevel.sendParticles( colorParticleOption,false, false, x, y, z, 2, 0,
                                0.001 + random.nextDouble() * 0.02,
                                0, 0.035);
                    }
                }
            }
        }
        return true;
    }



    @Override
    public boolean canApplyToSign(SignText text, ItemStack item, Player player) {
        return !text.hasMessage(player);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
        if (FMLLoader.getCurrent().getDist() != Dist.CLIENT || !ClientConfig.GUI.itemInformation.get()) return;
        builder.accept(Component.translatable(
                "info.eclipticseasons.seasonal_prayer_scroll.use"
        ).withStyle(ChatFormatting.GRAY));
    }
}

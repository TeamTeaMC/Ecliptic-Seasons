package com.teamtea.eclipticseasons.common.block.blockentity;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.climate.AgroClimaticZone;
import com.teamtea.eclipticseasons.api.data.quest.SeasonQuest;
import com.teamtea.eclipticseasons.api.data.quest.WarpItemPredicate;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import com.teamtea.eclipticseasons.common.registry.BlockEntityRegistry;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuestHangingSignBlockEntity extends SignBlockEntity {
    public static final ModelProperty<SignBlock> SIGN_BLOCK_MODEL_PROPERTY = new ModelProperty<>();

    private SignBlock sign;
    private SeasonQuest seasonQuest;

    private int sleepTime = 0;

    public QuestHangingSignBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.season_quest_hanging_sign_entity_type.get(), pos, blockState);
    }

    public static void popSign(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof QuestHangingSignBlockEntity blockEntity
                && blockEntity.sign != null) {
            Block.popResource(level, pos, blockEntity.getSignType().asItem().getDefaultInstance());
        }
    }

    public static void removeSign(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof QuestHangingSignBlockEntity blockEntity) {
            blockEntity.setSignType(null);
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        RegistryOps<Tag> registryops = RegistryOps.create(NbtOps.INSTANCE, level.registryAccess());
        tag.putString("sign_type", BuiltInRegistries.BLOCK.getKey(getSignType()).toString());
        if (seasonQuest != null) {
            SeasonQuest.CODEC
                    .encodeStart(registryops, seasonQuest)
                    .resultOrPartial(EclipticSeasons::logger)
                    .ifPresent(tag1 -> tag.put("season_quest", tag1));
        }

    }


    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (tag.contains("sign_type")) {
            Block block = BuiltInRegistries.BLOCK.get(new ResourceLocation(tag.getString("sign_type")));
            if (block instanceof SignBlock signBlock)
                this.sign = signBlock;
        }
        if (tag.contains("season_quest")) {
            RegistryOps<Tag> registryops = RegistryOps.create(NbtOps.INSTANCE, level.registryAccess());
            SeasonQuest.CODEC
                    .parse(registryops, tag.get("season_quest"))
                    .resultOrPartial(EclipticSeasons::logger)
                    .ifPresent(seasonQuest1 -> {
                        this.seasonQuest = seasonQuest1;
                    });
        } else {
            seasonQuest = null;
        }

    }

    protected void inventoryChanged() {
        super.setChanged();
        if (level != null)
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    public void setSignType(SignBlock signType) {
        this.sign = signType;
        inventoryChanged();
    }

    public SignBlock getSignType() {
        return sign != null ? sign : (SignBlock) Blocks.OAK_WALL_HANGING_SIGN;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, QuestHangingSignBlockEntity sign) {
        if (!level.isClientSide()) {
            if (sign.getSeasonQuest() == null) {
                if (sign.sleepTime == 0) {
                    sign.createSeasonalQuest();
                    if (sign.getSeasonQuest() != null)
                        sign.setQuestText();
                    else sign.sleepTime = 100;
                } else {
                    sign.sleepTime -= 1;
                }
                sign.setChanged();
            } else {
                if (level.getRandom().nextInt(128) == 0) {
                    SolarTerm nowSolarTerm = EclipticUtil.getNowSolarTerm(level);
                    Holder<AgroClimaticZone> agroClimaticZoneHolder = CropGrowthHandler.getclimateTypeHolder(CropGrowthHandler.getCropBiome(level, pos));
                    if (isInvalidQuest(sign.getSeasonQuest(), nowSolarTerm, agroClimaticZoneHolder)) {
                        sign.resetQuest();
                    }
                }
            }
        }

    }

    private void setQuestText() {
        SeasonQuest signSeasonQuest = getSeasonQuest();
        if (signSeasonQuest != null) {
            SignText signText = new SignText();
            String title = signSeasonQuest.tittle().orElse("block.eclipticseasons.season_quest_ceiling_hanging_sign");
            signText = signText.setMessage(0, Component.translatable(title));

            if (signSeasonQuest.description().isPresent()) {
                for (int i = 0; i < signSeasonQuest.description().get().size() && i < 3; i++) {
                    signText = signText.setMessage(i + 1, Component.translatable(signSeasonQuest.description().get().get(i)));
                }
            } else {
                if (!signSeasonQuest.need().isEmpty()) {
                    for (int i = 0; i < signSeasonQuest.need().size() && i < 3; i++) {
                        WarpItemPredicate itemPredicate = signSeasonQuest.need().get(i);
                        HolderSet<Item> holders = itemPredicate.items();
                        if (holders.size() > 0) {
                            Item value = holders.get(0).value();
                            Component c = Component.translatable("eclipticseasons.season_quest.hint.item_count", value.getName(value.getDefaultInstance()), itemPredicate.count());
                            signText = signText.setMessage(i + 1, c);
                        }
                    }
                }
            }
            setText(signText, true);
        }

    }


    public void createSeasonalQuest() {
        level.registryAccess().registry(ESRegistries.SEASON_QUEST).ifPresent(
                seasonQuests -> {
                    SolarTerm nowSolarTerm = EclipticUtil.getNowSolarTerm(level);
                    Holder<AgroClimaticZone> agroClimaticZoneHolder = CropGrowthHandler.getclimateTypeHolder(CropGrowthHandler.getCropBiome(level, getBlockPos()));
                    List<SeasonQuest> seasonQuestList = new ArrayList<>();
                    int totalWeight = 0;
                    for (Map.Entry<ResourceKey<SeasonQuest>, SeasonQuest> entry : seasonQuests.entrySet()) {
                        SeasonQuest quest = entry.getValue();
                        if (isInvalidQuest(quest, nowSolarTerm, agroClimaticZoneHolder)) continue;
                        // this.seasonQuest = quest;
                        seasonQuestList.add(quest);
                        totalWeight += quest.weight().orElse(10);
                        // break;
                    }
                    if (totalWeight < 1) return;

                    int randWeight = level.getRandom().nextInt(totalWeight);

                    int currentWeight = 0;
                    for (SeasonQuest quest : seasonQuestList) {
                        currentWeight += quest.weight().orElse(1);
                        if (randWeight < currentWeight) {
                            this.seasonQuest = quest;
                        }
                    }
                }
        );
        if (seasonQuest == null) {
            {
                showLoadingText();
            }
        }
    }

    private static boolean isInvalidQuest(SeasonQuest quest, SolarTerm nowSolarTerm, Holder<AgroClimaticZone> agroClimaticZoneHolder) {
        if (quest.start().isPresent() != quest.end().isPresent()) return true;
        if (quest.start().isPresent() && !nowSolarTerm.isInTerms(quest.start().get(), quest.end().get()))
            return true;
        if (agroClimaticZoneHolder == null || quest.climate().isPresent() && !quest.climate().get().contains(agroClimaticZoneHolder))
            return true;
        return false;
    }

    public SeasonQuest getSeasonQuest() {
        return seasonQuest;
    }

    public void finishSeasonQuest(ServerPlayer player) {
        SeasonQuest quest = getSeasonQuest();
        if (quest != null) {
            boolean isOk = true;
            for (WarpItemPredicate itemPredicate : quest.need()) {
                int i = player.getInventory().clearOrCountMatchingItems(itemPredicate::test, 0, player.inventoryMenu.getCraftSlots());
                if (i < itemPredicate.count()) {
                    isOk = false;
                    break;
                }
            }
            if (isOk) {
                for (WarpItemPredicate itemPredicate : quest.need()) {
                    player.getInventory().clearOrCountMatchingItems(itemPredicate::test, itemPredicate.count(), player.inventoryMenu.getCraftSlots());
                }
                for (ItemStack stack : quest.award()) {
                    ItemHandlerHelper.giveItemToPlayer(player, stack.copy());
                }
                resetQuest();
            }
        }

    }

    private void resetQuest() {
        this.seasonQuest = null;
        sleepTime = 100;
        inventoryChanged();
        showLoadingText();
    }

    private void showLoadingText() {
        setText(new SignText().setMessage(1, Component.translatable("eclipticseasons.season_quest.hint.loading")), true);
    }

    @Override
    public int getMaxTextLineWidth() {
        return 400;
    }

    @Override
    public @NotNull ModelData getModelData() {
        return ModelData.builder().with(SIGN_BLOCK_MODEL_PROPERTY, getSignType()).build();
    }
}

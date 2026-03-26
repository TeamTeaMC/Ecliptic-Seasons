package com.teamtea.eclipticseasons.common.block.blockentity;

import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.climate.AgroClimaticZone;
import com.teamtea.eclipticseasons.api.data.misc.ESSortInfo;
import com.teamtea.eclipticseasons.api.data.quest.SeasonQuest;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import com.teamtea.eclipticseasons.common.registry.BlockEntityRegistry;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class QuestHangingSignBlockEntity extends SignBlockEntity {
    public static final ModelProperty<SignBlock> SIGN_BLOCK_MODEL_PROPERTY = new ModelProperty<>();

    private SignBlock sign;
    private SeasonQuest seasonQuest;

    private int sleepTime = 0;

    public QuestHangingSignBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.season_quest_hanging_sign_entity_type.get(), pos, blockState);
    }

    public void popSign(Level level, BlockPos pos) {
        if (this.sign != null) {
            Block.popResource(level, pos, this.getSignType().asItem().getDefaultInstance());
        }
    }

    public static void removeSign(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof QuestHangingSignBlockEntity blockEntity) {
            blockEntity.setSignType(null);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
    }

    //@Override
    //protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
    //    super.saveAdditional(tag, registries);
    //
    //    tag.putString("sign_type", BuiltInRegistries.BLOCK.getKey(getSignType()).toString());
    //    if (seasonQuest != null) {
    //        DynamicOps<Tag> dynamicops = registries.createSerializationContext(NbtOps.INSTANCE);
    //        SeasonQuest.CODEC
    //                .encodeStart(dynamicops, seasonQuest)
    //                .resultOrPartial(EclipticSeasons::logger)
    //                .ifPresent(tag1 -> tag.put("season_quest", tag1));
    //    }
    //}
    //
    //@Override
    //protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
    //    super.loadAdditional(tag, registries);
    //    if (tag.contains("sign_type")) {
    //        Block block = BuiltInRegistries.BLOCK.get(EclipticSeasons.parse(tag.getString("sign_type")));
    //        if (block instanceof SignBlock signBlock)
    //            this.sign = signBlock;
    //    }
    //    if (tag.contains("season_quest")) {
    //        DynamicOps<Tag> dynamicops = registries.createSerializationContext(NbtOps.INSTANCE);
    //        SeasonQuest.CODEC
    //                .parse(dynamicops, tag.get("season_quest"))
    //                .resultOrPartial(EclipticSeasons::logger)
    //                .ifPresent(seasonQuest1 -> {
    //                    this.seasonQuest = seasonQuest1;
    //                });
    //    } else {
    //        seasonQuest = null;
    //    }
    //
    //}

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
            Component title = signSeasonQuest.tittle().orElse(Component.translatable("block.eclipticseasons.season_quest_ceiling_hanging_sign"));
            signText = signText.setMessage(0, title);

            if (signSeasonQuest.description().isPresent()) {
                for (int i = 0; i < signSeasonQuest.description().get().size() && i < 3; i++) {
                    signText = signText.setMessage(i + 1, signSeasonQuest.description().get().get(i));
                }
            } else {
                if (!signSeasonQuest.need().isEmpty()) {
                    for (int i = 0; i < signSeasonQuest.need().size() && i < 3; i++) {
                        ItemPredicate itemPredicate = signSeasonQuest.need().get(i);
                        HolderSet<Item> holders = itemPredicate.items().orElse(HolderSet.empty());
                        if (holders.size() > 0) {
                            Item value = holders.get(0).value();
                            Component c = Component.translatable("eclipticseasons.season_quest.hint.item_count", value.getName(value.getDefaultInstance()), itemPredicate.count().max().orElse(itemPredicate.count().min().orElse(0)));
                            signText = signText.setMessage(i + 1, c);
                        }
                    }
                }
            }
            setText(signText, true);
        }

    }


    public void createSeasonalQuest() {
        level.registryAccess().lookup(ESRegistries.SEASON_QUEST).ifPresent(
                seasonQuests -> {
                    SolarTerm nowSolarTerm = EclipticUtil.getNowSolarTerm(level);
                    Holder<AgroClimaticZone> agroClimaticZoneHolder = CropGrowthHandler.getclimateTypeHolder(CropGrowthHandler.getCropBiome(level, getBlockPos()));
                    List<SeasonQuest> seasonQuestList = new ArrayList<>();
                    int totalWeight = 0;
                    for (Map.Entry<ResourceKey<SeasonQuest>, SeasonQuest> entry : ESSortInfo.sorted(seasonQuests.entrySet())) {
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

    public static final class QuestMatcher implements Predicate<ItemStack> {
        private ItemPredicate predicate;
        private boolean strict = false;

        @Override
        public boolean test(ItemStack stack) {
            if (predicate.items().isPresent() && !stack.is(predicate.items().get())) {
                return false;
            } else if (strict && !predicate.count().matches(stack.getCount())) {
                return false;
            } else if (!predicate.components().test(stack)) {
                return false;
            } else {
                //for (ItemSubPredicate itemsubpredicate : predicate.subPredicates().values()) {
                //    if (!itemsubpredicate.matches(stack)) {
                //        return false;
                //    }
                //}
                return true;
            }
        }

        public QuestMatcher setPredicate(ItemPredicate predicate) {
            this.predicate = predicate;
            return this;
        }

        public QuestMatcher setStrict(boolean strict) {
            this.strict = strict;
            return this;
        }

        public boolean hasEnoughAmount(int count) {
            int cc = getCount(predicate);
            return count >= cc;
        }

        public static int getCount(ItemPredicate predicate) {
            return predicate.count().max().orElse(predicate.count().min().orElse(0));
        }

        public boolean test(ServerPlayer player) {
            int i = player.getInventory().clearOrCountMatchingItems(this, 0, player.inventoryMenu.getCraftSlots());
            return hasEnoughAmount(i);
        }

        public boolean consume(ServerPlayer player) {
            int i = player.getInventory().clearOrCountMatchingItems(this, getCount(predicate), player.inventoryMenu.getCraftSlots());
            return hasEnoughAmount(i);
        }
    }

    public void finishSeasonQuest(ServerPlayer player) {
        SeasonQuest quest = getSeasonQuest();
        if (quest != null) {
            boolean isOk = true;
            QuestMatcher questMatcher = new QuestMatcher();
            for (ItemPredicate itemPredicate : quest.need()) {
                if (!questMatcher.setPredicate(itemPredicate).test(player)) {
                    isOk = false;
                    break;
                }
            }
            if (isOk) {
                for (ItemPredicate itemPredicate : quest.need()) {
                    questMatcher.setPredicate(itemPredicate).consume(player);
                }
                for (ItemStackTemplate stack : quest.award()) {
                    player.getInventory().add(stack.create());
                    //ItemHandlerHelper.giveItemToPlayer(player, stack.copy());
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


    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (!level.isClientSide()) {
            popSign(level, pos);
        }
        super.preRemoveSideEffects(pos, state);
    }
}

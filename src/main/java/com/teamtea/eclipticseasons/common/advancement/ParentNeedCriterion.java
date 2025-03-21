package com.teamtea.eclipticseasons.common.advancement;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.common.registry.ModAdvancements;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.Optional;

public class ParentNeedCriterion extends SimpleCriterionTrigger<ParentNeedCriterion.TriggerInstance> {


    private final ResourceLocation id;

    public ParentNeedCriterion(ResourceLocation id) {
        this.id = id;
    }

    public void trigger(ServerPlayer player) {
        MinecraftServer server = player.getServer();
        if (server != null) {
            this.trigger(player, (t) -> t.test(player, server.getAdvancements()));
        }
    }

    public @NotNull ResourceLocation getId() {
        return this.id;
    }

    @Override
    protected TriggerInstance createInstance(JsonObject pJson, ContextAwarePredicate pPredicate, DeserializationContext pDeserializationContext) {
        Optional<ResourceLocation> parent =
                pJson.has("parent") ?
                        Optional.of(ResourceLocation.parse(pJson.get("parent").getAsString())) : Optional.empty();
        return new TriggerInstance(getId(), pPredicate, parent);
    }

    public static final class TriggerInstance extends AbstractCriterionTriggerInstance {

        private final Optional<ResourceLocation> parent;
        private WeakReference<Advancement> advancementHolderWeakReference = new WeakReference<>(null);

        public TriggerInstance(
                ResourceLocation pCriterion, ContextAwarePredicate pPlayer,
                Optional<ResourceLocation> parent) {
            super(pCriterion, pPlayer);
            this.parent = parent;
        }

        public static TriggerInstance simple(Advancement advancementHolder) {
            return new TriggerInstance(ModAdvancements.parentNeedCriterion.getId(),
                    ContextAwarePredicate.ANY,
                    Optional.of(advancementHolder.getId()));
        }

        public boolean test(ServerPlayer player, ServerAdvancementManager advancements) {
            if (parent.isEmpty()) return true;
            Advancement advancementHolder = advancementHolderWeakReference.get();
            if (advancementHolder == null) {
                advancementHolder = advancements.getAdvancement(parent.get());
                advancementHolderWeakReference = new WeakReference<>(advancementHolder);
            }
            if (advancementHolder == null) return true;
            return player.getAdvancements().getOrStartProgress(advancementHolder).isDone();
        }

        public Optional<ResourceLocation> parent() {
            return parent;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (TriggerInstance) obj;
            return Objects.equals(this.parent, that.parent);
        }

        @Override
        public int hashCode() {
            return Objects.hash(parent);
        }

        @Override
        public String toString() {
            return "TriggerInstance[" +
                    "parent=" + parent + ']';
        }

        @Override
        public JsonObject serializeToJson(SerializationContext pConditions) {
            JsonObject jsonObject = super.serializeToJson(pConditions);
            parent.ifPresent(resourceLocation -> jsonObject.addProperty("parent", resourceLocation.toString()));
            return jsonObject;
        }
    }
}

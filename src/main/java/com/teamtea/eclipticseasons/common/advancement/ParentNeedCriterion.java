package com.teamtea.eclipticseasons.common.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.common.registry.ModAdvancements;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.Optional;

public class ParentNeedCriterion extends SimpleCriterionTrigger<ParentNeedCriterion.TriggerInstance> {

    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        if (server != null) {
            this.trigger(player, (t) -> t.test(player, server.getAdvancements()));
        }
    }


    public static final class TriggerInstance implements SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
                builder -> builder.group(
                                Identifier.CODEC.optionalFieldOf("parent").forGetter(TriggerInstance::parent))
                        .apply(builder, TriggerInstance::new)
        );
        private final Optional<Identifier> parent;
        private WeakReference<AdvancementHolder> advancementHolderWeakReference = new WeakReference<>(null);

        public TriggerInstance(
                Optional<Identifier> parent) {
            this.parent = parent;
        }

        public static Criterion<TriggerInstance> simple(AdvancementHolder advancementHolder) {
            return ModAdvancements.parentNeedCriterion.get().createCriterion(
                    new TriggerInstance(Optional.of(advancementHolder.id()))
            );
        }

        public static Criterion<TriggerInstance> simple(Identifier Identifier) {
            return ModAdvancements.parentNeedCriterion.get().createCriterion(
                    new TriggerInstance(Optional.of(Identifier))
            );
        }

        public boolean test(ServerPlayer player, ServerAdvancementManager advancements) {
            if (parent.isEmpty()) return true;
            AdvancementHolder advancementHolder = advancementHolderWeakReference.get();
            if (advancementHolder == null) {
                advancementHolder = advancements.get(parent.get());
                advancementHolderWeakReference = new WeakReference<>(advancementHolder);
            }
            if (advancementHolder == null) return true;
            return player.getAdvancements().getOrStartProgress(advancementHolder).isDone();
        }

        @Override
        public @NotNull Optional<ContextAwarePredicate> player() {
            return Optional.empty();
        }

        public Optional<Identifier> parent() {
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

    }
}

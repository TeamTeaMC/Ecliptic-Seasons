package com.teamtea.eclipticseasons.client.model.block.unbake.multipart;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.client.renderer.block.dispatch.multipart.CombinedCondition;
import net.minecraft.client.renderer.block.dispatch.multipart.Condition;
import net.minecraft.client.renderer.block.dispatch.multipart.KeyValueCondition;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ConditionLike extends Condition {

    Codec<ConditionLike> CODEC = Codec.lazyInitialized(() -> Conditions.CODEC);
    AndConditionLike EMPTY = new AndConditionLike(List.of());


    static ConditionLike of(Optional<Condition> condition) {
        if (condition.isEmpty()) return EMPTY;
        Condition cond = condition.get();
        try {
            if(false){
                var dynamicops = JsonOps.INSTANCE;
                JsonElement orThrow = Condition.CODEC.encodeStart(dynamicops, cond).getOrThrow();
                return CODEC.decode(dynamicops, orThrow).getOrThrow().getFirst();
            }

            return switch (cond) {
                case KeyValueCondition kvc -> ofKeyValue(kvc);
                case CombinedCondition cc -> switch (cc.operation()) {
                    case OR -> new OrConditionLike(cc.terms().stream()
                            .map(Optional::of)
                            .map(ConditionLike::of).toList());
                    case AND -> new AndConditionLike(cc.terms().stream()
                            .map(Optional::of)
                            .map(ConditionLike::of).toList());
                };
                default -> EMPTY;
            };

            // DynamicOps<JsonElement> dynamicops = JsonOps.INSTANCE;
            // JsonElement orThrow = Condition.CODEC.encodeStart(dynamicops, cond).getOrThrow();
            // return CODEC.decode(dynamicops, orThrow).getOrThrow().getFirst();
        } catch (Exception e) {
            EclipticSeasons.logger(e);
            return EMPTY;
        }
    }

    static @NonNull AndConditionLike ofKeyValue(KeyValueCondition kvc) {
        Map<String, KeyValueCondition.Terms> tests = kvc.tests();
        ArrayList<ConditionLike> ckl = new ArrayList<>();
        tests.forEach(
                (s, terms) -> {
                    ArrayList<ConditionLike> ckl2 = new ArrayList<>();
                    for (KeyValueCondition.Term entry : terms.entries()) {
                        ckl2.add(new KeyValueConditionLike(
                                s, (entry.negated() ? "!" : "") + entry.value()
                        ));
                    }
                    ckl.add(new OrConditionLike(ckl2));
                }
        );
        return new AndConditionLike(ckl);
    }
}

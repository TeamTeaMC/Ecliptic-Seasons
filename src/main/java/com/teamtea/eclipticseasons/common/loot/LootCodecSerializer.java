package com.teamtea.eclipticseasons.common.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.teamtea.eclipticseasons.EclipticSeasons;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record LootCodecSerializer<T>(Codec<T> codec) implements net.minecraft.world.level.storage.loot.Serializer<T> {

    public void serialize(@NotNull JsonObject jsonObject, @NotNull T value, @NotNull JsonSerializationContext context) {
        JsonElement jsonelement = codec.encodeStart(JsonOps.INSTANCE, value).getOrThrow(false, EclipticSeasons::logger);
        if (jsonelement.isJsonObject()) {
            JsonObject obj = jsonelement.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                jsonObject.add(entry.getKey(), entry.getValue());
            }
        }
    }

    public @NotNull T deserialize(@NotNull JsonObject value, @NotNull JsonDeserializationContext context) {
        return codec.decode(JsonOps.INSTANCE, value).getOrThrow(false, EclipticSeasons::logger).getFirst();
    }
}

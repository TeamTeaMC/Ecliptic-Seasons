package com.teamtea.eclipticseasons.mixin.data;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.teamtea.eclipticseasons.data.extend.example.DatapackRegistryGeneratorExample;
import com.teamtea.eclipticseasons.data.general.datapack.DatapackRegistryGenerator;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.RegistriesDatapackGenerator;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.WithConditions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;


@Mixin({RegistriesDatapackGenerator.class})
public abstract class MixinsRegistriesDatapackGenerator {

    @Shadow
    @Final
    private Map<ResourceKey<?>, List<ICondition>> conditions;

    @Inject(at = {@At("HEAD")}, method = {"lambda$dumpRegistryCap$5"}, cancellable = true)
    private void eclipticseasons$lambda$dumpRegistryCap$11(PackOutput.PathProvider packoutput$pathprovider, CachedOutput output, DynamicOps ops, Codec conditionalCodec, Holder.Reference p_256105_, CallbackInfoReturnable<CompletableFuture> cir) {
        if ((Object) this instanceof DatapackRegistryGenerator||(Object) this instanceof DatapackRegistryGeneratorExample)
            cir.setReturnValue(dumpValue2(
                    packoutput$pathprovider.json(p_256105_.key().location()),
                    output,
                    ops,
                    conditionalCodec,
                    Optional.of(new net.neoforged.neoforge.common.conditions.WithConditions<>(conditions.getOrDefault(p_256105_.key(), List.of()), p_256105_.value()))
            ));
    }

    @Unique
    private static <E> CompletableFuture<?> dumpValue2(
            Path p_255678_, CachedOutput p_256438_, DynamicOps<JsonElement> p_256127_, Encoder<Optional<WithConditions<E>>> p_255938_, Optional<net.neoforged.neoforge.common.conditions.WithConditions<E>> p_256590_
    ) {
        return p_255938_.encodeStart(p_256127_, p_256590_)
                .mapOrElse(
                        p_351699_ -> eclipticseasons$lambda$static$0(p_256438_, p_255678_, p_351699_),
                        p_351701_ -> CompletableFuture.failedFuture(new IllegalStateException("Couldn't generate file '" + p_255678_ + "': " + p_351701_.message()))
                );
    }

    @Unique
    private static CompletableFuture<Void> eclipticseasons$lambda$static$0(CachedOutput output, Path path, JsonElement json) {
        return CompletableFuture.runAsync(() -> {
            try {

                ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                HashingOutputStream hashingoutputstream = new HashingOutputStream(Hashing.sha1(), bytearrayoutputstream);
                try (JsonWriter jsonwriter = new JsonWriter(new OutputStreamWriter(bytearrayoutputstream, StandardCharsets.UTF_8))) {
                    jsonwriter.setSerializeNulls(false);
                    jsonwriter.setIndent("  ");

                    // 使用 Gson 直接写入
                    Gson gson = new Gson();
                    gson.toJson(json, jsonwriter);
                }

                // 直接保存
                output.writeIfNeeded(path, bytearrayoutputstream.toByteArray(), hashingoutputstream.hash());
            } catch (IOException ioexception) {

            }
        }, Util.backgroundExecutor());
    }


}

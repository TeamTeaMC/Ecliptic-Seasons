package com.teamtea.eclipticseasons.mixin.data;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.teamtea.eclipticseasons.data.datapack.DatapackRegistryGenerator;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.RegistriesDatapackGenerator;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.common.crafting.conditions.ICondition;
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
import java.util.function.Predicate;


@Mixin({RegistriesDatapackGenerator.class})
public abstract class MixinsRegistriesDatapackGenerator {


    @Shadow(remap = false) @Final private Predicate<String> namespacePredicate;

    @Shadow @Final private PackOutput output;

    @Inject(at = {@At("HEAD")}, method = {"dumpRegistryCap"}, cancellable = true)
    private <T> void eclipticseasons$lambda$dumpRegistryCap$11(CachedOutput pOutput, HolderLookup.Provider pRegistries, DynamicOps<JsonElement> pOps, RegistryDataLoader.RegistryData<T> pRegistryData, CallbackInfoReturnable<Optional<CompletableFuture<?>>> cir) {
        if ((Object) this instanceof DatapackRegistryGenerator) {
            ResourceKey<? extends Registry<T>> resourcekey = pRegistryData.key();
            var cc= pRegistries.lookup(resourcekey).map((p_255847_) -> {
                PackOutput.PathProvider packoutput$pathprovider = this.output.createPathProvider(PackOutput.Target.DATA_PACK, net.minecraftforge.common.ForgeHooks.prefixNamespace(resourcekey.location()));
                return CompletableFuture.allOf(p_255847_.listElements().filter(holder -> this.namespacePredicate.test(holder.key().location().getNamespace())).map((p_256105_) -> {
                    return dumpValue2(packoutput$pathprovider.json(p_256105_.key().location()), pOutput, pOps, pRegistryData.elementCodec(), p_256105_.value());
                }).toArray((p_256279_) -> {
                    return new CompletableFuture[p_256279_];
                }));
            });
            cir.setReturnValue(((Optional)cc));
        }
    }

    @Unique
    private static <E> CompletableFuture<?> dumpValue2(Path pValuePath, CachedOutput pOutput, DynamicOps<JsonElement> pOps, Encoder<E> pEncoder, E pValue) {
        Optional<JsonElement> optional = pEncoder.encodeStart(pOps, pValue).resultOrPartial((p_255999_) -> {
        });
        return optional.isPresent() ? eclipticseasons$lambda$static$0(pOutput, pValuePath, optional.get()) : CompletableFuture.completedFuture((Object) null);
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

                    Gson gson = new Gson();
                    gson.toJson(json, jsonwriter);
                }

                output.writeIfNeeded(path, bytearrayoutputstream.toByteArray(), hashingoutputstream.hash());
            } catch (IOException ioexception) {

            }
        }, Util.backgroundExecutor());
    }


}

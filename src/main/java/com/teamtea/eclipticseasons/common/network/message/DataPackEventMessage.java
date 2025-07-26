package com.teamtea.eclipticseasons.common.network.message;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DataPackRegistriesHooks;

import java.util.*;

public class DataPackEventMessage<T> {

    public final ResourceKey<Registry<T>> resourceKey;
    public List<Pair<ResourceKey<T>, T>> data = new ArrayList<>();
    public List<CompoundTag> compoundTags = new ArrayList<>();
    public Codec<T> codec = null;
    private RegistryAccess registryAccess = null;

    public DataPackEventMessage(
            RegistryAccess registryAccess,
            ResourceKey<Registry<T>> resourceKey,
            Set<Map.Entry<ResourceKey<T>, T>> data,
            Codec<T> codec) {
        this.resourceKey = resourceKey;
        this.data = data.stream().map(e -> Pair.of(e.getKey(), e.getValue())).toList();
        this.codec = codec;
        this.registryAccess = registryAccess;
    }


    public DataPackEventMessage(FriendlyByteBuf buf) {
        ResourceLocation resourceLocation = buf.readResourceLocation();
        ResourceKey<Registry<T>> registryResourceKeyTo = null;
        for (var syncedCustomRegistry : DataPackRegistriesHooks.getDataPackRegistries()) {
            if (syncedCustomRegistry.key().location().compareTo(resourceLocation) == 0) {
                registryResourceKeyTo = (ResourceKey<Registry<T>>) syncedCustomRegistry.key();
                this.codec = (Codec<T>) syncedCustomRegistry.elementCodec();
                break;
            }
        }
        this.resourceKey = registryResourceKeyTo;
        if (this.codec != null) {
            int size = buf.readVarInt();
            for (int i = 0; i < size; i++) {
                CompoundTag compoundTag = buf.readNbt();
                if (compoundTag == null) continue;
                Tag tag = compoundTag.get("value");
                if (tag == null) continue;
                compoundTags.add(compoundTag);
            }
        }
    }


    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceLocation(resourceKey.location());
        buf.writeVarInt(data.size());
        RegistryOps<Tag> registryops = RegistryOps.create(NbtOps.INSTANCE, registryAccess);
        Codec<ResourceKey<T>> resourceKeyCodec = ResourceKey.codec(resourceKey);
        for (Pair<ResourceKey<T>, T> datum : data) {
            Optional<Tag> tag = codec
                    .encodeStart(registryops, datum.getSecond())
                    .resultOrPartial(EclipticSeasons::logger);
            Optional<Tag> k_tag = resourceKeyCodec
                    .encodeStart(registryops, datum.getFirst())
                    .resultOrPartial(EclipticSeasons::logger);
            CompoundTag compoundTag = new CompoundTag();
            if (tag.isPresent() && k_tag.isPresent()) {
                compoundTag.put("value", tag.get());
                compoundTag.put("key", k_tag.get());
            }
            ;
            buf.writeNbt(compoundTag);
        }
    }


    public List<T> build(RegistryAccess clientRegistryAccess, Class<T> tClass) {
        EclipticSeasons.logger("Rebuild registry %s for client side.".formatted(tClass.getSimpleName()));
        List<T> tList = new ArrayList<>();
        if (this.codec != null) {
            if (ClientCon.getUseLevel() != null) {
                RegistryOps<Tag> registryops = RegistryOps.create(NbtOps.INSTANCE, clientRegistryAccess);
                Codec<ResourceKey<T>> resourceKeyCodec = ResourceKey.codec(resourceKey);
                Registry<T> registry = clientRegistryAccess.registryOrThrow(resourceKey);
                List<Pair<ResourceKey<T>, T>> pairArrayList = new ArrayList<>();
                for (CompoundTag compoundTag : compoundTags) {
                    if (compoundTag == null) continue;
                    Tag tag = compoundTag.get("value");
                    if (tag == null) continue;
                    Optional<T> t = this.codec
                            .parse(registryops, tag)
                            .resultOrPartial(EclipticSeasons::logger);
                    if (t.isPresent()) {
                        T t1 = t.get();
                        tList.add(t1);

                        Tag k_tag = compoundTag.get("key");
                        if (k_tag == null) continue;
                        Optional<ResourceKey<T>> keyOptional = resourceKeyCodec
                                .parse(registryops, k_tag)
                                .resultOrPartial(EclipticSeasons::logger);
                        if (keyOptional.isPresent()) {
                            ResourceKey<T> tResourceKey = keyOptional.get();
                            pairArrayList.add(Pair.of(tResourceKey, t1));
                            Optional<Holder.Reference<T>> holder = registry.getHolder(tResourceKey);
                            if (holder.isPresent() && holder.get().getType() != Holder.Reference.Type.INTRUSIVE) {
                                holder.get().bindValue(t1);
                            }
                        }
                    }
                }
                this.data = pairArrayList;
            }
        }
        EclipticSeasons.logger("End registry %s for client side with size %s.".formatted(tClass.getSimpleName(), tList.size()));
        return tList;
    }

}

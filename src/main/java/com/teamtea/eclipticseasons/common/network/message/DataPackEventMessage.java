package com.teamtea.eclipticseasons.common.network.message;

import com.mojang.serialization.Codec;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.client.util.ClientCon;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DataPackEventMessage<T> {

    public final ResourceKey<Registry<T>> resourceKey;
    public List<T> data = new ArrayList<>();
    public List<CompoundTag> compoundTags = new ArrayList<>();
    public Codec<T> codec = null;
    private RegistryAccess registryAccess = null;

    public DataPackEventMessage(
            RegistryAccess registryAccess,
            ResourceKey<Registry<T>> resourceKey,
            List<T> data,
            Codec<T> codec) {
        this.resourceKey = resourceKey;
        this.data = data;
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
        for (T datum : data) {
            Optional<Tag> tag = codec
                    .encodeStart(registryops, datum)
                    .resultOrPartial(EclipticSeasons::logger);
            CompoundTag compoundTag = new CompoundTag();
            if (tag.isPresent()) compoundTag.put("value", tag.get());
            buf.writeNbt(compoundTag);
        }
    }


    public List<T> build(RegistryAccess clientRegistryAccess, Class<T> tClass) {
        if (this.codec != null) {
            if (ClientCon.getUseLevel() != null) {
                RegistryOps<Tag> registryops = RegistryOps.create(NbtOps.INSTANCE, clientRegistryAccess);
                List<T> tList = new ArrayList<>();
                for (CompoundTag compoundTag : compoundTags) {
                    if (compoundTag == null) continue;
                    Tag tag = compoundTag.get("value");
                    if (tag == null) continue;
                    Optional<T> t = this.codec
                            .parse(registryops, tag)
                            .resultOrPartial(EclipticSeasons::logger);
                    if (t.isPresent()) {
                        tList.add(t.get());
                    }
                }
                this.data = tList;
            }
        }
        return this.data;
    }

}

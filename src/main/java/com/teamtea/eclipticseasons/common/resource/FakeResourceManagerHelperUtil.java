package com.teamtea.eclipticseasons.common.resource;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.fml.loading.moddiscovery.ModFile;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforgespi.locating.IModFile;

import java.nio.file.Path;
import java.util.Optional;

public class FakeResourceManagerHelperUtil {
    private static final PackSelectionConfig FEATURE_SELECTION_CONFIG = new PackSelectionConfig(true, Pack.Position.BOTTOM, false);

    public static void registerBuiltinResourcePack(AddPackFindersEvent event, ResourceLocation packId, IModFile modFile, PackSource source) {
        String languageKey = packId.toLanguageKey("pack");
        registerBuiltinResourcePack(event,
                packId,
                modFile,
                Component.translatable(languageKey),
                source);
    }

    public static void registerBuiltinResourcePack(AddPackFindersEvent event, ResourceLocation packId, IModFile modFile, Component translate, PackSource source) {
        String packIdLanguageKey = packId.toLanguageKey() + "_" + event.getPackType().getSerializedName();
        var packLocationInfo = new PackLocationInfo(
                packIdLanguageKey, translate, source, Optional.of(knowPack(packIdLanguageKey)
        ));
        event.addRepositorySource(consumer -> consumer.accept(
                Pack.readMetaAndCreate(packLocationInfo,
                        new ESModFilePackResources.PathResourcesSupplier(modFile, Path.of("resourcepacks/" + packId.getPath())),
                        event.getPackType(),
                        FEATURE_SELECTION_CONFIG
                )));
    }

    public static void registerBuiltinResourcePack(AddPackFindersEvent event, ResourceLocation packId, IModFile modFile, MutableComponent translate, PackType packType, PackSource source) {
        registerBuiltinResourcePack(event, packId, modFile, translate, packType, source, FEATURE_SELECTION_CONFIG);
    }

    public static void registerBuiltinResourcePack(AddPackFindersEvent event, ResourceLocation packId, IModFile modFile, MutableComponent translate, PackType packType, PackSource source, PackSelectionConfig selectionConfig) {
        if (event.getPackType() == packType) {
            String packIdLanguageKey = packId.toLanguageKey() + "_" + packType.getSerializedName();
            var packLocationInfo = new PackLocationInfo(
                    packIdLanguageKey, translate, source, Optional.of(knowPack(packIdLanguageKey)
            ));
            event.addRepositorySource(consumer -> consumer.accept(
                    Pack.readMetaAndCreate(packLocationInfo,
                            new ESModFilePackResources.PathResourcesSupplier(modFile, Path.of("resourcepacks/" + packId.getPath())),
                            packType,
                            selectionConfig
                    )));
        }
    }

    public static void registerBuiltinResourcePack(AddPackFindersEvent event, String namespace, String pack, IModFile modFile, MutableComponent translate, PackType packType, PackSource source, PackSelectionConfig selectionConfig) {
        registerBuiltinResourcePack(event, "", namespace, pack, modFile, translate, packType, source, selectionConfig);
    }

    public static void registerBuiltinResourcePack(AddPackFindersEvent event, String prefix, String namespace, String pack, IModFile modFile, MutableComponent translate, PackType packType, PackSource source, PackSelectionConfig selectionConfig) {
        if (event.getPackType() == packType) {
            String packIdLanguageKey = namespace + ":" + prefix + pack + "_" + packType.getSerializedName();
            var packLocationInfo = new PackLocationInfo(
                    prefix + pack, translate, source, Optional.of(knowPack(packIdLanguageKey)
            ));
            event.addRepositorySource(consumer -> consumer.accept(
                    Pack.readMetaAndCreate(packLocationInfo,
                            new ESModFilePackResources.PathResourcesSupplier(modFile, Path.of("resourcepacks/" + pack)),
                            packType,
                            selectionConfig
                    )));
        }
    }

    public static void registerBuiltinDataPack(AddPackFindersEvent event, IModFile modContainer, String packId) {
        FakeResourceManagerHelperUtil.registerBuiltinResourcePack(
                event,
                EclipticSeasons.rl("compat_" + packId),
                modContainer,
                Component.translatable("pack." + EclipticSeasonsApi.MODID + "." + packId),
                PackType.SERVER_DATA,
                PackSource.BUILT_IN);
    }

    public static KnownPack knowPack(String pName) {
        return new KnownPack(EclipticSeasonsApi.MODID, pName, SharedConstants.getCurrentVersion().getId());
    }


    public static void addPackForExtra(AddPackFindersEvent event, IModFile modFile, String modid, String path, String pack_id, boolean priorityLoading) {
        FakeResourceManagerHelperUtil.registerBuiltinResourcePack(
                event, modid + "/",
                modid, path, modFile,
                Component.translatable(EclipticSeasons.erl(modid, pack_id).toLanguageKey("pack")),
                event.getPackType(), PackSource.FEATURE, new PackSelectionConfig(true, priorityLoading ? Pack.Position.TOP : Pack.Position.BOTTOM, false));
    }

}

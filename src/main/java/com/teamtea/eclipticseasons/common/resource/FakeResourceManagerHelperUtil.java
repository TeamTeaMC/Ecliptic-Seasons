package com.teamtea.eclipticseasons.common.resource;

import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;

public class FakeResourceManagerHelperUtil {
    public static void registerBuiltinResourcePack(AddPackFindersEvent event, ResourceLocation packId, ModFile modFile, PackSource source) {
        String languageKey = packId.toLanguageKey("pack");
        registerBuiltinResourcePack(event,
                packId,
                modFile,
                Component.translatable(languageKey),
                source);
    }

    public static void registerBuiltinResourcePack(AddPackFindersEvent event, ResourceLocation packId, ModFile modFile, Component translate, PackSource source) {
        String packIdLanguageKey = packId.toLanguageKey();
        event.addRepositorySource(consumer -> consumer.accept(
                Pack.readMetaAndCreate(packIdLanguageKey, translate, false,
                        id -> new ESModFilePackResources(packId.getNamespace(), packIdLanguageKey, modFile, "resourcepacks/" + packId.getPath()), event.getPackType(),
                        Pack.Position.BOTTOM, source)));
    }

    public static void registerBuiltinResourcePack(AddPackFindersEvent event, ResourceLocation packId, ModFile modFile, MutableComponent translate, PackType packType, PackSource source) {
        registerBuiltinResourcePack(event, packId, modFile, translate, packType, source, true);
    }

    public static void registerBuiltinResourcePack(AddPackFindersEvent event, ResourceLocation packId, ModFile modFile, MutableComponent translate, PackType packType, PackSource source, boolean require) {
        if (event.getPackType() == packType) {
            String packIdLanguageKey = packId.toLanguageKey();
            event.addRepositorySource(consumer -> consumer.accept(
                    Pack.readMetaAndCreate(packIdLanguageKey, translate, require,
                            id -> new ESModFilePackResources(packId.getNamespace(), packIdLanguageKey, modFile, "resourcepacks/" + packId.getPath()), packType,
                            Pack.Position.BOTTOM, source)));
        }
    }

    public static void registerBuiltinResourcePack(AddPackFindersEvent event, String namespace, String pack, ModFile modFile, MutableComponent translate, PackType packType, PackSource source, Pack.Position position, boolean require) {
        registerBuiltinResourcePack(event, "", namespace, pack, modFile, translate, packType, source, position, require);
    }

    public static void registerBuiltinResourcePack(AddPackFindersEvent event, String prefix, String namespace, String pack, ModFile modFile, MutableComponent translate, PackType packType, PackSource source, Pack.Position position, boolean require) {
        if (event.getPackType() == packType) {
            event.addRepositorySource(consumer -> consumer.accept(
                    Pack.readMetaAndCreate(pack, translate, require,
                            id -> new ESModFilePackResources(namespace, prefix + pack, modFile, "resourcepacks/" + pack), packType,
                            position, source)));
        }
    }

    public static void registerBuiltinDataPack(AddPackFindersEvent event, ModFile modContainer, String packId) {
        FakeResourceManagerHelperUtil.registerBuiltinResourcePack(
                event,
                EclipticSeasons.rl("compat_" + packId),
                modContainer,
                Component.translatable("pack." + EclipticSeasons.MODID + "." + packId),
                PackType.SERVER_DATA,
                PackSource.BUILT_IN);
    }

    public static void addPackForExtra(AddPackFindersEvent event, ModFile modFile, String modid, String path, String pack_id) {
            FakeResourceManagerHelperUtil.registerBuiltinResourcePack(
                    event, modid + "/",
                    modid, path, modFile,
                    Component.translatable(EclipticSeasons.rl(pack_id).toLanguageKey("pack")),
                    event.getPackType(), PackSource.FEATURE, Pack.Position.BOTTOM, true);
    }
}

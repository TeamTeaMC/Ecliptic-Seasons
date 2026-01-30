package com.teamtea.eclipticseasons.compat.eclipticseasons_bundles;


import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.common.resource.FakeResourceManagerHelperUtil;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;

import java.util.Locale;
import java.util.Optional;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModContents {

    @SubscribeEvent
    public static void registerBuiltinResourcePacks(AddPackFindersEvent event) {
        Optional<ModFile> modContainer = Optional.ofNullable(FMLLoader.getLoadingModList().getModFileById(EclipticSeasonsBundles.MODID).getFile());
        if (modContainer.isPresent()) {
            ModFile modFile = modContainer.get();

            CommentedFileConfig oldConfig = CommentedFileConfig.builder(FMLPaths.CONFIGDIR.get().resolve(EclipticSeasons.defaultConfigName(ModConfig.Type.COMMON, EclipticSeasonsBundles.MODID)))
                    .preserveInsertionOrder().build();
            oldConfig.load();

            for (var stringBooleanValueEntry : General.enableList.entrySet()) {
                String name = stringBooleanValueEntry.getKey();
                var booleanValue = stringBooleanValueEntry.getValue();
                if (oldConfig.getOrElse(booleanValue.getKey().getPath(), false)) {
                    FakeResourceManagerHelperUtil.addPackForExtra(
                            event, modFile,
                            EclipticSeasonsBundles.MODID,
                            name, normalizeId(name)
                    );
                }
            }
            oldConfig.close();
        }
    }

    public static String normalizeId(String raw) {
        return raw
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9._-]", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");
    }


}

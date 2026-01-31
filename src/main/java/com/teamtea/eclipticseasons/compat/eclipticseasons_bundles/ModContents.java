package com.teamtea.eclipticseasons.compat.eclipticseasons_bundles;


import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.common.resource.FakeResourceManagerHelperUtil;
import com.teamtea.eclipticseasons.compat.Platform;
import net.minecraft.server.packs.PackType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforgespi.locating.IModFile;

import java.util.Locale;
import java.util.Optional;

@SuppressWarnings("removal")
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ModContents {

    @SubscribeEvent
    public static void registerBuiltinResourcePacks(AddPackFindersEvent event) {
        Optional<IModFile> modContainer = Optional.ofNullable(Platform.getModFile(EclipticSeasonsBundles.MODID));
        if (modContainer.orElse(null) instanceof IModFile modFile) {
            CommentedFileConfig oldConfig = CommentedFileConfig.builder(FMLPaths.CONFIGDIR.get().resolve(EclipticSeasons.defaultConfigName(ModConfig.Type.COMMON, EclipticSeasonsBundles.MODID)))
                    .preserveInsertionOrder().build();
            oldConfig.load();

            for (var stringBooleanValueEntry : General.enableList.entrySet()) {
                String name = stringBooleanValueEntry.getKey();
                var packController = stringBooleanValueEntry.getValue();
                Optional<Boolean> serverOnly = packController.config().getServerOnly();
                if (serverOnly.isEmpty()
                        || serverOnly.get() == (event.getPackType() == PackType.SERVER_DATA)) {
                    if (isShouldLoad(oldConfig, packController.enable())) {
                        FakeResourceManagerHelperUtil.addPackForExtra(
                                event, modFile,
                                EclipticSeasonsBundles.MODID,
                                name, packController.config().getId(),
                                isShouldLoad(oldConfig, packController.priorityLoading())
                        );
                    }
                }
            }
            oldConfig.close();
        }
    }

    private static boolean isShouldLoad(CommentedFileConfig oldConfig, ModConfigSpec.BooleanValue booleanValue) {
        boolean shouldLoad;
        try {
            shouldLoad = booleanValue.get();
        } catch (IllegalStateException illegalStateException) {
            shouldLoad = oldConfig.getOrElse(booleanValue.getPath(), false);
        }
        return shouldLoad;
    }

    public static String normalizeId(String raw) {
        return raw
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9._-]", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");
    }


}

package com.teamtea.eclipticseasons.common.resource;


import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.DetectedVersion;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.resource.PathPackResources;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ESModFilePackResources extends PathPackResources {
    protected final IModFile modFile;
    protected final String sourcePath;
    private final PackMetadataSection bindSection;

    public ESModFilePackResources(String modid, String name, IModFile modFile, String sourcePath) {
        super(name, true, modFile.findResource(sourcePath));
        this.modFile = modFile;
        this.sourcePath = sourcePath;
        this.bindSection = new PackMetadataSection(Component.literal(modid),
                DetectedVersion.BUILT_IN.getPackVersion(PackType.SERVER_DATA),
                Arrays.stream(PackType.values()).collect(Collectors.toMap(Function.identity(), DetectedVersion.BUILT_IN::getPackVersion)));
    }

    public ESModFilePackResources(String name, IModFile modFile, String sourcePath) {
        this(EclipticSeasons.MODID, name, modFile, sourcePath);
    }

    @Override
    protected @NotNull Path resolve(String... paths) {
        String[] allPaths = new String[paths.length + 1];
        allPaths[0] = sourcePath;
        System.arraycopy(paths, 0, allPaths, 1, paths.length);
        return modFile.findResource(allPaths);
    }

    @Override
    public @Nullable <T> T getMetadataSection(MetadataSectionSerializer<T> pDeserializer) throws IOException {
        IoSupplier<InputStream> iosupplier = this.getRootResource("pack.mcmeta");
        if (iosupplier == null) {
            return pDeserializer == PackMetadataSection.TYPE ? (T) bindSection : null;
        } else {
            try (InputStream inputstream = iosupplier.get()) {
                return getMetadataFromStream(pDeserializer, inputstream);
            }
        }
    }

    @Override
    public void listResources(PackType type, String namespace, String path, ResourceOutput resourceOutput) {
        if (type == PackType.CLIENT_RESOURCES && sourcePath.equals("resourcepacks/extra_snow")) {
            try {
                if (!CommonConfig.Resource.extraSnow.get()) {
                    return;
                }
            } catch (Exception e) {
                EclipticSeasons.logger(e);
            }
        }
        super.listResources(type, namespace, path, resourceOutput);
    }

}

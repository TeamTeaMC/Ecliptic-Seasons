package com.teamtea.eclipticseasons.common.resource;


import net.minecraft.DetectedVersion;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.resources.IoSupplier;
import net.neoforged.neoforgespi.locating.IModFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ESModFilePackResources extends PathPackResources {
    protected final IModFile modFile;
    protected final String sourcePath;
    private final PackMetadataSection bindSection;


    public ESModFilePackResources(PackLocationInfo locationInfo, IModFile modFile, String sourcePath) {
        super(locationInfo, modFile.findResource(sourcePath));
        this.modFile = modFile;
        this.sourcePath = sourcePath;
        this.bindSection = new PackMetadataSection(locationInfo.title(),
                DetectedVersion.BUILT_IN.getPackVersion(PackType.CLIENT_RESOURCES),
                Optional.empty());
    }


    @Override
    public @Nullable <T> T getMetadataSection(@NotNull MetadataSectionSerializer<T> pDeserializer) throws IOException {
        IoSupplier<InputStream> iosupplier = this.getRootResource("pack.mcmeta");
        if (iosupplier == null) {
            return pDeserializer == PackMetadataSection.TYPE ? (T) bindSection : null;
        } else {
            try (InputStream inputstream = iosupplier.get()) {
                return getMetadataFromStream(pDeserializer, inputstream);
            }
        }
    }

    public static class PathResourcesSupplier implements Pack.ResourcesSupplier {
        private final Path content;
        protected final IModFile modFile;

        public PathResourcesSupplier(IModFile modFile, Path pContent) {
            this.content = pContent;
            this.modFile = modFile;
        }

        @Override
        public @NotNull ESModFilePackResources openPrimary(@NotNull PackLocationInfo pLocation) {
            return new ESModFilePackResources(pLocation, this.modFile, this.content.toString());
        }

        @Override
        public @NotNull PackResources openFull(@NotNull PackLocationInfo pLocation, Pack.Metadata pMetadata) {
            ESModFilePackResources packResources = this.openPrimary(pLocation);
            List<String> list = pMetadata.overlays();
            if (list.isEmpty()) {
                return packResources;
            } else {
                List<PackResources> list1 = new ArrayList<>(list.size());

                for (String s : list) {
                    Path path = this.content.resolve(s);
                    list1.add(new PathPackResources(pLocation, path));
                }

                // return new CompositePackResources(packresources, list1);
                return null;
            }
        }
    }
}

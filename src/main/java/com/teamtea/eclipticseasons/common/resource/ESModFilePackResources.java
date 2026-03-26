package com.teamtea.eclipticseasons.common.resource;


import net.minecraft.server.packs.*;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.metadata.pack.PackFormat;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.util.InclusiveRange;
import net.neoforged.neoforgespi.locating.IModFile;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ESModFilePackResources extends PathPackResources {
    protected final IModFile modFile;
    protected final String sourcePath;
    private final PackMetadataSection bindSection;


    public ESModFilePackResources(PackLocationInfo locationInfo, IModFile modFile, String sourcePath) {
        super(locationInfo, Path.of(modFile.getContents().findFile(sourcePath+"/pack.mcmeta").get().resolve(".")));
        this.modFile = modFile;
        this.sourcePath = sourcePath;
        this.bindSection = new PackMetadataSection(locationInfo.title(),
                InclusiveRange.create(PackFormat.of(0),PackFormat.of(100)).getOrThrow());
    }

    private @Nullable ResourceMetadata metadata;

    @Override
    public <T> @Nullable T getMetadataSection(MetadataSectionType<T> metadataSerializer) throws IOException {
        if (this.metadata == null) {
            this.metadata = loadMetadata(this);
        }

        return this.metadata.getSection(metadataSerializer).orElse(null);
    }

    public static ResourceMetadata loadMetadata(PackResources packResources) throws IOException {
        IoSupplier<InputStream> metadata = packResources.getRootResource("pack.mcmeta");
        if (metadata == null) {
            return ResourceMetadata.EMPTY;
        } else {
            ResourceMetadata var3;
            try (InputStream resource = metadata.get()) {
                var3 = ResourceMetadata.fromJsonStream(resource);
            }

            return var3;
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

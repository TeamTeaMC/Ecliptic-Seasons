package com.teamtea.eclipticseasons.common.resource;


import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.SharedConstants;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.metadata.pack.PackFormat;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.util.FileUtil;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.Util;
import net.neoforged.neoforgespi.locating.IModFile;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Stream;

public class ESModFilePackResources extends AbstractPackResources {
    protected final IModFile modFile;
    protected final String sourcePath;
    private final PackMetadataSection bindSection;
    // private final URI root;
    private final String packdir;

    public ESModFilePackResources(PackLocationInfo locationInfo, IModFile modFile, String sourcePath) {
        super(locationInfo);
        packdir = sourcePath.replace("\\", "/") + "/";
        // URI not supported blank
        // this.root = modFile.getContents().findFile(packdir+ "pack.mcmeta").get().resolve(".");
        this.modFile = modFile;
        this.sourcePath = sourcePath;
        this.bindSection = new PackMetadataSection(locationInfo.title(),
                InclusiveRange.create(PackFormat.of(0), PackFormat.of(100)).getOrThrow());
    }

    private @Nullable ResourceMetadata metadata;

    @Override
    public <T> @Nullable T getMetadataSection(MetadataSectionType<T> metadataSerializer) throws IOException {
        if (this.metadata == null) {
            this.metadata = loadMetadata(this);
        }

        return this.metadata.getSection(metadataSerializer).orElse(null);
    }

    @Override
    public void close() {

    }

    public static ResourceMetadata loadMetadata(PackResources packResources) throws IOException {
        IoSupplier<InputStream> metadata = packResources.getRootResource("pack.mcmeta");
        if (metadata == null) {
            return ResourceMetadata.EMPTY;
        } else {
            // System.out.println(new String(metadata.get().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8));
            ResourceMetadata var3;
            try (InputStream resource = metadata.get()) {
                var3 = ResourceMetadata.fromJsonStream(resource);
            }

            return var3;
        }
    }

    @Override
    public @Nullable IoSupplier<InputStream> getRootResource(String... path) {
        String relativePath = packdir + String.join("/", path);
        return modFile.getContents().containsFile(relativePath)
                ? () -> modFile.getContents().openFile(relativePath)
                : null;
    }

    @Override
    public @Nullable IoSupplier<InputStream> getResource(PackType type, Identifier location) {
        return FileUtil.decomposePath(location.getPath()).mapOrElse(parts -> {
            String relativePath = buildResourcePath(type, location.getNamespace(), parts);
            return modFile.getContents().containsFile(relativePath)
                    ? () -> modFile.getContents().openFile(relativePath)
                    : null;
        }, _ -> null);
    }

    @Override
    public void listResources(PackType type, String namespace, String directory, ResourceOutput output) {
        FileUtil.decomposePath(directory).ifSuccess(parts -> {
            String namespaceRoot = type.getDirectory() + "/" + namespace + "/";
            int startIndex = packdir.length() + namespaceRoot.length();
            String scanPrefix = buildResourcePath(type, namespace, parts);

            modFile.getContents().visitContent(scanPrefix, (relativePath, resource) -> {
                if (!relativePath.contains(namespaceRoot)) {
                    return;
                }

                if (!modFile.getContents().containsFile(relativePath)) {
                    return;
                }

                String resourcePath = relativePath.substring(startIndex);
                Identifier identifier = Identifier.tryBuild(namespace, resourcePath);
                if (identifier == null) {
                    Util.logAndPauseIfInIde(String.format(
                            Locale.ROOT,
                            "Invalid path in pack: %s:%s, ignoring",
                            namespace,
                            resourcePath
                    ));
                    return;
                }

                output.accept(identifier, () -> modFile.getContents().openFile(relativePath));
            });
        }).ifError(error -> EclipticSeasons.LOGGER.error("Invalid path {}: {}", directory, error.message()));
    }

    @Override
    public @NonNull Set<String> getNamespaces(PackType type) {
        Set<String> namespaces = new HashSet<>();
        String rootPrefix = packdir + type.getDirectory() + "/";

        modFile.getContents().visitContent(rootPrefix, (relativePath, resource) -> {
            if (!relativePath.contains(rootPrefix)) {
                return;
            }

            if (!modFile.getContents().containsFile(relativePath)) {
                return;
            }

            String rest = relativePath.substring(rootPrefix.length());
            int slash = rest.indexOf('/');
            if (slash <= 0) {
                return;
            }

            String namespace = rest.substring(0, slash);
            if (Identifier.isValidNamespace(namespace)) {
                namespaces.add(namespace);
            }
        });
        return namespaces;
    }

    private String buildResourcePath(PackType type, String namespace, List<String> parts) {
        StringBuilder sb = new StringBuilder(packdir);
        sb.append(type.getDirectory()).append('/').append(namespace);
        for (String part : parts) {
            if (!part.isEmpty()) {
                sb.append('/').append(part);
            }
        }
        return sb.toString();
    }

    public static class PathResourcesSupplier implements Pack.ResourcesSupplier {
        private final Path content;
        protected final IModFile modFile;

        public PathResourcesSupplier(IModFile modFile, Path pContent) {
            this.content = pContent;
            this.modFile = modFile;
        }

        @Override
        public @NonNull ESModFilePackResources openPrimary(@NonNull PackLocationInfo pLocation) {
            return new ESModFilePackResources(pLocation, this.modFile, this.content.toString());
        }

        @Override
        public @NonNull PackResources openFull(@NonNull PackLocationInfo pLocation, Pack.Metadata pMetadata) {
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

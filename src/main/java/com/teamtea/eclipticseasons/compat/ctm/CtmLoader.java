package com.teamtea.eclipticseasons.compat.ctm;

import com.teamtea.eclipticseasons.EclipticSeasons;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.IdentifierException;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;

import java.io.InputStream;
import java.util.*;

public class CtmLoader {

    private final ResourceManager resourceManager;
    public Map<Block, CtmProperties> ctmStates = new IdentityHashMap<>();
    public Map<Identifier, Void> ctmTiles = new HashMap<>();

    public CtmLoader(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    public static CTMLoadingResult loadAll(ResourceManager resourceManager) {
        return (new CtmLoader(resourceManager)).loadAll();
    }

    private CTMLoadingResult loadAll() {
        int packPriority = 0;
        Iterator<PackResources> iterator = this.resourceManager.listPacks().iterator();


        while (iterator.hasNext()) {
            PackResources pack = iterator.next();
            this.loadAll(pack, packPriority);
            ++packPriority;
        }

        return new CTMLoadingResult(ctmStates, ctmTiles);
    }

    private void loadAll(PackResources pack, int packPriority) {
        for (String namespace : pack.getNamespaces(PackType.CLIENT_RESOURCES)) {
            pack.listResources(PackType.CLIENT_RESOURCES, namespace, "optifine/ctm", (resourceId, inputSupplier) -> {
                if (resourceId.getPath().endsWith(".properties")) {
                    try {
                        InputStream stream = inputSupplier.get();
                        try {
                            Properties properties = new Properties();
                            properties.load(stream);
                            for (Pair<Block, CtmProperties> pair : CtmProperties.parse(resourceId, inputSupplier, properties)) {
                                // 需要避免存在多个文件应用同一个Block
                                ctmStates.compute(pair.key(), (block, ctmProperties) ->
                                {
                                    if (ctmProperties == null) return pair.value();
                                    ctmProperties.blockStates.putAll(pair.value().blockStates);
                                    return ctmProperties;
                                });
                            }
                            String method = properties.getProperty("method", null);
                            if (method != null && method.contains("ctm")
                                    && properties.getProperty("matchTiles", null) instanceof String s) {
                                String[] split = s.split(" ");
                                for (String string : split) {
                                    try {
                                        Identifier parse = EclipticSeasons.parse(string);
                                        String[] split1 = parse.getPath().split("textures/");

                                        // 如果不以textures开头，那么就是自动补全纹理，由于存储在内存中没有这个前缀，所以我们只需要加上block就好
                                        ctmTiles.putIfAbsent(Identifier.fromNamespaceAndPath(
                                                parse.getNamespace(), split1.length == 2 ? split1[1] : "block/" + parse.getPath()
                                        ), null);
                                        // ctmTiles.putIfAbsent(Identifier.fromNamespaceAndPath(
                                        //         parse.getNamespace(), split1.length == 2 ? split1[1] : parse.getPath()
                                        // ), null);
                                    } catch (IdentifierException ignore) {
                                    }
                                }
                            }
                        } catch (Throwable throwable) {
                            try {
                                stream.close();
                            } catch (Throwable throwable1) {
                                throwable.addSuppressed(throwable1);
                            }
                            throw throwable;
                        }

                        stream.close();
                    } catch (Exception ignored) {
                    }
                }

            });
        }

    }


    public static class CTMLoadingResult {
        public Map<Block, CtmProperties> ctmStates;
        public Map<Identifier, Void> ctmTiles;

        public CTMLoadingResult(Map<Block, CtmProperties> ctmStates, Map<Identifier, Void> ctmTiles) {
            this.ctmStates = ctmStates;
            this.ctmTiles = ctmTiles;

        }
    }
}

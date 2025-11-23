package com.teamtea.eclipticseasons.compat.modernui.util;

import com.teamtea.eclipticseasons.client.reload.ClientJsonCacheListener;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.compat.modernui.state.AP;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MUIUtil {
    public static List<AP> collectAps(ResourceKey<? extends Registry<?>> key, String limit) {
        Registry<?> registry = ClientCon.getUseLevel().registryAccess().registryOrThrow(key);
        Stream<AP> apStream;
        if (!limit.startsWith("#")) {
            String typeKey = getTypeKey(key);
            apStream = registry.keySet().stream()
                    .map(k -> {
                        String biomeName = Component.translatable(Util.makeDescriptionId(typeKey, k)).getString();
                        return (k.toString().contains(limit) || biomeName.contains(limit)) ?
                                new AP(k, biomeName, false) : null;
                    });
        } else {
            apStream = registry.getTagNames()
                    .map(kk -> {
                        ResourceLocation k = kk.location();
                        String biomeName = "#" + k;
                        String substring = limit.substring(1);
                        return (k.toString().contains(substring) || biomeName.contains(substring)) ?
                                new AP(k, biomeName, true) : null;
                    });
        }
        return apStream
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static List<AP> collectModels(String path, String limit) {
        return ClientJsonCacheListener.ALL_MAP.getOrDefault(path, HashSet::new).get()
                .stream()
                .map(k -> {
                    return (k.toString().contains(limit)) ? new AP(
                            k, k.toString(), false
                    ) : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static Collection<Block> getBlocksFromStr(String s) {
        try {
            if (!s.startsWith("#")) {
                ResourceLocation resourceLocation = ResourceLocation.parse(s);
                return List.of(BuiltInRegistries.BLOCK.get(resourceLocation));
            } else {
                ResourceLocation resourceLocation = ResourceLocation.parse(s.substring(1));
                return BuiltInRegistries.BLOCK.getTag(TagKey.create(Registries.BLOCK, resourceLocation))
                        .map(ssss -> ssss.stream().map(Holder::value).toList())
                        .orElseGet(ArrayList::new);
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static List<AP> collectPropertiesFromBlocks(Collection<Block> blocks) {
        if (blocks.isEmpty()) return new ArrayList<>();
        Set<Property<?>> properties =
                new LinkedHashSet<>(blocks.stream().findFirst().get().getStateDefinition().getProperties());
        for (Block block : blocks) {
            LinkedHashSet<Property<?>> properties1 = new LinkedHashSet<>(block.getStateDefinition().getProperties());
            properties.retainAll(properties1);
        }
        return properties.stream().map(
                        p -> new AP(p.getName(), p.getName()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static String getTypeKey(ResourceKey<? extends Registry<?>> key) {
        String type;
        if (key.equals(Registries.BIOME)) {
            type = "biome";
        } else if (key.equals(Registries.BLOCK)) {
            type = "block";
        } else if (key.equals(Registries.ITEM)) {
            type = "item";
        } else {
            type = key.registry().getPath();
        }
        return type;
    }

    public static @NotNull ArrayList<AP> getSnowyBlockFlag() {
        ArrayList<AP> objects = new ArrayList<>();
        objects.add(new AP(MapChecker.FLAG_CUSTOM_JSON, "简单覆雪方块"));
        objects.add(new AP(MapChecker.FLAG_CUSTOM_JSON_PLANTS, "简单覆雪植物"));
        objects.add(new AP(MapChecker.FLAG_CUSTOM_JSON_WITH_TOP, "覆雪方块（分层）"));
        objects.add(new AP(MapChecker.FLAG_CUSTOM_JSON_WITH_TOP_LEAVES, "覆雪树叶"));
        objects.add(new AP(MapChecker.FLAG_CUSTOM_JSON_VINE_LIKE, "藤蔓类方块"));
        objects.add(new AP(MapChecker.FLAG_CUSTOM, "内置-自动处理方块"));
        objects.add(new AP(MapChecker.FLAG_CUSTOM_AO, "内置-自定义处理方块（AO）"));
        objects.add(new AP(MapChecker.FLAG_NONE, "非覆雪方块"));
        objects.add(new AP(MapChecker.FLAG_BLOCK, "内置-标准方块"));
        objects.add(new AP(MapChecker.FLAG_SLAB, "内置-半砖"));
        objects.add(new AP(MapChecker.FLAG_STAIRS, "内置-楼梯"));
        objects.add(new AP(MapChecker.FLAG_STAIRS_TOP, "内置-楼梯顶部"));
        objects.add(new AP(MapChecker.FLAG_LEAVES, "内置-树叶"));
        objects.add(new AP(MapChecker.FLAG_GRASS, "内置-草"));
        objects.add(new AP(MapChecker.FLAG_GRASS_LARGE, "内置-高草"));
        objects.add(new AP(MapChecker.FLAG_FARMLAND, "内置-耕地"));
        objects.add(new AP(MapChecker.FLAG_VINE, "内置-藤蔓"));
        return objects;
    }

}

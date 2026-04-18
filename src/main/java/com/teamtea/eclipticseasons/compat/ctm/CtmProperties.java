package com.teamtea.eclipticseasons.compat.ctm;

import com.teamtea.eclipticseasons.EclipticSeasons;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.IdentifierException;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.NonNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Refer to {@link me.pepperbell.ctm.client.properties.BaseCtmProperties CtmProperties},
 * so we can easily check which block and its model use ctm model.
 **/

public final class CtmProperties {
    private final Properties properties;
    private final Identifier resourceId;
    private final PackResources pack;
    private final int packPriority;
    private final ResourceManager resourceManager;
    private final String method;

     final IdentityHashMap<BlockState, Integer> blockStates = new IdentityHashMap<>();

    public CtmProperties(Properties properties,
                         Identifier resourceId,
                         PackResources pack,
                         int packPriority,
                         ResourceManager resourceManager,
                         String method) {
        this.properties = properties;
        this.resourceId = resourceId;
        this.pack = pack;
        this.packPriority = packPriority;
        this.resourceManager = resourceManager;
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public Properties getProperties() {
        return properties;
    }

    public Identifier getResourceId() {
        return resourceId;
    }

    // not support yet
    public PackResources getPack() {
        throw new UnsupportedOperationException();
    }

    // not support yet
    public int getPackPriority() {
        throw new UnsupportedOperationException();
    }

    // not support yet
    public ResourceManager getResourceManager() {
        throw new UnsupportedOperationException();
    }

    // 如果不检查state的话，那么我们直接跳过就可以了
    public boolean matches(BlockState blockState) {
        return blockStates.isEmpty()
                || blockStates.containsKey(blockState);
    }

    // 我们暂时不去考虑优先级问题
    public static List<Pair<Block, CtmProperties>> parse(Identifier Identifier, IoSupplier<InputStream> resource, Properties properties) {
        List<Pair<Block, CtmProperties>> pairResult = new ArrayList<>();


        if (!properties.isEmpty()) {

            String method = properties.getProperty("method", null);
            if (method != null && method.contains("ctm")) {
                String matchBlocks = properties.getProperty("matchBlocks", null);
                if (matchBlocks != null) {
                    try {
                        List<Pair<Block, List<BlockState>>> pairs = parseBlockStates(properties, "matchBlocks", Identifier);
                        if (pairs != null) {
                            for (Pair<Block, List<BlockState>> pair : pairs) {
                                CtmProperties ctmProperties = new CtmProperties(
                                        properties, Identifier,
                                        null, 0, null, method
                                );
                                for (BlockState blockState : pair.second()) {
                                    ctmProperties.blockStates.putIfAbsent(blockState, 0);
                                }
                                pairResult.add(Pair.of(pair.first(), ctmProperties));
                            }
                        }
                    } catch (IdentifierException|IndexOutOfBoundsException ignore) {
                        EclipticSeasons.logger(matchBlocks, "can not parse CTM properties");
                    }
                }

            }
        }
        return pairResult;
    }

    public static @NonNull Properties parseProperties(Resource resource) {
        Properties properties = new Properties();
        try (BufferedReader br = resource.openAsReader()) {
            properties.load(br);
        } catch (IOException ignore) {
        }
        return properties;
    }

    public static @Nullable List<Pair<Block, List<BlockState>>> parseBlockStates(Properties properties, String propertyKey, Identifier resourceId) {
        String blockStatesStr = properties.getProperty(propertyKey);
        List<Pair<Block, List<BlockState>>> pairs = null;
        if (blockStatesStr == null) {
            return null;
        } else {
            String[] blockStateStrs = blockStatesStr.trim().split(" ");
            if (blockStateStrs.length != 0) {
                pairs = new ArrayList<>();
                for (int i = 0, blockStateStrsLength = blockStateStrs.length; i < blockStateStrsLength; i++) {
                    String blockStateStr = blockStateStrs[i].strip();
                    Pair<String, List<Pair<String, Set<String>>>> parseBlockStates = parseBlockState(blockStateStr);

                    Block block1 = BuiltInRegistries.BLOCK.get(EclipticSeasons.parse(parseBlockStates.first())).map(Holder::value).orElse(Blocks.AIR);
                    if (block1 != Blocks.AIR) {
                        boolean checkState = !parseBlockStates.value().isEmpty();
                        // 如果不检查state的话，那么我们直接跳过就可以了
                        List<BlockState> blockStates = new ArrayList<>();
                        if (checkState) {
                            for (BlockState possibleState : block1.getStateDefinition().getPossibleStates()) {
                                boolean isRight = true;
                                for (Pair<String, Set<String>> pair : parseBlockStates.value()) {
                                    Property<?> property = block1.getStateDefinition().getProperty(pair.first());
                                    if (property != null) {
                                        if (!pair.second().contains(possibleState.getValue(property).toString())) {
                                            isRight = false;
                                            break;
                                        }
                                    }
                                }
                                if (isRight) blockStates.add(possibleState);
                            }
                        }
                        pairs.add(Pair.of(block1, blockStates));
                    }

                }

            }
        }
        return pairs;
    }

    private static final List<Pair<String, Set<String>>> EMPTY_STRINGS = List.of();

    public static Pair<String, List<Pair<String, Set<String>>>> parseBlockState(String input) {
        String[] parts = input.split(":");
        if (parts.length <= 2 && !input.contains("=")) {
            return Pair.of(input, EMPTY_STRINGS);
        }
        String id = "";
        int startIndex = 2;

        String[] parts_temp = input.split("=");

        if (parts.length <= 2 || parts_temp[0].length() < parts[0].length() + parts[1].length()) {
            id=parts[0];
            startIndex=1;
        } else {
            String mod = parts[0];
            String path = parts[1];
            id = mod + ":" + path;
        }

        List<Pair<String, Set<String>>> predicates = new ArrayList<>();
        for (int i = startIndex; i < parts.length; i++) {
            String part = parts[i];
            String[] split = part.split("=");
            if (split.length == 2) {
                String propertyName = split[0];
                Set<String> values = Arrays.stream(split[1].split(",")).collect(Collectors.toSet());
                predicates.add(Pair.of(propertyName, values));
            }
        }

        return Pair.of(id, predicates);
    }


}

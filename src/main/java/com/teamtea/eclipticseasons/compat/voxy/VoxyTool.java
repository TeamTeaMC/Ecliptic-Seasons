package com.teamtea.eclipticseasons.compat.voxy;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.compat.CompatModule;
import com.teamtea.eclipticseasons.compat.voxy.helper.IVoxyAboveLightingSupplier;
import com.teamtea.eclipticseasons.compat.voxy.helper.IVoxyLevelProvider;
import com.teamtea.eclipticseasons.compat.voxy.helper.VoxyESImportManager;
import com.teamtea.eclipticseasons.config.CommonConfig;
import me.cortex.voxy.common.voxelization.ILightingSupplier;
import me.cortex.voxy.common.voxelization.VoxelizedSection;
import me.cortex.voxy.common.world.WorldEngine;
import me.cortex.voxy.common.world.WorldSection;
import me.cortex.voxy.common.world.other.Mapper;
import me.cortex.voxy.commonImpl.ImportManager;
import me.cortex.voxy.commonImpl.VoxyInstance;
import me.cortex.voxy.commonImpl.WorldIdentifier;
import me.cortex.voxy.commonImpl.importers.WorldImporter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.function.IntConsumer;

public class VoxyTool {
    public static boolean isVoxyTest() {
        return CompatModule.CommonConfig.voxyTest.get();
    }


    public static int changeBlockId(int blockId, Mapper stateMapper, int i, VoxelizedSection section, ILightingSupplier lightSupplier, int biomeId) {
        if (!isVoxyTest()) return blockId;
        int maxBlockId = 0xFFFFF;
        BlockState state = stateMapper.getBlockStateFromBlockId(blockId);
        if (MapChecker.getDefaultBlockTypeFlag(state)
                > MapChecker.FLAG_NONE) {
            BlockPos offset = SectionPos.of(section.x, section.y, section.z).origin()
                    .offset(i & 15, (i >> 8 & 15), i >> 4 & 15);

            Level level = ClientCon.getUseLevel();
            if (section instanceof IVoxyLevelProvider iVoxyLevelProvider) {
                Level levelBind = iVoxyLevelProvider.getLevelBind();
                if (levelBind != null) level = levelBind;
            }
            if (level != null) {
                if (MapChecker.isLoaded(level, section.x, section.z)) {
                    if (EclipticSeasonsApi.getInstance().isSnowyBlock(level,
                            state, offset)) {
                        blockId = maxBlockId - blockId;
                    }
                } else if (lightSupplier instanceof IVoxyAboveLightingSupplier supplier) {
                    byte supply = supplier.supply(i & 15, (i >> 8 & 15) + 1, i >> 4 & 15);
                    int skyLight = (supply & 0xFF) & 0x0F;
                    if (skyLight > 9 &&
                            (!CommonConfig.Snow.notSnowyNearGlowingBlock.get() ||
                                    (((supply & 0xFF) >> 4) & 0x0F) < CommonConfig.Snow.notSnowyNearGlowingBlockLevel.getAsInt())) {
                        BlockState aboveState = supplier.getBlockState(i & 15, (i >> 8 & 15) + 1, i >> 4 & 15);
                        boolean isLight = true;
                        int flag = MapChecker.getDefaultBlockTypeFlag(state);
                        if (MapChecker.leaveLike(flag)) {

                            boolean specialLeaves = aboveState.is(state.getBlock())
                                    && (Heightmap.Types.MOTION_BLOCKING_NO_LEAVES.isOpaque().test(aboveState) ||
                                    MapChecker.extraSnowPassable(aboveState));
                            if (specialLeaves) {
                                isLight = CommonConfig.Snow.snowyTree.get();
                            }
                        } else {
                            if (MapChecker.extraSnowPassable(state)) {
                                isLight = !MapChecker.extraSnowPassable(aboveState);
                            }
                        }
                        if (isLight) {
                            String biome = stateMapper.getBiomeEntries()[biomeId].biome;
                            var holderKey = ResourceKey.create(Registries.BIOME, Identifier.parse(biome));
                            Holder<Biome> holder = level.registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(holderKey);
                            if (MapChecker.shouldSnowAtBiome(level, holder.value(), state, level.getRandom(), state.getSeed(offset), offset)) {
                                blockId = maxBlockId - blockId;
                            }
                        }
                    }
                }
            }
        }
        return blockId;
    }

    private static final int maxBlockId = 0xFFFFF;

    public static int fixId(Mapper mapper, int blockId) {
        return fixId(mapper, blockId, VoxyTool::emptyConsumer);
    }

    private static void emptyConsumer(int i) {
    }

    public static int fixId(Mapper mapper, int blockId, IntConsumer consumer) {
        int blockStateCount = mapper.getBlockStateCount();
        if (blockId < blockStateCount) return blockId;
        blockId = maxBlockId - blockId;
        if (blockId < blockStateCount) {
            consumer.accept(blockId);
            return blockId;
        }
        return maxBlockId - blockId;
    }


    public static WorldEngine getWorld(Level level) {
        return getVoxyInstance().getNullable(WorldIdentifier.of(level));
    }

    public static Mapper getMapper(Level level) {
        WorldEngine world = getWorld(level);
        return world == null ? null : world.getMapper();
    }

    public static int getSkyLightFromBlockId(long blockId) {
        // return (Mapper.getLightId(blockId) & 0xFF) & 0x0F;
        return (Mapper.getLightId(blockId) % 16);
    }

    public static WorldSection getWorldSection(WorldEngine into, SectionPos section) {
        int lvl = 0;
        return into.acquireIfExists(lvl, section.x() >> lvl + 1, section.y() >> lvl + 1, section.z() >> lvl + 1);
    }

    public static WorldSection getWorldSection(Level level, SectionPos section) {
        WorldEngine world = getWorld(level);
        return world == null ? null : getWorldSection(world, section);
    }


    public static ImportManager esImporter;

    public static void releaseImporter() {
        VoxyTool.esImporter = null;
    }

    public static void tryUpdate() {
        if (!isVoxyTest()) return;
        if (!CompatModule.CommonConfig.voxyLODAutoReload.get()) return;

        Level level = ClientCon.getUseLevel();
        if (level == null || level.getGameTime() % (20 * 15) == 0
                || !ClientCon.getAgent().isSnowChange()
                // || !MapChecker.isValidDimension(level)
                || esImporter != null)
            return;

        // if (!(VoxyCommon.getInstance() instanceof VoxyClientInstance instance)) {
        //    return;
        //}
        // VoxyInstance instance = VoxyCommon.getInstance();
        VoxyInstance instance = getVoxyInstance();

        if (instance == null) return;

        var engine = WorldIdentifier.ofEngine(level);
        if (engine == null) return;

        ClientCon.agent.setSnowChange(false);

        esImporter = new VoxyESImportManager();

        // MixinAccessorModelFactory factory = (MixinAccessorModelFactory) ((MixinAccessorVoxyRenderSystem) ((IGetVoxyRenderSystem)
        //        Minecraft.getInstance().levelRenderer)
        //        .getVoxyRenderSystem()).getModelBakerySubsystem()
        //        .factory;
        //
        // Arrays.fill(factory.getIdMappings(), -1);
        // factory.getModelTexture2id().clear();


        VoxyInstance finalInstance = instance;
        esImporter.makeAndRunIfNone(engine, () -> {
            var importer = new WorldImporter(engine, level, finalInstance.getServiceManager(), finalInstance.savingServiceRateLimiter);
            String worldName = ClientCon.getAgent().getCurrentWorldName();
            Path file = (new File("saves")).toPath().resolve(worldName);
            if (!worldName.endsWith("region")) {
                file = file.resolve("region");
            }
            importer.importRegionDirectoryAsync(file.toFile());
            return importer;
        });
    }

    private static @Nullable VoxyInstance getVoxyInstance() {
        VoxyInstance instance = null;

        try {
            Class<?> clazz = Class.forName("me.cortex.voxy.commonImpl.VoxyCommon");
            Method method = clazz.getDeclaredMethod("getInstance");
            instance = (VoxyInstance) method.invoke(null);
        } catch (Exception _) {
        }
        return instance;
    }
}

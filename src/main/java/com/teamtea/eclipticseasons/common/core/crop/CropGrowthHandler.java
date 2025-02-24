package com.teamtea.eclipticseasons.common.core.crop;


import com.ferreusveritas.dynamictrees.api.registry.Registries;
import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.crop.CropHumidityInfo;
import com.teamtea.eclipticseasons.api.constant.crop.CropSeasonInfo;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.tag.EclipticBlockTags;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.SaplingGrowTreeEvent;
import net.minecraftforge.eventbus.api.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;


public final class CropGrowthHandler {
    public static void beforeCropGrowUp(BlockEvent.CropGrowEvent.Pre event) {
        BlockState block = event.getState();
        IWorld world = event.getWorld();
        BlockPos pos = event.getPos();
        beforeCropGrowUp(event, world, pos, block);
    }


    public static void beforeCropGrowUp(SaplingGrowTreeEvent event) {
        IWorld world = event.getWorld();
        BlockPos pos = event.getPos();
        beforeCropGrowUp(event, world, pos, world.getBlockState(pos));
    }

    public enum RoomStatus {
        GREEN_HOUSE, NORMAL, UNKNOWN;
    }

    public enum CropSeason {
        COLD, HOT, NORMAL;

        public Season modifySeason(SolarTerm solarTerm) {
            if (this != NORMAL) {
                int ordinal = solarTerm.ordinal();
                ordinal += (ordinal > 11.5 ? 1 : -1) * (this == COLD ? 1 : -1);
                SolarTerm.collectValues()[(ordinal + 24) % 24].getSeason();
            }
            return solarTerm.getSeason();
        }

        public static CropSeason of(World level, Biome biome, BlockPos pos) {
            RegistryKey<Biome> biomeRegistryKey = RegistryKey.create(Registry.BIOME_REGISTRY, biome.getRegistryName());
            Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes(biomeRegistryKey);
            if (types.contains(BiomeDictionary.Type.COLD)) {
                return CropSeason.COLD;
            } else if (types.contains(BiomeDictionary.Type.HOT)) {
                return CropSeason.HOT;
            }
            return CropSeason.NORMAL;
        }
    }

    public static void beforeCropGrowUp(Event event, IWorld world, BlockPos pos, BlockState blockState) {
        Block block = blockState.getBlock();
        Biome biome = world.getBiome(pos);
        SolarTerm solarTerm = EclipticUtil.getNowSolarTerm((World) world);
        CropSeasonInfo seasonInfo = CropInfoManager.getSeasonInfo(block);
        boolean notCancel = false;
        RoomStatus roomStatus = RoomStatus.UNKNOWN;
        Season season = CropSeason.of((World) world, biome, pos).modifySeason(solarTerm);
        if (seasonInfo != null && CommonConfig.Crop.enableCrop.get()) {
            notCancel |= seasonInfo.isSuitable(season);
            notCancel |= CommonConfig.Crop.cropGrowChanceInWrongSeason.get() > 0
                    && world.getRandom().nextInt(100) < CommonConfig.Crop.cropGrowChanceInWrongSeason.get() * 100;
            if (!notCancel) {
                notCancel = isInRoom(world, pos, blockState, season);
                roomStatus = notCancel ? RoomStatus.GREEN_HOUSE : RoomStatus.NORMAL;
            }
        } else {
            notCancel = true;
        }

        if (!notCancel) {
            setResult(event, CANCEL);
        } else {
            if (blockState.getFluidState().isSource()) return;
            Humidity env = EclipticUtil.getHumidityAt((World) world, pos);
            CropHumidityInfo humidityInfo = CropInfoManager.getHumidityInfo(block);
            checkHumidity(event, world, humidityInfo, env, roomStatus, pos, blockState, season, false);
        }
    }


    public static void checkHumidity(Event event, IWorld world, CropHumidityInfo humidityInfo, Humidity env, RoomStatus roomStatus, BlockPos pos, BlockState blockState, Season season, boolean hasUpdate) {
        if (humidityInfo != null && CommonConfig.Crop.enableCropHumidityControl.get()) {
            float f = humidityInfo.getGrowChance(env);
            if (f == 0) {
                setResult(event, CANCEL);
            } else if (f > 1.0F) {
                setResult(event, GROW);
            } else {
                if (world.getRandom().nextInt(1000) < 1000 * f) {
                    setResult(event, PASS);
                } else {
                    boolean should = true;
                    if (!hasUpdate) {
                        roomStatus = isInRoom(world, pos, blockState, season) ? RoomStatus.GREEN_HOUSE : RoomStatus.NORMAL;
                    }
                    if (roomStatus == RoomStatus.GREEN_HOUSE) {
                        should = false;
                    }
                    if (should) {
                        should = !(CommonConfig.Crop.cropGrowChanceInWrongHumidity.get() > 0
                                && world.getRandom().nextInt(100) < CommonConfig.Crop.cropGrowChanceInWrongHumidity.get() * 100);
                    }
                    if (should) {
                        setResult(event, CANCEL);
                    }
                }
            }
        }
    }


    public static final int CANCEL = 1;
    public static final int PASS = 2;
    public static final int GROW = 3;


    public static void setResult(Event event, int flag) {
        if (event.hasResult()) {
            if (flag == CANCEL) {
                if (event instanceof BlockEvent.CropGrowEvent.Pre) {
                    event.setResult(Event.Result.DENY);
                } else if (event instanceof SaplingGrowTreeEvent) {
                    event.setResult(Event.Result.DENY);
                }
            } else if (flag == PASS) {
                if (event instanceof BlockEvent.CropGrowEvent.Pre) {
                    event.setResult(Event.Result.DEFAULT);
                }
            } else if (flag == GROW) {
                if (event instanceof BlockEvent.CropGrowEvent.Pre) {
                    event.setResult(Event.Result.ALLOW);
                }
            }
        }

    }

    public static final Vector3d[] CHECK_DIRECTIONS = {
            new Vector3d(0, 1, 0),
            new Vector3d(1, 0, 0),
            new Vector3d(-1, 0, 0),
            new Vector3d(0, 0, 1),
            new Vector3d(0, 0, -1),

            new Vector3d(1, 1, 0),
            new Vector3d(-1, 1, 0),
            new Vector3d(0, 1, 1),
            new Vector3d(0, 1, -1),

            new Vector3d(1, 0, 1),
            new Vector3d(1, 0, -1),
            new Vector3d(-1, 0, 1),
            new Vector3d(-1, 0, -1),

            new Vector3d(1, 1, 1),
            new Vector3d(1, 1, -1),
            new Vector3d(-1, 1, 1),
            new Vector3d(-1, 1, -1),
    };

    public static final Vector3d[] CHECK_DIRECTIONS_SIMPLE = {
            new Vector3d(0, 1, 0),
            new Vector3d(1, 0, 0),
            new Vector3d(-1, 0, 0),
            new Vector3d(0, 0, 1),
            new Vector3d(0, 0, -1),

    };

    public static class SectionClipContext extends RayTraceContext {
        public final List<Pair<SectionPos, ChunkSection>> chunkAccessList = new ArrayList<>(1);

        public SectionClipContext(Vector3d from, Vector3d to, RayTraceContext.BlockMode pBlock, RayTraceContext.FluidMode pFluid, Entity pEntity) {
            super(from, to, pBlock, pFluid, pEntity);
        }

        public List<Pair<SectionPos, ChunkSection>> getChunkAccessList() {
            return chunkAccessList;
        }

        public BlockState getBlockState(IWorld levelAccessor, BlockPos pos) {
            int x = SectionPos.blockToSectionCoord(pos.getX());
            int z = SectionPos.blockToSectionCoord(pos.getZ());
            int y = SectionPos.blockToSectionCoord(pos.getY());
            for (int i = 0, size = this.chunkAccessList.size(); i < size; i++) {
                Pair<SectionPos, ChunkSection> chunkAccess = this.chunkAccessList.get(i);
                if (chunkAccess.getFirst().x() == x
                        && chunkAccess.getFirst().z() == z
                        && chunkAccess.getFirst().y() == y) {
                    return chunkAccess.getSecond().getBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
                }
            }
            ChunkSection chunk = levelAccessor.getChunk(x, z).getSections()[pos.getY() >> 4];
            this.chunkAccessList.add(Pair.of(SectionPos.of(x, y, z), chunk));
            return chunk.getBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
        }

        public void release() {
            this.chunkAccessList.clear();
        }
    }

    public static final class BlockTester implements BiFunction<RayTraceContext, BlockPos, BlockRayTraceResult> {
        private final IWorld levelReader;

        public BlockTester(
                IWorld levelReader) {
            this.levelReader = levelReader;
        }

        @Override
        public BlockRayTraceResult apply(RayTraceContext context, BlockPos pos) {
            SectionClipContext clipContext = (SectionClipContext) context;

            BlockState blockstate = clipContext.getBlockState(levelReader, pos);

            if (!blockstate.getMaterial().isSolid())
                return null;
            BlockRayTraceResult blockHitResult;
            if (blockstate.is(EclipticBlockTags.NOT_GREEN_HOUSE_MATERIAL)) {
                blockHitResult = FAIL_HANDLER.apply(clipContext);
            } else {
                Vector3d vec3 = clipContext.getFrom();
                Vector3d vec31 = clipContext.getTo();
                VoxelShape voxelshape = clipContext.getBlockShape(blockstate, levelReader, pos);
                blockHitResult = voxelshape.clip(vec3, vec31, pos);
            }
            if (blockHitResult != null)
                clipContext.release();
            return blockHitResult;
        }

    }


    private static final FailHandler FAIL_HANDLER = new FailHandler();

    public static class FailHandler implements Function<RayTraceContext, BlockRayTraceResult> {
        @Override
        public BlockRayTraceResult apply(RayTraceContext context) {
            SectionClipContext clipContext = (SectionClipContext) context;
            clipContext.release();
            Vector3d vec3 = clipContext.getFrom().subtract(clipContext.getTo());
            return BlockRayTraceResult.miss(clipContext.getTo(), Direction.getNearest(vec3.x, vec3.y, vec3.z), new BlockPos(clipContext.getTo()));
        }
    }

    public static BlockRayTraceResult clip(IWorld levelAccessor, SectionClipContext context) {
        return IBlockReader.traverseBlocks(
                context,
                new BlockTester(levelAccessor),
                FAIL_HANDLER);
    }

    public static boolean isInRoom(IWorld level, BlockPos pos, BlockState state, Season season) {
        if (state.getFluidState().isSource()) return false;
        int brightness = level.getBrightness(LightType.SKY, pos.above());
        boolean isInLight = brightness > 12;
        if (isInLight) {
            int height = level.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ());
            if (height < pos.getY()) return false;
        }

        if (season == Season.SUMMER) {
            if (isInLight && EclipticUtil.isDay((World) level))
                return false;
        }

        boolean isConnected = true;


        int maxDistance = CommonConfig.Crop.greenHouseMaxDiameter.get();
        int y_maxDistance = CommonConfig.Crop.greenHouseMaxHeight.get();
        Vector3d centerVec = Vector3d.atCenterOf(pos);
        Vector3d[] vec3s = CommonConfig.Crop.complexGreenHouseCheck.get() ?
                CHECK_DIRECTIONS : CHECK_DIRECTIONS_SIMPLE;

        float xr = (float) level.getRandom().nextGaussian() / 3f;
        float yr = (float) level.getRandom().nextGaussian() / 3f;
        for (int i = 0, vec3sLength = vec3s.length; i < vec3sLength; i++) {
            Vector3d direction = vec3s[i];
            direction = direction.add(xr, 0, yr);
            Vector3d startVec = centerVec;

            // direction是否要限制为圆形
            // direction=direction.normalize();

            Vector3d endVec = centerVec.add(direction.scale(direction.y == 0 ?
                    maxDistance : y_maxDistance));

            SectionClipContext context = new SectionClipContext(startVec, endVec,
                    RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.SOURCE_ONLY, null);
            BlockRayTraceResult hitResult = clip(level, context);
            if (hitResult.getType() == BlockRayTraceResult.Type.MISS) {
                isConnected = false;
                break;
            }
        }

        if (isConnected && !isInLight) {
            isConnected = level.getRandom().nextInt(5) > 2;
        }
        return isConnected;
    }

}

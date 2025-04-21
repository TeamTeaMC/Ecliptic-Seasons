package com.teamtea.eclipticseasons.common.core.crop;


import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.crop.CropHumidityInfo;
import com.teamtea.eclipticseasons.api.constant.crop.CropSeasonInfo;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.tag.EclipticBlockTags;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.SaplingGrowTreeEvent;
import net.minecraftforge.eventbus.api.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;


public final class CropGrowthHandler {
    public static void beforeCropGrowUp(BlockEvent.CropGrowEvent.Pre event) {
        var block = event.getState();
        var world = event.getLevel();
        BlockPos pos = event.getPos();
        beforeCropGrowUp(event, world, pos, block);
    }


    public static void beforeCropGrowUp(SaplingGrowTreeEvent event) {
        var world = event.getLevel();
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

        public static CropSeason of(Level level, Holder<Biome> biome, BlockPos pos) {
            if (biome.is(Tags.Biomes.IS_COLD)) {
                return CropSeason.COLD;
            } else if (biome.is(Tags.Biomes.IS_HOT)) {
                return CropSeason.HOT;
            }
            return CropSeason.NORMAL;
        }
    }

    public static void beforeCropGrowUp(Event event, LevelAccessor world, BlockPos pos, BlockState blockState) {
        Block block = blockState.getBlock();
        Holder<Biome> biome = world.getBiome(pos);
        SolarTerm solarTerm = EclipticUtil.getNowSolarTerm((Level) world);
        CropSeasonInfo seasonInfo = CropInfoManager.getSeasonInfo(block);
        boolean notCancel = false;
        RoomStatus roomStatus = RoomStatus.UNKNOWN;
        Season season = CropSeason.of((Level) world, biome, pos).modifySeason(solarTerm);
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
            Humidity env = EclipticUtil.getHumidityAt((Level) world, pos);
            CropHumidityInfo humidityInfo = CropInfoManager.getHumidityInfo(block);
            checkHumidity(event, world, humidityInfo, env, roomStatus, pos, blockState, season, false);
        }
    }


    public static void checkHumidity(Event event, LevelAccessor world, CropHumidityInfo humidityInfo, Humidity env, RoomStatus roomStatus, BlockPos pos, BlockState blockState, Season season, boolean hasUpdate) {
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
                if (event instanceof BlockEvent.CropGrowEvent.Pre cropGrowEvent) {
                    cropGrowEvent.setResult(Event.Result.DENY);
                } else if (event instanceof SaplingGrowTreeEvent blockGrowFeatureEvent) {
                    blockGrowFeatureEvent.setResult(Event.Result.DENY);
                }
            } else if (flag == PASS) {
                if (event instanceof BlockEvent.CropGrowEvent.Pre cropGrowEvent) {
                    cropGrowEvent.setResult(Event.Result.DEFAULT);
                }
            } else if (flag == GROW) {
                if (event instanceof BlockEvent.CropGrowEvent.Pre cropGrowEvent) {
                    cropGrowEvent.setResult(Event.Result.ALLOW);
                }
            }
        }

    }

    public static final Vec3[] CHECK_DIRECTIONS = {
            new Vec3(0, 1, 0),
            new Vec3(1, 0, 0),
            new Vec3(-1, 0, 0),
            new Vec3(0, 0, 1),
            new Vec3(0, 0, -1),

            new Vec3(1, 1, 0),
            new Vec3(-1, 1, 0),
            new Vec3(0, 1, 1),
            new Vec3(0, 1, -1),

            new Vec3(1, 0, 1),
            new Vec3(1, 0, -1),
            new Vec3(-1, 0, 1),
            new Vec3(-1, 0, -1),

            new Vec3(1, 1, 1),
            new Vec3(1, 1, -1),
            new Vec3(-1, 1, 1),
            new Vec3(-1, 1, -1),
    };

    public static final Vec3[] CHECK_DIRECTIONS_SIMPLE = {
            new Vec3(0, 1, 0),
            new Vec3(1, 0, 0),
            new Vec3(-1, 0, 0),
            new Vec3(0, 0, 1),
            new Vec3(0, 0, -1),

    };

    public static class SectionClipContext extends ClipContext {
        public final List<Pair<SectionPos, LevelChunkSection>> chunkAccessList = new ArrayList<>(1);

        public SectionClipContext(Vec3 from, Vec3 to, Block block, Fluid fluid, Entity pEntity) {
            super(from, to, block, fluid, pEntity);
        }

        public List<Pair<SectionPos, LevelChunkSection>> getChunkAccessList() {
            return chunkAccessList;
        }

        public BlockState getBlockState(LevelReader levelAccessor, BlockPos pos) {
            int x = SectionPos.blockToSectionCoord(pos.getX());
            int z = SectionPos.blockToSectionCoord(pos.getZ());
            int y = SectionPos.blockToSectionCoord(pos.getY());
            for (int i = 0, size = this.chunkAccessList.size(); i < size; i++) {
                Pair<SectionPos, LevelChunkSection> chunkAccess = this.chunkAccessList.get(i);
                if (chunkAccess.getFirst().x() == x
                        && chunkAccess.getFirst().z() == z
                        && chunkAccess.getFirst().y() == y) {
                    return chunkAccess.getSecond().getBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
                }
            }
            LevelChunkSection chunk = levelAccessor.getChunk(x, z).getSection(levelAccessor.getSectionIndex(pos.getY()));
            this.chunkAccessList.add(Pair.of(SectionPos.of(x, y, z), chunk));
            return chunk.getBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
        }

        public void release() {
            this.chunkAccessList.clear();
        }
    }

    public record BlockTester(
            LevelReader levelReader) implements BiFunction<SectionClipContext, BlockPos, BlockHitResult> {

        @Override
        public BlockHitResult apply(SectionClipContext clipContext, BlockPos pos) {
            BlockState blockstate = clipContext.getBlockState(levelReader, pos);

            if (!blockstate.getMaterial().isSolid())
                return null;
            BlockHitResult blockHitResult;
            if (blockstate.is(EclipticBlockTags.NOT_GREEN_HOUSE_MATERIAL)) {
                blockHitResult = FAIL_HANDLER.apply(clipContext);
            } else {
                Vec3 vec3 = clipContext.getFrom();
                Vec3 vec31 = clipContext.getTo();
                VoxelShape voxelshape = clipContext.getBlockShape(blockstate, levelReader, pos);
                blockHitResult = voxelshape.clip(vec3, vec31, pos);
            }
            if (blockHitResult != null)
                clipContext.release();
            return blockHitResult;
        }
    }

    private static final FailHandler FAIL_HANDLER = new FailHandler();

    public static class FailHandler implements Function<SectionClipContext, BlockHitResult> {
        @Override
        public BlockHitResult apply(SectionClipContext clipContext) {
            clipContext.release();
            Vec3 vec3 = clipContext.getFrom().subtract(clipContext.getTo());
            return BlockHitResult.miss(clipContext.getTo(), Direction.getNearest(vec3.x, vec3.y, vec3.z), new BlockPos(clipContext.getTo()));
        }
    }

    public static BlockHitResult clip(LevelReader levelAccessor, SectionClipContext context) {
        return BlockGetter.traverseBlocks(context.getFrom(),
                context.getTo(),
                context,
                new BlockTester(levelAccessor),
                FAIL_HANDLER);
    }

    public static boolean isInRoom(LevelAccessor level, BlockPos pos, BlockState state, Season season) {
        // if (state.getFluidState().isSource()) return false;
        int brightness = level.getBrightness(LightLayer.SKY, pos.above());
        boolean isInLight = brightness > 12;
        if (isInLight) {
            int height = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ());
            if (height < pos.getY()) return false;
        }

        if (season == Season.SUMMER) {
            if (isInLight && EclipticUtil.isDay((Level) level))
                return false;
        }

        boolean isConnected = true;


        int maxDistance = CommonConfig.Crop.greenHouseMaxDiameter.get();
        int y_maxDistance = CommonConfig.Crop.greenHouseMaxHeight.get();
        Vec3 centerVec = Vec3.atCenterOf(pos);
        Vec3[] vec3s = CommonConfig.Crop.complexGreenHouseCheck.get() ?
                CHECK_DIRECTIONS : CHECK_DIRECTIONS_SIMPLE;

        float xr = (float) level.getRandom().nextGaussian() / 3f;
        float yr = (float) level.getRandom().nextGaussian() / 3f;
        for (int i = 0, vec3sLength = vec3s.length; i < vec3sLength; i++) {
            Vec3 direction = vec3s[i];
            direction = direction.add(xr, 0, yr);
            Vec3 startVec = centerVec;

            // direction是否要限制为圆形
            // direction=direction.normalize();

            Vec3 endVec = centerVec.add(direction.scale(direction.y == 0 ?
                    maxDistance : y_maxDistance));

            SectionClipContext context = new SectionClipContext(startVec, endVec,
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.WATER, null);
            HitResult hitResult = clip(level, context);

            if (hitResult.getType() == HitResult.Type.MISS) {
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

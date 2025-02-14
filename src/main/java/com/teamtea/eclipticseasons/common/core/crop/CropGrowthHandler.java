package com.teamtea.eclipticseasons.common.core.crop;


import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.crop.CropHumidityInfo;
import com.teamtea.eclipticseasons.api.constant.crop.CropSeasonInfo;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.PosAndBlockStateCheck;
import com.teamtea.eclipticseasons.api.data.WetterStructure;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import com.teamtea.eclipticseasons.common.handler.SolarUtil;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockGrowFeatureEvent;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;

import java.util.*;
import java.util.function.Predicate;


public final class CropGrowthHandler {
    public static void beforeCropGrowUp(CropGrowEvent.Pre event) {
        var block = event.getState();
        var world = event.getLevel();
        BlockPos pos = event.getPos();
        beforeCropGrowUp(event, world, pos, block);
    }


    public static void beforeCropGrowUp(BlockGrowFeatureEvent event) {
        var world = event.getLevel();
        BlockPos pos = event.getPos();
        beforeCropGrowUp(event, world, pos, world.getBlockState(pos));
    }

    private final static List<WetterStructure> wetterStructures = new ArrayList<>();

    public static void resetUpdate(RegistryAccess registryAccess, boolean isServer) {
        if (isServer) {
            wetterStructures.clear();
            Registry<WetterStructure> structures = registryAccess.registryOrThrow(ESRegistries.WETTER);
            for (WetterStructure structure : structures) {
                wetterStructures.add(structure);
            }
        }
        // else {
        //     wetterStructures.clear();
        // }
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

    public static void beforeCropGrowUp(net.neoforged.bus.api.Event event, LevelAccessor world, BlockPos pos, BlockState blockState) {
        Block block = blockState.getBlock();
        CropSeasonInfo seasonInfo = CropInfoManager.getSeasonInfo(block);
        boolean notCancel = false;
        Holder<Biome> biome = world.getBiome(pos);
        SolarTerm solarTerm = SolarUtil.getSolarTerm((Level) world);
        RoomStatus roomStatus = RoomStatus.UNKNOWN;
        Season season = CropSeason.of((Level) world, biome, pos).modifySeason(solarTerm);
        if (seasonInfo != null && CommonConfig.Crop.enableCrop.get()) {
            // TODO：计算本地节气？
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
            // TODO:根据降雨量调整
            Humidity env = Humidity.getHumid(solarTerm, biome);
            CropHumidityInfo humidityInfo = CropInfoManager.getHumidityInfo(block);
            checkHumidity(event, world, humidityInfo, env, roomStatus, pos, blockState, season, false);
        }
    }


    // TODO：平衡调整，平原夏天是干旱？
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
                    // 或者用特殊气体，BlockEntity辅助查询
                    boolean should = true;
                    if (!hasUpdate) {
                        WetterStructure nearState = SolarHolders.getSaveData((Level) world).findNearPos(pos);
                        if (nearState != null) {
                            roomStatus = isInRoom(world, pos, blockState, season) ? RoomStatus.GREEN_HOUSE : RoomStatus.NORMAL;
                        }
                        if (nearState != null && roomStatus == RoomStatus.GREEN_HOUSE) {
                            env = env.above(nearState.level());
                            checkHumidity(event, world, humidityInfo, env, roomStatus, pos, blockState, season, true);
                            should = false;
                            return;
                            //  TODO:下雨增加湿润度
                        } else if (((Level) world).isRainingAt(pos)) {
                            env = env.above(1);
                            checkHumidity(event, world, humidityInfo, env, roomStatus, pos, blockState, season, true);
                            should = false;
                            return;
                        }

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


    public static void setResult(net.neoforged.bus.api.Event event, int flag) {
        if (flag == CANCEL) {
            if (event instanceof CropGrowEvent.Pre cropGrowEvent) {
                cropGrowEvent.setResult(CropGrowEvent.Pre.Result.DO_NOT_GROW);
            } else if (event instanceof BlockGrowFeatureEvent blockGrowFeatureEvent) {
                blockGrowFeatureEvent.setCanceled(true);
            }
        } else if (flag == PASS) {
            if (event instanceof CropGrowEvent.Pre cropGrowEvent) {
                cropGrowEvent.setResult(CropGrowEvent.Pre.Result.DEFAULT);
            }
        } else if (flag == GROW) {
            if (event instanceof CropGrowEvent.Pre cropGrowEvent) {
                cropGrowEvent.setResult(CropGrowEvent.Pre.Result.GROW);
            }
        }
    }

    public static final Vec3[] CHECK_DIRECTIONS = {
            // 基本方向
            new Vec3(1, 0, 0), // 向前
            new Vec3(-1, 0, 0), // 向后
            new Vec3(0, 1, 0), // 向上
            new Vec3(0, 0, 1), // 向右
            new Vec3(0, 0, -1), // 向左

            // 组合方向
            new Vec3(1, 1, 0), // 向前上
            new Vec3(-1, 1, 0), // 向后上
            new Vec3(0, 1, 1), // 向上右
            new Vec3(0, 1, -1), // 向上左

            new Vec3(1, 0, 1), // 向前右
            new Vec3(1, 0, -1), // 向前左
            new Vec3(-1, 0, 1), // 向后右
            new Vec3(-1, 0, -1), // 向后左

            // 三维组合方向
            new Vec3(1, 1, 1), // 向前上右
            new Vec3(1, 1, -1), // 向前上左
            new Vec3(-1, 1, 1), // 向后上右
            new Vec3(-1, 1, -1), // 向后上左
    };

    public static final Vec3[] CHECK_DIRECTIONS_SIMPLE = {
            // 基本方向
            new Vec3(1, 0, 0), // 向前
            new Vec3(-1, 0, 0), // 向后
            new Vec3(0, 1, 0), // 向上
            new Vec3(0, 0, 1), // 向右
            new Vec3(0, 0, -1), // 向左

    };

    public static boolean isInRoom(LevelAccessor level, BlockPos pos, BlockState state, Season season) {
        if (state.getFluidState().isSource()) return false;

        boolean isInLight = level.getBrightness(LightLayer.SKY, pos.above()) > 12;
        if (isInLight) {
            int height = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ());
            if (height < pos.getY()) return false;
        }
        if (season == Season.SUMMER) {
            if (isInLight && EclipticUtil.isDay((Level) level))
                return false;
        }
        boolean isConnected = true;

        Predicate<BlockState> blockStatePredicate = b -> (b.is(Tags.Blocks.GLASS_BLOCKS)
                || b.is(Tags.Blocks.VILLAGER_FARMLANDS)
                || b.getFluidState().is(FluidTags.WATER)
                || b.getBlock() instanceof DoorBlock);

        int maxDistance = CommonConfig.Crop.greenHouseMaxDiameter.get();
        int y_maxDistance = Math.max(7, maxDistance / 2);
        Vec3 centerVec = pos.getCenter();
        for (Vec3 direction : CommonConfig.Crop.complexGreenHouseCheck.get() ?
                CHECK_DIRECTIONS : CHECK_DIRECTIONS_SIMPLE) {

            // TODO: 最小起步点
            Vec3 startVec = centerVec.add(direction.scale(2));
            Vec3 endVec = centerVec.add(direction.scale(direction.y > 0 ?
                    maxDistance : y_maxDistance));

            ClipContext context = new ClipContext(startVec, endVec,
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.WATER, CollisionContext.empty());
            HitResult hitResult = level.clip(context);

            if (hitResult.getType() == HitResult.Type.MISS) {
                isConnected = false;
                break;
            }
        }

        // TODO:qucikly for windows green house
        if (isConnected && !isInLight) {
            isConnected = level.getRandom().nextInt(5) > 0;
        }
        return isConnected;
    }

    public static void checkIfRoom(PlayerInteractEvent.RightClickBlock event) {

        if (event.getHand() == InteractionHand.MAIN_HAND
                && event.getItemStack().isEmpty()
                && !FMLEnvironment.production) {
            LevelAccessor level = event.getLevel();
            BlockPos pos = event.getPos();
            BlockState blockState = level.getBlockState(pos);
            if (!blockState.blocksMotion()) {
                boolean isConnected = isInRoom(level, pos, blockState, EclipticUtil.getNowSolarTerm((Level) level).getSeason());
                event.getEntity().displayClientMessage(Component.literal("" + isConnected), true);
            }
        }
    }

    public static void unloadChunk(Level level, ChunkPos pos) {
        SolarHolders.getSaveData(level).unloadChunk(pos);
    }

    public static void handleRandomTick(Level serverLevel, LevelChunk chunk, BlockPos blockPos, BlockState blockState) {
        // if (blockState.is(Blocks.BUBBLE_COLUMN)
        //         && chunk.getBlockState(blockPos.above()).isAir()
        //         && chunk.getBlockState(blockPos.below()).is(Blocks.MAGMA_BLOCK)) {
        //     SolarDataManager saveData = SolarHolders.getSaveData(serverLevel);
        //     saveData.addMap(blockPos, blockState);
        // }
        boolean hasFound = false;
        WetterStructure needAdd = null;
        for (int j = 0, wetterStructuresSize = wetterStructures.size(); j < wetterStructuresSize; j++) {
            WetterStructure structure = wetterStructures.get(j);
            boolean needSkip = structure.core().isEmpty()
                    || (!structure.core().get().match(blockState));
            if (!needSkip) {
                SolarDataManager saveData = SolarHolders.getSaveData(serverLevel);
                WetterStructure nearPos = saveData.findNearPos(blockPos);
                if (nearPos != null) needSkip = true;
            }
            if (!needSkip) {
                if (structure.enableAirCheck()) {
                    needSkip = chunk.getBlockState(blockPos).isEmpty();
                }
            }
            if (!needSkip) {
                List<PosAndBlockStateCheck> blockStatePredicate = structure.blockStatePredicate();
                for (int i = 0, blockStatePredicateSize = blockStatePredicate.size(); i < blockStatePredicateSize; i++) {

                    PosAndBlockStateCheck check = blockStatePredicate.get(i);
                    BlockState stateTested;
                    if (check.offset().equals(Vec3i.ZERO)) {
                        stateTested = blockState;
                    } else {
                        stateTested = chunk.getBlockState(blockPos.offset(check.offset()));
                    }
                    if (!check.block().match(stateTested)) {
                        needSkip = true;
                        break;
                    }
                }
            }
            if (!needSkip) {
                hasFound = true;
                needAdd = structure;
                break;
            }
        }
        if (hasFound) {
            SolarDataManager saveData = SolarHolders.getSaveData(serverLevel);
            saveData.addMap(blockPos, needAdd);
        }
    }

    public static void handleRandomTick2(Level level, LevelChunk chunk) {
        SolarDataManager saveData = SolarHolders.getSaveData(level);
        saveData.randomClearSome(chunk.getPos(), level.getRandom());
    }
}

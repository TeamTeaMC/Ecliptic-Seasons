package com.teamtea.eclipticseasons.compat.distanthorizons;

import com.seibel.distanthorizons.core.api.internal.SharedApi;
import com.seibel.distanthorizons.core.config.Config;
import com.seibel.distanthorizons.core.dataObjects.fullData.FullDataPointIdMap;
import com.seibel.distanthorizons.core.level.ClientLevelModule;
import com.seibel.distanthorizons.core.level.DhClientLevel;
import com.seibel.distanthorizons.core.level.DhClientServerLevel;
import com.seibel.distanthorizons.core.level.IDhClientLevel;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos;
import com.seibel.distanthorizons.core.util.FullDataPointUtil;
import com.seibel.distanthorizons.core.world.IDhClientWorld;
import com.seibel.distanthorizons.core.wrapperInterfaces.IWrapperFactory;
import com.seibel.distanthorizons.core.wrapperInterfaces.block.IBlockStateWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IBiomeWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.config.CommonConfig;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import loaderCommon.neoforge.com.seibel.distanthorizons.common.wrappers.McObjectConverter;
import loaderCommon.neoforge.com.seibel.distanthorizons.common.wrappers.block.BiomeWrapper;
import loaderCommon.neoforge.com.seibel.distanthorizons.common.wrappers.block.BlockStateWrapper;
import loaderCommon.neoforge.com.seibel.distanthorizons.common.wrappers.world.ClientLevelWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.material.MapColor;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;

public class DHTool {

    public static void forceReloadAll() {
        IDhClientWorld clientWorld = SharedApi.getIDhClientWorld();
        if (Minecraft.getInstance().level != null
                && ClientLevelWrapper.getWrapper(Minecraft.getInstance().level) instanceof ClientLevelWrapper clientLevelWrapper
                && clientWorld.getLevel(clientLevelWrapper) instanceof IDhClientLevel clientLevel) {

            AtomicReference<ClientLevelModule.ClientRenderState> clientRenderStateAtomicReference = null;
            if (clientLevel instanceof DhClientServerLevel dhClientServerLevel) {
                clientRenderStateAtomicReference = dhClientServerLevel.clientside.ClientRenderStateRef;
            } else if (clientLevel instanceof DhClientLevel dhClientLevel) {
                clientRenderStateAtomicReference = dhClientLevel.clientside.ClientRenderStateRef;
            }
            if (clientRenderStateAtomicReference != null) {
                //     // 也许未来需要定向刷新，但是目前来看只需要全部刷新即可
                //     // DhSectionPos.encode((byte) 0,100,100);
                int d = (int) Config.Client.quickLodChunkRenderDistance.get().get() / 2;
                BlockPos pos = Minecraft.getInstance().player.getOnPos();
                SectionPos sectionPos = SectionPos.of(pos);
                int pSectionX = sectionPos.z();
                int pSectionZ = sectionPos.x();
                byte treeMinDetailLevel = clientRenderStateAtomicReference.get().quadtree.treeMinDetailLevel;
                byte treeMaxDetailLevel = clientRenderStateAtomicReference.get().quadtree.treeMaxDetailLevel;

                for (int i = pSectionX - d; i <= pSectionX + d; i++) {
                    for (int j = pSectionZ - d; j <= pSectionZ + d; j++) {
                        for (byte k = treeMaxDetailLevel; k <= treeMinDetailLevel; k++) {
                            // 注意这里是dh的sectionpos，其实与mc中类似
                            long rootPos = DhSectionPos.encode(k, i, j);
                            clientRenderStateAtomicReference.get().quadtree.reloadPos(rootPos);
                        }
                    }
                }

            }
        }
    }

    public static MapColor computeBaseColor(IDhClientLevel instance, DhBlockPos dhBlockPos, IBiomeWrapper iBiomeWrapper, IBlockStateWrapper iBlockStateWrapper, FullDataPointIdMap fullDataMapping, LongArrayList fullColumnData, IWrapperFactory WRAPPER_FACTORY) {
        if (ClientConfig.Renderer.snowyWinter.get()) {
            if (!dhBlockPos.equals(DhBlockPos.ZERO) && iBlockStateWrapper instanceof BlockStateWrapper blockStateWrapper
                    && !blockStateWrapper.isAir()) {
                var mcPos = McObjectConverter.Convert(dhBlockPos);
                var level = Minecraft.getInstance().level;
                var blockState = blockStateWrapper.blockState;
                // 当给的pos未加载时，读取的是虚空，这并不好。
                if (instance.getClientLevelWrapper() instanceof ClientLevelWrapper clientLevelWrapper) {
                    var holderKey = ResourceKey.create(Registries.BIOME, ResourceLocation.parse(iBiomeWrapper.getSerialString()));
                    if ((clientLevelWrapper.getLevel().registryAccess().holder(holderKey).orElse(null)
                            instanceof Holder.Reference<Biome> holder)) {
                        if (MapChecker.shouldSnowAtBiome(level, holder.value(), blockState, level.getRandom(), blockState.getSeed(mcPos)))
                        //     return mapColor.col;
                        {
                            HashSet<IBlockStateWrapper> blockStatesToIgnore = WRAPPER_FACTORY.getRendererIgnoredBlocks(instance.getLevelWrapper());
                            for (int i = 0; i < fullColumnData.size(); i++) {
                                long fullData = fullColumnData.getLong(i);
                                int id = FullDataPointUtil.getId(fullData);
                                IBlockStateWrapper iBlockStateWrapper_NowQuery;
                                try {
                                    iBlockStateWrapper_NowQuery = fullDataMapping.getBlockStateWrapper(id);
                                } catch (IndexOutOfBoundsException e) {
                                    continue;
                                }
                                int bottomY = FullDataPointUtil.getBottomY(fullData);
                                int blockHeight = FullDataPointUtil.getHeight(fullData);
                                int topY = bottomY + blockHeight;
                                if (CommonConfig.Debug.notLightAbove.get()
                                        && iBlockStateWrapper_NowQuery instanceof BlockStateWrapper blockStateWrapper_NowQuery) {
                                    if (blockStateWrapper_NowQuery.blockState != null &&
                                            blockStateWrapper_NowQuery.blockState.getBlock() instanceof LightBlock) {
                                        if (blockStateWrapper_NowQuery.blockState.hasProperty(LightBlock.LEVEL)
                                                && blockStateWrapper_NowQuery.blockState.getValue(LightBlock.LEVEL) == 0)
                                            break;
                                    }
                                }
                                if (iBlockStateWrapper_NowQuery instanceof BlockStateWrapper blockStateWrapper_NowQuery
                                        && !iBlockStateWrapper_NowQuery.isAir()
                                        && !blockStatesToIgnore.contains(iBlockStateWrapper_NowQuery)
                                ) {
                                    // TODO:后续需要研究哪些方块是被DH跳过渲染的，实际上草是不渲染的，但是樱花树林的粉色小花却渲染
                                    // 此为错误结论，原因是上面被跳过的方块会给下面的染色，比如上面是花，那么下面就可以被染色粉色
                                    // 以及直接跳过流体下方的渲染
                                    // 那么还是要研究谁会被跳过渲染
                                    // 目前猜测是无生物碰撞的
                                    // 以及双层的植物需要继续向下，看是否需要处理
                                    // boolean colorBelowWithAvoidedBlocks = Config.Client.Advanced.Graphics.Quality.tintWithAvoidedBlocks.get();

                                    if (bottomY + instance.getMinY() == dhBlockPos.getY() &&
                                            (MapChecker.getBlockType(blockStateWrapper_NowQuery.blockState, level, mcPos) != 0
                                                    // || (blockStateWrapper1.blockState.is(BlockTags.FLOWERS))
                                                    || (!blockStateWrapper_NowQuery.isSolid() && !blockStateWrapper_NowQuery.isLiquid())
                                            )) {
                                        // return Color.WHITE.getRGB();
                                        return MapColor.SNOW;
                                    } else {
                                        if (!blockStateWrapper_NowQuery.isLiquid()
                                                && !blockStateWrapper_NowQuery.blockState.blocksMotion()) {
                                            // 如果colorBelowWithAvoidedBlocks时，这时会查看下面的方块，我们也进行一个染色
                                            // 暂时不处理多层需要跳过的方块，实际上也许保留一点颜色会更好看
                                            if (i + 1 < fullColumnData.size()) {
                                                int belowBottomY = FullDataPointUtil.getBottomY(fullColumnData.getLong(i + 1));
                                                if (belowBottomY + instance.getMinY() == dhBlockPos.getY())
                                                    return MapColor.SNOW;
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }


                }
            }
        }
        return null;
    }

    public static Biome recoverBiomeObject(BiomeWrapper biomeWrapper, IClientLevelWrapper iClientLevelWrapper) {
        if (iClientLevelWrapper instanceof ClientLevelWrapper clientLevelWrapper) {
            var holderKey = ResourceKey.create(Registries.BIOME, ResourceLocation.parse(biomeWrapper.getSerialString()));
            if ((clientLevelWrapper.getLevel().registryAccess().holder(holderKey).orElse(null)
                    instanceof Holder.Reference<Biome> holder)) {
                // if (BiomeWrapper.getBiomeWrapper(holder, clientLevelWrapper) instanceof BiomeWrapper biomeWrapper1)
                return holder.value();
            }
        }
        return null;
    }

    public static void clearRenderCache() {
        IDhClientWorld clientWorld = SharedApi.getIDhClientWorld();
        if (Minecraft.getInstance().level != null
                && ClientLevelWrapper.getWrapper(Minecraft.getInstance().level) instanceof ClientLevelWrapper clientLevelWrapper
                && clientWorld.getLevel(clientLevelWrapper) instanceof IDhClientLevel clientLevel) {
            clientLevel.clearRenderCache();
        }
    }
}

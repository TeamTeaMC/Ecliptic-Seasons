package com.teamtea.eclipticseasons.client.core;

import com.teamtea.eclipticseasons.api.data.client.model.ESModelLoadedJson;
import com.teamtea.eclipticseasons.api.data.client.model.ModelResolver;
import com.teamtea.eclipticseasons.api.data.client.model.ModelTester;
import com.teamtea.eclipticseasons.api.data.client.model.seasonal.SeasonBlockDefinition;
import com.teamtea.eclipticseasons.api.data.client.model.seasonal.SeasonalTexture;
import com.teamtea.eclipticseasons.api.data.season.SnowDefinition;
import com.teamtea.eclipticseasons.api.misc.client.IMapSlice;
import com.teamtea.eclipticseasons.api.misc.client.IMapSliceProvider;
import com.teamtea.eclipticseasons.client.model.bakequad.*;
import com.teamtea.eclipticseasons.client.model.unbake.SolarBlockModel;
import com.teamtea.eclipticseasons.client.reload.ClientJsonCacheListener;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.client.util.ClientRef;
import com.teamtea.eclipticseasons.common.core.map.SnowyRemover;
import com.teamtea.eclipticseasons.common.core.snow.SnowChecker;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.client.model.*;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.compat.Platform;
import com.teamtea.eclipticseasons.config.ClientConfig;

import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import com.teamtea.eclipticseasons.EclipticSeasons;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// https://github.com/DoubleNegation/CompactOres/blob/1.18/src/main/java/doublenegation/mods/compactores/CompactOresResourcePack.java#L164
// 未来可以基于RepositorySource实现动态纹理生成（看情况，因为目前不需要，对内存消耗比较大）
public class ExtraModelManager {
    public static final RenderType CUTOUT_MIPPED = null;

    public static Map<ResourceLocation, BakedModel> models;
    public static ModelResourceLocation snowOverlayLeaves = new ModelResourceLocation(BlockRegistry.snowyLeaves.getId(), "");
    public static ModelResourceLocation snowySlabBottom = new ModelResourceLocation(BlockRegistry.snowySlab.getId(), "type=bottom,waterlogged=false");
    public static ModelResourceLocation snowOverlayBlock = new ModelResourceLocation(BlockRegistry.snowyBlock.getId(), "");


    public static ResourceLocation snowy_leaves_attach = EclipticSeasons.rl("block/snowy_leaves_attach");
    public static ResourceLocation snowy_leaves_top = EclipticSeasons.rl("block/snowy_leaves_top");

    public static ResourceLocation snowy_custom = EclipticSeasons.rl("block/snowy_custom");
    public static ResourceLocation stairs_top = EclipticSeasons.rl("block/stairs_top");
    public static ResourceLocation snowy_fern = EclipticSeasons.rl("block/snowy_fern");
    public static ResourceLocation snowy_grass = EclipticSeasons.rl("block/snowy_grass");
    public static ResourceLocation snowy_large_fern_bottom = EclipticSeasons.rl("block/snowy_large_fern_bottom");
    public static ResourceLocation snowy_large_fern_top = EclipticSeasons.rl("block/snowy_large_fern_top");
    public static ResourceLocation snowy_tall_grass_bottom = EclipticSeasons.rl("block/snowy_tall_grass_bottom");
    public static ResourceLocation snowy_tall_grass_top = EclipticSeasons.rl("block/snowy_tall_grass_top");
    public static ResourceLocation overlay_2 = EclipticSeasons.rl("block/overlay_2");
    public static ResourceLocation snow_height2 = EclipticSeasons.rl("block/snow_height2");
    public static ResourceLocation snow_height2_top = EclipticSeasons.rl("block/snow_height2_top");
    public static ResourceLocation grass_flower = EclipticSeasons.rl("block/grass_flower");

    public static ResourceLocation snow = new ResourceLocation("block/snow");
    public static ResourceLocation snow_overlay_half_left = textureRL("snow_overlay_half_left");
    public static ResourceLocation snow_overlay_half_right = textureRL("snow_overlay_half_right");
    public static ResourceLocation snow_overlay = textureRL("snow_overlay");
    public static ResourceLocation snow_overlay_leaves = textureRL("snow_overlay_leaves");
    public static ResourceLocation snow_overlay_tiny = textureRL("snow_overlay_tiny");


    public static ResourceLocation textureRL(String s) {
        return EclipticSeasons.rl("block/" + s);
    }

    public static List<ResourceLocation> flower_on_grass = List.of(1, 2, 3, 4, 5, 6).stream().map(
            i -> EclipticSeasons.rl("block/flower_%s".formatted(i))
    ).toList();
    public static List<ResourceLocation> snow_edge_overlays = IntStream.rangeClosed(0, 18).mapToObj(i -> EclipticSeasons.rl("block/snow_edge/snow_edge_overlay_%s".formatted(i))).collect(Collectors.toCollection(ArrayList::new));

    public static List<ResourceLocation> fourleaf_clovers = IntStream.rangeClosed(0, 6).mapToObj(i -> EclipticSeasons.rl("block/fourleaf_clover/fourleaf_clover_%s".formatted(i))).collect(Collectors.toCollection(ArrayList::new));


    public static ResourceLocation mrl(String s, String s2) {
        return new ModelResourceLocation(EclipticSeasons.rl(s), s2);
    }

    public static ResourceLocation extra_mrl(ResourceLocation resourceLocation, String v) {
        // return new ModelResourceLocation(resourceLocation.withPrefix("extra/"), v);
        return resourceLocation.withPrefix("extra/" + (v.isEmpty() ? "" : v + "/"));
    }

    public static HashMap<ResourceLocation, SpriteContents> blocksCache = new HashMap<>();

    public static boolean shouldCutoutMipped(BlockState state) {
        if (CommonConfig.isSnowyWinter()) {
            if (Minecraft.getInstance().level != null) {
                var onBlock = state.getBlock();
                if (!(onBlock instanceof FenceBlock)) {
                    if (onBlock instanceof SlabBlock ||
                            onBlock instanceof FarmBlock ||
                            onBlock instanceof DirtPathBlock ||
                            onBlock instanceof StairBlock
                            || state.isSolidRender(EmptyBlockGetter.INSTANCE, BlockPos.ZERO)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public static Map<BlockState, BakedModel> snowyModelsCache = new IdentityHashMap<>();
    public static Map<BlockState, BakedModel> snowyModelsCache2 = new IdentityHashMap<>();


    public static BakedModel getSnowyModel(BlockState state, BlockState snowState, int flag, int offset) {

        boolean notSpecialLeaves = !(
                (MapChecker.leaveLike(flag))
                        && snowState == null);
        BakedModel snowModel = notSpecialLeaves ?
                snowyModelsCache.get(state) : snowyModelsCache2.get(state);


        if (snowModel == null) {
            Block onBlock = state.getBlock();
            boolean forceReplace = false;

            // **************************
            // es patch for client override
            List<SnowDefinition> snowDefinitions = ClientRef.snowClientDef.get(onBlock);
            if (snowDefinitions != null && !snowDefinitions.isEmpty()) {
                for (SnowDefinition snowDefinition : snowDefinitions) {
                    ResourceLocation cinfo =
                            notSpecialLeaves ? snowDefinition.getInfo().getMid() :
                                    snowDefinition.getInfo().getMid2();
                    ModelResolver smr = extraSnowModelBuilds.get(cinfo);
                    if (smr != null) {
                        var mmrl = smr.tryFind(state);
                        if (mmrl != null) {
                            snowModel = models.get(mmrl.modelResourceLocation());
                            forceReplace = mmrl.replace();
                            flag = snowDefinition.getInfo().getFlag();
                            break;
                        }
                    }
                }
            }
            // **************************
            if (snowModel == null) {
                if (flag == MapChecker.FLAG_BLOCK) {
                    snowModel = models.get(snowOverlayBlock);
                } else if (flag == MapChecker.FLAG_LEAVES) {
                    snowModel = !CommonConfig.Season.snowyTree.get() ?
                            models.get(snowOverlayLeaves) : notSpecialLeaves ?
                            models.get(snowy_leaves_top) : models.get(snowy_leaves_attach);
                } else if (flag == MapChecker.FLAG_SLAB) {
                    snowModel = models.get(snowySlabBottom);
                } else if (flag == MapChecker.FLAG_STAIRS_TOP) {
                    snowModel = models.get(stairs_top);
                } else if (models != null && flag == MapChecker.FLAG_STAIRS) {
                    if (snowState != null)
                        snowModel = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(snowState);
                } else if (flag == MapChecker.FLAG_GRASS) {
                    if (onBlock == Blocks.GRASS) {
                        snowModel = models.get(snowy_grass);
                    } else if (onBlock == Blocks.FERN) {
                        snowModel = models.get(snowy_fern);
                    } else snowModel = models.get(snowy_grass);
                } else if (flag == MapChecker.FLAG_GRASS_LARGE) {
                    if (onBlock == Blocks.TALL_GRASS) {
                        snowModel = models.get(offset == 1 ? snowy_tall_grass_bottom : snowy_tall_grass_top);
                    } else if (onBlock == Blocks.LARGE_FERN) {
                        snowModel = models.get(offset == 1 ? snowy_large_fern_bottom : snowy_large_fern_top);
                    } else snowModel = models.get(offset == 1 ? snowy_tall_grass_bottom : snowy_tall_grass_top);
                } else if (flag == MapChecker.FLAG_FARMLAND) {
                    snowModel = models.get(snow_height2_top);
                } else if (flag == MapChecker.FLAG_CUSTOM) {
                    snowModel = models.get(snowy_custom);
                } else if (flag == MapChecker.FLAG_CUSTOM_JSON
                        | flag == MapChecker.FLAG_CUSTOM_JSON_PLANTS
                        || flag == MapChecker.FLAG_CUSTOM_JSON_VINE_LIKE
                        || flag == MapChecker.FLAG_CUSTOM_JSON_WITH_TOP
                        || flag == MapChecker.FLAG_CUSTOM_JSON_WITH_TOP_LEAVES) {
                    SnowDefinition.Info uncacheSnow = SnowChecker.getUncacheSnow(state);
                    ResourceLocation cinfo =
                            notSpecialLeaves ? uncacheSnow.getMid() : uncacheSnow.getMid2();
                    ModelResolver smr = extraSnowModelBuilds.get(cinfo);
                    if (smr != null) {
                        var mmrl = smr.tryFind(state);
                        if (mmrl != null) {
                            snowModel = models.get(mmrl.modelResourceLocation());
                            forceReplace = mmrl.replace();
                        }
                    }
                }
            }

            if (snowModel != null) {
                // stateModelsCache.putIfAbsent(snowState, snowModel);
                SnowyBakedModelWrapper<?> bakedModel =
                        snowModel instanceof SnowyBakedModelWrapper<?> ?
                                (SnowyBakedModelWrapper<?>) snowModel :
                                new SnowyBakedModelWrapper<>(snowModel);
                bakedModel.setReplace(forceReplace);
                if (ISnowyReplaceModel.isInvalid(bakedModel)) {
                    bakedModel.updateBlockType(flag);
                    bakedModel.setLowLayer(!notSpecialLeaves);
                }
                if (notSpecialLeaves)
                    snowyModelsCache.put(state, bakedModel);
                else snowyModelsCache2.put(state, bakedModel);
            }
        }
        return snowModel;
    }


    private final static List<BakedQuad> EMPTY = List.of();


    public static TextureAtlasSprite getSprite(ResourceLocation resourceLocation) {
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(resourceLocation);
    }

    public static List<BakedQuad> cancelTop(BakedModel bakedModel, BlockAndTintGetter blockAndTintGetter, BlockState state, BlockPos pos, Direction direction, RandomSource random, long seed, List<BakedQuad> original) {
        // if (state.is(Blocks.GRASS_BLOCK)) {
        //     int c = 0;
        // }
        if (bakedModel != null
                && ClientConfig.isTopFaceCulling()
                && !original.isEmpty()
                && (direction == Direction.UP || direction == null)
                && !(IESReplaceModel.isInvalid(bakedModel))
                && blockAndTintGetter instanceof IMapSlice
        ) {
            random.setSeed(seed);
            var snowModel = ExtraModelManager.findModel(blockAndTintGetter, pos, state, random, seed, posToMutable(pos));

            if (snowModel instanceof SnowyBakedModelWrapper) {
                int blockType = MapChecker.getBlockType(state, blockAndTintGetter, pos);
                if (blockType == MapChecker.FLAG_CUSTOM)
                    return original;
                if (direction == Direction.UP) {
                    if (blockType == MapChecker.FLAG_BLOCK) return EMPTY;
                }


                if (original.size() == 1) {
                    if (original.get(0).getDirection() == Direction.UP) return EMPTY;
                } else {
                    original = new ArrayList<>(original);
                    for (int i = 0; i < original.size(); i++) {
                        BakedQuad bakedQuad = original.get(i);
                        if (bakedQuad.getDirection() == Direction.UP) {
                            original.remove(i);
                            i--;
                        }
                    }
                }

            }
        }


        if (bakedModel instanceof SnowyBakedModelWrapper<?> snowyBakedModelWrapper) {

            int blockType = snowyBakedModelWrapper.getBindBlockType() > MapChecker.FLAG_IGNORE ?
                    snowyBakedModelWrapper.getBindBlockType() :
                    MapChecker.getBlockType(state, blockAndTintGetter, pos);
            if (blockType == MapChecker.FLAG_CUSTOM) {
                original = new ArrayList<>();
            }

            if ((blockType == MapChecker.FLAG_CUSTOM)
                // && state.toString().contains("stairs_a_cherry_blindwall")
                // &&(state.hasProperty(StairBlock.SHAPE)&& state.getValue(StairBlock.SHAPE) == StairsShape.OUTER_RIGHT)
            ) {
                // if(blockType==MapChecker.FLAG_STAIRS)
                {
                    if (shouldMakeSnowyBakedQuads(blockType, direction)) {
                        ArrayList<BakedQuad> quadsCTM = null;

                        BakedModel bakedModelCTM = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state);
                        if (bakedModelCTM != null) {
                            ModelData modelDataCTM = bakedModelCTM.getModelData(blockAndTintGetter, pos, state, ModelData.EMPTY);
                            random.setSeed(seed);
                            ChunkRenderTypeSet renderTypes = bakedModelCTM.getRenderTypes(state, random, modelDataCTM);
                            quadsCTM = new ArrayList<>();
                            for (RenderType renderType : renderTypes.asList()) {
                                random.setSeed(seed);
                                quadsCTM.addAll(bakedModelCTM.getQuads(state, direction, random, modelDataCTM, renderType));
                            }
                        }

                        original = makeSnowyBakedQuads(state, original, quadsCTM);
                    }
                }
            }
        }
        return original;
    }

    public static boolean shouldMakeSnowyBakedQuads(int blockType, Direction direction) {
        return blockType == MapChecker.FLAG_CUSTOM
                || direction != null && direction.ordinal() > 1;
    }

    public static List<BakedQuad> makeSnowyBakedQuads(BlockState state, List<BakedQuad> original, ArrayList<BakedQuad> quadsCTM) {
        if (quadsCTM != null) {

            boolean tooTiny = false;
            tooTiny |= state.getBlock() instanceof FenceBlock;
            tooTiny |= state.getBlock() instanceof FenceGateBlock;
            tooTiny |= state.getBlock() instanceof IronBarsBlock;
            tooTiny |= state.getBlock() instanceof StairBlock;
            if (!tooTiny)
                quadsCTM = QuadFixer.fixQuadCTM(quadsCTM);


            TextureAtlasSprite snow_overlay_sprite = getSprite(snow_overlay);
            TextureAtlasSprite snow_overlay_tiny_sprite = getSprite(snow_overlay_tiny);
            TextureAtlasSprite snow_sprite = getSprite(snow);
            float offset = 0.5f;
            boolean isSlabDown = false;
            original = new ArrayList<>(quadsCTM.size());
            for (BakedQuad bakedQuad : quadsCTM) {
                Direction bakedQuadDirection = bakedQuad.getDirection();
                if (bakedQuadDirection != Direction.DOWN) {
                    TextureAtlasSprite spriteUse = snow_overlay_sprite;
                    // if (blockType == MapChecker.FLAG_CUSTOM)
                    {
                        if (bakedQuadDirection != Direction.UP) {
                            isSlabDown = true;
                            float maxY = QuadFixer.getMaxY(bakedQuad);
                            offset = 1 - maxY;
                            if (offset < 0.00001f) {
                                offset = 0;
                                isSlabDown = false;
                            }
                            // if(maxY<0.75f)continue;
                        }
                    }

                    if (bakedQuadDirection == Direction.UP) spriteUse = snow_sprite;
                    else {
                        if (tooTiny)
                            spriteUse = snow_overlay_tiny_sprite;
                        else
                            spriteUse = QuadFixer.getMaxY(bakedQuad) - QuadFixer.getMinY(bakedQuad) > 0.4002f ? snow_overlay_sprite : snow_overlay_tiny_sprite;

                    }
                    BakedQuad retexturedBakedQuad;
                    if (RectangularPrismChecker.isRectangularPrism(bakedQuad)) {
                        retexturedBakedQuad = new BakedQuadRetexturedAndReUV(bakedQuad, spriteUse, isSlabDown, offset);
                    } else {
                        retexturedBakedQuad = new BakedQuadRetextured(bakedQuad, spriteUse);
                    }

                    original.add(retexturedBakedQuad);
                }
            }

        }
        return original;
    }


    public static BlockPos.MutableBlockPos posToMutable(BlockPos pos) {
        return new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());
    }

    public static boolean canSnowy(BlockAndTintGetter blockAndTintGetter, BlockPos pos, BlockState state, long seed, @Nullable BlockPos.MutableBlockPos checkPos) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return false;

        var onBlock = state.getBlock();
        int flag = MapChecker.getBlockType(state, blockAndTintGetter, pos);
        List<SnowDefinition> snowDefClientOverlay = ClientRef.snowClientDef.get(onBlock);

        if (flag == MapChecker.FLAG_NONE
                && (snowDefClientOverlay == null
                || snowDefClientOverlay.isEmpty()
                || snowDefClientOverlay.get(0).getInfo().getFlag() == MapChecker.FLAG_NONE)) {
            return false;
        }

        boolean leaveLike = MapChecker.leaveLike(flag);
        boolean leavesOrVine = leaveLike || MapChecker.vineLike(flag);

        IMapSlice mapSlice = null;
        if (blockAndTintGetter instanceof IMapSlice cmapSlice) {
            mapSlice = cmapSlice;
            if (!leavesOrVine) {
                int cut = mapSlice.getBlockHeight(pos) - pos.getY();
                if (cut > 1
                    // || cut < -3
                )
                    if (!ClientConfig.Renderer.snowUnderTree.get())
                        return false;
            }
        }

        int offset = snowDefClientOverlay == null ?
                MapChecker.getSnowOffset(state, flag) : snowDefClientOverlay.get(0).getInfo().getOffset();

        boolean isLight = false;
        if (checkPos == null) checkPos = posToMutable(pos);
        else checkPos.set(pos.getX(), pos.getY(), pos.getZ());

        if (ClientConfig.Renderer.useVanillaCheck.get()) {
            isLight = blockAndTintGetter.getBrightness(LightLayer.BLOCK, pos.above()) >= 15;
        } else {
            int cacheHeight = mapSlice != null ? mapSlice.getBlockHeight(pos) :
                    MapChecker.getHeightOrUpdate(level, pos, false);
            isLight = cacheHeight <= pos.getY() - offset;
        }

        boolean specialLeaves = false;

        if (!isLight && ClientConfig.Renderer.snowUnderTree.get()) {
            checkPos.set(pos.getX(), pos.getY() + 1, pos.getZ());
            if (blockAndTintGetter.getBrightness(LightLayer.SKY, checkPos) >= 9) {
                int y_real = blockAndTintGetter instanceof IMapSliceProvider ip ?
                        ip.getSolidBlockHeight(pos) :
                        level.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ()) - 1;
                checkPos.setY(y_real);
                BlockState getterBlockState = blockAndTintGetter.getBlockState(checkPos);
                if (getterBlockState.isAir()) {
                    try {
                        getterBlockState = level.getBlockState(checkPos);
                    } catch (Exception e) {
                        EclipticSeasons.logger(e);
                    }
                }
                if (getterBlockState.getShadeBrightness(blockAndTintGetter, checkPos) < 0.5f) {
                    isLight = true;
                    if (leaveLike) {
                        if (CommonConfig.Season.snowyTree.get())
                            specialLeaves = true;
                        else isLight = false;
                    }
                }
            }
        }

        if (isLight) {
            checkPos.set(pos.getX(), pos.getY() + 1, pos.getZ());
            if (leaveLike) {
                {
                    if (!specialLeaves) {
                        BlockState aboveState = blockAndTintGetter.getBlockState(checkPos);
                        if (isLight) {
                            // specialLeaves = true;
                            specialLeaves = blockAndTintGetter instanceof IMapSliceProvider ip ?
                                    ip.getSolidBlockHeight(checkPos) > pos.getY() :
                                    aboveState.is(state.getBlock());
                        }
                    }
                }
            } else {
                if (MapChecker.extraSnowPassable(state)) {
                    isLight = !MapChecker.extraSnowPassable(blockAndTintGetter.getBlockState(checkPos));
                } else if (!ClientConfig.Renderer.snowUnderTree.get()) {
                    isLight = !MapChecker.solidTest(blockAndTintGetter.getBlockState(checkPos));
                }
            }
        }


        if (isLight && specialLeaves) {
            isLight = CommonConfig.Season.snowyTree.get();
            if (!isLight) specialLeaves = false;
        }
        boolean isSnowy = false;
        if (isLight) {
            if (CommonConfig.isSnowyWinter()
                    && onBlock != Blocks.SNOW_BLOCK
                    && ((mapSlice != null && MapChecker.shouldSnowAt(level, pos, mapSlice.getSurfaceFaceBiomeId(pos), state, null, seed))
                    || (mapSlice == null && MapChecker.shouldSnowAt(level, pos, state, null, seed))
            )
            ) {
                isSnowy = true;
                if (CommonConfig.Season.notSnowyNearGlowingBlock.get()) {
                    if (mapSlice != null) {
                        checkPos.set(pos.getX(), pos.getY() + 1 - offset, pos.getZ());
                        if (blockAndTintGetter.getBrightness(LightLayer.BLOCK, checkPos) >=
                                CommonConfig.Season.notSnowyNearGlowingBlockLevel.get()) {
                            isSnowy = false;
                        }
                    }

                    if (mapSlice == null) {
                        checkPos.set(pos.getX(), pos.getY() + 1 - offset, pos.getZ());
                        if (blockAndTintGetter.getBrightness(LightLayer.BLOCK, checkPos) >=
                                CommonConfig.Season.notSnowyNearGlowingBlockLevel.get()) {
                            isSnowy = false;
                        }
                    }
                }
            }
        }
        return isSnowy;
    }

    public static BakedModel findModel(BlockAndTintGetter blockAndTintGetter, BlockPos pos, BlockState state, RandomSource random, long seed, @Nullable BlockPos.MutableBlockPos checkPos) {
        Level level = Minecraft.getInstance().level;
        BakedModel replace = null;
        if (level == null) return replace;

        var onBlock = state.getBlock();
        int flag = MapChecker.getBlockType(state, blockAndTintGetter, pos);
        List<SeasonBlockDefinition> seasonDefCache = null;
        List<SnowDefinition> snowDefClientOverlay = null;
        if (flag == 0) {
            seasonDefCache = ClientRef.seasonDef.get(onBlock);
            snowDefClientOverlay = ClientRef.snowClientDef.get(onBlock);
            if (snowDefClientOverlay != null
                    && snowDefClientOverlay.isEmpty()) snowDefClientOverlay = null;
            if (seasonDefCache == null && snowDefClientOverlay == null)
                return null;
        }
        int offset = snowDefClientOverlay == null ?
                MapChecker.getSnowOffset(state, flag) : snowDefClientOverlay.get(0).getInfo().getOffset();

        boolean isLight = false;

        boolean extendCheck = false;
        if (checkPos == null) checkPos = posToMutable(pos);
        else checkPos.set(pos.getX(), pos.getY(), pos.getZ());
        checkPos.set(pos.getX(), pos.getY() + 1, pos.getZ());
        if (ClientConfig.Debug.debugRender.get() && blockAndTintGetter.getBrightness(LightLayer.SKY, checkPos) > 0) {
            extendCheck = true;
        }

        boolean leaveLike = MapChecker.leaveLike(flag);
        boolean specialLeaves = false;
        boolean leavesOrVine = leaveLike || MapChecker.vineLike(flag);

        IMapSlice mapSlice = null;
        if (blockAndTintGetter instanceof IMapSlice cmapSlice) {
            mapSlice = cmapSlice;
            if (!leavesOrVine) {
                int cut = mapSlice.getBlockHeight(pos) - pos.getY();
                if (cut > 1
                    // || cut < -3
                )
                    if (!ClientConfig.Renderer.snowUnderTree.get()
                            && !extendCheck)
                        return null;
            }
        }

        if (ClientConfig.Renderer.useVanillaCheck.get()) {
            isLight = blockAndTintGetter.getBrightness(LightLayer.BLOCK, pos.above()) >= 15;
        } else {
            int cacheHeight = mapSlice != null ?
                    mapSlice.getBlockHeight(pos)
                    : MapChecker.getHeightOrUpdate(level, pos, false);
            isLight = cacheHeight <= pos.getY() - offset;
        }


        if (!isLight && ClientConfig.Renderer.snowUnderTree.get()) {
            checkPos.set(pos.getX(), pos.getY() + 1, pos.getZ());
            if (blockAndTintGetter.getBrightness(LightLayer.SKY, checkPos) >= 9) {
                int y_real = blockAndTintGetter instanceof IMapSliceProvider ip ?
                        ip.getSolidBlockHeight(pos) :
                        level.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ()) - 1;
                checkPos.setY(y_real);
                BlockState getterBlockState = blockAndTintGetter.getBlockState(checkPos);
                if (getterBlockState.isAir()) {
                    try {
                        getterBlockState = level.getBlockState(checkPos);
                    } catch (Exception e) {
                        EclipticSeasons.logger(e);
                    }
                }
                if (getterBlockState.getShadeBrightness(blockAndTintGetter, checkPos) < 0.5f) {
                    isLight = true;
                    if (leaveLike) {
                        if (CommonConfig.Season.snowyTree.get())
                            specialLeaves = true;
                        else isLight = false;
                    }
                }
            }
        }

        if (isLight) {
            checkPos.set(pos.getX(), pos.getY() + 1, pos.getZ());
            if (leaveLike) {
                {
                    if (!specialLeaves) {
                        BlockState aboveState = blockAndTintGetter.getBlockState(checkPos);
                        if (isLight) {
                            // specialLeaves = true;
                            specialLeaves = blockAndTintGetter instanceof IMapSliceProvider ip ?
                                    ip.getSolidBlockHeight(checkPos) > pos.getY() :
                                    aboveState.is(state.getBlock());
                        }
                    }
                }
            } else {
                if (MapChecker.extraSnowPassable(state)) {
                    isLight = !MapChecker.extraSnowPassable(blockAndTintGetter.getBlockState(checkPos));
                } else if (!ClientConfig.Renderer.snowUnderTree.get()) {
                    isLight = !MapChecker.solidTest(blockAndTintGetter.getBlockState(checkPos));
                }
            }
        }


        if (isLight && specialLeaves) {
            isLight = CommonConfig.Season.snowyTree.get();
            if (!isLight) specialLeaves = false;
        }

        if (extendCheck || isLight) {
            boolean isSnowy = false;
            if (CommonConfig.isSnowyWinter()
                    && isLight && onBlock != Blocks.SNOW_BLOCK
                    && ((mapSlice != null && MapChecker.shouldSnowAt(level, pos, mapSlice.getSurfaceFaceBiomeId(pos), state, random, seed))
                    || (mapSlice == null && MapChecker.shouldSnowAt(level, pos, state, random, seed))
            )) {
                // DynamicLeavesBlock

                isSnowy = true;

                if (CommonConfig.Season.notSnowyNearGlowingBlock.get()) {
                    if (mapSlice != null) {
                        checkPos.set(pos.getX(), pos.getY() + 1 - offset, pos.getZ());
                        if (blockAndTintGetter.getBrightness(LightLayer.BLOCK, checkPos) >=
                                CommonConfig.Season.notSnowyNearGlowingBlockLevel.get()) {
                            isSnowy = false;
                        }
                    }

                    if (mapSlice == null) {
                        checkPos.set(pos.getX(), pos.getY() + 1 - offset, pos.getZ());
                        if (blockAndTintGetter.getBrightness(LightLayer.BLOCK, checkPos) >=
                                CommonConfig.Season.notSnowyNearGlowingBlockLevel.get()) {
                            isSnowy = false;
                        }
                    }
                }

                if (isSnowy) {
                    BlockState snowState = null;
                    if (models != null) {
                        if (flag == MapChecker.FLAG_STAIRS) {
                            snowState = BlockRegistry.snowyStairs.get().defaultBlockState()
                                    .setValue(StairBlock.FACING, state.getValue(StairBlock.FACING))
                                    .setValue(StairBlock.HALF, state.getValue(StairBlock.HALF))
                                    .setValue(StairBlock.SHAPE, state.getValue(StairBlock.SHAPE));
                        } else if (leaveLike && !specialLeaves) {
                            snowState = BlockRegistry.snowyLeaves.get().defaultBlockState();
                        }
                    }
                    BakedModel snowModel = getSnowyModel(state, snowState, flag, offset);

                    if (snowModel != null) {
                        replace = snowModel;
                    }
                }

            }

            if (
                // !FMLEnvironment.production
                    !isSnowy
                            && (flag == MapChecker.FLAG_BLOCK || onBlock == Blocks.GRASS_BLOCK)
                            && ClientConfig.Debug.debugRender.get()) {
                int index = -1;
                int ddLength = 0;
                int[][][] directions = DirectionMask.DIRECTIONS;
                int[] indexs = DirectionMask.INDICES;
                BlockPos.MutableBlockPos originalCache = null;
                directionChecks:
                for (int i = 0, directionsLength = directions.length; i < directionsLength; i++) {
                    int[][] directionRequireGroup = directions[i];
                    for (int[] direction : directionRequireGroup) {
                        checkPos.set(pos.getX() + direction[0], pos.getY() + 1, pos.getZ() + direction[1]);
                        if ((MapChecker.getHeightOrUpdate(level, checkPos, false)) != pos.getY()) {
                            continue directionChecks;
                        }
                        checkPos.set(pos.getX() + direction[0], pos.getY(), pos.getZ() + direction[1]);

                        BlockState neighSate = blockAndTintGetter.getBlockState(checkPos);
                        long neighSateSeed = neighSate.getSeed(checkPos);


                        // if (!((mapSlice != null && MapChecker.shouldSnowAt(level, checkPos, mapSlice.getSurfaceFaceBiomeId(checkPos), neighSate, random, neighSateSeed))
                        //         || (mapSlice == null && MapChecker.shouldSnowAt(level, checkPos, neighSate, random, neighSateSeed))
                        //         || (mapSlice != null && mapSlice.getSnowyStatus(checkPos) == SnowyRemover.SnowyFlag.SNOWY_ALWAYS.ordinal())
                        // ))
                        if (!(neighSate.is(Blocks.GRASS_BLOCK) || MapChecker.getBlockType(neighSate, blockAndTintGetter, checkPos) == MapChecker.FLAG_BLOCK)) {
                            continue directionChecks;
                        }
                        if (originalCache == null)
                            originalCache = new BlockPos.MutableBlockPos(checkPos.getX(), checkPos.getY(), checkPos.getZ());
                        else originalCache.set(checkPos.getX(), checkPos.getY(), checkPos.getZ());
                        if (!canSnowy(level, originalCache, neighSate, neighSateSeed, checkPos)) {
                            continue directionChecks;
                        }
                    }
                    if (directionRequireGroup.length > ddLength) {
                        index = i;
                        ddLength = directionRequireGroup.length;
                    }
                }
                if (index > -1) {
                    index = indexs[index];
                    replace = models.get(snow_edge_overlays.get(index));
                    isSnowy = true;
                }
            }

            if (replace == null || !(replace instanceof IESReplaceModel iesReplaceModel && iesReplaceModel.isReplace())) {
                if (seasonDefCache == null)
                    seasonDefCache = ClientRef.seasonDef.get(onBlock);
                if (seasonDefCache != null)
                    for (SeasonBlockDefinition localSeasonStatus : seasonDefCache) {
                        List<SeasonBlockDefinition.FlatSliceHolder> flatSliceHolders = localSeasonStatus.getFlatSliceEnumMap().get(ClientCon.nowSolarTerm);
                        if (flatSliceHolders != null && !flatSliceHolders.isEmpty()) {
                            checkPos.set(pos.getX(), pos.getY() + 1, pos.getZ());
                            for (SeasonBlockDefinition.FlatSliceHolder flatSliceHolder : flatSliceHolders) {
                                SeasonBlockDefinition.FlatSlice flatSlice = flatSliceHolder.flatSlice();
                                if (!flatSlice.emptyAbove() || blockAndTintGetter.getBlockState(checkPos).isAir()) {
                                    if ((mapSlice == null && localSeasonStatus.getBiomes().contains(MapChecker.getSurfaceBiome(level, pos))
                                            || (mapSlice != null && localSeasonStatus.getBiomes().contains(MapChecker.idToBiome(level, mapSlice.getSurfaceFaceBiomeId(checkPos))))))
                                    {
                                        ResourceLocation cinfo = flatSlice.transitionModels() == null ?
                                                flatSlice.mid() :
                                                Mth.abs(((int) (seed + pos.getX()))) % 100 > ClientCon.progress ?
                                                        flatSlice.transitionModels().getFirst() : flatSlice.transitionModels().getSecond();
                                        ModelResolver smr = extraSnowModelBuilds.get(cinfo);
                                        if (smr != null) {
                                            var mmrl = smr.tryFind(state);
                                            if (mmrl != null) {
                                                var to_replace = models.get(mmrl.modelResourceLocation());
                                                if (to_replace != null) {
                                                    if (replace != null) {
                                                        replace = new SnowySeasonBakeModel<>(to_replace, replace, getRenderType(state));
                                                        if (mmrl.replace() && replace instanceof SnowySeasonBakeModel<?> snowyBakedModelWrapper) {
                                                            snowyBakedModelWrapper.setReplace(true);
                                                        }
                                                    } else {
                                                        replace = mmrl.replace() ?
                                                                new TempReplaceModelWrapper<>(to_replace) :
                                                                to_replace;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
            }
        }
        return replace;
    }

    public static RenderType getRenderType(BlockState state) {
        // if (!Minecraft.useFancyGraphics()) return RenderType.solid();
        // RenderType chunkRenderType = ItemBlockRenderTypes.getChunkRenderType(state);
        ChunkRenderTypeSet chunkRenderTypeSet = ItemBlockRenderTypes.getRenderLayers(state);
        if (chunkRenderTypeSet.contains(RenderType.translucent())) return RenderType.translucent();
        else if (chunkRenderTypeSet.contains(RenderType.cutout())) return RenderType.cutout();
        return
                // ( CompatModule.isContinuityLoad()||CompatModule.isCTMLoad())
                //         && !CompatModule.isSodiumLoad() ?
                //          RenderType.cutout() :
                RenderType.cutoutMipped();

        // return Minecraft.useFancyGraphics() ?
        //         RenderType.cutoutMipped() : RenderType.solid();
    }

    @Deprecated(forRemoval = true)
    public static boolean isModelReplaced(BlockState state) {
        // return !state.blocksMotion()
        //         && MapChecker.getBlockType(state, EmptyBlockGetter.INSTANCE, BlockPos.ZERO) != MapChecker.FLAG_NONE;
        return isModelReplaceable(state, EmptyBlockGetter.INSTANCE, BlockPos.ZERO, null);
    }

    public static boolean isModelReplaceable(BlockState state, BlockGetter blockAndTintGetter, BlockPos pos, BakedModel bakedModel) {
        return (bakedModel instanceof IESReplaceModel model
                && model.isReplace())
                || isModelReplaceable(MapChecker.getBlockType(state, blockAndTintGetter, pos));
    }

    public static boolean isModelReplaceable(BakedModel bakedModel, int flag) {
        return (bakedModel instanceof IESReplaceModel model
                && model.isReplace())
                || isModelReplaceable(flag);
    }

    private static boolean isModelReplaceable(int flag) {
        return flag == MapChecker.FLAG_GRASS
                || flag == MapChecker.FLAG_GRASS_LARGE;
    }

    public static void clearForRebaked(Map<ResourceLocation, BakedModel> modelRegistry) {
        ExtraModelManager.models = modelRegistry;
        snowyModelsCache.clear();
        snowyModelsCache2.clear();
        if (ClientCon.getUseLevel() != null) {
            ClientRef.updateClientSide(ClientCon.getUseLevel().registryAccess());
        }
    }

    public static final Map<ResourceLocation, ESModelLoadedJson> extraSnowModels = new HashMap<>(1024);

    public static final Map<ResourceLocation, ModelResolver> extraSnowModelBuilds = new HashMap<>(1024);

    public static void registerExtraSnowyModels(BiConsumer<ResourceLocation, UnbakedModel> registerModelAndDependenceMethod) {
        extraSnowModelBuilds.clear();
        // extraSnowModels.clear();
        Map<ResourceLocation, ESModelLoadedJson> snowModelLoadedJsonMap = ClientJsonCacheListener.modelDefCache.build(ESModelLoadedJson.CODEC);
        // extraSnowModels.putAll(snowModelLoadedJsonMap);
        EclipticSeasons.logger("Try to register extra model definitions with size %s.".formatted(snowModelLoadedJsonMap.size()));
        snowModelLoadedJsonMap.forEach(
                (resourceLocation, loadedJson) -> {
                    if (!loadedJson.getRequire().isEmpty()) {
                        for (String modid : loadedJson.getRequire()) {
                            if (!Platform.isModLoaded(modid)) {
                                return;
                            }
                        }
                    }
                    if (loadedJson.getMultiPartLike().isValid()) {
                        ResourceLocation mrl = ExtraModelManager.extra_mrl(resourceLocation, "0");
                        registerModelAndDependenceMethod.accept(mrl, loadedJson.getMultiPartLike());
                        extraSnowModelBuilds.put(
                                resourceLocation, new ModelResolver(List.of(new ModelTester(
                                        mrl, loadedJson.isReplace(), List.of()
                                )))
                        );
                    } else {
                        loadedJson.getVariants().forEach(
                                (va, multiVariant) -> {
                                    ResourceLocation mrl = ExtraModelManager.extra_mrl(resourceLocation, va.replaceAll("=", "_").replace(",", "_"));
                                    registerModelAndDependenceMethod.accept(
                                            mrl, multiVariant
                                    );
                                    {
                                        extraSnowModelBuilds.compute(
                                                resourceLocation,
                                                (sss, solver) -> {
                                                    if (solver == null) {
                                                        solver = new ModelResolver(new ArrayList<>());
                                                    }
                                                    List<SnowDefinition.PropertyTester> test = new ArrayList<>();
                                                    for (String s : va.split(",")) {
                                                        String[] split = s.split("=");
                                                        if (split.length == 2) {
                                                            test.add(
                                                                    SnowDefinition.PropertyTester.builder().name(split[0])
                                                                            .matcher(SnowDefinition.ExactMatcher.builder().value(split[1]).build()).build()
                                                            );
                                                        }
                                                    }
                                                    solver.modelTesters().add(
                                                            new ModelTester(mrl, loadedJson.isReplace(), test)
                                                    );
                                                    return solver;

                                                }
                                        );
                                    }
                                }
                        );
                    }
                }
        );
    }

    public static final Map<ResourceLocation, List<SeasonalTexture>> SEASONAL_TEXTURE_HASH_MAP = new HashMap<>();

    public static BlockModel remappingSeasonTextures(ResourceLocation resourceLocation, BlockModel returnValue) {
        if (SEASONAL_TEXTURE_HASH_MAP.containsKey(resourceLocation)) {
            List<SeasonalTexture> seasonalTexture = SEASONAL_TEXTURE_HASH_MAP.get(resourceLocation);
            return SolarBlockModel.of(returnValue).setSeasonalTexture(seasonalTexture);
        }
        return null;
    }
}

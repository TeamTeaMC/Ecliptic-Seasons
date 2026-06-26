package com.teamtea.eclipticseasons.client.particle;

import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.data.client.ColorMode;
import com.teamtea.eclipticseasons.api.data.client.LeafColor;
import com.teamtea.eclipticseasons.client.util.ClientRef;
import com.teamtea.eclipticseasons.client.util.ColorHelper;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import com.teamtea.eclipticseasons.common.registry.AttachmentRegistry;
import com.teamtea.eclipticseasons.common.registry.ParticleRegistry;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.tag.EclipticBlockTags;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.map.SnowyRemover;
import com.teamtea.eclipticseasons.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.TestOnly;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ParticleUtil {

    private static Holder<Biome> nowBiome = null;

    public static void createParticle(ClientLevel clientLevel, int x, int y, int z) {
        if (!ClientConfig.Particle.seasonParticle.get()) return;

        // BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        // for (int j = 0; j < 667; ++j) {
        //     // if (clientLevel.getRandom().nextInt(chanceW) == 0)
        //     {
        //         doAnimateTick(clientLevel, x, y, z, 16, clientLevel.getRandom(), blockpos$mutableblockpos);
        //         doAnimateTick(clientLevel, x, y, z, 32, clientLevel.getRandom(), blockpos$mutableblockpos);
        //     }
        // }
    }

    public static void doAnimateTick(ClientLevel clientLevel, int x, int y, int z, int b, RandomSource random, BlockPos.MutableBlockPos blockpos$mutableblockpos) {
        int i = x + random.nextInt(b) - random.nextInt(b);
        int j = y + random.nextInt(b) - random.nextInt(b);
        int k = z + random.nextInt(b) - random.nextInt(b);

        blockpos$mutableblockpos.set(i, j, k);
        BlockState blockstate = clientLevel.getBlockState(blockpos$mutableblockpos);
        doAnimateTick(clientLevel, x, y, z, b, random, blockpos$mutableblockpos, blockstate);
    }

    // can refer TerrainParticle
    public static boolean doAnimateTick(ClientLevel clientLevel, int x, int y, int z, int b, RandomSource random, BlockPos.MutableBlockPos blockpos$mutableblockpos, BlockState blockstate) {
        if (!ClientConfig.Particle.seasonParticle.get()) return false;
        int i = blockpos$mutableblockpos.getX();
        int j = blockpos$mutableblockpos.getY();
        int k = blockpos$mutableblockpos.getZ();

        Block block = blockstate.getBlock();
        boolean replace = false;
        boolean isLeaf = false;

        List<Pair<LeafColor.InstanceHolder, LeafColor.Instance>> pairs =
                !blockstate.isAir() ? null : ClientRef.leaveColors.get(block);
        if (pairs != null) {
            for (Pair<LeafColor.InstanceHolder, LeafColor.Instance> pair : pairs) {
                if (pair.getFirst().matches(clientLevel, i, j, k, random, blockstate)) {
                    isLeaf = true;
                    LeafColor.Instance second = pair.getSecond();
                    Integer chanceW = null;
                    if (second.weights().contains(ClientCon.nowSolarTerm)) {
                        chanceW = second.weights().get(ClientCon.nowSolarTerm);
                    } else switch (ClientCon.nowSeason) {
                        case SPRING -> chanceW = 17;
                        case SUMMER -> chanceW = 27;
                        case AUTUMN -> chanceW = 9;
                        case WINTER -> chanceW = 15;
                    }

                    if (second.sprites().get(ClientCon.nowSolarTerm) != null) {
                        if (chanceW != null) {
                            chanceW *= (int) (ClientConfig.Particle.fallenLeavesDropWeight.get() * 0.4f);
                            if (random.nextInt(chanceW) == 0) {
                                fallenLeaves(clientLevel, blockpos$mutableblockpos, random, blockstate, second);
                            }
                        }
                        replace = second.replace();
                    }
                    break;
                }
            }
        }

        if (!isLeaf && ClientConfig.Particle.fallenLeaves.get()
                && block instanceof LeavesBlock) {
            if (!blockstate.is(EclipticBlockTags.NONE_FALLEN_LEAVES)) {
                var sd = ClientCon.nowSeason;
                if (sd != Season.NONE) {
                    int chanceW = 19;
                    switch (sd) {
                        case SPRING -> chanceW = 17;
                        case SUMMER -> chanceW = 27;
                        case AUTUMN -> chanceW = 9;
                        case WINTER -> chanceW = 15;
                    }
                    chanceW *= (int) (ClientConfig.Particle.fallenLeavesDropWeight.get() * 0.4f);
                    // chanceW*=4;
                    if (random.nextInt(chanceW) == 0) {
                        fallenLeaves(clientLevel, blockpos$mutableblockpos, random, blockstate, null);
                    }
                }
            }
        }

        if (ClientConfig.Particle.butterfly.get()
                && ClientCon.nowSeason == Season.SPRING
                && ClientCon.isDay
        ) {
            if (!blockstate.isAir()
                    && blockstate.is(EclipticBlockTags.HABITAT_BUTTERFLY)
                    && !EclipticSeasonsApi.getInstance().isRainOrSnowAt(clientLevel, blockpos$mutableblockpos)
                    && clientLevel.canSeeSky(blockpos$mutableblockpos)
                    && random.nextInt((int) (1024 * (ClientConfig.Particle.butterflySpawnWeight.get() * 0.1f))) == 0
            ) {
                clientLevel.addParticle(ParticleRegistry.BUTTERFLY, false, i + 0.5, j + 0.8, k + 0.5, 0.0D, 5.0E-4D, 0.0D);
            }
        }
        if (ClientConfig.Particle.firefly.get()
                && ClientCon.nowSeason == Season.SUMMER
                && ClientCon.isEvening
        ) {
            if (!blockstate.isAir()
                    && blockstate.is(EclipticBlockTags.HABITAT_FIREFLY)
                    && !EclipticSeasonsApi.getInstance().isRainOrSnowAt(clientLevel, blockpos$mutableblockpos)
                    && clientLevel.canSeeSky(blockpos$mutableblockpos)
                    && random.nextInt((int) (160 * (ClientConfig.Particle.fireflySpawnWeight.get() * 0.1f))) == 0
            ) {
                clientLevel.addParticle(ParticleRegistry.FIREFLY, false, i + 0.5, j + 0.8, k + 0.5, 0.0D, 5.0E-4D, 0.0D);
            }
        }

        if (ClientConfig.Particle.wildGoose.get()
                && ClientCon.nowSeason == Season.AUTUMN
                && ClientCon.isNoon
                && clientLevel.canSeeSky(blockpos$mutableblockpos)
                && clientLevel.isEmptyBlock(blockpos$mutableblockpos)
                && !EclipticSeasonsApi.getInstance().isRainAt(clientLevel, blockpos$mutableblockpos)
                && clientLevel.getBiome(blockpos$mutableblockpos).value().getBaseTemperature() < 0.95f
                && random.nextInt((int) (2295 * (ClientConfig.Particle.wildGooseSpawnWeight.get() * 0.1f))) == 0) {
            clientLevel.addParticle(ParticleRegistry.WILD_GOOSE, false, x + random.nextInt(16, 16 * 2) * (random.nextBoolean() ? -1 : 1), y + random.nextInt(15, 16 * 2), z + random.nextInt(16, 16 * 2) * (random.nextBoolean() ? -1 : 1), 0.0D, 5.0E-4D, 0.0D);
        }

        if (random.nextInt(b) == 0 &&
                Minecraft.getInstance().player != null
                && Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == ItemRegistry.ice_wand.get()) {
            var data = clientLevel.getChunkAt(blockpos$mutableblockpos).getData(AttachmentRegistry.SNOWY_REMOVER);
            SnowyRemover.SnowyFlag snowyFlag = data.getSnowyFlag(blockpos$mutableblockpos);
            ParticleOptions indicatorParticleOptions = snowyFlag.getIndicatorParticleOptions(random);
            if (indicatorParticleOptions != null) {
                j = clientLevel.getHeight(Heightmap.Types.MOTION_BLOCKING, i, k);
                clientLevel.addParticle(indicatorParticleOptions,
                        false, i + 0.5, j + 0.3, k + 0.5, 0.0D, -0.0001, 0.0D);

                // clientLevel.addParticle(ParticleTypes.SMOKE, false, i + 0.5, j + 0.1, k + 0.5, 0.0D, -0.0001, 0.0D);
                // clientLevel.addParticle(ParticleTypes.CLOUD, false, i + 0.5, j + 0.5, k + 0.5, 0.0D, 5.0E-4D, 0.0D);
                // clientLevel.addParticle(new BlockParticleOption(ParticleTypes.BLOCK_MARKER, Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL,0)), (double) i + 0.5, (double) j + 0.5, (double) k + 0.5, 0.0, 0.0, 0.0);
                // Minecraft.getInstance().particleEngine.add(new SnowCleaner(clientLevel,(double) i + 0.5, (double) j + 0.5, (double) k + 0.5, Items.SNOWBALL.getDefaultInstance()));

            }
        }

        if (random.nextInt(b) == 0 &&
                Minecraft.getInstance().player != null
                && Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == ItemRegistry.salt_wand.get()) {
            var data = clientLevel.getChunkAt(blockpos$mutableblockpos).getData(AttachmentRegistry.NONE_SNOW_AREA);
            ParticleOptions indicatorParticleOptions = data.neverSnowyAt(blockpos$mutableblockpos) ? ParticleTypes.END_ROD : null;
            if (indicatorParticleOptions != null) {
                clientLevel.addParticle(indicatorParticleOptions,
                        false, i + 0.5, j + 1 + 0.3, k + 0.5, 0.0D, -0.0001, 0.0D);
            }
        }
        return replace;
    }

    public static void fallenLeaves(ClientLevel level, BlockPos pos, RandomSource random, BlockState state, LeafColor.Instance leafInfo) {
        if (!state.isAir()) {
            int color = -1;

            if (leafInfo == null) {
                Pair<Color, Integer> pair = getOrCreateColor(state);
                color = pair.getFirst().getRGB();
                if (pair.getSecond() != -1) {
                    int blockColor = Minecraft.getInstance().getBlockColors().getColor(state, level, pos, pair.getSecond());
                    if (blockColor != -1) {
                        color = color == -1 ? blockColor :
                                ColorHelper.simplyMixColor(color, 0.25f, blockColor, 0.75f);
                    }
                }
                if (color == -1) {
                    color = state.getMapColor(level, pos).col;
                }
            } else {
                color = switch (leafInfo.colorSource()) {
                    case MAP -> state.getMapColor(level, pos).col;
                    case BLOCK -> Minecraft.getInstance().getBlockColors().getColor(state, level, pos, 0);
                    case TEXTURE -> {
                        Pair<Color, Integer> pair = getOrCreateColor(state);
                        int textureColor = pair.getFirst().getRGB();
                        if (pair.getSecond() != -1) {
                            int color2 = Minecraft.getInstance().getBlockColors().getColor(state, level, pos, 0);
                            if (color2 != -1) {
                                textureColor = textureColor == -1 ? color2 :
                                        ColorHelper.simplyMixColor(textureColor, 0.25f, color2, 0.75f);
                            }
                        }
                        yield textureColor;
                    }
                    case CUSTOM -> {
                        ColorMode.Instance orDefault1 = leafInfo.colors().getOrDefault(ClientCon.nowSolarTerm, null);
                        yield orDefault1 != null ? orDefault1.value() : -1;
                    }
                };
            }
            // Minecraft.getInstance().particleEngine.textureAtlas.getSprite(ResourceLocation.parse("minecraft:big_smoke_4"))
            VoxelShape voxelshape = state.getShape(level, pos);
            double d0 = 0.25D;
            int finalColor = color;
            int finalColor1 = color;
            voxelshape.forAllBoxes((x0, y0, z0, x1, y1, z1) -> {
                double x = Math.min(1.0D, x1 - x0);
                double y = Math.min(1.0D, y1 - y0);
                double z = Math.min(1.0D, z1 - z0);
                int aX = Math.min(1, Mth.ceil(x / 0.25D));
                int aY = Math.min(1, Mth.ceil(y / 0.25D));
                int aZ = Math.min(1, Mth.ceil(z / 0.25D));

                for (int pX = 0; pX < aX; ++pX) {
                    for (int pY = -aY; pY < 0; ++pY) {
                        for (int pZ = 0; pZ < aZ; ++pZ) {
                            double d4 = ((double) pX + 0.5D) / (double) aX;
                            double d5 = ((double) pY + 0.5D) / (double) aY;
                            double d6 = ((double) pZ + 0.5D) / (double) aZ;
                            double d7 = d4 * x + x0;
                            double d8 = d5 * y + y0;
                            double d9 = d6 * z + z0;

                            if (d5 > 0.49f) {
                                d5 = 0.42f;
                            }

                            if (leafInfo == null || leafInfo.sprites().getOrDefault(ClientCon.nowSolarTerm, null) == null) {
                                level.addParticle(ColorParticleOption.create(ParticleRegistry.FALLEN_LEAVES, finalColor),
                                        (double) pos.getX() + d7,
                                        (double) pos.getY() + d8,
                                        (double) pos.getZ() + d9,
                                        Mth.clamp(d4 - 0.5D, -0.25f, 0.25f),
                                        (d5 - 0.5D) * 0.75,
                                        Mth.clamp(d6 - 0.5D, -0.25f, 0.25f)
                                );
                            } else {
                                List<ResourceLocation> resourceLocations = leafInfo.sprites().get(ClientCon.nowSolarTerm);
                                if (!resourceLocations.isEmpty()) {
                                    TextureAtlas textureAtlas = (TextureAtlas) Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_PARTICLES);
                                    ResourceLocation resourceLocation = resourceLocations.get(random.nextInt(resourceLocations.size()));
                                    Minecraft.getInstance().particleEngine.add(
                                            new FallenLeavesParticle(level,
                                                    (double) pos.getX() + d7,
                                                    (double) pos.getY() + d8,
                                                    (double) pos.getZ() + d9,
                                                    Mth.clamp(d4 - 0.5D, -0.25f, 0.25f),
                                                    (d5 - 0.5D) * 0.75,
                                                    Mth.clamp(d6 - 0.5D, -0.25f, 0.25f),
                                                    ColorParticleOption.create(ParticleRegistry.FALLEN_LEAVES, finalColor1),
                                                    textureAtlas.getSprite(resourceLocation)));
                                }
                            }


                        }
                    }
                }

            });
        }
    }


    public static void onReloadResource() {
        LEAVES_COLOR_MAP.clear();
    }

    private static final Map<BlockState, Pair<Color, Integer>> LEAVES_COLOR_MAP = new IdentityHashMap<>();

    public static Pair<Color, Integer> getOrCreateColor(BlockState state) {
        Minecraft mc = Minecraft.getInstance();
        Color c = Color.WHITE;
        try {
            Pair<Color, Integer> orDefault = LEAVES_COLOR_MAP.getOrDefault(state, null);
            if (orDefault != null) {
                // return orDefault;
            }
            BakedModel blockModel = mc.getBlockRenderer().getBlockModel(state);
            int tintIndex = -1;
            for (Direction direction : new Direction[]{Direction.UP, Direction.DOWN, Direction.SOUTH, Direction.EAST, Direction.NORTH, Direction.WEST, null}) {
                List<BakedQuad> quads = blockModel.getQuads(state, direction, mc.level.getRandom(), ModelData.EMPTY, ItemBlockRenderTypes.getChunkRenderType(state));
                for (BakedQuad quad : quads) {
                    if (quad.getTintIndex() != -1) {
                        tintIndex = quad.getTintIndex();
                        break;
                    }
                }
            }
            {
                TextureAtlasSprite texture = blockModel.getParticleIcon(ModelData.EMPTY);
                ArrayList<Integer> rlist = new ArrayList<>();
                ArrayList<Integer> glist = new ArrayList<>();
                ArrayList<Integer> blist = new ArrayList<>();
                for (int i = 0; i < texture.contents().width(); i++) {
                    for (int j = 0; j < texture.contents().height(); j++) {
                        int color = 0xff000000 | texture.getPixelRGBA(0, i, j);
                        int r = color & 0xff;
                        int g = (color >> 8) & 0xff;
                        int b = (color >> 16) & 0xff;
//                                int a = color >>> 24;
                        if (r * g * b > 0) {
                            rlist.add(r);
                            glist.add(g);
                            blist.add(b);
                        }
                    }
                }
                c = new Color((int) ColorHelper.getAvg(rlist), (int) ColorHelper.getAvg(glist), (int) ColorHelper.getAvg(blist));
                // c = new Color((int) rlist.stream()
                //         .mapToInt(Integer::intValue)
                //         .average()
                //         .orElse(0.0),
                //         (int) glist.stream()
                //                 .mapToInt(Integer::intValue)
                //                 .average()
                //                 .orElse(0.0),
                //         (int) blist.stream()
                //                 .mapToInt(Integer::intValue)
                //                 .average()
                //                 .orElse(0.0));
                // if (c.getRed() == c.getBlue() && c.getBlue() == c.getGreen()) {
                //     c = Color.WHITE;
                // }
            }
            Pair<Color, Integer> colorIntegerPair = Pair.of(c, tintIndex);
            LEAVES_COLOR_MAP.put(state, colorIntegerPair);
            return colorIntegerPair;
        } catch (Exception exception) {
            EclipticSeasons.logger(exception);
        }
        return Pair.of(c, -1);
    }


    @TestOnly
    private static Color getColorFromSpriteResource(TextureAtlasSprite texture) throws IOException {
        Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(texture.contents().name().withPrefix("textures/").withSuffix(".png"));
        var c = Color.WHITE;
        if (resource.isPresent()) {
            BufferedImage image = ImageIO.read(resource.get().open());
            ArrayList<Integer> rlist = new ArrayList<>();
            ArrayList<Integer> glist = new ArrayList<>();
            ArrayList<Integer> blist = new ArrayList<>();
            for (int i = 0; i < image.getWidth(); i++) {
                for (int j = 0; j < image.getHeight(); j++) {
                    int color = 0xff000000 | image.getRGB(i, j);
                    // note it was ARGB
                    int b = color & 0xff;
                    int g = (color >> 8) & 0xff;
                    int r = (color >> 16) & 0xff;
//                                int a = color >>> 24;
                    if (r * g * b > 0) {
                        rlist.add(r);
                        glist.add(g);
                        blist.add(b);
                    }
                }
            }
            c = new Color((int) ColorHelper.getAvg(rlist), (int) ColorHelper.getAvg(glist), (int) ColorHelper.getAvg(blist));
        }
        return c;
    }

    public static void attachSnowyParticle(ClientLevel clientLevel, BlockPos pos, BlockState state) {
        if (ClientConfig.Particle.snowLeafParticles.get()
                && MapChecker.leaveLike(MapChecker.getDefaultBlockTypeFlag(state))
                && EclipticSeasonsApi.getInstance().isSnowyBlock(clientLevel, state, pos)) {
            Minecraft.getInstance().particleEngine.destroy(pos, Blocks.SNOW.defaultBlockState());
        }
    }
}

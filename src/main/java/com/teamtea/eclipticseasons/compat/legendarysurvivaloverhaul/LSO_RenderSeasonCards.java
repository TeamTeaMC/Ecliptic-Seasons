package com.teamtea.eclipticseasons.compat.legendarysurvivaloverhaul;


import com.mojang.blaze3d.systems.RenderSystem;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import sfiomn.legendarysurvivaloverhaul.config.Config;
import sfiomn.legendarysurvivaloverhaul.util.RenderUtil;


public abstract class LSO_RenderSeasonCards {

    private static final ResourceLocation SPRING_CARD = new ResourceLocation("legendarysurvivaloverhaul", "textures/cards/spring.png");
    private static final ResourceLocation AUTUMN_CARD = new ResourceLocation("legendarysurvivaloverhaul", "textures/cards/autumn.png");
    private static final ResourceLocation SUMMER_CARD = new ResourceLocation("legendarysurvivaloverhaul", "textures/cards/summer.png");
    private static final ResourceLocation WINTER_CARD = new ResourceLocation("legendarysurvivaloverhaul", "textures/cards/winter.png");
    private static final ResourceLocation DRY_CARD = new ResourceLocation("legendarysurvivaloverhaul", "textures/cards/dry.png");
    private static final ResourceLocation WET_CARD = new ResourceLocation("legendarysurvivaloverhaul", "textures/cards/wet.png");
    private static final int CARD_WIDTH = 128;
    private static final int CARD_HEIGHT = 128;
    private static ResourceLocation seasonCard = null;
    private static Season ecliptic$lastSeason = null;

    private static ResourceKey<Level> lastDimension = null;
    private static boolean isDimensionSeasonal;
    private static float fadeLevel = 0.0F;
    private static boolean fadeIn;
    private static int delayTimer = 0;
    private static int cardTimer = 0;


    private static boolean ecliptic$lastTropicalSeasonDry = false;

    public static IGuiOverlay SEASON_CARD_GUI = (forgeGui, guiGraphics, partialTicks, width, height) -> {
        if (Config.Baked.seasonCardsEnabled && seasonCard != null) {
            int x = Mth.floor((float) width / 2.0F - 64.0F);
            int y = Mth.floor((float) height / 4.0F - 64.0F);
            forgeGui.setupOverlayRenderState(true, false);
            Minecraft.getInstance().getProfiler().push("season_card");
            RenderSystem.setShaderTexture(0, seasonCard);
            RenderUtil.drawTexturedModelRectWithAlpha(guiGraphics.pose().last().pose(), (float) (x + Config.Baked.seasonCardsDisplayOffsetX), (float) (y + Config.Baked.seasonCardsDisplayOffsetY), CARD_WIDTH, CARD_HEIGHT, 0, 0, 256, 256, fadeLevel);
            Minecraft.getInstance().getProfiler().pop();
        }
    };


    public static void updateSeasonCardFading(Player player) {
        if (player != null && player.isAlive()) {
            Level level = player.level();
            if (lastDimension == null || lastDimension != level.dimension()) {
                delayTimer = Config.Baked.seasonCardsSpawnDimensionDelayInTicks;
                lastDimension = level.dimension();
                isDimensionSeasonal = LSO_ESUtil.hasSeasons(level);
            }

            if (isDimensionSeasonal) {
                if (delayTimer > 0) {
                    --delayTimer;
                } else {
                    if (seasonCard == null) {
                        boolean isTropical = level.getBiome(player.blockPosition()).is(ClimateTypeBiomeTags.MONSOONAL);
                        Season currentSeason = EclipticUtil.getNowSolarTerm(level).getSeason();
                        if (!isTropical) {
                            if (currentSeason != ecliptic$lastSeason) {
                                ecliptic$lastSeason = currentSeason;
                                fadeIn = true;
                                cardTimer = 0;
                                if (currentSeason == Season.AUTUMN) {
                                    seasonCard = AUTUMN_CARD;
                                } else if (currentSeason == Season.SPRING) {
                                    seasonCard = SPRING_CARD;
                                } else if (currentSeason == Season.SUMMER) {
                                    seasonCard = SUMMER_CARD;
                                } else if (currentSeason == Season.WINTER) {
                                    seasonCard = WINTER_CARD;
                                }
                            }
                        } else {
                            if (currentSeason != ecliptic$lastSeason) {
                                ecliptic$lastSeason = currentSeason;
                                boolean currentTropicalSeasonDry = currentSeason == Season.SPRING || currentSeason == Season.WINTER;
                                if (currentTropicalSeasonDry != ecliptic$lastTropicalSeasonDry) {
                                    ecliptic$lastTropicalSeasonDry = currentTropicalSeasonDry;
                                    fadeIn = true;
                                    cardTimer = 0;
                                    if (currentTropicalSeasonDry) {
                                        seasonCard = DRY_CARD;
                                    } else {
                                        seasonCard = WET_CARD;
                                    }
                                }
                            }
                        }
                    }

                    if (seasonCard != null) {
                        float targetFadeLevel = 0.0F;
                        if (fadeIn) {
                            targetFadeLevel = 1.0F;
                        }

                        if (targetFadeLevel > fadeLevel) {
                            fadeLevel = Math.min(targetFadeLevel, fadeLevel + (float) Math.round(1.0F / (float) Config.Baked.seasonCardsFadeInInTicks * 100.0F) / 100.0F);
                        }

                        if (targetFadeLevel < fadeLevel) {
                            fadeLevel = Math.max(targetFadeLevel, fadeLevel - (float) Math.round(1.0F / (float) Config.Baked.seasonCardsFadeOutInTicks * 100.0F) / 100.0F);
                        }

                        if (fadeLevel == 1.0F) {
                            if (cardTimer++ >= Config.Baked.seasonCardsDisplayTimeInTicks) {
                                fadeIn = false;
                            }
                        } else if (fadeLevel == 0.0F) {
                            seasonCard = null;
                        }
                    }

                }
            } else {
                if (ecliptic$lastSeason != null) {
                    reset();
                }

            }
        }
    }

    public static void reset() {
        ecliptic$lastSeason = null;
        seasonCard = null;
        fadeLevel = 0.0F;
    }

    public static void init() {
        seasonCard = null;
        ecliptic$lastSeason = null;
        lastDimension = null;
    }
}

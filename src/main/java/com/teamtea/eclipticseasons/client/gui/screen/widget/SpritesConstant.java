package com.teamtea.eclipticseasons.client.gui.screen.widget;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.Season;

public class SpritesConstant {

    public static final WidgetSprites SPRING = create("spring", false);
    public static final WidgetSprites SPRING_SEASONAL = create("spring", true);
    public static final WidgetSprites SUMMER = create("summer", false);
    public static final WidgetSprites SUMMER_SEASONAL = create("summer", true);
    public static final WidgetSprites AUTUMN = create("autumn", false);
    public static final WidgetSprites AUTUMN_SEASONAL = create("autumn", true);
    public static final WidgetSprites WINTER = create("winter", false);
    public static final WidgetSprites WINTER_SEASONAL = create("winter", true);

    private static WidgetSprites CLIENT_SPRITES = SPRING;
    private static WidgetSprites CLIENT_SPRITES_SEASONAL = SPRING_SEASONAL;

    public static void setSeason(Season season) {
        switch (season) {
            case SPRING -> {
                setClientSprites(SPRING);
                setClientSpritesSeasonal(SPRING_SEASONAL);
            }
            case SUMMER -> {
                setClientSprites(SUMMER);
                setClientSpritesSeasonal(SUMMER_SEASONAL);
            }
            case AUTUMN -> {
                setClientSprites(AUTUMN);
                setClientSpritesSeasonal(AUTUMN_SEASONAL);
            }
            case WINTER -> {
                setClientSprites(WINTER);
                setClientSpritesSeasonal(WINTER_SEASONAL);
            }
        }
    }

    protected static WidgetSprites create(String season, boolean seasonal) {
        String path = "widget/" + season + "/es_button"
                + (seasonal ? "_season" : "");

        return new WidgetSprites(
                EclipticSeasons.rl(path),
                EclipticSeasons.rl(path + "_disabled"),
                EclipticSeasons.rl(path + "_highlighted")
        );
    }

    public static WidgetSprites getClientSprites() {
        return CLIENT_SPRITES;
    }

    public static void setClientSprites(WidgetSprites clientSprites) {
        CLIENT_SPRITES = clientSprites;
    }

    public static WidgetSprites getClientSpritesSeasonal() {
        return CLIENT_SPRITES_SEASONAL;
    }

    public static void setClientSpritesSeasonal(WidgetSprites clientSpritesSeasonal) {
        CLIENT_SPRITES_SEASONAL = clientSpritesSeasonal;
    }
}
package com.teamtea.eclipticseasons.config;

import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.config.ModConfig;

import java.io.Serializable;
import java.util.*;

public class ClientConfig {

    public static final ForgeConfigSpec CLIENT_CONFIG = new ForgeConfigSpec.Builder().configure(ClientConfig::new).getRight();

    protected ClientConfig(ForgeConfigSpec.Builder builder) {
        GUI.load(builder);
        Renderer.load(builder);
        Sound.load(builder);
    }

    @SafeVarargs
    public static <T> List<T> of(T... objs) {
        return Arrays.stream(objs).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    private static boolean testLList(Object o) {
        if (o instanceof List) {
            List<?> innerChildren = (List<?>) o;
            if (innerChildren.size() == 2) {
                return innerChildren.get(0) instanceof String
                        && innerChildren.get(1) instanceof Integer;
            }
        }
        return false;
    }

    public static final Map<BiomeDictionary.Type,Integer> SNOW_LINE_BIOME =new IdentityHashMap<>();
    public static void UpdateConfig(ModConfig.ModConfigEvent modConfigEvent) {
        if (modConfigEvent.getConfig().getSpec() == CLIENT_CONFIG) {
            SNOW_LINE_BIOME.clear();
            for (List<? extends Serializable> serializables : Renderer.snowBiomeLine.get()) {
                SNOW_LINE_BIOME.put(BiomeDictionary.Type.getType(serializables.get(0).toString()),
                        Integer.parseInt(serializables.get(1).toString()));
            }
        }
    }

    public static class GUI {
        public static ForgeConfigSpec.IntValue playerTemperatureX;
        public static ForgeConfigSpec.IntValue playerTemperatureY;
        public static ForgeConfigSpec.BooleanValue debugInfo;

        private static void load(ForgeConfigSpec.Builder builder) {
            builder.push("GUI");
            playerTemperatureX = builder.comment("The position X of Player Temperature UI")
                    .defineInRange("PlayerTemperatureX", 10, Integer.MIN_VALUE, Integer.MAX_VALUE);
            playerTemperatureY = builder.comment("The position Y of Player Temperature UI")
                    .defineInRange("PlayerTemperatureY", 40, Integer.MIN_VALUE, Integer.MAX_VALUE);
            debugInfo = builder.comment("Info used for development.")
                    .define("DebugInfo", false);
            builder.pop();
        }
    }

    public static class Renderer {
        public static ForgeConfigSpec.BooleanValue forceChunkRenderUpdate;
        public static ForgeConfigSpec.BooleanValue useVanillaCheck;
        public static ForgeConfigSpec.BooleanValue snowyWinter;
        public static ForgeConfigSpec.BooleanValue deeperSnow;
        public static ForgeConfigSpec.BooleanValue underSnow;
        public static ForgeConfigSpec.BooleanValue particle;
        public static ForgeConfigSpec.IntValue snowLine;
        public static ForgeConfigSpec.BooleanValue notSnowOverlayGlowingBlock;
        public static ForgeConfigSpec.BooleanValue notSnowyNearGlowingBlock;
        public static ForgeConfigSpec.IntValue notSnowyNearGlowingBlockLevel;
        public static ForgeConfigSpec.ConfigValue<List<? extends List<? extends Serializable>>> snowBiomeLine;
        public static ForgeConfigSpec.BooleanValue realisticSnowyChange;

        private static void load(ForgeConfigSpec.Builder builder) {
            builder.push("Renderer");
            forceChunkRenderUpdate = builder.comment("Force to update chunk rendering.")
                    .define("ForceChunkRenderUpdate", true);
            useVanillaCheck = builder.comment("Force to update chunk rendering.")
                    .define("useVanillaCheck", false);
            snowyWinter = builder.comment("If snow falls during cold weather, it will gradually cover all solid blocks and grass.")
                    .define("SnowyWinter", true);
            realisticSnowyChange = builder.comment("When the block is updated, the snow cover will not refresh immediately, but will be updated after a delay. Please note that this will consume more performance..")
                    .define("RealisticSnowyChange", true);
            deeperSnow = builder.comment("Occasionally a thicker layer of snow will cover the flowers and grass, especially.")
                    .define("DeeperSnow", false);
            underSnow = builder.comment("Blocks below fences and bamboo will also accumulate snow.")
                    .define("UnderSnow", false);
            notSnowOverlayGlowingBlock = builder.comment("Snow will not overlay the block has block light.")
                    .define("NotSnowOverlayGlowingBlock", true);
            notSnowyNearGlowingBlock = builder.comment("Snow will not appear in overly bright areas, here define restriction levels.")
                    .define("NotSnowyNearGlowingBlock", true);
            notSnowyNearGlowingBlockLevel = builder.comment("Snow will not appear in overly bright areas.")
                    .defineInRange("NotSnowyNearGlowingBlockLevel", 10, 1, 15);
            particle = builder.comment("Seasonal Particle.")
                    .define("Particle", true);
            snowLine = builder.comment("Snow Line Height.")
                    .defineInRange("SowLineHeight", 111, Integer.MIN_VALUE, Integer.MAX_VALUE);
            snowBiomeLine = builder.comment("Snow Line Height.")
                    .defineList("SowLineHeightBiome", of(of(BiomeDictionary.Type.COLD.toString(), 95), of(BiomeDictionary.Type.HOT.toString(), 143)), ClientConfig::testLList);
            builder.pop();
        }
    }


    public static class Sound {
        public static ForgeConfigSpec.BooleanValue sound;

        private static void load(ForgeConfigSpec.Builder builder) {
            builder.push("Sound");
            sound = builder.comment("Seasonal Ambient Sound.")
                    .define("Sound", true);
            builder.pop();
        }
    }

}

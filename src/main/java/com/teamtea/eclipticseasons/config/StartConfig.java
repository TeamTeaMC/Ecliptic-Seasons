package com.teamtea.eclipticseasons.config;


import net.neoforged.neoforge.common.ModConfigSpec;

public class StartConfig {
    public static final ModConfigSpec START_CONFIG = new ModConfigSpec.Builder().configure(StartConfig::new).getRight();

    protected StartConfig(ModConfigSpec.Builder builder) {
        Resource.load(builder);

    }

    public static class Resource {
        public static ModConfigSpec.BooleanValue extraSnow;

        private static void load(ModConfigSpec.Builder builder) {
            builder.push("Resource");
            extraSnow = builder.comment("Enable extra built-in snow definitions resourcepack for game.")
                    .define("ExtraSnowDefinitions", false);
            builder.pop();
        }
    }


}


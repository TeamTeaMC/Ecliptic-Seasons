package com.teamtea.eclipticseasons.compat.eclipticseasons_bundles;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Data;

import java.util.List;
import java.util.Optional;

@Data
public class BundleConfig {

    public static final Codec<BundleConfig> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Codec.STRING.fieldOf("id").forGetter(o -> o.id),
            Codec.STRING.listOf().optionalFieldOf("require", List.of()).forGetter(o -> o.require),
            Codec.STRING.listOf().optionalFieldOf("mc_version", List.of()).forGetter(o -> o.require),
            Codec.INT.optionalFieldOf("version", 0).forGetter(o -> o.version),
            Codec.BOOL.optionalFieldOf("enable", true).forGetter(o -> o.enable),
            Codec.BOOL.optionalFieldOf("server_only").forGetter(o -> o.serverOnly),
            Codec.STRING.optionalFieldOf("description", "").forGetter(o -> o.description),
            Codec.BOOL.optionalFieldOf("top", false).forGetter(o -> o.top),
            Codec.BOOL.optionalFieldOf("require_all", true).forGetter(o -> o.requireAll)
    ).apply(ins, BundleConfig::new));

    private final String id;
    private final List<String> require;
    private final List<String> mcVersion;
    private final int version;
    private final boolean enable;
    private final Optional<Boolean> serverOnly;
    private final String description;
    private final boolean top;
    private final boolean requireAll;
}

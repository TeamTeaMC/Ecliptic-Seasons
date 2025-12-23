package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.api.util.serializer.ESCommonSerializer;
import com.teamtea.eclipticseasons.common.advancement.SolarTermsRecord;
import com.teamtea.eclipticseasons.common.core.map.BiomeHolder;
import com.teamtea.eclipticseasons.common.core.map.NoneSnowArea;
import com.teamtea.eclipticseasons.common.core.map.SnowyRemover;
import com.teamtea.eclipticseasons.common.core.snow.SnowyStatusHandler;
import com.teamtea.eclipticseasons.common.core.snow.SnowyStatusKeeper;
import com.teamtea.eclipticseasons.common.core.snow.WeatherStatusKeeper;
import com.teamtea.eclipticseasons.common.item.attachment.ClickPos;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class AttachmentRegistry {
    // DataComponent
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, EclipticSeasonsApi.MODID);
    public static final Supplier<AttachmentType<BiomeHolder>> BIOME_HOLDER = ATTACHMENT_TYPES.register(
            "biome_holder",
            () -> AttachmentType.builder(BiomeHolder::empty).serialize(new BiomeHolder.Serializer()).build());
    public static final Supplier<AttachmentType<SnowyRemover>> SNOWY_REMOVER = ATTACHMENT_TYPES.register(
            "snowy_remover",
            () -> AttachmentType.builder(SnowyRemover::empty).serialize(new SnowyRemover.Serializer()).build());
    public static final Supplier<AttachmentType<SolarTermsRecord>> SOLAR_TERMS_RECORD = ATTACHMENT_TYPES.register(
            "solar_terms_record",
            () -> AttachmentType.builder(() -> new SolarTermsRecord(new Object2IntLinkedOpenHashMap<>())).serialize(SolarTermsRecord.CODEC).build());

    public static final Supplier<AttachmentType<SnowyStatusKeeper>> SNOWY_STATUS_KEEPER = ATTACHMENT_TYPES.register(
            "snowy_status",
            () -> AttachmentType.builder(SnowyStatusKeeper::create)
                    .serialize(new SnowyStatusKeeper.Serializer())
                    .sync(SnowyStatusHandler.INSTANCE).build());

    public static final Supplier<AttachmentType<WeatherStatusKeeper>> WEATHER_STATUS_KEEPER = ATTACHMENT_TYPES.register(
            "weather_status",
            () -> AttachmentType.builder(WeatherStatusKeeper::create)
                    .serialize(new WeatherStatusKeeper.Serializer())
                    .build());

    public static final Supplier<AttachmentType<NoneSnowArea>> NONE_SNOW_AREA = ATTACHMENT_TYPES.register(
            "none_snow_area",
            () -> AttachmentType.builder(NoneSnowArea::empty)
                    .sync((a, b) -> !EclipticUtil.canSnowyBlockInteract(), NoneSnowArea.STREAM_CODEC)
                    .serialize(new ESCommonSerializer<>(NoneSnowArea.CODEC, NoneSnowArea::empty))
                    .build());


}

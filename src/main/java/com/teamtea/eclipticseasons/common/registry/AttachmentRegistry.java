package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.advancement.SolarTermsRecord;
import com.teamtea.eclipticseasons.common.core.map.BiomeHolder;
import com.teamtea.eclipticseasons.common.core.map.SnowyRemover;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.function.Supplier;

public class AttachmentRegistry {
    // DataComponent
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, EclipticSeasonsApi.MODID);
    public static final Supplier<AttachmentType<BiomeHolder>> BIOME_HOLDER = ATTACHMENT_TYPES.register(
            "biome_holder",
            () -> AttachmentType.builder(() -> new BiomeHolder(new int[256], false, 0)).serialize(BiomeHolder.CODEC).build());
    public static final Supplier<AttachmentType<SnowyRemover>> SNOWY_REMOVER = ATTACHMENT_TYPES.register(
            "snowy_remover",
            () -> AttachmentType.builder(() -> new SnowyRemover(new int[16][16])).serialize(SnowyRemover.CODEC).build());
    public static final Supplier<AttachmentType<SolarTermsRecord>> SOLAR_TERMS_RECORD = ATTACHMENT_TYPES.register(
            "solar_terms_record",
            () -> AttachmentType.builder(() -> new SolarTermsRecord(new ArrayList<>())).serialize(SolarTermsRecord.CODEC).build());
}

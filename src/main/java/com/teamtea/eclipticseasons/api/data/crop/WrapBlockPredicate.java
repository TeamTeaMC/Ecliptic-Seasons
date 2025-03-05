package com.teamtea.eclipticseasons.api.data.crop;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public record WrapBlockPredicate(Optional<HolderSet<Block>> blockHolderSet) {




}

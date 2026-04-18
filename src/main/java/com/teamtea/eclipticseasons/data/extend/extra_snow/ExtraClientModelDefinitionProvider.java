package com.teamtea.eclipticseasons.data.extend.extra_snow;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.client.model.ESModelLoadedJson;
import com.teamtea.eclipticseasons.data.api.provider.AbstractModelDefinitionProvider;
import net.minecraft.client.data.models.blockstates.ConditionBuilder;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelDispatcher;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.renderer.block.dispatch.multipart.Selector;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowyBlock;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ExtraClientModelDefinitionProvider extends AbstractModelDefinitionProvider {
    public ExtraClientModelDefinitionProvider(PackOutput output, String modid, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, modid, registries);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        // addSnowy(Blocks.FIREFLY_BUSH).singleCross();
    }


    @Override
    public @NonNull String getName() {
        return super.getName() + "(Extra Version)";
    }
}

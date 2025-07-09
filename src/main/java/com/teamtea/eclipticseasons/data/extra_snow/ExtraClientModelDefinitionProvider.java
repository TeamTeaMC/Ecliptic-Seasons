package com.teamtea.eclipticseasons.data.extra_snow;

import com.teamtea.eclipticseasons.data.general.datapack.client.ClientModelDefinitionProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ExtraClientModelDefinitionProvider extends ClientModelDefinitionProvider {
    public ExtraClientModelDefinitionProvider(PackOutput output, String modid, ExistingFileHelper helper, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, modid, helper, registries);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {

        addSnowyBlockModelDefinition(Blocks.SUNFLOWER)
                .variantsForAllStatesExceptExact(state ->
                {
                    if (state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER) {
                        return models().withExistingParent("block/snowy/sunflower_bottom", "sunflower_bottom")
                                .texture("cross", "block/snowy/sunflower_bottom");
                    } else {
                        return models().withExistingParent("block/snowy/sunflower_top", "sunflower_top")
                                .texture("cross", "block/snowy/sunflower_top")
                                .texture("front", "block/snowy/sunflower_front")
                                .texture("back", "block/snowy/sunflower_back")
                                .texture("particle", "block/snowy/sunflower_front");
                    }
                })
                .replace(true);

        addSnowyPlant(Blocks.OAK_SAPLING);
        addSnowyPlant(Blocks.DARK_OAK_SAPLING);
        addSnowyPlant(Blocks.ACACIA_SAPLING);
        addSnowyPlant(Blocks.BIRCH_SAPLING);
        addSnowyPlant(Blocks.JUNGLE_SAPLING);
        addSnowyPlant(Blocks.SPRUCE_SAPLING);
        addSnowyPlant(Blocks.CHERRY_SAPLING);

        addSnowyBlockModelDefinition(Blocks.MANGROVE_PROPAGULE)
                .variantsForAllStatesExceptExact(state ->
                {
                    if (state.getValue(MangrovePropaguleBlock.HANGING)) {
                        return models().withExistingParent("block/snowy/mangrove_propagule_hanging_" + state.getValue(MangrovePropaguleBlock.AGE), "mangrove_propagule_hanging_" + state.getValue(MangrovePropaguleBlock.AGE))
                                .texture("propagule", "block/snowy/mangrove_propagule_hanging");
                    } else {
                        return models().withExistingParent("block/snowy/mangrove_propagule", "mangrove_propagule")
                                .texture("sapling", "block/snowy/mangrove_propagule");
                    }
                }, SaplingBlock.STAGE, BlockStateProperties.WATERLOGGED)
                .replace(true);
    }

    @Override
    public @NotNull String getName() {
        return super.getName() + "(Extra Version)";
    }
}

package com.teamtea.eclipticseasons.data.general.model;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;


public class ESBlockModelProvider extends BlockModelProvider {


    public static final String BLOCK = "block/block";
    public static final String HANDHELD = "item/handheld";

    public ESBlockModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }


    @Override
    protected void registerModels() {
        for (ResourceLocation flowerOnGrass : ExtraModelManager.flower_on_grass) {
            withExistingParent(flowerOnGrass.getPath(),resource("grass_flower"))
                    // .element()
                    // .from(0,0,0)
                    // .to(1,1,1)
                    // .end()
                    .texture("1",flowerOnGrass.getPath());
        }

        for (ResourceLocation flowerOnGrass : ExtraModelManager.fourleaf_clovers) {
            withExistingParent(flowerOnGrass.getPath(),resource("tinted_grass_flower"))
                    .texture("1",flowerOnGrass.getPath());
        }

        // for (SolarTerm solarTerm : SolarTerm.collectValues()) {
        //     getBuilder(soa)
        // }
        cubeAll(resource(BlockRegistry.block_in_wooden_grate_block.getId().getPath()).getPath(),EclipticSeasons.rl("block/wooden_grate"));
    }



    public ResourceLocation resource(String path) {
        return EclipticSeasons.rl("block/" + path);
    }


}

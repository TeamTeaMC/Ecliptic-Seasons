package com.teamtea.eclipticseasons.compat.jade;


import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.common.block.GreenHouseCoreBlock;
import com.teamtea.eclipticseasons.common.block.IceOrSnowCauldronBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadeCompact implements IWailaPlugin {
    public static final ResourceLocation SHIFT_HINT = EclipticSeasons.rl("crop.shift_hint");
    public static final ResourceLocation SNOWY_STATUS = EclipticSeasons.rl("snowy_status");

    public JadeCompact() {
    }


    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(JadeCropInfoProvider.INSTANCE, Block.class);
        registration.registerEntityComponent(JadeAnimalBreedInfoProvider.INSTANCE, LivingEntity.class);
        registration.registerBlockComponent(JadeESCauldronInfoProvider.INSTANCE, IceOrSnowCauldronBlock.class);
        registration.registerBlockComponent(JadeESGreenHouseCoreProvider.INSTANCE, GreenHouseCoreBlock.class);

        registration.addConfig(SHIFT_HINT, true);
        registration.addConfig(SNOWY_STATUS, true);
    }
}

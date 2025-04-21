package com.teamtea.eclipticseasons.compat.jade;


import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadeCompact implements IWailaPlugin {
    public static final ResourceLocation SHIFT_HINT = EclipticSeasons.rl("crop.shift_hint");

    // todo:
    // use IServerExtensionProvider and IClientExtensionProvider instaed
    // use FluidView.overrideText change text
    // https://github.com/Snownee/Jade/blob/1.19.1-forge/src/main/java/snownee/jade/test/ExampleFluidStorageProvider.java

    public JadeCompact() {
    }


    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(CropInfoProvider.INSTANCE, Block.class);
        registration.addConfig(SHIFT_HINT,true);
    }
}

package com.teamtea.eclipticseasons.mixin.client.model;


import com.mojang.blaze3d.platform.NativeImage;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import com.teamtea.eclipticseasons.client.util.ImageHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.textures.SpriteContentsConstructor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.InputStream;
import java.util.Collection;

@Mixin(SpriteResourceLoader.class)
public interface MixinSpriteLoader {

    @Inject(method = "lambda$create$0", at = @At("RETURN"))
    private static void eclipticseasons$loadSprite(Collection<MetadataSectionSerializer<?>> sectionSerializers, ResourceLocation resourceLocation, Resource resource, SpriteContentsConstructor constructor, CallbackInfoReturnable<SpriteContents> cir) {
        if (true) {
            return;
        }
        if (cir.getReturnValue() == null) {
            return;
        }

        if (!cir.getReturnValue().name().getPath().startsWith("block/")
                && !cir.getReturnValue().name().getPath().startsWith("block/")) {
            return;
        }
        if (FMLLoader.isProduction()) {
            return;
        }

        // if (!ExtraModelManager.blocksCache.containsKey(resourceLocation)) {
        //     return;
        // }
        ExtraModelManager.blocksCache.put(resourceLocation, cir.getReturnValue());

        if (cir.getReturnValue().name().getPath().contains("sapling")
                || cir.getReturnValue().name().getNamespace().contains("kaleido")
                || cir.getReturnValue().name().getNamespace().contains("farm")
        ||cir.getReturnValue().name().getNamespace().equals("biomeswevegone")) {
            ResourceMetadata section;
            ResourceMetadata snowySection;
            Resource snowy;
            NativeImage snowyImage;
            try {
                section = resource.metadata();
                snowy = Minecraft.getInstance().getResourceManager().getResourceOrThrow(ResourceLocation.withDefaultNamespace( "textures/block/snow.png"));
                snowySection = snowy.metadata();
                InputStream inputstream = snowy.open();
                snowyImage = NativeImage.read(inputstream);
            } catch (Throwable throwable) {
                EclipticSeasons.logger(throwable.getMessage());
                return;
            }

            SpriteContents content = cir.getReturnValue();
            NativeImage original = content.getOriginalImage();
            ImageHelper.fixSnowImageColor(section, original, snowySection, snowyImage);

        }
    }

}

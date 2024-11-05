package com.teamtea.eclipticseasons.mixin.client.model;


import com.mojang.blaze3d.platform.NativeImage;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.client.core.ModelManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraftforge.fml.loading.FMLLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Mixin(SpriteLoader.class)
public abstract class MixinSpriteLoader {
    @Shadow
    private @Final ResourceLocation location;


    // 我们的问题在于检查
    // 这里可以检查加载的纹理，过一遍
    @Inject(method = "loadSprite", at = @At("RETURN"))
    private static void ecliptic$loadSprite(ResourceLocation resourceLocation, Resource resource, CallbackInfoReturnable<SpriteContents> cir) {
        if (true)
        {
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

        if (!ModelManager.blocksCache.containsKey(resourceLocation)) {
            return;
        }
        ModelManager.blocksCache.put(resourceLocation, cir.getReturnValue());

        if (cir.getReturnValue().name().getPath().contains("brick")) {
            AnimationMetadataSection section;
            AnimationMetadataSection snowySection;
            Resource snowy;
            NativeImage snowyImage;
            try {
                section = resource.metadata().getSection(AnimationMetadataSection.SERIALIZER).orElse(AnimationMetadataSection.EMPTY);
                snowy = Minecraft.getInstance().getResourceManager().getResourceOrThrow(EclipticSeasons.rl("textures/block/snow_overlay.png"));
                snowySection = snowy.metadata().getSection(AnimationMetadataSection.SERIALIZER).orElse(AnimationMetadataSection.EMPTY);
                InputStream inputstream = snowy.open();
                snowyImage = NativeImage.read(inputstream);
            } catch (Throwable throwable) {
                EclipticSeasons.logger(throwable.getMessage());
                return;
            }

            SpriteContents content = cir.getReturnValue();
            NativeImage original = content.getOriginalImage();
            FrameSize frameSize = section.calculateFrameSize(original.getWidth(), original.getHeight());

            for (int i = 0; i < frameSize.width(); i++) {
                for (int j = 0; j < frameSize.height(); j++) {
                    // Color color = new Color(snowyImage.getPixelRGBA(i, j));
                    // if(color.getRGB()>0)
                    if (snowyImage.getPixelRGBA(i, j) != 0) {
                        original.setPixelRGBA(i, j, snowyImage.getPixelRGBA(i, j));
                    }
                }
            }

        }
    }


    // 这个方法改一下，总之可以改
    @ModifyVariable(method = "stitch", argsOnly = true, index = 1, at = @At("HEAD"))
    private List<SpriteContents> ecliptic$stitch(List<SpriteContents> contents) {
        if (true)
        {
            return contents;
        }
        if (location.equals(TextureAtlas.LOCATION_BLOCKS)) {
            ArrayList<SpriteContents> replacedContents = new ArrayList<>(contents);

            AnimationMetadataSection snowySection;
            Resource snowy;
            NativeImage snowyImage = null;
            try {
                snowy = Minecraft.getInstance().getResourceManager().getResourceOrThrow(EclipticSeasons.rl("textures/block/snow_overlay.png"));
                snowySection = snowy.metadata().getSection(AnimationMetadataSection.SERIALIZER).orElse(AnimationMetadataSection.EMPTY);
                snowyImage = NativeImage.read(snowy.open());
            } catch (Throwable throwable) {
                EclipticSeasons.logger(throwable.getMessage());
            }
            if (snowyImage != null) {
                NativeImage finalSnowyImage = snowyImage;
                ModelManager.blocksCache.forEach(
                        (resourceLocation, spriteContents) -> {
                            if(spriteContents!=null) {
                                NativeImage originalImage = spriteContents.getOriginalImage();
                                if (originalImage.getWidth() == finalSnowyImage.getWidth()
                                        && originalImage.getHeight() == finalSnowyImage.getHeight()) {
                                    SpriteContents spriteContents1 = ecliptic$getProcessedSpriteContents(finalSnowyImage, spriteContents);
                                    if (spriteContents1 != null)
                                        replacedContents.add(spriteContents1);
                                }
                            }
                        }
                );
            }

            return replacedContents;
        }

        return contents;
    }

    @Unique
    private static SpriteContents ecliptic$getProcessedSpriteContents(
            NativeImage snowyImage, SpriteContents contents) {
        ResourceLocation name = contents.name();
        AnimationMetadataSection section = null;

        try {
            ResourceLocation rs=new ResourceLocation(contents.name().getNamespace(),"textures/"+contents.name().getPath()+".png");
            Resource resource = Minecraft.getInstance().getResourceManager().getResourceOrThrow(rs);
            section = resource.metadata().getSection(AnimationMetadataSection.SERIALIZER).orElse(AnimationMetadataSection.EMPTY);
        } catch (Throwable throwable) {
            EclipticSeasons.logger(throwable.getMessage());
            return null;
        }
        NativeImage original = contents.getOriginalImage();
        FrameSize frameSize = section.calculateFrameSize(original.getWidth(), original.getHeight());
        NativeImage image = new NativeImage(
                contents.getOriginalImage().format(),
                contents.getOriginalImage().getWidth(),
                contents.getOriginalImage().getHeight(),
                true);

        for (int i = 0; i < frameSize.width(); i++) {
            for (int j = 0; j < frameSize.height(); j++) {
                if (snowyImage.getPixelRGBA(i, j) != 0) {
                    image.setPixelRGBA(i, j, snowyImage.getPixelRGBA(i, j));
                } else {
                    image.setPixelRGBA(i, j, original.getPixelRGBA(i, j));
                }
            }
        }


        return new SpriteContents(name.withSuffix(".snowy"), frameSize, image, section);
    }
}

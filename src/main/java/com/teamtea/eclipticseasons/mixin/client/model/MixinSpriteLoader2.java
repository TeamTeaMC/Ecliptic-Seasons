package com.teamtea.eclipticseasons.mixin.client.model;


import com.mojang.blaze3d.platform.NativeImage;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import com.teamtea.eclipticseasons.client.util.ImageHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.fml.loading.FMLLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Mixin(SpriteLoader.class)
public abstract class MixinSpriteLoader2 {
    @Shadow
    private @Final ResourceLocation location;


    @ModifyVariable(method = "stitch", argsOnly = true, index = 1, at = @At("HEAD"))
    private List<SpriteContents> eclipticseasons$stitch(List<SpriteContents> contents) {
        if (true) {
            return contents;
        }
        if (location.equals(InventoryMenu.BLOCK_ATLAS)) {
            ArrayList<SpriteContents> replacedContents = new ArrayList<>(contents);

            ResourceMetadata snowySection=null;
            Resource snowy;
            NativeImage snowyImage = null;
            try {
                snowy = Minecraft.getInstance().getResourceManager().getResourceOrThrow(EclipticSeasons.rl("textures/block/snow_overlay.png"));
                snowySection = snowy.metadata();
                snowyImage = NativeImage.read(snowy.open());
            } catch (Throwable throwable) {
                EclipticSeasons.logger(throwable.getMessage());
            }
            if (snowyImage != null) {
                NativeImage finalSnowyImage = snowyImage;
                ResourceMetadata finalSnowySection = snowySection;
                ExtraModelManager.blocksCache.forEach(
                        (resourceLocation, spriteContents) -> {
                            if (spriteContents != null) {
                                NativeImage originalImage = spriteContents.getOriginalImage();
                                if (originalImage.getWidth() == finalSnowyImage.getWidth()
                                        && originalImage.getHeight() == finalSnowyImage.getHeight()) {
                                    SpriteContents spriteContents1 = eclipticseasons$getProcessedSpriteContents(finalSnowyImage, finalSnowySection, spriteContents);
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
    private static SpriteContents eclipticseasons$getProcessedSpriteContents(
            NativeImage snowyImage, ResourceMetadata snowySection, SpriteContents contents) {
        ResourceLocation name = contents.name();
        ResourceMetadata section = null;

        try {
            ResourceLocation rs =  ResourceLocation.fromNamespaceAndPath(contents.name().getNamespace(), "textures/" + contents.name().getPath() + ".png");
            Resource resource = Minecraft.getInstance().getResourceManager().getResourceOrThrow(rs);
            section = resource.metadata();
        } catch (Throwable throwable) {
            EclipticSeasons.logger(throwable.getMessage());
            return null;
        }
        NativeImage original = contents.getOriginalImage();
        FrameSize frameSize = section.getSection(AnimationMetadataSection.SERIALIZER).orElse(AnimationMetadataSection.EMPTY).calculateFrameSize(original.getWidth(), original.getHeight());
        NativeImage image = new NativeImage(
                contents.getOriginalImage().format(),
                contents.getOriginalImage().getWidth(),
                contents.getOriginalImage().getHeight(),
                true);
        //
        // for (int i = 0; i < frameSize.width(); i++) {
        //     for (int j = 0; j < frameSize.height(); j++) {
        //         if (snowyImage.getPixelRGBA(i, j) != 0) {
        //             image.setPixelRGBA(i, j, snowyImage.getPixelRGBA(i, j));
        //         } else {
        //             image.setPixelRGBA(i, j, original.getPixelRGBA(i, j));
        //         }
        //     }
        // }

        ImageHelper.fixSnowImageColor(section, original, snowySection, snowyImage);


        return new SpriteContents(name
                .withSuffix(".snowy")
                , frameSize, image, section);
    }
}

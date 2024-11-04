package com.teamtea.eclipticseasons.mixin.client.model;


import com.mojang.blaze3d.platform.NativeImage;
import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.fml.loading.FMLLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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
    private static void ecliptic$loadSprite(ResourceLocation p_251630_, Resource resource, CallbackInfoReturnable<SpriteContents> cir) {
        if (cir.getReturnValue() == null) {
            return;
        }

        if (!cir.getReturnValue().name().getPath().startsWith("block/")
                && !cir.getReturnValue().name().getPath().startsWith("block/")) {
            return;
        }
        if (!FMLLoader.isProduction())
        if (cir.getReturnValue().name().getPath().contains("brick")) {
            AnimationMetadataSection section;
            AnimationMetadataSection snowySection;
            Resource snowy;
            NativeImage snowyImage;
            try {
                section = resource.metadata().getSection(AnimationMetadataSection.SERIALIZER).orElse(AnimationMetadataSection.EMPTY);
                snowy = Minecraft.getInstance().getResourceManager().getResourceOrThrow(EclipticSeasons.rl("textures/block/snow_overlay.png"));
                snowySection= snowy.metadata().getSection(AnimationMetadataSection.SERIALIZER).orElse(AnimationMetadataSection.EMPTY);
                InputStream inputstream = snowy.open();
                snowyImage = NativeImage.read(inputstream);
            } catch (Throwable throwable) {
               EclipticSeasons.logger( throwable.getMessage());
                return;
            }

            SpriteContents content = cir.getReturnValue();
            NativeImage original = content.getOriginalImage();
            FrameSize frameSize = section.calculateFrameSize(original.getWidth(), original.getHeight());

            for (int i = 0; i < frameSize.width(); i++) {
                for (int j = 0; j < frameSize.height(); j++) {
                    // Color color = new Color(snowyImage.getPixelRGBA(i, j));
                    // if(color.getRGB()>0)
                    if(snowyImage.getPixelRGBA(i,j)!=0)
                    {
                        original.setPixelRGBA(i,j,snowyImage.getPixelRGBA(i,j));
                    }
                }
            }

        }
    }


    // 这个方法改一下，总之可以改
    @ModifyVariable(method = "stitch", argsOnly = true, index = 1, at = @At("HEAD"))
    private List<SpriteContents> ecliptic$stitch(List<SpriteContents> contents) {
        if (location.equals(TextureAtlas.LOCATION_BLOCKS)) {
            ArrayList<SpriteContents> replacedContents = new ArrayList<>(contents);

            return replacedContents;
        }

        return contents;
    }

}

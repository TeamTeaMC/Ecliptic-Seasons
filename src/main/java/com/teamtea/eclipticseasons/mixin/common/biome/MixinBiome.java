package com.teamtea.eclipticseasons.mixin.common.biome;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags;
import com.teamtea.eclipticseasons.api.misc.IBiomeTagHolder;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.compat.vanilla.VanillaWeather;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Biome.class})
public abstract class MixinBiome implements IBiomeTagHolder {
    @Inject(at = {@At("HEAD")}, method = {"getPrecipitationAt"}, cancellable = true)
    public void eclipticseasons$getPrecipitationAt(BlockPos pos, int seaLevel, CallbackInfoReturnable<Biome.Precipitation> cir) {
        if (EclipticUtil.useSolarWeather()) {
            cir.setReturnValue(WeatherManager.getPrecipitationAt((Biome) (Object) this, pos));
        } else {
            cir.setReturnValue(VanillaWeather.handlePrecipitationAt((Biome) (Object) this, pos));
        }
    }

    @Inject(at = {@At("HEAD")}, method = {"hasPrecipitation"}, cancellable = true)
    public void eclipticseasons$hasPrecipitation(CallbackInfoReturnable<Boolean> cir) {
        if (EclipticUtil.useSolarWeather())
            cir.setReturnValue(BiomeClimateManager.agent$hasPrecipitation((Biome) (Object) this));
        else {
            if (BiomeClimateManager.getTag((Biome) (Object) this).equals(ClimateTypeBiomeTags.MONSOONAL)) {
                cir.setReturnValue(VanillaWeather.hasMonsoonalPrecipitation((Biome) (Object) this));
            }
        }
    }


    // Since now mojang use PrecipitationAt not coldEnoughToSnow, so something is changed
    @WrapOperation(at = {@At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitationAt(Lnet/minecraft/core/BlockPos;I)Lnet/minecraft/world/level/biome/Biome$Precipitation;")},
            method = {"shouldSnow"})
    public Biome.Precipitation eclipticseasons$shouldSnow_getPrecipitationAt(Biome instance, BlockPos pos, int seaLevel, Operation<Biome.Precipitation> original) {
        return instance.hasPrecipitation() && instance.coldEnoughToSnow(pos, seaLevel) ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN;
    }


    // ============================


    @Unique
    private boolean eclipticseasons$small = false;
    @Unique
    private int eclipticseasons$id = -1;

    @Unique
    private TagKey<Biome> eclipticseasons$biomeTagKey = ClimateTypeBiomeTags.RAINLESS;

    @Unique
    private TagKey<Biome> eclipticseasons$biomeColorTagKey = ClimateTypeBiomeTags.NONE_COLOR_CHANGE;

    @Override
    public TagKey<Biome> eclipticseasons$getBindTag() {
        return eclipticseasons$biomeTagKey;
    }

    @Override
    public void eclipticseasons$setTag(TagKey<Biome> tag) {
        this.eclipticseasons$biomeTagKey = tag;
    }

    @Override
    public void eclipticseasons$setColorTag(TagKey<Biome> tag) {
        this.eclipticseasons$biomeColorTagKey = tag;
    }

    @Override
    public TagKey<Biome> eclipticseasons$getBindColorTag() {
        return this.eclipticseasons$biomeColorTagKey;
    }

    @Override
    public boolean eclipticseasons$isSmallBiome() {
        return eclipticseasons$small;
    }

    @Override
    public void eclipticseasons$setSmall(boolean isSmall) {
        this.eclipticseasons$small = isSmall;
    }


    @Override
    public int eclipticseasons$getBindId() {
        return this.eclipticseasons$id;
    }

    @Override
    public void eclipticseasons$setBindId(int id) {
        this.eclipticseasons$id = id;
    }
}

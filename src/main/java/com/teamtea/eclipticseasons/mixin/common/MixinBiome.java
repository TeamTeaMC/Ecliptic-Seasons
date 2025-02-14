package com.teamtea.eclipticseasons.mixin.common;


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
    public void ecliptic$getPrecipitationAt(BlockPos pos, CallbackInfoReturnable<Biome.Precipitation> cir) {
        if (EclipticUtil.useSolarWeather())
            cir.setReturnValue(WeatherManager.getPrecipitationAt((Biome) (Object) this, pos));
        else {
            cir.setReturnValue(VanillaWeather.handlePrecipitationAt((Biome) (Object) this, pos));
        }
    }

    @Inject(at = {@At("HEAD")}, method = {"getBaseTemperature"}, cancellable = true)
    public void ecliptic$getBaseTemperature(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(BiomeClimateManager.agent$GetBaseTemperature((Biome) (Object) this));
    }


    @Inject(at = {@At("HEAD")}, method = {"hasPrecipitation"}, cancellable = true)
    public void ecliptic$hasPrecipitation(CallbackInfoReturnable<Boolean> cir) {
        if (EclipticUtil.useSolarWeather())
            cir.setReturnValue(BiomeClimateManager.agent$hasPrecipitation((Biome) (Object) this));
        else {
            if (BiomeClimateManager.getTag((Biome) (Object) this).equals(ClimateTypeBiomeTags.MONSOONAL)) {
                cir.setReturnValue(VanillaWeather.hasMonsoonalPrecipitation((Biome) (Object) this));
            }
        }
    }

    //
    // @Inject(at = {@At("HEAD")}, method = {"shouldFreeze(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z"}, cancellable = true)
    // public void ecliptic$shouldFreeze(LevelReader p_47520_, BlockPos p_47521_, CallbackInfoReturnable<Boolean> cir) {
    // }

    // @Inject(at = {@At("HEAD")}, method = {"warmEnoughToRain"}, cancellable = true)
    // public void ecliptic$warmEnoughToRain(BlockPos p_198905_, CallbackInfoReturnable<Boolean> cir) {
    // }
    //
    // @Inject(at = {@At("HEAD")}, method = {"shouldSnow"}, cancellable = true)
    // public void ecliptic$shouldSnow(LevelReader p_47520_, BlockPos p_47521_, CallbackInfoReturnable<Boolean> cir) {
    // }

    @Unique
    private boolean eclipticSeasons$small = false;
    @Unique
    private int eclipticSeasons$id = -1;

    @Unique
    private TagKey<Biome> eclipticSeasons$biomeTagKey = ClimateTypeBiomeTags.RAINLESS;

    @Override
    public TagKey<Biome> eclipticSeasons$getBindTag() {
        return eclipticSeasons$biomeTagKey;
    }

    @Override
    public void eclipticSeasons$setTag(TagKey<Biome> tag) {
        this.eclipticSeasons$biomeTagKey = tag;
    }

    @Override
    public boolean eclipticSeasons$isSmallBiome() {
        return eclipticSeasons$small;
    }

    @Override
    public void eclipticSeasons$setSmall(boolean isSmall) {
        this.eclipticSeasons$small = isSmall;
    }


    @Override
    public int eclipticSeasons$getBindId() {
        return this.eclipticSeasons$id;
    }

    @Override
    public void eclipticSeasons$setBindId(int id) {
        this.eclipticSeasons$id = id;
    }
}

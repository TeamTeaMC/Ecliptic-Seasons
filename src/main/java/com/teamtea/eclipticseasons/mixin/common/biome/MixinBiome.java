package com.teamtea.eclipticseasons.mixin.common.biome;


import com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags;
import com.teamtea.eclipticseasons.api.misc.IBiomeTagHolder;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Biome.class})
public abstract class MixinBiome implements IBiomeTagHolder {

    @Shadow
    @Deprecated
    public abstract float getTemperature(BlockPos pos);

    @Inject(at = {@At("HEAD")}, method = {"getPrecipitationAt"}, cancellable = true)
    public void eclipticseasons$getPrecipitationAt(BlockPos pos, CallbackInfoReturnable<Biome.Precipitation> cir) {
        cir.setReturnValue(WeatherManager.getPrecipitationAt((Biome) (Object) this, pos));
    }

    @Inject(at = {@At("HEAD")}, method = {"hasPrecipitation"}, cancellable = true)
    public void eclipticseasons$hasPrecipitation(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(BiomeClimateManager.agent$hasPrecipitation((Biome) (Object) this));
    }

    //
    // @Inject(at = {@At("HEAD")}, method = {"getBaseTemperature"}, cancellable = true)
    // public void eclipticseasons$getBaseTemperature(CallbackInfoReturnable<Float> cir) {
    //     cir.setReturnValue(BiomeClimateManager.agent$GetBaseTemperature((Biome) (Object) this));
    // }
    //
    //



    //
    // @ModifyExpressionValue(at = {@At(value = "INVOKE",
    //         target = "Lnet/minecraft/world/level/biome/Biome;getTemperature(Lnet/minecraft/core/BlockPos;)F")},
    //         method = {"warmEnoughToRain"})
    // public float eclipticseasons$warmEnoughToRain(float original) {
    //     Level level = WeatherManager.fetchLevelIfNull(null, (Biome) (Object) this);
    //     if (level != null)
    //         original = BiomeClimateManager.fixTemp(level, (Biome) (Object) this, original);
    //     return original;
    // }
    //
    // @WrapOperation(at = {@At(value = "INVOKE",
    //         target = "Lnet/minecraft/world/level/biome/Biome;warmEnoughToRain(Lnet/minecraft/core/BlockPos;)Z")},
    //         method = {"shouldSnow", "shouldFreeze(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Z)Z"})
    // public boolean eclipticseasons$fixTempWithoutSeason(Biome instance, BlockPos pPos, Operation<Boolean> original, @Local(argsOnly = true) LevelReader levelReader) {
    //     Level level = null;
    //     if (levelReader instanceof WorldGenLevel worldGenLevel) {
    //         level = worldGenLevel.getLevel();
    //     } else if (levelReader instanceof Level level1) {
    //         level = level1;
    //     } else {
    //         level = WeatherManager.fetchLevelIfNull(null, (Biome) (Object) this);
    //     }
    //     if (level != null) {
    //         return BiomeClimateManager.fixTemp(level, (Biome) (Object) this, getTemperature(pPos)) >= BiomeClimateManager.SNOW_LEVEL;
    //     }
    //     return original.call(instance, pPos);
    // }


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
        this.eclipticseasons$biomeColorTagKey=tag;
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

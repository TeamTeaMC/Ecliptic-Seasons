package com.teamtea.eclipticseasons.mixin.common.biome;


import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags;
import com.teamtea.eclipticseasons.api.misc.IBiomeTagHolder;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.mixin.injector.DirectInject;
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
    public abstract float getTemperature(BlockPos p_47506_);

    @DirectInject(at = {@At("HEAD")}, method = {"getPrecipitationAt"},
            mode = DirectInject.Mode.RETURN_WITH_CONTINUATION)
    public Biome.Precipitation eclipticseasons$getPrecipitationAt(BlockPos pos) {
        return (WeatherManager.getPrecipitationAt((Biome) (Object) this, pos));
    }

    @ModifyReturnValue(at = {@At("RETURN")}, method = {"hasPrecipitation"})
    public boolean eclipticseasons$hasPrecipitation(boolean original) {
        return BiomeClimateManager.agent$hasPrecipitation(Biome.class.cast(this));
    }

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

package com.teamtea.eclipticseasons.mixin.common.block;


import com.mojang.serialization.MapCodec;
import com.teamtea.eclipticseasons.api.misc.IBlockStateFlagger;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockState.class)
public abstract class MixinBlockState extends BlockBehaviour.BlockStateBase implements IBlockStateFlagger {

    @Shadow
    protected abstract BlockState asState();

    protected MixinBlockState(Block owner, Reference2ObjectArrayMap<Property<?>, Comparable<?>> values, MapCodec<BlockState> propertiesCodec) {
        super(owner, values, propertiesCodec);
    }


    @Unique
    public int eclipticSeasons$blockTypeFlag = -1;


    @Override
    public int getBlockTypeFlag(BlockGetter level, BlockPos pos) {
        if (eclipticSeasons$blockTypeFlag < 0)
            eclipticSeasons$blockTypeFlag = MapChecker.getBlockType(asState(), level, pos);
        return eclipticSeasons$blockTypeFlag;
    }
}

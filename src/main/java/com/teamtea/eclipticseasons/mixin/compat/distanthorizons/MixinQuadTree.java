package com.teamtea.eclipticseasons.mixin.compat.distanthorizons;


import com.seibel.distanthorizons.core.util.gridList.MovableGridRingList;
import com.seibel.distanthorizons.core.util.objects.quadTree.QuadNode;
import com.seibel.distanthorizons.core.util.objects.quadTree.QuadTree;
import com.teamtea.eclipticseasons.common.mixin.condition.ConditionalMixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@ConditionalMixin(value = "distanthorizons", version = "3.0.0-b")
@Mixin(QuadTree.class)
public interface MixinQuadTree {

    @Accessor(value = "topRingList",remap = false)
    MovableGridRingList<QuadNode> getTopRingList();

}

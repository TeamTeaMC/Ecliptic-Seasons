package com.teamtea.eclipticseasons.mixin.compat.distanthorizons;


import com.seibel.distanthorizons.core.util.gridList.MovableGridRingList;
import com.seibel.distanthorizons.core.util.objects.quadTree.QuadNode;
import com.seibel.distanthorizons.core.util.objects.quadTree.QuadTree;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(QuadTree.class)
public interface MixinQuadTree {

    @Accessor(value = "topRingList",remap = false)
    MovableGridRingList<QuadNode> getTopRingList();

}

package com.teamtea.eclipticseasons.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.client.model.entity.TryKeyframe;
import com.teamtea.eclipticseasons.client.model.entity.TryModel;
import com.teamtea.eclipticseasons.common.block.GreenHouseCoreBlock;
import com.teamtea.eclipticseasons.common.item.GreenhouseEssenceItem;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.NonNull;

import org.jspecify.annotations.Nullable;
import java.util.function.Consumer;

public class GreenHouseCoreSpecialRenderer implements SpecialModelRenderer<Integer> {
    private final SpriteGetter materialSet;
    private final SpriteId material;
    private ModelPart modelPart;
    private Identifier texture;
    private static final Vector3f ANIMATION_VECTOR_CACHE = new Vector3f();
    private final boolean isCore;

    public GreenHouseCoreSpecialRenderer(SpriteGetter materialSet,
                                         SpriteId material, Identifier texture, boolean isCore) {
        this.materialSet = materialSet;
        this.material = material;
        this.texture = texture;
        modelPart = isCore ?
                TryModel.createCoreLayer().bakeRoot().getChild("All")
                : TryModel.createBodyLayer().bakeRoot().getChild("All");
        // modelPart2 = TryModel.createCoreLayer().bakeRoot().getChild("All");
        this.isCore = isCore;
    }

    @Nullable
    public Integer extractArgument(ItemStack stack) {
        // Extract the data to be used
        return getLightFromItem(stack);
    }


    @Override
    public void submit(Integer argument, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        poseStack.pushPose();
        translatedPose(ItemDisplayContext.GUI, poseStack);
        renderModel(ItemDisplayContext.GUI, poseStack, lightCoords, submitNodeCollector);
        poseStack.popPose();
    }

    public void translatedPose(ItemDisplayContext displayContext, PoseStack poseStack) {
        if (displayContext == ItemDisplayContext.GUI) {
            poseStack.translate(0, 0.375F, 0F);
        } else if (displayContext == ItemDisplayContext.GROUND) {
        } else if (displayContext == ItemDisplayContext.FIXED) {
            poseStack.translate(0, 0.5F, -0.25f);
        } else if (displayContext == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
        } else if (displayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
        } else if (displayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
            poseStack.translate(0.75, 0, 0);
        } else if (displayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            poseStack.translate(0.75, 0, 0);
        }
    }

    protected void renderModel(ItemDisplayContext displayContext, PoseStack poseStack, int lightCoords, SubmitNodeCollector collector) {
        poseStack.pushPose();
        modelPart.getAllParts().forEach(ModelPart::resetPose);

        long seed = 0;
        long time = (Minecraft.getInstance().level.getGameTime()) % 50L;
        long renderTicks = (time + 0) * 40;
        renderTicks = renderTicks * 3000 / 2000;

        // VertexConsumer vertexconsumer2 = getMaterialFromItem(stack).buffer(bufferIn, RenderType::itemEntityTranslucentCull);
        // MultiBufferSource.BufferSource bufferIn = Minecraft.getInstance().renderBuffers().bufferSource();
        // VertexConsumer vertexconsumer2 = bufferIn.getBuffer(RenderTypes.entityTranslucentCullItemTarget(texture));
        doAnimate(modelPart, renderTicks, displayContext);
        collector.submitModelPart(modelPart,poseStack,RenderTypes.entityTranslucentCullItemTarget(texture),lightCoords, OverlayTexture.NO_OVERLAY,null);
        // modelPart.render(poseStack, vertexconsumer2, lightCoords, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }


    @Override
    public void getExtents(@NonNull Consumer<Vector3fc> output) {

    }

    protected GreenHouseCoreBlock getGreenHouseCoreBlockFromItem(ItemStack stack) {
        if (stack.getItem() == ItemRegistry.summer_greenhouse_essence_item.get()) {
            return (GreenHouseCoreBlock) BlockRegistry.summer_greenhouse_core.get();
        } else if (stack.getItem() == ItemRegistry.winter_greenhouse_essence_item.get()) {
            return (GreenHouseCoreBlock) BlockRegistry.winter_greenhouse_core.get();
        } else if (stack.getItem() == ItemRegistry.autumn_greenhouse_essence_item.get()) {
            return (GreenHouseCoreBlock) BlockRegistry.autumn_greenhouse_core.get();
        }
        return (GreenHouseCoreBlock) BlockRegistry.spring_greenhouse_core.get();
    }

    protected int getLightFromItem(ItemStack stack) {
        if (stack.getItem() == ItemRegistry.greenhouse_core_container_item.get()) return LightCoordsUtil.FULL_BRIGHT;

        GreenHouseCoreBlock block =
                stack.getItem() instanceof GreenhouseEssenceItem ?
                        getGreenHouseCoreBlockFromItem(stack) :
                        (GreenHouseCoreBlock) ((BlockItem) stack.getItem()).getBlock();
        return TryModel.getLightFromBlock(block);
    }


    protected void doAnimate(ModelPart modelPart1, long renderTicks, ItemDisplayContext context) {
        if (isCore) {
            if (context == ItemDisplayContext.GROUND) {
                modelPart1.xScale = 1;
                modelPart1.yScale = 1;
                modelPart1.zScale = 1;
            } else {
                modelPart1.offsetScale(new Vector3f(.5f, .5f, .5f));
            }
            modelPart1.y += .25f;
            modelPart1.x += .75f;
        } else TryKeyframe.animate(modelPart1, TryModel.animation, renderTicks, 1, ANIMATION_VECTOR_CACHE);
    }

    public static Identifier getMaterialFromItemS(BlockItem item) {
        GreenHouseCoreBlock block = (GreenHouseCoreBlock) (item).getBlock();
        return TryModel.getMaterialFromBlock(block).sprite();
    }


    public record Unbaked(Identifier texture, boolean isCore) implements SpecialModelRenderer.Unbaked<Integer> {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(
                i -> i.group(
                                Identifier.CODEC.fieldOf("texture").forGetter(Unbaked::texture),
                                Codec.BOOL.optionalFieldOf("is_core", false).forGetter(Unbaked::isCore)
                        )
                        .apply(i, Unbaked::new)
        );

        @Override
        public SpecialModelRenderer<Integer> bake(BakingContext context) {
                return new GreenHouseCoreSpecialRenderer(
                        context.sprites(), Sheets.BLOCKS_MAPPER.apply(texture)
                        , texture, isCore);
        }

        @Override
        public @NonNull MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        // @Override
        // public @org.jspecify.annotations.Nullable SpecialModelRenderer<GreenHouseCoreSpecialRenderer> bake(BakingContext ctx) {
        //     // Resolve resource location to absolute path
        //     // Identifier textureLoc = this.texture.withPath(path -> "textures/entity/" + path + ".png");
        //
        //     // Get the model and the material to render
        //     return new GreenHouseCoreSpecialRenderer(
        //             ctx.sprites(), Sheets.BLOCKS_MAPPER.apply(texture)
        //             , texture, isCore);
        //     return
        // }
    }
}



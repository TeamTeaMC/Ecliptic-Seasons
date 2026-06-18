//package com.teamtea.eclipticseasons.client.render.ber;
//
//import com.mojang.blaze3d.vertex.PoseStack;
//import com.mojang.blaze3d.vertex.VertexConsumer;
//import com.teamtea.eclipticseasons.EclipticSeasons;
//import com.teamtea.eclipticseasons.client.model.entity.TryAnimation;
//import com.teamtea.eclipticseasons.client.model.entity.TryKeyframe;
//import com.teamtea.eclipticseasons.common.block.base.SimpleHorizontalEntityBlock;
//import com.teamtea.eclipticseasons.common.block.blockentity.WindChimesBlockEntity;
//import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
//import net.minecraft.client.animation.AnimationDefinition;
//import net.minecraft.client.model.geom.ModelPart;
//import net.minecraft.client.model.geom.PartPose;
//import net.minecraft.client.model.geom.builders.*;
//
//import net.minecraft.client.renderer.RenderType;
//import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
//import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
//import net.minecraft.client.resources.model.Material;
//import net.minecraft.world.inventory.InventoryMenu;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.block.state.BlockState;
//import org.joml.Vector3f;
//
//
//public class WindChimesRenderer implements BlockEntityRenderer<WindChimesBlockEntity> {
//    public static final Material WINDRING_RESOURCE_LOCATION = new Material(
//            InventoryMenu.BLOCK_ATLAS, EclipticSeasons.rl("block/windring_entity")
//    );
//    public static final Material WINDRING_RESOURCE_LOCATION_BAMBOO = new Material(
//            InventoryMenu.BLOCK_ATLAS, EclipticSeasons.rl("block/windring_bamboo_entity")
//    );
//    public static final Material WINDRING_RESOURCE_LOCATION_PAPER = new Material(
//            InventoryMenu.BLOCK_ATLAS, EclipticSeasons.rl("block/windring_paper_entity")
//    );
//
//    private ModelPart windringModelPart;
//    private ModelPart windringModelPartPaper;
//    private ModelPart windringModelPartBamboo;
//
//    private static final Vector3f ANIMATION_VECTOR_CACHE = new Vector3f();
//
//    public WindChimesRenderer(BlockEntityRendererProvider.Context pContext) {
//        windringModelPart = createBodyLayer().bakeRoot();
//        windringModelPartPaper = createBodyLayer_Paper().bakeRoot();
//        windringModelPartBamboo = createBodyLayer_Bamboo().bakeRoot();
//    }
//
//
//    public static LayerDefinition createBodyLayer() {
//        MeshDefinition meshdefinition = new MeshDefinition();
//        PartDefinition partdefinition = meshdefinition.getRoot();
//
//        PartDefinition top = partdefinition.addOrReplaceChild("top", CubeListBuilder.create().texOffs(36, 32).addBox(-0.95F, -1.75F, -1.05F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.45F, 15.25F, 8.45F, 0.0F, 0.0F, -3.1416F));
//        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(36, 0).addBox(-4.4898F, -6.0F, 3.507F, 9.0F, 16.0F, 0.0F, new CubeDeformation(0.0F))
//                .texOffs(36, 16).addBox(-4.4898F, -6.0F, -3.493F, 9.0F, 16.0F, 0.0F, new CubeDeformation(0.0F))
//                .texOffs(0, 10).addBox(3.5102F, -6.0F, -4.493F, 0.0F, 16.0F, 9.0F, new CubeDeformation(0.0F))
//                .texOffs(18, 10).addBox(-3.4898F, -6.0F, -4.493F, 0.0F, 16.0F, 9.0F, new CubeDeformation(0.0F))
//                .texOffs(36, 36).addBox(-0.8898F, 3.0F, -1.093F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
//                .texOffs(0, 0).addBox(-4.6102F, -8.1F, -4.493F, 9.0F, 1.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.5102F, 7.0F, 8.493F, 0.0F, 0.0F, -3.1416F));
//
//        PartDefinition cube_r1 = bone.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(18, 35).addBox(2.0F, -11.0F, 1.9F, 0.0F, 16.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0102F, 4.0F, -5.993F, 0.0F, -0.7854F, 0.0F));
//
//        PartDefinition cube_r2 = bone.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 35).addBox(2.0F, -11.0F, 1.9F, 0.0F, 16.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.9898F, 4.0F, -2.993F, 0.0F, 0.7854F, 0.0F));
//
//        return LayerDefinition.create(meshdefinition, 64, 64);
//    }
//
//    public static LayerDefinition createBodyLayer_Paper() {
//        MeshDefinition meshdefinition = new MeshDefinition();
//        PartDefinition partdefinition = meshdefinition.getRoot();
//
//        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(16, 16).addBox(-1.5F, 14.0F, 0.0F, 3.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
//                .texOffs(22, 16).addBox(-1.5F, -1.0F, 0.0F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, 15.0F, 8.0F, 0.0F, 0.0F, -3.1416F));
//
//        PartDefinition bone2 = partdefinition.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(16, 21).addBox(-1.5F, 8.0F, 0.0F, 3.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
//                .texOffs(0, 16).addBox(-2.0F, 9.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, 15.0F, 8.0F, 0.0F, 0.0F, -3.1416F));
//
//        PartDefinition bone3 = partdefinition.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, 15.0F, 8.0F, 0.0F, 0.0F, -3.1416F));
//
//        PartDefinition top = partdefinition.addOrReplaceChild("top", CubeListBuilder.create().texOffs(22, 16).addBox(-1.5F, -1.0F, 0.0F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, 15.0F, 8.0F, 0.0F, 0.0F, -3.1416F));
//
//        return LayerDefinition.create(meshdefinition, 32, 32);
//    }
//
//    public static LayerDefinition createBodyLayer_Bamboo() {
//        MeshDefinition meshdefinition = new MeshDefinition();
//        PartDefinition partdefinition = meshdefinition.getRoot();
//
//        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(20, 10).addBox(0.3666F, 3.0F, -2.2F, 0.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.2667F, 7.0F, 7.6F, 0.0F, -1.5708F, -3.1416F));
//
//        PartDefinition bone2 = partdefinition.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(0, 10).addBox(-2.2333F, -7.0F, -2.1F, 5.0F, 11.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.2667F, 7.0F, 7.6F, 0.0F, -1.5708F, -3.1416F));
//
//        PartDefinition top = partdefinition.addOrReplaceChild("top", CubeListBuilder.create().texOffs(0, -9).addBox(0.3666F, -11.0F, -6.2F, 0.0F, 8.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.2667F, 7.0F, 7.6F, 0.0F, -1.5708F, -3.1416F));
//
//        return LayerDefinition.create(meshdefinition, 32, 32);
//    }
//
//    @Override
//    public void render(WindChimesBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLight, int combinedOverlay) {
//        // modelPart=BellRenderer.createBodyLayer().bakeRoot();
//        // modelPart = saa.createBodyLayer().bakeRoot();
//
//        ModelPart useModel = windringModelPart;
//        Material material = WINDRING_RESOURCE_LOCATION;
//        AnimationDefinition animationDefinition = TryAnimation.animation;
//        BlockState blockState = blockEntity.getBlockState();
//        Block block = blockState.getBlock();
//        float size = 0.075f;
//        if (block == BlockRegistry.bamboo_wind_chimes.get()) {
//            useModel = windringModelPartBamboo;
//            material = WINDRING_RESOURCE_LOCATION_BAMBOO;
//            size = 0.35f;
//            animationDefinition = TryAnimation.animation_bamboo;
//        } else if (block == BlockRegistry.paper_wind_chimes.get()) {
//            useModel = windringModelPartPaper;
//            material = WINDRING_RESOURCE_LOCATION_PAPER;
//            size = 0.35f;
//            animationDefinition = TryAnimation.animation_paper;
//        }
//
//        if (blockEntity.isShaking())
//            size += 0.75f;
//
//        poseStack.pushPose();
//
//        poseStack.translate(0.5f, 0, 0.5f);
//        poseStack.mulPose(XYZ.deg_to_rad(0, SimpleHorizontalEntityBlock.getRotateYByFacing(blockEntity.getBlockState()), 0));
//        poseStack.translate(-0.5f, 0, -0.5f);
//
//        useModel.getAllParts().forEach(ModelPart::resetPose);
//
//        long seed = blockState.getSeed(blockEntity.getBlockPos());
//        long time = (blockEntity.getLevel().getGameTime() + Math.abs(seed)) % 50L;
//        if (time == 0 && blockEntity.isShaking()) {
//            if (blockEntity.getLevel().getRandom().nextBoolean()) {
//                blockEntity.setShaking(false);
//            } else {
//                size -= 0.5f;
//            }
//        }
//        size += 0.001f * (seed % 50);
//
//        long renderTicks = (long) ((time + partialTicks) * 40);
//        TryKeyframe.animate(useModel, animationDefinition, renderTicks, size, ANIMATION_VECTOR_CACHE);
//
//        VertexConsumer vertexconsumer = material.buffer(bufferIn, RenderType::entityCutout);
//        useModel.render(poseStack, vertexconsumer, combinedLight, combinedOverlay);
//
//        poseStack.popPose();
//    }
//}

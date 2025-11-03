package com.teamtea.eclipticseasons.client.render;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.common.block.GreenHouseCoreBlock;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.inventory.InventoryMenu;

public class TryModel {
    public static final Material greenhouse_core_container = new Material(
            InventoryMenu.BLOCK_ATLAS, EclipticSeasons.rl("block/greenhouse_core_container")
    );

    public static final Material green_house_core_spring = new Material(
            InventoryMenu.BLOCK_ATLAS, EclipticSeasons.rl("block/green_house_core_spring")
    );

    public static final Material green_house_core_summer = new Material(
            InventoryMenu.BLOCK_ATLAS, EclipticSeasons.rl("block/green_house_core_summer")
    );
    public static final Material green_house_core_autumn = new Material(
            InventoryMenu.BLOCK_ATLAS, EclipticSeasons.rl("block/green_house_core_autumn")
    );
    public static final Material green_house_core_winter = new Material(
            InventoryMenu.BLOCK_ATLAS, EclipticSeasons.rl("block/green_house_core_winter")
    );

    public static Material getMaterialFromBlock(GreenHouseCoreBlock block) {
        return getMaterialFromBlock(block, GreenHouseCoreBlock.MAX_STAGE);
    }

    public static Material getMaterialFromBlock(GreenHouseCoreBlock block, int stage) {
        if (stage == 0) return greenhouse_core_container;
        switch (block.getSeason()) {
            case SUMMER -> {
                return green_house_core_summer;
            }
            case AUTUMN -> {
                return green_house_core_autumn;
            }
            case WINTER -> {
                return green_house_core_winter;
            }
            default -> {
                return green_house_core_spring;
            }
        }
    }

    public static int getLightFromBlock(GreenHouseCoreBlock block) {
        return getLightFromBlock(block, GreenHouseCoreBlock.MAX_STAGE);
    }

    public static int getLightFromBlock(GreenHouseCoreBlock block, int stage) {
        if (stage == 0) return LightTexture.FULL_SKY;
        float cut = 3f / stage;

        switch (block.getSeason()) {
            case SUMMER -> {
                return LightTexture.pack((int) (15 / cut), 15);
            }
            case WINTER -> {
                return LightTexture.pack((int) (9 / cut), 15);
            }
            default -> {
                return LightTexture.pack((int) (12 / cut), 15);
            }
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition All = partdefinition.addOrReplaceChild("All", CubeListBuilder.create(), PartPose.offsetAndRotation(7.0F, 1.0F, 8.0F, 0.0F, 0.0F, -3.1416F));

        PartDefinition A1 = All.addOrReplaceChild("A1", CubeListBuilder.create(), PartPose.offset(1.0F, 0.0F, -1.0F));

        PartDefinition cube_r1 = A1.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(32, 11).addBox(-9.0F, 0.0F, 0.0F, 18.0F, 11.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(16, 38).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, 0.0F, -7.0F, 0.3054F, -0.7854F, 0.0F));

        PartDefinition cube_r2 = A1.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(32, 0).addBox(-9.0F, -11.0F, 0.0F, 18.0F, 11.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(24, 38).addBox(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, 0.0F, -7.0F, -0.3054F, -0.7854F, 0.0F));

        PartDefinition cube_r3 = A1.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -10.5F, -8.0F, 0.0F, 22.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(32, 45).addBox(-1.5F, -0.5F, -1.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, -1.0F, -7.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition cube_r4 = A1.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(32, 36).addBox(-0.5F, 0.0F, -4.5F, 1.0F, 0.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.4038F, -7.0F, -2.4038F, 0.0873F, -0.7854F, 0.0F));

        PartDefinition A2 = All.addOrReplaceChild("A2", CubeListBuilder.create(), PartPose.offset(-1.0F, 0.0F, -1.0F));

        PartDefinition cube_r5 = A2.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(32, 11).mirror().addBox(-9.0F, 0.0F, 0.0F, 18.0F, 11.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(16, 38).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-7.0F, 0.0F, -7.0F, 0.3054F, 0.7854F, 0.0F));

        PartDefinition cube_r6 = A2.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(32, 0).mirror().addBox(-9.0F, -11.0F, 0.0F, 18.0F, 11.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(24, 38).mirror().addBox(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-7.0F, 0.0F, -7.0F, -0.3054F, 0.7854F, 0.0F));

        PartDefinition cube_r7 = A2.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0.0F, -10.5F, -8.0F, 0.0F, 22.0F, 16.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(32, 45).mirror().addBox(-1.5F, -0.5F, -1.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-7.0F, -1.0F, -7.0F, 0.0F, 0.7854F, 0.0F));

        PartDefinition cube_r8 = A2.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(32, 36).mirror().addBox(-0.5F, 0.0F, -4.5F, 1.0F, 0.0F, 9.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-2.4038F, -7.0F, -2.4038F, 0.0873F, 0.7854F, 0.0F));

        PartDefinition A3 = All.addOrReplaceChild("A3", CubeListBuilder.create(), PartPose.offset(-1.0F, 0.0F, 1.0F));

        PartDefinition cube_r9 = A3.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(32, 11).mirror().addBox(-9.0F, 0.0F, 0.0F, 18.0F, 11.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(16, 38).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-7.0F, 0.0F, 7.0F, -0.3054F, -0.7854F, 0.0F));

        PartDefinition cube_r10 = A3.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(32, 0).mirror().addBox(-9.0F, -11.0F, 0.0F, 18.0F, 11.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(24, 38).mirror().addBox(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-7.0F, 0.0F, 7.0F, 0.3054F, -0.7854F, 0.0F));

        PartDefinition cube_r11 = A3.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0.0F, -10.5F, -8.0F, 0.0F, 22.0F, 16.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-7.0F, -1.0F, 7.0F, 0.0F, 2.3562F, 0.0F));

        PartDefinition cube_r12 = A3.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(32, 36).mirror().addBox(-0.5F, 0.0F, -4.5F, 1.0F, 0.0F, 9.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-2.4038F, -7.0F, 2.4038F, -0.0873F, -0.7854F, 0.0F));

        PartDefinition cube_r13 = A3.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(32, 45).mirror().addBox(-1.5F, -0.5F, -2.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-7.0F, -1.0F, 7.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition A4 = All.addOrReplaceChild("A4", CubeListBuilder.create(), PartPose.offset(1.0F, 0.0F, 1.0F));

        PartDefinition cube_r14 = A4.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(32, 11).addBox(-9.0F, 0.0F, 0.0F, 18.0F, 11.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(16, 38).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, 0.0F, 7.0F, -0.3054F, 0.7854F, 0.0F));

        PartDefinition cube_r15 = A4.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(32, 0).addBox(-9.0F, -11.0F, 0.0F, 18.0F, 11.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(24, 38).addBox(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, 0.0F, 7.0F, 0.3054F, 0.7854F, 0.0F));

        PartDefinition cube_r16 = A4.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -10.5F, -8.0F, 0.0F, 22.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, -1.0F, 7.0F, 0.0F, -2.3562F, 0.0F));

        PartDefinition cube_r17 = A4.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(32, 36).addBox(-0.5F, 0.0F, -4.5F, 1.0F, 0.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.4038F, -7.0F, 2.4038F, -0.0873F, 0.7854F, 0.0F));

        PartDefinition cube_r18 = A4.addOrReplaceChild("cube_r18", CubeListBuilder.create().texOffs(32, 45).addBox(-1.5F, -0.5F, -2.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, -1.0F, 7.0F, 0.0F, 0.7854F, 0.0F));

        PartDefinition B1 = All.addOrReplaceChild("B1", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r19 = B1.addOrReplaceChild("cube_r19", CubeListBuilder.create().texOffs(32, 29).addBox(-8.0F, 0.0F, 0.0F, 16.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, 0.2182F, 0.0F, 0.0F));

        PartDefinition cube_r20 = B1.addOrReplaceChild("cube_r20", CubeListBuilder.create().texOffs(32, 22).addBox(-8.0F, -7.0F, 0.0F, 16.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition C = All.addOrReplaceChild("C", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r21 = C.addOrReplaceChild("cube_r21", CubeListBuilder.create().texOffs(0, 38).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 46).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.0771F, -0.5111F, -0.6834F));

        PartDefinition B2 = All.addOrReplaceChild("B2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition cube_r22 = B2.addOrReplaceChild("cube_r22", CubeListBuilder.create().texOffs(32, 29).addBox(-8.0F, 0.0F, 0.0F, 16.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, 0.2182F, 0.0F, 0.0F));

        PartDefinition cube_r23 = B2.addOrReplaceChild("cube_r23", CubeListBuilder.create().texOffs(32, 22).addBox(-8.0F, -7.0F, 0.0F, 16.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition B3 = All.addOrReplaceChild("B3", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition cube_r24 = B3.addOrReplaceChild("cube_r24", CubeListBuilder.create().texOffs(32, 29).mirror().addBox(-8.0F, 0.0F, 0.0F, 16.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, 0.2182F, 0.0F, 0.0F));

        PartDefinition cube_r25 = B3.addOrReplaceChild("cube_r25", CubeListBuilder.create().texOffs(32, 22).mirror().addBox(-8.0F, -7.0F, 0.0F, 16.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition B4 = All.addOrReplaceChild("B4", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r26 = B4.addOrReplaceChild("cube_r26", CubeListBuilder.create().texOffs(32, 29).addBox(-8.0F, 0.0F, 0.0F, 16.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 8.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition cube_r27 = B4.addOrReplaceChild("cube_r27", CubeListBuilder.create().texOffs(32, 22).addBox(-8.0F, -7.0F, 0.0F, 16.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 8.0F, 0.2182F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    public static LayerDefinition createBodyLayer_age1() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition All = partdefinition.addOrReplaceChild("All", CubeListBuilder.create(), PartPose.offsetAndRotation(7.0F, 1.0F, 8.0F, 0.0F, 0.0F, -3.1416F));

        PartDefinition A1 = All.addOrReplaceChild("A1", CubeListBuilder.create(), PartPose.offset(1.0F, 0.0F, -1.0F));

        PartDefinition cube_r1 = A1.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(32, 36).addBox(-0.5F, 0.0F, -4.5F, 1.0F, 0.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.4038F, -7.0F, -2.4038F, 0.0873F, -0.7854F, 0.0F));

        PartDefinition cube_r2 = A1.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(32, 45).addBox(-1.5F, -0.5F, -1.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, -1.0F, -7.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition cube_r3 = A1.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(24, 38).addBox(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, 0.0F, -7.0F, -0.3054F, -0.7854F, 0.0F));

        PartDefinition cube_r4 = A1.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(16, 38).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, 0.0F, -7.0F, 0.3054F, -0.7854F, 0.0F));

        PartDefinition A2 = All.addOrReplaceChild("A2", CubeListBuilder.create(), PartPose.offset(-1.0F, 0.0F, -1.0F));

        PartDefinition cube_r5 = A2.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(32, 36).mirror().addBox(-0.5F, 0.0F, -4.5F, 1.0F, 0.0F, 9.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-2.4038F, -7.0F, -2.4038F, 0.0873F, 0.7854F, 0.0F));

        PartDefinition cube_r6 = A2.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(32, 45).mirror().addBox(-1.5F, -0.5F, -1.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-7.0F, -1.0F, -7.0F, 0.0F, 0.7854F, 0.0F));

        PartDefinition cube_r7 = A2.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(24, 38).mirror().addBox(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-7.0F, 0.0F, -7.0F, -0.3054F, 0.7854F, 0.0F));

        PartDefinition cube_r8 = A2.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(16, 38).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-7.0F, 0.0F, -7.0F, 0.3054F, 0.7854F, 0.0F));

        PartDefinition A3 = All.addOrReplaceChild("A3", CubeListBuilder.create(), PartPose.offset(-1.0F, 0.0F, 1.0F));

        PartDefinition cube_r9 = A3.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(32, 36).mirror().addBox(-0.5F, 0.0F, -4.5F, 1.0F, 0.0F, 9.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-2.4038F, -7.0F, 2.4038F, -0.0873F, -0.7854F, 0.0F));

        PartDefinition cube_r10 = A3.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(32, 45).mirror().addBox(-1.5F, -0.5F, -2.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-7.0F, -1.0F, 7.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition cube_r11 = A3.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(24, 38).mirror().addBox(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-7.0F, 0.0F, 7.0F, 0.3054F, -0.7854F, 0.0F));

        PartDefinition cube_r12 = A3.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(16, 38).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-7.0F, 0.0F, 7.0F, -0.3054F, -0.7854F, 0.0F));

        PartDefinition A4 = All.addOrReplaceChild("A4", CubeListBuilder.create(), PartPose.offset(1.0F, 0.0F, 1.0F));

        PartDefinition cube_r13 = A4.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(32, 36).addBox(-0.5F, 0.0F, -4.5F, 1.0F, 0.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.4038F, -7.0F, 2.4038F, -0.0873F, 0.7854F, 0.0F));

        PartDefinition cube_r14 = A4.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(32, 45).addBox(-1.5F, -0.5F, -2.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, -1.0F, 7.0F, 0.0F, 0.7854F, 0.0F));

        PartDefinition cube_r15 = A4.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(24, 38).addBox(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, 0.0F, 7.0F, 0.3054F, 0.7854F, 0.0F));

        PartDefinition cube_r16 = A4.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(16, 38).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, 0.0F, 7.0F, -0.3054F, 0.7854F, 0.0F));

        PartDefinition B1 = All.addOrReplaceChild("B1", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r17 = B1.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(32, 29).addBox(-8.0F, 0.0F, 0.0F, 16.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, 0.2182F, 0.0F, 0.0F));

        PartDefinition cube_r18 = B1.addOrReplaceChild("cube_r18", CubeListBuilder.create().texOffs(32, 22).addBox(-8.0F, -7.0F, 0.0F, 16.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition C = All.addOrReplaceChild("C", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r19 = C.addOrReplaceChild("cube_r19", CubeListBuilder.create().texOffs(0, 38).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 46).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.0771F, -0.5111F, -0.6834F));

        PartDefinition B2 = All.addOrReplaceChild("B2", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition cube_r20 = B2.addOrReplaceChild("cube_r20", CubeListBuilder.create().texOffs(32, 29).addBox(-8.0F, 0.0F, 0.0F, 16.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, 0.2182F, 0.0F, 0.0F));

        PartDefinition cube_r21 = B2.addOrReplaceChild("cube_r21", CubeListBuilder.create().texOffs(32, 22).addBox(-8.0F, -7.0F, 0.0F, 16.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition B3 = All.addOrReplaceChild("B3", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition cube_r22 = B3.addOrReplaceChild("cube_r22", CubeListBuilder.create().texOffs(32, 29).mirror().addBox(-8.0F, 0.0F, 0.0F, 16.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, 0.2182F, 0.0F, 0.0F));

        PartDefinition cube_r23 = B3.addOrReplaceChild("cube_r23", CubeListBuilder.create().texOffs(32, 22).mirror().addBox(-8.0F, -7.0F, 0.0F, 16.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition B4 = All.addOrReplaceChild("B4", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r24 = B4.addOrReplaceChild("cube_r24", CubeListBuilder.create().texOffs(32, 29).addBox(-8.0F, 0.0F, 0.0F, 16.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 8.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition cube_r25 = B4.addOrReplaceChild("cube_r25", CubeListBuilder.create().texOffs(32, 22).addBox(-8.0F, -7.0F, 0.0F, 16.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 8.0F, 0.2182F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    public static LayerDefinition createCoreLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition All = partdefinition.addOrReplaceChild("All", CubeListBuilder.create(), PartPose.offsetAndRotation(7.0F, 1.0F, 8.0F, 0.0F, 0.0F, -3.1416F));

        PartDefinition C = All.addOrReplaceChild("C", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r1 = C.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 38).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 46).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.0771F, -0.5111F, -0.6834F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    public static final AnimationDefinition animation = AnimationDefinition.Builder.withLength(3.0F).looping()
            .addAnimation("C", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.625F, KeyframeAnimations.degreeVec(-23.0579F, -19.7426F, -38.4343F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.375F, KeyframeAnimations.degreeVec(0.0F, 60.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.0F, KeyframeAnimations.degreeVec(-32.7597F, 5.0875F, -16.7336F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .build();
}

package com.teamtea.eclipticseasons.client.util;

import com.teamtea.eclipticseasons.client.render.WorldRenderer;
import com.teamtea.eclipticseasons.client.sound.WindChimeSoundInstance;
import com.teamtea.eclipticseasons.common.block.WindChimesBlock;
import com.teamtea.eclipticseasons.common.block.blockentity.WindChimesBlockEntity;
import com.teamtea.eclipticseasons.common.misc.ClientAgent;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.SectionPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.HitResult;

public class ClientClientAgent implements ClientAgent {

    @Override
    public void loadWindChime(WindChimesBlockEntity windChimesBlockEntity) {
        if (windChimesBlockEntity.getLevel() == ClientCon.getUseLevel() && ClientCon.getUseLevel() != null) {
            Minecraft.getInstance().getSoundManager().queueTickingSound(new WindChimeSoundInstance(
                    windChimesBlockEntity, WindChimesBlock.getSoundEvent(windChimesBlockEntity.getBlockState().getBlock()), SoundSource.BLOCKS, windChimesBlockEntity.getLevel().getRandom()
            ));
        }
    }

    @Override
    public Entity getCameraEntity() {
        return Minecraft.getInstance().getCameraEntity();
    }

    @Override
    public HitResult getHitResult() {
        return Minecraft.getInstance().hitResult;
    }

    @Override
    public void setChunkDirty(SectionPos sectionPos) {
        WorldRenderer.setSectionDirtyWithNeighbors(sectionPos);
    }

    @Getter
    @Setter
    boolean change = false;


    @Override
    public String getCurrentWorldName() {
        IntegratedServer singleplayerServer = Minecraft.getInstance().getSingleplayerServer();
        if (singleplayerServer == null) return "world";
        return singleplayerServer.getWorldPath(LevelResource.ROOT).getParent().getFileName().toString();
    }
}

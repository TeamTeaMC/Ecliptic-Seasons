package com.teamtea.eclipticseasons.client.util;

import com.teamtea.eclipticseasons.client.sound.WindChimeSoundInstance;
import com.teamtea.eclipticseasons.common.block.WindChimesBlock;
import com.teamtea.eclipticseasons.common.block.blockentity.WindChimesBlockEntity;
import com.teamtea.eclipticseasons.common.misc.SoundUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;

public class ClientSoundUtil implements SoundUtil {
    @Override
    public void set(WindChimesBlockEntity windChimesBlockEntity) {
        if (windChimesBlockEntity.getLevel() == ClientCon.getUseLevel() && ClientCon.getUseLevel() != null) {
            Minecraft.getInstance().getSoundManager().queueTickingSound(new WindChimeSoundInstance(
                    windChimesBlockEntity, WindChimesBlock.getSoundEvent(windChimesBlockEntity.getBlockState().getBlock()), SoundSource.BLOCKS, windChimesBlockEntity.getLevel().getRandom()
            ));
        }
    }
}

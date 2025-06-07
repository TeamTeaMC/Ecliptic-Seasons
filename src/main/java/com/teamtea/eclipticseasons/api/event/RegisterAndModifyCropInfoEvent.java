package com.teamtea.eclipticseasons.api.event;

import com.teamtea.eclipticseasons.api.constant.crop.CropHumidityInfo;
import com.teamtea.eclipticseasons.api.constant.crop.CropHumidityType;
import com.teamtea.eclipticseasons.api.constant.crop.CropSeasonInfo;
import com.teamtea.eclipticseasons.api.constant.crop.CropSeasonType;
import com.teamtea.eclipticseasons.common.core.crop.CropInfoManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Map;


/**
 * <s>Fired on the {@link net.neoforged.neoforge.common.NeoForge#EVENT_BUS}.</s>
 *
 * @deprecated This event is no longer supported. See
 * {@link com.teamtea.eclipticseasons.common.registry.ESRegistries#CROP} for datapack registration.
 */

@Deprecated(since = "0.11")
public class RegisterAndModifyCropInfoEvent extends net.neoforged.bus.api.Event implements IESEvent {


    private final Map<Block, CropHumidityInfo> cropHumidityInfoMap;
    private final Map<Block, CropSeasonInfo> cropSeasonInfoMap;

    public RegisterAndModifyCropInfoEvent(Map<Block, CropHumidityInfo> cropHumidityInfoMap, Map<Block, CropSeasonInfo> cropSeasonInfoMap) {
        this.cropHumidityInfoMap = cropHumidityInfoMap;
        this.cropSeasonInfoMap = cropSeasonInfoMap;
    }


    public void registerCropHumidityInfo(Item item, CropHumidityType info) {
        CropInfoManager.registerCropHumidityInfo(item, info);
    }

    public void registerCropHumidityInfo(Block block, CropHumidityType info, boolean force) {

        CropInfoManager.registerCropHumidityInfo(block, info, force);
    }

    public void registerCropSeasonInfo(Item item, CropSeasonType info) {
        CropInfoManager.registerCropSeasonInfo(item, info);
    }

    public void registerCropSeasonInfo(Block block, CropSeasonType info, boolean force) {
        CropInfoManager.registerCropSeasonInfo(block, info, force);
    }


    public Map<Block, CropHumidityInfo> getCropHumidityInfoMap() {
        return cropHumidityInfoMap;
    }

    public Map<Block, CropSeasonInfo> getCropSeasonInfoMap() {
        return cropSeasonInfoMap;
    }
}

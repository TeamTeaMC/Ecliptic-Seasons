// package com.teamtea.eclipticseasons.config.update;
//
// import com.electronwill.nightconfig.core.file.CommentedFileConfig;
// import com.teamtea.eclipticseasons.api.constant.simulation.SeasonalSimulationLevel;
// import com.teamtea.eclipticseasons.config.CommonConfig;
// import com.teamtea.eclipticseasons.config.sync.SyncType;
//
// import java.util.List;
//
// public class SimulationUpdater {
//
//     public static boolean initialCheck(SeasonalSimulationLevel current, SyncType syncType, CommentedFileConfig config) {
//         if (syncType != SyncType.COMMON || current == SeasonalSimulationLevel.CUSTOM) return false;
//
//         boolean changed = false;
//
//         changed |= setIfChanged(config,
//                 "Crop.EnableSeasonalCrop",
//                 current.enable(SeasonalSimulationLevel.AGRICULTURE));
//
//         changed |= setIfChanged(config,"Crop.EnableSeasonalCrop",
//                 current.enable(SeasonalSimulationLevel.AGRICULTURE));
//
//         changed |= setIfChanged(config,"Temperature.HeatStroke",
//                 current.enable(SeasonalSimulationLevel.SURVIVAL));
//
//         changed |= setIfChanged(config,"Animal.EnableSeasonalBee",
//                 current.enable(SeasonalSimulationLevel.SURVIVAL));
//
//         changed |= setIfChanged(config,"Animal.EnableSeasonalBreed",
//                 current.enable(SeasonalSimulationLevel.SURVIVAL));
//
//         changed |= setIfChanged(config,"Animal.EnableTimeBreed",
//                 current.enable(SeasonalSimulationLevel.SURVIVAL));
//
//         changed |= setIfChanged(config,"Animal.EnableSeasonalFishing",
//                 current.enable(SeasonalSimulationLevel.SURVIVAL));
//
//         return changed;
//     }
//
//     private static boolean setIfChanged(CommentedFileConfig config, String path, boolean value) {
//         Object old = config.get(path);
//         if (!(old instanceof Boolean) || (Boolean) old != value) {
//             config.set(path, value);
//             return true;
//         }
//         return false;
//     }
// }

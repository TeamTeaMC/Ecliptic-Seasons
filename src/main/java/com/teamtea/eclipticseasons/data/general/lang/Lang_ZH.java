package com.teamtea.eclipticseasons.data.general.lang;


import com.teamtea.eclipticseasons.common.registry.*;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;


public class Lang_ZH extends LangHelper {
    public Lang_ZH(PackOutput gen, ExistingFileHelper helper) {
        super(gen, helper, EclipticSeasonsApi.MODID, "zh_cn");
    }


    @Override
    protected void addTranslations() {
        addAdvancements();

        add("itemGroup." + EclipticSeasonsApi.MODID + ".core", "节气");

        add(BlockRegistry.calendar.get(), "日历");
        add("item.eclipticseasons.calendar.pop_hint", "%1$s，%2$s/%3$s");
        add(BlockRegistry.wind_chimes.get(), "风铃");
        add(BlockRegistry.paper_wind_chimes.get(), "纸风铃");
        add(BlockRegistry.bamboo_wind_chimes.get(), "竹风铃");
        add(BlockRegistry.pinwheel_blue.get(), "蓝色纸风车");
        add(BlockRegistry.pinwheel_lime.get(), "淡绿色纸风车");
        add(BlockRegistry.pinwheel_orange.get(), "橘色纸风车");
        add(ItemRegistry.ice_wand.get(), "冰晶法杖");
        add(ItemRegistry.broom.get(), "扫帚");

        add(ItemRegistry.thermometer.get(), "温度计");
        add(ItemRegistry.hyetometer.get(), "雨量计");
        add(BlockRegistry.hygrometer.get(), "湿度计");

        add(BlockRegistry.greenhouse_core_container.get(), "温室之心室");

        add(BlockRegistry.spring_greenhouse_core.get(), "春季温室之心");
        add(BlockRegistry.summer_greenhouse_core.get(), "夏季温室之心");
        add(BlockRegistry.autumn_greenhouse_core.get(), "秋季温室之心");
        add(BlockRegistry.winter_greenhouse_core.get(), "冬季温室之心");

        add(ItemRegistry.spring_greenhouse_essence_item.get(), "春季温室心髓");
        add(ItemRegistry.summer_greenhouse_essence_item.get(), "夏季温室心髓");
        add(ItemRegistry.autumn_greenhouse_essence_item.get(), "秋季温室心髓");
        add(ItemRegistry.winter_greenhouse_essence_item.get(), "冬季温室心髓");

        add(ItemRegistry.seasonal_prayer_scroll_item.get(), "祈年令");


        add(ItemRegistry.growth_detector.get(), "生长检测计");

        add(BlockRegistry.season_quest_wall_hanging_sign.get(), "季节任务告示");
        add(BlockRegistry.season_quest_ceiling_hanging_sign.get(), "季节任务告示");

        add(BlockRegistry.block_in_copper_grate_block.get(), "湿度调节铜格栅");
        add(BlockRegistry.block_in_exposed_copper_grate_block.get(), "斑驳的湿度调节铜格栅");
        add(BlockRegistry.block_in_weathered_copper_grate_block.get(), "锈蚀的湿度调节铜格栅");
        add(BlockRegistry.block_in_oxidized_copper_grate_block.get(), "氧化的湿度调节铜格栅");
        add(BlockRegistry.block_in_waxed_copper_grate_block.get(), "打蜡的湿度调节铜格栅");
        add(BlockRegistry.block_in_waxed_exposed_copper_grate_block.get(), "打蜡的斑驳湿度调节铜格栅");
        add(BlockRegistry.block_in_waxed_weathered_copper_grate_block.get(), "打蜡的锈蚀湿度调节铜格栅");
        add(BlockRegistry.block_in_waxed_oxidized_copper_grate_block.get(), "打蜡的氧化湿度调节铜格栅");

        add(BlockRegistry.block_in_wooden_grate_block.get(), "湿度调节木格栅");

        add(ItemRegistry.snowless_hometown.get(), "无雪的故乡");
        add(SongRegistry.toLangKey(SongRegistry.SNOWLESS_HOMETOWN), "北山薇 & 橙子苏打 等");

        add(BlockRegistry.snow_cauldron.get(),"装有雪的炼药锅");
        add(BlockRegistry.ice_cauldron.get(),"装有冰块的炼药锅");


        add("info.eclipticseasons.environment.temperature.under_freezing", "严寒");
        add("info.eclipticseasons.environment.temperature.freezing", "冰冻");
        add("info.eclipticseasons.environment.temperature.cold", "寒冷");
        add("info.eclipticseasons.environment.temperature.cool", "凉爽");
        add("info.eclipticseasons.environment.temperature.warm", "温暖");
        add("info.eclipticseasons.environment.temperature.hot", "炎热");
        add("info.eclipticseasons.environment.temperature.heat", "炙烤");
        add("info.eclipticseasons.environment.temperature.over_heat", "酷暑");

        add("info.eclipticseasons.environment.rainfall.rare", "罕见");
        add("info.eclipticseasons.environment.rainfall.scarce", "稀少");
        add("info.eclipticseasons.environment.rainfall.moderate", "中等");
        add("info.eclipticseasons.environment.rainfall.adequate", "足量");
        add("info.eclipticseasons.environment.rainfall.abundant", "丰富");

        add("info.eclipticseasons.environment.humidity.arid", "干旱");
        add("info.eclipticseasons.environment.humidity.dry", "干燥");
        add("info.eclipticseasons.environment.humidity.average", "一般");
        add("info.eclipticseasons.environment.humidity.moist", "湿润");
        add("info.eclipticseasons.environment.humidity.humid", "潮湿");
        add("info.eclipticseasons.environment.humidity", "适宜湿度： ");
        add("info.eclipticseasons.environment.season", "适宜季节： ");
        add("info.eclipticseasons.environment.season.feed", "繁殖季节： ");
        add("info.eclipticseasons.environment.season.spring", "春");
        add("info.eclipticseasons.environment.season.summer", "夏");
        add("info.eclipticseasons.environment.season.autumn", "秋");
        add("info.eclipticseasons.environment.season.winter", "冬");
        add("info.eclipticseasons.environment.season.none", "全年");
        add("info.eclipticseasons.environment.solar_term.beginning_of_spring", "立春");
        add("info.eclipticseasons.environment.solar_term.rain_water", "雨水");
        add("info.eclipticseasons.environment.solar_term.insects_awakening", "惊蛰");
        add("info.eclipticseasons.environment.solar_term.spring_equinox", "春分");
        add("info.eclipticseasons.environment.solar_term.fresh_green", "清明");
        add("info.eclipticseasons.environment.solar_term.grain_rain", "谷雨");
        add("info.eclipticseasons.environment.solar_term.beginning_of_summer", "立夏");
        add("info.eclipticseasons.environment.solar_term.lesser_fullness", "小满");
        add("info.eclipticseasons.environment.solar_term.grain_in_ear", "芒种");
        add("info.eclipticseasons.environment.solar_term.summer_solstice", "夏至");
        add("info.eclipticseasons.environment.solar_term.lesser_heat", "小暑");
        add("info.eclipticseasons.environment.solar_term.greater_heat", "大暑");
        add("info.eclipticseasons.environment.solar_term.beginning_of_autumn", "立秋");
        add("info.eclipticseasons.environment.solar_term.end_of_heat", "处暑");
        add("info.eclipticseasons.environment.solar_term.white_dew", "白露");
        add("info.eclipticseasons.environment.solar_term.autumnal_equinox", "秋分");
        add("info.eclipticseasons.environment.solar_term.cold_dew", "寒露");
        add("info.eclipticseasons.environment.solar_term.first_frost", "霜降");
        add("info.eclipticseasons.environment.solar_term.beginning_of_winter", "立冬");
        add("info.eclipticseasons.environment.solar_term.light_snow", "小雪");
        add("info.eclipticseasons.environment.solar_term.heavy_snow", "大雪");
        add("info.eclipticseasons.environment.solar_term.winter_solstice", "冬至");
        add("info.eclipticseasons.environment.solar_term.lesser_cold", "小寒");
        add("info.eclipticseasons.environment.solar_term.greater_cold", "大寒");
        add("info.eclipticseasons.environment.solar_term.none", "未知");
        add("info.eclipticseasons.environment.solar_term.message", "[节气提示] %s");
        add("info.eclipticseasons.environment.solar_term.alternation.beginning_of_spring", "春回大地，万物复苏。");
        add("info.eclipticseasons.environment.solar_term.alternation.rain_water", "细雨如酥，遥看草色。");
        add("info.eclipticseasons.environment.solar_term.alternation.insects_awakening", "虫声骚动，雷声渐起。");
        add("info.eclipticseasons.environment.solar_term.alternation.spring_equinox", "昼夜平分，暖意生。");
        add("info.eclipticseasons.environment.solar_term.alternation.fresh_green", "燕来新社，梨落清明。");
        add("info.eclipticseasons.environment.solar_term.alternation.grain_rain", "布谷鸟啼，雨润无声。");
        add("info.eclipticseasons.environment.solar_term.alternation.beginning_of_summer", "残春已去，炎暑将至。");
        add("info.eclipticseasons.environment.solar_term.alternation.lesser_fullness", "春粒渐满，夏果新熟。");
        add("info.eclipticseasons.environment.solar_term.alternation.grain_in_ear", "风吹麦浪，虫鸣夏忙。");
        add("info.eclipticseasons.environment.solar_term.alternation.summer_solstice", "绿树浓阴，白昼长。");
        add("info.eclipticseasons.environment.solar_term.alternation.lesser_heat", "盛夏之始，入伏天。");
        add("info.eclipticseasons.environment.solar_term.alternation.greater_heat", "日盛三伏，暑气熏。");
        add("info.eclipticseasons.environment.solar_term.alternation.beginning_of_autumn", "云天收夏色，木叶动秋声。");
        add("info.eclipticseasons.environment.solar_term.alternation.end_of_heat", "暑气渐止，秋雨至。");
        add("info.eclipticseasons.environment.solar_term.alternation.white_dew", "清风至，白露生。");
        add("info.eclipticseasons.environment.solar_term.alternation.autumnal_equinox", "昼渐短，夜渐长。");
        add("info.eclipticseasons.environment.solar_term.alternation.cold_dew", "秋意渐浓，寒气渐生。");
        add("info.eclipticseasons.environment.solar_term.alternation.first_frost", "凝露成霜，寒意愈盛。");
        add("info.eclipticseasons.environment.solar_term.alternation.beginning_of_winter", "北风悄潜，秋尽冬。");
        add("info.eclipticseasons.environment.solar_term.alternation.light_snow", "冬雨渐起，寒意浓。");
        add("info.eclipticseasons.environment.solar_term.alternation.heavy_snow", "雪起此时，千树梨花。");
        add("info.eclipticseasons.environment.solar_term.alternation.winter_solstice", "日影渐长，长夜漫漫。");
        add("info.eclipticseasons.environment.solar_term.alternation.lesser_cold", "数九寒天，冷在三九。");
        add("info.eclipticseasons.environment.solar_term.alternation.greater_cold", "大寒大寒，无风自寒。");
        add("info.eclipticseasons.environment.solar_term.alternation.none", "...");
        add("commands.eclipticseasons.solar.set", "已将节气天数设置为第%s天");
        add("effect.eclipticseasons.heat_stroke", "中暑");
        add("effect.eclipticseasons.heat_stroke.description", "酷热难耐，视线逐渐模糊。");


        add(AgroClimateRegistry.COLD, "寒冷地区");
        add(AgroClimateRegistry.TEMPERATE, "温暖地区");
        add(AgroClimateRegistry.HOT, "炎热地区");
        // add(AgroClimateRegistry.DESERT, "沙漠");
        add(AgroClimateRegistry.NETHER, "下界");
        add(AgroClimateRegistry.END, "末地");

        add(SeasonQuestRegistry.SPRING_CORE, "春季核心任务");
        add(SeasonQuestRegistry.SUMMER_CORE, "夏季核心任务");
        add(SeasonQuestRegistry.AUTUMN_CORE, "秋季核心任务");
        add(SeasonQuestRegistry.WINTER_CORE, "冬季核心任务");

        addGrowthDetector();
        addSeasonQuest();
        addConfigLang();
        addInfo();
        addJade();
        addTouhouLittleMaid();

        addExtraEnvInfo();
    }

    private void addExtraEnvInfo() {
        add("info.eclipticseasons.environment.solar_term.hint2", "第 %s 年:");
        add("info.eclipticseasons.environment.solar_term.hint3", "自 %s 日后:");
        add("info.eclipticseasons.environment.solar_term.hint", "今日节气:");

        add("info.eclipticseasons.environment.season_phase.hint", "今日时节:");

        add("info.eclipticseasons.environment.season_phase.dry_start", "干季");
        add("info.eclipticseasons.environment.season_phase.dry_middle", "干季");
        add("info.eclipticseasons.environment.season_phase.dry_end", "干季");
        add("info.eclipticseasons.environment.season_phase.alternation.dry_start", "清风无力，落日着山。");
        add("info.eclipticseasons.environment.season_phase.alternation.dry_middle", "青垄成尘，绿叶如枯。");
        add("info.eclipticseasons.environment.season_phase.alternation.dry_end", "江海竭，河汉干。");

        add("info.eclipticseasons.environment.season_phase.rain_start", "雨季");
        add("info.eclipticseasons.environment.season_phase.rain_middle", "雨季");
        add("info.eclipticseasons.environment.season_phase.rain_end", "雨季");
        add("info.eclipticseasons.environment.season_phase.alternation.rain_start", "雨潺潺，绿意生。");
        add("info.eclipticseasons.environment.season_phase.alternation.rain_middle", "雨泻长空，搜龙霹雳。");
        add("info.eclipticseasons.environment.season_phase.alternation.rain_end", "大雨如豆，溪聚成河。");

        add("info.eclipticseasons.environment.season_phase.wet_start", "湿季");
        add("info.eclipticseasons.environment.season_phase.wet_middle", "湿季");
        add("info.eclipticseasons.environment.season_phase.wet_end", "湿季");
        add("info.eclipticseasons.environment.season_phase.alternation.wet_start", "水汽沉沉，湿热难耐。");
        add("info.eclipticseasons.environment.season_phase.alternation.wet_middle", "天地如蒸，众木欣荣。");
        add("info.eclipticseasons.environment.season_phase.alternation.wet_end", "雨稀稀，风渐止。");

        add("info.eclipticseasons.environment.season_phase.pattern.dry_start", "%s (前)");
        add("info.eclipticseasons.environment.season_phase.pattern.dry_middle", "%s (中)");
        add("info.eclipticseasons.environment.season_phase.pattern.dry_end", "%s (后)");
        add("info.eclipticseasons.environment.season_phase.pattern.rain_start", "%s (前)");
        add("info.eclipticseasons.environment.season_phase.pattern.rain_middle", "%s (中)");
        add("info.eclipticseasons.environment.season_phase.pattern.rain_end", "%s (后)");
        add("info.eclipticseasons.environment.season_phase.pattern.wet_start", "%s (前)");
        add("info.eclipticseasons.environment.season_phase.pattern.wet_middle", "%s (中)");
        add("info.eclipticseasons.environment.season_phase.pattern.wet_end", "%s (后)");

        add("info.eclipticseasons.environment.season_phase.dry", "旱季");
        add("info.eclipticseasons.environment.season_phase.alternation.dry", "黄沙漫漫，不见滴雨。");
        add("info.eclipticseasons.environment.season_phase.pattern.dry", "%s (全年)");

        add("info.eclipticseasons.environment.season_phase.rain", "雨季");
        add("info.eclipticseasons.environment.season_phase.alternation.rain", "雨水连日，少有晴空。");
        add("info.eclipticseasons.environment.season_phase.pattern.rain", "%s (全年)");

        add("info.eclipticseasons.environment.season_phase.wet", "湿季");
        add("info.eclipticseasons.environment.season_phase.alternation.wet", "水汽沉沉，湿热难耐。");
        add("info.eclipticseasons.environment.season_phase.pattern.wet", "%s (全年)");


        add("info.eclipticseasons.environment.season_phase.cold_beginning_of_spring", "立春");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_beginning_of_spring", "旧岁已除，雪满门扉。");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_beginning_of_spring", "%s (春)");

        add("info.eclipticseasons.environment.season_phase.cold_rain_water", "雨水");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_rain_water", "寒冬将渐，春意犹浅。");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_rain_water", "%s (春)");

        add("info.eclipticseasons.environment.season_phase.cold_insects_awakening", "惊蛰");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_insects_awakening", "雪度杨腊，花逢寒春。");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_insects_awakening", "%s (春)");

        add("info.eclipticseasons.environment.season_phase.cold_spring_equinox", "春分");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_spring_equinox", "昼夜平分，暖意生。");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_spring_equinox", "%s (春)");

        add("info.eclipticseasons.environment.season_phase.cold_fresh_green", "清明");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_fresh_green", "燕来新社，正清明时节。");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_fresh_green", "%s (春)");

        add("info.eclipticseasons.environment.season_phase.cold_grain_rain", "谷雨");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_grain_rain", "若闻鸟啼，雨润无声。");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_grain_rain", "%s (春)");

        add("info.eclipticseasons.environment.season_phase.cold_beginning_of_summer", "立夏");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_beginning_of_summer", "山花烂漫，春色未晚。");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_beginning_of_summer", "%s (夏)");

        add("info.eclipticseasons.environment.season_phase.cold_lesser_fullness", "小满");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_lesser_fullness", "残春将去，炎暑渐至。");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_lesser_fullness", "%s (夏)");

        add("info.eclipticseasons.environment.season_phase.cold_grain_in_ear", "芒种");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_grain_in_ear", "风吹麦浪，虫鸣夏忙。");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_grain_in_ear", "%s (夏)");

        add("info.eclipticseasons.environment.season_phase.cold_summer_solstice", "夏至");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_summer_solstice", "绿树浓阴，白昼长。");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_summer_solstice", "%s (夏)");

        add("info.eclipticseasons.environment.season_phase.cold_lesser_heat", "小暑");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_lesser_heat", "暖夏未足，秋意渐。");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_lesser_heat", "%s (夏)");

        add("info.eclipticseasons.environment.season_phase.cold_greater_heat", "大暑");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_greater_heat", "几云收夏色，木叶落秋声。");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_greater_heat", "%s (夏)");

        add("info.eclipticseasons.environment.season_phase.cold_beginning_of_autumn", "立秋");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_beginning_of_autumn", "清风至，秋满天。");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_beginning_of_autumn", "%s (秋)");

        add("info.eclipticseasons.environment.season_phase.cold_end_of_heat", "处暑");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_end_of_heat", "秋意渐尽，寒气渐生。");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_end_of_heat", "%s (秋)");

        add("info.eclipticseasons.environment.season_phase.cold_white_dew", "白露");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_white_dew", "露早成霜，寒意愈盛。");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_white_dew", "%s (秋)");

        add("info.eclipticseasons.environment.season_phase.cold_autumnal_equinox", "秋分");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_autumnal_equinox", "昼渐短，夜渐长。");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_autumnal_equinox", "%s (秋)");

        add("info.eclipticseasons.environment.season_phase.cold_cold_dew", "寒露");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_cold_dew", "霜霰惊夕，寒照林杪。");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_cold_dew", "%s (秋)");

        add("info.eclipticseasons.environment.season_phase.cold_first_frost", "霜降");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_first_frost", "冬雨渐起，寒意浓。");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_first_frost", "%s (秋)");

        add("info.eclipticseasons.environment.season_phase.cold_beginning_of_winter", "立冬");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_beginning_of_winter", "风凋白草，天阴冥冥。");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_beginning_of_winter", "%s (冬)");

        add("info.eclipticseasons.environment.season_phase.cold_light_snow", "小雪");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_light_snow", "北风号怒，雪大如席。");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_light_snow", "%s (冬)");

        add("info.eclipticseasons.environment.season_phase.cold_heavy_snow", "大雪");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_heavy_snow", "雪连天高，梨花千树。");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_heavy_snow", "%s (冬)");

        add("info.eclipticseasons.environment.season_phase.cold_winter_solstice", "冬至");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_winter_solstice", "日影渐长，长夜漫漫。");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_winter_solstice", "%s (冬)");

        add("info.eclipticseasons.environment.season_phase.cold_lesser_cold", "小寒");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_lesser_cold", "数九寒天，冷在三九。");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_lesser_cold", "%s (冬)");

        add("info.eclipticseasons.environment.season_phase.cold_greater_cold", "大寒");
        add("info.eclipticseasons.environment.season_phase.alternation.cold_greater_cold", "大寒大寒，无风自寒。");
        add("info.eclipticseasons.environment.season_phase.pattern.cold_greater_cold", "%s (冬)");

        add("info.eclipticseasons.environment.season_phase.hot_beginning_of_spring", "立春");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_beginning_of_spring", "风初和，冷意没。");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_beginning_of_spring", "%s (春)");

        add("info.eclipticseasons.environment.season_phase.hot_rain_water", "雨水");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_rain_water", "春雨轻，花满渚。");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_rain_water", "%s (春)");

        add("info.eclipticseasons.environment.season_phase.hot_insects_awakening", "惊蛰");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_insects_awakening", "虫声骚动，雷声渐起。");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_insects_awakening", "%s (春)");

        add("info.eclipticseasons.environment.season_phase.hot_spring_equinox", "春分");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_spring_equinox", "昼夜平分，天气正暖。");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_spring_equinox", "%s (春)");

        add("info.eclipticseasons.environment.season_phase.hot_fresh_green", "清明");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_fresh_green", "燕衔春去，梨落清明。");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_fresh_green", "%s (春)");

        add("info.eclipticseasons.environment.season_phase.hot_grain_rain", "谷雨");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_grain_rain", "天色冥冥，风软湿重。");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_grain_rain", "%s (春)");

        add("info.eclipticseasons.environment.season_phase.hot_beginning_of_summer", "立夏");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_beginning_of_summer", "雨晴梅肥，炎暑已至。");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_beginning_of_summer", "%s (夏)");

        add("info.eclipticseasons.environment.season_phase.hot_lesser_fullness", "小满");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_lesser_fullness", "小满未满，禾黍纷纭。");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_lesser_fullness", "%s (夏)");

        add("info.eclipticseasons.environment.season_phase.hot_grain_in_ear", "芒种");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_grain_in_ear", "风吹麦浪，虫鸣夏忙。");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_grain_in_ear", "%s (夏)");

        add("info.eclipticseasons.environment.season_phase.hot_summer_solstice", "夏至");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_summer_solstice", "绿树浓阴，白昼长。");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_summer_solstice", "%s (夏)");

        add("info.eclipticseasons.environment.season_phase.hot_lesser_heat", "小暑");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_lesser_heat", "盛夏之始，入伏天。");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_lesser_heat", "%s (夏)");

        add("info.eclipticseasons.environment.season_phase.hot_greater_heat", "大暑");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_greater_heat", "日盛三伏，暑气熏。");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_greater_heat", "%s (夏)");

        add("info.eclipticseasons.environment.season_phase.hot_beginning_of_autumn", "立秋");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_beginning_of_autumn", "风熏干草，杨柳恹恹。");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_beginning_of_autumn", "%s (秋)");

        add("info.eclipticseasons.environment.season_phase.hot_end_of_heat", "处暑");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_end_of_heat", "灼日消磨，清风无处。");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_end_of_heat", "%s (秋)");

        add("info.eclipticseasons.environment.season_phase.hot_white_dew", "白露");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_white_dew", "暑气渐退，雨后新凉。");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_white_dew", "%s (秋)");

        add("info.eclipticseasons.environment.season_phase.hot_autumnal_equinox", "秋分");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_autumnal_equinox", "昼渐短，夜渐长。");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_autumnal_equinox", "%s (秋)");

        add("info.eclipticseasons.environment.season_phase.hot_cold_dew", "寒露");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_cold_dew", "长空云淡，南雁至此。");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_cold_dew", "%s (秋)");

        add("info.eclipticseasons.environment.season_phase.hot_first_frost", "霜降");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_first_frost", "绿意渐浅，梧叶初黄。");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_first_frost", "%s (秋)");

        add("info.eclipticseasons.environment.season_phase.hot_beginning_of_winter", "立冬");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_beginning_of_winter", "暑气方尽，冬犹秋。");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_beginning_of_winter", "%s (冬)");

        add("info.eclipticseasons.environment.season_phase.hot_light_snow", "小雪");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_light_snow", "霜叶红遍，晚风生寒。");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_light_snow", "%s (冬)");

        add("info.eclipticseasons.environment.season_phase.hot_heavy_snow", "大雪");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_heavy_snow", "寒雨稀稀，闲打芭蕉。");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_heavy_snow", "%s (冬)");

        add("info.eclipticseasons.environment.season_phase.hot_winter_solstice", "冬至");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_winter_solstice", "日影渐长，长夜漫漫。");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_winter_solstice", "%s (冬)");

        add("info.eclipticseasons.environment.season_phase.hot_lesser_cold", "小寒");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_lesser_cold", "北风凉冷，时偶雪。");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_lesser_cold", "%s (冬)");

        add("info.eclipticseasons.environment.season_phase.hot_greater_cold", "大寒");
        add("info.eclipticseasons.environment.season_phase.alternation.hot_greater_cold", "大寒大寒，无风犹冷。");
        add("info.eclipticseasons.environment.season_phase.pattern.hot_greater_cold", "%s (冬)");

    }

    private void addJade() {
        add("config.jade.plugin_eclipticseasons.crop", "作物");
        add("config.jade.plugin_eclipticseasons.animal", "动物");
        add("config.jade.plugin_eclipticseasons.cauldron", "炼药锅");

        add("config.jade.plugin_eclipticseasons.crop.shift_hint", "Shift按键提示");
        add("hint.jade.plugin_eclipticseasons.crop.show", "§o<..按住%s§o以查看更多..>");
        add("config.jade.plugin_eclipticseasons.snowy_status", "显示覆雪状态");
        add("hint.jade.plugin_eclipticseasons.snowy_status.snowy", "§7覆雪状态");
    }

    private void addTouhouLittleMaid() {
        add("task.eclipticseasons.clean_snow", "扫雪");
        add("task.eclipticseasons.clean_snow.desc", "适用于节气的覆雪方块");
        add("task.eclipticseasons.clean_snow.condition.has_broom", "持有扫帚");
    }

    private void addInfo() {
        add("patchouli_books.eclipticseasons.seasons_chronicle.name", "岁时记");
        add("patchouli_books.eclipticseasons.seasons_chronicle.landing_text", "每个季节都会留下印记——《岁时记》记录了这一切。");

        add("info.eclipticseasons.humidity_control", "湿度调节");
        add("info.eclipticseasons.season_quest", "季节任务");
        add("info.eclipticseasons.humidity_control.below_need", "需要下方有%s");
        add("info.eclipticseasons.humidity_control.common_need", "需要%s");
        add("info.eclipticseasons.humidity_control.extra_hint", "§7§o或者");

        add("info.eclipticseasons.bone_meal.failure", "当前环境条件无法用骨粉催熟");

        add("info.eclipticseasons.calendar.model", "已切换为%s显示模式");
        add("info.eclipticseasons.calendar.model.normal", "一般");
        add("info.eclipticseasons.calendar.model.year", "年份");
        add("info.eclipticseasons.calendar.model.next", "下一节气");

        add("pack.eclipticseasons.extra_snow", "额外覆雪方块资源包");

        add("info.eclipticseasons.config.inactive", "§7当前配置下不起作用");
        add("info.eclipticseasons.greenhouse_core.effect", "为温室中%1$s格范围内的%2$s季作物提供生长加成");

        add("info.eclipticseasons.show.shift", "§o<..按住shift..>");
        add("info.eclipticseasons.greenhouse_essence.source", "完成%1$s季进度或者任务获取");

        add("info.eclipticseasons.seasonal_prayer_scroll.use", "激活悬挂式告示牌为季节任务栏，完成限时任务");

        add("info.eclipticseasons.snow_cauldron.extraction","§7§o使用锹可以获取雪球");
        add("info.eclipticseasons.ice_cauldron.extraction","§7§o使用镐可以获取冰块");
    }

    private void addAdvancements() {
        add("advancement.eclipticseasons.base", "节气");
        add("advancement.eclipticseasons.base.desc", "");

        add("advancement.eclipticseasons.root", "春去秋来");
        add("advancement.eclipticseasons.root.desc", "度过一年二十四个节气");
        add("advancement.eclipticseasons.heat_stroke", "第一次中暑");
        add("advancement.eclipticseasons.heat_stroke.desc", "夏季中午请勿在炎热的群系直面太阳行走，如有必要，带上能抵抗炎热的装备或者冰雪降温");

        add("advancement.eclipticseasons.green_house", "温室建筑师");
        add("advancement.eclipticseasons.green_house.desc", "建造一个封闭空间，锁住空气和热量");

        add("advancement.eclipticseasons.greenhouse_core_container", "反季节种植");
        add("advancement.eclipticseasons.greenhouse_core_container.desc", "放置温室核心心室方块，准备完成季节温室建造");
        add("advancement.eclipticseasons.greenhouse_core", "心髓能量");
        add("advancement.eclipticseasons.greenhouse_core.desc", "将温室心髓放入心室方块，为温室注入季节能量");

        add("advancement.eclipticseasons.copper_grate", "制作格栅");
        add("advancement.eclipticseasons.copper_grate.desc", "也许格子里可以放些什么改变环境？");
        add("advancement.eclipticseasons.block_in_copper_grate", "调节湿度");
        add("advancement.eclipticseasons.block_in_copper_grate.desc", "可以尝试放入一些改变湿度的玩意，注意他们也许会随着时间变化或是需要额外的热量蒸发~");

        add("advancement.eclipticseasons.seasonal_prayer_scroll", "祈年令");
        add("advancement.eclipticseasons.seasonal_prayer_scroll.desc", "制作祈年令来获取更多的温室心髓");
        add("advancement.eclipticseasons.decorate_oak_hanging_sign", "无尽任务");
        add("advancement.eclipticseasons.decorate_oak_hanging_sign.desc", "用祈年令装饰悬挂式告示，完成无尽任务，获取温室心髓");

        add("advancement.eclipticseasons.quest", "季节任务");
        add("advancement.eclipticseasons.quest.desc", "完成季节任务，可以获得温室心髓奖励。");

        add("advancement.eclipticseasons.spring_start", "春季任务");
        add("advancement.eclipticseasons.spring_start.desc", "在万物生长时种植小麦");
        add("advancement.eclipticseasons.spring_harvest", "春日收成");
        add("advancement.eclipticseasons.spring_harvest.desc", "迟迟春日，收获小麦");
        add("advancement.eclipticseasons.spring_feed", "喂养动物");
        add("advancement.eclipticseasons.spring_feed.desc", "喂养羊、牛或者鸡");
        add("advancement.eclipticseasons.spring_seed", "春日留种");
        add("advancement.eclipticseasons.spring_seed.desc", "收集小麦种子，为下一个春天做准备");
        add("advancement.eclipticseasons.spring_bread", "制作面包");
        add("advancement.eclipticseasons.spring_bread.desc", "辛苦之后，来饱餐一顿吧~");
        add("advancement.eclipticseasons.spring_hay", "制作草垛");
        add("advancement.eclipticseasons.spring_hay.desc", "制作干草块，储存收获");
        // add("advancement.eclipticseasons.spring_end", "春季温室核心");
        // add("advancement.eclipticseasons.spring_end.desc", "将春季温室心髓放入温室之心室");

        add("advancement.eclipticseasons.summer_start", "夏季任务");
        add("advancement.eclipticseasons.summer_start.desc", "盛夏可以准备吃西瓜了~");
        add("advancement.eclipticseasons.summer_harvest", "夏日收获");
        add("advancement.eclipticseasons.summer_harvest.desc", "炎炎夏天，西瓜清凉");
        add("advancement.eclipticseasons.summer_melon_slice", "制作西瓜片");
        add("advancement.eclipticseasons.summer_melon_slice.desc", "会有人能吃下一整个西瓜吗？");
        add("advancement.eclipticseasons.summer_seed", "夏日留种");
        add("advancement.eclipticseasons.summer_seed.desc", "收集西瓜种子，为下一个夏天做准备");
        add("advancement.eclipticseasons.summer_glistering_melon_slice", "闪烁西瓜片");
        add("advancement.eclipticseasons.summer_glistering_melon_slice.desc", "制作闪烁的西瓜片，这是什么？");
        add("advancement.eclipticseasons.summer_eat_glistering_melon_slice", "食用闪烁西瓜片");
        add("advancement.eclipticseasons.summer_eat_glistering_melon_slice.desc", "真的能吃吗？");
        // add("advancement.eclipticseasons.summer_end", "夏季温室核心");
        // add("advancement.eclipticseasons.summer_end.desc", "将夏季温室心髓放入温室之心室");

        add("advancement.eclipticseasons.autumn_start", "秋季任务");
        add("advancement.eclipticseasons.autumn_start.desc", "种下南瓜");
        add("advancement.eclipticseasons.autumn_harvest", "秋日收获");
        add("advancement.eclipticseasons.autumn_harvest.desc", "是大南瓜诶！");
        add("advancement.eclipticseasons.autumn_seed", "秋日留种");
        add("advancement.eclipticseasons.autumn_seed.desc", "收集南瓜种子，为下一个夏天做准备");
        add("advancement.eclipticseasons.autumn_carved_pumpkin", "秋天的雕刻南瓜");
        add("advancement.eclipticseasons.autumn_carved_pumpkin.desc", "听说神秘的节日就要到了");
        add("advancement.eclipticseasons.autumn_jack_o_lantern", "制作南瓜灯");
        add("advancement.eclipticseasons.autumn_jack_o_lantern.desc", "今天也要闪亮登场~");
        add("advancement.eclipticseasons.autumn_pumpkin_pie", "制作南瓜派");
        add("advancement.eclipticseasons.autumn_pumpkin_pie.desc", "是派，不是π");
        // add("advancement.eclipticseasons.autumn_end", "秋季温室核心");
        // add("advancement.eclipticseasons.autumn_end.desc", "将秋季温室心髓放入温室之心室");

        add("advancement.eclipticseasons.winter_start", "冬季任务");
        add("advancement.eclipticseasons.winter_start.desc", "准备拿起扫帚扫雪吧~");
        add("advancement.eclipticseasons.winter_harvest", "收获细雪");
        add("advancement.eclipticseasons.winter_harvest.desc", "下雪时可以用炼药锅收集细雪");
        add("advancement.eclipticseasons.winter_campfire", "冬夜暖火");
        add("advancement.eclipticseasons.winter_campfire.desc", "雪夜，暖火，与我");
        add("advancement.eclipticseasons.winter_milk", "喝牛奶");
        add("advancement.eclipticseasons.winter_milk.desc", "暖暖身子");
        add("advancement.eclipticseasons.winter_carpet", "制作地毯");
        add("advancement.eclipticseasons.winter_carpet.desc", "噢，亲爱的，别冷着了");
        add("advancement.eclipticseasons.winter_cake", "制作蛋糕");
        add("advancement.eclipticseasons.winter_cake.desc", "让我们来庆祝一年的好收成吧~");
        // add("advancement.eclipticseasons.winter_end", "冬季温室核心");
        // add("advancement.eclipticseasons.winter_end.desc", "将冬季温室心髓放入温室之心室");
    }

    private void addSeasonQuest() {
        add("eclipticseasons.season_quest.hint.loading", "空空如也");
        add("eclipticseasons.season_quest.hint.item_count", "%sx%s");
    }

    private void addGrowthDetector() {
        add("item.eclipticseasons.growth_detector.hint.title", "§l检测结果：");

        add("item.eclipticseasons.growth_detector.hint.agro_climatic_zone", "当前农业气候类型为%s，");

        add("item.eclipticseasons.growth_detector.hint.greenroom_1", "%s正在温室中，");
        add("item.eclipticseasons.growth_detector.hint.greenroom_2", "%s可能在温室中，");
        add("item.eclipticseasons.growth_detector.hint.greenroom_3", "%s不在温室中，");

        add("item.eclipticseasons.growth_detector.hint.season_core", "缺乏季节温室核心，");
        add("item.eclipticseasons.growth_detector.hint.humidity", "湿度条件不适宜，");

        add("item.eclipticseasons.growth_detector.hint.grow_chance_1", "作物会迅速生长");
        add("item.eclipticseasons.growth_detector.hint.grow_chance_2", "作物会较快生长");
        add("item.eclipticseasons.growth_detector.hint.grow_chance_3", "作物会正常生长");
        add("item.eclipticseasons.growth_detector.hint.grow_chance_4", "作物会缓慢生长");
        add("item.eclipticseasons.growth_detector.hint.grow_chance_5", "作物几乎不会生长");
        add("item.eclipticseasons.growth_detector.hint.grow_chance_6", "作物不会生长");

    }

    private void addConfigLang() {
        add("eclipticseasons.configuration.Compat", "兼容性");
        add("eclipticseasons.configuration.GUI", "界面");
        add("eclipticseasons.configuration.EnableCropHumidityControl", "启用作物湿度控制");
        add("eclipticseasons.configuration.Particle", "粒子效果");
        add("eclipticseasons.configuration.ServerRealisticSnowyChange", "服务器真实雪景变化控制");
        add("eclipticseasons.configuration.Animal", "动物");
        add("eclipticseasons.configuration.FlowerOnGrass", "草地上的花");
        add("eclipticseasons.configuration.EnableSeasonalBreed", "启用季节性繁殖");
        add("eclipticseasons.configuration.EnhancementChunkRenderUpdate", "区块渲染更新增强");
        add("eclipticseasons.configuration.DynamicDaylightDuration", "动态白昼时长");
        add("eclipticseasons.configuration.Sound", "音效");
        add("eclipticseasons.configuration.Map", "地图");
        add("eclipticseasons.configuration.LogIllegalUse", "记录非法使用");
        add("eclipticseasons.configuration.Crop", "作物");
        add("eclipticseasons.configuration.HeatStroke", "中暑");
        add("eclipticseasons.configuration.WildGooseSpawnDelay", "大雁生成逆权重");
        add("eclipticseasons.configuration.SnowOverlayGlowingBlock", "发光方块上可以有雪覆盖");
        add("eclipticseasons.configuration.SnowyWinter", "冬季雪景");
        add("eclipticseasons.configuration.WildGoose", "大雁");
        add("eclipticseasons.configuration.UseVanillaSnowCheck", "使用简单光照检查是否覆雪");
        add("eclipticseasons.configuration.SeasonalParticles", "季节性粒子效果");
        add("eclipticseasons.configuration.Season", "季节");
        add("eclipticseasons.configuration.FireflySpawnDelay", "萤火虫生成逆权重");
        add("eclipticseasons.configuration.LastingDaysOfEachTerm", "每个节气的持续天数");
        add("eclipticseasons.configuration.ThunderChancePercentMultiplier", "雷暴几率百分比倍数");
        add("eclipticseasons.configuration.ValidDimensions", "有效维度");
        add("eclipticseasons.configuration.Temperature", "温度");
        add("eclipticseasons.configuration.Firefly", "萤火虫");
        add("eclipticseasons.configuration.MinChunkCompileWarningTime", "区块编译超时警告时间");
        add("eclipticseasons.configuration.LegacyIceAndSnowAccumulationMelt", "经典冰雪积累与融化");
        add("eclipticseasons.configuration.RealisticSnowyChange", "真实雪景变化");
        add("eclipticseasons.configuration.NotSnowyNearGlowingBlockLevel", "不覆雪需要的光照等级强度");
        add("eclipticseasons.configuration.Butterfly", "蝴蝶");
        add("eclipticseasons.configuration.Debug", "调试");
        add("eclipticseasons.configuration.UseDefaultValue", "使用默认值");
        add("eclipticseasons.configuration.SeasonalGrassColorChange", "季节性草地颜色变化");
        add("eclipticseasons.configuration.RainChancePercentMultiplier", "降雨几率百分比倍数");
        add("eclipticseasons.configuration.Weather", "天气");
        add("eclipticseasons.configuration.FallenLeaves", "落叶");
        add("eclipticseasons.configuration.ForceChunkRenderUpdate", "强制区块渲染更新");
        add("eclipticseasons.configuration.EnableInform", "启用通知");
        add("eclipticseasons.configuration.AgriculturalInformation", "农业信息");
        add("eclipticseasons.configuration.NoSnowyUnderLight0", "光照等级0下无雪");
        add("eclipticseasons.configuration.NotSnowyNearGlowingBlock", "发光方块附近无雪");
        add("eclipticseasons.configuration.DebugInfo", "调试信息");
        add("eclipticseasons.configuration.EnableSeasonalBee", "启用季节性蜜蜂");
        add("eclipticseasons.configuration.UseSolarWeather", "启用节气的群系天气");
        add("eclipticseasons.configuration.DisableSnowOverlayControlTag", "禁用雪覆盖控制标签");
        add("eclipticseasons.configuration.SereneSeasonsCropTag", "静谧四季作物标签");
        add("eclipticseasons.configuration.Renderer", "渲染器");
        add("eclipticseasons.configuration.SnowUnderFence", "栅栏下的雪");
        add("eclipticseasons.configuration.NaturalSound", "自然音效");
        add("eclipticseasons.configuration.ButterflySpawnDelay", "蝴蝶生成逆权重");
        add("eclipticseasons.configuration.EnableSeasonalFishing", "启用季节性钓鱼");
        add("eclipticseasons.configuration.EnableSeasonalCrop", "启用季节性作物");
        add("eclipticseasons.configuration.WeatherBufferDistance", "天气缓冲距离");
        add("eclipticseasons.configuration.InitialSolarTermIndex", "初始节气索引");
        add("eclipticseasons.configuration.FallenLeavesDropDelay", "落叶掉落逆权重");
        add("eclipticseasons.configuration.SnowyFullCollisionShape", "使用完整碰撞形状检查覆雪");
        add("eclipticseasons.configuration.CropGrowChanceInWrongSeason", "错误季节作物生长几率");
        add("eclipticseasons.configuration.CropGrowChanceInWrongHumidity", "错误湿润度作物生长几率");
        add("eclipticseasons.configuration.GreenHouseMaxDiameter", "温室最大直径");
        add("eclipticseasons.configuration.ComplexGreenHouseCheck", "复杂温室检查");
        add("eclipticseasons.configuration.Compat.button", "兼容性");
        add("eclipticseasons.configuration.Weather.button", "天气");
        add("eclipticseasons.configuration.Particle.button", "粒子效果");
        add("eclipticseasons.configuration.Animal.button", "动物");
        add("eclipticseasons.configuration.ValidDimensions.button", "有效维度");
        add("eclipticseasons.configuration.Sound.button", "音效");
        add("eclipticseasons.configuration.Season.button", "季节");
        add("eclipticseasons.configuration.Temperature.button", "温度");
        add("eclipticseasons.configuration.Renderer.button", "渲染器");
        add("eclipticseasons.configuration.Crop.button", "作物");
        add("eclipticseasons.configuration.GUI.button", "图形界面");
        add("eclipticseasons.configuration.Map.button", "地图");
        add("eclipticseasons.configuration.title", "标题");
        add("eclipticseasons.configuration.Debug.button", "调试");
        add("eclipticseasons.configuration.EnableInformIcon", "节气通知中渲染节气图标");
        add("eclipticseasons.configuration.CalendarItemHint", "物品日历的节气提示");

        add("eclipticseasons.configuration.LowLightGreenhouseFailChance", "低光温室作物失败概率");
        add("eclipticseasons.configuration.NoCostHumidifier", "零消耗加湿器");
        add("eclipticseasons.configuration.SimpleGreenHouseMode", "简易温室模式");
        add("eclipticseasons.configuration.ForceCompatMode", "强制兼容模式");
        add("eclipticseasons.configuration.ShouldInitWeather", "是否初始化天气");
        add("eclipticseasons.configuration.IceAndSnowMelt", "冰雪融化");
        add("eclipticseasons.configuration.ResetRendererAfterSleep", "睡觉后重置渲染器");
        add("eclipticseasons.configuration.ChangeMapColor", "更改地图颜色");
        add("eclipticseasons.configuration.RenderTestMode", "渲染测试模式");
        add("eclipticseasons.configuration.snowUnderTree", "树下积雪");
        add("eclipticseasons.configuration.SnowTransitionBlend", "雪过渡混合");
        add("eclipticseasons.configuration.CullTopFaceWithSnow", "剔除覆雪的顶面");
        add("eclipticseasons.configuration.IceAndSnow", "冰雪效果");
        add("eclipticseasons.configuration.SnowyTree", "覆雪树木");
        add("eclipticseasons.configuration.RegisterCropDefaultValue", "注册作物默认值");
        add("eclipticseasons.configuration.GreenHouseMaxHeight", "温室最大高度");
        add("eclipticseasons.configuration.UseBoxDistance", "使用矩形箱距离");

        add("eclipticseasons.configuration.SeasonCoreRange", "季节核心工作范围");

        add("eclipticseasons.configuration.SeasonGreenhouse", "季节温室粒子");
        add("eclipticseasons.configuration.SeasonGreenhouseParticleSpawnCount", "季节温室粒子生成数量");

        add("eclipticseasons.configuration.BoneMealFailureMessage", "提示催熟失败");
        add("eclipticseasons.configuration.boneMealFailureMessageOnlyImpossible", "仅不可能时，提示催熟失败");
        add("eclipticseasons.configuration.BoneMealConsumeOnFailure", "催熟失败消耗骨粉");

        add("eclipticseasons.configuration.SpringDayTimes", "春季日照时间");
        add("eclipticseasons.configuration.SummerDayTimes", "夏季日照时间");
        add("eclipticseasons.configuration.AutumnDayTimes", "秋季日照时间");
        add("eclipticseasons.configuration.WinterDayTimes", "冬季日照时间");
        add("eclipticseasons.configuration.NoneDayTimes", "默认日照时间");

        add("eclipticseasons.configuration.ForceBlocksNotSnowy", "强制方块不覆雪");

        add("eclipticseasons.configuration.WeatherFrontBias", "加强前方天气采样的权重");
        add("eclipticseasons.configuration.WeatherTransitionSpeed", "天气变化的速度");

        add("eclipticseasons.configuration.EnableLocalInfoAndCalendar", "启用本地通知和日历");

        add("eclipticseasons.configuration.CropHumidityTransition", "更平滑的作物湿度");
        add("eclipticseasons.configuration.ItemInformation", "物品信息");

        add("eclipticseasons.configuration.SeasonCoreAffectsAnimals", "季节核心影响动物");
        add("eclipticseasons.configuration.Resource", "额外资源");
        add("eclipticseasons.configuration.SaveChunkEnvironmentalHumidity", "保存环境湿度");
        add("eclipticseasons.configuration.EnableSeasonDefinition", "启用季节方块变化");
        add("eclipticseasons.configuration.SmootherSeasonalGrassColorChange", "更平滑的草木颜色变化");
        add("eclipticseasons.configuration.DynamicSnowTerm", "动态变化的下雪时间");

        add("eclipticseasons.configuration.LessFishInThunder", "雷雨天气下减少鱼类数量");
        add("eclipticseasons.configuration.FishingSeasons", "允许或更适合钓鱼的季节");
        add("eclipticseasons.configuration.SmoothSnowyEdges", "启用积雪过渡纹理模型");
        add("eclipticseasons.configuration.BeeActiveSeasons", "蜜蜂在巢外活跃的季节");
        add("eclipticseasons.configuration.DisableUniqueBiomeTagsRebinding", "禁用群系标签唯一绑定");
        add("eclipticseasons.configuration.NotRainInDesert", "禁止沙漠等群系下雨");
        add("eclipticseasons.configuration.BeePollinateSeasons", "蜜蜂授粉的季节");
        add("eclipticseasons.configuration.FixBiomePrecipitation", "修正群系降水状态");
        add("eclipticseasons.configuration.EnableWeatherRegion", "群系天气区");
        add("eclipticseasons.configuration.ExtraSnowDefinitions", "扩展的覆雪方块定义");
        add("eclipticseasons.configuration.DisableChunkCacheCleaner", "禁用自动区块额外缓存清理");

        add("eclipticseasons.configuration.SeasonalColorChangeExtend", "季节颜色变化扩展");

        add("eclipticseasons.configuration.Snow", "积雪");
        add("eclipticseasons.configuration.SnowStepMelt", "踩化积雪");
        add("eclipticseasons.configuration.ForceSnowyChunkUpdate", "强制更新覆雪的区块");
        add("eclipticseasons.configuration.SnowUnderShadow", "阴影下积雪");
        add("eclipticseasons.configuration.SnowCoverUnderBlocks", "雪下的覆雪方块");
        add("eclipticseasons.configuration.BiomeSnowLines", "群系雪线");
        add("eclipticseasons.configuration.SnowInWorld", "世界中的积雪");
        add("eclipticseasons.configuration.WeatherVotePercent", "天气投票百分比");

        add("eclipticseasons.configuration.ClearAfterSleep", "醒后天晴");

        add("eclipticseasons.configuration.Iris", "Iris");
        add("eclipticseasons.configuration.UnifiedSnowyBlockShading", "统一覆雪方块表面反射");
        add("eclipticseasons.configuration.UnifiedSnowyBlockSides", "统一覆雪方块侧面反射");

    }


}

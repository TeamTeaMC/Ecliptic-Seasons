package com.teamtea.eclipticseasons.data.general.lang;


import com.teamtea.eclipticseasons.client.registry.KeyMappingRegistry;
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

        add(KeyMappingRegistry.DEBUG_KEY.getName(),"调试界面第二键");
        add(KeyMappingRegistry.DEBUG_KEY_1.getName(),"调试界面第一键");
        add(KeyMappingRegistry.DEBUG_KEY.getCategory(),  "节气");

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
        add(ItemRegistry.salt_wand.get(), "盐石法杖");
        add(ItemRegistry.broom.get(), "扫帚");

        add(ItemRegistry.thermometer.get(), "温度计");
        add(ItemRegistry.hyetometer.get(), "雨量计");
        add(BlockRegistry.hygrometer.get(), "湿度计");

        add(BlockRegistry.greenhouse_core_container.get(), "温室之心容器");

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

        add(BlockRegistry.humidity_tank.get(), "恒湿水箱");
        add(BlockRegistry.dehumidifier.get(), "温室除湿器");

        add(BlockRegistry.season_sensor.get(), "季节传感器");

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


        addJade();
        addTouhouLittleMaid();

        addExtraEnvInfo();
    }

    private void addExtraEnvInfo() {
        add("info.eclipticseasons.environment.solar_term.hint2", "第 %s 年:");
        add("info.eclipticseasons.environment.solar_term.hint4", "第 %s 天:");
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
        add("config.jade.plugin_eclipticseasons.greenhouse_core", "温室核心");

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
}

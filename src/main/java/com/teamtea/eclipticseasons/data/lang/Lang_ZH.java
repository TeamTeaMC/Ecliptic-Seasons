package com.teamtea.eclipticseasons.data.lang;


import com.teamtea.eclipticseasons.common.registry.AgroClimateRegistry;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.registry.SeasonQuestRegistry;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;


public class Lang_ZH extends LangHelper {
    public Lang_ZH(PackOutput gen, ExistingFileHelper helper) {
        super(gen, helper, EclipticSeasonsApi.MODID, "zh_cn");
    }


    @Override
    protected void addTranslations() {
        addAdvancements();

        add("info.eclipticseasons.environment.solar_term.hint", "今日节气:");
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


        add(AgroClimateRegistry.COLD, "寒带");
        add(AgroClimateRegistry.TEMPERATE, "温带");
        add(AgroClimateRegistry.HOT, "热带");
        add(AgroClimateRegistry.DESERT, "沙漠");
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
    }

    private void addJade() {
        add("config.jade.plugin_eclipticseasons.crop","作物");
        add("config.jade.plugin_eclipticseasons.crop.shift_hint","Shift按键提示");
        add("hint.jade.plugin_eclipticseasons.crop.show", "§o<..按住shift以查看更多..>");
        add("config.jade.plugin_eclipticseasons.snowy_status","显示覆雪状态");
        add("hint.jade.plugin_eclipticseasons.snowy_status.snowy", "§7覆雪状态");
    }

    private void addInfo() {
        add("info.eclipticseasons.humidity_control", "湿度调节");
        add("info.eclipticseasons.season_quest", "季节任务");
        add("info.eclipticseasons.humidity_control.below_need", "需要下方有%s");
        add("info.eclipticseasons.humidity_control.common_need", "需要%s");
        add("info.eclipticseasons.humidity_control.extra_hint", "§7§o或者");
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
        add("eclipticseasons.configuration.WildGooseSpawnWeight", "大雁生成权重");
        add("eclipticseasons.configuration.SnowOverlayGlowingBlock", "发光方块上可以有雪覆盖");
        add("eclipticseasons.configuration.SnowyWinter", "冬季雪景");
        add("eclipticseasons.configuration.WildGoose", "大雁");
        add("eclipticseasons.configuration.UseVanillaCheck", "使用原版检查");
        add("eclipticseasons.configuration.SeasonParticle", "季节粒子效果");
        add("eclipticseasons.configuration.Season", "季节");
        add("eclipticseasons.configuration.FireflySpawnWeight", "萤火虫生成权重");
        add("eclipticseasons.configuration.LastingDaysOfEachTerm", "每个节气的持续天数");
        add("eclipticseasons.configuration.ThunderChancePercentMultiplier", "雷暴几率百分比倍数");
        add("eclipticseasons.configuration.ValidDimensions", "有效维度");
        add("eclipticseasons.configuration.Temperature", "温度");
        add("eclipticseasons.configuration.Firefly", "萤火虫");
        add("eclipticseasons.configuration.MinChunkCompileWaringTime", "区块编译超时警告时间");
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
        add("eclipticseasons.configuration.UseSolarWeather", "使用太阳天气");
        add("eclipticseasons.configuration.DisableSnowOverlayControlTag", "禁用雪覆盖控制标签");
        add("eclipticseasons.configuration.SereneSeasonsCropTag", "静谧四季作物标签");
        add("eclipticseasons.configuration.Renderer", "渲染器");
        add("eclipticseasons.configuration.SnowUnderFence", "栅栏下的雪");
        add("eclipticseasons.configuration.NaturalSound", "自然音效");
        add("eclipticseasons.configuration.butterflySpawnWeight", "蝴蝶生成权重");
        add("eclipticseasons.configuration.EnableSeasonalFishing", "启用季节性钓鱼");
        add("eclipticseasons.configuration.EnableSeasonalCrop", "启用季节性作物");
        add("eclipticseasons.configuration.WeatherBufferDistance", "天气缓冲距离");
        add("eclipticseasons.configuration.InitialSolarTermIndex", "初始节气索引");
        add("eclipticseasons.configuration.FallenLeavesDropWeight", "落叶掉落权重");
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


        add("eclipticseasons.configuration.AutumnTemps", "秋季温度");
        add("eclipticseasons.configuration.SummerTemps", "夏季温度");
        add("eclipticseasons.configuration.SpringTemps", "春季温度");
        add("eclipticseasons.configuration.ColdSweat", "冷汗");
        add("eclipticseasons.configuration.WinterTemps", "冬季温度");
        add("eclipticseasons.configuration.SpringTemps.button", "春季温度");
        add("eclipticseasons.configuration.WinterTemps.button", "冬季温度");
        add("eclipticseasons.configuration.SummerTemps.button", "夏季温度");
        add("eclipticseasons.configuration.AutumnTemps.button", "秋季温度");
        add("eclipticseasons.configuration.ColdSweat.button", "冷汗");

        add("eclipticseasons.configuration.JourneyMap", "旅行地图");
        add("eclipticseasons.configuration.ShowSnowyBlock", "显示覆雪方块");
        add("eclipticseasons.configuration.JourneyMap.button", "旅行地图");
    }


}

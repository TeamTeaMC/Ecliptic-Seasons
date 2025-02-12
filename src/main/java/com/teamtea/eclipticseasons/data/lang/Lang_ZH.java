package com.teamtea.eclipticseasons.data.lang;


import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;


public class Lang_ZH extends LangHelper {
    public Lang_ZH(PackOutput gen, ExistingFileHelper helper) {
        super(gen, helper, EclipticSeasonsApi.MODID, "zh_cn");
    }


    @Override
    protected void addTranslations() {
        add("advancement.eclipticseasons.root", "春去秋来");
        add("advancement.eclipticseasons.root.desc", "度过一年二十四个节气");
        add("advancement.eclipticseasons.heat_stroke", "第一次中暑");
        add("advancement.eclipticseasons.heat_stroke.desc", "夏季中午请勿在炎热的群系直面太阳行走，如有必要，带上能抵抗炎热的装备或者冰雪降温。");

        add("info.eclipticseasons.environment.solar_term.hint", "今日节气:");
        add("itemGroup." + EclipticSeasonsApi.MODID + ".core", "节气");


        add(BlockRegistry.calendar.get(), "日历");
        add("item.eclipticseasons.calendar.pop_hint", "%1$s，%2$s/%3$s");
        // add(EclipticSeasons.ModContents.wind_chimes.get(), "风铃");
        // add(EclipticSeasons.ModContents.paper_wind_chimes.get(), "纸风铃");
        // add(EclipticSeasons.ModContents.bamboo_wind_chimes.get(), "竹风铃");
        // add(EclipticSeasons.ModContents.pinwheel_blue.get(), "蓝色纸风车");
        // add(EclipticSeasons.ModContents.pinwheel_lime.get(), "淡绿色纸风车");
        // add(EclipticSeasons.ModContents.pinwheel_orange.get(), "橘色纸风车");
        // add(EclipticSeasons.ModContents.snowy_maker_item.get(), "冰晶法杖");
        // add(EclipticSeasons.ModContents.broom_item.get(), "扫帚");
        add(ItemRegistry.thermometer.get(), "温度计");
        add(ItemRegistry.hyetometer.get(), "雨量计");
        add(ItemRegistry.hygrometer.get(), "湿度计");

        add("info.eclipticseasons.environment.temperature.under_freezing","严寒");
        add("info.eclipticseasons.environment.temperature.freezing","冰冻");
        add("info.eclipticseasons.environment.temperature.cold","寒冷");
        add("info.eclipticseasons.environment.temperature.cool","凉爽");
        add("info.eclipticseasons.environment.temperature.warm","温暖");
        add("info.eclipticseasons.environment.temperature.hot","炎热");
        add("info.eclipticseasons.environment.temperature.heat","炙烤");
        add("info.eclipticseasons.environment.temperature.over_heat","酷暑");
        add("info.eclipticseasons.environment.rainfall.rare","罕见");
        add("info.eclipticseasons.environment.rainfall.scarce","稀少");
        add("info.eclipticseasons.environment.rainfall.moderate","中等");
        add("info.eclipticseasons.environment.rainfall.adequate","足量");
        add("info.eclipticseasons.environment.rainfall.abundant","丰富");
        add("info.eclipticseasons.environment.humidity.arid","干旱");
        add("info.eclipticseasons.environment.humidity.dry","干燥");
        add("info.eclipticseasons.environment.humidity.average","一般");
        add("info.eclipticseasons.environment.humidity.moist","湿润");
        add("info.eclipticseasons.environment.humidity.humid","潮湿");
        add("info.eclipticseasons.environment.humidity","适宜湿度： ");
        add("info.eclipticseasons.environment.season","适宜季节： ");
        add("info.eclipticseasons.environment.season.spring","春");
        add("info.eclipticseasons.environment.season.summer","夏");
        add("info.eclipticseasons.environment.season.autumn","秋");
        add("info.eclipticseasons.environment.season.winter","冬");
        add("info.eclipticseasons.environment.season.none","全年");
        add("info.eclipticseasons.environment.solar_term.beginning_of_spring","立春");
        add("info.eclipticseasons.environment.solar_term.rain_water","雨水");
        add("info.eclipticseasons.environment.solar_term.insects_awakening","惊蛰");
        add("info.eclipticseasons.environment.solar_term.spring_equinox","春分");
        add("info.eclipticseasons.environment.solar_term.fresh_green","清明");
        add("info.eclipticseasons.environment.solar_term.grain_rain","谷雨");
        add("info.eclipticseasons.environment.solar_term.beginning_of_summer","立夏");
        add("info.eclipticseasons.environment.solar_term.lesser_fullness","小满");
        add("info.eclipticseasons.environment.solar_term.grain_in_ear","芒种");
        add("info.eclipticseasons.environment.solar_term.summer_solstice","夏至");
        add("info.eclipticseasons.environment.solar_term.lesser_heat","小暑");
        add("info.eclipticseasons.environment.solar_term.greater_heat","大暑");
        add("info.eclipticseasons.environment.solar_term.beginning_of_autumn","立秋");
        add("info.eclipticseasons.environment.solar_term.end_of_heat","处暑");
        add("info.eclipticseasons.environment.solar_term.white_dew","白露");
        add("info.eclipticseasons.environment.solar_term.autumnal_equinox","秋分");
        add("info.eclipticseasons.environment.solar_term.cold_dew","寒露");
        add("info.eclipticseasons.environment.solar_term.first_frost","霜降");
        add("info.eclipticseasons.environment.solar_term.beginning_of_winter","立冬");
        add("info.eclipticseasons.environment.solar_term.light_snow","小雪");
        add("info.eclipticseasons.environment.solar_term.heavy_snow","大雪");
        add("info.eclipticseasons.environment.solar_term.winter_solstice","冬至");
        add("info.eclipticseasons.environment.solar_term.lesser_cold","小寒");
        add("info.eclipticseasons.environment.solar_term.greater_cold","大寒");
        add("info.eclipticseasons.environment.solar_term.message","[节气提示] %s");
        add("info.eclipticseasons.environment.solar_term.alternation.beginning_of_spring","春回大地，万物复苏。");
        add("info.eclipticseasons.environment.solar_term.alternation.rain_water","细雨如酥，遥看草色。");
        add("info.eclipticseasons.environment.solar_term.alternation.insects_awakening","虫声骚动，雷声渐起。");
        add("info.eclipticseasons.environment.solar_term.alternation.spring_equinox","昼夜平分，暖意生。");
        add("info.eclipticseasons.environment.solar_term.alternation.fresh_green","燕来新社，梨落清明。");
        add("info.eclipticseasons.environment.solar_term.alternation.grain_rain","布谷鸟啼，雨润无声。");
        add("info.eclipticseasons.environment.solar_term.alternation.beginning_of_summer","残春已去，炎暑将至。");
        add("info.eclipticseasons.environment.solar_term.alternation.lesser_fullness","春粒渐满，夏果新熟。");
        add("info.eclipticseasons.environment.solar_term.alternation.grain_in_ear","风吹麦浪，虫鸣夏忙。");
        add("info.eclipticseasons.environment.solar_term.alternation.summer_solstice","绿树浓阴，白昼长。");
        add("info.eclipticseasons.environment.solar_term.alternation.lesser_heat","盛夏之始，入伏天。");
        add("info.eclipticseasons.environment.solar_term.alternation.greater_heat","日盛三伏，暑气熏。");
        add("info.eclipticseasons.environment.solar_term.alternation.beginning_of_autumn","云天收夏色，木叶动秋声。");
        add("info.eclipticseasons.environment.solar_term.alternation.end_of_heat","暑气渐止，秋雨至。");
        add("info.eclipticseasons.environment.solar_term.alternation.white_dew","清风至，白露生。");
        add("info.eclipticseasons.environment.solar_term.alternation.autumnal_equinox","昼渐短，夜渐长。");
        add("info.eclipticseasons.environment.solar_term.alternation.cold_dew","秋意渐浓，寒气渐生。");
        add("info.eclipticseasons.environment.solar_term.alternation.first_frost","凝露成霜，寒意愈盛。");
        add("info.eclipticseasons.environment.solar_term.alternation.beginning_of_winter","北风悄潜，秋尽冬。");
        add("info.eclipticseasons.environment.solar_term.alternation.light_snow","冬雨渐起，寒意浓。");
        add("info.eclipticseasons.environment.solar_term.alternation.heavy_snow","雪起此时，千树梨花。");
        add("info.eclipticseasons.environment.solar_term.alternation.winter_solstice","日影渐长，长夜漫漫。");
        add("info.eclipticseasons.environment.solar_term.alternation.lesser_cold","数九寒天，冷在三九。");
        add("info.eclipticseasons.environment.solar_term.alternation.greater_cold","大寒大寒，无风自寒。");
        add("commands.eclipticseasons.solar.set","已将节气天数设置为第%s天");
        add("effect.eclipticseasons.heat_stroke","中暑");


    }


}

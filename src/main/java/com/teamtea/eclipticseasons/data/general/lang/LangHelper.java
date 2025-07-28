package com.teamtea.eclipticseasons.data.general.lang;

import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.LanguageProvider;

public abstract class LangHelper extends LanguageProvider {
    private final ExistingFileHelper helper;
    private final PackOutput output;


    public LangHelper(PackOutput output, ExistingFileHelper helper, String modid, String locale) {
        super(output, modid, locale);
        this.output = output;
        this.helper = helper;
        this.modid = modid;
        this.locale = locale;
    }


    public <T> void add(ResourceKey<T> key, String name) {
        add(ESRegistries.createLangKey(key), name);
    }


    // There is a lot of code here that is redundant, but indispensable. In order to make corrections
    protected abstract void addTranslations();

    private final String locale;
    public final String modid;

}

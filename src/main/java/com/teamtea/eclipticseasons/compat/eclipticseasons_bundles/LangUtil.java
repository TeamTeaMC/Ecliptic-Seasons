package com.teamtea.eclipticseasons.compat.eclipticseasons_bundles;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teamtea.eclipticseasons.compat.Platform;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class LangUtil {

    private static final Pattern PATTERN = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
    private static final Gson GSON = new Gson();
    private static final Map<String, String> LANG_TABLE = new HashMap<>();

    private static void loadLocaleData(final InputStream inputstream) {
        try {
            JsonElement jsonelement = GSON.fromJson(new InputStreamReader(inputstream, StandardCharsets.UTF_8), JsonElement.class);
            JsonObject jsonobject = GsonHelper.convertToJsonObject(jsonelement, "strings");

            jsonobject.entrySet().forEach(entry -> {
                String s = PATTERN.matcher(GsonHelper.convertToString(entry.getValue(), entry.getKey())).replaceAll("%$1s");
                LANG_TABLE.put(entry.getKey(), s);
            });
        } finally {
            IOUtils.closeQuietly(inputstream);
        }
    }

    public static String parseI18n(String key, Object... objects) {
        try {
            return LANG_TABLE.getOrDefault(key, key).formatted(objects);
        } catch (Exception e) {
            e.printStackTrace();
            return key;
        }
    }

    public static void tryLoadLang(String modid, boolean shouldClear) {
        if (shouldClear)
            LANG_TABLE.clear();
        try {
            final InputStream forge = Thread.currentThread().getContextClassLoader().getResourceAsStream("assets/%s/lang/%s.json".formatted(modid, getLangCode()));
            loadLocaleData(forge);
        } catch (Exception ignored) {
        }
    }

    private static String getLangCode() {
        try {
            Class<?> mcClass = Class.forName("net.minecraft.client.Minecraft");

            Method getInstance = mcClass.getDeclaredMethod("getInstance");
            Object mc = getInstance.invoke(null);

            Field optionsField = mcClass.getDeclaredField("options");
            optionsField.setAccessible(true);
            Object optionsObj = optionsField.get(mc);

            Field langField = optionsObj.getClass().getDeclaredField("languageCode");
            langField.setAccessible(true);
            return (String) langField.get(optionsObj);
        } catch (Exception ignored) {
            return "en_us";
        }
    }

    public static String getModName(String modId) {
        return Platform.getModFile(modId).getModFileInfo().getMods().get(0).getDisplayName();
    }
}

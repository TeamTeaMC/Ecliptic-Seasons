package com.teamtea.eclipticseasons.client.gui.screen;

import lombok.Getter;
import net.minecraftforge.common.ForgeConfigSpec;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Predicate;

public class SpecUtil {
    public static <T> ForgeConfigSpec.ValueSpec getSpec(ForgeConfigSpec.ConfigValue<T> configValue) {
        try {
            Object next = configValue.next();

            Field storageField = next.getClass().getDeclaredField("storage");
            storageField.setAccessible(true);

            com.electronwill.nightconfig.core.Config storage = (com.electronwill.nightconfig.core.Config) storageField.get(next);

            return storage.get(configValue.getPath());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to get ValueSpec from ForgeConfigSpec.ConfigValue", e);
        }
    }

    public static <V extends Comparable<? super V>> Range<V> getRange(ForgeConfigSpec.ValueSpec spec) {
        try {
            Method getRange = spec.getClass().getDeclaredMethod("getRange");
            getRange.setAccessible(true);

            Object range = getRange.invoke(spec);
            if (range == null) {
                return null;
            }

            Method getMin = range.getClass().getDeclaredMethod("getMin");
            Method getMax = range.getClass().getDeclaredMethod("getMax");

            getMin.setAccessible(true);
            getMax.setAccessible(true);

            V min = (V) getMin.invoke(range);
            V max = (V) getMax.invoke(range);

            return Range.of2(min, max);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to read range from ValueSpec", e);
        }
    }


    @Getter
    public static
    class Range<V extends Comparable<? super V>> implements Predicate<Object> {
        private final Class<?> clazz;
        private final V min;
        private final V max;

        private Range(Class<V> clazz, V min, V max) {
            this.clazz = clazz;
            this.min = min;
            this.max = max;
        }

        private Range(V min, V max) {
            this.clazz = min.getClass();
            this.min = min;
            this.max = max;
        }

        public static Range<Integer> of(int i, int i1) {
            return new Range<>(Integer.class, i, i1);
        }

        public static <V extends Comparable<? super V>> Range<V> of2(V i, V i1) {
            return new Range<>(i, i1);
        }

        private boolean isNumber(Object other) {
            return Number.class.isAssignableFrom(clazz) && other instanceof Number;
        }

        @Override
        public boolean test(Object t) {
            if (isNumber(t)) {
                Number n = (Number) t;
                boolean result = ((Number) min).doubleValue() <= n.doubleValue() && n.doubleValue() <= ((Number) max).doubleValue();
                if (!result)
                    return result;
            }
            if (!clazz.isInstance(t)) return false;
            V c = (V) clazz.cast(t);

            return c.compareTo(min) >= 0 && c.compareTo(max) <= 0;
        }
    }
}

package com.teamtea.eclipticseasons.common.misc;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class LazyGet<T> {
    private final Supplier<T> cache;
    private T lock;

    private LazyGet(Supplier<T> supplier) {
        this.cache = supplier;
    }

    public static <T> LazyGet<T> of(Supplier<T> supplier) {
        return new LazyGet<>(supplier);
    }

    public T get() {
        if (lock == null)
            lock = cache.get();
        return lock;
    }

    public void ifPresent(Consumer<T> consumer) {
        consumer.accept(get());
    }

    public <U> Optional<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        return this.isPresent() ? Optional.of(mapper.apply(this.getValueUnsafe())) : Optional.empty();
    }

    private T getValueUnsafe() {
        return cache.get();
    }

    public boolean isPresent() {
        get();
        return lock == null;
    }

    public T orElse(T t) {
        get();
        return lock==null?t:null;
    }
}

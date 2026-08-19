package com.teamtea.eclipticseasons.common.mixin.condition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Applies a Mixin only when the declared mod environment matches.
 *
 * <p>All {@link #allOf()} conditions must match, at least one
 * {@link #anyOf()} condition must match when the array is not empty, and no
 * {@link #noneOf()} condition may match.</p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface ConditionalMixin {
    /** Shorthand mod id for the common single-mod case. */
    String value() default "";

    /** Minimum version used with {@link #value()}; empty only checks loading. */
    String version() default "";

    ModCondition[] allOf() default {};

    ModCondition[] anyOf() default {};

    ModCondition[] noneOf() default {};
}

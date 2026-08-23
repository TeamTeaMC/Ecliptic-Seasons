package com.teamtea.eclipticseasons.common.mixin.condition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Describes one required, optional, or forbidden mod and minimum version. */
@Target({})
@Retention(RetentionPolicy.CLASS)
public @interface ModCondition {
    /** Mod identifier as exposed by the active loader. */
    String value();

    /** Minimum accepted version, compared by Platform.isVersionSatisfied. */
    String version() default "";

    String name() default "";
}

package com.teamtea.eclipticseasons.common.mixin.injector;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Invokes a handler without allocating CallbackInfo.
 *
 * <p>This intentionally supports target arguments only. It does not capture
 * local variables.</p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DirectInject {
    String[] method();

    Slice[] slice() default {};

    At[] at();

    Mode mode() default Mode.CONTINUE;

    /**
     * Identifies the method that supplies the target return value for
     * {@link Mode#RETURN_IF_TRUE}.
     *
     * <p>A simple name searches for a method merged into the target class:</p>
     *
     * <pre>{@code
     * returnHandler = "eclipticseasons$getColor"
     * }</pre>
     *
     * <p>A JVM member selector invokes an external static method:</p>
     *
     * <pre>{@code
     * returnHandler =
     *     "Lcom/example/ColorHandler;getColor("
     *     + "Lnet/minecraft/world/level/BlockAndTintGetter;"
     *     + "Lnet/minecraft/core/BlockPos;)I"
     * }</pre>
     */
    String returnHandler() default "";


    boolean remap() default true;

    int require() default -1;

    int expect() default 1;

    int allow() default -1;

    int order() default 1000;

    enum Mode {
        CONTINUE,

        /**
         * * Returns the handler result immediately from the target.
         */
        RETURN,

        /**
         * Returns the handler result while preserving a formal control-flow
         * continuation edge to the original target instructions.
         *
         * <p>Generated control flow:</p>
         *
         * <pre>{@code
         * Result result = handler(...);
         * if (true) {
         *     return result;
         * }
         * // Original target instructions remain reachable in the control-flow graph.
         * }</pre>
         */
        RETURN_WITH_CONTINUATION,
        /**
         * Returns the configured return handler result when the annotated
         * boolean guard handler returns {@code true}.
         */
        RETURN_IF_TRUE,

        /**
         * Cancels a void target when the boolean handler returns {@code true}.
         */
        CANCEL_IF_TRUE
    }
}

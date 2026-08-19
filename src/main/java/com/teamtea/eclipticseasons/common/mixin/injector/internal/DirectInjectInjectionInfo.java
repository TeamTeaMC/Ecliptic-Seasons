package com.teamtea.eclipticseasons.common.mixin.injector.internal;

import com.teamtea.eclipticseasons.common.mixin.injector.DirectInject;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.injection.code.Injector;
import org.spongepowered.asm.mixin.injection.struct.InjectionInfo;
import org.spongepowered.asm.mixin.injection.throwables.InvalidInjectionException;
import org.spongepowered.asm.mixin.transformer.MixinTargetContext;
import org.spongepowered.asm.util.Annotations;

@InjectionInfo.AnnotationType(DirectInject.class)
@InjectionInfo.HandlerPrefix("directInject")
public class DirectInjectInjectionInfo extends InjectionInfo {
    public DirectInjectInjectionInfo(
            MixinTargetContext mixin,
            MethodNode method,
            AnnotationNode annotation
    ) {
        super(mixin, method, annotation);
    }

    @Override
    protected Injector parseInjector(AnnotationNode annotation) {
        DirectInject.Mode mode = Annotations.getValue(
                annotation,
                "mode",
                DirectInject.Mode.class,
                DirectInject.Mode.CONTINUE
        );

        String returnHandler = Annotations.getValue(
                annotation,
                "returnHandler",
                ""
        );

        if (mode == DirectInject.Mode.RETURN_IF_TRUE
                && returnHandler.isEmpty()) {
            throw new InvalidInjectionException(
                    this,
                    "@DirectInject RETURN_IF_TRUE requires returnHandler"
            );
        }

        if (mode != DirectInject.Mode.RETURN_IF_TRUE
                && !returnHandler.isEmpty()) {
            throw new InvalidInjectionException(
                    this,
                    "@DirectInject returnHandler is only valid with RETURN_IF_TRUE"
            );
        }

        return new DirectInjector(
                this,
                mode,
                returnHandler
        );
    }

    @Override
    public String getSliceId(String id) {
        return id == null ? "" : id;
    }
}
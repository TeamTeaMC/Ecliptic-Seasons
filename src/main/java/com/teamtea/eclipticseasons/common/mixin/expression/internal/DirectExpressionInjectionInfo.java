package com.teamtea.eclipticseasons.common.mixin.expression.internal;

import com.teamtea.eclipticseasons.common.mixin.expression.DirectExpression;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.injection.code.Injector;
import org.spongepowered.asm.mixin.injection.struct.InjectionInfo;
import org.spongepowered.asm.mixin.transformer.MixinTargetContext;
import org.spongepowered.asm.util.Annotations;

@InjectionInfo.AnnotationType(DirectExpression.class)
@InjectionInfo.HandlerPrefix("directExpression")
public final class DirectExpressionInjectionInfo extends InjectionInfo {
    public DirectExpressionInjectionInfo(
            MixinTargetContext mixin,
            MethodNode method,
            AnnotationNode annotation
    ) {
        super(mixin, method, annotation);
    }

    @Override
    protected Injector parseInjector(AnnotationNode annotation) {
        String handler = Annotations.getValue(annotation, "handler", "");
        return new DirectExpressionInjector(this, handler);
    }

    @Override
    public String getSliceId(String id) {
        return id == null ? "" : id;
    }
}
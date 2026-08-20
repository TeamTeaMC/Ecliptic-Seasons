package com.teamtea.eclipticseasons.common.mixin.processor;

import com.teamtea.eclipticseasons.common.mixin.expression.internal.DirectExpressionInjectionInfo;
import com.teamtea.eclipticseasons.common.mixin.injector.internal.DirectInjectInjectionInfo;
import org.spongepowered.asm.mixin.injection.struct.InjectionInfo;
import org.spongepowered.asm.util.logging.MessageRouter;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/** Makes Mixin's annotation processor aware of DirectInject. */
@SupportedAnnotationTypes("*")
public final class DirectInjectProcessor extends AbstractProcessor {
    private static boolean initialized;

    @Override
    public void init(ProcessingEnvironment environment) {
        super.init(environment);
        try {
            MessageRouter.setMessager(environment.getMessager());
            register();
        } catch (NoClassDefFoundError ignored) {
            // The processor may be discovered in a compilation without Mixin.
        }
    }

    private static synchronized void register() {
        if (!initialized) {
            InjectionInfo.register(DirectInjectInjectionInfo.class);
            InjectionInfo.register(DirectExpressionInjectionInfo.class);
            initialized = true;
        }
    }

    @Override
    public boolean process(
            Set<? extends TypeElement> annotations,
            RoundEnvironment roundEnvironment
    ) {
        return false;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}

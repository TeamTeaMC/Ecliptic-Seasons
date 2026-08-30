package com.teamtea.eclipticseasons.common.mixin.condition;

import com.teamtea.eclipticseasons.compat.Platform;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.spongepowered.asm.service.MixinService;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Reads ConditionalMixin annotations without loading Mixin classes.
 */
public class ConditionalMixinEvaluator {
    private static final String ANNOTATION_DESCRIPTOR = Type.getDescriptor(ConditionalMixin.class);
    private static final Map<String, Optional<ConditionSpec>> CACHE = new ConcurrentHashMap<>();

    public static boolean shouldApply(String mixinClassName) {
        Optional<ConditionSpec> condition = CACHE.computeIfAbsent(
                mixinClassName,
                ConditionalMixinEvaluator::readCondition
        );
        return condition.map(ConditionSpec::matches).orElse(true);
    }

    public static void clearCache() {
        CACHE.clear();
    }

    private static Optional<ConditionSpec> readCondition(String className) {
        String resourceName = className.replace('.', '/') + ".class";
        InputStream stream = MixinService.getService().getResourceAsStream(resourceName);
        if (stream == null) {
            ClassLoader ownLoader = ConditionalMixinEvaluator.class.getClassLoader();
            stream = ownLoader.getResourceAsStream(resourceName);
        }
        if (stream == null) {
            throw new IllegalStateException("Cannot read Mixin class resource " + resourceName);
        }
        try (InputStream input = stream) {
            ConditionClassVisitor visitor = new ConditionClassVisitor();
            new ClassReader(input).accept(
                    visitor,
                    ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES
            );
            return Optional.ofNullable(visitor.condition);
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot inspect Mixin class " + className, exception);
        }
    }

    private static final class ConditionClassVisitor extends ClassVisitor {
        private ConditionSpec condition;

        private ConditionClassVisitor() {
            super(Opcodes.ASM9);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            if (!ANNOTATION_DESCRIPTOR.equals(descriptor)) {
                return null;
            }
            this.condition = new ConditionSpec();
            return new ConditionAnnotationVisitor(this.condition);
        }
    }

    private static final class ConditionAnnotationVisitor extends AnnotationVisitor {
        private final ConditionSpec condition;

        private ConditionAnnotationVisitor(ConditionSpec condition) {
            super(Opcodes.ASM9);
            this.condition = condition;
        }

        @Override
        public void visit(String name, Object value) {
            if (name.equals("value")) {
                this.condition.direct.id = (String) value;
            } else if (name.equals("version")) {
                this.condition.direct.version = (String) value;
            } else if (name.equals("name")) {
                this.condition.direct.name = (String) value;
            }
        }

        @Override
        public AnnotationVisitor visitArray(String name) {
            List<ModSpec> destination = switch (name) {
                case "allOf" -> this.condition.allOf;
                case "anyOf" -> this.condition.anyOf;
                case "noneOf" -> this.condition.noneOf;
                default -> null;
            };
            return destination == null ? null : new ConditionArrayVisitor(destination);
        }
    }

    private static final class ConditionArrayVisitor extends AnnotationVisitor {
        private final List<ModSpec> destination;

        private ConditionArrayVisitor(List<ModSpec> destination) {
            super(Opcodes.ASM9);
            this.destination = destination;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String name, String descriptor) {
            ModSpec spec = new ModSpec();
            this.destination.add(spec);
            return new ModAnnotationVisitor(spec);
        }
    }

    private static final class ModAnnotationVisitor extends AnnotationVisitor {
        private final ModSpec mod;

        private ModAnnotationVisitor(ModSpec mod) {
            super(Opcodes.ASM9);
            this.mod = mod;
        }

        @Override
        public void visit(String name, Object value) {
            if (name.equals("value")) {
                this.mod.id = (String) value;
            } else if (name.equals("version")) {
                this.mod.version = (String) value;
            } else if (name.equals("name")) {
                this.mod.name = (String) value;
            }
        }
    }

    public static class ConditionSpec {
        public final ModSpec direct = new ModSpec();
        public final List<ModSpec> allOf = new ArrayList<>();
        public final List<ModSpec> anyOf = new ArrayList<>();
        public final List<ModSpec> noneOf = new ArrayList<>();

        public boolean matches() {
            return (this.direct.id == null || this.direct.id.isEmpty() || this.direct.matches())
                    && this.allOf.stream().allMatch(ModSpec::matches)
                    && (this.anyOf.isEmpty() || this.anyOf.stream().anyMatch(ModSpec::matches))
                    && this.noneOf.stream().noneMatch(ModSpec::matches);
        }
    }

    public static class ModSpec {
        public String id;
        public String version = "";
        public String name;

        public ModSpec(String id, String name, String version) {
            this.id = id;
            this.name = name;
            this.version = version;
        }

        public ModSpec() {
        }

        public boolean matches() {
            if (this.id == null || this.id.isEmpty()) {
                return false;
            }
            if (name != null && !name.isEmpty()
                    && !Platform.getModName(id, 0).equals(name)) {
                return false;
            }
            return this.version == null || this.version.isEmpty()
                    ? Platform.isModLoaded(this.id)
                    : Platform.isVersionSatisfied(this.id, this.version);
        }
    }
}

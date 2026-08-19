package com.teamtea.eclipticseasons.common.mixin.expression.internal;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.spongepowered.asm.mixin.injection.code.Injector;
import org.spongepowered.asm.mixin.injection.struct.InjectionInfo;
import org.spongepowered.asm.mixin.injection.struct.InjectionNodes.InjectionNode;
import org.spongepowered.asm.mixin.injection.struct.Target;
import org.spongepowered.asm.mixin.injection.throwables.InvalidInjectionException;
import org.spongepowered.asm.util.Bytecode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DirectExpressionInjector extends Injector {
    private final String replacementHandler;

    public DirectExpressionInjector(
            InjectionInfo info,
            String replacementHandler
    ) {
        super(info, "@DirectExpression");
        this.replacementHandler = replacementHandler;
    }

    @Override
    protected void inject(
            Target target,
            InjectionNode node
    ) {
        this.checkTargetModifiers(target, false);

        AbstractInsnNode originalNode =
                node.getOriginalTarget();

        AbstractInsnNode currentNode =
                node.getCurrentTarget();

        if (!(originalNode instanceof MethodInsnNode invocation)) {
            throw new InvalidInjectionException(
                    this.info,
                    "@DirectExpression only supports method invocations"
            );
        }

        if (invocation.name.equals("<init>")) {
            throw new InvalidInjectionException(
                    this.info,
                    "@DirectExpression does not support constructors"
            );
        }

        /*
         * DirectExpression must run before injectors which replace the
         * original invocation, such as WrapOperation.
         */
        if (currentNode != originalNode) {
            throw new InvalidInjectionException(
                    this.info,
                    "@DirectExpression target was already replaced by another "
                            + "injector: "
                            + Bytecode.describeNode(currentNode)
            );
        }

        Type expressionType =
                Type.getReturnType(invocation.desc);

        if (expressionType.getSort() == Type.VOID) {
            throw new InvalidInjectionException(
                    this.info,
                    "@DirectExpression requires a non-void invocation"
            );
        }

        Type tokenType = this.returnType;

        if (tokenType.getSort() != Type.OBJECT
                && tokenType.getSort() != Type.ARRAY) {
            throw new InvalidInjectionException(
                    this.info,
                    "@DirectExpression condition must return a reference token; "
                            + "null means invoke the original expression"
            );
        }

        Type[] invocationParameters =
                getInvocationParameters(invocation);

        /*
         * MixinExtras temporarily removes Sugar parameters such as @Local
         * before this injector runs. Therefore methodArgs contains:
         *
         * invocation receiver + invocation arguments
         * + optional captured target arguments
         */
        int capturedTargetArgs =
                validateConditionParameters(
                        target,
                        invocationParameters
                );

        ReplacementHandler replacement =
                resolveReplacementHandler(
                        tokenType,
                        expressionType
                );

        Type[] replacementArguments =
                Type.getArgumentTypes(replacement.desc);

        /*
         * Supported signatures:
         *
         * Result handler(Token)
         *
         * Result handler(Token, P1, P2, ...)
         */
        boolean forwardConditionParameters =
                replacementArguments.length > 1;

        int[] invocationLocals =
                allocateInvocationLocals(
                        target,
                        invocationParameters
                );

        int tokenLocal =
                target.allocateLocals(tokenType.getSize());

        InsnList before = new InsnList();
        LabelNode originalLabel = new LabelNode();
        LabelNode endLabel = new LabelNode();

        /*
         * At an invocation, its receiver and arguments are already on the
         * operand stack. Store them so that either branch can reload them.
         */
        storeInvocationParameters(
                before,
                invocationParameters,
                invocationLocals
        );

        /*
         * Token token = condition(
         *     invocationParameters,
         *     capturedTargetArguments,
         *     sugarParameters...
         * );
         *
         * Sugar parameters are restored later by MixinExtras.
         */
        pushConditionReceiver(before);

        loadInvocationParameters(
                before,
                invocationParameters,
                invocationLocals
        );

        pushCapturedTargetArgs(
                target,
                before,
                capturedTargetArgs
        );

        this.invokeHandler(before);

        before.add(new VarInsnNode(
                Opcodes.ASTORE,
                tokenLocal
        ));

        /*
         * if (token == null) {
         *     originalCall();
         * } else {
         *     replacement(token, ...);
         * }
         */
        before.add(new VarInsnNode(
                Opcodes.ALOAD,
                tokenLocal
        ));

        before.add(new JumpInsnNode(
                Opcodes.IFNULL,
                originalLabel
        ));

        replacement.pushReceiver(before);

        before.add(new VarInsnNode(
                Opcodes.ALOAD,
                tokenLocal
        ));

        /*
         * Token-only handlers receive no additional parameters.
         *
         * Forwarding handlers receive all non-Sugar condition parameters.
         * @Local values are consumed by the condition and represented by the
         * returned token.
         */
        if (forwardConditionParameters) {
            loadInvocationParameters(
                    before,
                    invocationParameters,
                    invocationLocals
            );

            pushCapturedTargetArgs(
                    target,
                    before,
                    capturedTargetArgs
            );
        }

        before.add(new MethodInsnNode(
                replacement.opcode,
                replacement.owner,
                replacement.name,
                replacement.desc,
                replacement.ownerInterface
        ));

        before.add(new JumpInsnNode(
                Opcodes.GOTO,
                endLabel
        ));

        /*
         * A null token executes the original invocation.
         */
        before.add(originalLabel);

        loadInvocationParameters(
                before,
                invocationParameters,
                invocationLocals
        );

        target.insertBefore(node, before);

        target.insns.insert(
                node.getCurrentTarget(),
                endLabel
        );

        int conditionStack =
                stackSize(this.methodArgs)
                        + (this.isStatic ? 0 : 1);

        int replacementStack =
                stackSize(replacementArguments)
                        + (replacement.isStatic ? 0 : 1);

        target.extendStack()
                .add(
                        Math.max(
                                conditionStack,
                                replacementStack
                        ) + 2
                )
                .apply();
    }

    private Type[] getInvocationParameters(
            MethodInsnNode invocation
    ) {
        Type[] arguments =
                Type.getArgumentTypes(invocation.desc);

        if (invocation.getOpcode() == Opcodes.INVOKESTATIC) {
            return arguments;
        }

        Type[] result =
                new Type[arguments.length + 1];

        result[0] =
                Type.getObjectType(invocation.owner);

        System.arraycopy(
                arguments,
                0,
                result,
                1,
                arguments.length
        );

        return result;
    }

    private int validateConditionParameters(
            Target target,
            Type[] invocationParameters
    ) {
        if (this.methodArgs.length < invocationParameters.length) {
            throw new InvalidInjectionException(
                    this.info,
                    "@DirectExpression condition must begin with the invocation "
                            + "receiver and arguments. Expected at least "
                            + Arrays.toString(invocationParameters)
                            + ", found "
                            + Arrays.toString(this.methodArgs)
            );
        }

        for (int i = 0; i < invocationParameters.length; i++) {
            Type found =
                    this.methodArgs[i];

            Type expected =
                    invocationParameters[i];

            if (!found.equals(expected)) {
                throw new InvalidInjectionException(
                        this.info,
                        "@DirectExpression condition parameter "
                                + i
                                + " has type "
                                + found
                                + ", expected "
                                + expected
                );
            }
        }

        int capturedTargetArgs =
                this.methodArgs.length
                        - invocationParameters.length;

        if (capturedTargetArgs > target.arguments.length) {
            throw new InvalidInjectionException(
                    this.info,
                    "@DirectExpression condition captures too many "
                            + "target arguments"
            );
        }

        for (int i = 0; i < capturedTargetArgs; i++) {
            Type found =
                    this.methodArgs[
                            invocationParameters.length + i
                            ];

            Type expected =
                    target.arguments[i];

            if (!found.equals(expected)) {
                throw new InvalidInjectionException(
                        this.info,
                        "@DirectExpression captured target argument "
                                + i
                                + " has type "
                                + found
                                + ", expected "
                                + expected
                );
            }
        }

        return capturedTargetArgs;
    }

    private ReplacementHandler resolveReplacementHandler(
            Type tokenType,
            Type expressionType
    ) {
        if (this.replacementHandler == null
                || this.replacementHandler.isEmpty()) {
            throw new InvalidInjectionException(
                    this.info,
                    "@DirectExpression requires a replacement handler"
            );
        }

        /*
         * Compact form:
         *
         * Result handler(Token)
         */
        String tokenOnlyDescriptor =
                Type.getMethodDescriptor(
                        expressionType,
                        tokenType
                );

        /*
         * Forwarding form:
         *
         * Result handler(Token, P1, P2, ...)
         *
         * P1...PN are the condition's non-Sugar parameters.
         */
        Type[] forwardedArguments =
                new Type[this.methodArgs.length + 1];

        forwardedArguments[0] = tokenType;

        System.arraycopy(
                this.methodArgs,
                0,
                forwardedArguments,
                1,
                this.methodArgs.length
        );

        String forwardedDescriptor =
                Type.getMethodDescriptor(
                        expressionType,
                        forwardedArguments
                );

        if (isExternalHandlerReference(this.replacementHandler)) {
            return parseExternalHandler(
                    this.replacementHandler,
                    tokenOnlyDescriptor,
                    forwardedDescriptor
            );
        }

        return findLocalHandler(
                this.replacementHandler,
                tokenOnlyDescriptor,
                forwardedDescriptor
        );
    }

    private ReplacementHandler findLocalHandler(
            String name,
            String tokenOnlyDescriptor,
            String forwardedDescriptor
    ) {
        List<MethodNode> matchingNames =
                new ArrayList<>();

        MethodNode forwardedMatch = null;

        for (MethodNode method : this.classNode.methods) {
            if (!method.name.equals(name)) {
                continue;
            }

            matchingNames.add(method);

            /*
             * Prefer the compact token-only form if both overloads exist.
             */
            if (method.desc.equals(tokenOnlyDescriptor)) {
                validateLocalReplacementModifiers(method);

                return ReplacementHandler.local(
                        this.classNode.name,
                        method
                );
            }

            if (method.desc.equals(forwardedDescriptor)) {
                forwardedMatch = method;
            }
        }

        if (forwardedMatch != null) {
            validateLocalReplacementModifiers(forwardedMatch);

            return ReplacementHandler.local(
                    this.classNode.name,
                    forwardedMatch
            );
        }

        throw new InvalidInjectionException(
                this.info,
                "Cannot find @DirectExpression replacement handler "
                        + name
                        + ". Expected either "
                        + name
                        + tokenOnlyDescriptor
                        + " or "
                        + name
                        + forwardedDescriptor
                        + ". Found methods with that name: "
                        + describeMethods(matchingNames)
        );
    }

    private ReplacementHandler parseExternalHandler(
            String reference,
            String tokenOnlyDescriptor,
            String forwardedDescriptor
    ) {
        int ownerEnd =
                reference.indexOf(';');

        int descriptorStart =
                reference.indexOf(
                        '(',
                        ownerEnd + 1
                );

        if (!reference.startsWith("L")
                || ownerEnd <= 1
                || descriptorStart <= ownerEnd + 1) {
            throw invalidExternalHandler(reference);
        }

        String owner =
                reference.substring(
                        1,
                        ownerEnd
                );

        String name =
                reference.substring(
                        ownerEnd + 1,
                        descriptorStart
                );

        String descriptor =
                reference.substring(descriptorStart);

        if (owner.isEmpty()
                || name.isEmpty()
                || name.equals("<init>")
                || name.equals("<clinit>")) {
            throw invalidExternalHandler(reference);
        }

        /*
         * JVM internal class names use '/' rather than '.'.
         */
        if (owner.indexOf('.') >= 0) {
            throw new InvalidInjectionException(
                    this.info,
                    "@DirectExpression external handler owner must use '/' "
                            + "instead of '.': "
                            + reference
            );
        }

        try {
            Type.getMethodType(descriptor);
        } catch (IllegalArgumentException exception) {
            throw new InvalidInjectionException(
                    this.info,
                    "@DirectExpression external handler has an invalid "
                            + "method descriptor: "
                            + reference
            );
        }

        if (!descriptor.equals(tokenOnlyDescriptor)
                && !descriptor.equals(forwardedDescriptor)) {
            throw new InvalidInjectionException(
                    this.info,
                    "@DirectExpression external replacement "
                            + reference
                            + " has an invalid signature. Expected either "
                            + "L"
                            + owner
                            + ";"
                            + name
                            + tokenOnlyDescriptor
                            + " or "
                            + "L"
                            + owner
                            + ";"
                            + name
                            + forwardedDescriptor
            );
        }

        /*
         * External handlers are emitted as INVOKESTATIC.
         *
         * The referenced source method must therefore be public static.
         */
        return ReplacementHandler.external(
                owner,
                name,
                descriptor
        );
    }

    private boolean isExternalHandlerReference(
            String handler
    ) {
        int ownerEnd =
                handler.indexOf(';');

        return handler.startsWith("L")
                && ownerEnd > 1
                && handler.indexOf(
                '(',
                ownerEnd + 1
        ) > ownerEnd;
    }

    private InvalidInjectionException invalidExternalHandler(
            String reference
    ) {
        return new InvalidInjectionException(
                this.info,
                "Invalid @DirectExpression external handler reference "
                        + reference
                        + ". Expected "
                        + "Lowner/Class;method(Arguments)ReturnType"
        );
    }

    private void validateLocalReplacementModifiers(
            MethodNode replacement
    ) {
        boolean replacementStatic =
                Bytecode.isStatic(replacement);

        if (replacementStatic != this.isStatic) {
            throw new InvalidInjectionException(
                    this.info,
                    "@DirectExpression condition and local replacement handler "
                            + "must both be static or both be instance methods"
            );
        }

        if (!replacementStatic
                && (replacement.access & Opcodes.ACC_PRIVATE) == 0) {
            throw new InvalidInjectionException(
                    this.info,
                    "Instance replacement handler must be private"
            );
        }
    }

    private int[] allocateInvocationLocals(
            Target target,
            Type[] invocationParameters
    ) {
        int[] locals =
                new int[invocationParameters.length];

        for (int i = 0; i < invocationParameters.length; i++) {
            locals[i] =
                    target.allocateLocals(
                            invocationParameters[i].getSize()
                    );
        }

        return locals;
    }

    private void storeInvocationParameters(
            InsnList code,
            Type[] parameters,
            int[] locals
    ) {
        for (int i = parameters.length - 1; i >= 0; i--) {
            Type type =
                    parameters[i];

            code.add(new VarInsnNode(
                    type.getOpcode(Opcodes.ISTORE),
                    locals[i]
            ));
        }
    }

    private void loadInvocationParameters(
            InsnList code,
            Type[] parameters,
            int[] locals
    ) {
        for (int i = 0; i < parameters.length; i++) {
            Type type =
                    parameters[i];

            code.add(new VarInsnNode(
                    type.getOpcode(Opcodes.ILOAD),
                    locals[i]
            ));
        }
    }

    private void pushCapturedTargetArgs(
            Target target,
            InsnList code,
            int capturedTargetArgs
    ) {
        this.pushArgs(
                target.arguments,
                code,
                target.getArgIndices(),
                0,
                capturedTargetArgs
        );
    }

    private void pushConditionReceiver(
            InsnList code
    ) {
        if (!this.isStatic) {
            code.add(new VarInsnNode(
                    Opcodes.ALOAD,
                    0
            ));
        }
    }

    private static int stackSize(
            Type[] types
    ) {
        int size = 0;

        for (Type type : types) {
            size += type.getSize();
        }

        return size;
    }

    private static String describeMethods(
            List<MethodNode> methods
    ) {
        if (methods.isEmpty()) {
            return "none";
        }

        StringBuilder result =
                new StringBuilder();

        for (MethodNode method : methods) {
            if (result.length() > 0) {
                result.append(", ");
            }

            result.append(method.name)
                    .append(method.desc);
        }

        return result.toString();
    }

    private static final class ReplacementHandler {
        private final String owner;
        private final String name;
        private final String desc;
        private final int opcode;
        private final boolean ownerInterface;
        private final boolean isStatic;

        private ReplacementHandler(
                String owner,
                String name,
                String desc,
                int opcode,
                boolean ownerInterface,
                boolean isStatic
        ) {
            this.owner = owner;
            this.name = name;
            this.desc = desc;
            this.opcode = opcode;
            this.ownerInterface = ownerInterface;
            this.isStatic = isStatic;
        }

        private static ReplacementHandler local(
                String owner,
                MethodNode method
        ) {
            boolean isStatic =
                    Bytecode.isStatic(method);

            return new ReplacementHandler(
                    owner,
                    method.name,
                    method.desc,
                    isStatic
                            ? Opcodes.INVOKESTATIC
                            : Opcodes.INVOKESPECIAL,
                    false,
                    isStatic
            );
        }

        private static ReplacementHandler external(
                String owner,
                String name,
                String descriptor
        ) {
            return new ReplacementHandler(
                    owner,
                    name,
                    descriptor,
                    Opcodes.INVOKESTATIC,
                    false,
                    true
            );
        }

        private void pushReceiver(
                InsnList code
        ) {
            if (!this.isStatic) {
                code.add(new VarInsnNode(
                        Opcodes.ALOAD,
                        0
                ));
            }
        }
    }
}
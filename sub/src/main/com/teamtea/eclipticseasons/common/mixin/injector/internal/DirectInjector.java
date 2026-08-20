package com.teamtea.eclipticseasons.common.mixin.injector.internal;

import com.teamtea.eclipticseasons.common.mixin.injector.DirectInject;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.spongepowered.asm.mixin.injection.code.Injector;
import org.spongepowered.asm.mixin.injection.struct.InjectionInfo;
import org.spongepowered.asm.mixin.injection.struct.InjectionNodes.InjectionNode;
import org.spongepowered.asm.mixin.injection.struct.Target;
import org.spongepowered.asm.mixin.injection.throwables.InvalidInjectionException;

import java.util.ArrayList;
import java.util.List;

public final class DirectInjector extends Injector {
    private final DirectInject.Mode mode;
    private final String returnHandlerSelector;

    public DirectInjector(
            InjectionInfo info,
            DirectInject.Mode mode,
            String returnHandlerSelector
    ) {
        super(info, "@DirectInject");
        this.mode = mode;
        this.returnHandlerSelector = returnHandlerSelector;
    }

    @Override
    protected void inject(
            Target target,
            InjectionNode node
    ) {
        this.checkTargetModifiers(target, false);
        this.validateMode(target);

        Type handlerReturnType = this.handlerReturnType(target);

        InjectorData handler =
                new InjectorData(target, "handler", false);

        this.validateParams(handler, handlerReturnType);

        ResolvedReturnHandler returnHandler = null;

        if (this.mode == DirectInject.Mode.RETURN_IF_TRUE) {
            returnHandler = this.resolveReturnHandler(target);
        }

        InsnList code = new InsnList();

        int guardStackSize = this.appendTargetArguments(
                target,
                code,
                handler.captureTargetArgs,
                this.isStatic
        );

        this.invokeHandler(code);

        int controlFlowStackSize = this.appendControlFlow(
                target,
                code,
                returnHandler
        );

        int requiredStack = Math.max(
                guardStackSize,
                Math.max(
                        handlerReturnType.getSize(),
                        controlFlowStackSize
                )
        );

        target.extendStack()
                .add(requiredStack)
                .apply();

        target.insertBefore(node, code);
    }

    private Type handlerReturnType(Target target) {
        switch (this.mode) {
            case CONTINUE:
                return Type.VOID_TYPE;

            case RETURN:
            case RETURN_WITH_CONTINUATION:
                return target.returnType;

            case RETURN_IF_TRUE:
            case CANCEL_IF_TRUE:
                return Type.BOOLEAN_TYPE;

            default:
                throw new AssertionError(this.mode);
        }
    }

    private void validateMode(Target target) {
        if (this.mode == DirectInject.Mode.CANCEL_IF_TRUE
                && target.returnType.getSort() != Type.VOID) {
            throw new InvalidInjectionException(
                    this.info,
                    "@DirectInject CANCEL_IF_TRUE requires a void target"
            );
        }

        if (this.mode == DirectInject.Mode.RETURN_IF_TRUE
                && target.returnType.getSort() == Type.VOID) {
            throw new InvalidInjectionException(
                    this.info,
                    "@DirectInject RETURN_IF_TRUE requires a non-void target"
            );
        }
    }

    private int appendTargetArguments(
            Target target,
            InsnList code,
            int argumentCount,
            boolean staticHandler
    ) {
        int stackSize = 0;

        if (!staticHandler) {
            code.add(new VarInsnNode(Opcodes.ALOAD, 0));
            stackSize++;
        }

        this.pushArgs(
                target.arguments,
                code,
                target.getArgIndices(),
                0,
                argumentCount
        );

        for (int i = 0; i < argumentCount; i++) {
            stackSize += target.arguments[i].getSize();
        }

        return stackSize;
    }

    private int appendControlFlow(
            Target target,
            InsnList code,
            ResolvedReturnHandler returnHandler
    ) {
        switch (this.mode) {
            case CONTINUE:
                return 0;

            case RETURN:
                code.add(new InsnNode(
                        target.returnType.getOpcode(Opcodes.IRETURN)
                ));
                return target.returnType.getSize();

            case RETURN_WITH_CONTINUATION:
                return this.appendReturnWithContinuation(
                        target,
                        code
                );

            case RETURN_IF_TRUE:
                return this.appendReturnIfTrue(
                        target,
                        code,
                        returnHandler
                );

            case CANCEL_IF_TRUE:
                this.appendCancelIfTrue(code);
                return 1;

            default:
                throw new AssertionError(this.mode);
        }
    }

    private int appendReturnWithContinuation(
            Target target,
            InsnList code
    ) {
        Type returnType = target.returnType;
        LabelNode continueLabel = new LabelNode();

        if (returnType.getSort() == Type.VOID) {
            code.add(new InsnNode(Opcodes.ICONST_1));
            code.add(new JumpInsnNode(
                    Opcodes.IFEQ,
                    continueLabel
            ));
            code.add(new InsnNode(Opcodes.RETURN));
            code.add(continueLabel);
            return 1;
        }

        int resultLocal = target.allocateLocals(
                returnType.getSize()
        );

        code.add(new VarInsnNode(
                returnType.getOpcode(Opcodes.ISTORE),
                resultLocal
        ));

        code.add(new InsnNode(Opcodes.ICONST_1));
        code.add(new JumpInsnNode(
                Opcodes.IFEQ,
                continueLabel
        ));

        code.add(new VarInsnNode(
                returnType.getOpcode(Opcodes.ILOAD),
                resultLocal
        ));
        code.add(new InsnNode(
                returnType.getOpcode(Opcodes.IRETURN)
        ));

        code.add(continueLabel);

        return Math.max(
                1,
                returnType.getSize()
        );
    }

    private int appendReturnIfTrue(
            Target target,
            InsnList code,
            ResolvedReturnHandler returnHandler
    ) {
        LabelNode continueLabel = new LabelNode();

        /*
         * The boolean guard result is currently on the operand stack.
         */
        code.add(new JumpInsnNode(
                Opcodes.IFEQ,
                continueLabel
        ));

        int stackSize = 0;

        if (!returnHandler.isStatic) {
            code.add(new VarInsnNode(Opcodes.ALOAD, 0));
            stackSize++;
        }

        this.pushArgs(
                target.arguments,
                code,
                target.getArgIndices(),
                0,
                returnHandler.argumentTypes.length
        );

        for (Type argumentType : returnHandler.argumentTypes) {
            stackSize += argumentType.getSize();
        }

        code.add(new MethodInsnNode(
                returnHandler.opcode,
                returnHandler.owner,
                returnHandler.name,
                returnHandler.descriptor,
                returnHandler.interfaceOwner
        ));

        code.add(new InsnNode(
                target.returnType.getOpcode(Opcodes.IRETURN)
        ));

        code.add(continueLabel);

        return Math.max(
                stackSize,
                target.returnType.getSize()
        );
    }

    private void appendCancelIfTrue(InsnList code) {
        LabelNode continueLabel = new LabelNode();

        code.add(new JumpInsnNode(
                Opcodes.IFEQ,
                continueLabel
        ));
        code.add(new InsnNode(Opcodes.RETURN));
        code.add(continueLabel);
    }

    private ResolvedReturnHandler resolveReturnHandler(
            Target target
    ) {
        if (this.isExternalSelector(this.returnHandlerSelector)) {
            return this.resolveExternalReturnHandler(
                    target,
                    this.returnHandlerSelector
            );
        }

        return this.resolveLocalReturnHandler(
                target,
                this.returnHandlerSelector
        );
    }

    private boolean isExternalSelector(String selector) {
        return selector.startsWith("L")
                && selector.indexOf(';') > 1
                && selector.indexOf('(') > selector.indexOf(';');
    }

    private ResolvedReturnHandler resolveExternalReturnHandler(
            Target target,
            String selector
    ) {
        int ownerEnd = selector.indexOf(';');
        int descriptorStart = selector.indexOf(
                '(',
                ownerEnd + 1
        );

        if (ownerEnd <= 1
                || descriptorStart <= ownerEnd + 1) {
            throw new InvalidInjectionException(
                    this.info,
                    "Invalid external returnHandler selector: "
                            + selector
            );
        }

        String owner = selector.substring(
                1,
                ownerEnd
        );

        String name = selector.substring(
                ownerEnd + 1,
                descriptorStart
        );

        String descriptor = selector.substring(
                descriptorStart
        );

        Type returnType;

        try {
            returnType = Type.getReturnType(descriptor);
        } catch (IllegalArgumentException exception) {
            throw new InvalidInjectionException(
                    this.info,
                    "Invalid external returnHandler descriptor: "
                            + selector,
                    exception
            );
        }

        if (!returnType.equals(target.returnType)) {
            throw new InvalidInjectionException(
                    this.info,
                    "@DirectInject external returnHandler returns "
                            + returnType
                            + ", expected "
                            + target.returnType
            );
        }

        Type[] argumentTypes;

        try {
            argumentTypes = Type.getArgumentTypes(descriptor);
        } catch (IllegalArgumentException exception) {
            throw new InvalidInjectionException(
                    this.info,
                    "Invalid external returnHandler descriptor: "
                            + selector,
                    exception
            );
        }

        this.validateArgumentPrefix(
                target,
                argumentTypes,
                "external returnHandler " + selector
        );

        /*
         * External handlers are deliberately restricted to static methods.
         * No receiver is available for an arbitrary external instance method.
         */
        return new ResolvedReturnHandler(
                owner,
                name,
                descriptor,
                argumentTypes,
                Opcodes.INVOKESTATIC,
                true,
                false
        );
    }

    private ResolvedReturnHandler resolveLocalReturnHandler(
            Target target,
            String name
    ) {
        List<MethodNode> matches = new ArrayList<>();

        for (MethodNode method : target.classNode.methods) {
            if (!method.name.equals(name)) {
                continue;
            }

            if (this.isCompatibleLocalReturnHandler(
                    target,
                    method
            )) {
                matches.add(method);
            }
        }

        if (matches.isEmpty()) {
            throw new InvalidInjectionException(
                    this.info,
                    "@DirectInject could not find compatible returnHandler "
                            + name
            );
        }

        if (matches.size() > 1) {
            throw new InvalidInjectionException(
                    this.info,
                    "@DirectInject found multiple compatible returnHandler methods named "
                            + name
            );
        }

        MethodNode method = matches.get(0);

        boolean staticMethod =
                (method.access & Opcodes.ACC_STATIC) != 0;

        boolean privateMethod =
                (method.access & Opcodes.ACC_PRIVATE) != 0;

        boolean interfaceOwner =
                (target.classNode.access & Opcodes.ACC_INTERFACE) != 0;

        int opcode;

        if (staticMethod) {
            opcode = Opcodes.INVOKESTATIC;
        } else if (privateMethod) {
            opcode = Opcodes.INVOKESPECIAL;
        } else if (interfaceOwner) {
            opcode = Opcodes.INVOKEINTERFACE;
        } else {
            opcode = Opcodes.INVOKEVIRTUAL;
        }

        return new ResolvedReturnHandler(
                target.classNode.name,
                method.name,
                method.desc,
                Type.getArgumentTypes(method.desc),
                opcode,
                staticMethod,
                interfaceOwner
        );
    }

    private boolean isCompatibleLocalReturnHandler(
            Target target,
            MethodNode method
    ) {
        Type returnType = Type.getReturnType(method.desc);

        if (!returnType.equals(target.returnType)) {
            return false;
        }

        boolean staticMethod =
                (method.access & Opcodes.ACC_STATIC) != 0;

        if (staticMethod != target.isStatic) {
            return false;
        }

        Type[] argumentTypes =
                Type.getArgumentTypes(method.desc);

        if (argumentTypes.length > target.arguments.length) {
            return false;
        }

        for (int i = 0; i < argumentTypes.length; i++) {
            if (!argumentTypes[i].equals(
                    target.arguments[i]
            )) {
                return false;
            }
        }

        return true;
    }

    private void validateArgumentPrefix(
            Target target,
            Type[] argumentTypes,
            String description
    ) {
        if (argumentTypes.length > target.arguments.length) {
            throw new InvalidInjectionException(
                    this.info,
                    "@DirectInject "
                            + description
                            + " captures too many target arguments"
            );
        }

        for (int i = 0; i < argumentTypes.length; i++) {
            if (!argumentTypes[i].equals(
                    target.arguments[i]
            )) {
                throw new InvalidInjectionException(
                        this.info,
                        "@DirectInject "
                                + description
                                + " argument "
                                + i
                                + " has type "
                                + argumentTypes[i]
                                + ", expected "
                                + target.arguments[i]
                );
            }
        }
    }

    private static final class ResolvedReturnHandler {
        private final String owner;
        private final String name;
        private final String descriptor;
        private final Type[] argumentTypes;
        private final int opcode;
        private final boolean isStatic;
        private final boolean interfaceOwner;

        private ResolvedReturnHandler(
                String owner,
                String name,
                String descriptor,
                Type[] argumentTypes,
                int opcode,
                boolean isStatic,
                boolean interfaceOwner
        ) {
            this.owner = owner;
            this.name = name;
            this.descriptor = descriptor;
            this.argumentTypes = argumentTypes;
            this.opcode = opcode;
            this.isStatic = isStatic;
            this.interfaceOwner = interfaceOwner;
        }
    }
}
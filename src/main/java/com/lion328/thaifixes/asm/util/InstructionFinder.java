package com.lion328.thaifixes.asm.util;

import com.lion328.thaifixes.asm.mapper.IClassDetail;
import com.lion328.thaifixes.asm.mapper.IClassMap;
import com.lion328.thaifixes.asm.mapper.IdentityClassMap;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.function.Function;

public class InstructionFinder implements InstructionMatcher {
    private ArrayList<InstructionMatcher> sequence = new ArrayList<>();
    private InstructionFinderRecord record;

    // TODO: how to reverse?
    private boolean reversed;
    private Object returnValue;

    private IClassMap classMap = IdentityClassMap.INSTANCE;
    private IClassDetail currentClassDetail;

    private void flush() {
        if (record != null) {
            if (record.desc != null)
                obfuscate();

            sequence.add(record);
            record = null;
        }
    }

    private void obfuscate() {
        if (record.type == AbstractInsnNode.FIELD_INSN)
            obfuscateField();
        else if (record.type == AbstractInsnNode.METHOD_INSN)
            obfuscateMethod();
    }

    private void obfuscateField() {
        if (record.name != null)
            record.name = currentClassDetail.getField(record.name);

        Type type = Type.getType(record.desc);

        if (type.getSort() == Type.OBJECT)
            type = Type.getObjectType(classMap.getClass(type.getClassName()).getObfuscatedName());

        record.desc = type.getDescriptor();
    }

    private void obfuscateMethod() {
        if (record.desc == null)
            return;

        if (record.name != null)
            record.name = currentClassDetail.getMethod(record.name, record.desc);

        Type type = Type.getMethodType(record.desc);
        Type[] argTypes = type.getArgumentTypes();
        Type returnType = type.getReturnType();

        if (returnType.getSort() == Type.OBJECT)
            returnType = Type.getObjectType(classMap.getClass(returnType.getClassName()).getObfuscatedName());

        for (int i = 0; i < argTypes.length; i++) {
            if (argTypes[i].getSort() == Type.OBJECT) {
                String obfuscated = classMap.getClass(argTypes[i].getClassName()).getObfuscatedName();
                argTypes[i] = Type.getObjectType(obfuscated);
            }
        }

        type = Type.getMethodType(returnType, argTypes);
        record.desc = type.getDescriptor();
    }

    public InstructionFinder field() {
        flush();
        record = new InstructionFinderRecord(AbstractInsnNode.FIELD_INSN);
        return this;
    }

    public InstructionFinder field(int opcode) {
        field();
        record.opcode = opcode;
        return this;
    }

    public InstructionFinder method() {
        flush();
        record = new InstructionFinderRecord(AbstractInsnNode.METHOD_INSN);
        return this;
    }

    public InstructionFinder method(int opcode) {
        method();
        record.opcode = opcode;
        return this;
    }

    public InstructionFinder var() {
        flush();
        record = new InstructionFinderRecord(AbstractInsnNode.VAR_INSN);
        return this;
    }

    public InstructionFinder var(int i) {
        var();
        record.opcode = i;
        return this;
    }

    public InstructionFinder owner(String owner) {
        if (record.owner != null || record.type != AbstractInsnNode.FIELD_INSN &&
                record.type != AbstractInsnNode.METHOD_INSN)
            throw new IllegalArgumentException();

        currentClassDetail = classMap.getClass(owner);
        record.owner = owner;
        return this;
    }

    public InstructionFinder name(String name) {
        if (record.name != null || record.type != AbstractInsnNode.FIELD_INSN &&
                record.type != AbstractInsnNode.METHOD_INSN)
            throw new IllegalArgumentException();

        record.name = name;
        return this;
    }

    public InstructionFinder desc(String desc) {
        if (record.desc != null)
            throw new IllegalArgumentException();
        if (record.type == AbstractInsnNode.METHOD_INSN && !desc.startsWith("("))
            throw new IllegalArgumentException();

        record.desc = desc;
        return this;
    }

    public InstructionFinder number(int i) {
        if (record.type != AbstractInsnNode.VAR_INSN)
            throw new IllegalArgumentException();
        record.var = i;
        return this;
    }

    public InstructionFinder ldc(Object obj) {
        flush();
        record = new InstructionFinderRecord(Opcodes.LDC);
        record.constant = obj;
        return this;
    }

    public InstructionFinder skip() {
        return skip(1);
    }

    public InstructionFinder skip(int count) {
        flush();
        for (int i = 0; i < count; i++)
            sequence.add(InstructionMatcher.SKIP_ONE);
        return this;
    }

    public InstructionFinder whenMatch(Consumer<AbstractInsnNode> fn) {
        record.callback = fn;
        return this;
    }

    public InstructionFinder group(Function<InstructionFinder, InstructionMatcher> lambda) {
        flush();

        InstructionFinder child = new InstructionFinder();
        child.classMap = classMap;
        child.reversed = reversed;

        sequence.add(lambda.apply(child));

        return this;
    }

    public InstructionFinder not(Function<InstructionFinder, InstructionMatcher> lambda) {
        return group(finder -> {
            InstructionMatcher matcher = lambda.apply(finder);
            return it -> !matcher.match(it);
        });
    }

    public boolean findFirst(InsnList list) {
        flush();

        for (int i = 0; i < list.size(); i++) {
            ListIterator<AbstractInsnNode> it = list.iterator(i);
            if (match(it)) {
                runCallback(list.iterator(i));
                return true;
            }
        }
        return false;
    }

    private void runCallback(ListIterator<AbstractInsnNode> it) {
        for (InstructionMatcher matcher : sequence)
            if (matcher instanceof InstructionFinderRecord)
                ((InstructionFinderRecord) matcher).runCallback(it.next());
    }

    @Override
    public boolean match(ListIterator<AbstractInsnNode> it) {
        for (InstructionMatcher matcher : sequence)
            if (!it.hasNext() || !matcher.match(it))
                return false;
        return true;
    }
}

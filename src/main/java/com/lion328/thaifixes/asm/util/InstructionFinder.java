package com.lion328.thaifixes.asm.util;

import com.lion328.thaifixes.asm.mapper.IClassDetail;
import com.lion328.thaifixes.asm.mapper.IClassMap;
import com.lion328.thaifixes.asm.mapper.IdentityClassMap;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;

public class InstructionFinder implements InstructionMatcher {
    private ArrayList<InstructionMatcher> sequence = new ArrayList<>();
    private InstructionFinderRecord record;

    private boolean reversed;
    private IClassMap classMap = IdentityClassMap.INSTANCE;
    private IClassDetail currentClassDetail;

    public InstructionFinder reversed() {
        reversed = !reversed;
        return this;
    }

    public InstructionFinder withClassMap(IClassMap classMap) {
        this.classMap = classMap;
        return this;
    }

    private void flush() {
        if (record != null) {
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

        if (record.owner != null)
            record.owner = currentClassDetail.getObfuscatedName();
    }

    private void obfuscateField() {
        if (record.name != null)
            record.name = currentClassDetail.getField(record.name);

        if (record.desc == null)
            return;

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

    private InstructionFinder newRecord(int type) {
        flush();
        record = new InstructionFinderRecord(type);
        return this;
    }

    public InstructionFinder field() {
        return newRecord(AbstractInsnNode.FIELD_INSN);
    }

    public InstructionFinder field(int opcode) {
        field();
        record.opcode = opcode;
        return this;
    }

    public InstructionFinder method() {
        return newRecord(AbstractInsnNode.METHOD_INSN);
    }

    public InstructionFinder method(int opcode) {
        method();
        record.opcode = opcode;
        return this;
    }

    public InstructionFinder var() {
        return newRecord(AbstractInsnNode.VAR_INSN);
    }

    public InstructionFinder var(int opcode) {
        var();
        record.opcode = opcode;
        return this;
    }

    public InstructionFinder ldc(Object obj) {
        newRecord(AbstractInsnNode.LDC_INSN);
        record.constant = obj;
        return this;
    }

    public InstructionFinder jump() {
        return newRecord(AbstractInsnNode.JUMP_INSN);
    }

    public InstructionFinder label() {
        return newRecord(AbstractInsnNode.LABEL);
    }

    public InstructionFinder insn() {
        return newRecord(AbstractInsnNode.INSN);
    }

    public InstructionFinder insn(int opcode) {
        newRecord(AbstractInsnNode.INSN);
        record.opcode = opcode;
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

    public InstructionFinder label(LabelNode node) {
        if (record.type != AbstractInsnNode.JUMP_INSN)
            throw new IllegalArgumentException();
        record.label = node;
        return this;
    }

    public InstructionFinder skip() {
        return skip(1);
    }

    public InstructionFinder skip(int count) {
        return skipCountedWithCondition(count, node -> true);
    }

    public InstructionFinder skipCountedWithCondition(int count, Function<AbstractInsnNode, Boolean> counted) {
        flush();
        sequence.add((it, cbs) -> {
            for (int i = 0; i < count; ) {
                if (!it.hasNext())
                    return false;
                if (counted.apply(it.next()))
                    i++;
            }
            return true;
        });
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
            return (it, cbs) -> !matcher.matchAndComputeCallback(it, Collections.emptyList());
        });
    }

    public InstructionFinder whenMatch(Consumer<AbstractInsnNode> fn) {
        record.addCallback(fn);
        return this;
    }

    public InstructionFinder whenMatchOnce(Consumer<AbstractInsnNode> fn) {
        Cell<Boolean> allowToCall = new Cell<>(true);
        return whenMatch(node -> {
            if (allowToCall.get()) {
                fn.accept(node);
                allowToCall.set(false);
            }
        });
    }

    private boolean find(InsnList list, AbstractInsnNode start, boolean once) {
        ArrayList<Runnable> callbacks = new ArrayList<>();

        IntFunction<Boolean> matchFn = i -> matchAndComputeCallback(list.iterator(i), callbacks);

        boolean matchSome = false;
        int i = list.indexOf(start);
        if (reversed) {
            for (; i >= 0; i--)
                if (matchFn.apply(i)) {
                    matchSome = true;
                    if (once)
                        break;
                }
        } else {
            for (; i < list.size(); i++)
                if (matchFn.apply(i)) {
                    matchSome = true;
                    if (once)
                        break;
                }
        }

        callbacks.forEach(Runnable::run);

        return matchSome;
    }

    public boolean findFirstStartFrom(InsnList list, AbstractInsnNode start) {
        return find(list, start, true);
    }

    public boolean findFirst(InsnList list) {
        return findFirstStartFrom(list, list.getFirst());
    }

    public boolean find(InsnList list, AbstractInsnNode start) {
        return find(list, start, false);
    }

    public boolean find(InsnList list) {
        return find(list, list.getFirst(), false);
    }

    @Override
    public boolean matchAndComputeCallback(ListIterator<AbstractInsnNode> it, List<Runnable> returnCallbacks) {
        flush();

        ArrayList<Runnable> callbacks = new ArrayList<>();

        for (InstructionMatcher matcher : sequence)
            if (!matcher.matchAndComputeCallback(it, callbacks))
                return false;

        returnCallbacks.addAll(callbacks);

        return true;
    }
}

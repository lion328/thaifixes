/*
 * Copyright (c) 2022 Waritnan Sookbuntherng
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.lion328.thaifixes.asm.util;

import com.lion328.thaifixes.asm.mapper.ClassDetail;
import com.lion328.thaifixes.asm.mapper.ClassMap;
import com.lion328.thaifixes.asm.mapper.IdentityClassMap;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;

public class InstructionFinder<T> implements InstructionMatcher {
    private final List<InstructionMatcher> sequence;
    private final InstructionFinderRecord record;

    private final boolean reversed;
    private final ClassMap classMap;
    private final ClassDetail currentClassDetail;
    private final String selfInternalName;

    private InstructionFinder(List<InstructionMatcher> sequence, InstructionFinderRecord record, boolean reversed,
                              ClassMap classMap, ClassDetail currentClassDetail, String selfInternalName) {
        this.sequence = sequence;
        this.record = record;
        this.reversed = reversed;
        this.classMap = classMap;
        this.currentClassDetail = currentClassDetail;
        this.selfInternalName = selfInternalName;
    }

    private InstructionFinder() {
        this(Collections.emptyList(), null, false, IdentityClassMap.INSTANCE, null, null);
    }

    public static InstructionFinder<Void> create() {
        return new InstructionFinder<>();
    }

    private InstructionFinder<Void> inheritAttributes() {
        return new InstructionFinder<>(Collections.emptyList(), null, reversed, classMap, null,
                selfInternalName);
    }

    private InstructionFinder<T> flush() {
        if (record != null) {
            InstructionFinder<T> o = obfuscate();
            return withRecord(null).addMatcher(o.record);
        }
        return this;
    }

    private <U> InstructionFinder<U> addMatcher(InstructionMatcher matcher) {
        InstructionFinder<T> o = flush();

        ArrayList<InstructionMatcher> sequence = new ArrayList<>(o.sequence);
        sequence.add(matcher);

        return new InstructionFinder<>(Collections.unmodifiableList(sequence), o.record, o.reversed, o.classMap,
                o.currentClassDetail, o.selfInternalName);
    }

    private <U> InstructionFinder<U> withRecord(InstructionFinderRecord record) {
        return new InstructionFinder<>(sequence, record, reversed, classMap, currentClassDetail, selfInternalName);
    }

    public InstructionFinder<T> reversed() {
        return new InstructionFinder<>(sequence, record, !reversed, classMap, currentClassDetail, selfInternalName);
    }

    public InstructionFinder<T> withClassMap(ClassMap classMap) {
        return new InstructionFinder<>(sequence, record, reversed, classMap, currentClassDetail, selfInternalName);
    }

    public InstructionFinder<T> withSelfInternalName(String selfInternalName) {
        return new InstructionFinder<>(sequence, record, reversed, classMap, currentClassDetail, selfInternalName);
    }

    private InstructionFinder<T> obfuscate() {
        if (record == null)
            throw new IllegalArgumentException();

        InstructionFinderRecord record = this.record;
        if (record.insnType == AbstractInsnNode.FIELD_INSN)
            record = obfuscateField();
        else if (record.insnType == AbstractInsnNode.METHOD_INSN)
            record = obfuscateMethod();
        else if (record.insnType == AbstractInsnNode.TYPE_INSN)
            record = obfuscateType();

        if (record.owner != null)
            record = record.withOwner(classMap.getClassFromInternalName(record.owner).getObfuscatedName());

        return withRecord(record);
    }

    private InstructionFinderRecord obfuscateField() {
        InstructionFinderRecord record = this.record;
        if (record.name != null)
            record = record.withName(currentClassDetail.getField(record.name));

        if (record.desc == null)
            return record;

        Type type = Type.getType(record.desc);

        if (type.getSort() == Type.OBJECT)
            type = Type.getObjectType(classMap.getClass(type.getClassName()).getObfuscatedName());

        record = record.withDescriptor(type.getDescriptor());
        return record;
    }

    private InstructionFinderRecord obfuscateMethod() {
        if (record.desc == null)
            return record;

        InstructionFinderRecord record = this.record;
        if (record.name != null)
            record.withName(currentClassDetail.getMethod(record.name, record.desc));

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
        record = record.withDescriptor(type.getDescriptor());

        return record;
    }

    private InstructionFinderRecord obfuscateType() {
        if (record.desc == null)
            return record;

        String newDesc = classMap.getClassFromInternalName(record.desc).getObfuscatedName();
        return record.withDescriptor(newDesc);
    }

    private <U> InstructionFinder<U> newRecord(int type) {
        return flush().withRecord(new InstructionFinderRecord(type));
    }

    public InstructionFinder<FieldInsnNode> field() {
        return newRecord(AbstractInsnNode.FIELD_INSN);
    }

    public InstructionFinder<FieldInsnNode> field(int opcode) {
        InstructionFinder<FieldInsnNode> newFinder = field();
        return newFinder.withRecord(newFinder.record.withOpcode(opcode));
    }

    public InstructionFinder<MethodInsnNode> method() {
        return newRecord(AbstractInsnNode.METHOD_INSN);
    }

    public InstructionFinder<MethodInsnNode> method(int opcode) {
        InstructionFinder<MethodInsnNode> newFinder = method();
        return newFinder.withRecord(newFinder.record.withOpcode(opcode));
    }

    public InstructionFinder<VarInsnNode> var() {
        return newRecord(AbstractInsnNode.VAR_INSN);
    }

    public InstructionFinder<VarInsnNode> var(int opcode) {
        InstructionFinder<VarInsnNode> newFinder = var();
        return newFinder.withRecord(newFinder.record.withOpcode(opcode));
    }

    public InstructionFinder<LdcInsnNode> ldc() {
        return newRecord(AbstractInsnNode.LDC_INSN);
    }

    public InstructionFinder<JumpInsnNode> jump() {
        return newRecord(AbstractInsnNode.JUMP_INSN);
    }

    public InstructionFinder<LabelNode> label() {
        return newRecord(AbstractInsnNode.LABEL);
    }

    public InstructionFinder<IntInsnNode> integer() {
        return newRecord(AbstractInsnNode.INT_INSN);
    }

    public InstructionFinder<IntInsnNode> integer(int opcode) {
        InstructionFinder<IntInsnNode> newFinder = integer();
        return newFinder.withRecord(newFinder.record.withOpcode(opcode));
    }

    public InstructionFinder<TypeInsnNode> type() {
        return newRecord(AbstractInsnNode.TYPE_INSN);
    }

    public InstructionFinder<TypeInsnNode> type(int opcode) {
        InstructionFinder<TypeInsnNode> newFinder = type();
        return newFinder.withRecord(newFinder.record.withOpcode(opcode));
    }

    public InstructionFinder<InsnNode> insn() {
        return newRecord(AbstractInsnNode.INSN);
    }

    public InstructionFinder<InsnNode> insn(int opcode) {
        InstructionFinder<InsnNode> newFinder = insn();
        return newFinder.withRecord(newFinder.record.withOpcode(opcode));
    }

    public InstructionFinder<T> owner(String owner) {
        if (record == null || record.owner != null || record.insnType != AbstractInsnNode.FIELD_INSN &&
                record.insnType != AbstractInsnNode.METHOD_INSN)
            throw new IllegalArgumentException();

        return new InstructionFinder<>(sequence, record.withOwner(owner), reversed, classMap, classMap.getClass(owner),
                selfInternalName);
    }

    public InstructionFinder<T> ownerSelf() {
        return owner(selfInternalName);
    }

    public InstructionFinder<T> name(String name) {
        if (record == null || record.name != null || record.insnType != AbstractInsnNode.FIELD_INSN &&
                record.insnType != AbstractInsnNode.METHOD_INSN)
            throw new IllegalArgumentException();

        return withRecord(record.withName(name));
    }

    public InstructionFinder<T> desc(String desc) {
        if (record == null || record.desc != null)
            throw new IllegalArgumentException();
        if (record.insnType == AbstractInsnNode.METHOD_INSN && !desc.startsWith("("))
            throw new IllegalArgumentException();

        return withRecord(record.withDescriptor(desc));
    }

    public InstructionFinder<T> number(int i) {
        if (record == null || record.insnType != AbstractInsnNode.VAR_INSN)
            throw new IllegalArgumentException();

        return withRecord(record.withVariableNumber(i));
    }

    public InstructionFinder<T> label(LabelNode node) {
        if (record == null || record.insnType != AbstractInsnNode.JUMP_INSN)
            throw new IllegalArgumentException();

        return withRecord(record.withLabel(node));
    }

    public InstructionFinder<T> value(Object v) {
        if (record == null || record.insnType != AbstractInsnNode.LDC_INSN &&
                record.insnType != AbstractInsnNode.INT_INSN)
            throw new IllegalArgumentException();

        return withRecord(record.withConstant(v));
    }

    public InstructionFinder<Void> skip() {
        return skip(1);
    }

    public InstructionFinder<Void> skip(int count) {
        return skipCountedWithCondition(count, node -> true);
    }

    public InstructionFinder<Void> skipCountedWithCondition(int count, Function<AbstractInsnNode, Boolean> counted) {
        return addMatcher((it, cbs) -> {
            for (int i = 0; i < count; ) {
                if (!it.hasNext())
                    return false;
                if (counted.apply(it.next()))
                    i++;
            }
            return true;
        });
    }

    public InstructionFinder<Void> not(Function<InstructionFinder<Void>, InstructionMatcher> lambda) {
        InstructionMatcher matcher = lambda.apply(inheritAttributes());
        return addMatcher((it, cbs) -> !matcher.matchAndComputeCallback(it, Collections.emptyList()));
    }

    public InstructionFinder<Void> or(Function<InstructionFinder<Void>, InstructionMatcher> left,
                                      Function<InstructionFinder<Void>, InstructionMatcher> right) {
        InstructionMatcher leftMatcher = left.apply(inheritAttributes());
        InstructionMatcher rightMatcher = right.apply(inheritAttributes());
        return addMatcher((it, returnCallback) -> {
            if (leftMatcher.matchAndComputeCallback(it, returnCallback))
                return true;
            return rightMatcher.matchAndComputeCallback(it, returnCallback);
        });
    }

    public InstructionFinder<T> whenMatch(Consumer<T> fn) {
        return withRecord(record.withCallbackAdded(node -> fn.accept((T) node)));
    }

    public InstructionFinder<T> whenMatchOnce(Consumer<T> fn) {
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

        InstructionFinder<T> finder = flush();
        IntFunction<Boolean> matchFn = i -> finder.matchAndComputeCallback(list.iterator(i), callbacks);

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
        InstructionFinder<T> finder = flush();

        ArrayList<Runnable> callbacks = new ArrayList<>();

        for (InstructionMatcher matcher : finder.sequence)
            if (!matcher.matchAndComputeCallback(it, callbacks))
                return false;

        returnCallbacks.addAll(callbacks);

        return true;
    }
}

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

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
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

class InstructionFinderRecord implements InstructionMatcher {
    public final int insnType;
    private final List<Consumer<AbstractInsnNode>> callbacks;
    public final Integer opcode;
    public final String owner;
    public final String name;
    public final String desc;
    public final Integer var;
    public final Object constant;
    public final LabelNode label;

    public InstructionFinderRecord(int insnType) {
        this.insnType = insnType;
        callbacks = Collections.emptyList();
        opcode = null;
        owner = null;
        name = null;
        desc = null;
        var = null;
        constant = null;
        label = null;
    }

    private InstructionFinderRecord(int insnType, List<Consumer<AbstractInsnNode>> callbacks, Integer opcode,
                                    String owner, String name, String desc, Integer var, Object constant,
                                    LabelNode label) {
        this.insnType = insnType;
        this.callbacks = callbacks;
        this.opcode = opcode;
        this.owner = owner;
        this.name = name;
        this.desc = desc;
        this.var = var;
        this.constant = constant;
        this.label = label;
    }

    public InstructionFinderRecord withCallbackAdded(Consumer<AbstractInsnNode> callback) {
        ArrayList<Consumer<AbstractInsnNode>> newCallbackList = new ArrayList<>(callbacks);
        newCallbackList.add(callback);
        return new InstructionFinderRecord(insnType, newCallbackList, opcode, owner, name, desc, var, constant, label);
    }

    public InstructionFinderRecord withOpcode(int opcode) {
        return new InstructionFinderRecord(insnType, callbacks, opcode, owner, name, desc, var, constant, label);
    }

    public InstructionFinderRecord withOwner(String owner) {
        return new InstructionFinderRecord(insnType, callbacks, opcode, owner, name, desc, var, constant, label);
    }

    public InstructionFinderRecord withName(String name) {
        return new InstructionFinderRecord(insnType, callbacks, opcode, owner, name, desc, var, constant, label);
    }

    public InstructionFinderRecord withDescriptor(String desc) {
        return new InstructionFinderRecord(insnType, callbacks, opcode, owner, name, desc, var, constant, label);
    }

    public InstructionFinderRecord withVariableNumber(int var) {
        return new InstructionFinderRecord(insnType, callbacks, opcode, owner, name, desc, var, constant, label);
    }

    public InstructionFinderRecord withConstant(Object constant) {
        return new InstructionFinderRecord(insnType, callbacks, opcode, owner, name, desc, var, constant, label);
    }

    public InstructionFinderRecord withLabel(LabelNode label) {
        return new InstructionFinderRecord(insnType, callbacks, opcode, owner, name, desc, var, constant, label);
    }

    private boolean match(AbstractInsnNode obj) {
        if (insnType != obj.getType())
            return false;
        if (opcode != null && !opcode.equals(obj.getOpcode()))
            return false;

        String objOwner = null;
        String objName = null;
        String objDesc = null;
        Integer objVar = null;

        if (obj instanceof FieldInsnNode) {
            FieldInsnNode node = (FieldInsnNode) obj;
            objOwner = node.owner;
            objName = node.name;
            objDesc = node.desc;
        } else if (obj instanceof MethodInsnNode) {
            MethodInsnNode node = (MethodInsnNode) obj;
            objOwner = node.owner;
            objName = node.name;
            objDesc = node.desc;
        } else if (obj instanceof VarInsnNode) {
            VarInsnNode node = (VarInsnNode) obj;
            objVar = node.var;
        } else if (obj instanceof LdcInsnNode) {
            LdcInsnNode node = (LdcInsnNode) obj;
            return constant == null || constant.equals(node.cst);
        } else if (obj instanceof JumpInsnNode) {
            JumpInsnNode node = (JumpInsnNode) obj;
            return label == null || label.equals(node.label);
        } else if (obj instanceof IntInsnNode) {
            IntInsnNode node = (IntInsnNode) obj;
            return constant == null || constant.equals(node.operand);
        } else if (obj instanceof TypeInsnNode) {
            TypeInsnNode node = (TypeInsnNode) obj;
            objDesc = node.desc;
        }

        if (owner != null && !owner.equals(objOwner))
            return false;
        if (name != null && !name.equals(objName))
            return false;
        if (desc != null && !desc.equals(objDesc))
            return false;
        if (var != null && !var.equals(objVar))
            return false;

        return true;
    }

    @Override
    public boolean matchAndComputeCallback(ListIterator<AbstractInsnNode> it, List<Runnable> returnCallbacks) {
        if (!it.hasNext())
            return false;

        AbstractInsnNode node = it.next();
        if (match(node)) {
            callbacks.forEach(cb -> returnCallbacks.add(() -> cb.accept(node)));
            return true;
        }

        return false;
    }
}

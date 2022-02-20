package com.lion328.thaifixes.asm.util;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;

public class InstructionFinderRecord implements InstructionMatcher {
    final int type;
    private List<Consumer<AbstractInsnNode>> callbacks = new ArrayList<>();
    Integer opcode;
    String owner;
    String name;
    String desc;
    Integer var;
    Object constant;
    LabelNode label;

    public InstructionFinderRecord(int type) {
        this.type = type;
    }

    public void addCallback(Consumer<AbstractInsnNode> callback) {
        callbacks.add(callback);
    }

    private boolean match(AbstractInsnNode obj) {
        if (type != obj.getType())
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

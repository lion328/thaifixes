package com.lion328.thaifixes.asm.util;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;
import java.util.function.Consumer;

public class InstructionFinderRecord implements InstructionMatcher {
    final int type;
    Integer opcode;
    String owner;
    String name;
    String desc;
    Integer var;
    Object constant;
    Consumer<AbstractInsnNode> callback = x -> {
    };

    public InstructionFinderRecord(int type) {
        this.type = type;
    }

    public InstructionFinderRecord(InstructionFinderRecord src) {
        type = src.type;
        opcode = src.opcode;
        owner = src.owner;
        name = src.name;
        desc = src.desc;
        var = src.var;
        callback = src.callback;
    }

    public void runCallback(AbstractInsnNode x) {
        callback.accept(x);
    }

    @Override
    public boolean match(ListIterator<AbstractInsnNode> it) {
        if (!it.hasNext())
            return false;

        AbstractInsnNode obj = it.next();

        if (type != obj.getType())
            return false;
        if (opcode != null && !opcode.equals(obj.getOpcode()))
            return false;

        String objOwner = null;
        String objName = null;
        String objDesc = null;
        Integer objVar = null;
        Object objConstant = null;

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
            objConstant = node.cst;
        }

        if (owner != null && !owner.equals(objOwner))
            return false;
        if (name != null && !name.equals(objName))
            return false;
        if (desc != null && !desc.equals(objDesc))
            return false;
        if (var != null && !var.equals(objVar))
            return false;
        if (constant != null && !constant.equals(objConstant))
            return false;

        return true;
    }
}

package com.lion328.thaifixes.coremod.patcher;

import com.lion328.thaifixes.coremod.mapper.IClassMap;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class GuiChatPatcher implements IClassPatcher
{

    private final IClassMap classMap;

    public GuiChatPatcher(IClassMap classMap)
    {
        this.classMap = classMap;
    }

    @Override
    public String getClassName()
    {
        return classMap.getClass("net/minecraft/client/gui/GuiChat").getObfuscatedName().replace('/', '.');
    }

    @Override
    public byte[] patch(byte[] original) throws Exception
    {
        ClassReader r = new ClassReader(original);
        ClassNode n = new ClassNode();
        r.accept(n, 0);

        for (MethodNode mn : n.methods)
        {
            InsnList insns = mn.instructions;

            for (int i = 0; i < insns.size(); i++)
            {
                AbstractInsnNode insn = insns.get(i);

                if (!replaceBipushWithConfig(insns, insn, 12, "getChatTextFieldHeight"))
                {
                    replaceBipushWithConfig(insns, insn, 14, "getChatTextFieldBoxHeight");
                }
            }
        }

        ClassWriter w = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        n.accept(w);

        return w.toByteArray();
    }

    private boolean replaceBipushWithConfig(InsnList insns, AbstractInsnNode insn, int b, String methodName)
    {
        if (insn.getOpcode() != Opcodes.BIPUSH)
        {
            return false;
        }

        IntInsnNode intInsn = (IntInsnNode) insn;

        if (intInsn.operand != b)
        {
            return false;
        }

        insns.insert(insn, GuiNewChatPatcher.getInjectedIntMethod(methodName));
        insns.remove(insn);

        return true;
    }
}

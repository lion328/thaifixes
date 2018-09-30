package com.lion328.thaifixes;

import net.minecraft.client.gui.fonts.IGlyphInfo;

public class HangingUnicodeThaiCharacterGlyphInfo implements IGlyphInfo
{

    private final IGlyphInfo parent;

    public HangingUnicodeThaiCharacterGlyphInfo(IGlyphInfo parent)
    {
        this.parent = parent;
    }

    @Override
    public float func_211577_d()
    {
        return parent.func_211577_d();
    }

    @Override
    public float func_211576_e()
    {
        return parent.func_211576_e();
    }

    @Override
    public float func_211198_f()
    {
        return parent.func_211198_f() - parent.func_211206_c();
    }

    @Override
    public float func_211199_g()
    {
        return parent.func_211199_g() - parent.func_211206_c();
    }

    @Override
    public float func_211200_h()
    {
        return parent.func_211200_h();
    }

    @Override
    public float func_211204_i()
    {
        return parent.func_211204_i();
    }

    @Override
    public float func_211574_l()
    {
        return parent.func_211574_l();
    }

    @Override
    public float func_211575_m()
    {
        return parent.func_211575_m();
    }

    @Override
    public int func_211202_a()
    {
        return parent.func_211202_a();
    }

    @Override
    public int func_211203_b()
    {
        return parent.func_211203_b();
    }

    @Override
    public float func_211206_c()
    {
        return 0.0f;
    }

    @Override
    public void func_211573_a(int i, int i1)
    {
        parent.func_211573_a(i, i1);
    }

    @Override
    public boolean func_211579_f()
    {
        return parent.func_211579_f();
    }

    @Override
    public float func_211578_g()
    {
        return parent.func_211578_g();
    }
}

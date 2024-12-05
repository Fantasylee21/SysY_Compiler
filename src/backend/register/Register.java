package backend.register;

public class Register {
    private RealRegister realRegister;
    private final String virtualReg;

    public Register(String virtualReg) {
        this.virtualReg = virtualReg;
        this.realRegister = null;
    }

    public Register(RealRegister realRegister, String virtualReg) {
        this.realRegister = realRegister;
        this.virtualReg = virtualReg;
    }

    public Register(RealRegister realRegister) {
        this.realRegister = realRegister;
        this.virtualReg = null;
    }

    public RealRegister getRealRegister() {
        return realRegister;
    }

    public String getVirtualReg() {
        return virtualReg;
    }

    public void setRealRegister(RealRegister realRegister) {
        this.realRegister = realRegister;
    }

    public boolean isRealRegister() {
        return realRegister != null;
    }

    @Override
    public String toString() {
        if (realRegister == null) {
            return virtualReg;
        }
        return realRegister.toString() ;
    }

    public static Register get$s(int i) {
        switch (i) {
            case 0:
                return new Register(RealRegister.S0);
            case 1:
                return new Register(RealRegister.S1);
            case 2:
                return new Register(RealRegister.S2);
            case 3:
                return new Register(RealRegister.S3);
            case 4:
                return new Register(RealRegister.S4);
            case 5:
                return new Register(RealRegister.S5);
            case 6:
                return new Register(RealRegister.S6);
            case 7:
                return new Register(RealRegister.S7);
            default:
                return null;
        }
    }

    public static Register get$a(int i) {
        switch (i) {
            case 0:
                return new Register(RealRegister.A0);
            case 1:
                return new Register(RealRegister.A1);
            case 2:
                return new Register(RealRegister.A2);
            case 3:
                return new Register(RealRegister.A3);
            default:
                return null;
        }
    }

    public static Register get$t(int i) {
        switch (i) {
            case 0:
                return new Register(RealRegister.T0);
            case 1:
                return new Register(RealRegister.T1);
            case 2:
                return new Register(RealRegister.T2);
            case 3:
                return new Register(RealRegister.T3);
            case 4:
                return new Register(RealRegister.T4);
            case 5:
                return new Register(RealRegister.T5);
            case 6:
                return new Register(RealRegister.T6);
            case 7:
                return new Register(RealRegister.T7);
            case 8:
                return new Register(RealRegister.T8);
            case 9:
                return new Register(RealRegister.T9);
            default:
                return null;
        }
    }

    public static Register get$zero() {
        return new Register(RealRegister.ZERO);
    }

    public static Register get$at() {
        return new Register(RealRegister.AT);
    }

    public static Register get$v0() {
        return new Register(RealRegister.V0);
    }

    public static Register get$v1() {
        return new Register(RealRegister.V1);
    }

    public static Register get$a0() {
        return new Register(RealRegister.A0);
    }

    public static Register get$a1() {
        return new Register(RealRegister.A1);
    }

    public static Register get$a2() {
        return new Register(RealRegister.A2);
    }

    public static Register get$a3() {
        return new Register(RealRegister.A3);
    }

    public static Register get$t0() {
        return new Register(RealRegister.T0);
    }

    public static Register get$t1() {
        return new Register(RealRegister.T1);
    }

    public static Register get$t2() {
        return new Register(RealRegister.T2);
    }

    public static Register get$t3() {
        return new Register(RealRegister.T3);
    }

    public static Register get$t4() {
        return new Register(RealRegister.T4);
    }

    public static Register get$t5() {
        return new Register(RealRegister.T5);
    }

    public static Register get$t6() {
        return new Register(RealRegister.T6);
    }

    public static Register get$t7() {
        return new Register(RealRegister.T7);
    }

    public static Register get$s0() {
        return new Register(RealRegister.S0);
    }

    public static Register get$s1() {
        return new Register(RealRegister.S1);
    }

    public static Register get$s2() {
        return new Register(RealRegister.S2);
    }

    public static Register get$s3() {
        return new Register(RealRegister.S3);
    }

    public static Register get$s4() {
        return new Register(RealRegister.S4);
    }

    public static Register get$s5() {
        return new Register(RealRegister.S5);
    }

    public static Register get$s6() {
        return new Register(RealRegister.S6);
    }

    public static Register get$s7() {
        return new Register(RealRegister.S7);
    }

    public static Register get$t8() {
        return new Register(RealRegister.T8);
    }

    public static Register get$t9() {
        return new Register(RealRegister.T9);
    }

    public static Register get$k0() {
        return new Register(RealRegister.K0);
    }

    public static Register get$k1() {
        return new Register(RealRegister.K1);
    }

    public static Register get$gp() {
        return new Register(RealRegister.GP);
    }

    public static Register get$sp() {
        return new Register(RealRegister.SP);
    }

    public static Register get$fp() {
        return new Register(RealRegister.FP);
    }

    public static Register get$ra() {
        return new Register(RealRegister.RA);
    }

}

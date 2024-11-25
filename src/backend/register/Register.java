package backend.register;

import backend.MipsBuilder;

public class Register {
    private RealRegister realRegister;
    private final String virtualReg;
//    private static final Register $zero = new Register(RealRegister.ZERO);
//    private static final Register $at = new Register(RealRegister.AT);
//    private static final Register $v0 = new Register(RealRegister.V0);
//    private static final Register $v1 = new Register(RealRegister.V1);
//    private static final Register $a0 = new Register(RealRegister.A0);
//    private static final Register $a1 = new Register(RealRegister.A1);
//    private static final Register $a2 = new Register(RealRegister.A2);
//    private static final Register $a3 = new Register(RealRegister.A3);
//    private static final Register $t0 = new Register(RealRegister.T0);
//    private static final Register $t1 = new Register(RealRegister.T1);
//    private static final Register $t2 = new Register(RealRegister.T2);
//    private static final Register $t3 = new Register(RealRegister.T3);
//    private static final Register $t4 = new Register(RealRegister.T4);
//    private static final Register $t5 = new Register(RealRegister.T5);
//    private static final Register $t6 = new Register(RealRegister.T6);
//    private static final Register $t7 = new Register(RealRegister.T7);
//    private static final Register $s0 = new Register(RealRegister.S0);
//    private static final Register $s1 = new Register(RealRegister.S1);
//    private static final Register $s2 = new Register(RealRegister.S2);
//    private static final Register $s3 = new Register(RealRegister.S3);
//    private static final Register $s4 = new Register(RealRegister.S4);
//    private static final Register $s5 = new Register(RealRegister.S5);
//    private static final Register $s6 = new Register(RealRegister.S6);
//    private static final Register $s7 = new Register(RealRegister.S7);
//    private static final Register $t8 = new Register(RealRegister.T8);
//    private static final Register $t9 = new Register(RealRegister.T9);
//    private static final Register $k0 = new Register(RealRegister.K0);
//    private static final Register $k1 = new Register(RealRegister.K1);
//    private static final Register $gp = new Register(RealRegister.GP);
//    private static final Register $sp = new Register(RealRegister.SP);
//    private static final Register $fp = new Register(RealRegister.FP);
//    private static final Register $ra = new Register(RealRegister.RA);

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


//    public static Register get$at() {
//        return $at;
//    }
//
//    public static Register get$v0() {
//        return $v0;
//    }
//
//    public static Register get$v1() {
//        return $v1;
//    }
//
//    public static Register get$a0() {
//        return $a0;
//    }
//
//    public static Register get$a1() {
//        return $a1;
//    }
//
//    public static Register get$a2() {
//        return $a2;
//    }
//
//    public static Register get$a3() {
//        return $a3;
//    }
//
//    public static Register get$t0() {
//        return $t0;
//    }
//
//    public static Register get$t1() {
//        return $t1;
//    }
//
//    public static Register get$t2() {
//        return $t2;
//    }
//
//    public static Register get$t3() {
//        return $t3;
//    }
//
//    public static Register get$t4() {
//        return $t4;
//    }
//
//    public static Register get$t5() {
//        return $t5;
//    }
//
//    public static Register get$t6() {
//        return $t6;
//    }
//
//    public static Register get$t7() {
//        return $t7;
//    }
//
//    public static Register get$s0() {
//        return $s0;
//    }
//
//    public static Register get$s1() {
//        return $s1;
//    }
//
//    public static Register get$s2() {
//        return $s2;
//    }
//
//    public static Register get$s3() {
//        return $s3;
//    }
//
//    public static Register get$s4() {
//        return $s4;
//    }
//
//    public static Register get$s5() {
//        return $s5;
//    }
//
//    public static Register get$s6() {
//        return $s6;
//    }
//
//    public static Register get$s7() {
//        return $s7;
//    }
//
//    public static Register get$t8() {
//        return $t8;
//    }
//
//    public static Register get$t9() {
//        return $t9;
//    }
//
//    public static Register get$k0() {
//        return $k0;
//    }
//
//    public static Register get$k1() {
//        return $k1;
//    }
//
//    public static Register get$gp() {
//        return $gp;
//    }
//
//    public static Register get$sp() {
//        return $sp;
//    }
//
//    public static Register get$fp() {
//        return $fp;
//    }
//
//    public static Register get$ra() {
//        return $ra;
//    }


}

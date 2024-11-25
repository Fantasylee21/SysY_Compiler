package backend.register;

public enum RealRegister {
    ZERO,
    AT,
    V0,
    V1,
    A0,
    A1,
    A2,
    A3,
    T0,
    T1,
    T2,
    T3,
    T4,
    T5,
    T6,
    T7,
    S0,
    S1,
    S2,
    S3,
    S4,
    S5,
    S6,
    S7,
    T8,
    T9,
    K0,
    K1,
    GP,
    SP,
    FP,
    RA;

//    映射到数字
    public int toNumber() {
        switch (this) {
            case ZERO:
                return 0;
            case AT:
                return 1;
            case V0:
                return 2;
            case V1:
                return 3;
            case A0:
                return 4;
            case A1:
                return 5;
            case A2:
                return 6;
            case A3:
                return 7;
            case T0:
                return 8;
            case T1:
                return 9;
            case T2:
                return 10;
            case T3:
                return 11;
            case T4:
                return 12;
            case T5:
                return 13;
            case T6:
                return 14;
            case T7:
                return 15;
            case S0:
                return 16;
            case S1:
                return 17;
            case S2:
                return 18;
            case S3:
                return 19;
            case S4:
                return 20;
            case S5:
                return 21;
            case S6:
                return 22;
            case S7:
                return 23;
            case T8:
                return 24;
            case T9:
                return 25;
            case K0:
                return 26;
            case K1:
                return 27;
            case GP:
                return 28;
            case SP:
                return 29;
            case FP:
                return 30;
            case RA:
                return 31;
            default:
                return -1;
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case ZERO:
                return "$zero";
            case AT:
                return "$at";
            case V0:
                return "$v0";
            case V1:
                return "$v1";
            case A0:
                return "$a0";
            case A1:
                return "$a1";
            case A2:
                return "$a2";
            case A3:
                return "$a3";
            case T0:
                return "$t0";
            case T1:
                return "$t1";
            case T2:
                return "$t2";
            case T3:
                return "$t3";
            case T4:
                return "$t4";
            case T5:
                return "$t5";
            case T6:
                return "$t6";
            case T7:
                return "$t7";
            case S0:
                return "$s0";
            case S1:
                return "$s1";
            case S2:
                return "$s2";
            case S3:
                return "$s3";
            case S4:
                return "$s4";
            case S5:
                return "$s5";
            case S6:
                return "$s6";
            case S7:
                return "$s7";
            case T8:
                return "$t8";
            case T9:
                return "$t9";
            case K0:
                return "$k0";
            case K1:
                return "$k1";
            case GP:
                return "$gp";
            case SP:
                return "$sp";
            case FP:
                return "$fp";
            case RA:
                return "$ra";
            default:
                return null;
        }
    }
}

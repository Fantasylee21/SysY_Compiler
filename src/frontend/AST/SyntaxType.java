package frontend.AST;

public enum SyntaxType {
    CompUnit,
    MainFuncDef,
    //const
    ConstDecl,
    ConstDef,
    ConstInitVal,
    //var
    VarDecl,
    VarDef,
    InitVal,

    //func
    FuncDef,
    FuncType,
    FuncFParams,
    FuncFParam,
    FuncRParams,

    Block,
    //stmt
    Stmt,
    ForStmt,
    //assign
    //exp
    PrimaryExp,

    LVal,

    Number,
    Character,

    UnaryExp,
    UnaryOp,

    Exp,
    Cond,

    AddExp,
    MulExp,
    RelExp,
    EqExp,
    LAndExp,
    LOrExp,
    ConstExp,

    Token,
}

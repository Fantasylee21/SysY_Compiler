package frontend;

import frontend.AST.Node;
import frontend.AST.SyntaxComponent.*;
import frontend.AST.SyntaxComponent.Character;
import frontend.AST.SyntaxComponent.Number;
import frontend.AST.SyntaxType;
import frontend.Token.Token;
import frontend.Token.TokenType;

import java.util.ArrayList;
import java.util.Comparator;

public class Parser {
    private final Lexer lexer;
    private int curTokenIndex;
    private final ArrayList<Error> errors;
    private Token curToken;
    private int curLineNum;

    public Parser(Lexer lexer, ArrayList<Error> errors) {
        this.lexer = lexer;
        this.curTokenIndex = 0;
        this.errors = errors;
        this.curToken = lexer.getToken(curTokenIndex);
        this.curLineNum = 1;
    }

    public void addTokenNode(ArrayList<Node> children, int curLineNum) {
        Node node = new TokenNode(curLineNum, curLineNum, SyntaxType.Token, null, curToken);
        children.add(node);
        curTokenIndex++;
        curToken = lexer.getToken(curTokenIndex);
        this.curLineNum = curToken.getLineNum();
    }

    public boolean hasError() {
        return !errors.isEmpty();
    }

    public void printErrors() {
        errors.sort(Comparator.comparingInt(Error::getLineNum));
        for (Error error : errors) {
            System.out.println(error.getLineNum() + " " + error.getMessage());
        }
    }

    public Node parse() {
        return parseCompUnit();
    }

    public boolean isUnaryOp() {
        return curToken.getType() == TokenType.PLUS || curToken.getType() == TokenType.MINU || curToken.getType() == TokenType.NOT;
    }

    public boolean isExpFirst() {
        return curToken.getType() == TokenType.LPARENT || curToken.getType() == TokenType.IDENFR || curToken.getType() == TokenType.INTCON || curToken.getType() == TokenType.CHRCON || isUnaryOp();
    }

    // CompUnit  ==>  {ConstDecl | VarDecl } {FuncDef} MainFuncDef
    public Node parseCompUnit() {
        ArrayList<Node> children = new ArrayList<>();
        
        Node node;
        while (lexer.getToken(curTokenIndex).getType() == TokenType.CONSTTK || lexer.getToken(curTokenIndex).getType() == TokenType.INTTK || lexer.getToken(curTokenIndex).getType() == TokenType.CHARTK || lexer.getToken(curTokenIndex).getType() == TokenType.VOIDTK) {
            if (lexer.getToken(curTokenIndex + 1).getType() == TokenType.MAINTK && lexer.getToken(curTokenIndex).getType() == TokenType.INTTK) {
                node = parseMainFuncDef();
            } else if (lexer.getToken(curTokenIndex + 2).getType() == TokenType.LPARENT) {
                node = parseFuncDef();
            } else if (lexer.getToken(curTokenIndex).getType() == TokenType.CONSTTK) {
                node = parseConstDecl();
            } else {
                node = parseVarDecl();
            }
            children.add(node);
        }
        int endLine = lexer.getToken(curTokenIndex - 1).getLineNum();
        return new CompUnit(curLineNum, endLine, SyntaxType.CompUnit, children);
    }

    // MainFuncDef ==> 'int' 'main' '(' ')' Block
    public Node parseMainFuncDef() {
        ArrayList<Node> children = new ArrayList<>();
        
        Node node;
        if (curToken.getType() == TokenType.INTTK) {
            addTokenNode(children, curLineNum);
        } else {
            throw new RuntimeException("Expect 'int' in MainFuncDef");
        }

        if (curToken.getType() == TokenType.MAINTK) {
            addTokenNode(children, curLineNum);
        } else {
            throw new RuntimeException("Expect 'main' in MainFuncDef");
        }
        if (lexer.getToken(curTokenIndex).getType() == TokenType.LPARENT) {
            addTokenNode(children, curLineNum);
            if (lexer.getToken(curTokenIndex).getType() == TokenType.RPARENT) {
                addTokenNode(children, curLineNum);
            } else {
                errors.add(new Error(lexer.getToken(curTokenIndex - 1).getLineNum(), "j"));
            }
        } else {
            throw new RuntimeException("Expect '(' in MainFuncDef");
        }
        node = parseBlock();
        children.add(node);
        int endLine = lexer.getToken(curTokenIndex - 1).getLineNum();
        return new MainFuncDef(curLineNum, endLine, SyntaxType.MainFuncDef, children);
    }

    // VarDecl ==> 'int'|'char' VarDef { ',' VarDef } ';'
    public Node parseVarDecl() {
        ArrayList<Node> children = new ArrayList<>();
        
        Node node;
        if (curToken.getType() == TokenType.INTTK || curToken.getType() == TokenType.CHARTK) {
            addTokenNode(children, curLineNum);
        } else {
            throw new RuntimeException("Expect 'int' or 'char' in VarDecl");
        }
        node = parseVarDef();
        children.add(node);

        while (lexer.getToken(curTokenIndex).getType() == TokenType.COMMA) {
            addTokenNode(children, curLineNum);

            node = parseVarDef();
            children.add(node);
        }

        if (lexer.getToken(curTokenIndex).getType() == TokenType.SEMICN) {
            addTokenNode(children, curLineNum);
        } else {
            errors.add(new Error(lexer.getToken(curTokenIndex - 1).getLineNum(), "i"));
        }

        int endLine = lexer.getToken(curTokenIndex - 1).getLineNum();
        return new VarDecl(curLineNum, endLine, SyntaxType.VarDecl, children);
    }

    //VarDef  ==> Ident [ '[' ConstExp ']' ] [ '=' InitVal]
    public Node parseVarDef() {
        ArrayList<Node> children = new ArrayList<>();
        
        Node node;
        if (curToken.getType() == TokenType.IDENFR) {
            addTokenNode(children, curLineNum);
        } else {
            throw new RuntimeException("Expect 'Ident' in VarDef");
        }

        if (lexer.getToken(curTokenIndex).getType() == TokenType.LBRACK) {
            addTokenNode(children, curLineNum);

            node = parseConstExp();
            children.add(node);

            if (lexer.getToken(curTokenIndex).getType() == TokenType.RBRACK) {
                addTokenNode(children, curLineNum);
            } else {
                errors.add(new Error(lexer.getToken(curTokenIndex - 1).getLineNum(), "k"));
            }
        }

        if (lexer.getToken(curTokenIndex).getType() == TokenType.ASSIGN) {
            addTokenNode(children, curLineNum);

            node = parseInitVal();
            children.add(node);
        }

        int endLine = lexer.getToken(curTokenIndex - 1).getLineNum();
        return new VarDef(curLineNum, endLine, SyntaxType.VarDef, children);
    }

    // ConstExp ==> AddExp
    public Node parseConstExp() {
        ArrayList<Node> children = new ArrayList<>();
        
        Node node = parseAddExp();
        children.add(node);
        int endLine = lexer.getToken(curTokenIndex - 1).getLineNum();
        return new ConstExp(curLineNum, endLine, SyntaxType.ConstExp, children);
    }

    // InitVal ==> Exp | '{' [ Exp { ',' Exp } ] '}' | StringConst
    public Node parseInitVal() {
        ArrayList<Node> children = new ArrayList<>();
        
        Node node;
        if (curToken.getType() == TokenType.LBRACE) {
            addTokenNode(children, curLineNum);

            if (isExpFirst()) {
                node = parseExp();
                children.add(node);

                while (lexer.getToken(curTokenIndex).getType() == TokenType.COMMA) {
                    addTokenNode(children, curLineNum);

                    node = parseExp();
                    children.add(node);
                }
            }

            if (lexer.getToken(curTokenIndex).getType() == TokenType.RBRACE) {
                addTokenNode(children, curLineNum);
            }
        } else if (curToken.getType() == TokenType.STRCON) {
            addTokenNode(children, curLineNum);
        } else {
            node = parseExp();
            children.add(node);
        }

        int endLine = lexer.getToken(curTokenIndex - 1).getLineNum();
        return new InitVal(curLineNum, endLine, SyntaxType.InitVal, children);
    }

    // Exp ==> AddExp
    public Node parseExp() {
        ArrayList<Node> children = new ArrayList<>();
        
        Node node = parseAddExp();
        children.add(node);
        int endLine = lexer.getToken(curTokenIndex - 1).getLineNum();
        return new Exp(curLineNum, endLine, SyntaxType.Exp, children);
    }

    // AddExp ==> MulExp {('+' | '-') MulExp}
    // AddExp ==> MulExp | MulExp ('+' | '-') AddExp
    public Node parseAddExp() {
        ArrayList<Node> children = new ArrayList<>();
        
        Node node = parseMulExp();
        children.add(node);

        while (lexer.getToken(curTokenIndex).getType() == TokenType.PLUS || lexer.getToken(curTokenIndex).getType() == TokenType.MINU) {
            addTokenNode(children, curLineNum);
            node = parseMulExp();
            children.add(node);
        }
//        if (lexer.getToken(curTokenIndex).getType() == TokenType.PLUS || lexer.getToken(curTokenIndex).getType() == TokenType.MINU) {
//            addTokenNode(children, curLineNum);
//            node = parseAddExp();
//            children.add(node);
//        }

        int endLine = lexer.getToken(curTokenIndex - 1).getLineNum();
        return new AddExp(curLineNum, endLine, SyntaxType.AddExp, children);
    }

    // MulExp ==> UnaryExp {('*' | '/' | '%') UnaryExp}
    // MulExp ==> UnaryExp | UnaryExp ('*' | '/' | '%') MulExp
    public Node parseMulExp() {
        ArrayList<Node> children = new ArrayList<>();

        Node node = parseUnaryExp();
        children.add(node);

        while (lexer.getToken(curTokenIndex).getType() == TokenType.MULT || lexer.getToken(curTokenIndex).getType() == TokenType.DIV || lexer.getToken(curTokenIndex).getType() == TokenType.MOD) {
            addTokenNode(children, curLineNum);

            node = parseUnaryExp();
            children.add(node);
        }
//        if (lexer.getToken(curTokenIndex).getType() == TokenType.MULT || lexer.getToken(curTokenIndex).getType() == TokenType.DIV || lexer.getToken(curTokenIndex).getType() == TokenType.MOD) {
//            addTokenNode(children, curLineNum);
//            node = parseMulExp();
//            children.add(node);
//
//        }

        int endLine = lexer.getToken(curTokenIndex - 1).getLineNum();
        return new MulExp(curLineNum, endLine, SyntaxType.MulExp, children);
    }

    // UnaryExp ==> PrimaryExp | Ident '(' [FuncRealParams] ')' | UnaryOp UnaryExp
    public Node parseUnaryExp() {
        ArrayList<Node> children = new ArrayList<>();
        
        Node node;
        if (curToken.getType() == TokenType.IDENFR && lexer.getToken(curTokenIndex + 1).getType() == TokenType.LPARENT) {
            addTokenNode(children, curLineNum);

            if (lexer.getToken(curTokenIndex).getType() == TokenType.LPARENT) {
                addTokenNode(children, curLineNum);

                if (isExpFirst()) {
                    node = parseFuncRParams();
                    children.add(node);
                }

                if (lexer.getToken(curTokenIndex).getType() == TokenType.RPARENT) {
                    addTokenNode(children, curLineNum);
                } else {
                    errors.add(new Error(lexer.getToken(curTokenIndex - 1).getLineNum(), "j"));
                }
            } else {
                throw new RuntimeException("Expect '(' in UnaryExp");
            }
        } else if (curToken.getType() == TokenType.PLUS || curToken.getType() == TokenType.MINU || curToken.getType() == TokenType.NOT) {
            node = parseUnaryOp();
            children.add(node);

            node = parseUnaryExp();
            children.add(node);
        } else {
            node = parsePrimaryExp();
            children.add(node);
        }
        int endLine = lexer.getToken(curTokenIndex - 1).getLineNum();
        return new UnaryExp(curLineNum, endLine, SyntaxType.UnaryExp, children);
    }

    // UnaryOp ==> '+' | '-' | '!'
    public Node parseUnaryOp() {
        ArrayList<Node> children = new ArrayList<>();
        
        if (curToken.getType() == TokenType.PLUS || curToken.getType() == TokenType.MINU || curToken.getType() == TokenType.NOT) {
            addTokenNode(children, curLineNum);
        } else {
            throw new RuntimeException("Expect '+' or '-' or '!' in UnaryOp");
        }

        int endLine = lexer.getToken(curTokenIndex - 1).getLineNum();
        return new UnaryOp(curLineNum, endLine, SyntaxType.UnaryOp, children);
    }

    // FuncRParams ==> Exp { ',' Exp }
    public Node parseFuncRParams() {
        ArrayList<Node> children = new ArrayList<>();
        
        Node node = parseExp();
        children.add(node);
        while (lexer.getToken(curTokenIndex).getType() == TokenType.COMMA) {
            addTokenNode(children, curLineNum);

            node = parseExp();
            children.add(node);
        }
        int endLine = lexer.getToken(curTokenIndex - 1).getLineNum();
        return new FuncRParams(curLineNum, endLine, SyntaxType.FuncRParams, children);
    }

    // LVal ==> Ident ['[' Exp ']']
    public Node parseLVal() {
        ArrayList<Node> children = new ArrayList<>();
        
        Node node;
        if (curToken.getType() == TokenType.IDENFR) {
            addTokenNode(children, curLineNum);
        } else {
            throw new RuntimeException("Expect 'Ident' in LVal");
        }

        if (lexer.getToken(curTokenIndex).getType() == TokenType.LBRACK) {
            addTokenNode(children, curLineNum);

            node = parseExp();
            children.add(node);

            if (lexer.getToken(curTokenIndex).getType() == TokenType.RBRACK) {
                addTokenNode(children, curLineNum);
            } else {
                errors.add(new Error(lexer.getToken(curTokenIndex - 1).getLineNum(), "k"));
            }
        }

        int endLine = lexer.getToken(curTokenIndex - 1).getLineNum();
        return new LVal(curLineNum, endLine, SyntaxType.LVal, children);
    }

    // Number ==> IntConst
    public Node parseNumber() {
        ArrayList<Node> children = new ArrayList<>();

        addTokenNode(children, curLineNum);

        int endLine = lexer.getToken(curTokenIndex - 1).getLineNum();
        return new Number(curLineNum, endLine, SyntaxType.Number, children);
    }

    // Character ==> CharConst
    public Node parseCharacter() {
        ArrayList<Node> children = new ArrayList<>();

        addTokenNode(children, curLineNum);

        int endLine = lexer.getToken(curTokenIndex - 1).getLineNum();
        return new Character(curLineNum, endLine, SyntaxType.Character, children);
    }

    // PrimaryExp ==> '(' Exp ')' | LVal | Number | Character
    public Node parsePrimaryExp() {
        ArrayList<Node> children = new ArrayList<>();
        
        Node node;
        if (curToken.getType() == TokenType.LPARENT) {
            addTokenNode(children, curLineNum);

            node = parseExp();
            children.add(node);

            if (lexer.getToken(curTokenIndex).getType() == TokenType.RPARENT) {
                addTokenNode(children, curLineNum);
            } else {
                errors.add(new Error(lexer.getToken(curTokenIndex - 1).getLineNum(), "j"));
            }
        } else if (curToken.getType() == TokenType.IDENFR) {
            node = parseLVal();
            children.add(node);
        } else if (curToken.getType() == TokenType.INTCON) {
            node = parseNumber();
            children.add(node);
        } else if (curToken.getType() == TokenType.CHRCON) {
            node = parseCharacter();
            children.add(node);
        } else {
            throw new RuntimeException("Expect '(' or 'Ident' or 'IntConst' or 'CharConst' in PrimaryExp");
        }
        int endLine = lexer.getToken(curTokenIndex - 1).getLineNum();
        return new PrimaryExp(curLineNum, endLine, SyntaxType.PrimaryExp, children);
    }

    // RelExp ==> AddExp {('<' | '>' | '<=' | '>=') AddExp}
    // RelExp ==> AddExp | AddExp ('<' | '>' | '<=' | '>=') RelExp
    public Node parseRelExp() {
        ArrayList<Node> children = new ArrayList<>();
        
        Node node = parseAddExp();
        children.add(node);

        while (lexer.getToken(curTokenIndex).getType() == TokenType.LSS || lexer.getToken(curTokenIndex).getType() == TokenType.LEQ || lexer.getToken(curTokenIndex).getType() == TokenType.GRE || lexer.getToken(curTokenIndex).getType() == TokenType.GEQ) {
            addTokenNode(children, curLineNum);

            node = parseAddExp();
            children.add(node);
        }
//        if (lexer.getToken(curTokenIndex).getType() == TokenType.LSS || lexer.getToken(curTokenIndex).getType() == TokenType.LEQ || lexer.getToken(curTokenIndex).getType() == TokenType.GRE || lexer.getToken(curTokenIndex).getType() == TokenType.GEQ) {
//            addTokenNode(children, curLineNum);
//            node = parseRelExp();
//            children.add(node);
//
//        }

        int endLine = lexer.getToken(curTokenIndex - 1).getLineNum();
        return new RelExp(curLineNum, endLine, SyntaxType.RelExp, children);
    }

    // EqExp ==> RelExp {('==' | '!=') RelExp}
    // EqExp ==> RelExp | RelExp ('==' | '!=') EqExp
    public Node parseEqExp() {
        ArrayList<Node> children = new ArrayList<>();
        
        Node node = parseRelExp();
        children.add(node);

        while (lexer.getToken(curTokenIndex).getType() == TokenType.EQL || lexer.getToken(curTokenIndex).getType() == TokenType.NEQ) {
            addTokenNode(children, curLineNum);

            node = parseRelExp();
            children.add(node);
        }
//        if (lexer.getToken(curTokenIndex).getType() == TokenType.EQL || lexer.getToken(curTokenIndex).getType() == TokenType.NEQ) {
//            addTokenNode(children, curLineNum);
//            node = parseEqExp();
//            children.add(node);
//        }

        int endLine = lexer.getToken(curTokenIndex - 1).getLineNum();
        return new EqExp(curLineNum, endLine, SyntaxType.EqExp, children);
    }

    // LAndExp ==> EqExp {'&&' EqExp}
    // LAndExp ==> EqExp | EqExp '&&' LAndExp
    public Node parseLAndExp() {
        ArrayList<Node> children = new ArrayList<>();
        
        Node node = parseEqExp();
        children.add(node);

        while (lexer.getToken(curTokenIndex).getType() == TokenType.AND) {
            addTokenNode(children, curLineNum);

            node = parseEqExp();
            children.add(node);
        }
//        if (lexer.getToken(curTokenIndex).getType() == TokenType.AND) {
//            addTokenNode(children, curLineNum);
//            node = parseLAndExp();
//            children.add(node);
//
//        }

        int endLine = lexer.getToken(curTokenIndex - 1).getLineNum();
        return new LAndExp(curLineNum, endLine, SyntaxType.LAndExp, children);
    }

    // LOrExp ==>  LAndExp {'||' LAndExp}
    // LOrExp ==> LAndExp | LAndExp '||' LOrExp
    public Node parseLOrExp() {
        ArrayList<Node> children = new ArrayList<>();
        
        Node node = parseLAndExp();
        children.add(node);

        while (lexer.getToken(curTokenIndex).getType() == TokenType.OR) {
            addTokenNode(children, curLineNum);

            node = parseLAndExp();
            children.add(node);
        }
//        if (lexer.getToken(curTokenIndex).getType() == TokenType.OR) {
//            addTokenNode(children, curLineNum);
//            node = parseLOrExp();
//            children.add(node);
//
//        }

        int endLine = lexer.getToken(curTokenIndex - 1).getLineNum();
        return new LOrExp(curLineNum, endLine, SyntaxType.LOrExp, children);
    }

    // ConstDecl ==> 'const' 'int'|'char' ConstDef { ',' ConstDef } ';'
    public Node parseConstDecl() {
        ArrayList<Node> children = new ArrayList<>();
        
        Node node;
        if (curToken.getType() == TokenType.CONSTTK) {
            addTokenNode(children, curLineNum);
        } else {
            throw new RuntimeException("Expect 'const' in ConstDecl");
        }

        if (lexer.getToken(curTokenIndex).getType() == TokenType.INTTK || lexer.getToken(curTokenIndex).getType() == TokenType.CHARTK) {
            addTokenNode(children, curLineNum);
        } else {
            throw new RuntimeException("Expect 'int' or 'char' in ConstDecl");
        }

        node = parseConstDef();
        children.add(node);

        while (lexer.getToken(curTokenIndex).getType() == TokenType.COMMA) {
            addTokenNode(children, curLineNum);

            node = parseConstDef();
            children.add(node);
        }
        if (lexer.getToken(curTokenIndex).getType() == TokenType.SEMICN) {
            addTokenNode(children, curLineNum);
        } else {
            errors.add(new Error(lexer.getToken(curTokenIndex - 1).getLineNum(), "i"));
        }
        int endLine = lexer.getToken(curTokenIndex - 1).getLineNum();
        return new ConstDecl(curLineNum, endLine, SyntaxType.ConstDecl, children);
    }

    // ConstDef ==> Ident [ '[' ConstExp ']' ] '=' ConstInitVal
    public Node parseConstDef() {
        ArrayList<Node> children = new ArrayList<>();
        
        Node node;
        if (curToken.getType() == TokenType.IDENFR) {
            addTokenNode(children, curLineNum);
        } else {
            throw new RuntimeException("Expect 'Ident' in ConstDef");
        }

        if (lexer.getToken(curTokenIndex).getType() == TokenType.LBRACK) {
            addTokenNode(children, curLineNum);

            node = parseConstExp();
            children.add(node);

            if (lexer.getToken(curTokenIndex).getType() == TokenType.RBRACK) {
                addTokenNode(children, curLineNum);
            } else {
                errors.add(new Error(lexer.getToken(curTokenIndex - 1).getLineNum(), "k"));
            }
        }

        if (lexer.getToken(curTokenIndex).getType() == TokenType.ASSIGN) {
            addTokenNode(children, curLineNum);

            node = parseConstInitVal();
            children.add(node);
        } else {
            throw new RuntimeException("Expect '=' in ConstDef");
        }

        int endLine = lexer.getToken(curTokenIndex - 1).getLineNum();
        return new ConstDef(curLineNum, endLine, SyntaxType.ConstDef, children);
    }

    // ConstInitVal ==> ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}' | StringConst
    public Node parseConstInitVal() {
        ArrayList<Node> children = new ArrayList<>();
        
        Node node;
        if (curToken.getType() == TokenType.LBRACE) {
            addTokenNode(children, curLineNum);

            if (lexer.getToken(curTokenIndex).getType() != TokenType.RBRACE) {
                node = parseConstExp();
                children.add(node);

                while (lexer.getToken(curTokenIndex).getType() == TokenType.COMMA) {
                    addTokenNode(children, curLineNum);

                    node = parseConstExp();
                    children.add(node);
                }
            }

            addTokenNode(children, curLineNum);
        } else if (curToken.getType() == TokenType.STRCON) {
            addTokenNode(children, curLineNum);
        } else {
            node = parseConstExp();
            children.add(node);
        }

        int endLine = lexer.getToken(curTokenIndex - 1).getLineNum();
        return new ConstInitVal(curLineNum, endLine, SyntaxType.ConstInitVal, children);
    }

    //FuncDef ==> FuncType Ident '(' [FuncFParams] ')' Block
    public Node parseFuncDef() {
        ArrayList<Node> children = new ArrayList<>();
        
        Node node = parseFuncType();
        children.add(node);

        if (lexer.getToken(curTokenIndex).getType() == TokenType.IDENFR) {
            addTokenNode(children, curLineNum);
        } else {
            throw new RuntimeException("Expect 'Ident' in FuncDef");
        }

        if (lexer.getToken(curTokenIndex).getType() == TokenType.LPARENT) {
            addTokenNode(children, curLineNum);

            if (lexer.getToken(curTokenIndex).getType() == TokenType.INTTK || lexer.getToken(curTokenIndex).getType() == TokenType.CHARTK) {
                node = parseFuncFParams();
                children.add(node);
            }

            if (lexer.getToken(curTokenIndex).getType() == TokenType.RPARENT) {
                addTokenNode(children, curLineNum);
            } else {
                errors.add(new Error(lexer.getToken(curTokenIndex - 1).getLineNum(), "j"));
            }
        } else {
            throw new RuntimeException("Expect '(' in FuncDef");
        }

        node = parseBlock();
        children.add(node);

        int endLine = lexer.getToken(curTokenIndex - 1).getLineNum();
        return new FuncDef(curLineNum, endLine, SyntaxType.FuncDef, children);
    }

    // FuncType ==> 'void' | 'int' | 'char'
    public Node parseFuncType() {
        ArrayList<Node> children = new ArrayList<>();
        
        if (curToken.getType() == TokenType.VOIDTK || curToken.getType() == TokenType.INTTK || curToken.getType() == TokenType.CHARTK) {
            addTokenNode(children, curLineNum);
        } else {
            throw new RuntimeException("Expect 'void' or 'int' or 'char' in FuncType");
        }
        int endLine = lexer.getToken(curTokenIndex - 1).getLineNum();
        return new FuncType(curLineNum, endLine, SyntaxType.FuncType, children);
    }

    // FuncFParams ==> FuncFParam { ',' FuncFParam }
    public Node parseFuncFParams() {
        ArrayList<Node> children = new ArrayList<>();
        
        Node node = parseFuncFParam();
        children.add(node);

        while (lexer.getToken(curTokenIndex).getType() == TokenType.COMMA) {
            addTokenNode(children, curLineNum);

            node = parseFuncFParam();
            children.add(node);
        }

        int endLine = lexer.getToken(curTokenIndex - 1).getLineNum();
        return new FuncFParams(curLineNum, endLine, SyntaxType.FuncFParams, children);
    }

    // FuncFParam ==> 'int'|'char' Ident ['[' ']']
    public Node parseFuncFParam() {
        ArrayList<Node> children = new ArrayList<>();
        
        if (curToken.getType() == TokenType.INTTK || curToken.getType() == TokenType.CHARTK) {
            addTokenNode(children, curLineNum);
        } else {
            throw new RuntimeException("Expect 'int' or 'char' in FuncFParam");
        }

        if (lexer.getToken(curTokenIndex).getType() == TokenType.IDENFR) {
            addTokenNode(children, curLineNum);
        } else {
            throw new RuntimeException("Expect 'Ident' in FuncFParam");
        }

        if (lexer.getToken(curTokenIndex).getType() == TokenType.LBRACK) {
            addTokenNode(children, curLineNum);

            if (lexer.getToken(curTokenIndex).getType() == TokenType.RBRACK) {
                addTokenNode(children, curLineNum);
            } else {
                errors.add(new Error(lexer.getToken(curTokenIndex - 1).getLineNum(), "k"));
            }
        }

        int endLine = lexer.getToken(curTokenIndex - 1).getLineNum();
        return new FuncFParam(curLineNum, endLine, SyntaxType.FuncFParam, children);
    }

    // Block ==> '{' {VarDecl | ConstDecl | Stmt} '}'
    public Node parseBlock() {
        ArrayList<Node> children = new ArrayList<>();
        
        Node node;
        if (curToken.getType() == TokenType.LBRACE) {
            addTokenNode(children, curLineNum);
        } else {
            throw new RuntimeException("Expect '{' in Block");
        }
        while (curToken.getType() != TokenType.RBRACE) {
            if (lexer.getToken(curTokenIndex).getType() == TokenType.CONSTTK) {
                node = parseConstDecl();
                children.add(node);
            } else if (lexer.getToken(curTokenIndex).getType() == TokenType.INTTK || lexer.getToken(curTokenIndex).getType() == TokenType.CHARTK) {
                node = parseVarDecl();
                children.add(node);
            } else {
                node = parseStmt();
                children.add(node);
            }
        }
        // parse '}'
        addTokenNode(children, curLineNum);

        int endLine = lexer.getToken(curTokenIndex - 1).getLineNum();
        return new Block(curLineNum, endLine, SyntaxType.Block, children);
    }

    // CondExp ==> LOrExp
    public Node parseCondExp() {
        ArrayList<Node> children = new ArrayList<>();
        
        Node node = parseLOrExp();
        children.add(node);
        int endLine = lexer.getToken(curTokenIndex - 1).getLineNum();
        return new Cond(curLineNum, endLine, SyntaxType.Cond, children);
    }

    // ForStmt ==> LVal '=' Exp
    public Node parseForStmt() {
        ArrayList<Node> children = new ArrayList<>();
        
        Node node = parseLVal();
        children.add(node);

        if (lexer.getToken(curTokenIndex).getType() == TokenType.ASSIGN) {
            addTokenNode(children, curLineNum);
        } else {
            throw new RuntimeException("Expect '=' in ForStmt");
        }

        node = parseExp();
        children.add(node);

        int endLine = lexer.getToken(curTokenIndex - 1).getLineNum();
        return new ForStmt(curLineNum, endLine, SyntaxType.ForStmt, children);
    }

    //Stmt ==> LVal '=' Exp ';'
    //| [Exp] ';' // i
    //| Block
    //| 'if' '(' Cond ')' Stmt [ 'else' Stmt ] // j
    //| 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
    //| 'break' ';' | 'continue' ';' // i
    //| 'return' [Exp] ';' // i
    //| LVal '=' 'getint''('')'';' // i j
    //| LVal '=' 'getchar''('')'';' // i j
    //| 'printf''('StringConst {','Exp}')'';' // i j
    public Node parseStmt() {
        ArrayList<Node> children = new ArrayList<>();
        
        Node node;
        if (lexer.getToken(curTokenIndex).getType() == TokenType.IFTK) {
            addTokenNode(children, curLineNum);

            addTokenNode(children, curLineNum);

            node = parseCondExp();
            children.add(node);

            if (lexer.getToken(curTokenIndex).getType() == TokenType.RPARENT) {
                addTokenNode(children, curLineNum);
            } else {
                errors.add(new Error(lexer.getToken(curTokenIndex - 1).getLineNum(), "j"));
            }

            node = parseStmt();
            children.add(node);

            if (lexer.getToken(curTokenIndex).getType() == TokenType.ELSETK) {
                addTokenNode(children, curLineNum);

                node = parseStmt();
                children.add(node);
            }
        } else if (lexer.getToken(curTokenIndex).getType() == TokenType.FORTK) {
            addTokenNode(children, curLineNum);

            if (lexer.getToken(curTokenIndex).getType() == TokenType.LPARENT) {
                addTokenNode(children, curLineNum);

                if (lexer.getToken(curTokenIndex).getType() != TokenType.SEMICN) {
                    node = parseForStmt();
                    children.add(node);
                }

                if (lexer.getToken(curTokenIndex).getType() == TokenType.SEMICN) {
                    addTokenNode(children, curLineNum);
                } else {
                    throw new RuntimeException("Expect ';' in For1 Stmt");
                }

                if (lexer.getToken(curTokenIndex).getType() != TokenType.SEMICN) {
                    node = parseCondExp();
                    children.add(node);
                }

                if (lexer.getToken(curTokenIndex).getType() == TokenType.SEMICN) {
                    addTokenNode(children, curLineNum);
                } else {
                    throw new RuntimeException("Expect ';' in For2 Stmt");
                }

                if (lexer.getToken(curTokenIndex).getType() != TokenType.RPARENT) {
                    node = parseForStmt();
                    children.add(node);
                }

                addTokenNode(children, curLineNum);

                node = parseStmt();
                children.add(node);
            } else {
                throw new RuntimeException("Expect '(' in Stmt");
            }
        } else if (lexer.getToken(curTokenIndex).getType() == TokenType.BREAKTK || lexer.getToken(curTokenIndex).getType() == TokenType.CONTINUETK) {
            addTokenNode(children, curLineNum);
            if (lexer.getToken(curTokenIndex).getType() == TokenType.SEMICN) {
                addTokenNode(children, curLineNum);
            } else {
                errors.add(new Error(lexer.getToken(curTokenIndex - 1).getLineNum(), "i"));
            }
        } else if (lexer.getToken(curTokenIndex).getType() == TokenType.RETURNTK) {
            addTokenNode(children, curLineNum);
            if (isExpFirst()) {
                node = parseExp();
                children.add(node);
            }
            if (lexer.getToken(curTokenIndex).getType() == TokenType.SEMICN) {
                addTokenNode(children, curLineNum);
            } else {
                errors.add(new Error(lexer.getToken(curTokenIndex - 1).getLineNum(), "i"));
            }
        } else if (lexer.getToken(curTokenIndex).getType() == TokenType.PRINTFTK) {
            addTokenNode(children, curLineNum);
            if (lexer.getToken(curTokenIndex).getType() == TokenType.LPARENT) {
                addTokenNode(children, curLineNum);
                if (lexer.getToken(curTokenIndex).getType() == TokenType.STRCON) {
                    addTokenNode(children, curLineNum);
                    while (lexer.getToken(curTokenIndex).getType() == TokenType.COMMA) {
                        addTokenNode(children, curLineNum);
                        node = parseExp();
                        children.add(node);
                    }
                } else {
                    throw new RuntimeException("Expect StringConst in print Stmt");
                }
                if (lexer.getToken(curTokenIndex).getType() == TokenType.RPARENT) {
                    addTokenNode(children, curLineNum);
                } else {
                    errors.add(new Error(lexer.getToken(curTokenIndex - 1).getLineNum(), "j"));
                }
                if (lexer.getToken(curTokenIndex).getType() == TokenType.SEMICN) {
                    addTokenNode(children, curLineNum);
                } else {
                    errors.add(new Error(lexer.getToken(curTokenIndex - 1).getLineNum(), "i"));
                }
            } else {
                throw new RuntimeException("Expect '(' in print Stmt");
            }
        } else if (lexer.getToken(curTokenIndex).getType() == TokenType.IDENFR && (lexer.getToken(curTokenIndex + 1).getType() == TokenType.ASSIGN || lexer.getToken(curTokenIndex + 1).getType() == TokenType.LBRACK)) {
            boolean isExpLVal = true;
            for (int i = curTokenIndex; lexer.getToken(i).getType() != TokenType.SEMICN; i++) {
                if (lexer.getToken(i).getType() == TokenType.ASSIGN) {
                    isExpLVal = false;
                    break;
                }
            }
            if (isExpLVal) {
                if (isExpFirst()) {
                    node = parseExp();
                    children.add(node);
                }
                if (lexer.getToken(curTokenIndex).getType() == TokenType.SEMICN) {
                    addTokenNode(children, curLineNum);
                } else {
                    errors.add(new Error(lexer.getToken(curTokenIndex - 1).getLineNum(), "i"));
                }
            } else {
                node = parseLVal();
                children.add(node);

                if (lexer.getToken(curTokenIndex).getType() == TokenType.ASSIGN) {
                    addTokenNode(children, curLineNum);

                    if (lexer.getToken(curTokenIndex).getType() == TokenType.GETINTTK || lexer.getToken(curTokenIndex).getType() == TokenType.GETCHARTK) {
                        addTokenNode(children, curLineNum);
                        if (lexer.getToken(curTokenIndex).getType() == TokenType.LPARENT) {
                            addTokenNode(children, curLineNum);
                            if (lexer.getToken(curTokenIndex).getType() == TokenType.RPARENT) {
                                addTokenNode(children, curLineNum);
                            } else {
                                errors.add(new Error(lexer.getToken(curTokenIndex - 1).getLineNum(), "j"));
                            }
                        } else {
                            throw new RuntimeException("Expect '(' in Stmt");
                        }
                    } else {
                        node = parseExp();
                        children.add(node);
                    }

                    if (lexer.getToken(curTokenIndex).getType() == TokenType.SEMICN) {
                        addTokenNode(children, curLineNum);
                    } else {
                        errors.add(new Error(lexer.getToken(curTokenIndex - 1).getLineNum(), "i"));
                    }
                }
            }
        } else if (lexer.getToken(curTokenIndex).getType() == TokenType.LBRACE) {
            node = parseBlock();
            children.add(node);
        } else {
            if (isExpFirst()) {
                node = parseExp();
                children.add(node);
            }
            if (lexer.getToken(curTokenIndex).getType() == TokenType.SEMICN) {
                addTokenNode(children, curLineNum);
            } else {
                errors.add(new Error(lexer.getToken(curTokenIndex - 1).getLineNum(), "i"));
            }
        }

        int endLine = lexer.getToken(curTokenIndex - 1).getLineNum();
        return new Stmt(curLineNum, endLine, SyntaxType.Stmt, children);
    }

}

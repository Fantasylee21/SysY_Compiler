package frontend;

import java.util.ArrayList;

public class Lexer {
    private final ArrayList<Token> tokens;
    private final ArrayList<Error> errors;
    private int lineNum;
    private int index;

    public Lexer() {
        tokens = new ArrayList<>();
        errors = new ArrayList<>();
        lineNum = 1;
        index = 0;
    }

    public void error() {
        // 输出到error.txt
        for (Error error : errors) {
            System.out.println(error.getLineNum() + " " + error.getMessage());
        }
    }

    public void lexerOut() {
        // 输出到lexer.txt
        for (Token token : tokens) {
            System.out.println(token.getType() + " " + token.getValue());
        }
    }

    public boolean hasNext(String input) {
        return index < input.length();
    }

    public boolean isIdentifierNonDigit (char c) {
        return c == '_' || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    public boolean isIdentifier(char c) {
        return isIdentifierNonDigit(c) || (c >= '0' && c <= '9');
    }

    public boolean hasError() {
        return !errors.isEmpty();
    }

    public void lexerIn(String input) {
        while (hasNext(input)) {
            char curChar = input.charAt(index);
            switch (curChar) {
                case ' ':
                    break;
                case '\n':
                    lineNum++;
                    break;
                case '+':
                    tokens.add(new Token(lineNum, TokenType.PLUS, "+"));
                    break;
                case '-':
                    tokens.add(new Token(lineNum, TokenType.MINU, "-"));
                    break;
                case '*':
                    tokens.add(new Token(lineNum, TokenType.MULT, "*"));
                    break;
                case '/':
                    if (hasNext(input) && input.charAt(index + 1) == '/') {
                        index++;
                        while (hasNext(input) && input.charAt(index) != '\n') {
                            index++;
                        }
                        index--;
                    } else if (hasNext(input) && input.charAt(index + 1) == '*') {
                        index += 2;
                        while (hasNext(input) && !(input.charAt(index) == '*' && hasNext(input) && input.charAt(index + 1) == '/')) {
                            if (input.charAt(index) == '\n') {
                                lineNum++;
                            }
                            index++;
                        }
                        index++;
                    } else {
                        tokens.add(new Token(lineNum, TokenType.DIV, "/"));
                    }
                    break;
                case '%':
                    tokens.add(new Token(lineNum, TokenType.MOD, "%"));
                    break;
                case '!':
                    if (hasNext(input) && input.charAt(index + 1) == '=') {
                        tokens.add(new Token(lineNum, TokenType.NEQ, "!="));
                        index++;
                    } else {
                        tokens.add(new Token(lineNum, TokenType.NOT, "!"));
                    }
                    break;
                case '&':
                    if (hasNext(input) && input.charAt(index + 1) == '&') {
                        tokens.add(new Token(lineNum, TokenType.AND, "&&"));
                        index++;
                    } else {
                        errors.add(new Error(lineNum, "a"));
                    }
                    break;
                case '|':
                    if (hasNext(input) && input.charAt(index + 1) == '|') {
                        tokens.add(new Token(lineNum, TokenType.OR, "||"));
                        index++;
                    } else {
                        errors.add(new Error(lineNum, "a"));
                    }
                    break;
                case '<':
                    if (hasNext(input) && input.charAt(index + 1) == '=') {
                        tokens.add(new Token(lineNum, TokenType.LEQ, "<="));
                        index++;
                    } else {
                        tokens.add(new Token(lineNum, TokenType.LSS, "<"));
                    }
                    break;
                case '>':
                    if (hasNext(input) && input.charAt(index + 1) == '=') {
                        tokens.add(new Token(lineNum, TokenType.GEQ, ">="));
                        index++;
                    } else {
                        tokens.add(new Token(lineNum, TokenType.GRE, ">"));
                    }
                    break;
                case '=':
                    if (hasNext(input) && input.charAt(index + 1) == '=') {
                        tokens.add(new Token(lineNum, TokenType.EQL, "=="));
                        index++;
                    } else {
                        tokens.add(new Token(lineNum, TokenType.ASSIGN, "="));
                    }
                    break;
                case ';':
                    tokens.add(new Token(lineNum, TokenType.SEMICN, ";"));
                    break;
                case ',':
                    tokens.add(new Token(lineNum, TokenType.COMMA, ","));
                    break;
                case '(':
                    tokens.add(new Token(lineNum, TokenType.LPARENT, "("));
                    break;
                case ')':
                    tokens.add(new Token(lineNum, TokenType.RPARENT, ")"));
                    break;
                case '[':
                    tokens.add(new Token(lineNum, TokenType.LBRACK, "["));
                    break;
                case ']':
                    tokens.add(new Token(lineNum, TokenType.RBRACK, "]"));
                    break;
                case '{':
                    tokens.add(new Token(lineNum, TokenType.LBRACE, "{"));
                    break;
                case '}':
                    tokens.add(new Token(lineNum, TokenType.RBRACE, "}"));
                    break;
                case '"':
                    StringBuilder sb0 = new StringBuilder();
                    sb0.append(input.charAt(index));
                    index++;
                    while (hasNext(input) && input.charAt(index) != '"') {
                        sb0.append(input.charAt(index));
                        index++;
                    }
                    sb0.append(input.charAt(index));
                    tokens.add(new Token(lineNum, TokenType.STRCON, sb0.toString()));
                    break;
                case '\'':
                    StringBuilder sb1 = new StringBuilder();
                    sb1.append(input.charAt(index));
                    index++;
                    while (hasNext(input) && input.charAt(index) != '\'') {
                        sb1.append(input.charAt(index));
                        index++;
                    }
                    sb1.append(input.charAt(index));
                    tokens.add(new Token(lineNum, TokenType.CHRCON, sb1.toString()));
                    break;
                case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9':
                    StringBuilder sb2 = new StringBuilder();
                    while (hasNext(input) && Character.isDigit(input.charAt(index))) {
                        sb2.append(input.charAt(index));
                        index++;
                    }
                    index--;
                    tokens.add(new Token(lineNum, TokenType.INTCON, sb2.toString()));
                    break;
                case '_': case 'a': case 'b': case 'c': case 'd': case 'e': case 'f': case 'g': case 'h': case 'i':
                case 'j': case 'k': case 'l': case 'm': case 'n': case 'o': case 'p': case 'q': case 'r': case 's':
                case 't': case 'u': case 'v': case 'w': case 'x': case 'y': case 'z': case 'A': case 'B': case 'C':
                case 'D': case 'E': case 'F': case 'G': case 'H': case 'I': case 'J': case 'K': case 'L': case 'M':
                case 'N': case 'O': case 'P': case 'Q': case 'R': case 'S': case 'T': case 'U': case 'V': case 'W':
                case 'X': case 'Y': case 'Z':
                    StringBuilder sb3 = new StringBuilder();
                    while (hasNext(input) && isIdentifier(input.charAt(index))) {
                        sb3.append(input.charAt(index));
                        index++;
                    }
                    index--;
                    String s = sb3.toString();
                    switch (s) {
                        case "const":
                            tokens.add(new Token(lineNum, TokenType.CONSTTK, s));
                            break;
                        case "int":
                            tokens.add(new Token(lineNum, TokenType.INTTK, s));
                            break;
                        case "char":
                            tokens.add(new Token(lineNum, TokenType.CHARTK, s));
                            break;
                        case "void":
                            tokens.add(new Token(lineNum, TokenType.VOIDTK, s));
                            break;
                        case "main":
                            tokens.add(new Token(lineNum, TokenType.MAINTK, s));
                            break;
                        case "if":
                            tokens.add(new Token(lineNum, TokenType.IFTK, s));
                            break;
                        case "else":
                            tokens.add(new Token(lineNum, TokenType.ELSETK, s));
                            break;
                        case "for":
                            tokens.add(new Token(lineNum, TokenType.FORTK, s));
                            break;
                        case "break":
                            tokens.add(new Token(lineNum, TokenType.BREAKTK, s));
                            break;
                        case "continue":
                            tokens.add(new Token(lineNum, TokenType.CONTINUETK, s));
                            break;
                        case "return":
                            tokens.add(new Token(lineNum, TokenType.RETURNTK, s));
                            break;
                        case "getint":
                            tokens.add(new Token(lineNum, TokenType.GETINTTK, s));
                            break;
                        case "printf":
                            tokens.add(new Token(lineNum, TokenType.PRINTFTK, s));
                            break;
                        case "getchar":
                            tokens.add(new Token(lineNum, TokenType.GETCHARTK, s));
                            break;
                        default:
                            tokens.add(new Token(lineNum, TokenType.IDENFR, s));
                            break;
                    }
                    break;
            }
            index++;
        }
    }
}


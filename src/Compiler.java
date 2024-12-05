import backend.MipsBuilder;
import frontend.*;
import frontend.AST.Node;
import frontend.Error.Error;
import frontend.Error.LexerErrors;
import frontend.Error.ParserErrors;
import frontend.Error.SymbolErrors;
import llvm.LLVMBuilder;

import java.io.*;
import java.util.TreeMap;

public class Compiler {
    public static void main(String[] args) {
        //从小到大排序
        TreeMap<Integer,String> errors = new TreeMap<>();

        Lexer lexer = new Lexer();
        String path = "testfile.txt";
        StringBuilder input = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                input.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        lexer.lexerIn(input.toString());
        Parser parser = new Parser(lexer);
        Node root = parser.parse();
        //重定向输出到lexer.txt
        try {
            System.setOut(new PrintStream(new FileOutputStream("lexer.txt")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        lexer.lexerOut();

        //重定向输出到parser.txt
        try {
            System.setOut(new PrintStream(new FileOutputStream("parser.txt")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        root.print();

        //重定向输出到symbol.txt
        root.checkErrors();
        try {
            System.setOut(new PrintStream(new FileOutputStream("symbol.txt")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        SymbolManager.getInstance().print();

        //重定向输出到error.txt
        for (Error error : LexerErrors.getInstance().getErrors()) {
            errors.put(error.getLineNum(),error.getMessage());
        }
        for (Error error : ParserErrors.getInstance().getErrors()) {
            errors.put(error.getLineNum(),error.getMessage());
        }
        for (Error error : SymbolErrors.getInstance().getErrors()) {
            errors.put(error.getLineNum(),error.getMessage());
        }
        try {
            System.setOut(new PrintStream(new FileOutputStream("error.txt")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Integer key : errors.keySet()) {
            System.out.println(key + " " + errors.get(key));
        }

        root.generateIR();
        //重定向输出到IR.txt
        try {
            System.setOut(new PrintStream(new FileOutputStream("llvm_ir.txt")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(LLVMBuilder.getLlvmBuilder().getModule().toString());
        //重定向输出到mips.txt
        try {
            System.setOut(new PrintStream(new FileOutputStream("mipsTemp.txt")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        LLVMBuilder.getLlvmBuilder().getModule().generateMips();
        System.out.println(MipsBuilder.getMipsBuilder().getObjModule().toString());

        try {
            System.setOut(new PrintStream(new FileOutputStream("mips.txt")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(MipsBuilder.getMipsBuilder().getObjModule(true).toString());
//        将mips.txt复制到mips.asm
        try {
            BufferedReader reader = new BufferedReader(new FileReader("mips.txt"));
            BufferedWriter writer = new BufferedWriter(new FileWriter("mips.asm"));
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

import frontend.AST.Node;
import frontend.Lexer;
import frontend.Error;
import frontend.Parser;

import java.io.*;
import java.util.ArrayList;

public class Compiler {
    public static void main(String[] args) {
        ArrayList<Error> errors = new ArrayList<>();
        Lexer lexer = new Lexer(errors);
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
        Parser parser = new Parser(lexer, errors);
        Node root = parser.parse();
        //重定向输出到lexer.txt
        try {
            System.setOut(new PrintStream(new FileOutputStream("lexer.txt")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        lexer.lexerOut();
        if (parser.hasError()) {
            //重定向输出到error.txt
            try {
                System.setOut(new PrintStream(new FileOutputStream("error.txt")));
            } catch (Exception e) {
                e.printStackTrace();
            }
            parser.printErrors();
        } else {
            //重定向输出到parser.txt
            try {
                System.setOut(new PrintStream(new FileOutputStream("parser.txt")));
            } catch (Exception e) {
                e.printStackTrace();
            }
            root.print();
        }

    }
}

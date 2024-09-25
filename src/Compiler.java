import frontend.Lexer;

import java.io.*;

public class Compiler {
    public static void main(String[] args) {
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
        if (lexer.hasError()) {
            //重定向输出到error.txt
            try {
                System.setOut(new PrintStream(new FileOutputStream("error.txt")));
            } catch (Exception e) {
                e.printStackTrace();
            }
            lexer.error();
        } else {
            //重定向输出到lexer.txt
            try {
                System.setOut(new PrintStream(new FileOutputStream("lexer.txt")));
            } catch (Exception e) {
                e.printStackTrace();
            }
            lexer.lexerOut();
        }

    }
}

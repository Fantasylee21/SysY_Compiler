# SysY_Compiler——BUAA2024秋编译设计文档

下面各部分描述严格按照本人完成的顺序进行

### 总体结构

下面本编译器分为前端、中端和后端

- 前端：词法分析、语法分析、语义分析、错误处理
- 中端：LLVM IR代码优化，做了死代码删除（未使用的函数、不可达块、不可达函数）、Mem2Reg
- 后端：活跃变量分析、寄存器分配

整体代码架构如下在Compile.java即可看出各部分的接口

```java
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
    System.setOut(new PrintStream(new FileOutputStream("llvm_ir_Ori.txt")));
} catch (Exception e) {
    e.printStackTrace();
}
System.out.println(LLVMBuilder.getLlvmBuilder().getModule().toString());

try {
    System.setOut(new PrintStream(new FileOutputStream("llvm_ir.txt")));
} catch (Exception e) {
    e.printStackTrace();
}
MidOptimize midOptimize = new MidOptimize(LLVMBuilder.getLlvmBuilder().getModule());
midOptimize.run();
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
//将mips.txt复制到mips.asm
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
```

整体思路如下：

- Lexer类将源程序读入并解析为token，并存储在该类中用于后续操作
- Parse类用于解析Lexer中存储的token，并根据文法通过递归向下构建抽象语法树
- 从语法树的根节点递归向下实现checkErrors并在这个过程中建立符号表，SymbolManager类用于管理符号表的作用域符号
- LexerErrors、ParserErrors、SymbolErrors实现各阶段分析所产生问题的储存
- 结束词法分析、语法分析、语义分析这一章后，就可以根据构建的语法树和符号表生成中间代码，思路依旧是从根节点递归向下生成中间代码（无优化版）
- 生成中间代码后将`Module`传入`MidOptimize`进行中间代码的各类优化
- 将所有llvm ir通过`generateMips`翻译为mips中间代码（在这个过程中只是单纯翻译中间代码、所需要的寄存器均为寄存器）
- `RegAllocator`根据第一遍生成的代码（mipsTemp.txt）进行寄存器分配和栈帧保留区、扩展区代码的插入

### 前端

#### 词法分析

和OO课的第一单元很像、只要弄清最小基本单元很简单、对着OO作业的架构几乎一遍通过。

我定义完Token类以及按照教程中给出的TokenType的Enum类后

```java
public class Token {
    private final int lineNum;
    private final TokenType type;
    private final String value;
}
```

遍历读入的字符串，只需用switch列出所有可能的字符，并实现对应的操作即可，如果读入的字符是字母，只需要将其读到尾部再使用switch穷举其可能遇到字符串。我没有使用任何正则匹配，任何相比其他方法，我认为这是一种高可维护性并且十分简单的代码，或许部分工作有些繁琐，例如下面的case可能特别多，code过程列完需要的操作，繁琐的操作完全可以扔给ai做。

```java
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
                ……
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
                        ……
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
```

这个部分相对来说是我做的比较满意的部分，全程除了对 `'` 的处理有点bug外，几乎没有任何修改



#### 语法分析

这部分主要参考陈学长递归下降子程序和OO作业的思路，实现不是很难，重点还是熟悉文法

- 第一步，毫无疑问、根据理论课消除左递归

- 第二步，构造各个语法单元的类，其都继承`Node`类

  ```java
  public class Node {
      protected final int startLine;
      private final int endLine;
      protected final SyntaxType type;
      protected final ArrayList<Node> children;
  }
  ```

- 第三步，根据文法解析，这里要区分终结符和非终结符，这里以VarDecl为例介绍

  ```java
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
          ParserErrors.getInstance().addError(lexer.getToken(curTokenIndex - 1).getLineNum(), "i");
          addMissedNode(children, lexer.getToken(curTokenIndex - 1).getLineNum(), TokenType.SEMICN, ";");
      }
  
      int endLine = lexer.getToken(curTokenIndex - 1).getLineNum();
      return new VarDecl(curLineNum, endLine, SyntaxType.VarDecl, children);
  }
  ```

  从文法可以看出，各个组成部分应该是什么，如果是非中介符，需要继续调用非终结符的`parseVardef`方法，但是无论是终结符还是非终结符都需要加入该节点的子节点中去，因为语法分析还需要进行错误处理，所以还需要进行终结符的特判来识别i、j、k类错误，为了快速定位错误，我也在一些地方进行了抛出异常来处理某些地方token的过读。

- 最后，按照要求进行遍历输出即可



#### 语义分析

这里虽然写的很顺利，但是我也在这一部分作为单独一次作业是否合理，也许是我的调研不够充分，如果单纯为了实现实验任务，这里的只有两项任务——递归向下进行错误处理，建立栈式符号表输出对应内容

我建立的符号表如下：

```java
public class SymbolManager {
    private static final SymbolManager instance = new SymbolManager();
    private final Stack<SymbolTable> symbolTables;
    private int loopDepth;
    private int scopeNum; // 作用域序号
    private FuncSymbol lastFuncSymbol;
    private final TreeMap<Integer, SymbolTable> symbolTableList;
}
```

采用单例模式，在对语法树递归向下`CheckErrors`时进行符号表的管理，针对不同符号，我建了`ArraySymbol`,`ConstArraySymbol`,`ConstVarSymbol`,`FuncSymbol`和`VarSymbol`五个子类，实现针对不同符号的不同处理，在输出时直接将其`toString()`即可



**错误处理**

要做的事情大致分为以下

- 更新符号表
  - 更新当前函数
  - 更新当前block
  - 更新当前循环层数
- 检查根据描述检查各类错误——缺少return、重命名等

针对不同节点，实现checkErrors即可



这部分架构后续没做太多更改，bug主要出在一下几个方面

- printf里面的%d和%c与参数个数的检查
- 语法分析未对缺失的符号进行补全，导致后续涉及对节点数目的判断出现问题
- Stmt语句`LVal '=' Exp ';'` 和 `[Exp] ';'`的First集合冲突问题，这里我采用的是遍历提前读的方式来予以区分

做完上述工作足以通过语法分析的测试，但在后续生成LLVM的过程才发现这里我做的只能概括为——为了输出而输出



### 中端

#### llvm代码生成

难+工作量大，由于前期符号表建立的不足，后续又对符号表进行了加工，增加了一些方法。下面介绍代码按照个人完成的顺序进行。

初学LLVM，对指令并不熟悉，按照教材中创建了对应的类，继承关系严格按照教程中的这张图

![value](img/value.png)

1. 

针对中间代码生成，这一步创建对应的类后，实现各个类的toString方法，以`Alloca`指令为例

```java
@Override
public String toString() {
	return name + " = alloca " + targetType.toString();
}
```

其实最开始写的时候，对于“`User` 和 `Value` 之间的配对用 `Use` 类记录，通过 `Use` 类建立起了语法结构的上下级关系双向索引。这样所有的语法结构都可以统一成 `Value` 类，语法结构间的使用关系都可以统一成 `Use` 类。”我并不是很理解，只是知道应该这么做，知道优化的时候才发现其作用。参考学长代码我发现可以通过新建对象时就将其加入BasicBlock和更新User

```java
public MidInstr(LLVMType type, String name, MidInstrType instrType) {
    super(type, name);
    this.instrType = instrType;
    parentBasicBlock = null;
    LLVMBuilder.getLlvmBuilder().addInstruction(this);
}
```

```java
public void addOperand(Value operand) {
    operands.add(operand);
    if (operand != null) {
    operand.addUse(this);
    }
}
```

```java
public AllocaInstr(String name, LLVMType targetType) {
    super(new PointerType(targetType), name, MidInstrType.ALLOCA);
    this.targetType = targetType;
}
```

![uml](img/uml.png)

针对指令的类型，我参考了学长的写法，也实现了`ArrayType`,`BoolType`,`Int8Type`,`Int32Type`,`OtherType`,`PointerType`和`VoidType`,并定义了一个枚举类来区分其类型

针对常量和全局变量的初始值，我使用了两个子类ArrayInitial和VarInitial（继承Initial类，initial类继承Value）来作为常量和全局变量的初值。

**生成llvm**

完成各个类的创建后，接下来就是中间代码生成中最难的部分，由语法树——生成对应的llvm ir代码，大体思路还是按照语法树递归向下，为节点实现`generateMips`方法

我实现了LLVMBuilder类，主要用于生成变量名、为当前Function生成BasicBlock，为当前BasicBlock生MidInstr。

```java
 private static final LLVMBuilder llvmBuilder = new LLVMBuilder();
private static int registerCounter = 0; // 虚拟寄存器编号
private static int printStringCounter = 0; // 输出字符编号
private static int branchCounter = 0; // 分支名编号
private Module module;
private Function curFunction;
private BasicBlock curBasicBlock;
private Stack<Loop> loopStack;
```

命名方面，最开始我是按照%1,%2这种命名方式，后来发现无法保证严格递增的条件，索性把虚拟寄存器加个v，给分支加个branch这样就可以通过语法检查，也方便分别储存。

代码生成方面，比较难的部分就是for循环

![for](img/for.png)

针对这张图，需要针对每个创建BasicBlock和对每个Block生成mips，同时需要记录



**小结**

LLVM中间代码上手难度十分不易，debug也花了很长时间，但是在IDEA的控制台用wsl做好代码的测试也比较方便，下面是我的测试程序，每次生成完用wsl跑看报错，真的很方便！！！

```shell
cd ..
cat llvm_ir.txt > ./testLLVM/out.ll
cat llvm_ir.txt > ./testLLVM/llvm_ir.ll

cat testfile.txt > ./testLLVM/main.c
cd ./testLLVM

llvm-link out.ll lib.ll -S -o out.ll
lli out.ll < in.txt
```

### 后端

#### 生成Mips代码

做完中间代码生成后，我做了无优化版本的mips代码生成，由于我采用的是先生成虚拟寄存器，这部分的难点在于栈帧的分配，我的栈帧分配如下：

```java
/*
    |-------------------------|
    |----------ext------------|
    |-------------------------|
    |---------local-----------|
    |-------------------------|
    |----------save-----------|
    |-------------------------|
    |-----------ra------------|
    |-------------------------|
    |----------para-----------|
    |-------------------------|
 */
```

由于某些原因，我没有采用任何对齐，所有的值均按照四字节存储

- para区：存放当前函数调用其他函数的实参，我并没有将\$a0这些进行存储，大小为该函数调用的所有$(函数参数的最大值-4)* 4$，这样可以方便被调用函数获取实参，只需要原来偏移量加上当前函数的大小fSize
- ra：存放函数返回值$ra
- sava保留区：存放函数调用时需要存起来的寄存器，大小由我分配的全局寄存器数目决定
- local：本地值的存放处，如alloca指令
- ext扩展区：存放后续保留中间计算结果的位置

弄清上述东西花费了我大部分时间，接下来就是和llvm代码一样，对所有MidInstr实现`generateMips`方法进行翻译，并用`MipsBuilder`类实现指令添加、生成虚拟寄存器、计算栈帧偏移量

我的第一遍仅仅分了.text和.data段用于存放各类目标代码，然后遍历代码，分配`$t0`和`$t1`寄存器



破坏\$a0,\$a1,\$a2,\$a3的行为：

1. 系统调用
2. 函数调用
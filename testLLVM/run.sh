cd ..
cat llvm_ir.txt > ./testLLVM/out.ll
cat llvm_ir.txt > ./testLLVM/llvm_ir.ll

cat testfile.txt > ./testLLVM/main.c
cd ./testLLVM

llvm-link out.ll lib.ll -S -o out.ll
lli out.ll < in.txt

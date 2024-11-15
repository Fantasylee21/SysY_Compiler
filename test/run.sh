cd ..
cat llvm_ir.txt > ./test/out.ll
cat llvm_ir.txt > ./test/llvm_ir.ll

cat testfile.txt > ./test/main.c
cd ./test

llvm-link out.ll lib.ll -S -o out.ll
lli out.ll < in.txt

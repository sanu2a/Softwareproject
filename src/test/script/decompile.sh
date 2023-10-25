#! /bin/sh

cd "$(dirname "$0")"/../../.. || exit 1

rouge='\e[0;31m'
vert='\e[0;32m'
blanc='\e[1;37m'
PATH=~/src/test/script/launchers/:"$PATH"

for test in src/test/deca/codegen/valid/improvided/*.deca
do 
  
    decac -p $test > "${test}".decompile
    echo "$test decompilation: OK"
    rm "${test}".decompile
done
for test in src/test/deca/syntax/valid/provided/*.deca
do 
  decac -p $test > "${test}".decompile
    echo "$test decompilation: OK"
    rm "${test}".decompile
   
done
for test in src/test/deca/context/valid/improvided/*.deca
do 
   decac -p $test > "${test}".decompile
    echo "$test decompilation: OK"
    rm "${test}".decompile
   
   
done
for test in src/test/deca/context/invalid/improvided/*.deca
do 
   decac -p $test > "${test}".decompile
    echo "$test decompilation: OK"
    rm "${test}".decompile
   
   
done
   

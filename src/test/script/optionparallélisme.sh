#! /bin/sh
cd "$(dirname "$0")"/../../.. || exit 1


PATH=~/src/test/script/launchers:./src/main/bin:"$PATH"


rouge='\e[0;31m'
vert='\e[0;32m'
blanc='\e[1;37m'
violet='\e[1;35m'



 

echo "----------------- Compilation sans option P  -----------------"
echo ""
time decac src/test/deca/codegen/valid/improvided/*.deca
echo ""
echo "----------------- Compilation terminée ------------------------"
echo ""
echo "----------------- Compilation en parallèle: -------------------"
echo ""
time decac -P src/test/deca/codegen/valid/improvided/*.deca
echo ""
echo "----------------- Compilation terminée -------------------------"


exit 0

   

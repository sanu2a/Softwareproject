#! /bin/sh

cd "$(dirname "$0")"/../../.. || exit 1


PATH=~/src/test/script/launchers:./src/main/bin:"$PATH"


rouge='\e[0;31m'
vert='\e[0;32m'
blanc='\e[1;37m'
violet='\e[1;35m'

for test in src/test/deca/codegen/valid/improvided/*.deca
do 
    decac $test
    echo "$test : compilation: Ok"
done
for test in src/test/deca/codegen/invalid/improvided/*.deca
do 
    decac $test
    echo "$test : compilation: Ok"
done

echo "${voilet}Etape de l'execution : valid "
for prog in src/test/deca/codegen/valid/improvided/*.ass
do
   
    ima "$prog" > "${prog}".sortie
    if diff --ignore-all-space "${prog}".sortie "${prog}".attendu
    then echo "${blanc}$prog ${violet}PASSED"
      	rm "${prog}".sortie
	rm "${prog}"
    else echo "${rouge}FAILED"
	rm "${prog}".sortie
	rm "${prog}"
        exit 1
    fi 

done
echo "${voilet}Etape de l'execution : invalid "
for prog in src/test/deca/codegen/invalid/improvided/*.ass
do

    ima "$prog" > "${prog}".sortie
    if diff --ignore-all-space "${prog}".sortie "${prog}".attendu
    then echo "${blanc}$prog ${violet}PASSED"
	rm "${prog}".sortie
	rm "${prog}"
    else echo "${rouge}FAILED"
	rm "${prog}".sortie
	rm "${prog}"
        exit 1
    fi
done


 

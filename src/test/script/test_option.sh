#! /bin/sh
cd "$(dirname "$0")"/../../.. || exit 1


PATH=~/src/test/script/launchers:./src/main/bin:"$PATH"


rouge='\e[0;31m'
vert='\e[0;32m'
blanc='\e[1;37m'
violet='\e[1;35m'
echo "Option -b"
decac -b
echo "Option decac sans arguments"
decac


for test in src/test/deca/codegen/valid/improvided/*.deca
do 
    decac -n -r 8 $test
    echo "$test : compilation: Ok"
done


#Decac -r invalid: test d'exception  
if	decac -r -n 2>&1 | \
	grep "L'option -r X : X doit etre un entier entre 4 et 16"
	then 
	echo "Error Handled : Ok"
	else
	echo "Resultat inattendu"
	exit 1
fi
	 
	

echo "Etape de l'execution :"
for prog in src/test/deca/codegen/valid/improvided/*.ass
do
   
    ima "$prog" > "${prog}".sortie
    if diff --ignore-all-space "${prog}".sortie "${prog}".attendu
    then echo "${blanc}$prog ${violet}PASSED"
      
    else echo "${rouge}FAILED"
        exit 1
    fi 
    rm "${prog}".sortie 
    
    rm "${prog}"

done
exit 0

   

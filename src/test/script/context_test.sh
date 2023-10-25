#! /bin/sh


# Test  de la vérification contextuelle.
cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"
# Lancement des tests valides contextuellement
# Les resultats attendus sont dans des fichiers nomdufichier.attendu
for i in src/test/deca/context/valid/improvided/*.deca
do   
    if test_context  "$i" 2>&1 | \
    grep -q -e '$i:[0-9]'
    then 
    	echo ${i}
	echo "Echec inattendu pour test_context"
        exit 1

    else 
    	echo "Succes attendu de test_context"
fi 
done
# Lancement des tests invalides contextuellement
# Les resultats attendus sont dans des fichiers nomdufichier.attendu
for i in src/test/deca/context/invalid/improvided/*.deca
do
    if test_context  "$i" 2>&1 | \
    grep -q -e  "$i:[0-9]"
    then
	echo "Echec attendu pour test_context"
    else 
	echo "${rouge}FAILED"
    	echo ${i}
	echo "Succes inattendu pour test_context"
    	exit 1
fi
done

#! /bin/sh

# Test  de la vérification syntaxique.
cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"
# Lancement des tests valides syntaxiquement
for i in src/test/deca/syntax/valid/improvided/*.deca
do
    if test_synt  "$i" 2>&1 | \
    grep -q -e '$i:[0-9]'
    then
        echo ${i}
        echo "Echec inattendu pour test_synt"
        exit 1

    else
        echo "Succes attendu de test_synt"
fi
done
# Lancement des tests invalides syntaxiquement
# Les resultats attendus sont dans des fichiers nomdufichier.attendu
for i in src/test/deca/syntax/invalid/improvided/*.deca

do
    if test_synt  "$i" 2>&1 | \
    grep -q -e "$i:[0-9]"
    then
        echo "Echec attendu pour test_synt"

    else
        echo "${rouge}FAILED"
        echo ${i}
        echo "Succes inattendu pour test_synt"
        exit 1
fi
done

for i in src/test/deca/context/invalid/improvided/*.deca

do
    if test_synt  "$i" 2>&1 | \
    grep -q -e '$i:[0-9]'
    then
        echo ${i}
        echo "Echec inattendu pour test_synt"
        exit 1

    else
        echo "Succes attendu de test_synt"
fi
done


for i in src/test/deca/context/valid/improvided/*.deca
do
    if test_synt  "$i" 2>&1 | \
    grep -q -e '$i:[0-9]'
    then
        echo ${i}
        echo "Echec inattendu pour test_synt"
        exit 1

    else
        echo "Succes attendu de test_synt"
fi
done

for i in src/test/deca/codegen/valid/improvided/*.deca

do
    if test_synt  "$i" 2>&1 | \
    grep -q -e '$i:[0-9]'
    then
        echo ${i}
        echo "Echec inattendu pour test_synt"
        exit 1

    else
        echo "Succes attendu de test_synt"
fi
done

for i in src/test/deca/codegen/invalid/improvided/*.deca

do
    if test_synt  "$i" 2>&1 | \
    grep -q -e '$i:[0-9]'
    then
        echo ${i}
        echo "Echec inattendu pour test_synt"
        exit 1

    else
        echo "Succes attendu de test_synt"
fi
done


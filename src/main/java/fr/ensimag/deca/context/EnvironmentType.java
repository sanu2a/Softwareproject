package fr.ensimag.deca.context;

import fr.ensimag.deca.DecacCompiler;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.Location;
import fr.ensimag.deca.tree.Null;


// A FAIRE: étendre cette classe pour traiter la partie "avec objet" de Déca
/**
 * Environment containing types. Initially contains predefined identifiers, more
 * classes can be added with declareClass().
 *
 * @author gl35
 * @date 01/01/2023
 */
public class EnvironmentType {
    public EnvironmentType(DecacCompiler compiler) {
        
        envTypes = new HashMap<Symbol, TypeDefinition>();
        
        Symbol intSymb = compiler.createSymbol("int");
        INT = new IntType(intSymb);
        envTypes.put(intSymb, new TypeDefinition(INT, Location.BUILTIN));

        Symbol floatSymb = compiler.createSymbol("float");
        FLOAT = new FloatType(floatSymb);
        envTypes.put(floatSymb, new TypeDefinition(FLOAT, Location.BUILTIN));

        Symbol voidSymb = compiler.createSymbol("void");
        VOID = new VoidType(voidSymb);
        envTypes.put(voidSymb, new TypeDefinition(VOID, Location.BUILTIN));

        Symbol booleanSymb = compiler.createSymbol("boolean");
        BOOLEAN = new BooleanType(booleanSymb);
        envTypes.put(booleanSymb, new TypeDefinition(BOOLEAN, Location.BUILTIN));

        Symbol stringSymb = compiler.createSymbol("string");
        STRING = new StringType(stringSymb);
        // not added to envTypes, it's not visible for the user.

        Symbol objectSymb = compiler.createSymbol("Object");
        OBJECT = new ClassType(objectSymb, Location.BUILTIN, null);
        ClassDefinition objectDefinition = new ClassDefinition(OBJECT, Location.BUILTIN, null);
        objectDefinition.setNumberOfMethods(1);
        envTypes.put(objectSymb, objectDefinition);

        Symbol nullSymb = compiler.createSymbol("null");
        NULL = new NullType(nullSymb);
        envTypes.put(nullSymb, new TypeDefinition(NULL, Location.BUILTIN));
    }

    private final Map<Symbol, TypeDefinition> envTypes;

    public TypeDefinition defOfType(Symbol s) {
        return envTypes.get(s);
    }

    public boolean assignCompatible(Type type1, Type type2) {
        if (type1.isFloat() && type2.isInt()) {
            return true;
        } else if (subType(type2, type1)) {
            return true;
        }
        return false;
    }

    public boolean subType(Type type1, Type type2) {
        if (type1.isClass() && type2.isClass()) {
            if (type2.getName().getName().equals("Object")) {
                return true;
            }
            else {
                Set<String> setParentsType = new HashSet<>();
                ClassDefinition typeDefinition = (ClassDefinition) this.defOfType(type1.getName());
                String parentType1 = typeDefinition.getType().getName().getName();
                while (!parentType1.equals("Object")) {
                    setParentsType.add(parentType1);
                    typeDefinition = typeDefinition.getSuperClass();
                    parentType1 = typeDefinition.getType().getName().getName();
                }
                return setParentsType.contains(type2.getName().getName());
            }
        } else if (type1.isNull() && type2.isClass()) {
            return true;
        } else if (type1.sameType(type2) && (!type1.isClass() || !type2.isClass())) {
            return true;
        }
        return false;
    }


    public void addClass(Symbol symbol,TypeDefinition typeDefinition){
        envTypes.put(symbol,typeDefinition);
    }
    public final VoidType    VOID;
    public final IntType     INT;
    public final FloatType   FLOAT;
    public final StringType  STRING;
    public final BooleanType BOOLEAN;
    public final ClassType OBJECT;
    public final NullType NULL;
}

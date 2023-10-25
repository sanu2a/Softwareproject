package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;

import java.util.HashMap;
import java.util.Map;

public class ListDeclField extends TreeList< AbstractDeclField>{


    public void verifyListDeclField(DecacCompiler compiler, ClassDefinition superClass, ClassDefinition currentClass)  throws ContextualError {
        currentClass.setNumberOfFields(superClass.getNumberOfFields());
        for (AbstractDeclField f: this.getList()){
            f.verifyDeclField(compiler,superClass,currentClass);
        }

    }

    public void verifyListFieldInit(DecacCompiler compiler, ClassDefinition currentClass) throws ContextualError{
        for (AbstractDeclField f: this.getList()){
            f.verifyFieldInit(compiler,currentClass);
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclField f : this.getList()){
            f.decompile(s);
        }
    }
}

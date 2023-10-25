package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

public class ListDeclMethod extends TreeList<AbstractDeclMethod>{

    public void verifyListDeclMethod(DecacCompiler compiler, ClassDefinition superClass, ClassDefinition currentClass) throws ContextualError {
        currentClass.setNumberOfMethods(superClass.getNumberOfMethods());
        for (AbstractDeclMethod m: this.getList()){
            m.verifyDeclMethod(compiler,superClass,currentClass);
        }
    }
    public void verifyListMethodBody(DecacCompiler compiler, ClassDefinition currentClass) throws ContextualError{
        for(AbstractDeclMethod m:this.getList()){
            m.verifyMethodBody(compiler,currentClass);
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        for(AbstractDeclMethod m:this.getList()){
            m.decompile(s);
        }
    }

    public void codeGenMethod(DecacCompiler compiler){
        for (AbstractDeclMethod d: getList()){
            d.codeGenMethod(compiler);
        }
    }

}

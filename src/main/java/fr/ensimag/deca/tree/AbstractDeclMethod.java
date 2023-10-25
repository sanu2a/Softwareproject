package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;


public abstract class AbstractDeclMethod extends Tree{
    /**
     * Pass 2 of [SyntaxeContextuelle]
     */
    public abstract void verifyDeclMethod(DecacCompiler compiler, ClassDefinition superClass,ClassDefinition currentClass) throws ContextualError;
    /**
     * Pass 3 of [SyntaxeContextuelle]
     */
    public abstract void verifyMethodBody(DecacCompiler compiler, ClassDefinition currentClass) throws ContextualError;

    public abstract void codeGenMethod(DecacCompiler compiler);

}

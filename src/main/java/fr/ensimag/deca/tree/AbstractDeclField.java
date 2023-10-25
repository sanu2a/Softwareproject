package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;

public abstract class AbstractDeclField extends Tree{
    /**
     * Pass 2 of [SyntaxeContextuelle]
     */
    public abstract void verifyDeclField(DecacCompiler compiler, ClassDefinition superClass, ClassDefinition currentClass) throws ContextualError;
    /**
     * Pass 3 of [SyntaxeContextuelle]
     */
    public abstract void verifyFieldInit(DecacCompiler compiler,ClassDefinition currentClass) throws ContextualError;
}

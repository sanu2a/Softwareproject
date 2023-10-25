package fr.ensimag.deca.tree;


import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.ContextualError;

public abstract class AbstractDeclParam extends Tree{
    @Override
    public void decompile(IndentPrintStream s) {

    }

    public abstract Type verifyDeclParam(DecacCompiler compiler) throws ContextualError;


    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {

    }

    @Override
    protected void iterChildren(TreeFunction f) {

    }

    public void verifyParam(DecacCompiler compiler, EnvironmentExp environmentExp) throws ContextualError{

    }
}

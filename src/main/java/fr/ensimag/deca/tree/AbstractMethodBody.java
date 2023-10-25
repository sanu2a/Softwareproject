package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;

import java.io.PrintStream;

public abstract class AbstractMethodBody extends Tree {

    protected abstract void verifyBody(DecacCompiler compiler, EnvironmentExp envExpParam, ClassDefinition currentClass, Type typeReturn) throws ContextualError;

    public ListDeclVar getListVar() {return null;}

    public ListInst getListinst() {return null;}
}

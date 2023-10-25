package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.context.Type;

import java.io.PrintStream;

public class MethodBody extends AbstractMethodBody {
    final private ListDeclVar listvar;
    final private ListInst listinst;

    public MethodBody(ListDeclVar listvar, ListInst listinst) {
        this.listvar = listvar;
        this.listinst = listinst;
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        listvar.prettyPrint(s, prefix, false);
        listinst.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {

    }

    @Override
    protected void verifyBody(DecacCompiler compiler, EnvironmentExp envExpParam, ClassDefinition currentClass, Type typeReturn) throws ContextualError {
        this.listvar.verifyListDeclVariable(compiler, envExpParam, currentClass);
        this.listinst.verifyListInst(compiler, envExpParam, currentClass, typeReturn);
    }

    public ListDeclVar getListVar() {
        return listvar;
    }

    public ListInst getListinst() {
        return listinst;
    }


    public void decompile(IndentPrintStream s) {
        s.print("{");
        s.println();
        s.indent();
        listvar.decompile(s);
        listinst.decompile(s);
        s.unindent();
        s.print("}");
    }
}
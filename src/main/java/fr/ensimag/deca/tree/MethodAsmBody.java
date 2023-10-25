package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;

import java.io.PrintStream;

public class MethodAsmBody extends AbstractMethodBody{
    private final StringLiteral code;

    public MethodAsmBody(StringLiteral code) {
        this.code = code;
        this.code.setValue(code.getValue().replace("\\\"","\""));
    }

    @Override
    public void verifyBody(DecacCompiler compiler, EnvironmentExp envExpParam, ClassDefinition currentClass, Type typeReturn) throws ContextualError{
        this.code.verifyExpr(compiler,envExpParam,currentClass);
    }
    protected void prettyPrintChildren(PrintStream s, String prefix){
        code.prettyPrint(s,prefix,true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {

    }

    public StringLiteral getCode() {
        return code;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        code.setValue(code.getValue().replace("\"","\\\""));
        s.print("asm(" + code.toString() + ");");
    }
}

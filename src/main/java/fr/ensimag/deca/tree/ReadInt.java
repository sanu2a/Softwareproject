package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;

import java.io.PrintStream;

/**
 *
 * @author gl35
 * @date 01/01/2023
 */
public class ReadInt extends AbstractReadExpr {

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        return compiler.environmentType.INT;
    }

    protected void codeGenInst(DecacCompiler compiler) {

        compiler.addInstruction(new RINT());
        Label l = new Label("readError");
        if (!compiler.isNoCheck()) compiler.addInstruction(new BOV(l));
    }
    @Override
    public void decompile(IndentPrintStream s) {
        s.print("readInt()");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

    public void codeGenDeclVar(ExpDefinition e, DecacCompiler compiler){
        codeGenInst(compiler);
        compiler.addInstruction(new STORE(Register.getR(1), e.getOperand()));
    }

    @Override
    public void codeExp(int n, DecacCompiler compiler){
        codeGenInst(compiler);
        compiler.addInstruction(new LOAD(Register.getR(1), Register.getR(n)));
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler, boolean isHex) {
        codeGenInst(compiler);
        compiler.addInstruction(new WINT());
    }

}

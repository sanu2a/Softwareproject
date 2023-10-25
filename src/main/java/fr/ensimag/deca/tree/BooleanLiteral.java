package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;

import java.io.PrintStream;

/**
 *
 * @author gl35
 * @date 01/01/2023
 */
public class BooleanLiteral extends AbstractExpr {

    private boolean value;

    public BooleanLiteral(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
            this.setType(compiler.environmentType.BOOLEAN);
            return compiler.environmentType.BOOLEAN;
    }


    @Override
    public void decompile(IndentPrintStream s) {
        s.print(Boolean.toString(value));
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

    @Override
    String prettyPrintNode() {
        return "BooleanLiteral (" + value + ")";
    }

    public void codeGenDeclVar(ExpDefinition e, DecacCompiler compiler){
        compiler.addInstruction(new LOAD(new ImmediateInteger(getValue()?1:0), Register.getR(2)));
        compiler.addInstruction(new STORE(Register.getR(2), e.getOperand()));
    }

    @Override
    public void codeExp(int n, DecacCompiler compiler){
        compiler.addInstruction(new LOAD(new ImmediateInteger(getValue()?1:0), Register.getR(n)));
        compiler.setMaxRegistreUtilisee(n);
    }

    @Override
    protected void codeBranch(DecacCompiler compiler, boolean b, Label l){
        if (getValue()) {
            if (b) compiler.addInstruction(new BRA(l));
        }
        else {
            if (!b) compiler.addInstruction(new BRA(l));
        }
    }

}

package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.instructions.WSTR;

import java.io.PrintStream;

/**
 * Integer literal
 *
 * @author gl35
 * @date 01/01/2023
 */
public class IntLiteral extends AbstractExpr {
    public int getValue() {
        return value;
    }

    private int value;

    public IntLiteral(int value) {
        this.value = value;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
            this.setType(compiler.environmentType.INT);
            return compiler.environmentType.INT;
        //throw new UnsupportedOperationException("not yet implemented");
    }
    @Override
    protected void codeGenPrint(DecacCompiler compiler, boolean isHex) {
        compiler.addInstruction(new WSTR(new ImmediateString(String.valueOf(value))));
    }
    @Override
    String prettyPrintNode() {
        return "Int (" + getValue() + ")";
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(Integer.toString(value));
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
    public void codeExp(int n, DecacCompiler compiler){
        compiler.addInstruction(new LOAD(new ImmediateInteger(getValue()), Register.getR(n)));
        compiler.setMaxRegistreUtilisee(n);
    }
    /*@Override
    public  void codeCast(int n , DecacCompiler compiler){
        compiler.addInstruction((new FLOAT(new ImmediateInteger(getValue()),Register.getR(2))));
    }*/

    @Override
    public DVal dval(DecacCompiler compiler){
        return new ImmediateInteger(value);
    }

}

package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.ImmediateString;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.commons.lang.Validate;

/**
 * Single precision, floating-point literal
 *
 * @author gl35
 * @date 01/01/2023
 */
public class FloatLiteral extends AbstractExpr {

    public float getValue() {
        return value;
    }

    private float value;

    public FloatLiteral(float value) {

        Validate.isTrue(!Float.isInfinite(value),
                "literal values cannot be infinite");
        Validate.isTrue(!Float.isNaN(value),
                "literal values cannot be NaN");
        this.value = value;
        /*
        catch (IllegalArgumentException e){
            System.out.println("The given float" + value+" is too big");
            System.exit(1);
            //throw new IllegalArgumentException("The given float is too big");
        }*/
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
            this.setType(compiler.environmentType.FLOAT);
            return compiler.environmentType.FLOAT;
        //throw new UnsupportedOperationException("not yet implemented");
    }
    @Override
    protected void codeGenPrint(DecacCompiler compiler, boolean isHex) {
        codeExp(1, compiler);
        if (isHex) compiler.addInstruction(new WFLOATX());
        else compiler.addInstruction(new WFLOAT());
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(java.lang.Float.toHexString(value));
    }

    @Override
    String prettyPrintNode() {
        return "Float (" + getValue() + ")";
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
        compiler.addInstruction(new LOAD(new ImmediateFloat(getValue()), Register.getR(n)));
        compiler.setMaxRegistreUtilisee(n);
    }
    /*@Override
    public  void codeCast(int n, DecacCompiler compiler){
        compiler.addInstruction(new INT(new ImmediateFloat(getValue()),Register.getR(2)));

    }*/

}

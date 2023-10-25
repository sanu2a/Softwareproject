package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Line;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;

/**
 * Conversion of an int into a float. Used for implicit conversions.
 * 
 * @author gl35
 * @date 01/01/2023
 */
public class ConvFloat extends AbstractUnaryExpr {
    public ConvFloat(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) {
        this.setType(compiler.environmentType.FLOAT);
        return compiler.environmentType.FLOAT;
    }


    @Override
    public String getOperatorName() {
        return "/* conv float */";
    }

    /*public void codeCast(int n , DecacCompiler compiler){
        getOperand().codeCast(n,compiler);
    }*/

    public void codeExp(int n, DecacCompiler compiler){
        {
            //9lb registrat a w9 lmax ou dakchi rak fahem
            //System.out.println("hahayo"+getOperand().getType());
            getOperand().codeExp(n, compiler);
            if (getOperand().getType().isInt()) compiler.addInstruction(new FLOAT(Register.getR(n), Register.getR(n)));

        }
    }
    @Override
    public boolean isConvFloat(){
        return true;
    }
}

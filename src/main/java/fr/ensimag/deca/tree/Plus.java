package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.ImmediateString;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;

/**
 * @author gl35
 * @date 01/01/2023
 */
public class    Plus extends AbstractOpArith {
    public Plus(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "+";
    }


    @Override
    public void codeExp(int n, DecacCompiler compiler){
        super.codeExp(n, compiler);
        if (getRightOperand().dval(compiler) != null) {
            compiler.addInstruction(new ADD(getRightOperand().dval(compiler), Register.getR(n)));
        }
        else {
            if (n < compiler.getRMax()){
                compiler.addInstruction(new ADD(Register.getR(n+1), Register.getR(n)));
            }
            else {
                compiler.addInstruction(new ADD(Register.getR(0), Register.getR(n)));
            }
        }
        if (!compiler.isNoCheck() && getType().isFloat()) {
            Label l = new Label("OV");
            compiler.addInstruction(new BOV(l));
        }

    }



}

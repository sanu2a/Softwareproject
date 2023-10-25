package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;
import fr.ensimag.deca.context.Type;

/**
 * @author gl35
 * @date 01/01/2023
 */
public class Multiply extends AbstractOpArith {
    public Multiply(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "*";
    }

    @Override
    public void codeExp(int n, DecacCompiler compiler){
        super.codeExp(n, compiler);
        if (getRightOperand().dval(compiler) != null) {
            compiler.addInstruction(new MUL(getRightOperand().dval(compiler), Register.getR(n)));
        }
        else {
            if (n < compiler.getRMax()){
                compiler.addInstruction(new MUL(Register.getR(n+1), Register.getR(n)));
            }
            else {
                compiler.addInstruction(new MUL(Register.getR(0), Register.getR(n)));
            }
        }
        if (!compiler.isNoCheck() && getType().isFloat()) {
            Label l = new Label("OV");
            compiler.addInstruction(new BOV(l));
        }
    }
}

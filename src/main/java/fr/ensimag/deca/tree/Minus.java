package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;

/**
 * @author gl35
 * @date 01/01/2023
 */
public class Minus extends AbstractOpArith {
    public Minus(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "-";
    }


    @Override
    public void codeExp(int n, DecacCompiler compiler) {
        super.codeExp(n, compiler);
        if (getRightOperand().dval(compiler) != null) {
            compiler.addInstruction(new SUB(getRightOperand().dval(compiler), Register.getR(n)));
        } else {
            if (n < compiler.getRMax()) {
                compiler.addInstruction(new SUB(Register.getR(n + 1), Register.getR(n)));
            } else {
                compiler.addInstruction(new SUB(Register.getR(0), Register.getR(n)));
            }
        }
        if (!compiler.isNoCheck() &&getType().isFloat()) {
            Label l = new Label("OV");
            compiler.addInstruction(new BOV(l));
        }
    }
    
}

package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.FloatType;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;
import fr.ensimag.deca.context.Type;

/**
 *
 * @author gl35
 * @date 01/01/2023
 */
public class Divide extends AbstractOpArith {
    public Divide(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "/";
    }


    @Override
    public void codeExp(int n, DecacCompiler compiler){
        super.codeExp(n, compiler);
        if (getRightOperand().dval(compiler) != null) {
            if (getType().isFloat()) compiler.addInstruction(new DIV(getRightOperand().dval(compiler), Register.getR(n)));
            else compiler.addInstruction(new QUO(getRightOperand().dval(compiler), Register.getR(n)));
        }
        else {
            if (n < compiler.getRMax()){
                if (getType().isFloat()) compiler.addInstruction(new DIV(Register.getR(n+1), Register.getR(n)));
                else compiler.addInstruction(new QUO(Register.getR(n+1), Register.getR(n)));
            }
            else {
                if (getType().isFloat()) compiler.addInstruction(new DIV(Register.getR(0), Register.getR(n)));
                else compiler.addInstruction(new QUO(Register.getR(0), Register.getR(n)));

            }
        }
        if (!compiler.isNoCheck()) {
            Label l = new Label("OV");
            compiler.addInstruction(new BOV(l));
        }
    }
}

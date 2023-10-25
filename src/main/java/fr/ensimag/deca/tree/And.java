package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Line;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.instructions.*;
import fr.ensimag.deca.context.Type;

/**
 *
 * @author gl35
 * @date 01/01/2023
 */
public class And extends AbstractOpBool {

    public And(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "&&";
    }

    private static int compteur = 0;

    @Override
    public void codeExp(int n, DecacCompiler compiler){
        {
            getLeftOperand().codeExp(n, compiler);
            compiler.addInstruction(new CMP(0, Register.getR(n)));
            Label l = new Label("initand0"+String.valueOf(compteur));
            Label l1 = new Label("initand1"+String.valueOf(compteur));
            compteur++;
            compiler.addInstruction(new BEQ(l));
            getRightOperand().codeExp(n, compiler);
            compiler.addInstruction(new CMP(new ImmediateInteger(0), Register.getR(n)));
            compiler.addInstruction(new BEQ(l));
            compiler.addInstruction(new LOAD(1, Register.getR(n)));
            compiler.addInstruction(new BRA(l1));
            compiler.add(new Line(l, new LOAD(0, Register.getR(n)), null));
            compiler.add(new Line(l1, null, null));
        }
    }

    @Override
    protected void codeBranch(DecacCompiler compiler, boolean b, Label l){
        if (b) {
            Label l1 = new Label("And_Fin"+String.valueOf(compteur));
            compteur++;
            getLeftOperand().codeBranch(compiler, false, l1);
            getRightOperand().codeBranch(compiler, true, l);
            compiler.addLabel(l1);
        }
        else {
            getLeftOperand().codeBranch(compiler, false, l);
            getRightOperand().codeBranch(compiler, false, l);
        }
    }

}

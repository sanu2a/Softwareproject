package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Line;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;
import fr.ensimag.deca.context.Type;

/**
 *
 * @author gl35
 * @date 01/01/2023
 */
public class Or extends AbstractOpBool {

    public Or(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "||";
    }

    private static int compteur = 0;

    @Override
    public void codeExp(int n, DecacCompiler compiler){
        {
            //9lb registrat a w9 lmax ou dakchi rak fahem
            getLeftOperand().codeExp(n, compiler);
            compiler.addInstruction(new CMP(new ImmediateInteger(1), Register.getR(n)));
            Label l = new Label("initor0"+String.valueOf(compteur));
            Label l1 = new Label("initor1"+String.valueOf(compteur));
            compteur++;
            compiler.addInstruction(new BEQ(l));
            getRightOperand().codeExp(n, compiler);
            compiler.addInstruction(new CMP(new ImmediateInteger(1), Register.getR(n)));
            compiler.addInstruction(new BEQ(l));
            compiler.addInstruction(new LOAD(new ImmediateInteger(0), Register.getR(n)));
            compiler.addInstruction(new BRA(l1));
            compiler.add(new Line(l, new LOAD(new ImmediateInteger(1), Register.getR(n)), null));
            compiler.add(new Line(l1, null, null));
            //codeGenHandler(compiler, 0, n);
        }
    }

    @Override
    protected void codeBranch(DecacCompiler compiler, boolean b, Label l){
        if (!b) {
            Label l1 = new Label("Or_Fin"+String.valueOf(compteur));
            compteur++;
            getLeftOperand().codeBranch(compiler, true, l1);
            getRightOperand().codeBranch(compiler, false, l);
            compiler.addLabel(l1);
        }
        else {
            getLeftOperand().codeBranch(compiler, true, l);
            getRightOperand().codeBranch(compiler, true, l);
        }
    }


}

package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;


/**
 *
 * @author gl35
 * @date 01/01/2023
 */
public class    Equals extends AbstractOpExactCmp {

    public Equals(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "==";
    }


    public void codeExp(int n, DecacCompiler compiler){
            //9lb registrat a w9 lmax ou dakchi rak fahem
        super.codeExp(n, compiler);
            /*compiler.addInstruction(new BEQ(l));
            compiler.addInstruction(new LOAD(new ImmediateInteger(0), Register.getR(n)));
            compiler.addInstruction(new BRA(l1));
            compiler.add(new Line(l, new LOAD(new ImmediateInteger(1), Register.getR(n)), null));
            compiler.add(new Line(l1, null, null));*/
        compiler.addInstruction(new SEQ(Register.getR(n)));
    }

    @Override
    protected void codeBranch(DecacCompiler compiler, boolean b, Label l){
        super.codeExp(2, compiler);
        if (b) compiler.addInstruction(new BEQ(l));
        else compiler.addInstruction(new BNE(l));
    }

}

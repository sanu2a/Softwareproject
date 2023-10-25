package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Line;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;

/**
 *
 * @author gl35
 * @date 01/01/2023
 */
public class Greater extends AbstractOpIneq {

    public Greater(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return ">";
    }


    public void codeExp(int n, DecacCompiler compiler){
        //9lb registrat a w9 lmax ou dakchi rak fahem
        super.codeExp(n, compiler);
        /*compiler.addInstruction(new BGT(l));
        compiler.addInstruction(new LOAD(new ImmediateInteger(0), Register.getR(n)));
        compiler.addInstruction(new BRA(l1));
        compiler.add(new Line(l, new LOAD(new ImmediateInteger(1), Register.getR(n)), null));
        compiler.add(new Line(l1, null, null));*/
        compiler.addInstruction(new SGT(Register.getR(n)));
    }

    @Override
    protected void codeBranch(DecacCompiler compiler, boolean b, Label l){
        super.codeExp(2, compiler);
        if (b) compiler.addInstruction(new BGT(l));
        else compiler.addInstruction(new BLE(l));
    }
}

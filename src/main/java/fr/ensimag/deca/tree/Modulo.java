package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;

/**
 *
 * @author gl35
 * @date 01/01/2023
 */
public class Modulo extends AbstractOpArith {

    public Modulo(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type type1 = this.getLeftOperand().verifyExpr(compiler,localEnv,currentClass);
        this.getLeftOperand().setType(type1);
        Type type2 = this.getRightOperand().verifyExpr(compiler,localEnv,currentClass);
        this.getRightOperand().setType(type2);
        if (type1.isInt() && type2.isInt()){
            return type1;
        }
        else{
            throw new ContextualError("The arithmetic operation " + this.getOperatorName() + " cannot be done due to the given types. (3.33)",this.getLocation());
        }
    }


    @Override
    protected String getOperatorName() {
        return "%";
    }


    @Override
    public void codeExp(int n, DecacCompiler compiler){
        super.codeExp(n, compiler);
        if (getRightOperand().dval(compiler) != null) {
            compiler.addInstruction(new REM(getRightOperand().dval(compiler), Register.getR(n)));
        }
        else {
            if (n < compiler.getRMax()){
                compiler.addInstruction(new REM(Register.getR(n+1), Register.getR(n)));
            }
            else {
                compiler.addInstruction(new REM(Register.getR(0), Register.getR(n)));
            }
        }
        if (!compiler.isNoCheck()) {
            Label l = new Label("OV");
            compiler.addInstruction(new BOV(l));
        }
    }
}

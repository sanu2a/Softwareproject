package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;

/**
 * @author gl35
 * @date 01/01/2023
 */
public class UnaryMinus extends AbstractUnaryExpr {

    public UnaryMinus(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type type = this.getOperand().verifyExpr(compiler,localEnv,currentClass);
        if (type.isInt() || type.isFloat()){
            this.getOperand().setType(type);
            this.setType(type);
            return type;
        }
        else{
            throw new ContextualError("The unary operation " + this.getOperatorName() + " cannot be done due to the given type. (3.62)",this.getLocation());
        }
    }




    @Override
    protected String getOperatorName() {
        return "-";
    }


    @Override
    public void codeExp(int n, DecacCompiler compiler){
        super.codeExp(n, compiler);
        compiler.addInstruction(new OPP(Register.getR(n), Register.getR(n)));

    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler, boolean isHex) {
        codeExp(1, compiler);
        if (getType().isInt()) compiler.addInstruction(new WINT());
        if (getType().isFloat()) {
            if (isHex) compiler.addInstruction(new WFLOATX());
            else compiler.addInstruction(new WFLOAT());
        }

    }

}

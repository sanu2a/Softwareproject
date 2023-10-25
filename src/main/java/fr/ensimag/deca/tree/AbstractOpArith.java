package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Arithmetic binary operations (+, -, /, ...)
 * 
 * @author gl35
 * @date 01/01/2023
 */
public abstract class AbstractOpArith extends AbstractBinaryExpr {

    public AbstractOpArith(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type type1 = this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type type2 = this.getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        /*A revoir, le set se fait avant.*/
        this.getLeftOperand().setType(type1);
        this.getRightOperand().setType(type2);
        if (type1.isInt() && type2.isInt()) {
            this.setType(type1);
            return type1;
        } else if (type1.isInt() && type2.isFloat()) {
            this.setLeftOperand(new ConvFloat(this.getLeftOperand()));
            this.setType(type2);
            return type2;
        } else if (type1.isFloat() && type2.isInt()) {
            this.setRightOperand(new ConvFloat(this.getRightOperand()));
            this.setType(type1);
            return type1;
        } else if (type1.isFloat() && type2.isFloat()) {
            this.setType(type1);
            return type1;
        } else {
            throw new ContextualError("The arithmetic operation " + this.getOperatorName() + " cannot be done due to the given types. (3.33)", this.getLocation());
        }
        //throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public void codeExp(int n, DecacCompiler compiler){
        getLeftOperand().codeExp(n, compiler);
        compiler.setMaxRegistreUtilisee(n);
        if (getRightOperand().dval(compiler) != null) return;
        if (n < compiler.getRMax()) {
            compiler.setMaxRegistreUtilisee(n+1);
            getRightOperand().codeExp(n+1, compiler);
        }
        else {
            compiler.addInstruction(new PUSH(Register.getR(n)));
            compiler.incrementeNbPush();
            getRightOperand().codeExp(n, compiler);
            compiler.addInstruction(new LOAD(Register.getR(n), Register.getR(0)));
            compiler.addInstruction(new POP(Register.getR(n)));
            compiler.decrementeNbPush();
        }
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler, boolean isHex) {
        codeExp(2, compiler);
        compiler.setMaxRegistreUtilisee(2);
        compiler.addInstruction(new LOAD(Register.getR(2), Register.getR(1)));
        if (getType().isInt()) compiler.addInstruction(new WINT());
        if (getType().isFloat()) {
            if (isHex) compiler.addInstruction(new WFLOATX());
            else compiler.addInstruction(new WFLOAT());
        }
    }
}

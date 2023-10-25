package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Line;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author gl35
 * @date 01/01/2023
 */
public abstract class AbstractOpCmp extends AbstractBinaryExpr {


    public AbstractOpCmp(AbstractExpr leftOperand, AbstractExpr rightOperand){
        super(leftOperand,rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type type1 = this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type type2 = this.getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        this.getLeftOperand().setType(type1);
        this.getRightOperand().setType(type2);
        if (type1.isBoolean() && type2.isBoolean()) {
            if (this.getOperatorName().equals("==") || this.getOperatorName().equals("!=")) {
                this.setType(type1);
                return type1;
            } else {
                throw new ContextualError("The given comparison operator cannot be done between two boolean. (3.33)", this.getLocation());
            }
        } else if (type1.isInt() && type2.isFloat()) {
            this.setLeftOperand(this.getLeftOperand().verifyRValue(compiler, localEnv, currentClass, type2));
            this.getLeftOperand().setType(compiler.environmentType.FLOAT);
            Type type = compiler.environmentType.BOOLEAN;
            this.setType(type);
            return type;
        } else if (type1.isFloat() && type2.isInt()) {
            this.setRightOperand(this.getRightOperand().verifyRValue(compiler, localEnv, currentClass, type1));
            this.getRightOperand().setType(compiler.environmentType.FLOAT);
            Type type = compiler.environmentType.BOOLEAN;
            this.setType(type);
            return type;
        } else if ((type1.isInt() && type2.isInt()) || (type1.isFloat() && type2.isFloat())) {
            Type type = compiler.environmentType.BOOLEAN;
            this.setType(type);
            return type;
        }
        else if (((this.getOperatorName().equals("==") || this.getOperatorName().equals("!=")) && (type1.isClassOrNull() && type2.isClassOrNull()))) {
            Type type = compiler.environmentType.BOOLEAN;
            this.setType(type);
            return type;
        }
         else {
            throw new ContextualError("The given comparison "+ this.getOperatorName() +  " operator cannot be done between the given types. (3.33)", this.getLocation());
        }

    }


    @Override
    public void codeExp(int n, DecacCompiler compiler){
        getLeftOperand().codeExp(n, compiler);
        if (getRightOperand().dval(compiler) != null) {
            compiler.addInstruction(new CMP(getRightOperand().dval(compiler), Register.getR(n)));
            return;
        }
        if (n < compiler.getRMax()) {
            compiler.setMaxRegistreUtilisee(n+1);
            getRightOperand().codeExp(n+1, compiler);
            compiler.addInstruction(new CMP(Register.getR(n + 1), Register.getR(n)));
        }
        else {
            compiler.addInstruction(new PUSH(Register.getR(n)));
            compiler.incrementeNbPush();
            getRightOperand().codeExp(n, compiler);
            compiler.addInstruction(new LOAD(Register.getR(n), Register.getR(0)));
            compiler.addInstruction(new POP(Register.getR(n)));
            compiler.decrementeNbPush();
            compiler.addInstruction(new CMP(Register.getR(0), Register.getR(n)));
        }
    }


}

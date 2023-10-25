package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author gl35
 * @date 01/01/2023
 */
public abstract class AbstractOpBool extends AbstractBinaryExpr {

    public AbstractOpBool(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type type1 = this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type type2 = this.getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        if (type1.isBoolean() && type2.isBoolean()) {
            this.getLeftOperand().setType(type1);
            this.getRightOperand().setType(type2);
            this.setType(type1);
            return type1;
        } else {
            throw new ContextualError("The boolean operation " +  this.getOperatorName() + " cannot be done due to the given types. (3.33)",this.getLocation());
        }
    }

}

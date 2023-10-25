package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;

/**
 * Left-hand side value of an assignment.
 * 
 * @author gl35
 * @date 01/01/2023
 */
public abstract class AbstractLValue extends AbstractExpr {
    @Override
    public Type verifyExpr(DecacCompiler compiler,
                           EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        ExpDefinition expDefinition = localEnv.get(((Identifier) this).getName());
        if (expDefinition != null && !expDefinition.isMethod()) {
            Type type = expDefinition.getType();
            ((Identifier) this).setDefinition(expDefinition);
            this.setType(type);
            return type;
        } else {
            if (expDefinition == null) {
                throw new ContextualError("The left value you are using is not defined (3.67) ", this.getLocation());
            } else {
                throw new ContextualError("A left value cannot be a method (3.67)", this.getLocation());
            }
        }
    }

}
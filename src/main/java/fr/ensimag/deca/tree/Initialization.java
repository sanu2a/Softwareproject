package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import org.apache.commons.lang.Validate;

/**
 * @author gl35
 * @date 01/01/2023
 */
public class Initialization extends AbstractInitialization {

    public AbstractExpr getExpression() {
        return expression;
    }

    private AbstractExpr expression;

    public void setExpression(AbstractExpr expression) {
        Validate.notNull(expression);
        this.expression = expression;
    }

    public Initialization(AbstractExpr expression) {
        Validate.notNull(expression);

        this.expression = expression;
    }

    @Override
    protected void verifyInitialization(DecacCompiler compiler, Type t,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        AbstractExpr expr = expression.verifyRValue(compiler, localEnv, currentClass, t);
        if (expr.isConvFloat()){
            this.setExpression(expr);
            this.getExpression().setType(compiler.environmentType.FLOAT);
        }
    }


    @Override
    public void decompile(IndentPrintStream s) {
        s.print(" = ");
        this.expression.decompile(s);
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        expression.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expression.prettyPrint(s, prefix, true);
    }

    @Override
    public void codeGenDeclVar(ExpDefinition e, DecacCompiler compiler){
        AbstractExpr ex = getExpression();
        ex.codeExp(2, compiler);
        compiler.addInstruction(new STORE(Register.getR(2), e.getOperand()));

    }
    /*@Override
    public void codeCast(int n, DecacCompiler compiler) {
        expression.codeCast(n, compiler);
    }*/


}

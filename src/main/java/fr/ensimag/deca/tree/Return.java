package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;

import java.io.PrintStream;

public class Return extends AbstractInst{
    private AbstractExpr exp;

    private AbstractIdentifier methodName;

    public Return(AbstractExpr exp) {
        this.exp = exp;
    }
    public void setExpression(AbstractExpr expr){
            this.exp = expr;
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass, Type returnType) throws ContextualError {
        if (!returnType.isVoid()){
            AbstractExpr expression = this.exp.verifyRValue(compiler,localEnv,currentClass,returnType);
            if (expression.isConvFloat()){
                this.setExpression(expression);
                this.exp.setType(compiler.environmentType.FLOAT);
            }
            methodName = returnType.getMethodName();
        }
        else{
            throw new ContextualError("A method with void type to return should not have a return statement. (3.24)",this.getLocation());
        }
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        exp.codeExp(2, compiler);
        compiler.addInstruction(new LOAD(Register.getR(2), Register.getR(0)));
        compiler.addInstruction(new BRA(new Label("fin."+methodName.getMethodDefinition().getLabel().toString())));
    }

    @Override
    public void decompile(IndentPrintStream s) {
            s.print("return ");
            this.exp.decompile(s);
            s.print(";");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        exp.prettyPrint(s,prefix,true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {

    }
    @Override
    public boolean isReturn(){
        return true;
    }

}

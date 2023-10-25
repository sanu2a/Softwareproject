package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

import java.io.PrintStream;

public class This extends AbstractExpr {

    private Type type;

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        if (currentClass!=null) {
            this.setType(currentClass.getType());
            this.type = currentClass.getType();
            return currentClass.getType();
        } else {
            throw new ContextualError("This cannot be used at the main. (3.43)", this.getLocation());
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("this");
    }


    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
    }

    @Override
    protected void iterChildren(TreeFunction f) {

    }

    @Override
    public DVal dval(DecacCompiler compiler){
        return new RegisterOffset(-2, Register.LB);
    }

    @Override
    public void codeExp(int n, DecacCompiler compiler){
        compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.getR(n)));
    }

    @Override
    boolean isImplicit() {
        return true;
    }
}

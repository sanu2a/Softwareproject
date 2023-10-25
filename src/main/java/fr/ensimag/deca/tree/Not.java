package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
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
public class Not extends AbstractUnaryExpr {

    public Not(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type type = this.getOperand().verifyExpr(compiler,localEnv,currentClass);
        if (type.isBoolean()){
            this.getOperand().setType(type);
            this.setType(type);
            return type;
        }
        else {
            throw new ContextualError("The unary operation " + this.getOperatorName() + " cannot be done due to the given type. (3.63)", this.getLocation());

        }
    }


    @Override
    protected String getOperatorName() {
        return "!";
    }
    private static int compteur = 0;


    @Override
    public void codeExp(int n, DecacCompiler compiler){
        {
            //9lb registrat a w9 lmax ou dakchi rak fahem
            //getOperand().codeGenHandler(compiler, 1, n);
            getOperand().codeExp(n, compiler);
            compiler.addInstruction(new CMP(new ImmediateInteger(0), Register.getR(n)));
            Label l = new Label("initnot0"+String.valueOf(compteur));
            Label l1 = new Label("initnot1"+String.valueOf(compteur));
            compteur++;
            compiler.addInstruction(new BEQ(l));
            compiler.addInstruction(new LOAD(new ImmediateInteger(0), Register.getR(n)));
            compiler.addInstruction(new BRA(l1));
            compiler.add(new Line(l, new LOAD(new ImmediateInteger(1), Register.getR(n)), null));
            compiler.add(new Line(l1, null, null));

        }
    }

    @Override
    protected void codeBranch(DecacCompiler compiler, boolean b, Label l){
        getOperand().codeBranch(compiler, !b, l);
    }
}

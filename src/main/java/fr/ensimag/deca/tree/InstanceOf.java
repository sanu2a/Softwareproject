package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;

import java.io.PrintStream;

public class InstanceOf extends AbstractExpr {
    private AbstractExpr exp;
    private AbstractIdentifier nomClasseCompare;

    public InstanceOf (AbstractExpr exp, AbstractIdentifier nomClasseCompare){
        this.exp = exp;
        this.nomClasseCompare = nomClasseCompare;

    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
                           ClassDefinition currentClass) throws ContextualError {
                Type type1 = this.exp.verifyExpr(compiler,localEnv,currentClass);
                Type type2 = this.nomClasseCompare.verifyType(compiler);
                if (type1.isClassOrNull() && type2.isClass()){
                    this.setType(compiler.environmentType.BOOLEAN);
                    return compiler.environmentType.BOOLEAN;
                }
                else{
                    throw new ContextualError("You cannot use the instance of with the given types (3.40) ",this.getLocation());
                }

    }
    @Override
    public void decompile(IndentPrintStream s){
        s.print("(");
        exp.decompile(s);
        s.print(" instanceof ");
        nomClasseCompare.decompile(s);
        s.print(")");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {

    }

    @Override
    protected void iterChildren(TreeFunction f){

    }
    private static int compteur = 0;
    @Override
    public void codeExp(int n, DecacCompiler compiler){
        if (exp.dval(compiler)!= null)
        {
            compiler.addInstruction(new LOAD(exp.dval(compiler), Register.getR(n)));
        }
        else {
            exp.codeExp(n, compiler);
        }
        //compiler.addInstruction(new LOAD(compiler.envExp.get(((Identifier)exp).getName()).getOperand(), Register.getR(n)));
        compiler.addInstruction(new LEA(nomClasseCompare.getClassDefinition().getOperand(), Register.R0));
        compiler.addInstruction(new CMP(new NullOperand(), Register.getR(n)));
        compiler.addInstruction(new BNE(new Label("instanceof"+String.valueOf(compteur))));
        compiler.addInstruction(new LOAD(new ImmediateInteger(1), Register.getR(n)));
        compiler.addInstruction(new BRA(new Label("fininstanceof"+String.valueOf(compteur))));
        compiler.addLabel(new Label("instanceof"+String.valueOf(compteur)));
        compiler.addInstruction(new CMP(new NullOperand(), Register.getR(n)));
        compiler.addInstruction(new BEQ(new Label("instanceofnull"+String.valueOf(compteur))));
        compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.getR(n)),Register.getR(n)));
        compiler.addInstruction(new CMP(Register.R0, Register.getR(n)));
        compiler.addInstruction(new BNE(new Label("instanceof"+String.valueOf(compteur))));
        compiler.addInstruction(new LOAD(new ImmediateInteger(1), Register.getR(n)));
        compiler.addInstruction(new BRA(new Label("fininstanceof"+String.valueOf(compteur))));
        compiler.addLabel(new Label("instanceofnull"+String.valueOf(compteur)));
        compiler.addInstruction(new LOAD(new ImmediateInteger(0), Register.getR(n)));
        compiler.addLabel(new Label("fininstanceof"+String.valueOf(compteur)));
        compteur++;
    }

    @Override
    protected void codeBranch(DecacCompiler compiler, boolean b, Label l){
        codeExp(2, compiler);
        compiler.addInstruction(new CMP(new ImmediateInteger(0), Register.getR(2)));
        if (b) compiler.addInstruction(new BNE(l));
        else compiler.addInstruction(new BEQ(l));
    }

}

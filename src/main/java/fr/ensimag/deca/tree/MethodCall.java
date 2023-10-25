package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;

import java.io.PrintStream;

public class MethodCall extends AbstractExpr {
    private AbstractExpr exp;
    private AbstractIdentifier methodName;
    private ListExpr rvalueStar;

    public MethodCall(AbstractExpr exp, AbstractIdentifier methodName, ListExpr rvalueStar) {
        this.exp = exp;
        this.methodName = methodName;
        this.rvalueStar = rvalueStar;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        Type typeClass;

        if (exp == null && currentClass == null){
            throw new ContextualError("The method is called in the main without an identifier. (3.71)",this.getLocation());
        }
        if (exp != null && currentClass == null) {
                typeClass = this.exp.verifyExpr(compiler, localEnv, currentClass);
        }
        else {
            if (exp!=null){
            typeClass = this.exp.verifyExpr(compiler, localEnv, currentClass);
            }
            else {
                typeClass = currentClass.getType();
            }
        }
        if (typeClass.isClass()) {
            EnvironmentExp environmentExp = ((ClassDefinition) compiler.environmentType.defOfType(typeClass.getName())).getMembers();
            ExpDefinition methodDefinition = environmentExp.get(this.methodName.getName());
            if (environmentExp.get(this.methodName.getName()) != null) {
                Type typeMethod = environmentExp.get(this.methodName.getName()).getType();
                if (methodDefinition != null && methodDefinition.isMethod()) {
                    this.methodName.setDefinition(methodDefinition);
                    this.methodName.setType(methodDefinition.getType());
                    Signature signature = ((MethodDefinition) methodDefinition).getSignature();
                    int index = 0;
                    if (this.rvalueStar.getList().size() == signature.size()) {
                        for (AbstractExpr e : this.rvalueStar.getList()) {
                            AbstractExpr exp = e.verifyRValue(compiler, localEnv, currentClass, signature.paramNumber(index));
                            if (exp.isConvFloat()){
                                this.rvalueStar.set(index,exp);
                                this.rvalueStar.getList().get(index).setType(compiler.environmentType.FLOAT);

                            }
                            index++;
                        }
                    } else {
                        throw new ContextualError("The given signature doesn't match with the signature of the used method (3.74)", this.getLocation());
                    }

                }
                return typeMethod;
            }
            else {
                throw new ContextualError("You're trying to use a method which is not defined in the current class. (3.71)",this.getLocation());
            }
    } else {
            throw new ContextualError("You cannot apply the given method to a such type. (3.71)", this.getLocation());
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        if (exp!=null){
            exp.decompile(s);
            s.print(".");
        }
        this.methodName.decompile(s);
        s.print("(");
        this.rvalueStar.decompile(s);
        s.print(")");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        if (exp!= null) {
            exp.prettyPrint(s, prefix, false);
        }
        methodName.prettyPrint(s,prefix,false);
        rvalueStar.prettyPrint(s,prefix,true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {

    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        compiler.setNbParam(rvalueStar.size()+1);
        compiler.addInstruction(new ADDSP(rvalueStar.size()+1));
        if (exp == null) {
            compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.getR(2)));
        }
        else {
            if (exp.dval(compiler) != null) {
                compiler.addInstruction(new LOAD(exp.dval(compiler), Register.getR(2)));
            }
            else {
                exp.codeExp(2, compiler);
            }
        }
        compiler.addInstruction(new STORE(Register.getR(2), new RegisterOffset(0, Register.SP)));
        int i = -1;
        for(AbstractExpr e: rvalueStar.getList()){
            e.codeExp(2, compiler);
            compiler.addInstruction(new STORE(Register.getR(2), new RegisterOffset(i, Register.SP)));
            //((Identifier)e).getExpDefinition().setOperand(new RegisterOffset(i, Register.SP));
            //System.out.println(methodName);
            i--;
        }
        compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.SP), Register.getR(2)));
        if (!compiler.isNoCheck()) {
            compiler.addInstruction(new CMP(new NullOperand(), Register.getR(2)));
            compiler.addInstruction(new BEQ(new Label("dereferencement_null")));
        }
        compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.getR(2)), Register.getR(2)));
        compiler.addInstruction(new BSR(new RegisterOffset(methodName.getMethodDefinition().getIndex(),Register.getR(2))));
        compiler.setMaxRegistreUtilisee(2);
        compiler.addInstruction(new SUBSP(rvalueStar.size()+1));
    }

    protected void codeGenPrint(DecacCompiler compiler, boolean isHex) {
        codeGenInst(compiler);
        compiler.addInstruction(new LOAD(Register.getR(0), Register.getR(1)));
        if (methodName.getType().isInt()) {
            compiler.addInstruction(new WINT());
        }
        else {
            if (isHex) compiler.addInstruction(new WFLOATX());
            else compiler.addInstruction(new WFLOAT());
        }
    }

    @Override
    public void codeExp(int n, DecacCompiler compiler) {
        int i = compiler.getMaxRegistreUtilisee();
        int j = i;
        while (i >= 2) {
            compiler.addInstruction(new PUSH(Register.getR(i)));
            i--;
        }
        codeGenInst(compiler);
        i = 2;
        while (i <= j) {
            compiler.addInstruction(new POP(Register.getR(i)));
            i++;
        }
        compiler.setMaxRegistreUtilisee(n);
        compiler.addInstruction(new LOAD(Register.getR(0), Register.getR(n)));
    }

    @Override
    protected void codeBranch(DecacCompiler compiler, boolean b, Label l) {
        codeExp(2, compiler);
        compiler.addInstruction(new CMP(new ImmediateInteger(0), Register.getR(2)));
        if (b) compiler.addInstruction(new BNE(l));
        else compiler.addInstruction(new BEQ(l));
    }

    /*@Override
    public DVal dval(DecacCompiler compiler) {
        codeExp(1, compiler);
        if (!compiler.isNoCheck()) {
            compiler.addInstruction(new CMP(new NullOperand(), Register.getR(1)));
            compiler.addInstruction(new BEQ(new Label("dereferencement_null")));
        }
        return Register.getR(1);
    }*/
}

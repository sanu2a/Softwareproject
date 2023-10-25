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

public class Cast extends AbstractExpr {
    private AbstractIdentifier ident;
    private AbstractExpr exp;

    public Cast( AbstractIdentifier ident ,AbstractExpr exp ) {
        this.exp = exp;
        this.ident = ident;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        Type type = this.ident.verifyType(compiler);
        Type type2 = this.exp.verifyExpr(compiler,localEnv,currentClass);
        if (!type.isVoid()){
            if (compiler.environmentType.assignCompatible(type,type2) || compiler.environmentType.assignCompatible(type2,type)){
                    return type;
            }
            else{
                throw new ContextualError("The demanded Cast is illegal due to the given types. (3.39)",this.getLocation());
            }
        }
        else{
            throw new ContextualError("The given type for the Cast is void. (3.39)",this.getLocation());
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        ident.decompile(s);
        s.print(") (");
        exp.decompile(s);
        s.print(")");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        ident.prettyPrint(s,prefix,false);
        exp.prettyPrint(s,prefix,true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
    }

    @Override
    public boolean isCast(){
        return true;
    }

    private static int compteur = 0;

    @Override
    public void codeExp(int n, DecacCompiler compiler) {
        if (ident.getType().sameType(exp.getType()) && (ident.getType().isFloat() || ident.getType().isBoolean() || ident.getType().isInt())) {
            exp.codeExp(n, compiler);
        }
        else if (ident.getType().isFloat() && exp.getType().isInt()){
            exp.codeExp(n, compiler);
            compiler.addInstruction(new FLOAT(Register.getR(n), Register.getR(n)));
        }
        else if (ident.getType().isInt() && exp.getType().isFloat()){
            exp.codeExp(n, compiler);
            compiler.addInstruction(new SETROUND_TOWARDZERO());
            compiler.addInstruction(new INT(Register.getR(n), Register.getR(n)));
            compiler.addInstruction(new SETROUND_TONEAREST());
        }
        else if (ident.getType().isClass()){
            if (exp.dval(compiler) != null) {
                compiler.addInstruction(new LOAD(exp.dval(compiler), Register.getR(n)));
            }
            else {
                exp.codeExp(n, compiler);
            }
            compiler.addInstruction(new LEA(ident.getClassDefinition().getOperand(), Register.R0));
            compiler.addInstruction(new CMP(new NullOperand(), Register.getR(n)));
            compiler.addInstruction(new BNE(new Label("cast"+String.valueOf(compteur))));
            compiler.addInstruction(new LOAD(exp.dval(compiler), Register.getR(n)));
            compiler.addInstruction(new BRA(new Label("fincast"+String.valueOf(compteur))));
            //compiler.addInstruction(new LOAD(compiler.envExp.get(((Identifier)exp).getName()).getOperand(), Register.getR(n)));
            compiler.addLabel(new Label("cast"+String.valueOf(compteur)));
            compiler.addInstruction(new CMP(new NullOperand(), Register.getR(n)));
            compiler.addInstruction(new BEQ(new Label("castnull"+String.valueOf(compteur))));
            compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.getR(n)),Register.getR(n)));
            compiler.addInstruction(new CMP(Register.R0, Register.getR(n)));
            compiler.addInstruction(new BNE(new Label("cast"+String.valueOf(compteur))));
            compiler.addInstruction(new LOAD(exp.dval(compiler), Register.getR(n)));
            compiler.addInstruction(new BRA(new Label("fincast"+String.valueOf(compteur))));
            compiler.addLabel(new Label("castnull"+String.valueOf(compteur)));
            if (!compiler.isNoCheck()) compiler.addInstruction(new BRA(new Label("cast_impossible")));
            compiler.addLabel(new Label("fincast"+String.valueOf(compteur)));
            compteur++;
        }

    }

    @Override
    public DVal dval(DecacCompiler compiler) {
        codeExp(1, compiler);
        return Register.getR(1);
    }
}

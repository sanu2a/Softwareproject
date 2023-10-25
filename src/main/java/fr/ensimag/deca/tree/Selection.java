package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;

import java.io.PrintStream;

public class Selection extends AbstractLValue{

    AbstractExpr expr;
    AbstractIdentifier ident;

    public Selection(AbstractExpr expr, AbstractIdentifier ident) {
        this.expr = expr;
        this.ident = ident;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        Type type = this.expr.verifyExpr(compiler,localEnv,currentClass);
        this.setType(type);
        if (type.isClass()){
            EnvironmentExp environmentExp = ((ClassDefinition)compiler.environmentType.defOfType(type.getName())).getMembers();
            Type type2 = this.ident.verifyExpr(compiler,environmentExp,currentClass);
            ExpDefinition expDefinition = this.ident.getExpDefinition();
            FieldDefinition fieldDefinition = (FieldDefinition) expDefinition;
            if (fieldDefinition.getVisibility() == Visibility.PUBLIC){
                return type2;
            }
            else{
                ClassType classField = fieldDefinition.getContainingClass().getType();
                if (currentClass != null && compiler.environmentType.subType(type,currentClass.getType()) && compiler.environmentType.subType(currentClass.getType(),classField)){
                    return type2;
                }
                else{
                    throw new ContextualError("The specification about fields doesn't allow the access to the used field. (3.66)",this.getLocation());
                }
            }
        }
        else{
            throw new ContextualError("The given type isn't a class. (3.66)",this.getLocation() );
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        this.expr.decompile(s);
        s.print(".");
        this.ident.decompile(s);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expr.prettyPrint(s,prefix,false);
        ident.prettyPrint(s,prefix,true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {

    }

    protected void codeGenPrint(DecacCompiler compiler, boolean isHex) {
        if (expr.dval(compiler) != null) {
            compiler.addInstruction(new LOAD(expr.dval(compiler), Register.getR(1)));
        }
        else {
            expr.codeExp(1, compiler);
        }
        if (!compiler.isNoCheck()) {
            compiler.addInstruction(new CMP(new NullOperand(), Register.getR(1)));
            compiler.addInstruction(new BEQ(new Label("dereferencement_null")));
        }
        compiler.addInstruction(new LOAD(new RegisterOffset((ident).getFieldDefinition().getIndex(), Register.getR(1)), Register.getR(1)));
        if ((ident).getType().isFloat()) {
            if (isHex) compiler.addInstruction(new WFLOATX());
            else compiler.addInstruction(new WFLOAT());
        }
        else if ((ident).getType().isInt()) {
            compiler.addInstruction(new WINT());
        }
    }

    @Override
    public void codeExp(int n, DecacCompiler compiler){
        if (expr.dval(compiler) != null) {
            compiler.addInstruction(new LOAD(expr.dval(compiler), Register.getR(n)));
        }
        else {
            expr.codeExp(n, compiler);
        }
        if (!compiler.isNoCheck()) {
            compiler.addInstruction(new CMP(new NullOperand(), Register.getR(n)));
            compiler.addInstruction(new BEQ(new Label("dereferencement_null")));
        }
        compiler.setMaxRegistreUtilisee(n);
        compiler.addInstruction(new LOAD(new RegisterOffset((ident).getFieldDefinition().getIndex(), Register.getR(n)), Register.getR(n)));
    }

    @Override
    public DVal dval(DecacCompiler compiler) {
        if (expr.dval(compiler) != null) {
            compiler.addInstruction(new LOAD(expr.dval(compiler), Register.getR(0)));
        }
        else {
            expr.codeExp(0, compiler);
        }
        if (!compiler.isNoCheck()) {
            compiler.addInstruction(new CMP(new NullOperand(), Register.getR(0)));
            compiler.addInstruction(new BEQ(new Label("dereferencement_null")));
        }
        return new RegisterOffset((ident).getFieldDefinition().getIndex(), Register.getR(0));
    }

    @Override
    protected void codeBranch(DecacCompiler compiler, boolean b, Label l){
        codeExp(2, compiler);
        compiler.addInstruction(new CMP(new ImmediateInteger(0), Register.getR(2)));
        if (b) compiler.addInstruction(new BNE(l));
        else compiler.addInstruction(new BEQ(l));
    }
}

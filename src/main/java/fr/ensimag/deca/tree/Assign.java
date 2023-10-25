package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.*;

import fr.ensimag.ima.pseudocode.instructions.*;

/**
 * Assignment, i.e. lvalue = expr.
 *
 * @author gl35
 * @date 01/01/2023
 */
public class Assign extends AbstractBinaryExpr {

    @Override
    public AbstractLValue getLeftOperand() {
        // The cast succeeds by construction, as the leftOperand has been set
        // as an AbstractLValue by the constructor.
        return (AbstractLValue) super.getLeftOperand();
    }

    public Assign(AbstractLValue leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
                           ClassDefinition currentClass) throws ContextualError {
        if (true) {
            Type type = (this.getLeftOperand()).verifyExpr(compiler,localEnv,currentClass);
            AbstractExpr expr = this.getRightOperand().verifyRValue(compiler, localEnv, currentClass, type);
            if(expr.isConvFloat()){
                this.setRightOperand(expr);
                this.getRightOperand().setType(compiler.environmentType.FLOAT);
            }
            this.setType(type);
            return type;
        }
            else {
            throw new ContextualError("You cannot assign your variable with the given value. (3.32)", this.getLocation());
        }
    }


    @Override
    protected String getOperatorName() {
        return "=";
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        getRightOperand().codeExp(2, compiler);
        compiler.setMaxRegistreUtilisee(2);
        compiler.addInstruction(new STORE(Register.getR(2), (DAddr) getLeftOperand().dval(compiler)));
        /*if (getLeftOperand().dval(compiler) != null){
            compiler.addInstruction(new STORE(Register.getR(2), ((ExpDefinition) ((AbstractIdentifier) getLeftOperand()).getDefinition()).getOperand()));
        }
        else {
            compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.getR(0)));
            compiler.addInstruction(new STORE(Register.getR(2), new RegisterOffset(((AbstractIdentifier)getLeftOperand()).getFieldDefinition().getIndex(), Register.getR(0))));
        }*/
    }

    @Override
    public void codeExp(int n, DecacCompiler compiler) {
        getRightOperand().codeExp(n, compiler);
        compiler.setMaxRegistreUtilisee(n);
        compiler.addInstruction(new STORE(Register.getR(n), (DAddr) getLeftOperand().dval(compiler)));
        /*if (getLeftOperand().dval(compiler) != null){
            compiler.addInstruction(new STORE(Register.getR(n), ((ExpDefinition) ((AbstractIdentifier) getLeftOperand()).getDefinition()).getOperand()));
        }
        else {
            compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.getR(0)));
            compiler.addInstruction(new STORE(Register.getR(n), new RegisterOffset(((AbstractIdentifier)getLeftOperand()).getFieldDefinition().getIndex(), Register.getR(0))));
        }*/

    }

    @Override
    protected void codeBranch(DecacCompiler compiler, boolean b, Label l) {
        codeExp(2, compiler);
        compiler.addInstruction(new CMP(new ImmediateInteger(0), Register.getR(2)));
        if (b) compiler.addInstruction(new BNE(l));
        else compiler.addInstruction(new BEQ(l));
    }
    @Override
    public void decompile(IndentPrintStream s){
        this.getLeftOperand().decompile(s);
        s.print(" = ");
        this.getRightOperand().decompile(s);
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler, boolean isHex) {
        codeGenInst(compiler);
        compiler.addInstruction(new LOAD(Register.getR(2), Register.getR(1)));
        if (getType().isInt()) {
            compiler.addInstruction(new WINT());
        } else if (getType().isFloat()) {
            if (isHex) compiler.addInstruction(new WFLOATX());
            else compiler.addInstruction(new WFLOAT());
        } else if (getType().isBoolean()) {
            compiler.addInstruction(new WINT());
        }
    }

}

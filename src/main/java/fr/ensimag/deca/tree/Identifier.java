package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * Deca Identifier
 *
 * @author gl35
 * @date 01/01/2023
 */
public class Identifier extends AbstractIdentifier {

    @Override
    protected void checkDecoration() {
        if (getDefinition() == null) {
            throw new DecacInternalError("Identifier " + this.getName() + " has no attached Definition");
        }
    }

    @Override
    public Definition getDefinition() {
        return definition;
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * ClassDefinition.
     * <p>
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     *
     * @throws DecacInternalError if the definition is not a class definition.
     */
    @Override
    public ClassDefinition getClassDefinition() {
        try {
            return (ClassDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a class identifier, you can't call getClassDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * MethodDefinition.
     * <p>
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     *
     * @throws DecacInternalError if the definition is not a method definition.
     */
    @Override
    public MethodDefinition getMethodDefinition() {
        try {
            return (MethodDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a method identifier, you can't call getMethodDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * FieldDefinition.
     * <p>
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     *
     * @throws DecacInternalError if the definition is not a field definition.
     */
    @Override
    public FieldDefinition getFieldDefinition() {
        try {
            return (FieldDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a field identifier, you can't call getFieldDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * VariableDefinition.
     * <p>
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     *
     * @throws DecacInternalError if the definition is not a field definition.
     */
    @Override
    public VariableDefinition getVariableDefinition() {
        try {
            return (VariableDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a variable identifier, you can't call getVariableDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a ExpDefinition.
     * <p>
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     *
     * @throws DecacInternalError if the definition is not a field definition.
     */
    @Override
    public ExpDefinition getExpDefinition() {
        try {
            return (ExpDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a Exp identifier, you can't call getExpDefinition on it");
        }
    }


    public ParamDefinition getParamDefinition() {
        try {
            return (ParamDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a Exp identifier, you can't call getExpDefinition on it");
        }
    }

    @Override
    public void setDefinition(Definition definition) {
        this.definition = definition;
    }

    @Override
    public Symbol getName() {
        return name;
    }

    private Symbol name;

    public Identifier(Symbol name) {
        Validate.notNull(name);
        this.name = name;
    }
/*    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
                           ClassDefinition currentClass) throws ContextualError {
        ExpDefinition expDefinition = localEnv.get(((AbstractIdentifier) this).getName());
        if (expDefinition != null) {
            this.setType(expDefinition.getType());
            this.setDefinition(expDefinition);
            this.setType(expDefinition.getType());
            return expDefinition.getType();
        }
        else {
            throw new ContextualError("The given name does not correspond to any definition in EnvironementExp. (0.1)", this.getLocation());
        }
        //return this.verifyExpr(compiler,localEnv,null);
        //throw new UnsupportedOperationException("not yet implemented");
    }*/

    /**
     * Implements non-terminal "type" of [SyntaxeContextuelle] in the 3 passes
     *
     * @param compiler contains "env_types" attribute
     */
    @Override
    public Type verifyType(DecacCompiler compiler) throws ContextualError {
        TypeDefinition typeDefinition = compiler.environmentType.defOfType(this.getName());
        if (typeDefinition == null) {
            throw new ContextualError("Identifier of type is not defined in Environment Type. (0.2)", this.getLocation());
        } else {
            Type typeSynth = typeDefinition.getType();
            this.setDefinition(typeDefinition);
            this.setType(typeSynth);
            return typeSynth;
        }
    }


    private Definition definition;


    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(name.toString());

    }

    @Override
    String prettyPrintNode() {
        return "Identifier (" + getName() + ")";
    }

    @Override
    protected void prettyPrintType(PrintStream s, String prefix) {
        Definition d = getDefinition();
        if (d != null) {
            s.print(prefix);
            s.print("definition: ");
            s.print(d);
            s.println();
        }
    }

    @Override
    public void codeExp(int n, DecacCompiler compiler) {
        //7di rasek ya bnadem
        if (definition.isField()){
            compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.getR(n)));
            compiler.addInstruction(new LOAD(new RegisterOffset(getFieldDefinition().getIndex(), Register.getR(n)), Register.getR(n)));
        }
        else {
            compiler.addInstruction(new LOAD(compiler.envExp.get(name).getOperand(), Register.getR(n)));
        }
    }

    protected void codeGenPrint(DecacCompiler compiler, boolean isHex) {
        if (definition.isField()){
            compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.getR(1)));
            compiler.addInstruction(new LOAD(new RegisterOffset(getFieldDefinition().getIndex(), Register.getR(1)), Register.getR(1)));
        }
        else {
            compiler.addInstruction(new LOAD(compiler.envExp.get(name).getOperand(), Register.getR(1)));
        }
        if (definition.getType().isInt()) {
            compiler.addInstruction(new WINT());
        } else if (definition.getType().isFloat()) {
            if (isHex) compiler.addInstruction(new WFLOATX());
            else compiler.addInstruction(new WFLOAT());
        } else if (definition.getType().isBoolean()) {
            compiler.addInstruction(new WINT());
        }
    }

    /*@Override
    public void codeCast(int n, DecacCompiler compiler) {
        compiler.addInstruction(new LOAD(((ExpDefinition) definition).getOperand(), Register.getR(n)));
        if (getType().isFloat()) {
            compiler.addInstruction(new INT(Register.getR(n), Register.getR(n)));
        } else if (getType().isInt()) {
            compiler.addInstruction(new FLOAT(Register.getR(n), Register.getR(n)));

        }
    }*/

    @Override
    protected void codeBranch(DecacCompiler compiler, boolean b, Label l){
        if (definition.isField()){
            compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.getR(0)));
            compiler.addInstruction(new LOAD(new RegisterOffset(getFieldDefinition().getIndex(), Register.getR(0)), Register.getR(0)));
        }
        else {
            compiler.addInstruction(new LOAD(compiler.envExp.get(name).getOperand(), Register.getR(0)));
        }
        compiler.addInstruction(new CMP(new ImmediateInteger(0), Register.getR(0)));
        if (b) compiler.addInstruction(new BNE(l));
        else compiler.addInstruction(new BEQ(l));
    }

    @Override
    public DVal dval(DecacCompiler compiler){
        if (!definition.isField()){
            return compiler.envExp.get(name).getOperand();
        }
        else {
            compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.getR(0)));
            return new RegisterOffset(getFieldDefinition().getIndex(), Register.getR(0));
        }
    }
}

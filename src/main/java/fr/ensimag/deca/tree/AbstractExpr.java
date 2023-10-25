package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Label;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * Expression, i.e. anything that has a value.
 *
 * @author gl35
 * @date 01/01/2023
 */
public abstract class AbstractExpr extends AbstractInst {
    /**
     * @return true if the expression does not correspond to any concrete token
     * in the source code (and should be decompiled to the empty string).
     */
    boolean isImplicit() {
        return false;
    }

    /**
     * Get the type decoration associated to this expression (i.e. the type computed by contextual verification).
     */
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        Validate.notNull(type);
        this.type = type;
    }
    private Type type;

    @Override
    protected void checkDecoration() {
        if (getType() == null) {
            throw new DecacInternalError("Expression " + decompile() + " has no Type decoration");
        }
    }

    /**
     * Verify the expression for contextual error.
     * 
     * implements non-terminals "expr" and "lvalue" 
     *    of [SyntaxeContextuelle] in pass 3
     *
     * @param compiler  (contains the "env_types" attribute)
     * @param localEnv
     *            Environment in which the expression should be checked
     *            (corresponds to the "env_exp" attribute)
     * @param currentClass
     *            Definition of the class containing the expression
     *            (corresponds to the "class" attribute)
     *             is null in the main bloc.
     * @return the Type of the expression
     *            (corresponds to the "type" attribute)
     */
    public abstract Type verifyExpr(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError;

    /**
     * Verify the expression in right hand-side of (implicit) assignments 
     * 
     * implements non-terminal "rvalue" of [SyntaxeContextuelle] in pass 3
     *
     * @param compiler  contains the "env_types" attribute
     * @param localEnv corresponds to the "env_exp" attribute
     * @param currentClass corresponds to the "class" attribute
     * @param expectedType corresponds to the "type1" attribute            
     * @return this with an additional ConvFloat if needed...
     */
    public AbstractExpr verifyRValue(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass, 
            Type expectedType)
            throws ContextualError {
        Type typeSynth = this.verifyExpr(compiler,localEnv,currentClass);
        // Le set se fait aussi avant.
        this.setType(typeSynth);
        if (compiler.environmentType.assignCompatible(expectedType,typeSynth)){
            if (expectedType.isFloat() && typeSynth.isInt()){
                ConvFloat convFloat = new ConvFloat(this);
                convFloat.verifyExpr(compiler,localEnv,currentClass);
                return convFloat;
            }
            return this;
        } else if (this.isReturn()) {
            throw new ContextualError("The return types are incompatible. (3.24)",this.getLocation());
        }
        else
        {
            throw new ContextualError("The given types are incompatible. (3.28)",this.getLocation());
        }
    }
    
    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        if (true){
            Type type = this.verifyExpr(compiler,localEnv,currentClass);
            this.setType(type);
        }
        else{
            throw new ContextualError("Appel d'inst vers expr. (3.20)",this.getLocation());
        }
    }

    /**
     * Verify the expression as a condition, i.e. check that the type is
     * boolean.
     *
     * @param localEnv
     *            Environment in which the condition should be checked.
     * @param currentClass
     *            Definition of the class containing the expression, or null in
     *            the main program.
     */
    void verifyCondition(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type type = this.verifyExpr(compiler,localEnv,currentClass);
        if (!type.isBoolean()){
            throw new ContextualError("The type of the given expression is not boolean. (3.29)",this.getLocation());
        }
        //throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Generate code to print the expression
     *
     * @param compiler
     */
    protected void codeGenPrint(DecacCompiler compiler, boolean isHex) {
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        codeExp(2, compiler);
    }

    protected void codeBranch(DecacCompiler compiler, boolean b, Label l){
        throw new UnsupportedOperationException("not yet implemented");
    }

    public void codeExp(int n, DecacCompiler compiler){
        throw new UnsupportedOperationException("not yet implemented");
    }

    public DVal dval(DecacCompiler compiler){
        return null;
    }


    @Override
    protected void decompileInst(IndentPrintStream s) {
        this.decompile(s);
        s.print(";");
    }

    @Override
    public void decompile(IndentPrintStream s) {
    }

    @Override
    protected void prettyPrintType(PrintStream s, String prefix) {
        Type t = getType();
        if (t != null) {
            s.print(prefix);
            s.print("type: ");
            s.print(t);
            s.println();
        }
    }
    public boolean isConvFloat(){
        return false;
    }

}

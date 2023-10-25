package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import org.apache.commons.lang.Validate;

/**
 * Full if/else if/else statement.
 *
 * @author gl35
 * @date 01/01/2023
 */
public class IfThenElse extends AbstractInst {
    
    private final AbstractExpr condition; 
    private final ListInst thenBranch;
    private ListInst elseBranch;

    public IfThenElse(AbstractExpr condition, ListInst thenBranch, ListInst elseBranch) {
        Validate.notNull(condition);
        Validate.notNull(thenBranch);
        Validate.notNull(elseBranch);
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }
    
    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        this.condition.verifyCondition(compiler,localEnv,currentClass);
        this.thenBranch.verifyListInst(compiler,localEnv,currentClass,returnType);
        this.elseBranch.verifyListInst(compiler,localEnv,currentClass,returnType);
    }


    @Override
    public void decompile(IndentPrintStream s) {
        s.print("if(");
        condition.decompile(s);
        s.print(")");
        s.println("{");
        s.indent();
        thenBranch.decompile(s);
        s.unindent();
        s.print("}");
        s.println(" else {");
        s.indent();
        elseBranch.decompile(s);
        s.unindent();
        s.print("}");
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        condition.iter(f);
        thenBranch.iter(f);
        elseBranch.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        condition.prettyPrint(s, prefix, false);
        thenBranch.prettyPrint(s, prefix, false);
        elseBranch.prettyPrint(s, prefix, true);
    }

    private static int compteur = 0;
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        Label l = new Label("E_Fin"+String.valueOf(compteur));
        Label l1 = new Label("E_Sinon"+String.valueOf(compteur));
        compteur++;
        condition.codeBranch(compiler, false, l1);
        thenBranch.codeGenInst(compiler);
        compiler.addInstruction(new BRA(l));
        compiler.addLabel(l1);
        elseBranch.codeGenInst(compiler);
        compiler.addLabel(l);
    }
}

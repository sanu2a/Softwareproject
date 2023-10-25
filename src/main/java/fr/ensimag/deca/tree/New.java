package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.*;

import java.io.PrintStream;

public class New extends AbstractExpr {
    private AbstractIdentifier nomClasse;

    public New(AbstractIdentifier nomClasse) {
        this.nomClasse = nomClasse;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        Type type = this.nomClasse.verifyType(compiler);
        if (type.isClass()){
            this.setType(type);
            return type;
        }
        else{
            throw new ContextualError("The given type for Cast isn't compatible. (3.42)",this.getLocation());
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("new ");
        this.nomClasse.decompile(s);
        s.print("()");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        nomClasse.prettyPrint(s,prefix,true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {

    }

    public void codeExp(int n, DecacCompiler compiler){
        compiler.addInstruction(new NEW(nomClasse.getClassDefinition().getNumberOfFields()+1, Register.getR(n)));
        if (!compiler.isNoCheck()) compiler.addInstruction(new BOV(new Label("tasPlein")));
        compiler.addInstruction(new LEA(nomClasse.getClassDefinition().getOperand(), Register.getR(0)));
        compiler.addInstruction(new STORE(Register.getR(0), new RegisterOffset(0, Register.getR(n))));
        compiler.addInstruction(new PUSH(Register.getR(n)));
        compiler.addInstruction(new BSR(new Label("init."+String.valueOf(nomClasse.getName().getName().hashCode()).replace("-", "_"))));
        compiler.addInstruction(new POP(Register.getR(n)));
    }

    /*@Override
    public DVal dval(DecacCompiler compiler){
        codeExp(1, compiler);
        return Register.getR(1);
    }*/

}

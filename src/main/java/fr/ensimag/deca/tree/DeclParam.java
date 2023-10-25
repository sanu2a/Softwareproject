package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ParamDefinition;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;

import java.io.PrintStream;


public class DeclParam extends AbstractDeclParam {
    private final AbstractIdentifier type;
    private final AbstractIdentifier name;

    public AbstractIdentifier getName() {return name;}

    public DeclParam(AbstractIdentifier type, AbstractIdentifier name) {
        this.type = type;
        this.name = name;
    }
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s,prefix,false);
        name.prettyPrint(s,prefix,true);
    }

    public Type verifyDeclParam(DecacCompiler compiler) throws ContextualError{
        Type type1 = this.type.verifyType(compiler);
        ParamDefinition paramDefinition = new ParamDefinition(type1,this.getLocation());
        this.name.setDefinition(paramDefinition);
        this.name.setType(type1);
        if(type1.isVoid()){
            throw new ContextualError("The type of a parameter can not be Void. (2.9)",this.getLocation());
        }
        return type1;
    }
    @Override
    public void verifyParam(DecacCompiler compiler, EnvironmentExp environmentExp) throws ContextualError{
        Type type1 = this.type.verifyType(compiler);
        ParamDefinition paramDefinition = new ParamDefinition(type1,this.getLocation());
        try {
            environmentExp.declare(this.name.getName(), paramDefinition);
        }
        catch (EnvironmentExp.DoubleDefException e){
            throw new ContextualError("The given paramater exists already. (3.12)",this.getLocation());
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        s.print(" ");
        name.decompile(s);
    }
}

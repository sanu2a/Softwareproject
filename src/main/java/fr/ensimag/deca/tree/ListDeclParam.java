package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;


public class ListDeclParam extends TreeList<AbstractDeclParam>{


    public Signature verifyListDeclParam(DecacCompiler compiler) throws ContextualError {
        Signature signature = new Signature();
        for (AbstractDeclParam p : this.getList()) {
            Type type = p.verifyDeclParam(compiler);
            signature.add(type);
        }
        return signature;
    }

    public EnvironmentExp verifyListParam(DecacCompiler compiler,EnvironmentExp parentEnv) throws ContextualError{
        EnvironmentExp environmentExp = new EnvironmentExp(parentEnv);
        for(AbstractDeclParam p : this.getList()){
            p.verifyParam(compiler,environmentExp);
        }
        return environmentExp;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        int cmt = 0;
        for(AbstractDeclParam p: this.getList()){
            p.decompile(s);
            cmt ++;
            if (cmt != this.getList().size()) {
                s.print(",");
            }
        }
    }
}

package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.VoidType;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * @author gl35
 * @date 01/01/2023
 */
public class Main extends AbstractMain {
    private static final Logger LOG = Logger.getLogger(Main.class);
    
    private ListDeclVar declVariables;
    private ListInst insts;

    public Main(ListDeclVar declVariables,
            ListInst insts) {
        Validate.notNull(declVariables);
        Validate.notNull(insts);
        this.declVariables = declVariables;
        this.insts = insts;
    }

    @Override
    protected void verifyMain(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify Main: start");
        // A FAIRE: Appeler méthodes "verify*" de ListDeclVarSet et ListInst.
        this.declVariables.verifyListDeclVariable(compiler,compiler.envExp,null);
        // Current Class est nulle, vu qu'on implémente sans objet.
        this.insts.verifyListInst(compiler, compiler.envExp,null, new VoidType(compiler.createSymbol("void")));
        // Vous avez le droit de changer le profil fourni pour ces méthodes
        // (mais ce n'est à priori pas nécessaire).
        LOG.debug("verify Main: end");
        //throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    protected void codeGenMain(DecacCompiler compiler) {
        // A FAIRE: traiter les déclarations de variables.
        compiler.resetNbPushMax();
        compiler.addComment("Beginning of variables declaration:");
        DeclVar.initCompteur();
        declVariables.codeGenDeclVar(compiler);

        //envExp.wrini();
        compiler.addComment("Beginning of main instructions:");
        insts.codeGenListInst(compiler);

    }
    public int getNbVariablesGlobales() {return declVariables.getList().size();}
    @Override
    protected void codeGenMain1(DecacCompiler compiler) {
        compiler.addInstruction(new TSTO(new ImmediateInteger(declVariables.getList().size()+2)));
        Label l = new Label("pilePleine");
        compiler.addInstruction(new BOV(l));
        compiler.addInstruction(new ADDSP(new ImmediateInteger(declVariables.getList().size()+2)));
    }


    @Override
    protected void codeGenMain2(DecacCompiler compiler) {
        compiler.addFirst(new ADDSP(new ImmediateInteger(DeclVar.compteur  + compiler.getNbPushMax() + compiler.getNbParamMax())));
        Label l = new Label("pilePleine");
        if (!compiler.isNoCheck()) {
            compiler.addLabel(l);
            compiler.addInstruction(new WSTR("Erreur : Débordement de pile"));
            compiler.addInstruction(new WNL());
            compiler.addInstruction(new ERROR());
            l = new Label("tasPlein");
            compiler.addLabel(l);
            compiler.addInstruction(new WSTR("Erreur : Débordement de tas"));
            compiler.addInstruction(new WNL());
            compiler.addInstruction(new ERROR());
            l = new Label("readError");
            compiler.addLabel(l);
            compiler.addInstruction(new WSTR("Erreur : Entrée/Sortie"));
            compiler.addInstruction(new WNL());
            compiler.addInstruction(new ERROR());
            l = new Label("OV");
            compiler.addLabel(l);
            compiler.addInstruction(new WSTR("Erreur : Débordement opération arithmétique"));
            compiler.addInstruction(new WNL());
            compiler.addInstruction(new ERROR());
            l = new Label("dereferencement_null");
            compiler.addLabel(l);
            compiler.addInstruction(new WSTR("Erreur : dereferencement de null"));
            compiler.addInstruction(new WNL());
            compiler.addInstruction(new ERROR());
            l = new Label("cast_impossible");
            compiler.addLabel(l);
            compiler.addInstruction(new WSTR("Erreur : cast impossible"));
            compiler.addInstruction(new WNL());
            compiler.addInstruction(new ERROR());


            l = new Label("pilePleine");
            compiler.addFirst(new BOV(l));
            compiler.addFirst(new TSTO(DeclVar.compteur + compiler.getNbPushMax() + compiler.getNbParamMax()));// a modifier
        }
        l = new Label("code.object.equals");
        compiler.addLabel(l);
        compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R0));
        compiler.addInstruction(new LOAD(new RegisterOffset(-3, Register.LB), Register.R1));
        compiler.addInstruction(new CMP(Register.R0, Register.R1));
        compiler.addInstruction(new SEQ(Register.R0));
        compiler.addInstruction(new RTS());
        compiler.resetNbPushMax();
    }
    
    @Override
    public void decompile(IndentPrintStream s) {
        s.println("{");
        s.indent();
        declVariables.decompile(s);
        insts.decompile(s);
        s.unindent();
        s.println("}");

    }

    @Override
    protected void iterChildren(TreeFunction f) {
        declVariables.iter(f);
        insts.iter(f);
    }
 
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        declVariables.prettyPrint(s, prefix, false);
        insts.prettyPrint(s, prefix, true);
    }
}

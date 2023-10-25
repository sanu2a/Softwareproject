package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.*;

import java.io.PrintStream;

/**
 * Empty main Deca program
 *
 * @author gl35
 * @date 01/01/2023
 */
public class EmptyMain extends AbstractMain {
    @Override
    protected void verifyMain(DecacCompiler compiler) throws ContextualError {
        assert true;
        //throw new ContextualError("EmptyMain (3.3)",this.getLocation());
        //throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    protected void codeGenMain(DecacCompiler compiler) {
        compiler.resetNbPushMax();

    }
    @Override
    protected void codeGenMain1(DecacCompiler compiler) {
    }


    @Override
    protected void codeGenMain2(DecacCompiler compiler) {
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
            compiler.addFirst(new TSTO( DeclClass.getCompteur() + compiler.getNbPushMax()));// a modifier
        }
        l = new Label("code.object.equals");
        compiler.addLabel(l);
        compiler.addInstruction(new WSTR("Erreur : mazal magadinaha"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());
        compiler.addFirst(new ADDSP(new ImmediateInteger(  DeclClass.getCompteur() + compiler.getNbPushMax())));
        compiler.resetNbPushMax();
    }

    /**
     * Contains no real information => nothing to check.
     */
    @Override
    protected void checkLocation() {
        // nothing
    }
    
    @Override
    public void decompile(IndentPrintStream s) {
        // no main program => nothing
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

    public int getNbVariablesGlobales() {return 0;}
}

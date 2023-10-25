package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.*;

import java.io.PrintStream;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.commons.lang.Validate;
import fr.ensimag.ima.pseudocode.GPRegister;

/**
 * @author gl35
 * @date 01/01/2023
 */
public class DeclVar extends AbstractDeclVar {

    
    final private AbstractIdentifier type;
    final public AbstractIdentifier varName;
    final private AbstractInitialization initialization;

    public DeclVar(AbstractIdentifier type, AbstractIdentifier varName, AbstractInitialization initialization) {
        Validate.notNull(type);
        Validate.notNull(varName);
        Validate.notNull(initialization);
        this.type = type;
        this.varName = varName;
        this.initialization = initialization;
    }

    @Override
    protected void verifyDeclVar(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        if (!localEnv.getMap().containsKey(varName.getName())) {
            Type typeSynth = type.verifyType(compiler);
            this.varName.setType(typeSynth);
            // Faire attention avec le get dans le cas ou ça sera une liste de Classes.
            if (typeSynth.isVoid()) {
                throw new ContextualError("The given type is Void. (3.17)", this.getLocation());
            }
            VariableDefinition variableDefinition = new VariableDefinition(typeSynth, this.getLocation());
            this.varName.setDefinition(variableDefinition);
            // Faire attention à l'empilement des environnements dans le cas avec Objet
            initialization.verifyInitialization(compiler, typeSynth, localEnv, currentClass);
            try {
                localEnv.declare(varName.getName(), variableDefinition);
            } catch (EnvironmentExp.DoubleDefException e) {
                throw new ContextualError("Double definition of your symbol. (3.17)", this.getLocation());

            }
        }
        else {
            throw new ContextualError("The variable name is already used for another variable. (3.17)",this.getLocation());
        }
    }

    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        s.print(" ");
        varName.decompile(s);
        initialization.decompile(s);
        s.print(";");
        s.println();
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        type.iter(f);
        varName.iter(f);
        initialization.iter(f);
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        varName.prettyPrint(s, prefix, false);
        initialization.prettyPrint(s, prefix, true);
    }
    public static int compteur;
    public void codeGenDeclVar(DecacCompiler compiler){
        ExpDefinition e = compiler.envExp.get(varName.getName());
        compiler.envExp.get(varName.getName()).setOperand(new RegisterOffset(compteur, Register.GB));
        compteur++;
        initialization.codeGenDeclVar(compiler.envExp.get(varName.getName()), compiler);
    }
    public void codeGenDeclVar2(DecacCompiler compiler){
        //System.out.println("hahayo");
        //varName.getExpDefinition().setOperand(new RegisterOffset(compteur, Register.LB));
        compiler.envExp.get(varName.getName()).setOperand(new RegisterOffset(compteur, Register.LB));
        compteur++;
        initialization.codeGenDeclVar(compiler.envExp.get(varName.getName()), compiler);
    }

    public static void initCompteur() {compteur = DeclClass.getCompteur();}

    public static void initCompteur2() {compteur = 1;}
}

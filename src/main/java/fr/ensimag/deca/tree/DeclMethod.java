package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;

import java.io.PrintStream;


public class DeclMethod extends AbstractDeclMethod{
    final private AbstractIdentifier type;
    final private AbstractIdentifier name;
    final private ListDeclParam params;
    final private AbstractMethodBody body;



    private EnvironmentExp envExpParam;
    public EnvironmentExp getEnvExp(){
        return this.envExpParam;
    }
    public void setEnvExp(EnvironmentExp envExpParam){
        this.envExpParam = envExpParam;
    }
    public DeclMethod(AbstractIdentifier type, AbstractIdentifier name, ListDeclParam params,AbstractMethodBody body ) {
        this.type = type;
        this.name = name;
        this.params = params;
        this.body = body;
        this.envExpParam = null;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.println();
        type.decompile(s);
        s.print(" ");
        name.decompile(s);
        s.print("(");
        params.decompile(s);
        s.print(")");
        this.body.decompile(s);
    }

    public void verifyDeclMethod(DecacCompiler compiler, ClassDefinition superClass, ClassDefinition currentClass) throws ContextualError {
        Type type1 = this.type.verifyType(compiler);
        this.type.setType(type1);
        Signature signature = this.params.verifyListDeclParam(compiler);
        int indexDecl = currentClass.getNumberOfMethods() + 1;
        boolean constante = false;
        // verifyMethodBody
        if (compiler.environmentType.defOfType(superClass.getType().getName()) != null && superClass.getMembers().get(this.name.getName()) != null) {
            ExpDefinition methodDefinition = superClass.getMembers().get(this.name.getName());
            if (methodDefinition.isMethod()) {
                if (compiler.environmentType.subType(type1, methodDefinition.getType())) {
                    Signature signature2 = ((MethodDefinition) methodDefinition).getSignature();
                    if (!signature.equals(signature2)) {
                        throw new ContextualError("The signature of the new method isn't the same as the past signature. (2.7)", this.getLocation());
                    }
                    indexDecl = ((MethodDefinition) methodDefinition).getIndex();
                    constante = true;
                }
                else{
                    throw new ContextualError("The return type of the new method isn't a subtype of the one already defined. (2.7)",this.getLocation());
                }
            }
            else {
                throw new ContextualError("The given name of the method is already used for a field. (2.7)",this.getLocation());
            }
        }

            try {
                MethodDefinition definitionMethod = new MethodDefinition(type1, this.getLocation(), signature, indexDecl);
                String stringLabel = "code." + String.valueOf(currentClass.getType().getName().getName().hashCode()).replace("-", "_") + "." + String.valueOf(name.getName().getName().hashCode()).replace("-", "_");
                definitionMethod.setLabel(new Label(stringLabel));
                this.name.setType(type1);
                this.name.setDefinition(definitionMethod);
                currentClass.getMembers().declare(this.name.getName(), definitionMethod);
                if (!constante){
                    currentClass.incNumberOfMethods();
                }
            } catch (EnvironmentExp.DoubleDefException e) {
                throw new ContextualError("The given method name is already used. (2.6)", this.getLocation());
            }
    }

    public void verifyMethodBody(DecacCompiler compiler, ClassDefinition currentClass) throws ContextualError{
        this.envExpParam= this.params.verifyListParam(compiler,currentClass.getMembers());
        this.type.getType().setMethodName(name);
        body.verifyBody(compiler,this.envExpParam,currentClass,this.type.getType());
    }

    @Override
    public void codeGenMethod(DecacCompiler compiler) {
        EnvironmentExp old = compiler.envExp;
        compiler.envExp = envExpParam;
        compiler.resetMaxRegistreUtilisee();
        compiler.resetNbPushMax();
        compiler.resetNbParamMax();
        int j = -3;
        for (AbstractDeclParam p: params.getList()){
            /*((DeclParam)p).getName().getExpDefinition().setOperand(new RegisterOffset(j, Register.SP));
            System.out.println(((DeclParam)p).getName().getName().getName());
            ((DeclParam)p).getName().getVariableDefinition().setOperand(new RegisterOffset(j, Register.SP));
            System.out.println(env.get(((DeclParam)p).getName().getName()));*/
            //System.out.println(envExpParam.get(((DeclParam)p).getName().getName()));
            //System.out.println(((DeclParam)p).getName().getName());
            envExpParam.get(((DeclParam)p).getName().getName()).setOperand(new RegisterOffset(j, Register.LB));
            j--;
        }
        /*for (SymbolTable.Symbol s: envExpParam.keys()){
            ExpDefinition def = envExpParam.get(s);
            if (def.isField()) {
                try {
                    envExpParam.get(s).setOperand(new RegisterOffset(def.asFieldDefinition(null, null).getIndex()+1, new RegisterOffset(-1, Register.SP)));
                }
                catch (ContextualError e){
                    System.out.println("erreur label");
                    System.exit(1);
                }
            }
        }*/
        //System.out.println(name.getMethodDefinition().getLabel().toString());
        if (body instanceof MethodBody) {
            IMAProgram p = new IMAProgram();
            p.addInstruction(new ADDSP(body.getListVar().size() + compiler.getNbParamMax()));
            IMAProgram oldProgram = compiler.getIMAProgram();
            compiler.setProgram(p);
            DeclVar.initCompteur2();
            for (AbstractDeclVar v : body.getListVar().getList()) {
                ((DeclVar) v).codeGenDeclVar2(compiler);
            }
            body.getListinst().codeGenInst(compiler);
            //eRREUR NON VOIDname.getClassDefinition().getMembers()
            if (!type.getType().isVoid() && !compiler.isNoCheck()) {
                compiler.addInstruction(new WSTR("Erreur: La méthode ne retourne rien"));
                compiler.addInstruction(new ERROR());
            }
            try {
                /*a revoir*/
                compiler.addLabel(new Label("fin." + envExpParam.get(name.getName()).asMethodDefinition(null, null).getLabel().toString()));
            } catch (ContextualError e) {
                System.out.println("erreur label");
                System.exit(1);
            }
            int i = compiler.getMaxRegistreUtilisee();
            while (i >= 2) {
                p.addFirst(new PUSH(Register.getR(i)));
                p.addInstruction(new POP(Register.getR(i)));
                i--;
            }
            compiler.addInstruction(new RTS());
            if (!compiler.isNoCheck()) {
                p.addFirst(new BOV(new Label("pilePleine")));
                p.addFirst(new TSTO(((compiler.getMaxRegistreUtilisee() >= 2) ? (compiler.getMaxRegistreUtilisee() - 1) : (0)) + compiler.getNbPushMax() + body.getListVar().size() + compiler.getNbParamMax()));
            }
            try {
                /*A revoir*/
                p.addFirst(new Line(envExpParam.get(name.getName()).asMethodDefinition(null, null).getLabel(), null, null));
            } catch (ContextualError e) {
                System.out.println("erreur label");
                System.exit(1);
            }
            oldProgram.append(p);
            compiler.setProgram(oldProgram);
            compiler.envExp = old;
        }
        else {
            try {
                /*A revoir*/
                compiler.add(new Line(envExpParam.get(name.getName()).asMethodDefinition(null, null).getLabel(), null, null));
            } catch (ContextualError e) {
                System.out.println("erreur label");
                System.exit(1);
            }
            compiler.getIMAProgram().add(new Line(((MethodAsmBody)body).getCode()));
        }
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s,prefix,false);
        name.prettyPrint(s,prefix,false);
        params.prettyPrint(s,prefix,false);
        body.prettyPrint(s,prefix,true);


    }

    @Override
    protected void iterChildren(TreeFunction f) {

    }
}

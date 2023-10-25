package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;

import java.io.PrintStream;
import java.util.HashMap;

/**
 * Declaration of a class (<code>class name extends superClass {members}<code>).
 * 
 * @author gl35
 * @date 01/01/2023
 */
public class DeclClass extends AbstractDeclClass {
    private final AbstractIdentifier name;
    private AbstractIdentifier superclass;
    private final ListDeclField fields;
    private final ListDeclMethod methods;


    public DeclClass(AbstractIdentifier name, AbstractIdentifier superclass, ListDeclField fields, ListDeclMethod methods) {
        this.name = name;
        this.superclass = superclass ;
        this.fields = fields;
        this.methods = methods;
    }


    @Override
    public void decompile(IndentPrintStream s) {
       s.print("class ");
       this.name.decompile(s);
       s.print(" extends ");
       this.superclass.decompile(s);
       s.print(" {");
       s.indent();
       fields.decompile(s);
       methods.decompile(s);
       s.unindent();
       s.println();
       s.print("}");
    }

    @Override
    public void verifyClass(DecacCompiler compiler) throws ContextualError {

        if (superclass.getName().getName().equals("Object")){
            this.superclass.setDefinition(compiler.getObject());
            this.superclass.setLocation(Location.BUILTIN);
        }
        else{
            TypeDefinition typeDefinition = compiler.environmentType.defOfType( this.superclass.getName());
            ClassType classTypeSuper = new ClassName(this.superclass.getName());
            this.superclass.setLocation(this.getLocation());
            this.superclass.setType(classTypeSuper);
            this.superclass.setDefinition(typeDefinition);
        }
        TypeDefinition definition = compiler.environmentType.defOfType(this.superclass.getName());
        if (definition!= null && definition.isClass()){
            if (compiler.environmentType.defOfType(this.name.getName()) == null){
                ClassDefinition classDefinition = new ClassDefinition(new ClassName(this.name.getName()),this.getLocation(),this.superclass.getClassDefinition());
                this.name.setDefinition(classDefinition);
                this.name.setType(new ClassName(this.name.getName()));
                compiler.environmentType.addClass(this.name.getName(),classDefinition);
            }
            else {
                throw new ContextualError("The class already exists. (1.3)", this.getLocation());
            }
    }
        else {
            throw new ContextualError("The given superclass does not exist, or its definition doesn't match Class type (1.3)",this.getLocation());
        }
    }

    @Override
    public void verifyClassMembers(DecacCompiler compiler)
            throws ContextualError {
        fields.verifyListDeclField(compiler,this.superclass.getClassDefinition(), this.name.getClassDefinition());
        methods.verifyListDeclMethod(compiler,this.superclass.getClassDefinition(), this.name.getClassDefinition());
    }
    
    @Override
    public void verifyClassBody(DecacCompiler compiler) throws ContextualError {
        /*if (compiler.environmentType.defOfType(this.name.getName()) == null){
            throw new ContextualError("The given class name isn't already defined. (3.5)",this.getLocation());
        }*/
        //else{
            fields.verifyListFieldInit(compiler,this.name.getClassDefinition());
            methods.verifyListMethodBody(compiler,this.name.getClassDefinition());
        //}
    }


    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        name.prettyPrint(s,prefix,false);
        if (superclass != null){superclass.prettyPrint(s,prefix,false);}
        fields.prettyPrint(s,prefix,false);
        methods.prettyPrint(s,prefix,true);

    }

    @Override
    protected void iterChildren(TreeFunction f) {
        //throw new UnsupportedOperationException("Not yet supported");
    }

    public static int compteur = 3;

    @Override
    public void codeGenTablemethod(DecacCompiler compiler){
        HashMap<SymbolTable.Symbol, ExpDefinition> table = new HashMap<SymbolTable.Symbol, ExpDefinition>();
        EnvironmentExp exp = name.getClassDefinition().getMembers();
        while (exp != null){
            for (SymbolTable.Symbol s: exp.getMap().keySet()){
                if (!table.containsKey(s) && exp.getMap().get(s).isMethod()) {
                    table.put(s, exp.get(s));
                }
            }
            exp = exp.parentEnvironment;
        }
        ClassDefinition defini = this.name.getClassDefinition();
        defini.setOperand(new RegisterOffset(compteur, Register.GB));
        DAddr addrSuper = this.superclass.getClassDefinition().getOperand();
        compiler.addInstruction(new LEA(addrSuper, Register.getR(0)));
        compiler.addInstruction(new STORE(Register.getR(0), defini.getOperand()));
        for (SymbolTable.Symbol d: table.keySet()){
            try {
                compiler.addInstruction(new LOAD(new LabelOperand(name.getClassDefinition().getMembers().get(d).asMethodDefinition(null, null).getLabel()), GPRegister.getR(0)));
                compiler.addInstruction(new STORE(Register.getR(0), new RegisterOffset(name.getClassDefinition().getMembers().get(d).asMethodDefinition(null, null).getIndex()+compteur, Register.GB)));
            }
            catch (ContextualError e) {
                System.out.println("erreur label");
                System.exit(1);
            }
        }
        compteur = compteur + 1 + table.size();
    }

    public static int getCompteur() {return compteur;}


    public void codeGenMethod(DecacCompiler compiler){
        IMAProgram p = new IMAProgram();

        //System.out.println(superclass.getName().getName());
        //sauvegarde registre
        compiler.resetMaxRegistreUtilisee();
        compiler.resetNbPushMax();
        p.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R1));
        for (AbstractDeclField f: fields.getList()){
            if (((DeclField) f).getType().getType().isFloat()) p.addInstruction(new LOAD(new ImmediateFloat(0), Register.R0));
            else if (((DeclField) f).getType().getType().isClass()) p.addInstruction(new LOAD(new NullOperand(), Register.R0));
            else {p.addInstruction(new LOAD(new ImmediateInteger(0), Register.R0));}
            p.addInstruction(new STORE(Register.R0, new RegisterOffset(((DeclField) f).getName().getFieldDefinition().getIndex(), Register.R1)));
            //((DeclField)f).getName().getExpDefinition().setOperand(new RegisterOffset(((DeclField) f).getName().getFieldDefinition().getIndex(), Register.R1));
        }
        int n = 0;
        if (!superclass.getName().getName().equals("Object")) {
            p.addInstruction(new PUSH(Register.getR(1)));
            p.addInstruction(new BSR(new Label("init."+String.valueOf(superclass.getName().getName().hashCode()).replace("-", "_"))));
            p.addInstruction(new SUBSP(1));
            n++;
        }

        IMAProgram oldProgram = compiler.getIMAProgram();
        compiler.setProgram(p);
        for (AbstractDeclField f: fields.getList()){
            AbstractInitialization i =  ((DeclField)f).getInitialization();
            if (i.getExpression() != null) {
                i.getExpression().codeExp(2, compiler);
                compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R1));
                compiler.setMaxRegistreUtilisee(2);
                compiler.addInstruction(new STORE(Register.getR(2), new RegisterOffset(((DeclField) f).getName().getFieldDefinition().getIndex(), Register.R1)));
            }

        }
        p = compiler.getIMAProgram();
        int i = compiler.getMaxRegistreUtilisee();
        while (i >= 2) {
            p.addFirst(new PUSH(Register.getR(i)));
            p.addInstruction(new POP(Register.getR(i)));
            i--;
        }
        if (!compiler.isNoCheck()) {
            p.addFirst(new BOV(new Label("pilePleine")));
            p.addFirst(new TSTO(compiler.getMaxRegistreUtilisee()+compiler.getNbPushMax()+n));
        }
        p.addFirst(new Line(new Label("init."+String.valueOf(name.getName().getName().hashCode()).replace("-", "_")), null, null));
        oldProgram.append(p);
        compiler.setProgram(oldProgram);
        compiler.addInstruction(new RTS());
        //System.out.println(methods.getList().get(0).);
        methods.codeGenMethod(compiler);

    }



}

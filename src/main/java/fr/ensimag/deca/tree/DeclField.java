package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;

public class DeclField extends AbstractDeclField{

    final private Visibility visibility;
    final private AbstractIdentifier type;
    final private AbstractIdentifier name;
    final private AbstractInitialization initialization;

    public DeclField(Visibility visibility, AbstractIdentifier type, AbstractIdentifier name, AbstractInitialization initialization) {
        Validate.notNull(visibility);
        Validate.notNull(type);
        Validate.notNull(name);
        //Validate.notNull(initialization);
        this.visibility = visibility;
        this.type = type;
        this.name = name;
        this.initialization = initialization;
    }

    public void verifyDeclField(DecacCompiler compiler, ClassDefinition superClass, ClassDefinition currentClass) throws ContextualError {
        Type type = this.type.verifyType(compiler);
        if (!type.isVoid()){
            FieldDefinition fieldDefinition = new FieldDefinition(type, this.getLocation(), visibility, currentClass, currentClass.getNumberOfFields() + 1);
            currentClass.incNumberOfFields();
                if (compiler.environmentType.defOfType(superClass.getType().getName()) != null &&
                superClass.getMembers().get(this.name.getName()) != null) {
                    if (!superClass.getMembers().get(this.name.getName()).isField()){
                        throw new ContextualError("The given attribute has already been declared and with a different type than Field. (2.5)",this.getLocation());
                    }
                }
            try {
                this.name.setType(type);
                this.name.setDefinition(fieldDefinition);
                currentClass.getMembers().declare(this.name.getName(), fieldDefinition);
            } catch (EnvironmentExp.DoubleDefException e) {
                throw new ContextualError("The given attribute has already been declared. (2.4)", this.getLocation());
            }
        }
        else{
            throw new ContextualError("The field type can't be void. (2.5)",this.getLocation());
        }
    }

    public void verifyFieldInit(DecacCompiler compiler,ClassDefinition currentClass) throws ContextualError{
        Type type = this.type.verifyType(compiler);
        this.initialization.verifyInitialization(compiler,type,currentClass.getMembers(),currentClass);
    }
    @Override
    public void decompile(IndentPrintStream s) {
            s.println();
            s.print(this.visibility.name().toLowerCase() + " ");
            this.type.decompile(s);
            s.print(" ");
            this.name.decompile(s);
            this.initialization.decompile(s);
            s.print(";");
    }
    @Override
    String prettyPrintNode(){
        return "[Visibility=" + visibility.name() + "] DeclField" ;
    }
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s,prefix,false);
        name.prettyPrint(s,prefix,false);
        initialization.prettyPrint(s,prefix,true);

    }

    @Override
    protected void iterChildren(TreeFunction f) {

    }

    public AbstractIdentifier getType() {return type;}

    public AbstractIdentifier getName() {return name;}

    public AbstractInitialization getInitialization() {return initialization;}
}

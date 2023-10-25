package fr.ensimag.deca.context;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class TestVerify {
    final ClassType CLASSTYPE = new ClassType(null);
    @Mock
    AbstractIdentifier name;
    @Mock
    AbstractIdentifier superclass;
    @Mock
    ListDeclField fields;
    @Mock
    ListDeclMethod methods;
    DecacCompiler compiler;
    EnvironmentExp environmentExp;
    ClassDefinition classDefinition;

    @BeforeEach
    public void setup() throws ContextualError {
        MockitoAnnotations.initMocks(this);
        compiler = new DecacCompiler(null, null);
        environmentExp = new EnvironmentExp(null);
        classDefinition = new ClassDefinition(CLASSTYPE,null,null);
        name = new Identifier(compiler.createSymbol("A"));
        fields = new ListDeclField();
        methods = new ListDeclMethod();
    }

    @Test
    public void testDeclClass()  throws ContextualError{
        superclass = new Identifier(compiler.createSymbol("Object"));
        DeclClass declClass = new DeclClass(name,superclass,fields,methods);
        declClass.verifyClass(compiler);
        if (superclass.getName().getName().equals("Object")){
            assertSame(superclass.getDefinition(),compiler.getObject());
            assertSame(superclass.getLocation(), Location.BUILTIN);
        }
        else{
            assertNotNull(superclass.getLocation());
            assertNotNull(superclass.getType());
            assertNotNull(superclass.getDefinition());
        }
        assertNotNull(name.getType());
        assertNotNull(name.getClassDefinition());
        assertTrue(compiler.environmentType.defOfType(compiler.createSymbol("A")).isClass());
    }

    @Test
    public void testDeclFieldWithoutInit() throws ContextualError{
        AbstractIdentifier type = new Identifier(compiler.createSymbol("int"));
        AbstractIdentifier name = new Identifier(compiler.createSymbol("Aa"));
        type.setType(compiler.environmentType.INT);
        DeclField declField = new DeclField(Visibility.PUBLIC,type,name,null);
        int memo = classDefinition.getNumberOfFields();
        declField.verifyDeclField(compiler,compiler.environmentType.OBJECT.getDefinition(),classDefinition);
        assertNotEquals(memo,classDefinition.getNumberOfFields());
        assertNotNull(name.getType());
        assertNotNull(name.getDefinition());
        assertTrue(name.getDefinition().isField());
        assertNotNull(classDefinition.getMembers().get(name.getName()));
    }

    @Test
    public void testDeclParam() throws ContextualError{
        AbstractIdentifier type = new Identifier(compiler.createSymbol("boolean"));
        AbstractIdentifier name = new Identifier(compiler.createSymbol("a"));

    }
    @Test
    public void testDeclMethod() throws ContextualError{
        AbstractIdentifier type = new Identifier(compiler.createSymbol("float"));
        AbstractIdentifier name = new Identifier(compiler.createSymbol("getX"));
        ListDeclParam listDeclParam = new ListDeclParam();
    }


}

package fr.ensimag.deca.context;

import fr.ensimag.deca.tools.SymbolTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnvironmentExpTest {

    @Test
    void getMap() {
        // Test of getMap that returns envExp
        EnvironmentExp environmentExp1 = new EnvironmentExp(null);
        EnvironmentExp environmentExp2 = new EnvironmentExp(null);
        // envExp associé a notre environmentExp est vide au debut.
        assertTrue(environmentExp2.getMap().isEmpty());
        // Assurer que son parent est null
        assertNull(environmentExp2.parentEnvironment);
        environmentExp2.parentEnvironment = environmentExp1;
        // Assurer que c'est plus null
        assertNotNull(environmentExp2.parentEnvironment);
        assertTrue(environmentExp1.getMap().isEmpty());
        assertNotSame(environmentExp1.getMap(),environmentExp2.getMap());
        assertSame(environmentExp2.parentEnvironment.getMap(),environmentExp1.getMap());
    }
    @Test
    void declare() throws EnvironmentExp.DoubleDefException {
        SymbolTable t = new SymbolTable();
        SymbolTable.Symbol var1 = t.create("x");
        SymbolTable.Symbol var2 = t.create("y");
        SymbolTable.Symbol methode = t.create("equals");
        // Test le declare dans environmentExp
        EnvironmentExp environmentExpParent = new EnvironmentExp(null);
        EnvironmentExp environmentExp1 = new EnvironmentExp(environmentExpParent);
        // Declaration de deux variables sans definition dans l'envExp
        environmentExp1.declare(var1,null);
        environmentExp1.declare(var2,null);
        // Assurer que les deux variables on bien ete declares
        assertTrue(environmentExp1.getMap().containsKey(var1));
        assertTrue(environmentExp1.getMap().containsKey(var2));
        // Assurer que l'env parent est encore vide
        assertTrue(environmentExp1.parentEnvironment.getMap().isEmpty());
        // Declarer une methode dans l'env parent
        environmentExpParent.declare(methode,null);
        assertTrue(environmentExp1.parentEnvironment.getMap().containsKey(methode));
        // Assurer que la methode n'est pas dans le current Dictionnary
        assertFalse(environmentExp1.getMap().containsKey(methode));
    }
    @Test
    void get() throws EnvironmentExp.DoubleDefException {
        SymbolTable t = new SymbolTable();
        SymbolTable.Symbol methode = t.create("equals");
        EnvironmentExp environmentExpParent = new EnvironmentExp(null);
        EnvironmentExp environmentExpParent1 = new EnvironmentExp(environmentExpParent);
        EnvironmentExp environmentExp1 = new EnvironmentExp(environmentExpParent1);
        // Declarer une methode dans l'env parent
        environmentExpParent.getMap().put(methode,null);
        assertTrue(environmentExp1.parentEnvironment.parentEnvironment.getMap().containsKey(methode));
        // Assurer que la methode n'est pas dans le current Dictionnary ni son parent
        assertFalse(environmentExp1.getMap().containsKey(methode));
        assertFalse(environmentExpParent1.getMap().containsKey(methode));
        // Assurer que la recherche s'effectue dans toute la hierarchie
        assertSame(environmentExpParent.get(methode),environmentExp1.get(methode));
        // Test of hidden definitions when defined at parent and current dictionary
        MethodDefinition methodDefinition = new MethodDefinition(null,null,null,0);
        environmentExp1.declare(methode,methodDefinition);
        assertNotSame(environmentExp1.get(methode),environmentExpParent.get(methode));
        // Assurer la bonne recherche (La definition dans le parent est bien cachée)
        assertSame(environmentExp1.get(methode),methodDefinition);
    }
}
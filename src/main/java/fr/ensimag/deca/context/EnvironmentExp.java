package fr.ensimag.deca.context;

import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import fr.ensimag.deca.tools.SymbolTable.Symbol;

/**
 * Dictionary associating identifier's ExpDefinition to their names.
 * 
 * This is actually a linked list of dictionaries: each EnvironmentExp has a
 * pointer to a parentEnvironment, corresponding to superblock (eg superclass).
 * 
 * The dictionary at the head of this list thus corresponds to the "current" 
 * block (eg class).
 * 
 * Searching a definition (through method get) is done in the "current" 
 * dictionary and in the parentEnvironment if it fails. 
 * 
 * Insertion (through method declare) is always done in the "current" dictionary.
 * 
 * @author gl35
 * @date 01/01/2023
 */
public class EnvironmentExp {
    // A FAIRE : implémenter la structure de donnée représentant un
    // environnement (association nom -> définition, avec possibilité
    // d'empilement).

    public EnvironmentExp parentEnvironment;
    
    public EnvironmentExp(EnvironmentExp parentEnvironment) {
        this.parentEnvironment = parentEnvironment;
        envExp = new HashMap<Symbol, ExpDefinition>();
    }
    private final Map<Symbol, ExpDefinition> envExp;

    public Map<Symbol, ExpDefinition> getMap(){
        return envExp;
    }


    public static class DoubleDefException extends Exception {
        private static final long serialVersionUID = -2733379901827316441L;
    }

    /**
     * Return the definition of the symbol in the environment, or null if the
     * symbol is undefined.
     */
    // A modifier, car il faut rechercher avec une boucle while dans l'empilement des envs.
    public ExpDefinition get(Symbol key) {
        EnvironmentExp environmentExp = this;
        while(environmentExp!= null){
            if (environmentExp.envExp.get(key)!= null){
                return environmentExp.envExp.get(key);
            }
            environmentExp = environmentExp.parentEnvironment;
        }
        return null;
    }
    /**
     * Add the definition def associated to the symbol name in the environment.
     *
     * Adding a symbol which is already defined in the environment,
     * - throws DoubleDefException if the symbol is in the "current" dictionary
     * - or, hides the previous declaration otherwise.
     *
     * @param name
     *            Name of the symbol to define
     * @param def
     *            Definition of the symbol
     * @throws DoubleDefException
     *             if the symbol is already defined at the "current" dictionary
     *
     */
    public void declare(Symbol name, ExpDefinition def) throws DoubleDefException {
        if(!envExp.containsKey(name))
        {
            envExp.put(name, def);
        }
        else{
                throw new DoubleDefException();
            }
    }

    /*public Set<Symbol> keys(){
        Set<Symbol> res = new HashSet<>();
        res.addAll(envExp.keySet());
        EnvironmentExp p =parentEnvironment;
        while(p != null){
            res.addAll(p.envExp.keySet());
            p = p.parentEnvironment;
        }
        return res;
    }*/
}

package fr.ensimag.deca.context;

import fr.ensimag.deca.tree.Location;

/**
 * Definition of a variable.
 *
 * @author gl35
 * @date 01/01/2023
 */
public class VariableDefinition extends ExpDefinition {
    public VariableDefinition(Type type, Location location) {
        super(type, location);
    }
    @Override
    public boolean isVariable(){return true;}
    @Override
    public String getNature() {
        return "variable";
    }

    @Override
    public boolean isExpression() {
        return true;
    }

    public boolean sameDefinition(ExpDefinition definition){
        return definition.isVariable();
    }
}

package fr.ensimag.deca.context;

import java.util.ArrayList;
import java.util.List;

/**
 * Signature of a method (i.e. list of arguments)
 *
 * @author gl35
 * @date 01/01/2023
 */
public class Signature {
    List<Type> args = new ArrayList<Type>();

    public void add(Type t) {
        args.add(t);
    }
    
    public Type paramNumber(int n) {
        return args.get(n);
    }
    
    public int size() {
        return args.size();
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof Signature){
            Signature signature = (Signature) o;
            if (this.size() != signature.size()){
                return false;
            }
            for (int i = 0; i < signature.size(); i++) {
                if (!this.paramNumber(i).sameType(signature.paramNumber(i))){
                    return false;
                }
            }
            return true;
        }
        else {
            return false;
        }
    }

}

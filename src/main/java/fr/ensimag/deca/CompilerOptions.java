package fr.ensimag.deca;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.lang.Integer;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * User-specified options influencing the compilation.
 *
 * @author gl35
 * @date 01/01/2023
 */
public class CompilerOptions {
    public static final int QUIET = 0;
    public static final int INFO  = 1;
    public static final int DEBUG = 2;
    public static final int TRACE = 3;
    public int getDebug() {
        return debug;
    }

    public boolean getParallel() {
        return parallel;
    }

    public boolean getPrintBanner() {
        return printBanner;
    }
    public boolean getDecompile(){
        return decompile;
    }
    
    public List<File> getSourceFiles() {
        return Collections.unmodifiableList(sourceFiles);
    }
    public boolean getverify(){
        return verify;
    }
    public int getrmaxoption(){
        return rmaxoption;
    }

    private int debug = 0;
    private boolean parallel = false;
    private boolean printBanner = false;
    private boolean decompile = false;
    private boolean verify = false;
    private int rmaxoption = 15;
    private List<File> sourceFiles = new ArrayList<File>();

    
    public void parseArgs(String[] args) throws CLIException {
        // A FAIRE : parcourir args pour positionner les options correctement.
        for (String s: args){
            if (s.equals("-p")){
                if (verify){
                    throw  new CLIException("Les options '-p' et '-v' sont incompatibles.Sorry");
                }
                decompile = true;
              } else if (rmaxoption == 0) {
                try {
                    int i = Integer.parseInt(s);
                    if (4 <= i && i <= 16) {
                        rmaxoption = i;
                    } else {
                        throw  new CLIException("L'option -r X : X doit etre un entier entre 4 et 16");
                    }

                } catch (NumberFormatException i) {
                    throw new CLIException("L'option -r X : X doit etre un entier entre 4 et 16");
                }
            } else if (s.equals("-b")) {
                 printBanner = true;
            } else if (s.equals("-v")) {
                if (decompile){
                    throw  new CLIException("Les options '-p' et '-v' sont incompatibles.Sorry");
                }
                verify = true;
            } else if (s.equals("-n")) {
                noCheck = true;

                
            } else if (s.equals("-r")) {
                rmaxoption = 0;
            } else if (s.equals("-P")) {
                parallel = true;
            } else if (s.equals("-d")) {
                debug++;
            } else {
                if (!printBanner) {
                    File myObj = new File(s);
                    sourceFiles.add(myObj);
                }
            }

        }
        Logger logger = Logger.getRootLogger();
        // map command-line debug option to log4j's level.
        switch (getDebug()) {
        case QUIET: break; // keep default
        case INFO:
            logger.setLevel(Level.INFO); break;
        case DEBUG:
            logger.setLevel(Level.DEBUG); break;
        case TRACE:
            logger.setLevel(Level.TRACE); break;
        default:
            logger.setLevel(Level.ALL); break;
        }
        logger.info("Application-wide trace level set to " + logger.getLevel());

        boolean assertsEnabled = false;
        assert assertsEnabled = true; // Intentional side effect!!!
        if (assertsEnabled) {
            logger.info("Java assertions enabled");
        } else {
            logger.info("Java assertions disabled");
        }

        //throw new UnsupportedOperationException("not yet implemented");
    }

    protected void displayUsage() {
        System.out.println("La syntaxe d’utilisation de l’exécutable decac est");
        System.out.println("decac [[-p | -v] [-n] [-r X] [-d]* [-P] [-w] <fichier deca>...] | [-b]");
        System.out.println(" --------------------------------------------------------------------------------------------------");
        System.out.println("                                            Les options de decac");
        System.out.println("---------------------------------------------------------------------------------------------------");
        System.out.println("  -b (banner) : affiche une bannière indiquant le nom de l'équipe");
        System.out.println("  -v (verification) : arrête decac après l'étape de vérifications ne produit aucune sortie en l'absence d'erreur");
        System.out.println("  -n (no check) : supprime les tests à l'exécution spécifiés dans les points 11.1 et 11.3 de la sémantique de Deca.");
        System.out.println("  -r X (registers) : limite les registres banalisés disponibles à R0 ... R{X-1}, avec 4 <= X <= 16");
        System.out.println("  -d (debug) : active les traces de debug. Répéter l'option plusieurs fois pour avoir plus de traces.");
        System.out.println("  -P (parallel) : s'il y a plusieurs fichiers sources, lance la compilation des fichiers en parallèle (pour accélérer la compilation)");

    }
    private boolean noCheck = false;


    public boolean isNoCheck() {
        return noCheck;
    }
}

package fr.ensimag.deca;

import java.io.File;
import org.apache.log4j.Logger;

import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * Main class for the command-line Deca compiler.
 *
 * @author gl35
 * @date 01/01/2023
 */
public class DecacMain {
    private static Logger LOG = Logger.getLogger(DecacMain.class);
    
    public static void main(String[] args) {
        // example log4j message.
        LOG.info("Decac compiler started");
        boolean error = false;
        final CompilerOptions options = new CompilerOptions();
        try {
            options.parseArgs(args);
        } catch (CLIException e) {
            System.err.println("Error during option parsing:\n"
                    + e.getMessage());
            options.displayUsage();
            System.exit(0);
        }
        if (options.getPrintBanner()) {
            System.out.println("---------------------------------------------------");
            System.out.println("                 Ensimag Projet GL");
            System.out.println( );
            System.out.println("         gl35 -> Morad,Sana,Ayman,Tifaf,habib");
            System.out.println("---------------------------------------------------");
            System.exit( 0 );
        }
        if (options.getSourceFiles().isEmpty() && !options.getPrintBanner()) {
            System.out.println("Absence de fichiers source a compiler, Sorry");
            options.displayUsage();
            System.exit( 0 );

        }

        if (options.getParallel()) {
            // A FAIRE : instancier DecacCompiler pour chaque fichier à
            // compiler, et lancer l'exécution des méthodes compile() de chaque
            // instance en parallèle. Il est conseillé d'utiliser
            // java.util.concurrent de la bibliothèque standard Java.
            int nmbProce = java.lang.Runtime.getRuntime().availableProcessors();
            ExecutorService threads = Executors.newFixedThreadPool(nmbProce);
            Future <? >[] futures = new Future [options.getSourceFiles().size() ];
            for (int i = 0; i < options.getSourceFiles().size(); i++) {
                DecacCompiler compiler = new DecacCompiler(options, options.getSourceFiles().get(i));
                ThreadsCompiler filexec = new ThreadsCompiler(compiler);
                futures[i] = threads.submit(filexec);

            }
            for(Future f:futures){
                try {
                    f.get();
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                threads.shutdown();
            }
            //throw new UnsupportedOperationException("Parallel build not yet implemented");
        } else {
            //System.out.println("moi");
            for (File source : options.getSourceFiles()) {
                DecacCompiler compiler = new DecacCompiler(options, source);
                if (compiler.compile()) {
                    error = true;
                }

            }
        }

        System.exit(error ? 1 : 0);
    }
}

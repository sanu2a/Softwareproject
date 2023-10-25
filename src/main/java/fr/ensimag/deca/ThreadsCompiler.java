package fr.ensimag.deca;

import fr.ensimag.deca.DecacCompiler;
import java.io.File;

import static java.lang.Thread.yield;


public class ThreadsCompiler implements Runnable {
   DecacCompiler compiler;

    public ThreadsCompiler(DecacCompiler compiler){
        this.compiler = compiler;

    }
    public void run(){
        compiler.compile();

    }
}

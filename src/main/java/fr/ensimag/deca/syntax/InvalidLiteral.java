package fr.ensimag.deca.syntax;


import org.antlr.v4.runtime.ParserRuleContext;

public class InvalidLiteral extends DecaRecognitionException{
    public InvalidLiteral(DecaParser recognizer, ParserRuleContext ctx) {
        super(recognizer, ctx);
    }
    @Override
    public String getMessage() {
        return "the given literal is too big.";
    }

}

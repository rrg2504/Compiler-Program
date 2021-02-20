/**
 * Author: Ramses Geronimo
 *
 * This program initializes everything that deals with tokenizing, parsing, creating symbol table,
 * and the execution of the code.
 */

import Parser.ParseTree;
import Parser.Program;
import Scanner.Token;
import Scanner.Tokenizer;

import java.util.LinkedList;

public class Compile {
    public static void main(String[] args){
        String fName = args[0];
        Tokenizer myToken = new Tokenizer(fName);

        LinkedList<Token> newList = myToken.getListOfTokens();

        for(int x = 0; x < newList.size(); x++){
            if(x != 0){
                if(Tokenizer.Negative(x,newList)){
                    newList.get(x).setNegative(true);
                }
            }
        }

        Tokenizer.setHasElse(newList);
        ParseTree parse = new ParseTree(newList,fName);
        Program myProgram = parse.getRoot();


        myProgram.execute(parse.getSymTab());


    }
}

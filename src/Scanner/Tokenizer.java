/**
 *
 * Author: Ramses Geronimo
 * File name: Tokenizer.java
 * Description: Tokenizes the given string inputs and creates a list of Token class object
 */
package Scanner;
import java.io.*;
import java.util.LinkedList;
import java.util.Scanner;

public class Tokenizer {
    private LinkedList<Token> listOfTokens;

    private int lineNum = 1;
    private String fileName;


    /**
     * Reads in a file then tokenizes the file and adds token class
     * in a linkedlist of tokens.
     * @param str
     */
    public Tokenizer(String str){
        this.fileName = str;
        listOfTokens = new LinkedList<>();
        File file = new File(str);
        try(Scanner in = new Scanner(file)) {
            in.useDelimiter("");
            if (!in.hasNext()){
                System.exit(1);
            }
            String temp = in.next();
            char c = temp.charAt(0);
            StringBuilder token = new StringBuilder();  //will continue creating the string until dfa accepts
            while(true){
                if (temp == null){
                    break;
                }
                if(!in.hasNext()){

                    if (temp.charAt(0) == '\n'){
                        break;
                    }
                    token.append(temp.charAt(0));
                    Token t = new Token(token.toString(), lineNum);
                    listOfTokens.add(t);
                    token = new StringBuilder();
                    break;
                }
                else if(temp.equals("\"")){
                    token.append(temp);
                    while(true) {

                        temp = in.next();
                        c = temp.charAt(0);
                        if(temp.equals("\"")){
                            token.append(temp);
                            Token t = new Token(token.toString(), lineNum);
                            listOfTokens.add(t);
                            token = new StringBuilder();
                            temp = in.next();
                            c = temp.charAt(0);
                            break;
                        }

                        else if (Character.isDigit(c)) {
                            token.append(c);
                        }else if(Character.isLetter(c)){
                            token.append(c);
                        }else if(c == ' '){
                            token.append(c);
                        }
                        else {
                            System.err.println("Syntax error:  Missing \", line " + lineNum + " of " + fileName);
                            System.exit(1);
                        }
                    }
                }
                else if(Character.isLetter(c)){
                    token.append(c);
                    while(true){
                        temp = in.next();
                        c = temp.charAt(0);
                        if(!Character.isLetter(c) && !Character.isDigit(c)){
                            break;
                        }

                        token.append(c);

                    }
                    Token t = new Token(token.toString(), lineNum);
                    listOfTokens.add(t);
                    token = new StringBuilder();

                }
                else if (c == '.'){
                    token.append(c);
                    while(true){
                        temp = in.next();
                        c = temp.charAt(0);
                        if(!Character.isDigit(c)){
                            Token t = new Token(token.toString(), lineNum);
                            listOfTokens.add(t);
                            token = new StringBuilder();
                            break;
                        }
                        token.append(c);

                    }
                }
                else if(Character.isDigit(c)){
                    token.append(c);
                    while(true){
                        temp = in.next();
                        c = temp.charAt(0);
                        if(!Character.isDigit(c)){
                            break;
                        }
                        token.append(c);

                    }
                    if(c == '.'){
                        token.append(c);
                        while(true){
                            temp = in.next();
                            c = temp.charAt(0);
                            if(!Character.isDigit(c)){
                                break;
                            }
                            token.append(c);

                        }
                    }
                    Token t = new Token(token.toString(), lineNum);
                    listOfTokens.add(t);
                    token = new StringBuilder();

                }
                else if(c == '>'){
                    token.append(c);
                    temp = in.next();
                    c = temp.charAt(0);
                    if(c == '='){
                        token.append(c);
                        temp = in.next();
                        c = temp.charAt(0);
                    }
                    Token t = new Token(token.toString(), lineNum);
                    listOfTokens.add(t);
                    token = new StringBuilder();
                }
                else if(c == '<'){
                    token.append(c);
                    temp = in.next();
                    c = temp.charAt(0);
                    if(c == '='){
                        token.append(c);
                        temp = in.next();
                        c = temp.charAt(0);
                    }
                    Token t = new Token(token.toString(), lineNum);
                    listOfTokens.add(t);
                    token = new StringBuilder();
                }
                else if(c == '!'){
                    token.append(c);
                    temp = in.next();
                    c = temp.charAt(0);
                    if(c !=  '='){
                        System.err.println("expected !=, instead got ! at line " + lineNum);
                        System.exit(1);
                    }
                    token.append(c);
                    temp = in.next();
                    c = temp.charAt(0);

                    Token t = new Token(token.toString(), lineNum);
                    listOfTokens.add(t);
                    token = new StringBuilder();

                }
                else if(c == '='){
                    token.append(c);
                    temp = in.next();
                    c = temp.charAt(0);
                    if(c == '='){
                        token.append(c);
                        temp = in.next();
                        c = temp.charAt(0);

                    }

                    Token t = new Token(token.toString(), lineNum);
                    listOfTokens.add(t);
                    token = new StringBuilder();
//                    temp = in.next();
//                    c = temp.charAt(0);
                }
                else if(c == ';'){
                    token.append(c);
                    Token t = new Token(token.toString(), lineNum);
                    listOfTokens.add(t);
                    token = new StringBuilder();
                    temp = in.next();
                    c = temp.charAt(0);
                }
                else if(c == '('){
                    token.append(c);
                    Token t = new Token(token.toString(), lineNum);
                    listOfTokens.add(t);
                    token = new StringBuilder();
                    temp = in.next();
                    c = temp.charAt(0);
                }
                else if(c == ')'){
                    token.append(c);
                    Token t = new Token(token.toString(), lineNum);
                    listOfTokens.add(t);
                    token = new StringBuilder();
                    temp = in.next();
                    c = temp.charAt(0);
                }
                else if(c == '^'){
                    token.append(c);
                    Token t = new Token(token.toString(), lineNum);
                    listOfTokens.add(t);
                    token = new StringBuilder();
                    temp = in.next();
                    c = temp.charAt(0);
                }


                else if(c == '/'){
                    token.append(c);

                    temp = in.next();
                    c = temp.charAt(0);
                    if (c == '/'){
                        while(true){
                            if (!in.hasNext()){
                                token = new StringBuilder();
                                temp = null;
                                break;
                            }
                            temp = in.next();
                            c = temp.charAt(0);
                            if (c == '\n'){
                                token = new StringBuilder();
                                break;
                            }
                        }
                    }
                    else {
                        Token t = new Token(token.toString(), lineNum);
                        listOfTokens.add(t);
                        token = new StringBuilder();

                        if(Character.isDigit(c)){
                            token.append(c);
                            while(true){
                                temp = in.next();
                                c = temp.charAt(0);
                                if(!Character.isDigit(c)){
                                    break;
                                }
                                token.append(c);

                            }
                            if(c == '.'){
                                token.append(c);
                                while(true){
                                    temp = in.next();
                                    c = temp.charAt(0);
                                    if(!Character.isDigit(c)){
                                        break;
                                    }
                                    token.append(c);

                                }
                            }
                            t = new Token(token.toString(), lineNum);
                            listOfTokens.add(t);
                            token = new StringBuilder();

                        }
                    }
                }
                else if(c == '*'){
                    token.append(c);
                    Token t = new Token(token.toString(), lineNum);
                    listOfTokens.add(t);
                    token = new StringBuilder();
                    temp = in.next();
                    c = temp.charAt(0);
                }
                else if(c == '-'){
                    token.append(c);
                    Token t = new Token(token.toString(), lineNum);
                    listOfTokens.add(t);
                    token = new StringBuilder();
                    temp = in.next();
                    c = temp.charAt(0);
                }
                else if(c == '+'){
                    token.append(c);
                    Token t = new Token(token.toString(), lineNum);
                    listOfTokens.add(t);
                    token = new StringBuilder();
                    temp = in.next();
                    c = temp.charAt(0);
                }
                else if(c == ','){
                    token.append(c);
                    Token t = new Token(token.toString(), lineNum);
                    listOfTokens.add(t);
                    token = new StringBuilder();
                    temp = in.next();
                    c = temp.charAt(0);
                }
                else if(c == '{'|| c == '}'){
                    token.append(c);
                    Token t = new Token(token.toString(), lineNum);
                    listOfTokens.add(t);
                    token = new StringBuilder();
                    temp = in.next();
                    c = temp.charAt(0);
                }
                else if (c == '\n'){
                    if (token.length() > 0){
                        Token t = new Token(token.toString(), lineNum);
                        listOfTokens.add(t);
                        token = new StringBuilder();
                    }
                    temp = in.next();
                    c = temp.charAt(0);
                    lineNum++;
                }
                else if (c == ' ' || c == '\r'){
                    temp = in.next();
                    c = temp.charAt(0);
                }
                else {
                    System.err.println("Syntax error: unexpected symbol '" + c + "' at " + lineNum + " of " + fileName);
                    System.exit(1);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * checks if the token is an operator
     * @param input
     * @return
     */
    public static boolean isOperator(String input){
        if(input.equals("+")){
            return true;
        }else if(input.equals("-")){
            return true;
        }else if(input.equals("/")){
            return true;
        }else if(input.equals("*")){
            return true;
        }else return input.equals("^");
    }

    /**
     * checks if given token is negative
     */
    public static boolean Negative(int idx,LinkedList<Token>token){
        if(isOperator(token.get(idx - 1).getToken())){
            if(token.get(idx).getToken().equals("-")){
                return true;
            }
        }
        if(token.get(idx - 1).getToken().equals("(")){
            if(token.get(idx).getToken().equals("-")){
                return true;
            }
        }
        if(token.get(idx - 1).getToken().equals("=")){
            return token.get(idx).getToken().equals("-");
        }
        if(token.get(idx - 1).getToken().equals(",")){
            return token.get(idx).getToken().equals("-");
        }
        return false;
    }

    /**
     * checks if 'if' token has an else statement
     * @param tokens
     */
    public static void setHasElse(LinkedList<Token>tokens){
        for(int x = 0; x < tokens.size(); x++){
            if(tokens.get(x).getToken().equals("if")){
                int y = x + 1;
                while(true){

                    if(y >= tokens.size()){
                        break;
                    }
                    else if(tokens.get(y).getToken().equals("if")){
                        break;
                    }else if(tokens.get(y).getToken().equals("else")){
                        tokens.get(x).setIfHasElse(true);
                        break;
                    }
                    y++;
                }
            }
        }
    }

    /**
     * checks if there is a bracket
     * @param tokens
     */
    public static void ifHasBracket(LinkedList<Token>tokens){
        for(int x = 0; x < tokens.size(); x++){
            if(tokens.get(x).getToken().equals("if")){
                int y = x + 1;
                while(true){

                    if(y >= tokens.size()){
                        break;
                    }
                    else if(tokens.get(y).getToken().equals("}")){
                        break;
                    }else if(tokens.get(y).getToken().equals("else")){
                        tokens.get(x).setIfHasElse(true);
                        break;
                    }
                    y++;
                }
            }
        }
    }
    public LinkedList<Token> getListOfTokens() {
        return listOfTokens;
    }



}

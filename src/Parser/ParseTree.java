package Parser;

import Scanner.Token;

import javax.swing.*;
import java.beans.Expression;
import java.text.ParseException;
import java.util.*;

/**
 * This algorithm creates the parse tree by first reading in the stack, popping off the read
 * item and adding its two children based on what the token is and adding the children to the stack
 * so that it will eventually be evaluated. (notice that the right child will be added to the stack first
 * since we want to go from left to right.)
 */
public class ParseTree {

    //    private Node root
    private String currentVar = null;
    private Stack<String> myStack;
    private Program program;
    private HashMap<String,Object> symTab;
    private LinkedList<Token> tokens;
    private LinkedList<Token> errorTokens;
    private static boolean isNegative = false;
    private String inputFile;
    private static boolean hasEndBracket = false;
    private static boolean forWithoutEnd = false;
    private HashMap<String,String>funtionTypes = new HashMap<>();  //to map the functions to its types
    private Collection<String>functions = new ArrayList<String>(); // so that it knows a token is a function
    private HashMap<String,String>varTypes = new HashMap<>();

    /**
     * This constuctor adds in a new hashmap, symbol table, stack, and starts off with
     * a program root node and makes its children if the tokens list is not empty.
     * @param tokens
     */
    public ParseTree(LinkedList<Token> tokens, String inputFile){
        this.symTab = new HashMap<>();
        this.program = new Program();
        this.tokens = tokens;
        this.myStack = new Stack<>();
        errorTokens = new LinkedList<Token>(tokens);
        this.inputFile = inputFile;

        if(!tokens.isEmpty()){
            //myStack.pop();
            myStack.add("$$$");
            myStack.add("Stmt_list");
            program.setLeftChild(parseAction(tokens,myStack));
            program.setRightChild(parseAction(tokens,myStack));
        }
    }

    public String getFileName(){
        return inputFile;
    }

    public Collection<String> getFunctions(){
        return this.functions;
    }
    /**
     * Returns the string that made the error
     * @param lineNumber
     * @return
     */
    public String makeErrorString(int lineNumber){
        StringBuilder line = new StringBuilder();
        for(int x = 0; x < errorTokens.size(); x++){
            if(errorTokens.get(x).getLineNumber() == lineNumber){
                line.append(errorTokens.get(x).getToken());
                if(!errorTokens.get(x).getToken().equals(")") && !errorTokens.get(x).getToken().equals(";")){
                    line.append(" ");
                }

            }

        }
        return line.toString();
    }

    /**
     * returns if token is an integer
     * @param input
     * @return
     */
    public boolean isInteger(String input){
        try{
            Integer.parseInt(input);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * returns if the token is a double
     * @param input
     * @return
     */
    public boolean isDouble(String input){
        for(int x = 0; x < input.length(); x++){
            if(input.charAt(x) == '.'){
                return true;
            }
        }
        return false;
    }

    /**
     * Returns if the token is an operator
     * @param input
     * @return
     */
    public boolean isOperator(String input){
        if(input.equals("+")){
            return true;
        }else if(input.equals("-")){
            return true;
        }else if(input.equals("/")){
            return true;
        }else if(input.equals("*")){
            return true;
        }else if(input.equals(">")){
            return true;
        }else if(input.equals("<")){
            return true;
        }else if(input.equals("<=")){
            return true;
        }else if(input.equals(">=")){
            return true;
        }else if(input.equals("==")){
            return true;
        }else if(input.equals("!=")){
            return true;
        }else return input.equals("^");
    }

    private functions parseFunction(LinkedList<Token> tokens, Stack<String>myStack){
        if(myStack.peek().equals("P_list")){
            if(tokens.get(0).getToken().equals("(")){
                tokens.remove();
                //maybe add boolean
            }
            myStack.pop();
            if(tokens.get(0).getToken().equals("Integer") ||tokens.get(0).getToken().equals("String")
                    || tokens.get(0).getToken().equals("Double")){  //if the type def of p list is an Integer
                myStack.push("Id");
                String type = tokens.remove().getToken();
                ExpressionNode id = parseExpression(tokens,myStack);

                // check if there is another parameter
                if(tokens.get(0).getToken().equals(",")){
                    // other p list
                    tokens.remove();
                    myStack.push("P_list");
                    return new P_list(type,id,parseFunction(tokens,myStack));

                }
                if(!tokens.get(0).getToken().equals(")")){
                    return null; // syntax erroe
                }
                tokens.remove();
                if(!tokens.get(0).getToken().equals("{")){
                    return null;
                }
                tokens.remove();
                return new P_list(type,id);
            }
        }

        if(myStack.peek().equals("F_stmt")){
            myStack.pop();
            if(tokens.get(0).getToken().equals("return")){
                tokens.remove();
                myStack.push("Expr");
                ExpressionNode expr = parseExpression(tokens,myStack);
                if(!tokens.get(0).getToken().equals(";")){
                    return null;
                }
                tokens.remove();
                if(!tokens.get(0).getToken().equals("}")){
                    return null; // syntax erroe
                }
                tokens.remove();
                return new Funct_stmt(true,expr);
            }else{
                myStack.push("F_stmt");
                myStack.push("Stmt");
                ActionNode stmt = parseAction(tokens,myStack);
                Parser.functions f_stmt;
                if(tokens.get(0).getToken().equals("}")){   //this means no return
                    myStack.pop();
                    tokens.remove();
                    f_stmt = null;
                }else{
                    f_stmt = parseFunction(tokens,myStack);
                }

                return new Funct_stmt(stmt,f_stmt);
            }

        }
        return null;
    }
    /**
     * This parses action nodes. Since they dont neccesarily represent any value but instead
     * do something to the values, they are classified as action nodes.
     * @param tokens
     * @param myStack
     * @return
     */
    private ActionNode parseAction(LinkedList<Token> tokens, Stack<String>myStack){
//        if(!(tokens.size() <= 0) && tokens.get(0).getToken().equals("}")){
//            hasEndBracket = true;
//            tokens.remove();
//        }
        if(myStack.isEmpty()){
            return new DollarSign();
        }
        if(myStack.peek().equals("$$$")){
            myStack.pop();
            return new DollarSign();
        }
        if(!(tokens.size() <= 0) && tokens.get(0).getToken().equals("}")){
            myStack.pop();
            tokens.remove();
            hasEndBracket = true;
            return new Epsilon();
        }
        if(tokens.isEmpty() && myStack.peek().equals("Stmt_list")){
            myStack.pop();
            return new Stmt_list(new Epsilon(),new Epsilon());
        }
        if(tokens.isEmpty() && myStack.peek().equals("B_Stmt_list")){
            myStack.pop();
            return new B_Stmt_list(new Epsilon(),new Epsilon());
        }
        if(tokens.get(0).getToken().contains("\n")){
            myStack.pop();
            tokens.remove();
            return new Stmt_list(new Epsilon(),new Epsilon());
        }


        if(myStack.peek().equals("Stmt_list")){
            myStack.pop();
            myStack.push("Stmt_list");
            myStack.push("Stmt");
            return new Stmt_list(parseAction(tokens,myStack),parseAction(tokens,myStack));
        }

        if(myStack.peek().equals("Fc_pl")){
            myStack.pop();
            myStack.push("Expr");
            if(functions.contains(tokens.get(0).getToken())){
                myStack.pop();
                myStack.push("F_call");
                ExpressionNode call = parseExpression(tokens,myStack);
                if(tokens.get(0).getToken().equals(",")){
                    return null; //more calls
                }
//                if(!tokens.get(0).getToken().equals(")")){
////                    String error = makeErrorString(tokens.get(0).getLineNumber());
////                    System.err.println("Syntax Error: Missing Character: Expected ) in print, " +
////                            "\""  + error + "\" (inputs/" + inputFile + ":" + tokens.get(0).getLineNumber() + ")");
//                    System.out.println("error");
//                    System.exit(1);
//                }
//                tokens.remove();
//                if(tokens.size() == 0 || !tokens.get(0).getToken().equals(";") ){
////                    String error = makeErrorString(noNull.getLineNumber());
////                    System.err.println("Syntax Error: Missing Character: Expected ; in print, " +
////                            "\""  + error + "\" (inputs/" + inputFile + ":" + noNull.getLineNumber() + ")");
////                    System.exit(1);
//                    System.out.println("error");
//                    System.exit(1);
//                }
//                tokens.remove();

//                myStack.push("Fc_pl");
//                myStack.push("Id");
//                String name = tokens.get(0).getToken();
//                ExpressionNode id = parseExpression(tokens,myStack);
//                ExpressionNode functCall = new F_call(id,name);
                return new Fc_p_list(call);
            }
            ExpressionNode expr  = parseExpression(tokens,myStack);
            if(tokens.get(0).getToken().equals(",")){
                myStack.push("Fc_pl");
                tokens.remove();
                return new Fc_p_list(expr,(functions)parseAction(tokens,myStack));
            }
            return new Fc_p_list(expr);
        }

        if(myStack.peek().equals("Stmt")){
            myStack.pop();
            if(functions.contains(tokens.get(0).getToken())){
                myStack.push("F_call");
                return new Stmt(parseExpression(tokens,myStack));
            }
            if(tokens.get(0).getToken().equals("Integer")){ // this will return the function def. Im changing stmt -> type to a functdef class
                if(tokens.get(2).getToken().equals("(")) { //should maybe check for errors idk
                    if(tokens.get(3).getToken().equals(")")){
                        return null; // if parameter has nothing
                    }
                    String var = tokens.remove().getToken();
                    myStack.push("F_stmt");  // make sure F_stmt removes from stack
                    myStack.push("P_list");  //make sur p list removes from stack
                    myStack.push("Id"); // maybe p list should be put into the local and each share the local
                    functions.add(tokens.get(0).getToken());
                    funtionTypes.put(tokens.get(0).getToken(),"Integer");
                    String varname = tokens.get(0).getToken();
                    ExpressionNode id = parseExpression(tokens,myStack);
                    functions p_list = parseFunction(tokens,myStack);
                    functions f_stmt = parseFunction(tokens,myStack);
                    return new Funct_def(var,id,p_list,f_stmt,varname,this); // will add token in there at some point
                }
                myStack.push("Asmt");
                return new Stmt(parseAction(tokens,myStack));
            }
            if(tokens.get(0).getToken().equals("Void")){
                String var = tokens.remove().getToken();
                if(tokens.get(1).getToken().equals("(") && tokens.get(2).getToken().equals(")")){
                    myStack.push("F_stmt");
                    myStack.push("Id");

                    String varname = tokens.get(0).getToken();
                    functions.add(varname);
                    ExpressionNode id = parseExpression(tokens,myStack);
                    tokens.remove();
                    tokens.remove();
                    tokens.remove();
                    functions f_stmt = parseFunction(tokens,myStack);
                    return new Funct_def(var,id,null,f_stmt,varname,this);

                }
                myStack.push("F_stmt");  // make sure F_stmt removes from stack
                myStack.push("P_list");  //make sur p list removes from stack
                myStack.push("Id");
                // maybe p list should be put into the local and each share the local
                functions.add(tokens.get(0).getToken());
                String varname = tokens.get(0).getToken();
                ExpressionNode id = parseExpression(tokens,myStack);
                functions p_list = parseFunction(tokens,myStack);
                functions f_stmt = parseFunction(tokens,myStack);
                return new Funct_def(var,id,p_list,f_stmt,varname,this);
            }
            if(tokens.get(0).getToken().equals("print")){
                //will ad to check if variable starts with lowerCase
                myStack.push("Print");
                return new Stmt(parseAction(tokens,myStack));
            }
            if(tokens.get(0).getToken().equals("Double")){
                myStack.push("Asmt");
                return new Stmt(parseAction(tokens,myStack));
            }

            if(tokens.get(0).getToken().equals("String")){
                myStack.push("Asmt");
                return new Stmt(parseAction(tokens,myStack));
            }
            if(tokens.get(0).getToken().equals("if")){
                myStack.push("If");
                return new Stmt(parseAction(tokens,myStack));
            }
            if(tokens.get(0).getToken().equals("while")){
                myStack.push("While");
                return new Stmt(parseAction(tokens,myStack));
            }
            if(tokens.get(0).getToken().equals("for")){
                myStack.push("For");
                return new Stmt(parseAction(tokens,myStack));
            }
            if(!isInteger(tokens.get(0).getToken()) && !isDouble(tokens.get(0).getToken())){
                myStack.push("R_asmt");
                return new Stmt(parseAction(tokens,myStack));
            }
        }
        if(myStack.peek().equals("For")){
            myStack.pop();
            tokens.remove();
            myStack.push("Stmt_list");
            myStack.push("R_asmt");
            myStack.push("Expr");
            myStack.push("Asmt");
            if(!tokens.get(0).getToken().equals("(")){
                return null; // will add error message
            }
            tokens.remove();
            ActionNode asmt = parseAction(tokens,myStack);
            ExpressionNode expr = parseExpression(tokens,myStack);
            if(!tokens.get(0).getToken().equals(";")){
                return null; // will add error message
            }
            tokens.remove();
            forWithoutEnd = true;
            ActionNode r_asmt = parseAction(tokens,myStack);
            forWithoutEnd = false;
            if(!tokens.get(0).getToken().equals(")")){
                return null; // will add error message
            }
            tokens.remove();
            if(!tokens.get(0).getToken().equals("{")){
                return null; // will add error message
            }
            tokens.remove();
            ActionNode body = parseAction(tokens,myStack);
            if(!hasEndBracket){
                return null;
            }
            hasEndBracket = false;
            return new For_stmt(asmt,expr,r_asmt,body);
        }
        if(myStack.peek().equals("While")){
            myStack.pop();
            tokens.remove();
            myStack.push("Stmt_list");
            myStack.push("Expr");
            if(!tokens.get(0).getToken().equals("(")){
                return null; // will add error message
            }
            tokens.remove();
            ExpressionNode I_expr = parseExpression(tokens,myStack);
            if(!tokens.get(0).getToken().equals(")")){
                return null; // will add error message
            }
            tokens.remove();
            if(!tokens.get(0).getToken().equals("{")){
                return null; // will add error
            }
            tokens.remove();
            ActionNode body = parseAction(tokens,myStack);
            if(!hasEndBracket){
                return null;
            }
            hasEndBracket = true;
            return new While_stmt(I_expr,body);
        }
        if(myStack.peek().equals("If")){
            myStack.pop();

            tokens.remove();
            myStack.push("Stmt_list");
            myStack.push("Expr");
            if(!tokens.get(0).getToken().equals("(")){
                return null; // will add error message
            }
            tokens.remove();
            ExpressionNode expr = parseExpression(tokens,myStack);
//                myStack.pop();
            if(!tokens.get(0).getToken().equals(")")){
                return null; // will add error message
            }
            tokens.remove();
            if(!tokens.get(0).getToken().equals("{")){
                return null; // will add error
            }

            tokens.remove();
            ActionNode body = parseAction(tokens,myStack);
//                myStack.pop();
            if(!hasEndBracket){
                return null; // will add error
            }
            hasEndBracket = false;
            if(!(tokens.size() < 1) && tokens.get(0).getToken().equals("else")){
                myStack.push("Stmt_list");
                tokens.remove();
                if(!tokens.get(0).getToken().equals("{")){
                    return null; // will add error message
                }
                tokens.remove();
                ActionNode elseStmt = parseAction(tokens,myStack);

                if(!hasEndBracket){
                    return null; // will add error
                }
                hasEndBracket = false;

                return new IfStmt(expr,body,elseStmt);
            }
            return new IfStmt(expr,body);

        }
        if(myStack.peek().equals("R_asmt")){
            myStack.pop();
            if(isInteger(tokens.get(2).getToken())){
                myStack.push("I_expr");

                String variable = tokens.remove().getToken();
                if(!(tokens.get(0).getToken().equals("="))){
                    System.err.println("Missing an = sign in variable assingment at line # " + tokens.get(0).getLineNumber());
                    System.exit(1);
                }
                tokens.remove();
                ExpressionNode right = parseExpression(tokens,myStack);
                if(!tokens.get(0).getToken().equals(";")){
                    System.err.println("Missing ; end statement on line # " + tokens.get(0).getLineNumber());
                    System.exit(1);
                }
                Token token = tokens.get(0);
                tokens.remove();
                return new R_asmt(variable,right,this,token);
            }
            else if(isDouble(tokens.get(2).getToken())){
                myStack.push("D_expr");

                String variable = tokens.remove().getToken();

                if(!(tokens.get(0).getToken().equals("="))){
                    System.err.println("Missing an = sign in variable assingment at line # " + tokens.get(0).getLineNumber());
                    System.exit(1);
                }
                tokens.remove();
                ExpressionNode right = parseExpression(tokens,myStack);
                if(!tokens.get(0).getToken().equals(";")){
                    System.err.println("Missing ; end statement on line # " + tokens.get(0).getLineNumber());
                    System.exit(1);
                }
                Token token = tokens.get(0);
                tokens.remove();
                return new R_asmt(variable,right,this,token);
            }
            else{
                Token noNull = tokens.get(0);
                myStack.push("Expr");
                String variable = tokens.remove().getToken();
                currentVar = variable;
//                if(varTypes.containsKey(variable)){
//                    if(varTypes.get(variable).equals("Integer")){
//                        String error = makeErrorString(noNull.getLineNumber());
//                        System.err.println("Syntax Error: Invalid type in re-assignment: Expected Integer got String;  " +
//                                "\""  + error + "\" (inputs/" + inputFile + ":" + noNull.getLineNumber() + ")");
//                        System.exit(1);
//                    }
//                }
                if(!(tokens.get(0).getToken().equals("="))){
                    System.err.println("Missing an = sign in variable assingment at line # " + tokens.get(0).getLineNumber());
                    System.exit(1);
                }
                Token token = tokens.get(0);
                tokens.remove();
                ExpressionNode right = parseExpression(tokens,myStack);
                if(!forWithoutEnd) {
                    if (!tokens.get(0).getToken().equals(";")) {
                        System.err.println("Missing ; end statement on line # " + tokens.get(0).getLineNumber());
                        System.exit(1);
                    }
                    tokens.remove();
                }
                return new R_asmt(variable,right,this,token);
            }
        }
        if(myStack.peek().equals("Asmt")){
            myStack.pop();
            if(tokens.get(0).getToken().equals("Integer")){
                Token noNull = tokens.get(0);
                String identifier = tokens.remove().getToken();
                myStack.push("I_expr");
                myStack.push("Id");
                if(Character.isUpperCase(tokens.get(0).getToken().charAt(0))){
                    System.err.println("Variable: " + tokens.get(0).getToken() +
                            " cannot start with upper case line # " + tokens.get(0).getLineNumber());
                    System.exit(1);
                }
                if(Character.isDigit(tokens.get(0).getToken().charAt(0))){
                    System.err.println("Variables cannot start with Integer/Double case line # " + tokens.get(0).getLineNumber());
                    System.exit(1);
                }
                varTypes.put(tokens.get(0).getToken(),"Integer");
                ExpressionNode variable = parseExpression(tokens,myStack);
                if(!(tokens.get(0).getToken().equals("="))){
                    System.err.println("Missing an = sign in variable assingment at line # " + tokens.get(0).getLineNumber());
                    System.exit(1);
                }
                tokens.remove();
                if(functions.contains(tokens.get(0).getToken())){
                    if(!funtionTypes.get(tokens.get(0).getToken()).equals("Integer")){
                        String error = makeErrorString(noNull.getLineNumber());
                        System.err.println("Syntax Error: String expected but got " + funtionTypes.get(tokens.get(0).getToken()) +
                                "\" "  + error + "\" (inputs/" + inputFile + ":" + noNull.getLineNumber() + ")");
                        System.exit(1);
                    }
                    myStack.pop();
                    myStack.push("F_call");
                    ExpressionNode call = parseExpression(tokens,myStack);
                    return new Asmt(identifier,variable,call);
                }
                ExpressionNode right = parseExpression(tokens,myStack);
                if(!tokens.get(0).getToken().equals(";")){
                    System.err.println("Missing ; end statement on line # " + tokens.get(0).getLineNumber());
                    System.exit(1);
                }
                tokens.remove();
                return new Asmt(identifier,variable,right);
            }if(tokens.get(0).getToken().equals("Double")){
                String identifier = tokens.remove().getToken();
                myStack.push("D_expr");
                myStack.push("Id");
                ExpressionNode variable = parseExpression(tokens,myStack);
                if(!(tokens.get(0).getToken().equals("="))){
                    System.err.println("Missing an = assingment statement at line #: " + tokens.get(0).getLineNumber());
                    System.exit(1);
                }
                tokens.remove();
                ExpressionNode right = parseExpression(tokens,myStack);
                if(!tokens.get(0).getToken().equals(";")){
                    System.err.println("Missing an end statement l at line #: " + tokens.get(0).getLineNumber());
                    System.exit(1);
                }
                tokens.remove();
                return new Asmt(identifier,variable,right);
            }
        }
        if(tokens.get(0).getToken().equals("String")){
            Token noNull = tokens.get(0);
            String identifier = tokens.remove().getToken();
            myStack.push("S_expr");
            myStack.push("Id");
            ExpressionNode variable = parseExpression(tokens,myStack);
            if(!(tokens.get(0).getToken().equals("="))){
                System.err.println("Missing an = assingment statement at line #: " + tokens.get(0).getLineNumber());
                System.exit(1);
            }
            tokens.remove();
            if(functions.contains(tokens.get(0).getToken())){
                if(!funtionTypes.get(tokens.get(0).getToken()).equals( "String")){
                    String error = makeErrorString(noNull.getLineNumber());
                    System.err.println("Syntax Error: String expected but got " + funtionTypes.get(tokens.get(0).getToken()) +
                            "\" "  + error + "\" (inputs/" + inputFile + ":" + noNull.getLineNumber() + ")");
                    System.exit(1);
                }
                myStack.pop();
                myStack.push("F_call");
                ExpressionNode call = parseExpression(tokens,myStack);
                return new Asmt(identifier,variable,call);
            }
            ExpressionNode right = parseExpression(tokens,myStack);
            if(tokens.size() == 0 || !tokens.get(0).getToken().equals(";")){
                String error = makeErrorString(noNull.getLineNumber());
                System.err.println("Syntax Error: Missing Character: Expected ; in print, " +
                        "\""  + error + "\" (inputs/" + inputFile + ":" + noNull.getLineNumber() + ")");
                System.exit(1);
            }
            tokens.remove();
            return new Asmt(identifier,variable,right);
        }
        if(myStack.peek().equals("Print")){
            Token noNull = tokens.get(0);
            tokens.remove();
            if(!tokens.get(0).getToken().equals("(")){
                String error = makeErrorString(tokens.get(0).getLineNumber());
                System.err.println("Syntax Error: Missing Character: Expected ( in print, " +
                        "\""  + error + "\" (inputs/" + inputFile + ":" + tokens.get(0).getLineNumber() + ")");
                System.exit(1);
            }
            tokens.remove();
            if(functions.contains(tokens.get(0).getToken())){
                myStack.pop();
                myStack.push("F_call");
                ExpressionNode call = parseExpression(tokens,myStack);
                if(!tokens.get(0).getToken().equals(")")){
                    String error = makeErrorString(tokens.get(0).getLineNumber());
                    System.err.println("Syntax Error: Missing Character: Expected ) in print, " +
                            "\""  + error + "\" (inputs/" + inputFile + ":" + tokens.get(0).getLineNumber() + ")");
                    System.exit(1);
                }
                tokens.remove();
                if(tokens.size() == 0 || !tokens.get(0).getToken().equals(";") ){
                    String error = makeErrorString(noNull.getLineNumber());
                    System.err.println("Syntax Error: Missing Character: Expected ; in print, " +
                            "\""  + error + "\" (inputs/" + inputFile + ":" + noNull.getLineNumber() + ")");
                    System.exit(1);
                }
                tokens.remove();
                return new Print(call);
            }
            myStack.pop();
            myStack.push("Expr");
            ExpressionNode expr = parseExpression(tokens,myStack);
            if(!tokens.get(0).getToken().equals(")")){
                String error = makeErrorString(tokens.get(0).getLineNumber());
                System.err.println("Syntax Error: Missing Character: Expected ) in print, " +
                        "\""  + error + "\" (inputs/" + inputFile + ":" + tokens.get(0).getLineNumber() + ")");
                System.exit(1);
            }
            tokens.remove();
            if(tokens.size() == 0 || !tokens.get(0).getToken().equals(";") ){
                String error = makeErrorString(noNull.getLineNumber());
                System.err.println("Syntax Error: Missing Character: Expected ; in print, " +
                        "\""  + error + "\" (inputs/" + inputFile + ":" + noNull.getLineNumber() + ")");
                System.exit(1);
            }
            tokens.remove();
            return new Print(expr);
        }
        String error = makeErrorString(tokens.get(0).getLineNumber());
        System.err.println("Syntax Error: Invalid Statement, " +
                "\""  + error + "\" (inputs/" + inputFile + ":" + tokens.get(0).getLineNumber() + ")");
        System.exit(1);
        return null;

    }

    /**
     * this method parses the expression nodes that hold values/assign or create new values
     * based on their children to pass up to parent nodes.
     * @param tokens
     * @param myStack
     * @return
     */
    private ExpressionNode parseExpression(LinkedList<Token>tokens,Stack<String>myStack){

        if(myStack.peek().equals("Id")){
            myStack.pop();
            return new Id(tokens.remove().getToken());
        }
        if(myStack.peek().equals("F_call")){
            myStack.pop();
            myStack.push("Id");
            String f_name = tokens.get(0).getToken();
            ExpressionNode id = parseExpression(tokens,myStack);
            if(!tokens.get(0).getToken().equals("(")){
                return null;
            }
            tokens.remove();
            if(tokens.get(0).getToken().equals(")")){
                tokens.remove();
                tokens.remove();
                return new F_call(id,f_name);  //maybe improve idk if correct
            }
            myStack.push("Fc_pl");
            ActionNode f_p_list = parseAction(tokens,myStack);
            if(!tokens.get(0).getToken().equals(")")){
                return null;
            }
            tokens.remove();
            if(tokens.get(0).getToken().equals(";")){
                tokens.remove();
            }
            return new F_call(id,f_name,(functions)f_p_list);

        }
        if(myStack.peek().equals("VariableOp")){
            myStack.pop();
            if(tokens.get(3).getToken() != null){
                if(isOperator(tokens.get(3).getToken())){
                    ExpressionNode lhs = new Id(tokens.remove().getToken());
                    String operator = tokens.remove().getToken();
                    myStack.add("VariableOp");
                    ExpressionNode rhs = parseExpression(tokens,myStack);
                    Token token = new Token("sa",1);
                    return new VariableOperation(lhs,rhs,operator,this,token); // will keep adding more binary operations
                }
            }
            ExpressionNode lhs = new Id(tokens.remove().getToken());
            Token token = tokens.get(0);
            String operator = tokens.remove().getToken();
            ExpressionNode rhs = new Id(tokens.remove().getToken());
            return new VariableOperation(lhs,rhs,operator,this,token);
        }
        if(myStack.peek().equals("D_expr")){
            myStack.pop();
            if(tokens.get(0).isNegative()){
                isNegative = true;
                tokens.remove();
            }
            if(!isDouble(tokens.get(0).getToken())){
                myStack.push("Id");
                Token token = tokens.get(0);
                ExpressionNode node = parseExpression(tokens,myStack);
                return new D_expr(null,node,null,this,token);
            }
            if(tokens.get(0).isNegative()){
                isNegative = true;
                tokens.remove();
            }
            if(isInteger(tokens.get(0).getToken())){
                String error = makeErrorString(tokens.get(0).getLineNumber());
                System.err.println("Syntax Error: Expected Double got Integer, "
                        + error + " (inputs/" + inputFile + ":" + tokens.get(0).getLineNumber() + ")");
                System.exit(1);
            }
            if(!isOperator(tokens.get(1).getToken())){
                if(isNegative){
                    isNegative = false;
                    Token token = tokens.get(0);
                    ExpressionNode node = new DoubleCons(-1 * Double.parseDouble(tokens.remove().getToken()));
                    return new D_expr(null,node,null,this,token);
                }
                Token token = tokens.get(0);
                ExpressionNode node = new DoubleCons(Double.parseDouble(tokens.remove().getToken()));
                return new D_expr(null,node,null,this,token);
            }else{
                if(isOperator(tokens.get(3).getToken())|| isOperator(tokens.get(4).getToken())){
                    ExpressionNode left;
                    if(isNegative){
                        left = new DoubleCons(-1 * Double.parseDouble(tokens.remove().getToken()));
                        isNegative = false;
                    }
                    else{
                        left = new DoubleCons(Double.parseDouble(tokens.remove().getToken()));
                    }
                    if(isInteger(tokens.get(0).getToken())){
                        String error = makeErrorString(tokens.get(0).getLineNumber());
                        System.err.println("Syntax Error: Expected Double got Integer, "
                                + error + " (inputs/" + inputFile + ":" + tokens.get(0).getLineNumber() + ")");
                        System.exit(1);
                    }
                    Token token = tokens.get(0);
                    String identifier = tokens.remove().getToken();
                    myStack.push("D_expr");
                    ExpressionNode right = parseExpression(tokens,myStack);
                    return new D_expr(identifier,left,right,this,token);

                }
                ExpressionNode left;
                if(isNegative){
                    left = new DoubleCons(-1 * Double.parseDouble(tokens.remove().getToken()));
                    isNegative = false;
                }
                else{
                    left = new DoubleCons(Double.parseDouble(tokens.remove().getToken()));
                }
                if(isInteger(tokens.get(0).getToken())){
                    String error = makeErrorString(tokens.get(0).getLineNumber());
                    System.err.println("Syntax Error: Expected Double got Integer, "
                            + error + " (inputs/" + inputFile + ":" + tokens.get(0).getLineNumber() + ")");
                    System.exit(1);
                }
                String identifier = tokens.remove().getToken();
                if(tokens.get(0).isNegative()){
                    tokens.remove();
                    if(isInteger(tokens.get(0).getToken())){
                        String error = makeErrorString(tokens.get(0).getLineNumber());
                        System.err.println("Syntax Error: Expected Double got Integer, "
                                + error + " (inputs/" + inputFile + ":" + tokens.get(0).getLineNumber() + ")");
                        System.exit(1);
                    }
                    Token token = tokens.get(0);
                    ExpressionNode right = new DoubleCons(-1 * Double.parseDouble(tokens.remove().getToken()));
                    return new D_expr(identifier,left,right,this,token);
                }else{
                    if(isInteger(tokens.get(0).getToken())){
                        String error = makeErrorString(tokens.get(0).getLineNumber());
                        System.err.println("Syntax Error: Expected Double got Integer, "
                                + error + " (inputs/" + inputFile + ":" + tokens.get(0).getLineNumber() + ")");
                        System.exit(1);
                    }
                    Token token = tokens.get(0);
                    ExpressionNode right = new DoubleCons(Double.parseDouble(tokens.remove().getToken()));
                    return new D_expr(identifier,left,right,this,token);
                }
            }
        }
        if(myStack.peek().equals("I_expr")) {
            myStack.pop();
            if(tokens.get(0).isNegative()){
                isNegative = true;
                tokens.remove();
            }
            if(!isInteger(tokens.get(0).getToken())){
                Token token = tokens.get(0);
                myStack.push("Id");
                ExpressionNode node = parseExpression(tokens,myStack);
                return new I_expr(null,node,null,this,token);
            }
            if(tokens.get(0).isNegative()){
                isNegative = true;
                tokens.remove();
            }
            if(isDouble(tokens.get(0).getToken())){
                String error = makeErrorString(tokens.get(0).getLineNumber());
                System.err.println("Syntax Error: Expected Integer got Double, "
                        + error + " (inputs/" + inputFile + ":" + tokens.get(0).getLineNumber() + ")");
                System.exit(1);
            }
            if(!isOperator(tokens.get(1).getToken())){
                Token token = tokens.get(1);
                if(isNegative){
                    isNegative = false;
                    ExpressionNode node = new Constant(-1 * Integer.parseInt(tokens.remove().getToken()));
                    return new I_expr(null,node,null,this,token);
                }
                ExpressionNode node = new Constant(Integer.parseInt(tokens.remove().getToken()));
                return new I_expr(null,node,null,this,token);
            }
            else{
                if((isOperator(tokens.get(3).getToken()) || isOperator(tokens.get(4).getToken()))){
                    ExpressionNode left;
                    if(isNegative){
                        left = new Constant(-1 * Integer.parseInt(tokens.remove().getToken()));
                        isNegative = false;
                    }
                    else{
                        left = new Constant(Integer.parseInt(tokens.remove().getToken()));
                    }
                    if(isDouble(tokens.get(0).getToken())){
                        String error = makeErrorString(tokens.get(0).getLineNumber());
                        System.err.println("Syntax Error: Expected Integer got Double, "
                                + error + " (inputs/" + inputFile + ":" + tokens.get(0).getLineNumber() + ")");
                        System.exit(1);
                    }
                    Token token = tokens.get(0);
                    String identifier = tokens.remove().getToken();
                    myStack.push("I_expr");
                    ExpressionNode right = parseExpression(tokens,myStack);
                    return new I_expr(identifier,left,right,this,token);

                }
                ExpressionNode left;
                if(isNegative){
                    left = new Constant(-1 * Integer.parseInt(tokens.remove().getToken()));
                    isNegative = false;
                }
                else{
                    left = new Constant(Integer.parseInt(tokens.remove().getToken()));
                }
                String identifier = tokens.remove().getToken();
                if(tokens.get(0).isNegative()){
                    Token token = tokens.get(0);
                    tokens.remove();
                    if(isDouble(tokens.get(0).getToken())){
                        String error = makeErrorString(tokens.get(0).getLineNumber());
                        System.err.println("Syntax Error: Expected Integer got Double, "
                                + error + " (inputs/" + inputFile + ":" + tokens.get(0).getLineNumber() + ")");
                        System.exit(1);
                    }
                    ExpressionNode right = new Constant(-1 * Integer.parseInt(tokens.remove().getToken()));
                    return new I_expr(identifier,left,right,this,token);
                }else{
                    if(isDouble(tokens.get(0).getToken())){
                        String error = makeErrorString(tokens.get(0).getLineNumber());
                        System.err.println("Syntax Error: Expected Integer got Double, "
                                + error + " (inputs/" + inputFile + ":" + tokens.get(0).getLineNumber() + ")");
                        System.exit(1);
                    }
                    Token token = tokens.get(0);
                    ExpressionNode right = new Constant(Integer.parseInt(tokens.remove().getToken()));
                    return new I_expr(identifier,left,right,this,token);
                }

            }
        }
        if(myStack.peek().equals("S_expr")){
            myStack.pop();
            if(tokens.get(0).getToken().charAt(0) != '"'
                    && tokens.get(0).getToken().charAt(tokens.get(0).getToken().length() - 1) == '"'){
                String error = makeErrorString(tokens.get(0).getLineNumber());

                System.err.println("Syntax Error: Missing Character: Expected \" in string defenition, " +
                        "\""  + error + "\" (inputs/" + inputFile + ":" + tokens.get(0).getLineNumber() + ")");
                System.exit(1);
            }
            if(tokens.get(0).getToken().charAt(0) == '"'){
                if(!(tokens.get(0).getToken().charAt(tokens.get(0).getToken().length() - 1) == '"')){
                    String error = makeErrorString(tokens.get(0).getLineNumber());

                    System.err.println("Syntax Error: Missing Character: Expected \" in string defenition, " +
                            "\""  + error + "\" (inputs/" + inputFile + ":" + tokens.get(0).getLineNumber() + ")");
                    System.exit(1);
                }
                ExpressionNode string = new Str_literal(tokens.remove().getToken());

                return new S_expr(string,null);
            }
            if(tokens.get(0).getToken().equals("charAt")){
                myStack.push("Expr");
                myStack.push("S_expr");

                String function = tokens.remove().getToken();
                if(!tokens.get(0).getToken().equals("(")){
                    String error = makeErrorString(tokens.get(0).getLineNumber());

                    System.err.println("Syntax Error: Missing Character: Expected ( in charAt, " +
                            "\""  + error + "\" (inputs/" + inputFile + ":" + tokens.get(0).getLineNumber() + ")");

                    System.exit(1);
                }
                tokens.remove();
                ExpressionNode left = parseExpression(tokens,myStack);
                if(!tokens.get(0).getToken().equals(",")){
                    String error = makeErrorString(tokens.get(0).getLineNumber());

                    System.err.println("Syntax Error: Expected , got " +
                            "\""  + error + "\" (inputs/" + inputFile + ":" + tokens.get(0).getLineNumber() + ")");
                    System.exit(1); // missing a parameter
                }
                tokens.remove();
                ExpressionNode right = parseExpression(tokens,myStack);
                if(!tokens.get(0).getToken().equals(")")){
                    String error = makeErrorString(tokens.get(0).getLineNumber());

                    System.err.println("Syntax Error: Missing Character: Expected ) in concat, " +
                            "\""  + error + "\" (inputs/" + inputFile + ":" + tokens.get(0).getLineNumber() + ")");
                    System.exit(1);
                }
                tokens.remove();
                return new S_expr(function,left,right);

            }
            if(tokens.get(0).getToken().equals("concat")){

                myStack.push("S_expr");
                myStack.push("S_expr");
                String function = tokens.remove().getToken();
                if(!tokens.get(0).getToken().equals("(")){
                    String error = makeErrorString(tokens.get(0).getLineNumber());

                    System.err.println("Syntax Error: Missing Character: Expected ( in concat, " +
                            "\""  + error + "\" (inputs/" + inputFile + ":" + tokens.get(0).getLineNumber() + ")");
                    System.exit(1);
                }
                tokens.remove();
                ExpressionNode left = parseExpression(tokens,myStack);
                if(!tokens.get(0).getToken().equals(",")){
                    String error = makeErrorString(tokens.get(0).getLineNumber());

                    System.err.println("Syntax Error: Expected , got" +
                            "\""  + error + "\" (inputs/" + inputFile + ":" + tokens.get(0).getLineNumber() + ")");
                    System.exit(1);
                }
                tokens.remove();
                ExpressionNode right = parseExpression(tokens,myStack);
                if(!tokens.get(0).getToken().equals(")")){
                    String error = makeErrorString(tokens.get(0).getLineNumber());

                    System.err.println("Syntax Error: Missing Character: Expected ( in concat, " +
                            "\""  + error + "\" (inputs/" + inputFile + ":" + tokens.get(0).getLineNumber() + ")");
                    System.exit(1); // missing closing parenthesis
                }
                tokens.remove();
                return new S_expr(function,left,right);
            }if(!isInteger(tokens.get(0).getToken()) && !isDouble(tokens.get(0).getToken())){
                myStack.push("Id");
                ExpressionNode left = parseExpression(tokens,myStack);
                return new S_expr(null,left,null);
            }
            System.err.println("Invalid string statement at line # " + tokens.get(0).getLineNumber());
            System.exit(1);
            return null; // will add more stuff
        }
        if(myStack.peek().equals("Expr")){
            myStack.pop();

            if(tokens.get(0).isNegative()){
                isNegative = true;
                tokens.remove();
            }
            if(isDouble(tokens.get(0).getToken())){
                myStack.push("D_expr");
                return new Expr(parseExpression(tokens,myStack));// for now its set to null
            }else if(isInteger(tokens.get(0).getToken())){
                myStack.push("I_expr");
                return new Expr(parseExpression(tokens,myStack));
            }
            else if(tokens.get(0).getToken().equals("concat")){
                myStack.push("S_expr"); // for now
                return new Expr(parseExpression(tokens,myStack));
            }else if(tokens.get(0).getToken().equals("charAt")){
                myStack.push("S_expr");
                return new Expr(parseExpression(tokens,myStack));
            }else if(tokens.get(0).getToken().charAt(0) == '"'){
                if(varTypes.containsKey(currentVar)){
                    if(!varTypes.get(currentVar).equals("String")){
                        Token noNull = tokens.get(0);
                        String error = makeErrorString(noNull.getLineNumber());
                        System.err.println("Syntax Error: Invalid type in re-assignment: Expected Integer got String;  " +
                                "\""  + error + "\" (inputs/" + inputFile + ":" + noNull.getLineNumber() + ")");
                        System.exit(1);

                    }
                }
                currentVar = null;
                myStack.push("S_expr");
                return new Expr(parseExpression(tokens,myStack));
            }else{ //its an id then
                if(isOperator(tokens.get(1).getToken())){
                    myStack.push("VariableOp");
                    return new Expr(parseExpression(tokens,myStack));
                }
                myStack.push("Id");
                return new Expr(tokens.get(0).getToken(),parseExpression(tokens,myStack));
            }
        }
        System.err.println("Invalid expression at line # " + tokens.get(0).getLineNumber());
        System.exit(1);
        return null;
    }

    public Map<String, Object> getSymTab() {
        return symTab;
    }

    public Program getRoot(){
        return this.program;
    }
    public void getNode(){

    }

}

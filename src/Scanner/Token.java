/**
 * Author: Ramses Geronimo
 *
 * File Name: Token
 * Description: each token has a string token that represents it, and the line number that
 * token was read from (line number used in error messages).
 */
package Scanner;



public class Token {

    private String token;
    private int lineNumber;
    private boolean isNegative;
    private boolean ifHasElse;


    public Token(String token, int lineNumber){
        this.token = token;
        this.lineNumber = lineNumber;
        this.isNegative = false;
        this.ifHasElse = false;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public String getToken(){
        return this.token;
    }

    @Override
    public String toString(){
        return this.token;
    }


    public boolean isNegative() {
        return isNegative;
    }

    public void setNegative(boolean negative) {
        isNegative = negative;
    }

    public boolean isIfHasElse() {
        return ifHasElse;
    }

    public void setIfHasElse(boolean ifHasElse) {
        this.ifHasElse = ifHasElse;
    }
}

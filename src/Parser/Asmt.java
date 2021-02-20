package Parser;

import Scanner.Token;

import java.util.Map;

public class Asmt implements ActionNode {

    /**
     * sa
     */
    private String identifier;
    private static final String nodeRep = "Asmt";
    private static final String endStatement = ";";
    private ExpressionNode rhs;
    private ExpressionNode variable;



    @Override
    public void display() {

    }

    public Asmt(String identifier, ExpressionNode variable, ExpressionNode rhs){
        this.identifier = identifier;
        this.variable = variable;
        this.rhs = rhs;
    }

    /**
     *
     * @param symTab
     */
    @Override
    public void execute(Map<String, Object> symTab) {
        symTab.put( (String)variable.evaluate(symTab), rhs.evaluate(symTab) );
    }

    public String getNodeRep(){
        return nodeRep;
    }

    public String getIdentifier() {
        return identifier;
    }

    public ExpressionNode getRhs() {
        return rhs;
    }

    public String getVariable() {
        return variable.getValue();
    }
}

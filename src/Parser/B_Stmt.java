package Parser;

import java.util.Map;

public class B_Stmt implements ActionNode {
    private ExpressionNode expressionNode;
    private ActionNode actionNode;

    public B_Stmt(ExpressionNode expressionNode){
        this.expressionNode = expressionNode;
    }

    public B_Stmt(ActionNode actionNode){
        this.actionNode = actionNode;
    }


    @Override
    public void display() {

    }


    @Override
    public void execute(Map<String, Object> symTab) {
        if( expressionNode != null ) {
            expressionNode.evaluate(symTab);
        }
        else if( actionNode != null ){
            actionNode.execute(symTab);
        }
    }

    public ActionNode getActionNode() {
        return actionNode;
    }

    public ExpressionNode getExpressionNode() {
        return expressionNode;
    }
}

package Parser;

import Scanner.Token;

import java.util.LinkedList;
import java.util.Map;

public class Program implements ActionNode {


    private ActionNode leftChild;
    private ActionNode rightChild;

    public Program (){
        leftChild = null;
        rightChild = null;

    }

    @Override
    public void display() { }

    @Override
    public void execute(Map<String, Object> symTab) {
        leftChild.execute(symTab);
        rightChild.execute(symTab);
    }

    public ActionNode getLeftChild() {
        return leftChild;
    }

    public ActionNode getRightChild() {
        return rightChild;
    }

    public void setRightChild(ActionNode rightChild) {
        this.rightChild = rightChild;
    }

    public void setLeftChild(ActionNode leftChild) {
        this.leftChild = leftChild;
    }
}

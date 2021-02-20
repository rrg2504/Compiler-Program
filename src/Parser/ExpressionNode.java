package Parser;

import Parser.Node;
import java.util.Map;

/**
 * These Nodes compute the values that are needed inorder to make the
 * variable/assingment for the symbol table.
 */
public interface ExpressionNode extends Node{
    Object evaluate(Map<String, Object> symTab);
    String getValue();
}

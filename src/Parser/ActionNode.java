package Parser;
/**
 * Author: Ramses Geronimo
 *
 */

import Parser.Node;

import java.util.Map;

/**
 * This Interface represent nodes that do a certain action on their children. For instance,
 * print will only print out the values of its children.
 */
public interface ActionNode extends Node {

    void execute(Map<String,Object>symTab);
}

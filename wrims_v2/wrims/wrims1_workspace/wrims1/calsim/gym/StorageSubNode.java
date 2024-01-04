package calsim.gym;
/**
 * Represents a node in the system. A node
 * is a junction into which arcs abut.
 * @author Nicky Sandhu
 */
public class StorageSubNode extends Node{
    private int _level;
    /**
     * a node with a given id
     */
    public StorageSubNode(int id, int level){
	super(id);
	_level = level;
    }
    /**
     * gets name 
     */
    public String getName(){
	return "NODE: " + getName() + "_" + getLevel();
    }
    /**
     * gets the integer id for this node.
     */
    public int getLevel(){
	return _level;
    }
}

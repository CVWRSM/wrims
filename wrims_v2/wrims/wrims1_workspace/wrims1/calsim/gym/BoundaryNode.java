package calsim.gym;
/**
 * A boundary node. Usually only there is but
 * one boundary node for the entire system.
 *
 * @author Nicky Sandhu
 */
public class BoundaryNode extends Node{
    /**
     * construct a boundary node with given id.
     */
    public BoundaryNode(int id){
	super(new Integer(id).toString());
    }
}

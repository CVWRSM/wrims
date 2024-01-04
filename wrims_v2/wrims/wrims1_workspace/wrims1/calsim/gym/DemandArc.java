package calsim.gym;
/**
 * A demand arc
 *
 * @author Nicky Sandhu
 */
public class DemandArc extends Arc{
    /**
     *
     */
    public DemandArc(String name, Node upNode, Node downNode){
	super(name, upNode, downNode);
    }
    /**
     * type abbrev impl.
     */
    public String getTypeAbbrev(){
	return "D";
    }
    /**
     * type name impl
     */
    public String getTypeName(){
	return "DEMAND-ARC";
    }
}

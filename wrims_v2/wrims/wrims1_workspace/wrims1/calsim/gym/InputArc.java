package calsim.gym;
/**
 * An input arc
 *
 * @author Nicky Sandhu
 */
public class InputArc extends Arc{
  /**
    *
    */
  public InputArc(String name, Node upNode, Node downNode){
    super(name, upNode, downNode);
  }
  /**
    * type abbrev impl.
    */
  public String getTypeAbbrev(){
    return "I";
  }
  /**
    * type name impl
    */
  public String getTypeName(){
    return "INPUT-ARC";
  }
}

package calsim.gym;
/**
 * An return  arc
 *
 * @author Nicky Sandhu
 */
public class ReturnArc extends Arc{
  /**
    *
    */
  public ReturnArc(String name, Node upNode, Node downNode){
    super(name, upNode, downNode);
    _factor = 0.0;
  }
  /**
    * type abbrev impl.
    */
  public String getTypeAbbrev(){
    return "R";
  }
  /**
    * type name impl
    */
  public String getTypeName(){
    return "RETURN-ARC";
  }
  /**
    * gets the return flow factor between 0.0 - 1.0
    */
  public double getReturnFlowFactor(){
    return _factor;
  }
  /**
    * gets the return flow factor between 0.0 - 1.0
    */
  public void setReturnFlowFactor(double d){
    _factor = d;
  }
  /* the return flow factor*/
  private double _factor;
}

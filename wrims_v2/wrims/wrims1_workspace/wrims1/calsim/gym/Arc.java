package calsim.gym;
/**
 * An arc in the schematic. It typically carries flow
 * in a certain direction. In other words it is a
 * directed arc
 * @see Node
 * @author Nicky Sandhu
 */
public abstract class Arc implements java.io.Serializable{
  private Node _upNode, _downNode;
  private String _name;
  private int _priority;
  /**
   * creates an empty arc
   */
  public Arc(String name){
    this(name, null,null);
  }
  /**
   * creates an arc between two existing nodes.
   * If no down node exists then the arc is assumed
   * to have a universal boundary node that is assumed
   * to exist around the network.
   */
  public Arc(String name, Node upNode, Node downNode){
    setName(name);
    setUpstreamNode(upNode);
    setDownstreamNode(downNode);
    _priority = 0; // equivalent to no priority
    // should i check for both nodes being null??
  }
  /**
   *
   */
  public void setName(String name){
    _name = name;
  }
  /**
   * set upstream node
   */
  public void setUpstreamNode(Node n){
    if ( n != null )
      _upNode = n;
    else
      _upNode = GymUtils.UNIVERSAL_BOUNDARY_NODE;
  }
  /**
   * set downstream node
   */
  public void setDownstreamNode(Node n){
    if ( n != null)
      _downNode = n;
    else 
      _downNode = GymUtils.UNIVERSAL_BOUNDARY_NODE;
  }
  /**
   * get upstream node ( back of arrow )
   */
  public Node getUpstreamNode(){
    return _upNode;
  }
  /**
   * get downstream node ( arrow point )
   */
  public Node getDownstreamNode(){
    return _downNode;
  }
  /**
   * set the priority level associated with this arc
   */
  public void setPriority(int p){
    _priority = p;
  }
  /**
   * get priority level associated with this arc
   */
  public int getPriority(){
    return _priority;
  }
  /**
    * returns the variable represeting the weight of this arc in WRESL 
    */
  public String getWeightVariable(){
    return "wt_" + getName();
  }
  /**
   * get type name abbreviation
   */
  public abstract String getTypeAbbrev();
  /**
   * get name of the arc type
   */
  public abstract String getTypeName();
  /**
   * @return a string containing the representative
   * name of the arc.
   */
  public String getName(){
    return _name;
  }
  /**
   *  @return the arcs name consisting of its type name 
   * followed by the up node id
   */
  public String toString(){
    return getName();
  }
  /**
   * dumps information about itself to the standard output
   */
  public void dump(){
    System.out.println("Arc : " + getTypeName());
    System.out.println("Name: " + getName());
    System.out.println("Up node: " + _upNode.getName());
    System.out.println("Down node: " + _downNode.getName());
    System.out.println();
  }
}

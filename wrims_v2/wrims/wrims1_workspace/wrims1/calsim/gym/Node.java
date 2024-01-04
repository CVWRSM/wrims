package calsim.gym;
import java.util.Vector;
/**
 * Represents a node in the system. A node
 * is a junction into which arcs abut.
 * @author Nicky Sandhu
 */
public class Node implements java.io.Serializable{
  private Arc [] _arcs;
  private Vector _varcs;
  private int _id;
  private String _name;
  private boolean _hasStorage;
  private boolean _modified;
  public float x,y;
  
  public Node(int id) {
	  
  }
  
  /**
    * a node with a given id
    */
  public Node(String name){
    _name = name;
    try {
      _id = new Integer(_name).intValue();
    }catch(Exception e){
      _id = -1;
    }
    _hasStorage = false;
    _varcs = new Vector();
  }
  /**
    * gets name 
    */
  public String getName(){
    return _name;
  }
  /**
    * gets the integer id for this node.
    */
  public int getId(){
    return _id;
  }
  /**
    * @return an array of arcs that have this node
    * as either their upnode or downnode
    */
  public Arc [] getConnectingArcs(){
    if ( _modified ) {
      if ( _varcs.size() == 0 ) 
	_arcs = null;
      else {
	_arcs = new Arc[_varcs.size()];
	_varcs.copyInto(_arcs);
      }
      _modified = false;
    }
    return _arcs;
  }
  /**
    * @return true if the node has storage
    */
  public boolean hasStorage(){
    return _hasStorage;
  }
  /**
    * sets it to true if the node is also a storage node
    */
  public void setHasStorage(boolean b){
    _hasStorage = b;
  }
  /**
    * a string representation of this node
    */
  public String toString(){
    return getName();
  }
  /**
    * add arc to this node
    */
  public void addArc(Arc arc){
    _varcs.addElement(arc);
    _modified = true;
  }
  /**
    * removes the given arc from this node
    */
  public void removeArc(Arc arc){
    if ( arc == null || _varcs.size() == 0 ) return;
    if ( _varcs.contains(arc) ){
      _varcs.removeElement(arc);
      _modified = true;
    }else {
      return;
    }
  }
}

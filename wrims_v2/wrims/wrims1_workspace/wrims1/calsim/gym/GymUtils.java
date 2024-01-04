package calsim.gym;
import java.util.*;
import java.io.*;
/**
  * A set of utility functions for this package
  *
  * @author Nicky Sandhu
  * @version $Id: GymUtils.java,v 1.1.1.1.2.13 1999/03/12 00:02:31 nsandhu Exp $
  */
public class GymUtils{
  public static boolean DEBUG = false;
  /**
   * represents the boundary of the entire network. 
   */
  public static Node UNIVERSAL_BOUNDARY_NODE = new BoundaryNode(0);
  /**
    * represents the maximum priority of an arc. 
    */
  public static int MAX_PRIORITY = 5000;
  /**
    * represents the priority given to the flood arcs and is the minimum
    * priority within the system.
    */
  public static int MIN_PRIORITY = -100;
  /**
   * @return the just upstream nodes of node n in an
   * array or null if no upstream nodes exist ( ie. if the
   * only upstream node is a boundary node )
   */
  public static Node [] getJustUpstreamNodes(Node n, Node [] boundaryNodes){
    if ( isInArray(n,boundaryNodes) ) return null;
    Arc [] arcs = n.getConnectingArcs();
    Node [] upnodes = new Node[arcs.length];
    int count=0;
    for(int i=0;  i < arcs.length; i++ ){
      if ( arcs[i].getDownstreamNode() == n ){
	Node un1 = arcs[i].getUpstreamNode();
	if ( ! isInArray(un1,boundaryNodes)) { // no boundary nodes allowed
	  upnodes[count] = arcs[i].getUpstreamNode();
	  count++;
	}
      }
    }
    if ( count == 0 ) 
      return null;
    else {
      // only boundary node upstream
      //      if ( isInArray(upnodes[0],boundaryNodes)) return null;
      Node [] tmpNodes = new Node[count];
      System.arraycopy(upnodes,0,tmpNodes,0,count);
      return tmpNodes;
    }
  }
  /**
   * @return the just upstream nodes of node n in an
   * array or null if no upstream nodes exist ( ie. if the
   * only downstream node is a boundary node )
   */
  public static Node [] getJustDownstreamNodes(Node n, Node [] boundaryNodes){
    if ( isInArray(n,boundaryNodes) ) return null;
    Arc [] arcs = n.getConnectingArcs();
    Node [] downnodes = new Node[arcs.length];
    int count=0;
    for(int i=0;  i < arcs.length; i++ ){
      if ( arcs[i].getUpstreamNode() == n ){
	Node dn1 = arcs[i].getDownstreamNode();
	if ( ! isInArray(dn1,boundaryNodes) ) { // no boundary nodes allowed
	  downnodes[count] = arcs[i].getDownstreamNode();
	  count++;
	}
      }
    }
    if ( count == 0 ) 
      return null;
    else {
      Node [] tmpNodes = new Node[count];
      System.arraycopy(downnodes,0,tmpNodes,0,count);
      return tmpNodes;
    }
  }
  /**
   * @return the just upstream nodes of node n in an
   * array or null if no upstream nodes exist ( ie. if the
   * only upstream node is a boundary node )
   */
  public static Node [] getJustUpstreamNodes(Node n){
    return getJustUpstreamNodes(n,new Node[]{UNIVERSAL_BOUNDARY_NODE});
  }
  /**
   * @return the just upstream nodes of node n in an
   * array or null if no upstream nodes exist ( ie. if the
   * only downstream node is a boundary node )
   */
  public static Node [] getJustDownstreamNodes(Node n){
    return getJustDownstreamNodes(n,new Node[]{UNIVERSAL_BOUNDARY_NODE});
  }
  /**
   * Note: All these methods will currently blow up if
   * there is a loop in the network. I will add a check for
   * loop later.
   * @return all the upstream nodes of node n or null
   * if no upstream nodes exist
   */
  public static Node [] getUpstreamNodes(Node n){
    Node [] nodes = getJustUpstreamNodes(n);
    if ( nodes == null ) return null;
    Vector vnodes = new Vector(nodes.length*5,15); // guess a size
    // add just upstream nodes
    for(int j=0; j < nodes.length; j++){
      vnodes.addElement(nodes[j]);
    }
    // get upstream nodes list from each of the just upstream nodes
    for(int i=0; i < nodes.length; i++){
      Node n1 = nodes[i];
      Node [] nodes1 = getUpstreamNodes(n1);
      if (nodes1 == null) continue;
      for(int j=0; j < nodes1.length; j++){
	// ?? does this stop the loop here for infinite recursion
	// should check here for loop or infinite recursion will occur
	if ( vnodes.contains(nodes1[j])) continue;
	vnodes.addElement(nodes1[j]);
      }
    }
    Node [] anodes = new Node[vnodes.size()];
    vnodes.copyInto(anodes);
    return anodes;
  }
  /**
   * @return the downstream nodes of node n or null
   * if no downstream nodes exist
   */
  public static Node [] getDownstreamNodes(Node n){
    Node [] nodes = getJustDownstreamNodes(n);
    if ( nodes == null ) return null;
    Vector vnodes = new Vector(nodes.length*5,15); // guess a size
    // add just Downstream nodes
    for(int j=0; j < nodes.length; j++){
      vnodes.addElement(nodes[j]);
    }
    // get Downstream nodes list from each of the just Downstream nodes
    for(int i=0; i < nodes.length; i++){
      Node n1 = nodes[i];
      Node [] nodes1 = getDownstreamNodes(n1);
      if (nodes1 == null) continue;
      for(int j=0; j < nodes1.length; j++){
	// should check here for loop or infinite recursion will occur
	if ( vnodes.contains(nodes1[j]) ) continue;
	vnodes.addElement(nodes1[j]);
      }
    }
    Node [] anodes = new Node[vnodes.size()];
    vnodes.copyInto(anodes);
    return anodes;
  }
  /**
   * get just upstream arcs of given node. These are all the arcs
   * that have the given node as their downstream node
   * @return an array of upstream arcs or null if none exist
   */
  public static Arc [] getJustUpstreamArcs(Node n){
    Arc [] arcs = n.getConnectingArcs();
    if ( arcs == null ) return null;
    Arc [] narcs = new Arc[arcs.length];
    int count=0;
    for(int i=0;  i < arcs.length; i++ ){
      if ( arcs[i].getDownstreamNode() == n ){
	narcs[count] = arcs[i];
	count++;
      }
    }
    if ( count == 0 ) return null;
    else {
      Arc [] tarcs = new Arc[count];
      System.arraycopy(narcs,0,tarcs,0,count);
      return tarcs;
    }
  }
  /**
   * get just downstream arcs of given node. These are all the arcs
   * that have the given node as their downstream node
   * @return an array of downstream arcs or null if none exist
   */
  public static Arc [] getJustDownstreamArcs(Node n){
    Arc [] arcs = n.getConnectingArcs();
    if ( arcs == null ) return null;
    Arc [] narcs = new Arc[arcs.length];
    int count=0;
    for(int i=0;  i < arcs.length; i++ ){
      if ( arcs[i].getUpstreamNode() == n ){
	narcs[count] = arcs[i];
	count++;
      }
    }
    if ( count == 0 ) return null;
    else {
      Arc [] tarcs = new Arc[count];
      System.arraycopy(narcs,0,tarcs,0,count);
      return tarcs;
    }
  }
  /**
   * Upstream arcs of given node are all arcs that connect given
   * node with its upstream nodes
   * @return an array of upstream arcs of node n or
   * null if none exist
   */
  public static Arc [] getUpstreamArcs(Arc arc){
    return getUpstreamArcs(arc.getUpstreamNode());
  }
  /**
   * Upstream arcs of given node are all arcs that connect given
   * node with its upstream nodes
   * @return an array of upstream arcs of node n or
   * null if none exist
   */
  public static Arc [] getUpstreamArcs(Node n){
    Arc [] arcs = getJustUpstreamArcs(n);
    if ( arcs == null ) return null;
    Vector varcs = new Vector(arcs.length*5,15); // guess a size
    // add just upstream arcs
    for(int j=0; j < arcs.length; j++){
      varcs.addElement(arcs[j]);
    }
    // get upstream arcs list from each of the just upstream arcs
    for(int i=0; i < arcs.length; i++){
      Node n1 = arcs[i].getUpstreamNode();
      Arc [] arcs1 = getUpstreamArcs(n1);
      if (arcs1 == null) continue;
      for(int j=0; j < arcs1.length; j++){
	// should check here for loop or infinite recursion will occur
	if ( varcs.contains(arcs1[j]) ) continue;
	varcs.addElement(arcs1[j]); 
      }
    }
    Arc [] aarcs = new Arc[varcs.size()];
    varcs.copyInto(aarcs);
    return aarcs;
  }
  /**
   * @return an array of downstream arcs of node n or
   * null if none exist
   */
  public static Arc [] getDownstreamArcs(Arc arc){
    return getDownstreamArcs(arc.getDownstreamNode());
  }
  /**
   * @return an array of downstream arcs of node n or
   * null if none exist
   */
  public static Arc [] getDownstreamArcs(Node n){
    Arc [] arcs = getJustDownstreamArcs(n);
    if ( arcs == null ) return null;
    Vector varcs = new Vector(arcs.length*5,15); // guess a size
    // add just downstream arcs
    for(int j=0; j < arcs.length; j++){
      varcs.addElement(arcs[j]);
    }
    // get downstream arcs list from each of the just downstream arcs
    for(int i=0; i < arcs.length; i++){
      Node n1 = arcs[i].getDownstreamNode();
      Arc [] arcs1 = getDownstreamArcs(n1);
      if (arcs1 == null) continue;
      for(int j=0; j < arcs1.length; j++){
	// should check here for loop or infinite recursion will occur
	if ( varcs.contains(arcs1[j]) ) continue;
	varcs.addElement(arcs1[j]); 
      }
    }
    Arc [] aarcs = new Arc[varcs.size()];
    varcs.copyInto(aarcs);
    return aarcs;
  }
  /**
   * get all the nodes upstream of this arc including the
   * just upstream node of this arc
   */
  public static Node [] getUpstreamNodes(Arc arc){
    Node unode = arc.getUpstreamNode();
    Node [] upNodes = getUpstreamNodes(unode);
    if ( upNodes == null ){
      if ( unode == UNIVERSAL_BOUNDARY_NODE )
	upNodes = null;
      else
	upNodes = new Node[] {unode};
    } else {
      Node [] tmpNodes = new Node[upNodes.length+1];
      System.arraycopy(upNodes,0,tmpNodes,0,upNodes.length);
      tmpNodes[tmpNodes.length-1]=unode;
      upNodes= tmpNodes;
    }
    return upNodes;
  }
  /**
   * get all the nodes downstream of this arc including the
   * just downstream node of this arc
   */
  public static Node [] getDownstreamNodes(Arc arc){
    Node dnode = arc.getDownstreamNode();
    Node [] dpNodes = getDownstreamNodes(dnode);
    if ( dpNodes == null ){
      if ( dnode == UNIVERSAL_BOUNDARY_NODE )
	dpNodes = null;
      else
	dpNodes = new Node[] {dnode};
    } else {
      Node [] tmpNodes = new Node[dpNodes.length+1];
      System.arraycopy(dpNodes,0,tmpNodes,0,dpNodes.length);
      tmpNodes[tmpNodes.length-1]=dnode;
      dpNodes= tmpNodes;
    }
    return dpNodes;
  }
  /**
   * get all nodes that can be reached by traversing upstream or downstream from this node.
   * While traversing no change of direction is allowed, e.g. while traversing upstream only
   * further upstream nodes can be accessed and no downstream traversing is allowed
   */
  public static Node [] getAllNodes(Arc arc){
    // all the nodes that can be obtained from traversing back
    // and forth along paths from this arc
    Node [] allNodes = null;
    // get upstream nodes and also include the arc's upstream node
    Node [] upNodes = getUpstreamNodes(arc);
    // get downstream nodes
    Node [] downNodes = getDownstreamNodes(arc);
    // if all null return null
    if ( upNodes == null && downNodes == null){
      return null;
    } 
    // copy all nodes into one big array
    if ( upNodes == null || downNodes == null ){
      if ( upNodes == null ) 
	allNodes = downNodes;
      else
	allNodes = upNodes;
    } else{
      allNodes = new Node[upNodes.length+downNodes.length];
      System.arraycopy(upNodes,0,allNodes,0,upNodes.length);
      System.arraycopy(downNodes,0,allNodes,upNodes.length,downNodes.length);
    }
    return allNodes;
  }
  /**
   * gets all arcs that can be reached by uni-directional traversing from this arc.
   */
  public static Arc [] getAllArcs(Arc arc){
    // all the arcs that can be obtained from traversing back
    // and forth along paths from this arc
    Arc [] allArcs = null;
    // get upstream arcs and also include the arc's upstream arc
    Arc [] upArcs = getUpstreamArcs(arc);
    // get downstream arcs
    Arc [] downArcs = getDownstreamArcs(arc);
    // if all null return null
    if ( upArcs == null && downArcs == null){
      return new Arc[] {arc};
    } 
    // copy all arcs into one big array
    if ( upArcs == null || downArcs == null ){
      if ( upArcs == null )
	allArcs = downArcs;
      else
	allArcs = upArcs;
      Arc [] tmpArcs = new Arc[allArcs.length+1];
      tmpArcs[0] = arc;
      System.arraycopy(allArcs,0,tmpArcs,1,allArcs.length);
      allArcs = tmpArcs;
    } else{
      allArcs = new Arc[upArcs.length+downArcs.length+1];
      allArcs[0] = arc;
      System.arraycopy(upArcs,0,allArcs,1,upArcs.length);
      System.arraycopy(downArcs,0,allArcs,upArcs.length+1,downArcs.length);
    }
    return allArcs;
  }
  /**
   * this method calculates all the competing paths which
   * would could be taken so as to avoid this arc. 
   * @return null if the upstream node of this arc is
   * a boundary node.
   */
  public static Path [] getCompetingPaths(Arc arc){
    // all the nodes that can be obtained from traversing back
    // and forth along paths from this arc
    // get upstream nodes and also include the arc's upstream node
    Node [] upNodes = getUpstreamNodes(arc);
    // if upstream node is a boundary node return null;
    if ( upNodes == null ){
      return null;
    }
    // get all nodes along the path of this arc
    Arc [] allArcs = getAllArcs(arc);
    // get all downstream nodes
    Node [] downNodes = getDownstreamNodes(arc);
    // initialize a container for the competing paths
    Vector compPaths = new Vector();
    // for each upstream node do the following...
    for(int i=0; i < upNodes.length; i++){
      Node unode = upNodes[i];
      if ( DEBUG ) System.out.println("Searching for competing paths from node " + unode.getName());
      // get all paths rooted at the upstream node not including
      // a just downstream node that belongs in the allNodes list
      // at a node in the upstream nodes list
      //      Path [] altPaths = getCompetingPaths(unode,allArcs);
      Path mainPath = getMainPath(unode,arc,allArcs);
      Path [] altPaths = getCompetingBranches(unode,downNodes,allArcs);
      // if alternate paths are found store them in the vector
      if ( altPaths != null ) {
	for(int j=0; j < altPaths.length; j++){
	  altPaths[j].mainPath = mainPath;
	  compPaths.addElement(altPaths[j]);
	}
      }
      if ( altPaths == null ){
	if (DEBUG) System.out.println("No competing paths rooted at node " + unode.getName());
      }
    }
    // finally return these networks in an array
    Path [] nets = new Path[compPaths.size()];
    compPaths.copyInto(nets);
    return nets;
  }
  /**
   * gets the competing branches from this root node terminating at the given boundary nodes
   */
  public static Path [] getCompetingBranches(Node rootNode, 
					     Node [] boundaryNodes,
					     Arc [] allArcs){
    Arc [] darcs = getJustDownstreamArcs(rootNode);
    if ( darcs == null ) return null;
    Vector vpaths = new Vector();
    for(int i=0; i < darcs.length; i++){
      Arc arc = darcs[i];
      if ( isInArray(arc,allArcs) ) continue;
      Path p = new Path();
      p.addArc(arc);
      Path [] tpaths = getBranches(arc.getDownstreamNode(), boundaryNodes, p);
      if ( tpaths != null ){
	for( int j=0; j < tpaths.length; j++){
	  vpaths.addElement(tpaths[j]);
	}
      }
    }
    // finally get all the paths in one array
    if ( vpaths.size() == 0 ) return null;
    Path [] paths = new Path[vpaths.size()];
    vpaths.copyInto(paths);
    return paths;
  }
  /**
   * This method gets all the branches from the rootNode terminating
   * at one of the boundaryNodes and appending onto the given path.
   * @return an array of paths of the various branches that exist from 
   * this root node onwards including the given path and terminating
   * at one of the boundary nodes.
   */
  public static Path [] getBranches(Node rootNode, Node [] boundaryNodes, Path p){
    if ( isInArray(rootNode, boundaryNodes) ) return new Path[] {p};
    Arc [] darcs = getJustDownstreamArcs(rootNode);
    if ( darcs == null ) return new Path []{p};
    Vector paths = new Vector();
    for(int i=0; i < darcs.length; i++){
      Path p2 = p.createClone();
      p2.addArc(darcs[i]);
      Path [] p2branches = getBranches(darcs[i].getDownstreamNode(),
				       boundaryNodes, p2);
      for(int j=0; j < p2branches.length; j++){
	paths.addElement(p2branches[j]);
      }
    }
    Path [] allPaths = new Path[paths.size()];
    paths.copyInto(allPaths);
    return allPaths;
  }
  /**
   * returns the main path to a given arc, rooted at the root node
   * node list and the root node from which to start off the competing
   * paths
   * @return null if no competing path found else return a Network containing
   * the path
   */
  public static Path getMainPath(Node rootNode, Arc mainArc, Arc [] allArcs){
    // get all the downstream arcs of given root node. 
    Arc [] downArcs = getDownstreamArcs(rootNode);
    // ?? excuse me, how is this supposed to happen NOT !!!
    if ( downArcs == null ) return null;
    // keep only the arcs that are in the allArcs array and return it.
    Vector varcs = new Vector();
    for(int i=0; i < downArcs.length; i++){
      Arc arc = downArcs[i];
      if ( isInArray(arc, allArcs) ) {
	varcs.addElement(arc);
      } else {
	continue;
      }
      if ( arc == mainArc ) break; // don't go further downstream than main arc
    }
    // copy into a arc array and return as a path object
    Arc [] arcs = new Arc[varcs.size()];
    varcs.copyInto(arcs);
    Path p = new Path();
    p.arcs = arcs;
    return p;
  }
  /**
    * get the arc between the given upstream and downstream node
    */
  public static Arc getArcBetween(Node upNode, Node downNode){
    Arc [] arcs = upNode.getConnectingArcs();
    for(int i=0; i < arcs.length; i++){
      if ( arcs[i].getDownstreamNode() == downNode )
	return arcs[i];
    }
    return null;
  }
  /**
   * @returns true if obj is found in the obj array provided
   * and false if not found
   */
  public static boolean isInArray(Object obj, Object [] objArray){
    if( obj == null || objArray == null ) return false;
    boolean found = false;
    for(int i=0; i < objArray.length; i++){
      if (obj == objArray[i]) {
	found = true;
	break;
      }
    }
    return found;
  }
  /**
    *
    */
  public static void genWRESL(Network net, PrintWriter pw, PrintWriter pw2) throws IOException{
    // get all the arcs in the network
    Arc [] arcs = net.getAllArcs();
    pw2.println("! need this weighted junk variable to get things rolling");
    pw2.println("! if no weighted variable, Calsim does not make an OBJ");
    pw2.println("define junk { ");
    pw2.println("  STD WEIGHT -10 ");
    pw2.println("}");
    pw2.println("! Declare weights as decision variables");
    // loop over each arc...
    for(int i=0; i < arcs.length; i++){
      Arc arc = arcs[i];
      if ( arc.getPriority() == 0 ) continue; // no priority defined
      // for flood arc and flood storage arc only
      if ( arc instanceof FloodArc ){
	genWRESL((FloodArc) arc,pw,pw2);
	continue;
      }
      // for flood storage only i have already accounted for as above
      if ( isFloodStorageArc(arc.getName()) ){
	continue;
      }
      // generate global wresl statements for putting this weight greater
      // than the weights of any lower priority arc in the system
      genGlobalWRESL(pw,arc,arcs);
      // get the competing paths for the arc ...
      Path [] cpaths = getCompetingPaths(arc);
      // if no competing paths then nothing to really do
      if ( cpaths == null ) continue;
      //
      pw2.println("define " + arc.getWeightVariable() 
		  + " { STD } ! Priority " + arc.getPriority());
      // for all the competing paths generate wresl statements
      pw.println("! Constraints to satisfy water allocation priorities for " + arc);
      Hashtable uniqGoals = genWRESL(cpaths,arc);
      for(Enumeration e = uniqGoals.elements(); e.hasMoreElements();){
	pw.println(e.nextElement());
      }
      pw.println();
    }
    pw.println("! Set up objective function to minimize the differences between weights");
    pw.println("! this will minimize scaling problems ");
    pw.println("/*");
    pw.println("\tSet up objective function to minimize the differences between weights");
    pw.println("*/");
    pw.println("goal objective { ");
    pw.println("\tlhs         "+
	       getHighestPriorityArc(net).getWeightVariable()+ " - " +
	       getLowestPriorityArc(net).getWeightVariable() );
    pw.println("\trhs         0");
    pw.println("\tlhs>rhs     penalty 500");
    pw.println("\tlhs<rhs     penalty 500");
    pw.println("}");
    //
    pw2.println("! Define minimum weight value");
    pw2.println("define e {VALUE 10.0 }");
  }
  /**
    *
    */
  public static void genGlobalWRESL(PrintWriter pw, Arc arc, Arc [] arcs){
    int pr = arc.getPriority();
    for(int i=0; i < arcs.length; i++){
      Arc carc = arcs[i];
      int cpr = carc.getPriority();
      if ( cpr <= 0 ) continue; // don't worry about unmarked or flood arcs
      if ( carc.getPriority() > pr ) { // if of lower priority
	String statement = arc.getWeightVariable() + " > " + carc.getWeightVariable() + " + e";
	pw.println("goal global_priority_"+arc.getName()+"_"+i+ " \t{ "+statement+" }");
      }
    }
  }
  /**
    *
    */
  public static Hashtable genWRESL(Path [] paths, Arc arc) {
    Hashtable ht = new Hashtable();
    if ( paths == null ) return ht;
    for( int i=0; i < paths.length; i++){
      Path path = paths[i];
      if ( path == null ) continue;
      String statement = generateWRESLStatement(path,arc);
      if ( ! ht.containsKey(statement) )
	ht.put(statement,"goal priority_"+arc.getName()+"_"+i+ " \t{ "+statement+" }");
    }
    return ht;
  }
  /**
    * Generate wresl statements for competing path p, for arc 
    * to the print writer stream. 
    * 
    */
  public static String generateWRESLStatement(Path p, Arc arc){
    StringBuffer lhs = new StringBuffer(100);
    StringBuffer rhs = new StringBuffer(100);
    if ( p.arcs == null ) return "";
    int priority = arc.getPriority();
    // using the main path for this competing path construct the lhs of the
    // equation.
    //    Path mp = p.mainPath;
    Path mp = null;
    lhs.append(arc.getWeightVariable());
    if ( mp == null ) {
      // lhs.append(arc.getWeightVariable());
      // System.out.println("mainPath for arc: " + arc + " is " + mp);
    } else {
      // System.out.println("mainPath for arc: " + arc + " is " + mp);
      boolean mpfirst = false;
      for(int i=0; i <  mp.arcs.length; i++){
	Arc arc1 = mp.arcs[i];
	if ( arc1 == arc ) { 
	  mpfirst = false;
	  continue;
	}
	else if ( arc1.getPriority() > priority ){ // if lower priority i.e. higher priority number
	  if ( mpfirst ){
	    mpfirst = false;
	  } else{
	    lhs.append(" + ");
	  }
	  lhs.append(arc1.getWeightVariable());
	}
      }
    }
    // now do the rhs using this competing path
    boolean first = true;
    boolean equalPriorities = false;

    for(int i=0; i < p.arcs.length; i++){
      Arc arc1 = p.arcs[i];
      // if lower priority i.e. higher priority number
      if ( arc1.getPriority() > priority ){
	if ( first ){
	  first = false;
	} else{
	  rhs.append(" + ");
	}
	// check to see if this arc is in competition with any arcs with 
	// equal priority. If it is then drop the +e at the end of the 
	// sum of weights
	// ?? taken out as equal priorities are not included
	// if ( arc1.getPriority() == priority ) equalPriorities = true;
	rhs.append(arc1.getWeightVariable());
      }
    }
    StringBuffer buf = new StringBuffer(200);
    if ( !equalPriorities ){
      buf.append(lhs.toString());
      buf.append(" > ");
      buf.append(rhs.toString());
      if ( !first )
	buf.append(" + e");
      else
	buf.append(" e");
    }else{
      String ls = System.getProperty("line.separator");
      buf.append(ls);
      buf.append("\tlhs\t"); buf.append(lhs.toString()); buf.append(ls);
      buf.append("\trhs\t"); buf.append(rhs.toString()); buf.append(ls);
      buf.append("\tlhs>rhs\tpenalty 1"); buf.append(ls);
      buf.append("\tlhs<rhs\tpenalty 1"); buf.append(ls);
    }
    return buf.toString();
  }
  /**
    * generates wresl statements for flood arc and flood storage arc
    */
  public static void genWRESL(FloodArc arc, PrintWriter pw, 
			      PrintWriter pw2) throws IOException{
    pw.print("goal priority"+arc.getName()+" \t{ ");
    pw.println(arc.getWeightVariable() + " = -e*1000 }");
    pw2.println("define "+arc.getWeightVariable()+ " { lower -10000 upper 0 }");
    Arc [] arcs = getJustDownstreamArcs(arc.getUpstreamNode());
    for(int i=0; i < arcs.length; i++){
      if ( isFloodStorageArc(arcs[i].getName()) ){
	Arc farc = arcs[i];
	pw.print("goal priority" + farc.getName()+"_1 \t{ ");
	pw.println(farc.getWeightVariable() + " = -e*10 }");
	pw2.println("define "+farc.getWeightVariable()+ " { lower -10000 upper 0 }");
	break;
      }
    }
  }
  /**
    * return a path containing only arcs in the path that are
    * equal to or less than the priority of the given arc. If none
    * are found then it returns a null path
    */
  public static Path getLowerPriorityPath(Path p, Arc arc){
    if ( p == null ) return null;
    if ( p.arcs == null ) return null;
    Arc [] arcs = p.arcs;
    Arc [] narcs = new Arc[arcs.length];
    int count = 0;
    int pr = arc.getPriority();
    for(int i=0; i < arcs.length; i++){
      int pr1 = arcs[i].getPriority();
      if ( pr1 == 0 ) continue;
      // ?? lower priority means the priority number is greater than this one
      if ( pr1 <= pr ) continue; 
      narcs[count] = arcs[i];
      count++;
    }
    if ( count == 0 ) return null;
    Arc [] tarcs = new Arc[count];
    System.arraycopy(narcs,0,tarcs,0,count);
    Path path = new Path();
    path.arcs = tarcs;
    return path;
  }
  /**
    * gets the highest priority arc in the network neglecting the 
    * negative priority arcs. The highest priority arc has the lowest
    * priority number
    */
  public static Arc getHighestPriorityArc(Network net){
    Arc [] arcs = net.getAllArcs();
    Arc highArc = null;
    for(int i=0; i < arcs.length; i++){
      int pr = arcs[i].getPriority();
      if ( pr <= 0 ) continue;
      if ( highArc == null ) {
	highArc = arcs[i];
	continue;
      }
      if ( pr < highArc.getPriority() )
	highArc = arcs[i];
    }
    return highArc;
  }
  /**
    * gets the lowest priority arc neglecting the zero or negative priority 
    * numbers. The lowest priority arc has the highest priority number
    */
  public static Arc getLowestPriorityArc(Network net){
    Arc [] arcs = net.getAllArcs();
    Arc lowArc = null;
    for(int i=0; i < arcs.length; i++){
      int pr = arcs[i].getPriority();
      if ( pr <= 0 ) continue;
      if ( lowArc == null ) {
	lowArc = arcs[i];
	continue;
      }
      if ( pr > lowArc.getPriority() )
	lowArc = arcs[i];
    }
    return lowArc;
  }
  /**
    * a class representing a path in a network. It is basically
    * an array of arcs which are connected to each other via nodes.
    *
    * @author Nicky Sandhu
    * @version $Id: GymUtils.java,v 1.1.1.1.2.13 1999/03/12 00:02:31 nsandhu Exp $
    */
  public static class Path{
    /**
      * the arcs comprising this path
      */
    public Arc [] arcs;
    /**
      * the main path with which this path is competing with
      */
    public Path mainPath;
    /**
     * shallow copy of this path
     */
    public Path createClone(){
      Path p = new Path();
      p.mainPath = this.mainPath;
      p.arcs = this.arcs;
      return p;
    }
    /**
     *
     */
    public void addArc(Arc arc){
      if ( arcs == null ){
	arcs = new Arc[1];
	arcs[0] = arc;
      }else{
	Arc [] tarcs = new Arc[arcs.length+1];
	System.arraycopy(arcs,0,tarcs,0,arcs.length);
	tarcs[arcs.length] = arc;
	arcs = tarcs;
      }
    }
    /**
     *
     */
    public void addPath(Path p){
      if ( p.arcs == null ) return;
      if ( arcs == null ){
	arcs = new Arc[p.arcs.length];
	System.arraycopy(p.arcs,0,arcs,0,p.arcs.length);
      }else{
	Arc [] tarcs = new Arc[arcs.length+p.arcs.length];
	System.arraycopy(arcs,0,tarcs,0,arcs.length);
	System.arraycopy(p.arcs,0,tarcs,arcs.length,p.arcs.length);
	arcs = tarcs;
      }
    }
    /**
      *
      */
    public String toString(){
      StringBuffer buf = new StringBuffer(50);
      if ( arcs == null ) return "EMPTY PATH";
      for(int i=0; i < arcs.length; i++){
	buf.append(" --> " );
	buf.append(arcs[i].getName());
      }
      return buf.toString();
    }
  }
  /**
    * tests if arc is a dead storage arc
    */
  public static boolean isDeadStorageArc(String arcName){
    return arcName.indexOf("_1") > 0 ;
  }
  /**
    * tests if arc is a flood storage arc
    */
  public static boolean isFloodStorageArc(String arcName){
    return arcName.indexOf("_5") > 0;
  }
  /**
    * This method generates the WRESL code for calculating weights given 
    * the connectivity file defining the network and the priority file defining 
    * the priorities for various arcs in the network.
    * It outputs a constraints WRESL file which contains the constraints for 
    * WRESL code and a definitions WRESL file which containst the definition for 
    * variables used in the constraints WRESL file.
    */
  public static void genWRESL(String connectivityFile, String priorityFile,
			      String constraintsWRESLFile, String defsWRESLFile)
    throws IOException{
      Network net = Network.read(connectivityFile);
      net.readPriorities(priorityFile);
      PrintWriter cpw = new PrintWriter(new FileWriter(constraintsWRESLFile));
      PrintWriter dpw = new PrintWriter(new FileWriter(defsWRESLFile));
      genWRESL(net,cpw,dpw);
      cpw.close(); dpw.close();
      int wi = constraintsWRESLFile.indexOf(".wresl");
      String gymFileName = null;
      if ( wi >= 0 )
	gymFileName = constraintsWRESLFile.substring(0,wi)
	  + "-gym.wresl";
      else
	gymFileName = constraintsWRESLFile + "-gym.wresl";
      PrintWriter pw = new PrintWriter(new FileWriter(gymFileName));
      pw.println("! The main gym ");
      pw.println("include '" + new File(defsWRESLFile).getName() +"'");
      pw.println("include '" + new File(constraintsWRESLFile).getName() +"'");
      pw.close();
  }
}

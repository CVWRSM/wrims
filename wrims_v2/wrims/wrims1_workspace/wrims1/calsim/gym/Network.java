package calsim.gym;
import java.util.*;
import java.io.*;
/**
 * A network of nodes and arcs. This is a directed
 * graph of nodes and directed arcs
 * @see Node, Arc
 * @author Nicky Sandhu
	Last change:  AM   27 Apr 2000    8:08 am
 */
public class Network implements Serializable{
  public static boolean DEBUG = false;
  private Vector _nodes, _storageNodes;
  private Hashtable _arcsDict, _nodesDict;
  /**
    *
    */
  public Network(){
    _nodes = new Vector();
    _nodesDict = new Hashtable();
    _arcsDict = new Hashtable();
    _storageNodes = new Vector();
  }
  /**
    *
    */
  public void readPriorities(String prfile) throws IOException{
    LineNumberReader reader = 
      new LineNumberReader(new FileReader(prfile));
    String line1 = reader.readLine().toUpperCase().trim();
    if ( line1.indexOf("ARC") < 0 ||
	 line1.indexOf("PRIORITY") < 0 )
      throw new IOException("Illegal Format for priority file " + prfile);
    while(true){
      String line = reader.readLine();
      if ( line == null ) break;
      StringTokenizer st = new StringTokenizer(line,",");
      if ( st.countTokens() < 2 ) {
	System.err.println("Insufficient # of tokens on line: " + line);
	continue;
      }
      String arcName = st.nextToken().trim();
      Arc arc = this.getArc(arcName);
      if ( arc == null ){
	System.err.println("No such arc in network, Arc : " + arcName);
	continue;
      } 
      String prStr = st.nextToken().trim();
      Integer prN = null;
      try {
	prN = new Integer(prStr);
	if ( prN.intValue() <= 0 ) throw new RuntimeException("Incorrect range");
      }catch(Exception e){
	System.err.println("Incorrect priority " + prStr + " , only integers > 0 accepted");
	continue;
      }
      int prval = prN.intValue();
      if ( arc.getPriority() !=0 && arc.getPriority() != prval ){
	System.out.println("Warning: Arc " + arc + 
			   " had been assigned a priority of " + arc.getPriority() + 
			   " is now being assigned a priority of " + prval);
      }
      arc.setPriority(prval);
    }
	 
  }
  /**
    * reads a connectivity matrix
    */
  public static Network read(String ctable) throws IOException{
    Network network = null;
    LineNumberReader reader = 
      new LineNumberReader(new FileReader(ctable));
    boolean foundHeader = false;
    // loop thro' till header is found
    while (true){
      String line = reader.readLine();
      if ( line == null ) break;
      foundHeader =  
	line.indexOf("Node No.") >= 0 &&
	line.indexOf("Arcs IN") >= 0 &&
	line.indexOf("Arcs OUT") >= 0 &&
	line.indexOf("Storage") >=0 ;
      if (DEBUG) {
	System.out.println("foundHeader: " + foundHeader);
	System.out.println("line: " + line);
      }
      if ( foundHeader ) break;
    }
    if ( foundHeader ){
      while ( true ){
	String line = reader.readLine();
	if ( line == null ) break;
	// parse out 1 to 4 columns
	String nodeNo = "", arcsIn = "", arcsOut = "", storageNode = "";
	if (DEBUG) System.out.println("line: " + line);
	// return separator as token also ( String Tokenizer is really dumb on
	// empty strings between tokens)
	StringTokenizer st = new StringTokenizer(line,",",true);
	if  (st.hasMoreTokens()) {
	  String str = st.nextToken();
	  if ( str.equals(",") ) // empty node string
	    nodeNo = "";
	  else {
	    nodeNo= str.trim();
	    try{
	      st.nextToken();
	    }catch(NoSuchElementException e){
	      continue;
	    }
	  }
	}
	if (DEBUG) System.out.println("nodeNo: " + nodeNo);
	if ( st.hasMoreTokens()) {
	  String str = st.nextToken();
	  if ( str.equals(",") ) // empty string
	    arcsIn = "";
	  else {
	    arcsIn= str.trim();
	    try{
	      st.nextToken();
	    }catch(NoSuchElementException e){
	      continue;
	    }
	  }
	}
	if (DEBUG) System.out.println("arcsIn: " + arcsIn);
	if ( st.hasMoreTokens()) {
	  String str = st.nextToken();
	  if ( str.equals(",") ) // empty string
	    arcsOut = "";
	  else {
	    arcsOut= str.trim();
	    try{
	      st.nextToken();
	    }catch(NoSuchElementException e){
	      continue;
	    }
	  }
	}
	if (DEBUG) System.out.println("arcsOut: " + arcsOut);
	if ( st.hasMoreTokens()) {
	  String str = st.nextToken();
	  if ( str.equals(",") ) // empty string
	    storageNode = "";
	  else {
	    storageNode = str.trim();
	  }
	}
	if (DEBUG) System.out.println("storage Node: " + storageNode);
	// if all are empty then end of description
	if ( nodeNo.equals("") &&
	     arcsIn.equals("") &&
	     arcsOut.equals("") &&
	     storageNode.equals("") ) {
	  break;
	}
	// initialize to empty network
	if (network == null ){
	  network = new Network();
	}
	// add nodes, arcs in, out and storage nodes
	// all arc additions are with reference to the last
	// node that was added to the network
	if ( ! nodeNo.equals("") ){
 	  if (DEBUG) System.out.println("Adding node # " + nodeNo);
	  network.addNode(nodeNo);
	}
	if ( ! arcsIn.equals("") ) {
	  network.addArcsIn(arcsIn);
	}
	if ( ! arcsOut.equals("") ){
	  network.addArcsOut(arcsOut);
	}
	if ( ! storageNode.equals("") ){
	  network.addStorageNode(storageNode);
	}
      }
    } else{
      // throw exception later
      System.err.println("No header found in " + ctable);
    }
    return network;
  }
  /**
    *
    */
  public void write(String file) throws IOException, FileNotFoundException {
    PrintWriter writer = new PrintWriter(new FileWriter(file));
    writer.println("CONNECTIVITY TABLE");
    writer.println();
    writer.println("Node No.,Arcs IN,Arcs OUT,Storage,Node Descriptions,,,");
    Node [] nodes = getAllNodes();
    for(int i=0; i < nodes.length; i++){
      Node n1 = nodes[i];
      if ( n1 == null ) continue;
      Arc [] uarcs = GymUtils.getJustUpstreamArcs(n1);
      Arc [] darcs = GymUtils.getJustDownstreamArcs(n1);
      int lu = (uarcs == null ? 0 : uarcs.length);
      int ld = (darcs == null ? 0 : darcs.length);
      int ui = 0, di = 0;
      String gap = "\t, ";
      boolean firstLine = true;
      while ( ui < lu || di < ld ){
	if ( firstLine ) 
	  writer.print(n1.getName()+gap);
	else
	  writer.print(gap);
	// arcs in
	if( ui < lu ) 
	  writer.print(uarcs[ui].getName()+gap);
	else
	  writer.print(gap);
	// arcs out
	if( di < ld )
	  writer.print(darcs[di].getName()+gap);
	else 
	  writer.print(gap);
	// if storage then S + node id
	if ( firstLine ){
	  if ( n1.hasStorage() ) 
	    writer.print("S"+n1.getName()+gap);
	} else {
	  writer.print(gap);
	}
	writer.println();
	//
	firstLine = false;
	ui++;
	di++;
      }
    }
    writer.close();
  }
  /**
    * adds a node to the network
    */
  public void addNode(String nodeNo){
    Node node = new Node(nodeNo);
    _nodes.addElement(node);
    _nodesDict.put(nodeNo.trim(),node);
  }
  /**
    * adds a node to the network
    */
  public void addNode(Node node){
    _nodes.addElement(node);
    _nodesDict.put(node.getName(),node);
  }
  /**
    * add incoming arcs to last added node
    */
  public void addArcsIn(String arcsIn){
    // look up last node added to network
    Node lastNode = (Node) _nodes.lastElement();
    // look up arc by name in dictionary
    Arc arc = (Arc) _arcsDict.get(arcsIn);
    // create a new arc if search turned up empty
    if ( arc == null ){
      if ( arcsIn.startsWith("I") ){
	arc = new InputArc(arcsIn, null, null);
	_arcsDict.put(arcsIn,arc);
      } else if (arcsIn.startsWith("C")){
	arc = new ChannelArc(arcsIn, null,null);
	_arcsDict.put(arcsIn,arc);
      } else if ( arcsIn.startsWith("D") ){
	arc = new DemandArc(arcsIn, null,null);
	_arcsDict.put(arcsIn,arc);
      } else if ( arcsIn.startsWith("R") ){
	arc = new ReturnArc(arcsIn, null,null);
	_arcsDict.put(arcsIn,arc);
      } else if (arcsIn.startsWith("G")){
	arc = new ChannelArc(arcsIn, null,null);
	_arcsDict.put(arcsIn,arc);
      } else {
	// ignore, maybe print message?
      }
    }
    arc.setDownstreamNode(lastNode);
    lastNode.addArc(arc);
  }
  /**
    * add outgoing arcs from last added node
    */ 
  public void addArcsOut(String arcsOut){
    // look up last node added to network
    Node lastNode = (Node) _nodes.lastElement();
    // look up arc by name in dictionary
    Arc arc = (Arc) _arcsDict.get(arcsOut);
    // create a new arc if search turned up empty
    if ( arc == null ){
      // is there ever an input arc as outgoing arc?
      if ( arcsOut.startsWith("I") ){
	arc = new InputArc(arcsOut, null, null);
	_arcsDict.put(arcsOut,arc);
      } else if (arcsOut.startsWith("C")){
	arc = new ChannelArc(arcsOut, null, null);
	_arcsDict.put(arcsOut,arc);
      } else if ( arcsOut.startsWith("D") ){
	arc = new DemandArc(arcsOut, null, null);
	_arcsDict.put(arcsOut,arc);
      } else if ( arcsOut.startsWith("R") ){
	arc = new ReturnArc(arcsOut, null, null);
	_arcsDict.put(arcsOut,arc);
      } else if ( arcsOut.startsWith("S")){
	arc = new StorageArc(arcsOut, null, null);
	_arcsDict.put(arcsOut,arc);
      } else {
	arc = new FloodArc(arcsOut, null, null);
	_arcsDict.put(arcsOut,arc);
      }
    }
    arc.setUpstreamNode(lastNode);
    lastNode.addArc(arc);
  }
  /**
    * add storage node. This means it would add
    * level number of nodes with an arc between
    * each of them 
    */
  public void addStorageNode(String storageNode){
    // a special case (neglected for now)
    _storageNodes.addElement(_nodes.lastElement());
    Node n = (Node) _nodes.lastElement();
    n.setHasStorage(true);
    // add n number of arcs including an extra one for flooding
    for(int i=0; i < NUM_STORAGE_LEVELS; i++){
      addArcsOut("S"+n.getName()+"_"+(i+1));
    }
    addArcsOut("F"+n.getName());
    //
  }
  private static int NUM_STORAGE_LEVELS=5;
  /**
    * the number of nodes in the system
    */
  public int getNumberOfNodes(){
    return _nodes.size();
  }
  /**
    * the number of arcs in the system
    */
  public int getNumberOfArcs(){
    return _arcsDict.size();
  }
  /**
    * the node with the given integer id
    */
  public Node getNode(int i){
    return (Node) _nodesDict.get(new Integer(i).toString());
  }
  /**
    * the node with the given identifier
    */
  public Node getNode(String str){
    return (Node) _nodesDict.get(str.trim());
  }
  /**
    * the arc with the given identifier
    */
  public Arc getArc(String str){
    return (Arc) _arcsDict.get(str);
  }
  /**
    * remove node only if no arc is connected to it.
    */
  public void remove(Node n){
    if ( n.getConnectingArcs() != null ) {
      throw new RuntimeException("Cannot remove node: " +
				 n.getName() + " as it has connecting arcs");
    }
    _nodes.removeElement(n);
    _nodesDict.remove(n.getName());
  }
  /**
    * remove arc and its connecting nodes
    */
  public void remove(Arc arc){
    if ( arc == null ) return;
    Node un1 = arc.getUpstreamNode();
    Node dn1 = arc.getDownstreamNode();
    if ( un1 != null ) 
      un1.removeArc(arc);
    if ( dn1 != null )
      dn1.removeArc(arc);
    _arcsDict.remove(arc.getName());
  }
  /**
    *
    */
  public void add(Node n){
    _nodes.addElement(n);
    _nodesDict.put(n.getName(),n);
  }
  /**
    *
    */
  public void add(Arc arc){
    _arcsDict.put(arc.getName(), arc);
  }
  /**
    * get all the arcs in the network
    */
  public Arc [] getAllArcs(){
    Arc [] arcs = new Arc[_arcsDict.size()];
    int i=0; 
    for(Enumeration e = _arcsDict.elements(); e.hasMoreElements(); i++){
      arcs[i] = (Arc) e.nextElement();
    }
    return arcs;
  }
  /**
    * get all the nodes in the network
    */
  public Node [] getAllNodes(){
    Node [] nodes = new Node[_nodesDict.size()];
    int i=0; 
    for(Enumeration e = _nodesDict.elements(); e.hasMoreElements(); i++){
      nodes[i] = (Node) e.nextElement();
    }
    return nodes;
  }
  /**
    * test for Network
    */
  public static void test(String [] args){
    String filename = null;
    if ( args == null ){
      filename = "test/net1.csv";
    } else {
      if ( args[0] == null ) 
	filename = "test/net1.csv";
      else 
	filename = args[0];
    }
//    Network network;
    try {
//    	Network network = Network.read(filename);
    	Network.read(filename);
    }catch(IOException ioe){
      System.err.println("Error reading : " + filename);
      ioe.printStackTrace();
    }
  }
}

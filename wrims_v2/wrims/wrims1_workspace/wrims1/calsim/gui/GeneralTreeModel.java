package calsim.gui;
//import vista.gui.*;
import javax.swing.*;
import javax.swing.event.*;
//import javax.swing.JTree.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import com.sun.xml.tree.*;
import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * The generic Tree Model for written for the CALSIM GUI
 *
 * @author Joel Fenolio
 * @version $Id: GeneralTreeModel.java,v 1.1.2.2 2001/07/12 01:59:37 amunevar Exp $
 */


public class GeneralTreeModel extends DefaultTreeModel implements Serializable {

  /**
   *Creates a Tree that looks for nodes that end with the array of Strings 'tags' that will not allow children.
   *The pics array are the path name for image files that will represent the nodes with the same index in the array
   *as in the tags array.  The dumbyRoot is required by the DefaultTreeModel and should be nulled after instantiation
   *of this class.
   */

  public GeneralTreeModel(TreeNode dumbyRoot,String[] tags,String[] pics) {
    super(dumbyRoot);
    dumbyRoot = null;
    setRoot(readData());
    childtag = tags;
    picpath = pics;
    addTreeModelListener(new TreeModelListener() {
      public void treeNodesChanged(TreeModelEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
               (e.getTreePath().getLastPathComponent());
        try {
          int index = e.getChildIndices()[0];
          node = (DefaultMutableTreeNode)(node.getChildAt(index));
        } catch (NullPointerException exc) {}
          System.out.println("The user has finished editing the node.");
          System.out.println("New value: " + node.getUserObject());
        }
      public void treeNodesInserted(TreeModelEvent e) {}
      public void treeNodesRemoved(TreeModelEvent e) {}
      public void treeStructureChanged(TreeModelEvent e) {}
    });
    //getPopupMenuItems();
  }

  ActionListener al = new ActionListener() {
    public void actionPerformed(ActionEvent e) {
      JMenuItem mi = (JMenuItem)(e.getSource());
      if(mi.getText() == "Add Node")    addNode();
      if(mi.getText() == "Delete")      removeNode(true);
      if(mi.getText() == "Edit")        editNode();
      if(mi.getText() == "Rename")      renameNode();
      if(mi.getText() == "Open")        open();
      if(mi.getText() == "Properties")  properties();
      if(mi.getText() == "Add Folder")  addFolder();
      if(mi.getText() == "Cut")         cutNode();
      if(mi.getText() == "Copy")        copyNode();
      if(mi.getText() == "Paste")       pasteNode();
    }
  };
  /**
   *Displays the Popup menu in the Tree at the node closest to the
   *specified x and y coordinates from the event that initiates this method
   */
  public void getNodePopup(Component jtree,int x,int y) {
    tree = (JTree)jtree;
    path = tree.getSelectionPath();
    nodepopup.show(jtree,x,y);
  }

  public boolean checkExtension(String extension, String name) {
    int end = name.length();
    String name1 = new String(name.substring(end-4,end));
    if (extension.equalsIgnoreCase(name1)) {
      return true;
     } else {
      return false;
    }
  }

  /**
   *Creates the menu items that are to be displayed in the Popup Menu
   */
  public void getPopupMenuItems() {
    open.addActionListener(al);
    nodepopup.add(open);
    nodepopup.addSeparator();
    addnode.addActionListener(al);
    nodepopup.add(addnode);
    addfolder.addActionListener(al);
    nodepopup.add(addfolder);
    nodepopup.addSeparator();
    rename.addActionListener(al);
    nodepopup.add(rename);
    deletenode.addActionListener(al);
    nodepopup.add(deletenode);
    nodepopup.addSeparator();
    cutnode.addActionListener(al);
    nodepopup.add(cutnode);
    copynode.addActionListener(al);
    nodepopup.add(copynode);
    pastenode.addActionListener(al);
    nodepopup.add(pastenode);
    nodepopup.addSeparator();
    editnode.addActionListener(al);
    nodepopup.add(editnode);
    prop.addActionListener(al);
    nodepopup.add(prop);
  }
  /**
   *Override to specifiy what the default new node name is to be
   */

  public String getLeafName() {
    return "New Node";
  }
  /**
   *Used to set class objects needed by other methods
   */
  public void setClassTS() {}
  /**
   *Adds a new node that does not allow children to be added
   *to the selected parent with the string specified in getLeafName()
   */
  public DefaultMutableTreeNode addNode() {
     DefaultMutableTreeNode parent = null;
     DefaultMutableTreeNode child = new DefaultMutableTreeNode(getLeafName());
     if (path == null) {
       parent = (DefaultMutableTreeNode)child.getRoot();
      } else {
       parent = (DefaultMutableTreeNode)(path.getLastPathComponent());
     }
     child.setAllowsChildren(false);
     if (parent.getAllowsChildren()) {
       insertNodetoModel(parent,child,parent.getChildCount());
       tree.scrollPathToVisible(new TreePath(child.getPath()));
      } else {
       nodepopup.setVisible(false);
       cannotPerform();
       return child;
     }
     return child;
  }
  /**
   *Adds a new node that allows children to be added
   *to the selected parent with the string specified in getLeafName()
   */
  public void addFolder() {
    DefaultMutableTreeNode parent = null;
    DefaultMutableTreeNode child = new DefaultMutableTreeNode("New Folder");
    if (path == null) {
      parent = (DefaultMutableTreeNode)child.getRoot();
     } else {
      parent = (DefaultMutableTreeNode)(path.getLastPathComponent());
    }
    if (parent.isLeaf()) {
      System.out.println(parent.getUserObject());
      System.out.println(isFolder(parent));
      if (isFolder(parent)) {
        parent.setAllowsChildren(true);
       } else if (parent.getAllowsChildren()) {
        parent.setAllowsChildren(true);
       } else {
        parent.setAllowsChildren(false);
        nodepopup.setVisible(false);
        cannotPerform();
        return;
       }
    }
    insertNodetoModel(parent,child,parent.getChildCount());
    tree.scrollPathToVisible(new TreePath(child.getPath()));
  }
  /**
   *Removes the selected node if the boolean is true a warning message will prompt the user
   *else the node will be automatically removed from its parent
   */
  public void removeNode(boolean warn) {
    if (warn) {
      if (permissionToRemove()) {
        nodepopup.setVisible(false);
        return;
      }
    }
    if (path != null) {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                        (path.getLastPathComponent());
      MutableTreeNode parent = (MutableTreeNode)(node.getParent());
      if (parent != null) {
        removeNode(node);
        node = null;
        return;
      }
    }
  }
  /**
   *Clones the currently selected node so that the clone can be pasted somewhere else on the tree
   */
  public void copyNode() {
    copiednode = (DefaultMutableTreeNode)
                      (path.getLastPathComponent());
    iscopied = true;
  }
  /**
   *Clones and removes the currently selected node so that the clone can be pasted somewhere else on the tree
   */
  public void cutNode() {
    copiednode = (DefaultMutableTreeNode)
                      (path.getLastPathComponent());
    iscopied = true;
    removeNode(copiednode);
  }
  /**
   *Pastes the cloned node from either the copy or cut methods
   */
  public void pasteNode() {
    DefaultMutableTreeNode parent = null;
    TreePath path = tree.getSelectionPath();
    DefaultMutableTreeNode child = copiednode;
    if (copiednode == null) iscopied = false;
    if (iscopied) {
      if (path == null) {
        parent = (DefaultMutableTreeNode)getRoot();
       } else {
        parent = (DefaultMutableTreeNode)(path.getLastPathComponent());
      }
      int children = parent.getChildCount();
      if (children > 0) {
		DefaultMutableTreeNode node;
		String name, name1;
		for (int i = 0; i < children; i++) {
		  node = (DefaultMutableTreeNode)parent.getChildAt(i);
		  name = (String)node.getUserObject();
		  name1 = (String)child.getUserObject();
		  if (name.equals(child.getUserObject())) {
            Integer j = new Integer(paste);
            name1 = new String("Copy of"+j.toString()+" "+name);
            child = new DefaultMutableTreeNode(name1);
            int children1 = node.getChildCount();
            System.out.println(children1);
            if (children1 > 0) {
			  for (int k = 0; k < children1; k++) {
				System.out.println(k);
			    DefaultMutableTreeNode newchild = (DefaultMutableTreeNode)node.getChildAt(k);
			    child.add(newchild);
			  }
		    }
            paste++;
	      }
	    }
      }
      if (parent.getAllowsChildren()) {
		insertNodeInto(child,parent,parent.getChildCount());
        tree.scrollPathToVisible(new TreePath(child.getPath()));
       } else {
        nodepopup.setVisible(false);
        cannotPerform();
      }
    }
  }


/*
  public void pasteNode() {
    DefaultMutableTreeNode parent = null;

    DefaultMutableTreeNode child = new DefaultMutableTreeNode((String)copiednode.getUserObject());
    if (copiednode.getChildCount() > 0) {
      for (int i = 0; i < copiednode.getChildCount(); i++) {
        child.add((DefaultMutableTreeNode)copiednode.getChildAt(i));
      }
    } else {
	  child.setAllowsChildren(false);
    }

    if (copiednode == null) iscopied = false;
    if (iscopied) {
      if (path == null) {
        parent = (DefaultMutableTreeNode)copiednode.getRoot();
       } else {
        parent = (DefaultMutableTreeNode)(path.getLastPathComponent());
      }
      if (parent.getAllowsChildren()) {
        insertNodetoModel(parent,copiednode,parent.getChildCount());
        tree.scrollPathToVisible(new TreePath(copiednode.getPath()));
       } else {
        nodepopup.setVisible(false);
        cannotPerform();
        //return child;
      }
    }
    //return child;
  }
*/
  /**
   *Opens the node at the specified path and tree
   */
  public void open(TreePath path, Component jtree) {
    tree = (JTree)jtree;
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
    if (node.isLeaf()) {
      System.out.println("Open");
     } else {
      tree.expandPath(path);
     }
  }
  /**
   *Opens the node specified class path and tree objects
   */
  public void open() {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
    if (node.isLeaf()) {
      System.out.println("Open");
     } else {
      tree.expandPath(path);
     }
  }
  /**
   *Override
   */
  public void properties() {
    System.out.println("Properties");
  }
  /**
   *Override
   */
  public void editNode() {
    System.out.println("Edit");
  }
  /**
   *Starts the editing process at the node specified by the class path
   */
  public void renameNode() {
    tree.startEditingAtPath(path);
  }
  /**
   *Inserts the child at the specified index of the parent node
   */
  public void insertNodetoModel(DefaultMutableTreeNode parent,
                                DefaultMutableTreeNode child,
                                int nodeindex) {
    insertNodeInto(child,parent,nodeindex);
  }
  /**
   *Removes the node from its parent
   */
  public void removeNode(DefaultMutableTreeNode node) {
    removeNodeFromParent(node);
    return;
  }
  /**
   *Removes all the children under the root node
   */
  public void clearAllNodes() {
    root.removeAllChildren();
    reload();
  }
  /**
   *Saves the tree structure to Xml at the specified path name
   */
  public void saveFile(String fname) throws FileNotFoundException, IOException {
    XmlDocument doc = new XmlDocument();
    FileOutputStream fos = new FileOutputStream(fname);
    try {
      PrintWriter pw = new PrintWriter(fos);
      saveData(doc);
      doc.write(pw);
    } catch (FileNotFoundException fnfe) {
    } catch (IOException ioe) {
    }
    fos.close();
  }
  /**
   *Saves the tree structure to Xml using the Xml Document class
   */
  public void saveData(XmlDocument doc) {
//    int children;
    String rootname = (String)root.getUserObject();
    String name;
    Element parentel, childel;
    DefaultMutableTreeNode parentnode, childnode; //, lastchild;
    Integer level;
    int rootchild = root.getChildCount();
    int rootdepth = root.getDepth();
    Element rootel = doc.createElement("node");
    DefaultMutableTreeNode nodes[] = new DefaultMutableTreeNode[100];
    Element elements[] = new Element[100];
    rootel.setAttribute("level","0");
    rootel.setAttribute("name",rootname);
    doc.appendChild(rootel);
    int rootcounter = 1;
    int lvlcount = 1;
    if (root.getChildCount() == 0) return;
    parentnode = (DefaultMutableTreeNode)root.getChildAt(0);
    name = (String)parentnode.getUserObject();
    parentel = doc.createElement("node");
    level = new Integer(lvlcount);
    parentel.setAttribute("level",level.toString());
    parentel.setAttribute("name",name);
    nodes[1] = parentnode;
    elements[1] = parentel;
    int parentchildren = parentnode.getChildCount();
    int depth = 1;
    rootel.appendChild(parentel);
    while (rootchild >= rootcounter){
      if (parentchildren > 0) {
        lvlcount++;
        depth++;
        for (int i = 0;i <= parentchildren-1; i++) {
          childnode = (DefaultMutableTreeNode)parentnode.getChildAt(i);
          name = (String)childnode.getUserObject();
          childel = doc.createElement("node");
          level = new Integer(lvlcount);
          childel.setAttribute("level",level.toString());
          childel.setAttribute("name",name);
          nodes[lvlcount] = childnode;
          elements[lvlcount] = childel;
          parentel.appendChild(childel);
          if (childnode.getChildCount() > 0) {
            lvlcount++;
            depth++;
            parentnode = childnode;
            parentel = childel;
            int childnum[] = new int[100];
            childnum[0] = parentnode.getChildCount();
            int childindex[] = new int[100];
            childnum[lvlcount] = parentnode.getChildCount();
            boolean exit = true;
            int i1 = 0;
            childindex[i1] = 0;
//            int index = 0;
//            int counter = 0;
            while (exit) {
              if (/*lvlcount >= 2 && */childindex[i1] > parentnode.getChildCount()-1) break;
              childnode = (DefaultMutableTreeNode)parentnode.getChildAt(childindex[i1]);
              name = (String)childnode.getUserObject();
              childel = doc.createElement("node");
              level = new Integer(lvlcount);
              childel.setAttribute("level",level.toString());
              childel.setAttribute("name",name);
              nodes[lvlcount] = childnode;
              elements[lvlcount] = childel;
              parentel.appendChild(childel);
              if (childnode.getChildCount() > 0) {
                parentnode = childnode;
                parentel = childel;
                lvlcount++;
                depth++;
                i1++;
                childnum[lvlcount] = parentnode.getChildCount();
              } else if (childnode.getChildCount() == 0 && childnum[lvlcount]-1 == childindex[i1]) {
                boolean check = false;
                i1--;
                if (i1 < 0) i1 = 0;
                lvlcount--;
                depth--;
                childindex[i1]++;
                childindex[i1+1] = 0;
                if (lvlcount <= 2) break;
                parentnode = nodes[lvlcount-1];
                parentel = elements[lvlcount-1];
                if (childindex[i1] > parentnode.getChildCount()-1) check = true;
                while (check) {
                  i1--;
                  if (i1 < 0) i1 = 0;
                  lvlcount--;
                  depth--;
                  childindex[i1]++;
                  childindex[i1+1] = 0;
                  parentnode = nodes[lvlcount-1];
                  parentel = elements[lvlcount-1];
                  if (childindex[i1] > parentnode.getChildCount()-1) check = false;
                }
              } else if (childnode.getChildCount() == 0 && childnum[lvlcount]-1 > childindex[i1]) {
                childindex[i1]++;
              } else if (childnum[lvlcount-1]-1 == childindex[i1] && depth == rootdepth) {
                exit = false;
              }
              if (lvlcount-1 == 2 && childindex[i1] > parentnode.getChildCount()-1) break;
            }
          }
          lvlcount = 2;
          depth = 2;
          parentnode = nodes[lvlcount-1];
          parentel = elements[lvlcount-1];
        }
      }
      if (rootchild == rootcounter) break;
      parentnode = (DefaultMutableTreeNode)root.getChildAt(rootcounter);
      parentchildren = parentnode.getChildCount();
      name = (String)parentnode.getUserObject();
      rootcounter++;
      parentel = doc.createElement("node");
      lvlcount = 1;
      depth = 1;
      parentel.setAttribute("level", Integer.toString(lvlcount));
      parentel.setAttribute("name",name);
      rootel.appendChild(parentel);
      nodes[lvlcount] = parentnode;
      elements[lvlcount] = parentel;
    }
  }
  /**
   *Reads the tree at the specified file path
   */
  public void getFile(String fname) throws IOException {
    try {
      readData(fname);
    } catch (Exception e) {
      e.printStackTrace(System.err);
      throw new IOException("File Not Found");
    }
  }
  /**
   *Checks to see if the code has the tags specified in the constructor
   */
  public boolean getAllowChildren(String name) {
    boolean test = false;
    for (int i = 0; i < childtag.length;i++) {
      if(name.endsWith(childtag[i])) test = true;
    }
    if(test) {
      return false;
     } else {
      return true;
    }
  }
  /**
   *Returns the array of Strings that are the path locations of the image files specified in the constructor
   */
  public String[] getIcons() {
    return picpath;
  }
  /**
   *Returns the array of tags specified in the constructor
   */
  public String[] getExtensions() {
    return childtag;
  }
  /**
   *Warns the user if they try to insert a node into a node that does not allow children
   */
  public void cannotPerform() {
     JOptionPane.showMessageDialog(null,"Cannot add a node to a Leaf");
  }
  /**
   *Returns true if Folder is contained in the name of the node
   */
  public boolean isFolder(DefaultMutableTreeNode node) {
    String s = (String)node.getUserObject();
    if(s.indexOf("Folder")>=0) {
      return true;
     }
     return false;
  }
  /**
   *Asks the user if they really want to remove this node
   */
  public boolean permissionToRemove() {
    int selectedoption = JOptionPane.showConfirmDialog(null,"Remove Node? This could result in permanently removing the DTS and MTS's from the project.",
                         "Warning",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
    if (selectedoption == 1) {
      return true;
     } else {
      return false;
    }
  }
  /**
   *Override
   */
  public void DTSOpen() {}
  /**
   *Reads in and constructs a tree from the XmlDocument at fname
   */
  public TreeNode readData(String fname) throws IOException, SAXException {
    DefaultMutableTreeNode parentnode, prvnode, curnode;
//    Element parentel, prvel, curel;
    Element curel;
    Integer lvl;
//    int parentlvl, prvlvl, curlvl;
    int prvlvl, curlvl;
    String name;
    DefaultMutableTreeNode nodes[] = new DefaultMutableTreeNode[100];
    FileInputStream fis = new FileInputStream(fname);
    try {
      XmlDocument doc = XmlDocument.createXmlDocument(fis,false);
      Element top = (Element)doc.getDocumentElement();
      TreeWalker xtt = new TreeWalker(top);
      parentnode = new DefaultMutableTreeNode(top.getAttribute("name"));
      root = parentnode;
//      parentlvl = 0;
      nodes[0] = root;
      curel = xtt.getNextElement("node");
      name = curel.getAttribute("name");
      curnode = new DefaultMutableTreeNode(name);
      curnode.setAllowsChildren(getAllowChildren(name));
      lvl = new Integer(curel.getAttribute("level"));
      curlvl = lvl.intValue();
      nodes[curlvl] = curnode;
      parentnode.add(curnode);
      prvnode = curnode;
//      prvel = curel;
      prvlvl = curlvl;
      while (true) {
        curel = xtt.getNextElement("node");
        if (curel == null) break;
        name = curel.getAttribute("name");
        curnode = new DefaultMutableTreeNode(name);
        curnode.setAllowsChildren(getAllowChildren(name));
        lvl = new Integer(curel.getAttribute("level"));
        curlvl = lvl.intValue();
        if (curlvl == prvlvl) {
          parentnode.add(curnode);
        } else if (curlvl > prvlvl) {
//          parentel = prvel;
          parentnode = prvnode;
          parentnode.add(curnode);
          nodes[prvlvl] = parentnode;
        } else if (curlvl < prvlvl) {
          parentnode = nodes[curlvl-1];
          parentnode.add(curnode);
        }
        prvlvl = curlvl;
//        prvel = curel;
        prvnode = curnode;
      }
      setRoot(root);
    } catch (IOException e) {
      e.printStackTrace(System.err);
      throw new IOException("File Not Found");
    } catch (SAXException se) {
      throw new SAXException("Error trying to read Xml File");
    }
    fis.close();
    return root;
  }
  /**
   *Returns the root node
   */
  public TreeNode readData() {
    return root;
  }

  boolean iscopied = false;
  final int ELEMENT_TYPE = 1;

  DefaultMutableTreeNode copiednode = new DefaultMutableTreeNode();
  DefaultMutableTreeNode root =
                new DefaultMutableTreeNode("Dts Directory");
  DefaultMutableTreeNode n1, n2, n3, n4, n5, n6, n7;

  JPopupMenu nodepopup = new JPopupMenu();
  JMenuItem open = new JMenuItem("Open");
  JMenuItem prop = new JMenuItem("Properties");
  JMenuItem rename = new JMenuItem("Rename");
  JMenuItem addnode = new JMenuItem("Add Node");
  JMenuItem deletenode = new JMenuItem("Delete");
  JMenuItem addfolder = new JMenuItem("Add Folder");
  JMenuItem editnode = new JMenuItem("Edit");
  JMenuItem cutnode = new JMenuItem("Cut");
  JMenuItem copynode = new JMenuItem("Copy");
  JMenuItem pastenode = new JMenuItem("Paste");
  JMenuItem save = new JMenuItem("Save");

  TreePath path;
  JTree tree;

  String childtag[];
  String picpath[];

  int paste = 0;

}



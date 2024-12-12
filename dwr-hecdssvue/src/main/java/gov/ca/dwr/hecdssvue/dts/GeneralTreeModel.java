/*
 * Water Resource Integrated Modeling System (WRIMS) Copyright (c) 2024.
 *
 * WRIMS 2 is copyrighted by the State of California Department of Water Resources.
 * It is licensed under the Eclipse Public License, Version 1.0.
 * See Eclipse Public License for more details.
 */

package gov.ca.dwr.hecdssvue.dts;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Serializable;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;
import org.xml.sax.SAXException;

/**
 * The generic Tree Model for written for the CALSIM GUI
 *
 * @author Joel Fenolio
 * @version $Id: GeneralTreeModel.java,v 1.1.2.2 2001/07/12 01:59:37 amunevar Exp $
 */


public class GeneralTreeModel extends DefaultTreeModel implements Serializable {

    /**
     * Creates a Tree that looks for nodes that end with the array of Strings 'tags' that will not allow children.
     * The pics array are the path name for image files that will represent the nodes with the same index in the array
     * as in the tags array.  The dumbyRoot is required by the DefaultTreeModel and should be nulled after instantiation
     * of this class.
     */

    public GeneralTreeModel(TreeNode dumbyRoot, String[] tags, String[] pics) {
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
                    node = (DefaultMutableTreeNode) (node.getChildAt(index));
                } catch (NullPointerException exc) {
                }
                System.out.println("The user has finished editing the node.");
                System.out.println("New value: " + node.getUserObject());
            }

            public void treeNodesInserted(TreeModelEvent e) {
            }

            public void treeNodesRemoved(TreeModelEvent e) {
            }

            public void treeStructureChanged(TreeModelEvent e) {
            }
        });
        //getPopupMenuItems();
    }

    ActionListener al = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            JMenuItem mi = (JMenuItem) (e.getSource());
            if (mi.getText() == "Add Node") {
                addNode();
            }
            if (mi.getText() == "Delete") {
                removeNode(true);
            }
            if (mi.getText() == "Edit") {
                editNode();
            }
            if (mi.getText() == "Rename") {
                renameNode();
            }
            if (mi.getText() == "Open") {
                open();
            }
            if (mi.getText() == "Properties") {
                properties();
            }
            if (mi.getText() == "Add Folder") {
                addFolder();
            }
            if (mi.getText() == "Cut") {
                cutNode();
            }
            if (mi.getText() == "Copy") {
                copyNode();
            }
            if (mi.getText() == "Paste") {
                pasteNode();
            }
        }
    };

    /**
     * Displays the Popup menu in the Tree at the node closest to the
     * specified x and y coordinates from the event that initiates this method
     */
    public void getNodePopup(Component jtree, int x, int y) {
        tree = (JTree) jtree;
        path = tree.getSelectionPath();
        nodepopup.show(jtree, x, y);
    }

    public boolean checkExtension(String extension, String name) {
        int end = name.length();
        String name1 = name.substring(end - 4, end);
        return extension.equalsIgnoreCase(name1);
    }

    /**
     * Creates the menu items that are to be displayed in the Popup Menu
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
     * Override to specifiy what the default new node name is to be
     */

    public String getLeafName() {
        return "New Node";
    }

    /**
     * Used to set class objects needed by other methods
     */
    public void setClassTS() {
    }

    /**
     * Adds a new node that does not allow children to be added
     * to the selected parent with the string specified in getLeafName()
     */
    public DefaultMutableTreeNode addNode() {
        DefaultMutableTreeNode parent = null;
        DefaultMutableTreeNode child = new DefaultMutableTreeNode(getLeafName());
        if (path == null) {
            parent = (DefaultMutableTreeNode) child.getRoot();
        } else {
            parent = (DefaultMutableTreeNode) (path.getLastPathComponent());
        }
        child.setAllowsChildren(false);
        if (parent.getAllowsChildren()) {
            insertNodetoModel(parent, child, parent.getChildCount());
            tree.scrollPathToVisible(new TreePath(child.getPath()));
        } else {
            nodepopup.setVisible(false);
            cannotPerform();
            return child;
        }
        return child;
    }

    /**
     * Adds a new node that allows children to be added
     * to the selected parent with the string specified in getLeafName()
     */
    public void addFolder() {
        DefaultMutableTreeNode parent = null;
        DefaultMutableTreeNode child = new DefaultMutableTreeNode("New Folder");
        if (path == null) {
            parent = (DefaultMutableTreeNode) child.getRoot();
        } else {
            parent = (DefaultMutableTreeNode) (path.getLastPathComponent());
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
        insertNodetoModel(parent, child, parent.getChildCount());
        tree.scrollPathToVisible(new TreePath(child.getPath()));
    }

    /**
     * Removes the selected node if the boolean is true a warning message will prompt the user
     * else the node will be automatically removed from its parent
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
            MutableTreeNode parent = (MutableTreeNode) (node.getParent());
            if (parent != null) {
                removeNode(node);
                node = null;
            }
        }
    }

    /**
     * Clones the currently selected node so that the clone can be pasted somewhere else on the tree
     */
    public void copyNode() {
        copiednode = (DefaultMutableTreeNode)
            (path.getLastPathComponent());
        iscopied = true;
    }

    /**
     * Clones and removes the currently selected node so that the clone can be pasted somewhere else on the tree
     */
    public void cutNode() {
        copiednode = (DefaultMutableTreeNode)
            (path.getLastPathComponent());
        iscopied = true;
        removeNode(copiednode);
    }

    /**
     * Pastes the cloned node from either the copy or cut methods
     */
    public void pasteNode() {
        DefaultMutableTreeNode parent = null;
        TreePath path = tree.getSelectionPath();
        DefaultMutableTreeNode child = copiednode;
        if (copiednode == null) {
            iscopied = false;
        }
        if (iscopied) {
            if (path == null) {
                parent = (DefaultMutableTreeNode) getRoot();
            } else {
                parent = (DefaultMutableTreeNode) (path.getLastPathComponent());
            }
            int children = parent.getChildCount();
            if (children > 0) {
                DefaultMutableTreeNode node;
                String name, name1;
                for (int i = 0; i < children; i++) {
                    node = (DefaultMutableTreeNode) parent.getChildAt(i);
                    name = (String) node.getUserObject();
                    name1 = (String) child.getUserObject();
                    if (name.equals(child.getUserObject())) {
                        Integer j = new Integer(paste);
                        name1 = "Copy of" + j + " " + name;
                        child = new DefaultMutableTreeNode(name1);
                        int children1 = node.getChildCount();
                        System.out.println(children1);
                        if (children1 > 0) {
                            for (int k = 0; k < children1; k++) {
                                System.out.println(k);
                                DefaultMutableTreeNode newchild = (DefaultMutableTreeNode) node.getChildAt(k);
                                child.add(newchild);
                            }
                        }
                        paste++;
                    }
                }
            }
            if (parent.getAllowsChildren()) {
                insertNodeInto(child, parent, parent.getChildCount());
                tree.scrollPathToVisible(new TreePath(child.getPath()));
            } else {
                nodepopup.setVisible(false);
                cannotPerform();
            }
        }
    }

    /**
     * Opens the node specified class path and tree objects
     */
    public void open() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        if (node.isLeaf()) {
            System.out.println("Open");
        } else {
            tree.expandPath(path);
        }
    }

    /**
     * Override
     */
    public void properties() {
        System.out.println("Properties");
    }

    /**
     * Override
     */
    public void editNode() {
        System.out.println("Edit");
    }

    /**
     * Starts the editing process at the node specified by the class path
     */
    public void renameNode() {
        tree.startEditingAtPath(path);
    }

    /**
     * Inserts the child at the specified index of the parent node
     */
    public void insertNodetoModel(DefaultMutableTreeNode parent,
        DefaultMutableTreeNode child,
        int nodeindex) {
        insertNodeInto(child, parent, nodeindex);
    }

    /**
     * Removes the node from its parent
     */
    public void removeNode(DefaultMutableTreeNode node) {
        removeNodeFromParent(node);
    }

    /**
     * Reads the tree at the specified file path
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
     * Checks to see if the code has the tags specified in the constructor
     */
    public boolean getAllowChildren(String name) {
        boolean test = false;
        for (int i = 0; i < childtag.length; i++) {
            if (name.endsWith(childtag[i])) {
                test = true;
                break;
            }
        }
        return !test;
    }

    /**
     * Returns the array of Strings that are the path locations of the image files specified in the constructor
     */
    public String[] getIcons() {
        return picpath;
    }

    /**
     * Returns the array of tags specified in the constructor
     */
    public String[] getExtensions() {
        return childtag;
    }

    /**
     * Warns the user if they try to insert a node into a node that does not allow children
     */
    public void cannotPerform() {
        JOptionPane.showMessageDialog(null, "Cannot add a node to a Leaf");
    }

    /**
     * Returns true if Folder is contained in the name of the node
     */
    public boolean isFolder(DefaultMutableTreeNode node) {
        String s = (String) node.getUserObject();
        return s.indexOf("Folder") >= 0;
    }

    /**
     * Asks the user if they really want to remove this node
     */
    public boolean permissionToRemove() {
        int selectedoption = JOptionPane.showConfirmDialog(null,
            "Remove Node? This could result in permanently removing the DTS and MTS's from the project.",
            "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        return selectedoption == 1;
    }

    /**
     * Reads in and constructs a tree from the XmlDocument at fname
     */
    public TreeNode readData(String fname) throws IOException, SAXException {
        DefaultMutableTreeNode parentnode, prvnode, curnode;
        Element curel;
        Integer lvl;
        int prvlvl, curlvl;
        String name;
        DefaultMutableTreeNode[] nodes = new DefaultMutableTreeNode[100];

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(fname);
            Element top = doc.getDocumentElement();
            DocumentTraversal traversal = (DocumentTraversal) doc;
            TreeWalker xtt = traversal.createTreeWalker(top, NodeFilter.SHOW_ELEMENT, null, false);
            parentnode = new DefaultMutableTreeNode(top.getAttribute("name"));
            root = parentnode;
            nodes[0] = root;
            curel = XmlUtilities.getNextElement(xtt, "node");
            name = curel.getAttribute("name");
            curnode = new DefaultMutableTreeNode(name);
            curnode.setAllowsChildren(getAllowChildren(name));
            lvl = new Integer(curel.getAttribute("level"));
            curlvl = lvl.intValue();
            nodes[curlvl] = curnode;
            parentnode.add(curnode);
            prvnode = curnode;
            prvlvl = curlvl;
            while (true) {
                curel = XmlUtilities.getNextElement(xtt, "node");
                if (curel == null) {
                    break;
                }
                name = curel.getAttribute("name");
                curnode = new DefaultMutableTreeNode(name);
                curnode.setAllowsChildren(getAllowChildren(name));
                lvl = new Integer(curel.getAttribute("level"));
                curlvl = lvl.intValue();
                if (curlvl == prvlvl) {
                    parentnode.add(curnode);
                } else if (curlvl > prvlvl) {
                    parentnode = prvnode;
                    parentnode.add(curnode);
                    nodes[prvlvl] = parentnode;
                } else if (curlvl < prvlvl) {
                    parentnode = nodes[curlvl - 1];
                    parentnode.add(curnode);
                }
                prvlvl = curlvl;
                prvnode = curnode;
            }
            setRoot(root);
        } catch (IOException e) {
            e.printStackTrace(System.err);
            throw new IOException("File Not Found");
        } catch (SAXException | ParserConfigurationException se) {
            throw new SAXException("Error trying to read Xml File");
        }
        return root;
    }

    /**
     * Returns the root node
     */
    public TreeNode readData() {
        return root;
    }

    boolean iscopied = false;

    DefaultMutableTreeNode copiednode = new DefaultMutableTreeNode();
    DefaultMutableTreeNode root =
        new DefaultMutableTreeNode("Dts Directory");

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

    String[] childtag;
    String[] picpath;

    int paste = 0;

}



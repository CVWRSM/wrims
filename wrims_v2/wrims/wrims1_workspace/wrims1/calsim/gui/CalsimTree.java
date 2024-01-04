package calsim.gui;
import javax.swing.*;
//import javax.swing.event.*;
//import javax.swing.JTree.*;
import javax.swing.tree.*;
//import java.awt.dnd.*;
import java.awt.*;
import java.awt.event.*;
//import java.awt.datatransfer.*;
//import java.io.*;

/**
 * JTree written for CALSIM GUI
 *
 * @author Joel Fenolio
 * @version $Id: CalsimTree.java,v 1.1.2.2 2001/07/12 01:59:32 amunevar Exp $
 */

public class CalsimTree extends JTree {

  boolean add = false;

  JPopupMenu leafpopup = new JPopupMenu();
  JMenuItem openleaf = new JMenuItem("Open");
  JMenuItem deleteleaf = new JMenuItem("Delete");
  JMenuItem editleaf = new JMenuItem("Edit");

  DefaultMutableTreeNode dragnode = new DefaultMutableTreeNode();
  DefaultMutableTreeNode dragnodeparent = new DefaultMutableTreeNode();

//  DragSource ds = DragSource.getDefaultDragSource();
  CalsimTreeRenderer ctr = new CalsimTreeRenderer();
/*
  DragSourceListener dsl = new DragSourceListener() {
    public void dragDropEnd(DragSourceDropEvent dse) {}
    public void dragEnter(DragSourceDragEvent dse) {}
    public void dragExit(DragSourceEvent dse) {}
    public void dragOver(DragSourceDragEvent dse) {}
    public void dropActionChanged(DragSourceDragEvent dse) {}
  };

  DragGestureListener dgl = new  DragGestureListener() {
    public void dragGestureRecognized(DragGestureEvent dge) {
      TreePath path = getSelectionPath();
      if (path == null) {
        System.out.println ("Nothing selected - beep");
       } else {
        DefaultMutableTreeNode selection =
                      (DefaultMutableTreeNode)path.getLastPathComponent();
        dragnodeparent = (DefaultMutableTreeNode)selection.getParent();
        TransferableTreeNode node = new TransferableTreeNode(selection);
        ds.startDrag(dge,ds.DefaultCopyDrop,node,dsl);
        dragnode = selection;
      }
    }
  };

  DropTarget dt;

  DropTargetListener dtl = new DropTargetListener() {
     public void dragEnter(DropTargetDragEvent dtde) {}
     public void dragOver(DropTargetDragEvent dtde) {
      dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
       Point p = dtde.getLocation();
       TreePath path = getClosestPathForLocation(p.x,p.y);
       setSelectionPath(path);
     }
     public void dropActionChanged(DropTargetDragEvent dtde) {}
     public void dragExit(DropTargetEvent dte) {}
     public void drop(DropTargetDropEvent dtde) {
       int i = dtde.getDropAction();
       if (i == 2) {
        add = true;
        Point p = dtde.getLocation();
        System.out.println(p.x);
        System.out.println(p.y);
        TreePath path = getSelectionPath();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
        if (!node.getAllowsChildren()) {
           cannotPerform();
           return;
        }
        removeDragNode();
        addDragNode(p.x,p.y);
       }
     }
  };

*/
  DefaultMutableTreeNode root =
                new DefaultMutableTreeNode("Dts Directory");
  GeneralTreeModel _gtm;

  public CalsimTree(GeneralTreeModel gtm) {

    _gtm = gtm;

    setModel(_gtm);
/*
    ds.createDefaultDragGestureRecognizer(
        this, DnDConstants.ACTION_COPY_OR_MOVE, dgl);
    dt = new DropTarget(this,dtl);
*/
    MouseListener ml = new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        if(e.getModifiers() == InputEvent.BUTTON3_MASK) {
          TreePath path = getClosestPathForLocation(e.getX(),e.getY());
          setSelectionPath(path);
          _gtm.getNodePopup(e.getComponent(),e.getX(),e.getY());
        }
        /*
        if(e.getClickCount() == 2) {
          TreePath path = getSelectionPath();
          DefaultMutableTreeNode node = (DefaultMutableTreeNode)(path.getLastPathComponent());
          if (node.isLeaf()) _gtm.open(path,e.getComponent());
        }
		*/
        if(e.getModifiers() ==InputEvent.BUTTON1_MASK) {
          TreePath path = getClosestPathForLocation(e.getX(),e.getY());
          //System.out.println(path);
          //DefaultMutableTreeNode node = (DefaultMutableTreeNode)(path.getLastPathComponent());
          setSelectionPath(path);
        }

        if (e.getClickCount() == 1) {
		  _gtm.setClassTS();
        }
      }


      public void mouseClicked(MouseEvent e) {}
      public void mouseReleased(MouseEvent e) {}
      public void mouseEntered(MouseEvent e) {}
      public void mouseExited(MouseEvent e) {}
        /*For differentiating between if a leaf or a node was selected
        DefaultMutableTreeNode node = new DefaultMutableTreeNode();
        node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent()
          if(e.getModifiers() == e.BUTTON3_MASK) {
            if(node.isLeaf()) {
              getLeafPopup(e.getComponent(),e.getX(),e.getY());
             } else {
              getNodePopup(e.getComponent(),e.getX(),e.getY());
             }
            }
         */
    };

    setEditable(true);
    setShowsRootHandles(true);
    getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    addMouseListener(ml);
    setCellRenderer(ctr);
  }
/*
  public void addDragNode(int x,int y) {
    if (add) {
      TreePath path = getSelectionPath();
      DefaultMutableTreeNode parent = null;
      DefaultMutableTreeNode child = dragnode;
      if (path == null) {
        parent = (DefaultMutableTreeNode)child.getRoot();
       } else {
        parent = (DefaultMutableTreeNode)(path.getLastPathComponent());
      }
      _gtm.insertNodetoModel(parent,child,parent.getChildCount());
      scrollPathToVisible(new TreePath(child.getPath()));
    }
    add = false;
  }

  public void removeDragNode() {
    if (dragnodeparent != null) {
      _gtm.removeNode(dragnode);
      return;
    }
  }
*/
  public void cannotPerform() {
    JOptionPane.showMessageDialog(this,"Cannot add a node to a Leaf");
  }

    private class CalsimTreeRenderer extends DefaultTreeCellRenderer  {

    public CalsimTreeRenderer() {}

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
                                 boolean expanded, boolean leaf, int row, boolean hasFocus) {
      super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
      DefaultMutableTreeNode node =
                    (DefaultMutableTreeNode)value;
      if(leaf && isFolder(node)) {
        setIcon(getClosedIcon());
        node.setAllowsChildren(true);
      }
      if(allowsChildren(node)) {
        setIcon(getClosedIcon());
      }
      ImageIcon icon;
      String tags[] = _gtm.getExtensions();
      String icons[] = _gtm.getIcons();
      if (icons != null) {
        for (int i = 0; i < tags.length; i++) {
          String s = (String)node.getUserObject();
          if (_gtm.checkExtension(tags[i],s)/*s.endsWith(tags[i])*/) {
            if (i > icons.length-1) {
              setIcon(getLeafIcon());
            } else {
              icon = new ImageIcon(icons[i]);
              setIcon(icon);
            }
          }
        }
      }

      return this;

    }

    public boolean isFolder(DefaultMutableTreeNode node) {
      String s = node.toString();
      if(s.indexOf("Folder")>=0) {
        return true;
      }
      return false;
    }

    public boolean allowsChildren(DefaultMutableTreeNode node) {
      if (node.getAllowsChildren()) return true;
      return false;
    }
  }





}



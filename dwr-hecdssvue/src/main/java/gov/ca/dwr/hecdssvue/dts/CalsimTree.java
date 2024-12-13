/*
 * Water Resource Integrated Modeling System (WRIMS) Copyright (c) 2024.
 *
 * WRIMS 2 is copyrighted by the State of California Department of Water Resources.
 * It is licensed under the Eclipse Public License, Version 1.0.
 * See Eclipse Public License for more details.
 */

package gov.ca.dwr.hecdssvue.dts;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * JTree written for CALSIM GUI
 *
 * @author Joel Fenolio
 * @version $Id: CalsimTree.java,v 1.1.2.2 2001/07/12 01:59:32 amunevar Exp $
 */

public class CalsimTree extends JTree {
    CalsimTreeRenderer ctr = new CalsimTreeRenderer();
    GeneralTreeModel _gtm;

    public CalsimTree(GeneralTreeModel gtm) {

        _gtm = gtm;

        setModel(_gtm);
        MouseListener ml = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
                    TreePath path = getClosestPathForLocation(e.getX(), e.getY());
                    setSelectionPath(path);
                    _gtm.getNodePopup(e.getComponent(), e.getX(), e.getY());
                }
                if (e.getModifiers() == InputEvent.BUTTON1_MASK) {
                    TreePath path = getClosestPathForLocation(e.getX(), e.getY());
                    setSelectionPath(path);
                }

                if (e.getClickCount() == 1) {
                    _gtm.setClassTS();
                }
            }


            public void mouseClicked(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
        };

        setEditable(true);
        setShowsRootHandles(true);
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        addMouseListener(ml);
        setCellRenderer(ctr);
    }

    private class CalsimTreeRenderer extends DefaultTreeCellRenderer {

        public CalsimTreeRenderer() {
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
            boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            DefaultMutableTreeNode node =
                (DefaultMutableTreeNode) value;
            if (leaf && isFolder(node)) {
                setIcon(getClosedIcon());
                node.setAllowsChildren(true);
            }
            if (allowsChildren(node)) {
                setIcon(getClosedIcon());
            }
            ImageIcon icon;
            String[] tags = _gtm.getExtensions();
            String[] icons = _gtm.getIcons();
            if (icons != null) {
                for (int i = 0; i < tags.length; i++) {
                    String s = (String) node.getUserObject();
                    if (_gtm.checkExtension(tags[i], s)/*s.endsWith(tags[i])*/) {
                        if (i > icons.length - 1) {
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
            return s.indexOf("Folder") >= 0;
        }

        public boolean allowsChildren(DefaultMutableTreeNode node) {
            return node.getAllowsChildren();
        }
    }


}



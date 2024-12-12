/*
 * Water Resource Integrated Modeling System (WRIMS) Copyright (c) 2024.
 *
 * WRIMS 2 is copyrighted by the State of California Department of Water Resources.
 * It is licensed under the Eclipse Public License, Version 1.0.
 * See Eclipse Public License for more details.
 */

package gov.ca.dwr.hecdssvue.dts;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.PrintJob;
import java.awt.Toolkit;
import java.text.CollationKey;
import java.text.Collator;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import vista.app.DataGraphFrame;
import vista.app.DataTableFrame;
import vista.app.MultiDataTableFrame;
import vista.gui.VistaUtils;

/**
 * Common utilities for Gui package
 *
 * @author Nicky Sandhu, Yan-Ping Zuo, Armin munevar
 * @version $Id: GuiUtils.java,v 1.1.4.52 2001/10/23 16:28:39 jfenolio Exp $
 */
public class GuiUtils {
    static {
        VistaUtils.AWT_FILE_DIALOG = true;
    }


    /**
     * prints component to printer
     */

    public static void print(Component comp) {
        Frame fr = JOptionPane.getFrameForComponent(comp);
        Toolkit dtk = Toolkit.getDefaultToolkit();
        PrintJob pjob = dtk.getPrintJob(fr, "Print Dialog", null);
        if (pjob != null) {
            Graphics pg = pjob.getGraphics();
            if (pg != null) {
                comp.paint(pg);  //CB see paint method of comp's actual class to fit on one page
                pg.dispose(); // flush page
            }
            pjob.end();
        } else {
            AppUtils.showDssFileErrorDialog(new RuntimeException("No print job available!!"));
        }
    }

    /**
     *
     */
    static void convertFramesToPanels(JFrame[] frs) {
        if (frs == null) {
            return;
        }
        for (int i = 0; i < frs.length; i++) {
            if (frs[i] == null) {
                continue;
            }
            if (frs[i] instanceof DataGraphFrame) {
                frs[i].setVisible(true);
            } else if (frs[i] instanceof DataTableFrame || frs[i] instanceof MultiDataTableFrame) {
                frs[i].setVisible(true);
            } else {
                frs[i].setVisible(true);
            }
        }
    }

    /**
     *
     */
    public static void displayDTS(DerivedTimeSeries dts) {
        //send bpart and cpart to app
        JFrame[] frs = AppUtils.displayDTSData(dts);
        convertFramesToPanels(frs);
    }

    /**
     *
     */
    public static void displayMTS(MultipleTimeSeries mts) {
        //send bpart and cpart to app
        JFrame[] frs = AppUtils.displayData(mts);
        convertFramesToPanels(frs);
    }

    /**
     * traverses the component to find the first component of the
     * given class.
     */
    public static Component getComponent(Class cl, Container cont) {
        Component[] comps = cont.getComponents();
        for (int i = 0; i < comps.length; i++) {
            if (cl.isInstance(comps[i])) {
                return comps[i];
            }
            if (comps[i] instanceof Container) {
                Component compx = getComponent(cl, (Container) comps[i]);
                if (compx != null) {
                    return compx;
                }
            }
        }
        return null;
    }

    /**
     *
     */
    public static void checkAndAddToProject(Component comp, DerivedTimeSeries dts) {
        String name = dts.getName();
        Project prj = AppUtils.getCurrentProject();
        DerivedTimeSeries dts1 = prj.getDTS(name);
        prj.setDTSMod(true);
        if (dts1 == null) {
            prj.add(dts);
        } else {
            int opt = JOptionPane.showConfirmDialog
                (comp,
                    "DTS with name " + name + " already exists in project!\n" +
                        "Do you want to override its definition?", "Warning",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (opt == JOptionPane.YES_OPTION) {
                prj.remove(name); // remove old by the same name
                prj.add(dts);
            } else {
            }
        }
    }

    /**
     *
     */
    public static void checkAndAddToProject(Component comp, MultipleTimeSeries mts) {
        String name = mts.getName();
        Project prj = AppUtils.getCurrentProject();
        MultipleTimeSeries mts1 = prj.getMTS(name);
        if (mts1 == null) {
            prj.add(mts);
        } else {
            int opt = JOptionPane.showConfirmDialog
                (comp,
                    "MTS with name " + name + " already exists in project!\n" +
                        "Do you want to override its definition?", "Warning",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (opt == JOptionPane.YES_OPTION) {
                prj.remove(name); // remove old by the same name
                prj.add(mts);
            } else {
            }
        }
    }

    public static String[] sortArray(String[] s1) {
        CollationKey tmp;
        Collator col = Collator.getInstance();
        CollationKey[] keys = new CollationKey[s1.length];
        for (int j = 0; j < s1.length; j++) {
            keys[j] = col.getCollationKey(s1[j]);
        }
        for (int i = 0; i < keys.length; i++) {
            for (int j = i + 1; j < keys.length; j++) {
                if (keys[i].compareTo(keys[j]) > 0) {
                    tmp = keys[i];
                    keys[i] = keys[j];
                    keys[j] = tmp;
                }
            }
        }
        String[] s2 = new String[s1.length];
        for (int j = 0; j < s1.length; j++) {
            s2[j] = keys[j].getSourceString();
        }
        return s2;
    }

}
/*
$Log: GuiUtils.java,v $
Revision 1.1.4.52  2001/10/23 16:28:39  jfenolio
Changes made are:
1) Bug Fixes to the dts, mts, and the dts tree
2) MSRGui added and MSR modifications made
3) Changes to option pane including new controller for multi-year position analysis
4) Output tab changes include 4 different dss files can be viewed and other modifications to the message panel and menus

Revision 1.1.4.51  2001/07/12 01:59:39  amunevar
removal of all unneeded files for cleanup

Revision 1.1.4.50  2001/04/18 21:07:44  jfenolio
dts tree added, start month selection for table display

Revision 1.1.4.49  2000/12/20 20:07:10  amunevar
commit for ver 1.0.7

Revision 1.1.4.48  2000/06/22 00:57:53  amunevar
commit to update all files before Ryan checks out first time

Revision 1.1.4.47  1999/10/13 16:46:43  nsandhu
*** empty log message ***

Revision 1.1.4.46  1999/09/16 23:54:25  nsandhu
*** empty log message ***

Revision 1.1.4.45  1999/09/16 16:59:56  amunevar
*** empty log message ***

Revision 1.1.4.44  1999/09/01 16:56:58  amunevar
*** empty log message ***

Revision 1.1.4.43  1999/08/26 01:43:07  nsandhu
*** empty log message ***

Revision 1.1.4.42  1999/08/26 01:36:51  nsandhu
*** empty log message ***

Revision 1.1.4.41  1999/08/24 18:43:29  nsandhu
*** empty log message ***

Revision 1.1.4.40  1999/08/23 21:20:25  nsandhu
*** empty log message ***

Revision 1.1.4.39  1999/08/22 19:19:59  nsandhu
added tabbed pane for input data

Revision 1.1.4.38  1999/08/18 19:50:24  nsandhu
*** empty log message ***

Revision 1.1.4.37  1999/08/13 00:59:38  amunevar
*** empty log message ***

Revision 1.1.4.36  1999/08/07 00:23:03  nsandhu
changed about dialog; fixed dts table editing bug

Revision 1.1.4.35  1999/07/29 21:32:27  zuo
add ControlContainer.java

Revision 1.1.4.34  1999/07/25 19:25:31  nsandhu
*** empty log message ***

Revision 1.1.4.33  1999/07/20 22:25:13  zuo
separate status panel from main Panel

Revision 1.1.4.32  1999/07/20 16:52:57  zuo
added status panel to frame

Revision 1.1.4.31  1999/07/18 20:56:48  nsandhu
*** empty log message ***

Revision 1.1.4.30  1999/07/08 00:42:28  nsandhu
*** empty log message ***

Revision 1.1.4.29  1999/07/02 22:49:24  nsandhu
*** empty log message ***

Revision 1.1.4.28  1999/07/02 22:37:59  nsandhu
incorporated yan-ping's changes

Revision 1.1.4.27  1999/07/02 22:13:54  nsandhu
first move to eliminate extra frames on screen

Revision 1.1.4.26  1999/07/02 20:25:51  nsandhu
moved versioning info to calsim directory

*/

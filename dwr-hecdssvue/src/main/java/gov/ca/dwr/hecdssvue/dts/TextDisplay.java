/*
 * Water Resource Integrated Modeling System (WRIMS) Copyright (c) 2024.
 *
 * WRIMS 2 is copyrighted by the State of California Department of Water Resources.
 * It is licensed under the Eclipse Public License, Version 1.0.
 * See Eclipse Public License for more details.
 */

package gov.ca.dwr.hecdssvue.dts;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.FileWriter;
import java.io.PrintWriter;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.MinimalHTMLWriter;
import vista.gui.VistaUtils;

/**
 * Panel for monthly table display
 *
 * @author Nicky Sandhu
 * @version $Id: TextDisplay.java,v 1.1.2.15 2001/07/12 02:00:02 amunevar Exp $
 */
public class TextDisplay extends MPanel {
    public static boolean DEBUG = false;
    public static String[] itemText = {
        "Save",
        "Save As Html",
        "Print",
        "Quit",
    };
    public static String[] toolTipText = {
        "Saves to a text file",
        "Saves to a html file",
        "Prints as displayed",
        "Closes this display frame",
    };
    public static int[] itemKeys = {
        KeyEvent.VK_S,
        KeyEvent.VK_H,
        KeyEvent.VK_P,
        KeyEvent.VK_Q,
    };

    /**
     *
     */
    protected TextDisplay() {
    }

    /**
     * adds the given document to this frame
     */
    void addDocument(StyledDocument doc) {
        _doc = doc;
        JTextPane jtp = new JTextPane() {
            @Override
            public boolean getScrollableTracksViewportWidth() {
                return false;
            }
        };
        jtp.setEditable(false);
        jtp.setDocument(doc);
        setLayout(new BorderLayout());
        add(new JScrollPane(jtp), BorderLayout.CENTER);
    }

    /**
     *
     */
    public void setFrameTitle(String str) {
        _frameTitle = str;
    }

    /**
     *
     */
    public String getFrameTitle() {
        return _frameTitle;
    }

    /**
     *
     */
    public JMenuBar getJMenuBar() {
        if (_mbar == null) {
            _mbar = createJMenuBar();
        }
        return _mbar;
    }

    /**
     *
     */
    public JMenuBar createJMenuBar() {

        int mindex = 0;
        JMenuItem saveItem = new JMenuItem(itemText[mindex]);
        saveItem.setToolTipText(toolTipText[mindex]);
        saveItem.setAccelerator(KeyStroke.getKeyStroke(itemKeys[mindex], KeyEvent.CTRL_MASK));
        saveItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                save();
            }
        });
        //
        mindex++;
        JMenuItem saveAsHtmlItem = new JMenuItem(itemText[mindex]);
        //saveAsHtmlItem.setEnabled(false); // ?? temporary
        saveAsHtmlItem.setToolTipText(toolTipText[mindex]);
        saveAsHtmlItem.setAccelerator(KeyStroke.getKeyStroke(itemKeys[mindex], KeyEvent.CTRL_MASK));
        saveAsHtmlItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                saveAsHtml();
            }
        });
        mindex++;
        //
        JMenuItem printItem = new JMenuItem(itemText[mindex]);
        printItem.setToolTipText(toolTipText[mindex]);
        printItem.setAccelerator(KeyStroke.getKeyStroke(itemKeys[mindex], KeyEvent.CTRL_MASK));
        printItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                print();
            }
        });
        //
        mindex++;
        JMenuItem quitItem = new JMenuItem(itemText[mindex]);
        quitItem.setToolTipText(toolTipText[mindex]);
        quitItem.setAccelerator(KeyStroke.getKeyStroke(itemKeys[mindex], KeyEvent.CTRL_MASK));
        quitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                quit();
            }
        });
        //
        //
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(saveItem);
        fileMenu.add(saveAsHtmlItem);
        fileMenu.add(printItem);
        fileMenu.addSeparator();
        fileMenu.addSeparator();
        fileMenu.add(quitItem);
        //
        _mbar = new JMenuBar();
        _mbar.add(fileMenu);
        return _mbar;
    }

    /**
     *
     */
    void print() {
        if (DEBUG) {
            System.out.println("Print");
        }
        Style s = null;
        try {
            s = _doc.getStyle("main");
            StyleConstants.setFontSize(s,
                6);  //CB changed to 6, from 7, to fit add'l lines from extending hydrology on same page
            Style dateStyle = _doc.getStyle("date style");
            if (dateStyle != null) {
                StyleConstants.setFontSize(dateStyle, 5);
            }
            GuiUtils.print(GuiUtils.getComponent(JTextPane.class, this));
        } catch (Exception e) {
            AppUtils.showDssFileErrorDialog(e);
        } finally {
            if (s != null) {
                s = _doc.getStyle("main");
                StyleConstants.setFontSize(s, 12);
                Style dateStyle = _doc.getStyle("date style");
                if (dateStyle != null) {
                    StyleConstants.setFontSize(dateStyle, 5);
                }
            }
        }
    }

    /**
     *
     */
    void save() {
        if (_doc != null) {
            try {
                String saveFile = VistaUtils.getFilenameFromDialog(this, FileDialog.SAVE,
                    "txt", "Text File");
                if (saveFile == null) {
                    return;
                }
                PrintWriter writer = new PrintWriter(new FileWriter(saveFile));
                String txt = _doc.getText(0, _doc.getLength());
                java.util.StringTokenizer st =
                    new java.util.StringTokenizer(txt, System.getProperty("line.separator"));
                while (st.hasMoreTokens()) {
                    String line = st.nextToken().trim();
                    writer.println(line);
                }
                writer.close();
            } catch (Exception e) {
                AppUtils.showDssFileErrorDialog(e);
            }
        }
        if (DEBUG) {
            System.out.println("Save");
        }

    }

    /**
     *
     */
    void saveAsHtml() {
        if (_doc != null) {
            try {
                String saveFile = VistaUtils.getFilenameFromDialog(this, FileDialog.SAVE,
                    "html", "HTML files");
                if (saveFile == null) {
                    return;
                }
                FileWriter writer = new FileWriter(saveFile);
                new MinimalHTMLWriter(writer, _doc).write();
                writer.close();
            } catch (Exception e) {
                AppUtils.showDssFileErrorDialog(e);
            }
        }
        if (DEBUG) {
            System.out.println("Save As Html");
        }
    }

    /**
     *
     */
    void quit() {
        if (DEBUG) {
            System.out.println("Quit");
        }
        JOptionPane.getFrameForComponent(this).dispose();
    }

    private StyledDocument _doc;
    private String _frameTitle = "REPORT";
    private JMenuBar _mbar;
}

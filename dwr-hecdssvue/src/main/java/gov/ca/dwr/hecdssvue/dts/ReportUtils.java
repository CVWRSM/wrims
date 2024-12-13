/*
 * Water Resource Integrated Modeling System (WRIMS) Copyright (c) 2024.
 *
 * WRIMS 2 is copyrighted by the State of California Department of Water Resources.
 * It is licensed under the Eclipse Public License, Version 1.0.
 * See Eclipse Public License for more details.
 */

package gov.ca.dwr.hecdssvue.dts;

import java.awt.Color;
import java.text.NumberFormat;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import org.python.modules.itertools.repeat;

/**
 * A set of utility functions for writing out text aligned in columns of a
 * line.
 *
 * @author Nicky Sandhu
 * @version $Id: ReportUtils.java,v 1.1.2.5 2001/07/12 01:58:31 amunevar Exp $
 */
public class ReportUtils {
    public static final int BEGIN_AT = 1;
    public static final int END_AT = 2;
    public static final int CENTER_AT = 3;
    public static String ls = System.getProperty("line.separator");
    static char _filler = ' ';

    /**
     * 1. Does not write out null strings
     * 2. Does not write out string if space is not sufficient.
     *
     * @param doc            The document to append to
     * @param columns        The columns containing the column value from the beginning of the line
     * @param strs           The strings to be inserted at the column
     * @param justifications The justification ( begin at, end at or center at ) for the string
     * @param styles         The style for the inserted string
     */
    public static void addLine(Document doc, int[] columns, String[] strs, int[] justifications, Style[] styles) {
        if (doc == null) {
            return;
        }
        if (columns == null || strs == null || justifications == null || styles == null) {
            return;
        }
        if (columns.length != strs.length) {
            return;
        }
        if (columns.length != justifications.length) {
            return;
        }
        if (columns.length != styles.length) {
            return;
        }
        // check to make sure that columns are in increasing order
        //
        int beginOffset = doc.getEndPosition().getOffset();
        int currentOffset = doc.getEndPosition().getOffset();
        for (int i = 0; i < columns.length; i++) {
            int currentCol = currentOffset - beginOffset;
            int col = columns[i];
            String str = strs[i];
            int offset = 0;
            if (str == null) {
                continue;
            }
            switch (justifications[i]) {
                case BEGIN_AT:
                    offset = col - currentCol;
                    break;
                case END_AT:
                    offset = col - currentCol - str.length();
                    break;
                case CENTER_AT:
                    offset = col - currentCol - str.length() / 2;
                    break;
                default:
                    throw new RuntimeException("Unrecognized type for justification: " +
                        justifications[i]);
            }
            if (offset < 0) {
                continue;
            }
            addString(doc, offset, str, styles[i]);
            currentOffset = doc.getEndPosition().getOffset();
        }
        // finally add the end of line marker
        addString(doc, 0, ls, styles[styles.length - 1]);
    }

    /**
     * This method checks to make sure the doc and string are non-null
     * and then inserts the string to the end of the document after pushing
     * offset number of spaces and in the given style.
     */
    public static void addString(Document doc, int offset, String str, Style s) {
        if (doc == null || str == null) {
            return;
        }
        try {
            if (offset > 0) {
                doc.insertString(doc.getEndPosition().getOffset() - 1, space(offset), s);
            }
            doc.insertString(doc.getEndPosition().getOffset() - 1, str, s);
        } catch (BadLocationException ble) {
            throw new RuntimeException("Hmm.. an internal insertion error @ " +
                doc.getEndPosition() + " for string: " + str);
        }
    }

    /**
     * makes a string with the given number of characters using the
     * the current filler character. Its a bit more efficient
     * than using repeat
     *
     * @param number The number of spaces in the string
     * @return the string of given number of spaces
     * @see repeat (int,String)
     */
    public static String space(int number) {
        if (number < 0) {
            return "";
        }
        StringBuffer buf = new StringBuffer(number);
        buf.setLength(number);
        for (int i = 0; i < number; i++) {
            buf.setCharAt(i, _filler);
        }
        return buf.toString();
    }

    /**
     * @return a style context of the given default styles.
     */
    public static StyleContext createDefaultStyles() {
        StyleContext styles = new StyleContext();
        // the main style
        Style smain = styles.addStyle("main", null);
        StyleConstants.setFontFamily(smain, "MonoSpaced");
        StyleConstants.setFontSize(smain,
            DEFAULT_FONT_SIZE); //CB chnaging font size here changes displayed size, NOT printed size
        // the normal style
        Style s = styles.addStyle("normal", smain); // normal style
        float space = StyleConstants.getLineSpacing(s);
        // the minimum value style
        Style smin = styles.addStyle("min", s);
        StyleConstants.setForeground(smin, MIN_ITEM_COLOR);
        StyleConstants.setBold(smin, true);
        // the maximum value style
        Style smax = styles.addStyle("max", s);
        StyleConstants.setForeground(smax, MAX_ITEM_COLOR);
        StyleConstants.setBold(smax, true);
        // the year header style
        Style sheader = styles.addStyle("header", s);
        StyleConstants.setBold(sheader, true);
        // the main header style
        Style mainHeaderStyle = styles.addStyle("main header", sheader);
        StyleConstants.setForeground(mainHeaderStyle, HEADER_COLOR);
        StyleConstants.setUnderline(mainHeaderStyle, true);
        // the right header style
        Style srheader = styles.addStyle("right header", s);
        StyleConstants.setBold(srheader, true);
        StyleConstants.setAlignment(srheader, StyleConstants.ALIGN_RIGHT);
        // water year style
        Style waterYearStyle = styles.addStyle("water year style", s);
        StyleConstants.setBold(waterYearStyle, true);
        StyleConstants.setForeground(waterYearStyle, WATER_YEAR_COLOR);
        // date style
        Style dateStyle = styles.addStyle("date style", s);
        StyleConstants.setFontSize(dateStyle, StyleConstants.getFontSize(dateStyle) - 2);
        StyleConstants.setAlignment(dateStyle, StyleConstants.ALIGN_RIGHT);
        space = StyleConstants.getSpaceAbove(dateStyle);
        StyleConstants.setSpaceAbove(dateStyle, space / 2);
        space = StyleConstants.getSpaceBelow(dateStyle);
        StyleConstants.setSpaceBelow(dateStyle, space / 2);
        StyleConstants.setItalic(dateStyle, true);
        //
        return styles;
    }

    /**
     *
     */
    static void initProps() {
        DEFAULT_FONT_SIZE = new Integer(AppProps.getProperty("ReportUtils.DEFAULT_FONT_SIZE"));
        HEADER_COLOR = vista.graph.GraphUtils.parseColorProperty
            (AppProps.getProperty("ReportUtils.HEADER_COLOR"));
        MIN_ITEM_COLOR = vista.graph.GraphUtils.parseColorProperty
            (AppProps.getProperty("ReportUtils.MIN_ITEM_COLOR"));
        MAX_ITEM_COLOR = vista.graph.GraphUtils.parseColorProperty
            (AppProps.getProperty("ReportUtils.MAX_ITEM_COLOR"));
        WATER_YEAR_COLOR = vista.graph.GraphUtils.parseColorProperty
            (AppProps.getProperty("ReportUtils.WATER_YEAR_COLOR"));
        // customize formatter for numbers
        _numberFormat.setGroupingUsed(Boolean.valueOf(AppProps.getProperty("ReportUtils.groupingUsed")));
        _numberFormat.setMinimumFractionDigits
            (new Integer(AppProps.getProperty("ReportUtils.minimumFractionDigits")));
        _numberFormat.setMinimumIntegerDigits
            (new Integer(AppProps.getProperty("ReportUtils.minimumIntegerDigits")));
        _numberFormat.setMaximumFractionDigits
            (new Integer(AppProps.getProperty("ReportUtils.maximumFractionDigits")));
    }

    /**
     * returns a string with the current formatter
     */
    public static String format(double d) {
        return _numberFormat.format(d);
    }

    /**
     * gets the desired space count for monthly tables (ie. column width)
     */
    public static int getSpaceCount() {
        return _spacecount;
    }


    private static final int _spacecount = 8;

    /**
     * sets the default font size
     */
    public static int DEFAULT_FONT_SIZE = 12;
    /**
     * this is the color for the min value in a line
     */
    public static Color HEADER_COLOR = new Color(128, 58, 0); //
    /**
     * this is the color for the min value in a line
     */
    public static Color MIN_ITEM_COLOR = Color.red;
    /**
     * this is the color for the max value in a line
     */
    public static Color MAX_ITEM_COLOR = Color.blue;
    /**
     * this is the color for the max value in a line
     */
    public static Color WATER_YEAR_COLOR = new Color(85, 107, 47); // Dark olive green
    /**
     * the default formatter for numbers
     */
    static NumberFormat _numberFormat = NumberFormat.getInstance();

    // initialize from properties
    static {
        initProps();
    }
}

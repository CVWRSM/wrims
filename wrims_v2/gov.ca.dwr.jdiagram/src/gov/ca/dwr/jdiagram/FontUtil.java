package gov.ca.dwr.jdiagram;

import javax.swing.JPanel;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;

public class FontUtil {
	
    private final static String Az = "ABCpqr";
    /** A dummy JPanel used to provide font metrics. */
    protected static final JPanel DUMMY_PANEL = new JPanel();

    public static FontData toSwtFontData(Device device, java.awt.Font font,
            boolean ensureSameSize) {
        FontData fontData = new FontData();
        fontData.setName(font.getFamily());
        // SWT and AWT share the same style constants.
        fontData.setStyle(font.getStyle());
        // convert the font size (in pt for awt) to height in pixels for swt
        int height = (int) Math.round(font.getSize() * 72.0
                / device.getDPI().y);
        fontData.setHeight(height);
        // hack to ensure the newly created swt fonts will be rendered with the
        // same height as the awt one
        if (ensureSameSize) {
            GC tmpGC = new GC(device);
            Font tmpFont = new Font(device, fontData);
            tmpGC.setFont(tmpFont);
            if (tmpGC.textExtent(Az).x
                    > DUMMY_PANEL.getFontMetrics(font).stringWidth(Az)) {
                while (tmpGC.textExtent(Az).x
                        > DUMMY_PANEL.getFontMetrics(font).stringWidth(Az)) {
                    tmpFont.dispose();
                    height--;
                    fontData.setHeight(height);
                    tmpFont = new Font(device, fontData);
                    tmpGC.setFont(tmpFont);
                }
            }
            else if (tmpGC.textExtent(Az).x
                    < DUMMY_PANEL.getFontMetrics(font).stringWidth(Az)) {
                while (tmpGC.textExtent(Az).x
                        < DUMMY_PANEL.getFontMetrics(font).stringWidth(Az)) {
                    tmpFont.dispose();
                    height++;
                    fontData.setHeight(height);
                    tmpFont = new Font(device, fontData);
                    tmpGC.setFont(tmpFont);
                }
            }
            tmpFont.dispose();
            tmpGC.dispose();
        }
        return fontData;
    }    
    
	public static java.awt.Font toAwtFont(Device device, FontData fontData,
            boolean ensureSameSize) {
        int height = (int) Math.round(fontData.getHeight() * device.getDPI().y
                / 72.0);
        // hack to ensure the newly created awt fonts will be rendered with the
        // same height as the swt one
        if (ensureSameSize) {
            GC tmpGC = new GC(device);
            Font tmpFont = new Font(device, fontData);
            tmpGC.setFont(tmpFont);
            JPanel DUMMY_PANEL = new JPanel();
            java.awt.Font tmpAwtFont = new java.awt.Font(fontData.getName(),
                    fontData.getStyle(), height);
            if (DUMMY_PANEL.getFontMetrics(tmpAwtFont).stringWidth(Az)
                    > tmpGC.textExtent(Az).x) {
                while (DUMMY_PANEL.getFontMetrics(tmpAwtFont).stringWidth(Az)
                        > tmpGC.textExtent(Az).x) {
                    height--;
                    tmpAwtFont = new java.awt.Font(fontData.getName(),
                            fontData.getStyle(), height);
                }
            }
            else if (DUMMY_PANEL.getFontMetrics(tmpAwtFont).stringWidth(Az)
                    < tmpGC.textExtent(Az).x) {
                while (DUMMY_PANEL.getFontMetrics(tmpAwtFont).stringWidth(Az)
                        < tmpGC.textExtent(Az).x) {
                    height++;
                    tmpAwtFont = new java.awt.Font(fontData.getName(),
                            fontData.getStyle(), height);
                }
            }
            tmpFont.dispose();
            tmpGC.dispose();
        }
        return new java.awt.Font(fontData.getName(), fontData.getStyle(),
                height);
    }

}

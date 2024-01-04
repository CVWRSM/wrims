package wrims.dss;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;

/**
 * 
 * @author Japanese programmer on the internet
 */
public class RowHeaderRenderer extends JLabel implements ListCellRenderer {
	/**
	 * 
	 * @param table
	 */
	public RowHeaderRenderer(JTable table) {
		JTableHeader header = table.getTableHeader();
		setOpaque(true);
		setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		setForeground(header.getForeground());
		setBackground(header.getBackground());
		// setBackground(table.getBackground()); // CB if blending in is desired
		// setFont(header.getFont());
		/*
		 * if (!header.getFont().isBold()) { setFont(new
		 * Font(header.getFont().getFontName(), header.getFont().getStyle() +
		 * Font.BOLD, header.getFont().getSize())); }
		 */
		setHorizontalAlignment(SwingConstants.LEFT);
		// prints 0 System.out.println("table header height = " +
		// table.getTableHeader().getHeight());
	}

	/**
	 *
	 */
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		setText((value == null) ? "" : value.toString());
		return this;
	}
}
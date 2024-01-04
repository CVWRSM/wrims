package wrims.schematic;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

/**
 * 
 * @author Clay Booher (after a Japanese programmer - name unknown)
 */
public class ComboRenderer extends JLabel implements ListCellRenderer {

	/**
	 *
	 */
	public ComboRenderer() {
		setOpaque(true);
		setBorder(new EmptyBorder(1, 1, 1, 1));
	}

	/**
	 *
	 */
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		if (isSelected) {
			// System.out.println("selected list item = " + value.toString());
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			// System.out.println("non-selected list item = " +
			// value.toString());
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		ComboItem comboValue;
		if (value != null) {
			if (value instanceof String) {
				comboValue = new ComboItem((String) value);
			} else {
				comboValue = (ComboItem) value;
			}
			if (!comboValue.isEnabled()) {
				setBackground(list.getBackground());
				setForeground(UIManager.getColor("Label.disabledForeground"));
			}
		}
		setFont(list.getFont());
		setText((value == null) ? "" : value.toString());
		return this;
	}
}
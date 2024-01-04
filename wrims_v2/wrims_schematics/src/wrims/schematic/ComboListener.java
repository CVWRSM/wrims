package wrims.schematic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

/**
 * Listens to list item selection changes (actions actually) and disallows the
 * attempted change if the user-selected item is disabled.
 * 
 * @author Clay Booher (after a Japanese programmer - name unknown)
 */
public class ComboListener implements ActionListener {
	JComboBox _combo;
	Object _currentItem;

	public ComboListener(JComboBox combo) {
		_combo = combo;
		// combo.setSelectedIndex(0);
		_currentItem = combo.getSelectedItem();
	}

	public void actionPerformed(ActionEvent e) {
		Object tempItem = _combo.getSelectedItem();
		ComboItem comboItem;
		if (tempItem instanceof String) {
			comboItem = new ComboItem((String) tempItem);
		} else {
			comboItem = (ComboItem) tempItem;
		}
		if (!comboItem.isEnabled()) {
			_combo.setSelectedItem(_currentItem);
		} else {
			_currentItem = tempItem;
		}
	}
}
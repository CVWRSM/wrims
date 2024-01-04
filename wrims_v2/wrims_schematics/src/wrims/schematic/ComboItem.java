package wrims.schematic;

/**
 * Class allows enabling/disabling of items in a list (e.g., a JComboBox model
 * list).
 * 
 * @author Clay Booher (after a Japanese programmer - name unknown)
 */
public class ComboItem { // needs to be public for ResultSetTableModel
	private Object _object;
	private boolean _isEnabled;

	/**
	 * 
	 * @param obj
	 * @param isEnable
	 */
	public ComboItem(Object object, boolean isEnabled) {
		_object = object;
		setEnabled(isEnabled);
	}

	/**
	 * 
	 * @param obj
	 */
	public ComboItem(Object object) {
		this(object, true);
	}

	/**
	 * 
	 * @return the Object wrapped
	 */
	Object getObject() {
		return _object;
	}

	/**
	 * Allows reuse of ComboItems so that it can have persistent properties.
	 * 
	 * @param the
	 *            Object to be wrapped
	 */
	public void setObject(Object object) {
		_object = object;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isEnabled() {
		return _isEnabled;
	}

	/**
	 * 
	 * @param isEnable
	 */
	public void setEnabled(boolean isEnabled) {
		_isEnabled = isEnabled;
	}

	/**
		 *
		 */
	public String toString() {
		if (_object == null) {
			return "";
		} else {
			return _object.toString();
		}
	}

	/**
	 * CDB added for correct JComboBox setSeletedItem & setSelectedIndex
	 * functionality
	 * 
	 * @return
	 */
	public boolean equals(ComboItem item) {
		if (this == item)
			return true;
		if (!(item instanceof ComboItem))
			return false;

		if (this.toString().equalsIgnoreCase(item.toString())) {
			return true;
		}
		return false;
	}

	/**
	 * Overrides hashCode (because equals is overridden).
	 */
	public int hashCode() {
		int result = 17;
		return result * 37 + _object.toString().hashCode();
	}
}
package gov.ca.dwr.hecdssvue.components;

import java.util.regex.Pattern;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import wrimsv2_plugin.debugger.exception.WPPException;

public class NumericTextField extends JTextField {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2314410705390899848L;
	private float minimum;
	private float maximum;
	//private static Logger log = Logger.getLogger(NumericTextField.class.getName());

	public NumericTextField() {
	}

	@Override
	protected Document createDefaultModel() {
		return new NumericDocument();
	}

	public void setMinVal(float minval) {
		minimum = minval;
	}

	public void setMaxVal(float maxval) {
		maximum = maxval;
	}

	public float getMinVal() {
		return minimum;
	}

	public float getMaxVal() {
		return maximum;
	}

	private class NumericDocument extends PlainDocument {
		/**
		 * 
		 */
		private static final long serialVersionUID = 234219857118535655L;
		// The regular expression to match input against (zero or more digits)
		private final Pattern DIGITS = Pattern.compile("(\\d*)|[0-9]{0,15}[.]{1}[0-9]{0,15}");

		@Override
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
			// Only insert the text if it matches the regular expression
			String s = super.getText(0, super.getLength());
			String s1 = s.substring(0, offs);
			String s2 = s.substring(offs, super.getLength());
			String sfinal = s1 + str + s2;
			str = str.trim(); // EDIT DKR 4/8/14 Corrected from previous "sfinal"

			try {
				float f = Float.valueOf(sfinal).floatValue();
				float min = getMinVal();
				float max = getMaxVal();

				if (str != null && DIGITS.matcher(str).matches() && f >= min && f <= max)
					super.insertString(offs, str, a); // EDIT DKR 4/8/14 from previous "sfinal"

			} catch (NumberFormatException e) {
				WPPException.handleException(e);
			}
		}
	}
}
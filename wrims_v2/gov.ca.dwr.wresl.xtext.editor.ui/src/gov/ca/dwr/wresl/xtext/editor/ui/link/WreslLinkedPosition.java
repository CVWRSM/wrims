package gov.ca.dwr.wresl.xtext.editor.ui.link;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.link.LinkedPosition;


public class WreslLinkedPosition extends LinkedPosition {
	
	public WreslLinkedPosition(IDocument document, int offset, int length) {
		super(document, offset, length);
	}

	public WreslLinkedPosition(IDocument document, int offset, int length, int sequence) {
		super(document, offset, length, sequence);
	}
	
	@Override
	public String getContent() throws BadLocationException {
		return super.getContent().toLowerCase();
	}
}

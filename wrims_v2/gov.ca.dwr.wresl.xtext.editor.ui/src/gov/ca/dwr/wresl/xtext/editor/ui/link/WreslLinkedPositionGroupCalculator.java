package gov.ca.dwr.wresl.xtext.editor.ui.link;

import static com.google.common.collect.Iterables.filter;
import static org.eclipse.xtext.util.Strings.notNull;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.refactoring.ui.DefaultLinkedPositionGroupCalculator;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

public class WreslLinkedPositionGroupCalculator extends
		DefaultLinkedPositionGroupCalculator {

	private static final Logger LOG = Logger.getLogger(DefaultLinkedPositionGroupCalculator.class);
	
	protected LinkedPositionGroup createLinkedGroupFromReplaceEdits(List<ReplaceEdit> edits, XtextEditor xtextEditor,
			final String originalName, SubMonitor progress) {
		if (edits == null)
			return null;
		final IXtextDocument document = xtextEditor.getDocument();
		LinkedPositionGroup group = new LinkedPositionGroup();
		Iterable<LinkedPosition> linkedPositions = filter(
				Iterables.transform(edits, new Function<ReplaceEdit, LinkedPosition>() {
					public LinkedPosition apply(ReplaceEdit edit) {
						try {
							String textToReplace = document.get(edit.getOffset(), edit.getLength());
							int indexOf = textToReplace.toLowerCase().indexOf(originalName.toLowerCase());
							if (indexOf != -1) {
								int calculatedOffset = edit.getOffset() + indexOf;
								return new WreslLinkedPosition(document, calculatedOffset, originalName.length());
							}
						} catch (BadLocationException exc) {
							LOG.error("Skipping invalid text edit " + notNull(edit), exc);
						}
						return null;
					}
				}), Predicates.notNull());
		progress.worked(10);
		final int invocationOffset = xtextEditor.getInternalSourceViewer().getSelectedRange().x;
		int i = 0;
		for (LinkedPosition position : sortPositions(linkedPositions, invocationOffset)) {
			try {
				position.setSequenceNumber(i);
				i++;
				group.addPosition(position);
			} catch (BadLocationException e) {
				LOG.error(e.getMessage(), e);
				return null;
			}
		}
		return group;
	}
}

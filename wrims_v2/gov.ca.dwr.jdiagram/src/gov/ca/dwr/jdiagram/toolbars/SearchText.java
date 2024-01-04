package gov.ca.dwr.jdiagram.toolbars;

import gov.ca.dwr.jdiagram.SchematicPluginCore;
import gov.ca.dwr.jdiagram.views.SchematicBase;
import gov.ca.dwr.jdiagram.views.SchematicView;

import java.awt.geom.Rectangle2D;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

import com.mindfusion.diagramming.DiagramView;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.tools.TimeOperation;

public class SearchText extends
    WorkbenchWindowControlContribution {
	
	SchematicBase schematicView;
	private Text text;
	private Button search;
	
	public SearchText(SchematicBase schematicBase) {
		this.schematicView=schematicBase;
	}

	@Override
	protected Control createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout glContainer = new GridLayout(2, false);
		glContainer.marginTop = 0;
		glContainer.marginHeight = 0;
		glContainer.marginWidth = 0;
		glContainer.marginBottom = 0;
		container.setLayout(glContainer);
		GridData glReader1 = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		glReader1.heightHint=20;
		search = new Button(container, SWT.BORDER|SWT.PUSH);
		search.setText("Search");
		Font font = new Font(container.getDisplay(), "Arial", 10, SWT.NONE );
		search.setFont(font);
		search.setLayoutData(glReader1);
		search.addMouseListener(new MouseListener(){

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseDown(MouseEvent e) {
				
			}

			@Override
			public void mouseUp(MouseEvent e) {
				String searchText=text.getText();
				schematicView.findInView(searchText);
			}
			
		});
		text=new Text(container, SWT.BORDER);
		text.setText("");
		GridData glReader2 = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		glReader2.widthHint=140;
		glReader2.heightHint=15;
		text.setLayoutData(glReader2);
		return container;
	}
}

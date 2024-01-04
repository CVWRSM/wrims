package gov.ca.dwr.jdiagram.dialog;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import gov.ca.dwr.hecdssvue.DssPluginCore;
import gov.ca.dwr.jdiagram.SchematicPluginCore;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.mindfusion.diagramming.Diagram;
import com.mindfusion.diagramming.export.PdfExporter;
import com.mindfusion.pdf.AutoScale;
import com.mindfusion.pdf.PageSizesEnum;

public class PDFOptionDialog extends Dialog {
	
	private String filename;
	private Diagram diagram;
	private Combo pageSize;
	private String[] pageSizeNames={"A0",                 
			"A1", 		    
			"A10", 		    
			"A2", 		    
			"A3", 		    
			"A4", 		    
			"A5", 		    
			"A6", 		    
			"A7", 		    
			"A8", 		    
			"A9", 		    
			"ArchA", 		    
			"ArchB", 		    
			"ArchC", 		    
			"ArchD", 		    
			"ArchE", 		    
			"B0", 		    
			"B1", 		    
			"B2", 		    
			"B3", 		    
			"B4", 		    
			"B5", 		    
			"Flsa", 		    
			"HalfLetter", 	    
			"Ledger", 		    
			"Legal", 		    
			"Letter", 		    
			"Letter11x17", 	    
			"Note"};
	private Map<String, PageSizesEnum> pageSizeMap = new HashMap<String, PageSizesEnum>(){{
		put("A0", PageSizesEnum.A0);               
		put("A1", PageSizesEnum.A1);
		put("A10", PageSizesEnum.A10);
		put("A2", PageSizesEnum.A2);
		put("A3", PageSizesEnum.A3);
		put("A4", PageSizesEnum.A4);
		put("A5", PageSizesEnum.A5);
		put("A6", PageSizesEnum.A6);
		put("A7", PageSizesEnum.A7);
		put("A8", PageSizesEnum.A8);
		put("A9", PageSizesEnum.A9);
		put("ArchA", PageSizesEnum.ArchA);
		put("ArchB", PageSizesEnum.ArchB);
		put("ArchC", PageSizesEnum.ArchC);
		put("ArchD", PageSizesEnum.ArchD);
		put("ArchE", PageSizesEnum.ArchE);
		put("B0", PageSizesEnum.B0);
		put("B1", PageSizesEnum.B1);
		put("B2", PageSizesEnum.B2);
		put("B3", PageSizesEnum.B3);
		put("B4", PageSizesEnum.B4);
		put("B5", PageSizesEnum.B5);
		put("Flsa", PageSizesEnum.Flsa);
		put("HalfLetter", PageSizesEnum.HalfLetter);
		put("Ledger", PageSizesEnum.Ledger);
		put("Legal", PageSizesEnum.Legal);
		put("Letter", PageSizesEnum.Letter);
		put("Letter11x17", PageSizesEnum.Letter11x17);
		put("Note", PageSizesEnum.Note);
	}};
	
	public PDFOptionDialog(Shell parent, String filename, Diagram diagram) {
		super(parent, SWT.MIN);
		this.filename=filename;
		this.diagram=diagram;
		setText("PDF Options");
	}

	public void openDialog(){
		Shell shell=new Shell(getParent(), getStyle());
		shell.setText(getText());
		createContents(shell);
		shell.setSize(400, 150);
		shell.setLocation(450, 300);
		//shell.pack();
		shell.open();
	}
	
	protected void createContents(final Shell shell) {
		GridLayout layout=new GridLayout();
		layout.numColumns = 14;
		layout.makeColumnsEqualWidth = true;
		layout.marginWidth=20;
		layout.marginHeight=20;
		shell.setLayout(layout);
		
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.horizontalSpan=6;
		Label label = new Label(shell, SWT.NULL);
		label.setText("Page Size:");
		label.setLayoutData(gd);
		
		pageSize=new Combo(shell, SWT.BORDER);
		for (int i=0; i<pageSizeNames.length; i++){
			pageSize.add(pageSizeNames[i]);
		}
		pageSize.select(0);
		GridData gd1 = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd1.horizontalSpan=8;
		pageSize.setLayoutData(gd1);
		
		Button ok = new Button(shell, SWT.PUSH);
		GridData gd2 = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd2.horizontalSpan=3;
		ok.setLayoutData(gd2);
		ok.setText("OK");
		ok.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				SchematicPluginCore.pageSize=pageSizeMap.get(pageSize.getText());
				PdfExporter pdfExp = new PdfExporter();
				pdfExp.setPageSize(SchematicPluginCore.pageSize);
				pdfExp.setAutoScale(AutoScale.FitToPage);
				//pdfExp.setScale(SchematicPluginCore.scale);
				pdfExp.export(diagram, filename);
				shell.close();
			}
		});
		
		Button cancel = new Button(shell, SWT.PUSH);
		cancel.setLayoutData(gd2);
		cancel.setText("Cancel");
		cancel.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				shell.close();
			}
		});
		
		shell.setDefaultButton(ok);
		shell.pack();
	}

}

package wrimsv2_plugin.debugger.dialog;

import java.text.Collator;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.eclipse.debug.core.DebugException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;
import wrimsv2_plugin.debugger.listener.TableCopyListener;
import wrimsv2_plugin.debugger.model.WPPValue;
import wrimsv2_plugin.debugger.view.ProcessAltColumn;
import wrimsv2_plugin.tools.DataProcess;

public class WPPWeightDialog extends Dialog {
	
	private boolean firstAlt=false;
	
	public WPPWeightDialog(Shell parent) {
		super(parent, SWT.MIN|SWT.RESIZE);
		setText("Weighted Variables");
	}
	
	public void openDialog(){
		Shell shell=new Shell(getParent(), getStyle());
		shell.setText(getText());
		createContents(shell);
		shell.setSize(820, 520);
		shell.setLocation(450, 300);
		//shell.pack();
		shell.open();
	}
	
	protected void createContents(final Shell shell) {
		GridLayout layout=new GridLayout();
		layout.numColumns=1;
		layout.marginHeight=15;
		layout.marginWidth=20;
		shell.setLayout(layout);
				
		Table table = new Table(shell, SWT.MULTI|SWT.FULL_SELECTION);
		GridData gd1=new GridData(750, 390);
		table.setLayoutData(gd1);
	    constructTable(table);
	    
	    Composite okCancel=new Composite(shell, SWT.NONE);
		okCancel.setLayout(layout);
		Button ok = new Button(okCancel, SWT.PUSH);
		ok.setText("OK");
		GridData gd3 = new GridData(100, 25);
		gd3.horizontalSpan = 15;
		ok.setLayoutData(gd3);
		ok.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){
				shell.close();
			}
		});
	    
		shell.pack();
	 }
	
	public void constructTable(final Table table){
		
	    final TableColumn tc1 = new TableColumn(table, SWT.LEFT);
		tc1.setText("Variable");
		tc1.setWidth(150);
		tc1.setResizable(true);
		
		
		final TableColumn tc2 = new TableColumn(table, SWT.LEFT);
	    tc2.setText("Weight");
	    tc2.setWidth(150);
		tc2.setResizable(true);
		
		final TableColumn tc3 = new TableColumn(table, SWT.LEFT);
	    tc3.setText("Value");
	    tc3.setWidth(150);
		tc3.setResizable(true);
	    
		Map<Integer, Integer> variableAltColIndex = DebugCorePlugin.variableAltColIndex;
		if (variableAltColIndex.size()>0) firstAlt=true;
		
		final TableColumn tc4 = new TableColumn(table, SWT.LEFT);
		if (firstAlt){
			int i=variableAltColIndex.get(2)+1;
			tc4.setText("Alt"+i);
		}else{
			tc4.setText("Alt");
		}
		tc4.setWidth(150);
		tc4.setResizable(true);

		final TableColumn tc5 = new TableColumn(table, SWT.LEFT);
		tc5.setText("Obj Change");
		tc5.setWidth(150);
		tc5.setResizable(true);
		
	    table.setHeaderVisible(true);
	    table.setLinesVisible(true);
	    
	    TableCopyListener tcl=new TableCopyListener(table);
	    table.addKeyListener(tcl);
	    
	    /*
	    table.addKeyListener(new KeyListener() {
            
			@Override
	    	public void keyReleased(KeyEvent e) {
                
            }

			@Override
			public void keyPressed(KeyEvent e) {
				String lineSep=DebugCorePlugin.lineSep;
				String tab=DebugCorePlugin.tab;
				
				if((e.stateMask == SWT.CTRL) && (e.keyCode == 'c'))
                {
                	Clipboard cb = new Clipboard(getShell().getDisplay());
                	TextTransfer textTransfer = TextTransfer.getInstance();
                	TableItem[] ti = table.getSelection();
                	String[] data=new String[1];
                	Transfer[] transfer=new Transfer[1];
               		transfer[0]=textTransfer;
                	TableColumn[] tc = table.getColumns();
                	data[0]="";
                	int tcl=tc.length;
                	for (int i=0; i<tcl; i++){
                		data[0]=data[0]+tc[i].getText()+tab;
                	}
                	data[0]=data[0]+lineSep;
                	for (int i=0; i<ti.length; i++){
                		TableItem tableItem = ti[i];
                		for (int j=0; j<tcl; j++){
                			data[0]=data[0]+ti[i].getText(j)+tab;
                		}
                		data[0]=data[0]+lineSep;
                	}
                	cb.setContents(data, transfer);
                	cb.dispose();
                }		
			}
	    });
	    */
	    
	    Listener nameSortListener = new Listener() {  
            
			@Override
			public void handleEvent(Event e) {
				 TableItem[] items = table.getItems();
				 Collator collator = Collator.getInstance(Locale.getDefault());  
	             TableColumn column = (TableColumn)e.widget;  
	             int index=0;
	             
	             for (int i = 1; i < items.length; i++) {  
	                 String value1 = items[i].getText(index);  
	                 for (int j = 0; j < i; j++){  
	                     String value2 = items[j].getText(index);  
	                     if (collator.compare(value1, value2) < 0) {  
	                         String[] values = {items[i].getText(0), items[i].getText(1), items[i].getText(2), items[i].getText(3), items[i].getText(4)};  
	                         items[i].dispose();  
	                         TableItem item = new TableItem(table, SWT.NONE, j);  
	                         item.setText(values);  
	                         items = table.getItems();  
	                         break;  
	                     }
	                 }  
	             }  
	             table.setSortColumn(column);  
	        } 
	     };  
	     
		 Listener valueSortListener = new Listener() {  
	            
				@Override
				public void handleEvent(Event e) {
					 TableItem[] items = table.getItems();
					 Collator collator = Collator.getInstance(Locale.getDefault());  
		             TableColumn column = (TableColumn)e.widget;  
		             int index;
		             if (column==tc2){
		            	 index=1;
		             }else if (column == tc3){
		            	 index=2;
		             }else if (column == tc4){
		            	 index=3;
		             }else{
		            	 index=4;
		             }
		             
		             for (int i = 1; i < items.length; i++) {
		            	 double value1, value2;
		            	 String value1Str = items[i].getText(index);
		            	 if (value1Str.equals("")){
		            		 value1 = 0.0;
		            	 }else{
		            		 value1 = Double.parseDouble(value1Str);
		            	 }
		                 for (int j = 0; j < i; j++){
		                	 String value2Str = items[j].getText(index);
		                	 if (value2Str.equals("")){
			            		 value2 = 0.0;
			            	 }else{
			            		 value2 = Double.parseDouble(value2Str);
			            	 }
		                     if (value1 > value2) {
		                    	 String[] values = {items[i].getText(0), items[i].getText(1), items[i].getText(2), items[i].getText(3), items[i].getText(4)};  
		                         items[i].dispose();  
		                         TableItem item = new TableItem(table, SWT.NONE, j);  
		                         item.setText(values);  
		                         items = table.getItems(); 
		                         break;
		                     }
		                 }  
		             }  
		             table.setSortColumn(column);  
		        } 
		     };  
		 tc1.addListener(SWT.Selection, nameSortListener);
		 tc2.addListener(SWT.Selection, valueSortListener);
		 tc3.addListener(SWT.Selection, valueSortListener);
		 tc4.addListener(SWT.Selection, valueSortListener);
		 tc5.addListener(SWT.Selection, valueSortListener);
	     
		 fillData(table);
		 table.redraw();
	}
	
	public void fillData(final Table table){

		Map<String, String> weightedVariableMap = DataProcess.retrieveWeightedVariables();
				
		DebugCorePlugin.allVariableProperty = DataProcess.retrieveAllVariableProperty();
		WPPValue[] allVarStack = (WPPValue[])DebugCorePlugin.allVariableStack;
		int allVarStackSize=allVarStack.length;
		String value="";
		Set<String> keys = weightedVariableMap.keySet();
		Iterator it=keys.iterator();
		while (it.hasNext()){
			String var=(String) it.next();
			String wt=weightedVariableMap.get(var);
			TableItem ti=new TableItem(table, SWT.NONE);
			ti.setText(0, var);
			ti.setText(1, wt);
			boolean notFound=true;
			int j=0;
			while (j<allVarStackSize && notFound){
				value="";
				if (allVarStack[j].getVariableString().equals(var)){
					try {
						value=allVarStack[j].getValueString();
						ti.setText(2, value);
						notFound=false;
					} catch (DebugException e) {
						WPPException.handleException(e);
					}
				}
				j++;
			}
			if (firstAlt){
				String alt=ProcessAltColumn.addAltColumnData(var, 2, 2);
				ti.setText(3, alt);
				if (!alt.equals("")){
					double objChange=(Double.parseDouble(value)-Double.parseDouble(alt))*Double.parseDouble(wt);
					ti.setText(4, DebugCorePlugin.df.format(objChange));
				}
			}
		}
	}
}

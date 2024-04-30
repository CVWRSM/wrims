package wrimsv2_plugin.debugger.listener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;

public class TableCursorCopyListener implements KeyListener {

	private Table table;
	
	public TableCursorCopyListener(Table table){
		this.table=table;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		String lineSep=DebugCorePlugin.lineSep;
		String tab=DebugCorePlugin.tab;
		
		if((e.stateMask == SWT.CTRL) && (e.keyCode == 'c'))
        {
        	Clipboard cb = new Clipboard(table.getShell().getDisplay());
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
        } else if ((e.stateMask == SWT.CTRL) && (e.keyCode == 'a')){
        	table.selectAll();
        }
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}

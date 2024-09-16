package wrimsv2_plugin.tools;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Table;

import wrimsv2_plugin.debugger.model.WPPValue;

public class SetSelectionInTable {
	public SetSelectionInTable(IStructuredSelection oldSelection, TableViewer viewer, Table table){
		int[] indices=new int[0];
		table.select(indices);
		ArrayList<String> oldSelections = new ArrayList<String>();
		ArrayList<Integer> oldSelectionIndices=new ArrayList<Integer>();
    	int i=0;
    	if (oldSelection.isEmpty()){
    		table.setTopIndex(0);
    	}else{
    		Iterator iterator = oldSelection.iterator();
    		while (iterator.hasNext()){
    			oldSelections.add(((WPPValue)(iterator.next())).getVariableString());
    		}
    		Object element;
    		while ((element=viewer.getElementAt(i))!=null ){
    			if (oldSelections.contains(((WPPValue)element).getVariableString())){
        			oldSelectionIndices.add(i);
    			}
    			i=i+1;
    		}
    		int size = oldSelectionIndices.size();
    		if (size>0){
    			indices=new int[size];
    			for (int j=0; j<size; j++){
    				indices[j]=oldSelectionIndices.get(j);
    			}
    			table.select(indices);
    			table.setTopIndex(oldSelectionIndices.get(0));
    		}else{
    			table.setTopIndex(0);
    		}
    	}
	}
}

package wrimsv2_plugin.tools;

import java.util.Stack;

import org.eclipse.debug.core.DebugException;
import org.eclipse.swt.custom.TableTreeItem;
import org.eclipse.swt.widgets.TableItem;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;
import wrimsv2_plugin.debugger.model.WPPValue;

public class SearchTable {
	public static int search(TableItem[] tableItems, int bi, int ei, String text, boolean exact, boolean includeValue){
		text=text.toLowerCase();
		int foundIndex=-1;
		Stack indexStack=new Stack();
		int i=bi;
		while (i<ei && foundIndex==-1){
			TableItem item = tableItems[i];
			String itemString=item.getText();
			try {
				String valueString=((WPPValue)(item.getData())).getValueString();
				if (exact){
					if (includeValue){
						if (itemString.equals(text) || valueString.equals(text)){
							foundIndex=i;
						}
					}else{
						if (itemString.equals(text)){
							foundIndex=i;
						}
					}
				}else{
					if (includeValue){
						if (itemString.contains(text) || valueString.contains(text)){
							foundIndex=i;
						}
					}else{
						if (itemString.contains(text)){
							foundIndex=i;
						}
					}
				}
			} catch (DebugException e) {
				WPPException.handleException(e);
			}
			i=i+1;
		}
		return foundIndex;
	}
}

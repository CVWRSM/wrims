package wrimsv2_plugin.debugger.view;

import hec.gfx2d.G2dObject;
import hec.gfx2d.G2dPanel;
import hec.gfx2d.PairedDataSet;
import hec.gfx2d.TimeSeriesDataSet;
import hec.io.DataContainer;
import hec.io.PairedDataContainer;
import hec.io.TimeSeriesContainer;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Panel;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JRootPane;

import org.eclipse.debug.core.DebugException;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ViewPart;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.tools.DataProcess;

public class WPPVarMonitorView extends ViewPart{

	protected Container contentPane;
	G2dPanel plot;
	
	public void createPartControl(Composite parent) {
		System.setProperty("sun.awt.noerasebackground", "true");
		Composite swingContainer = new Composite(parent, SWT.BACKGROUND
				| SWT.EMBEDDED | SWT.NONE);
		final Frame frame = SWT_AWT.new_Frame(swingContainer);
		@SuppressWarnings("serial")
		Panel panel = new Panel(new BorderLayout()) {
			public void update(java.awt.Graphics g) {
				/* Do not erase the background */
				paint(g);
			}
		};
		frame.add(panel);
		JRootPane root = new JRootPane();
		panel.add(root);
		contentPane = root.getContentPane();
	}
	
	public void updatePlot(String dataString){
		Vector<TimeSeriesContainer> dataVector=new Vector<TimeSeriesContainer>();
		boolean correctVarNames=true;
		String[] variables=dataString.split("!");
		boolean hasData=false;
		for (int k=0; k<variables.length; k++){
			String[] nameData=variables[k].split("\\$");
			if (nameData.length==2){
				dataVector.add(convertDataVector(nameData[1], nameData[0]));
				hasData=true;
			}
		}
		
		if (!hasData) return;
		
		G2dObject g2dObj = null;
		Vector g2dataVector = new Vector();
		for (Iterator iterator = dataVector.iterator(); iterator.hasNext();) {
			DataContainer data = (DataContainer) iterator.next();
			if (data instanceof TimeSeriesContainer) {
				TimeSeriesDataSet ts = new TimeSeriesDataSet(
					(TimeSeriesContainer) data);
				if (((TimeSeriesContainer) data).timeZoneID != null) {
					ts.setGmtOffset(((TimeSeriesContainer) data).timeZoneRawOffset
						/ (1000 * 60 * 60));
				}
				g2dObj = ts;
			} else if (data instanceof PairedDataContainer) {
				PairedDataSet pd = new PairedDataSet((PairedDataContainer) data);
				g2dObj = pd;
			}
			if (g2dObj == null) {
				return;
			}
			g2dataVector.add(g2dObj);
		}
		
		if (plot == null) {
			plot = new G2dPanel();
			contentPane.add(plot);
			plot.buildComponents(g2dataVector);
		}else{
			plot.setPlotObjects(g2dataVector);
		}
		plot.repaint();
		
	}
	
	protected TimeSeriesContainer convertDataVector(
			String dataString, String varName) {
		ArrayList<String[]> varDetailTimeseries=DataProcess.generateVarDetailData(dataString);
		Vector<TimeSeriesContainer> vdc=new Vector<TimeSeriesContainer> ();		
		
		TimeSeriesContainer dc=new TimeSeriesContainer();
		Date startDate=new Date(DebugCorePlugin.startYear-1900, DebugCorePlugin.startMonth-1, DebugCorePlugin.startDay);
		Date endDate=new Date(DebugCorePlugin.endYear-1900, DebugCorePlugin.endMonth-1, DebugCorePlugin.endDay);
		dc.startTime=(int)(startDate.getTime()/60000)+25568*1440;
		dc.endTime=(int)(endDate.getTime()/60000)+25568*1440;
		dc.units="undefined";
		dc.location=varName;
		int size = varDetailTimeseries.size();
		dc.numberValues=size+1;
		dc.times=new int[size+1];
		dc.values=new double[size+1];
		for (int i=0; i<size; i++){
			String[] varItem=varDetailTimeseries.get(i);
			String[] time=varItem[1].split("-");
			Date date=new Date(Integer.parseInt(time[2])-1900, Integer.parseInt(time[0])-1, Integer.parseInt(time[1]));
			dc.times[i]=(int)(date.getTime()/60000)+25568*1440;
			dc.values[i]=Double.parseDouble(varItem[2]);
		}
		if (dc.times[size-1]<dc.endTime){
			dc.times[size]=dc.endTime;
			dc.values[size]=-901.0;
		}else{
			dc.times[size]=dc.endTime+1;
			dc.values[size]=-901.0;
		}
		
		return dc;
	}
	
	@Override
	public void setFocus() {
		getSite().getPart().setFocus();
	}

}

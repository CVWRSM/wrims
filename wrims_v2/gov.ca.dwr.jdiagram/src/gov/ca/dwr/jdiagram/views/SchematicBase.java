package gov.ca.dwr.jdiagram.views;

import gov.ca.dwr.hecdssvue.DssPluginCore;
import gov.ca.dwr.hecdssvue.views.DSSCatalogView;
import gov.ca.dwr.hecdssvue.views.DSSMonthlyView;
import gov.ca.dwr.hecdssvue.views.DSSPlotView;
import gov.ca.dwr.hecdssvue.views.DSSTableView;
import gov.ca.dwr.jdiagram.Activator;
import gov.ca.dwr.jdiagram.FontUtil;
import gov.ca.dwr.jdiagram.RectangleZoomBehavior;
import gov.ca.dwr.jdiagram.SchematicPluginCore;
import gov.ca.dwr.jdiagram.dialog.DecimalDialog;
import gov.ca.dwr.jdiagram.dialog.PDFOptionDialog;
import gov.ca.dwr.jdiagram.panel.MagnifierPanel;
import gov.ca.dwr.jdiagram.toolbars.DateCombo;
import gov.ca.dwr.jdiagram.toolbars.SearchText;
import hec.heclib.dss.CondensedReference;
import hec.heclib.dss.DSSPathname;
import hec.heclib.dss.HecDss;
import hec.heclib.util.HecTime;
import hec.hecmath.HecMath;
import hec.hecmath.HecMathException;
import hec.io.DataContainer;
import hec.io.TimeSeriesContainer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.prefs.Preferences;

import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import wrimsv2_plugin.debugger.core.DebugCorePlugin;
import wrimsv2_plugin.debugger.exception.WPPException;
import wrimsv2_plugin.tools.TimeOperation;

import com.mindfusion.diagramming.AttachToNode;
import com.mindfusion.diagramming.Behavior;
import com.mindfusion.diagramming.Diagram;
import com.mindfusion.diagramming.DiagramAdapter;
import com.mindfusion.diagramming.DiagramItem;
import com.mindfusion.diagramming.DiagramLink;
import com.mindfusion.diagramming.DiagramLinkList;
import com.mindfusion.diagramming.DiagramNode;
import com.mindfusion.diagramming.DiagramNodeList;
import com.mindfusion.diagramming.DiagramView;
import com.mindfusion.diagramming.Group;
import com.mindfusion.diagramming.InplaceEditable;
import com.mindfusion.diagramming.LinkEvent;
import com.mindfusion.diagramming.NodeEvent;
import com.mindfusion.diagramming.Overview;
import com.mindfusion.diagramming.ShapeNode;
import com.mindfusion.diagramming.export.PdfExporter;
import com.mindfusion.diagramming.export.SvgExporter;
import com.mindfusion.drawing.Align;
import com.mindfusion.drawing.TextFormat;
import com.mindfusion.pdf.AutoScale;

/**
 * This view represents the schematic drawing
 */

public abstract class SchematicBase extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	private DiagramView diagramView;

	private Composite swingContainer;
	
	private java.awt.Container contentPane;

	private Diagram diagram;

	protected DiagramAdapter listener;
	
	private Action zoomInAction;

	private Action zoomOutAction;
	
	private Action zoomRectAction;
	
	private Action zoomMagnifier;

	private Action openSchematicAction;
	
	private Action saveAsSchematicAction;

	private float _zoomFactor = 100;

	private Action zoomNormalAction;
	
	private Action fontAction;
	
	private Action decimalAction;
	
	private Action tafTypeAction;
	
	private Action forwardAction;
	
	private Action backwardAction;

	private DiagramSelectionProvider selectionProvider;

	private Rectangle2D.Float lastVisibleRect;

	protected DateCombo dateCombo;
	
	private static final Object VALUE_TEXT = "__VT__";
	
	//private static int precision = 0;
	
	private static double FACTOR = 1000. * 43560 / (24 * 60 * 60);
	
	private int index_lastnode = 0;
	
	private int baseIndex = 0;

	private SearchText searchText;

	private SchematicOverview overview;
	
	private static String DECIMAL_PLACES = "decimalPlaces";
	
	private static String ISMONTHLYTAF="ismonthlytaf";

	/**
	 * The constructor.
	 */
	public SchematicBase() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@SuppressWarnings("serial")
	public void createPartControl(Composite parent) {
		System.setProperty("sun.awt.noerasebackground", "true");
		swingContainer = new Composite(parent, SWT.BACKGROUND | SWT.EMBEDDED);
		Frame frame = SWT_AWT.new_Frame(swingContainer);
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
		//JLayeredPane layeredPane = new JLayeredPane();
		//contentPane.add(layeredPane);
		diagramView = new DiagramView(diagram = new Diagram());
		DiagramView.setLicenseKey("AQAAADEAAAAoAAAAAQAACBsIvJ6TqJ6Lmo2ylpGbuYqMlpCR0buWnpiNnpKSlpGY0bWeiZ5uSnEFZ5kp95H1RGyzvJ0ujmsyQtAvHsv6pq5QSIn+bYJpUI0gl6+sIDhBXOtsGtA=");
		diagramView.setAllowInplaceEdit(false);
		diagramView.setBehavior(Behavior.Pan);
		getSite().setSelectionProvider(
				selectionProvider = new DiagramSelectionProvider());
		diagram.addDiagramListener(listener=new DiagramAdapter() {
			private Timer refreshTimer = new Timer();
			private TimerTask refreshTask = new TimerTask(){
				@Override
				public void run() {					
				}};
				
			private int delay = 250;
			
			
			@Override
			public void linkClicked(LinkEvent e) {
				selectionProvider.setSelection(new DiagramItemSelection(e
						.getLink()));
			}

			@Override
			public void linkDoubleClicked(LinkEvent e) {
				showDSS(e.getLink().getTextToEdit());
			}

			@Override
			public void nodeClicked(NodeEvent e) {
				selectionProvider.setSelection(new DiagramItemSelection(e
						.getNode()));
			}

			@Override
			public void nodeDoubleClicked(NodeEvent e) {
				DiagramNode n = e.getNode();
				showDSS(((InplaceEditable)n).getTextToEdit());
			}
			
			public void openDSSPerspective(final String perspectiveId, final String name) { 
				final IWorkbench workbench = PlatformUI.getWorkbench();
				workbench.getDisplay().asyncExec(new Runnable(){
					public void run(){
						IWorkbenchWindow workBenchWindow = workbench.getActiveWorkbenchWindow();
						try {
							workbench.showPerspective(perspectiveId, workBenchWindow);
							DSSCatalogView dcv = (DSSCatalogView)workbench.getActiveWorkbenchWindow().getActivePage().findView(DSSCatalogView.ID);
							Table table = dcv.getViewer().getTable();							
							int size = table.getItemCount();
							for (int i=0; i<size; i++){
								TableItem ti = table.getItem(i);
								if (ti.getText(1).equalsIgnoreCase(name)){
									table.setSelection(i);
									table.showItem(ti);
								}
							}
							ISelectionProvider selProvider = dcv.getSite().getSelectionProvider();
							selProvider.setSelection(selProvider.getSelection());
						} catch (WorkbenchException e) {
						}
					}
				});
			} 

			public void showDSS(String name){
				final Vector<DataContainer> data=new Vector<DataContainer>();
				for (int i=0; i<4; i++){
					if (DssPluginCore.allSchematicVariableData[i].containsKey(name)){
						try {
							data.add(DssPluginCore.allSchematicVariableData[i].get(name).getData());
						} catch (HecMathException e) {
						}
					}
				}
				final IWorkbench workbench = PlatformUI.getWorkbench();
				workbench.getDisplay().asyncExec(new Runnable(){
					public void run(){
						IWorkbenchPage activePage = workbench.getActiveWorkbenchWindow().getActivePage();
						try {
							DSSMonthlyView dmv= (DSSMonthlyView)activePage.showView(DSSMonthlyView.ID);
							dmv.showSelected(data);
							
							DSSPlotView dpv= (DSSPlotView)activePage.showView(DSSPlotView.ID);
							dpv.showSelected(data);
							
							DSSTableView dtv= (DSSTableView)activePage.showView(DSSTableView.ID);
							dtv.showSelected(data);
						} catch (PartInitException e) {
						}
					}
				});
			}
			
			@Override
			public void viewportChanged() {
				refreshTask.cancel();
				refreshTimer.cancel();
				refreshTimer = new Timer();
				refreshTask = new TimerTask() {

					@Override
					public void run() {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								try {
									// diagramView.suspendRepaint();
									// overview.suspendRepaint();
									if (!DebugCorePlugin.isDebugging){
										refreshValues(0, false);					
									}else{
										if (DebugCorePlugin.target !=null){
											refreshValues(0, false);
										}
									}
								} finally {
									// diagramView.resumeRepaint();
									// overview.resumeRepaint();
								}

							}
						});
					}

				};
				refreshTimer.schedule(refreshTask, delay);
			}

		});
		contentPane.add(new JScrollPane(diagramView));
		//Overview overview = new Overview();
		//overview.setDiagramView(diagramView);
		//layeredPane.add(overview,1);
		IWorkbenchPage activePage = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		if (activePage != null) {
			IViewPart sview = activePage.findView(SchematicOverview.ID);
			if (sview != null) {
				overview = (SchematicOverview) sview;
				overview.setDiagramView(diagramView);
			}
		}
		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(swingContainer, "gov.ca.dwr.calsim.jdiagram.viewer");
		makeActions();
		contributeToActionBars();
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(openSchematicAction);
		manager.add(saveAsSchematicAction);
		manager.add(zoomInAction);
		manager.add(zoomOutAction);
		manager.add(zoomNormalAction);
		manager.add(zoomRectAction);
		manager.add(zoomMagnifier);
		manager.add(fontAction);
		manager.add(decimalAction);
		manager.add(tafTypeAction);
		dateCombo=new DateCombo(this);
		manager.add(dateCombo);
		manager.add(backwardAction);
		manager.add(forwardAction);
		searchText=new SearchText(this);
		manager.add(searchText);
	}

	@SuppressWarnings("deprecation")
	private void makeActions() {
		openSchematicAction = new Action("Open", PlatformUI.getWorkbench()
				.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OPEN_MARKER)) {
			public void run() {
				FileDialog dialog = new FileDialog(swingContainer.getShell(),
						SWT.OPEN);
				dialog.setFilterExtensions(new String[] { "*.xml", "*.cht" });
				final String file = dialog.open();
				if (file == null) {
					return;
				}
				
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							diagramView.suspendRepaint();
							if (file.endsWith(".xml")) {
								diagram.loadFromXml(file);
							}else {
								diagram.loadFrom(file);
							}
							Rectangle2D rect = diagram.getBounds();
							setupForEditor();
							diagramView.zoomToFit(rect);
							diagramView.resumeRepaint();
							collectAllSchematicVariables();
							loadAllSchematicVariableData();
						} catch (Exception ex) {
							WPPException.handleException(ex);
						}
					}
					
				});
			}
		};
		
		saveAsSchematicAction = new Action("Save As", PlatformUI.getWorkbench()
				.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_ETOOL_SAVEAS_EDIT)) {
			public void run() {
				FileDialog dialog = new FileDialog(swingContainer.getShell(),
						SWT.SAVE);
				dialog.setFilterExtensions(new String[] { "*.xml", "*.pdf", "*.svg","*.*"});
				String file = dialog.open();
				if (file == null) {
					return;
				}
				try {
					save(file);
				} catch (Exception e) {
					WPPException.handleException(e);
				}
			}
		};
		
		zoomNormalAction = new Action("Zoom 100",
				Activator.getImageDescriptor("ZoomNormal24.gif")) {

			public void run() {
				_zoomFactor = 100;
				diagramView.zoomToFit(); 
				overview.setDiagramView(diagramView);
			};
		};
		zoomInAction = new Action("Zoom In",
				Activator.getImageDescriptor("ZoomIn24.gif")) {

			public void run() {
				//_zoomFactor *= 2;
				//diagramView.setZoomFactor(_zoomFactor);
				Rectangle2D.Float rect = diagramView.deviceToDoc(diagramView.getVisibleRect());
				rect.setRect(rect.x+rect.width/4, rect.y+rect.height/4, rect.width/2, rect.height/2);
				diagramView.zoomToFit(rect);
				overview.setDiagramView(diagramView);
			};
		};
		zoomOutAction = new Action("Zoom Out",
				Activator.getImageDescriptor("ZoomOut24.gif")) {

			public void run() {
				//_zoomFactor /= 2;
				Rectangle2D.Float rect = diagramView.deviceToDoc(diagramView.getVisibleRect());
				rect.setRect(rect.x-rect.width/2, rect.y-rect.height/2, rect.width*2, rect.height*2);
				diagramView.zoomToFit(rect);
				overview.setDiagramView(diagramView);
			};
		};
		
		zoomRectAction = new Action("Zoom to Rectangular", Activator.getImageDescriptor("zoom_region.png")) {

			private Behavior behavior=Behavior.SelectOnly;
			
			public void run() {
				SchematicPluginCore.zoomToRect=!SchematicPluginCore.zoomToRect;
				if (SchematicPluginCore.zoomToRect) {
					behavior = diagramView.getBehavior();
					diagramView.setCustomBehavior(new RectangleZoomBehavior(
							diagramView, overview));
				} else {
					diagramView.setBehavior(Behavior.SelectOnly);
					diagramView.setBehavior(behavior);
				}
			}
		};
		
		zoomMagnifier = new Action("Magnifier", Activator.getImageDescriptor("zoom_magnifier.png")) {
			private MagnifierPanel magnifier;

			public void run() {
				if (magnifier == null) {
					magnifier = new MagnifierPanel(diagramView);
				}
				SchematicPluginCore.showMagnifier=!SchematicPluginCore.showMagnifier;
				magnifier.setShowing(SchematicPluginCore.showMagnifier);
			}
		};
	
		fontAction = new Action("Font", Activator.getImageDescriptor("font.png")){
			
			public void run(){
				final Font font = getDiagram().getFont();
				final IWorkbench workbench=PlatformUI.getWorkbench();
				workbench.getDisplay().asyncExec(new Runnable(){
					public void run(){
						Shell shell=workbench.getActiveWorkbenchWindow().getShell();
						Display display=shell.getDisplay();
						Font oldFont = getDiagram().getFont();
						FontData oldFontData=FontUtil.toSwtFontData(display, oldFont, true);
						FontDialog fontDialog = new FontDialog(shell);
						fontDialog.setFontData(oldFontData);
						FontData fontData = fontDialog.open();
						if (fontData!=null){
							Font newFont = FontUtil.toAwtFont(display, fontData, true);
							//getDiagramView().suspendRepaint();
							getDiagram().setFont(newFont);
							Diagram diagram = getDiagram();
							for (DiagramItem item : diagram.items(DiagramItem.class)) {
								item.setFont(newFont);
								if (item instanceof ShapeNode) {
									ShapeNode snode = (ShapeNode) item;
								}
							}
							//getDiagramView().resumeRepaint();
						}
					}
				});
			};
		};
		
		decimalAction = new Action("Decimal", Activator.getImageDescriptor("decimal.png")){
			
			public void run(){
				final IWorkbench workbench=PlatformUI.getWorkbench();
				workbench.getDisplay().asyncExec(new Runnable(){
					public void run(){
						Shell shell=workbench.getActiveWorkbenchWindow().getShell();
						Display display=shell.getDisplay();
						DecimalDialog decimalDialog = new DecimalDialog(shell, getSchematic());
						decimalDialog.openDialog();
					}
				});
			};
		};
		
		SchematicPluginCore.isTAFMonthly = DssPluginCore._preferences.getBoolean(ISMONTHLYTAF, true);
		tafTypeAction = new Action("TAF Type"){
			
			public void run(){
				final IWorkbench workbench=PlatformUI.getWorkbench();
				workbench.getDisplay().asyncExec(new Runnable(){
					public void run(){
						SchematicPluginCore.isTAFMonthly=!SchematicPluginCore.isTAFMonthly;
						if (SchematicPluginCore.isTAFMonthly){
							DssPluginCore._preferences.putBoolean(ISMONTHLYTAF, true);
							tafTypeAction.setImageDescriptor(Activator.getImageDescriptor("M.png"));
						}else{
							DssPluginCore._preferences.putBoolean(ISMONTHLYTAF, false);
							tafTypeAction.setImageDescriptor(Activator.getImageDescriptor("Y.png"));
						}
					}
				});
			};
		};
		if (SchematicPluginCore.isTAFMonthly){
			tafTypeAction.setImageDescriptor(Activator.getImageDescriptor("M.png"));
		}else{
			tafTypeAction.setImageDescriptor(Activator.getImageDescriptor("Y.png"));
		}
		
		forwardAction = new Action("Forward", Activator.getImageDescriptor("forward.png")){
			
			public void run(){
				int twSize=DssPluginCore._schematicTwSelections.size();
				Combo dateList = dateCombo.getDateList();
				int size=dateList.getItemCount();
				if (SchematicPluginCore.selIndex<twSize){
					if (twSize<size){
						SchematicPluginCore.selIndex=twSize;
						dateList.select(SchematicPluginCore.selIndex);
					}
				}else{
					if (SchematicPluginCore.selIndex+1<size){
						SchematicPluginCore.selIndex = SchematicPluginCore.selIndex+1;
						dateList.select(SchematicPluginCore.selIndex);
					}
				}
			}
			
		};
		
		backwardAction = new Action("Backward", Activator.getImageDescriptor("backward.png")){
			
			public void run(){
				int twSize=DssPluginCore._schematicTwSelections.size();
				Combo dateList = dateCombo.getDateList();
				int size=dateList.getItemCount();
				if (SchematicPluginCore.selIndex<=twSize){
					if (twSize<size){
						SchematicPluginCore.selIndex=twSize;
						dateList.select(SchematicPluginCore.selIndex);
					}
				}else{
					if (SchematicPluginCore.selIndex>=size){
						SchematicPluginCore.selIndex = size-1;
						dateList.select(SchematicPluginCore.selIndex);
					}else{
						SchematicPluginCore.selIndex = SchematicPluginCore.selIndex-1;
						dateList.select(SchematicPluginCore.selIndex);
					}
				}
			}
			
		};
	}

	public void setupForEditor(){
		if (this instanceof SchematicEditor){
			((SchematicEditor)this).load();
		}
	}
	
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		swingContainer.setFocus();
	}

	public Diagram getDiagram() {
		return diagram;
	}

	public DiagramView getDiagramView() {
		return diagramView;
	}

	/**
	 * Selection wrapper for DiagramItem
	 * 
	 * @author psandhu
	 * 
	 */
	public static final class DiagramItemSelection implements
			IStructuredSelection, IAdaptable {

		private DiagramItem item;

		public DiagramItemSelection(DiagramItem item) {
			this.item = item;
		}

		public DiagramItem getItem() {
			return item;
		}

		public void setItem(DiagramItem item) {
			this.item = item;
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@SuppressWarnings("rawtypes")
		@Override
		public Object getAdapter(Class adapter) {
			if (adapter == IPropertySource.class) {
				return new DiagramItemPropertySource(item);
			}
			return null;
		}

		@Override
		public Object getFirstElement() {
			return new DiagramItemPropertySource(item);
		}

		@SuppressWarnings("rawtypes")
		@Override
		public Iterator iterator() {
			return Collections.singleton(getFirstElement()).iterator();
		}

		@Override
		public int size() {
			return 1;
		}

		@Override
		public Object[] toArray() {
			return new Object[] { getFirstElement() };
		}

		@SuppressWarnings("rawtypes")
		@Override
		public List toList() {
			return Collections.singletonList(getFirstElement());
		}

	}

	public static final class DiagramItemPropertySource implements
			IPropertySource {
		public static final String LABEL = "label";
		public static final String INCOMING = "incoming";
		public static final String OUTGOING = "outgoing";
		private DiagramItem item;

		public DiagramItemPropertySource(DiagramItem item) {
			this.item = item;
		}

		@Override
		public Object getEditableValue() {
			return null;
		}

		@Override
		public IPropertyDescriptor[] getPropertyDescriptors() {
			PropertyDescriptor labelDescriptor = new PropertyDescriptor(LABEL,
					"label");
			PropertyDescriptor incomingDescriptor = new PropertyDescriptor(
					INCOMING, "incoming");
			PropertyDescriptor outgoingDescriptor = new PropertyDescriptor(
					OUTGOING, "outgoing");
			return new IPropertyDescriptor[] { labelDescriptor,
					incomingDescriptor, outgoingDescriptor };
		}

		@Override
		public Object getPropertyValue(Object id) {
			if (id.equals(LABEL)) {
				if (item instanceof ShapeNode) {
					return ((ShapeNode) item).getPlainText();
				} else if (item instanceof DiagramLink) {
					return ((DiagramLink) item).getText();
				}
				return "";
			} else if (id.equals(INCOMING) || id.equals(OUTGOING)){
				if (item instanceof DiagramNode){
					DiagramNode node = (DiagramNode) item;
					if (id.equals(INCOMING)){
						return getIncomingNodes(node);
					} else {
						return getOutgoingNodes(node);
					}
				}
			}
			return "";
		}

		private Object getIncomingNodes(DiagramNode node) {
			DiagramLinkList incomingLinks = node.getIncomingLinks();
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < incomingLinks.size(); i++) {
				DiagramLink diagramLink = incomingLinks.get(i);
				if (i>0){
					buf.append(", ");
				}
				DiagramNode orig = diagramLink.getOrigin();
				buf.append(((InplaceEditable)orig).getTextToEdit());
			}
			return buf.toString();
		}
		
		private Object getOutgoingNodes(DiagramNode node) {
			DiagramLinkList outgoingLinks = node.getOutgoingLinks();
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < outgoingLinks.size(); i++) {
				DiagramLink diagramLink = outgoingLinks.get(i);
				if (i>0){
					buf.append(", ");
				}
				DiagramNode dest = diagramLink.getDestination();
				buf.append(((InplaceEditable)dest).getTextToEdit());
			}
			return buf.toString();
		}

		@Override
		public boolean isPropertySet(Object id) {
			if (id.equals(LABEL)) {
				return true;
			}
			return false;
		}

		@Override
		public void resetPropertyValue(Object id) {
		}

		@Override
		public void setPropertyValue(Object id, Object value) {
		}

	}

	public void refreshValues(final int index, boolean force) {		
		synchronized (diagramView) {
			final Rectangle2D.Float visibleRect = diagramView.deviceToDoc(diagramView
					.getVisibleRect());
			if (!force && lastVisibleRect==null) {
				return;
			}
			if (lastVisibleRect == null) {
				lastVisibleRect = visibleRect;
			}
			clearValueBoxes(lastVisibleRect);
			lastVisibleRect = visibleRect;
			
			final String selDate = SchematicPluginCore.selDate;
			new Thread(new Runnable() {
				public void run() {
					updateValues(index, selDate, visibleRect);
				}
			}).start();
		}
	}
	
	public void updateValues(int index, String date, Rectangle2D.Float visibleRect){
		Hashtable<String, String>[] values = null;
		Hashtable<String, Object> visibleNodes = getVisibleNodes(visibleRect);
		
		final Hashtable<String, Object> names = new Hashtable<String, Object>();
		for (String v : visibleNodes.keySet()) {
			names.put(v, v);
		}
		if (index==0){
			values = retrieveUndebug(date, names);
		}else{
			values = retrieveDebug(date, names);
		}
		setValues(index, visibleNodes, values);
	}
	
	public Hashtable<String, String>[] retrieveUndebug(String date, Hashtable<String, Object> names){
		ArrayList<String> tws = DssPluginCore._schematicTwSelections;
		for (int i=0; i<tws.size()-1; i++){
			if (date.equals(tws.get(i+1))){
				if (DssPluginCore.units.equals(DssPluginCore.cfs)){
					if (DssPluginCore.months.size()<12){
						return retrieveLongTermAverageSelectedMonths(date, names, true);
					}else{
						if (!DssPluginCore.longTermAverageDataCFS.containsKey(i)) {
							calculateLongTermAverage(i, date, true);
						}
						return retrieveLongTermAverage(i, names, true);
					}
				}else{
					if (DssPluginCore.months.size()<12){
						return retrieveLongTermAverageSelectedMonths(date, names, false);
					}else{
						if (!DssPluginCore.longTermAverageDataTAF.containsKey(i)) {
							calculateLongTermAverage(i, date, false);
						}
						return retrieveLongTermAverage(i, names, false);
					}
				}
			}
		}
		
		int size=names.size();
		Hashtable<String, String>[] results = new Hashtable[4];

		for (int i = 0; i < 4; ++i) {
			results[i] = new Hashtable<String, String>();
		}

		baseIndex=5;
		
		for (int k = 0; k < 4; k++) {
			if (DebugCorePlugin.selectedStudies[k]){
				if (baseIndex>k){
					baseIndex=k;
				}

				Enumeration<String> variableEnum = names.keys();
				while (variableEnum.hasMoreElements()) {
					String name = variableEnum.nextElement();
					ArrayList<String[]> pathParts = new ArrayList<String[]>();
					CondensedReference found = null;
					boolean isStorage=false;
					String value = "";			
					if (DssPluginCore.allSchematicVariableData[k].containsKey(name)){
						try {
							HecMath dataSet = DssPluginCore.allSchematicVariableData[k].get(name);
							if (DssPluginCore.allStorageNames.contains(name)) isStorage=true;
							TimeSeriesContainer tsc = (TimeSeriesContainer)dataSet.getData();
							if (tsc !=null) {
								boolean isTAFSelected = (DssPluginCore.units.equalsIgnoreCase("taf") ? true
										: false);
								tsc = unitsConversion(isStorage, tsc, isTAFSelected, k, name);
								if (DssPluginCore.units.equals("TAF") && tsc.units.equals("TAF") && !SchematicPluginCore.isTAFMonthly){
									tsc = convertAnnualTAFData(tsc); // CB added constant IV
								}
								HecTime hecStartTime = new HecTime();
								hecStartTime.set(tsc.startTime);
								HecTime ht = new HecTime();
							
								ht.setDate(date);
								int valueIndex = (ht.year() - hecStartTime.year())
											* 12 + (ht.month() - hecStartTime.month());
								if (valueIndex < 0 || valueIndex > tsc.values.length - 1) {
										value = "N/A";
								} else {
									Double value_temp=tsc.values[valueIndex];
									if (Math.abs(value_temp)>100000000) {//empty value
										value="N/A";
									} else {
										value = tsc.values[valueIndex] + " "
												+ tsc.units;
									}
								}
															
								if (DssPluginCore.mode.equals(DssPluginCore.diff)) {
									if (k > baseIndex) {
										if (results[baseIndex] == null || results[baseIndex].get(name) == null){
											results[k].put(name,"N/A");
											continue;
										}
										String[] baseFields = results[baseIndex].get(name)
											.split("\\s");
										String[] altFields = value.split("\\s");
										try {
											double baseVal = Double.parseDouble(baseFields[0]);
											double altVal = Double.parseDouble(altFields[0]);
											results[k].put(name, (altVal - baseVal) + " " + baseFields[1]);
										} catch (NumberFormatException nfe) {
											results[k].put(name,"N/A");
										}
									}else {
										results[k].put(name, value);
									}
								} else {
									results[k].put(name, value);
								}
							}else{
								results[k].put(name, value);
							}
						} catch (Exception e) {
							//WPPException.handleException(e);
							results[k].put(name, value);
						}
					}
				}
			}
		}
		return results;
	}
	
	public void calculateLongTermAverage(int pi, String date, boolean isCFS){
		
		Hashtable<String, Double> _longTermTafToCfsConversionFactors = new Hashtable<String, Double>();

		ArrayList<HashMap<String, Double>> termAverage= new ArrayList<HashMap<String, Double>>();
		if (isCFS){
			DssPluginCore.longTermAverageDataCFS.put(pi, termAverage);
		}else{
			DssPluginCore.longTermAverageDataTAF.put(pi, termAverage);
		}
		
		int index = 0;
		int month = -1;
		int year = -1;

		HecTime startDate = new HecTime();
		HecTime endDate = new HecTime();
		
		String[] split = date.split(" - ");
		startDate.setDate(split[0]); 
		startDate.setTime("0100");

		endDate.setDate(split[1]); 
		endDate.setTime("2400");
		month = endDate.month() + 1;
		year = endDate.year();
		if (month == 13) {
			year = startDate.year() + 1;
		}
		String monthName = TimeOperation.getMonthText(month);
		endDate.setDate(monthName + year);
		endDate.add(-1440);
		
		HecTime ht = new HecTime();
		HecTime hecStartTime = new HecTime();
		HecTime hecEndTime = new HecTime();

		for (int i=0; i<4; i++){
			HashMap<String, Double> altAverage = new HashMap<String, Double>();
			termAverage.add(altAverage);
			
			HashMap<String, HecMath> altSchematicVariableData = DssPluginCore.allSchematicVariableData[i];
			if (altSchematicVariableData !=null){
				Set<String> keys=altSchematicVariableData.keySet();
				Iterator<String> it = keys.iterator();
				while (it.hasNext()){
					String name=it.next();
					HecMath dataSet=altSchematicVariableData.get(name);
					boolean isStorage=false;
					try {
						TimeSeriesContainer tsc = (TimeSeriesContainer)dataSet.getData();
						if (DssPluginCore.allStorageNames.contains(name)) isStorage=true;
						boolean isTAFSelected = (DssPluginCore.units.equalsIgnoreCase("taf") ? true
							: false);
						tsc=unitsConversion(isStorage, tsc, isTAFSelected, i, name);
						ht.set(startDate);
						hecStartTime.set(tsc.startTime);
						hecEndTime.set(tsc.endTime);
						int count=0;
						double sum=0.0;
						boolean inRange=false;
						while (ht.compareTimes(hecEndTime)<=0 && ht.compareTimes(endDate)<=0){
							if (ht.compareTimes(hecStartTime) >= 0){
								int valueIndex = (ht.year() - hecStartTime
										.year())
										* 12
										+ (ht.month() - hecStartTime
												.month());
								sum += tsc.values[valueIndex];
								count++;
								inRange=true;
							}
							month = ht.month() + 1;
							if (month == 13) {
								month = 1;
								year = ht.year() + 1;
							} else {
								year = ht.year();
							}
							ht.setDate(TimeOperation.getMonthText(month)+
								+ year);
						}
						if (inRange) {
							altAverage.put(name, sum/count);
						}
					} catch (HecMathException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}	
	
	public ArrayList<HashMap<String, Double>> calculateLongTermAverageSelectedMonths(String date, Hashtable<String, Object> names, boolean isCFS){
		
		Hashtable<String, Double> _longTermTafToCfsConversionFactors = new Hashtable<String, Double>();

		ArrayList<HashMap<String, Double>> termAverage= new ArrayList<HashMap<String, Double>>();
				
		int index = 0;
		int month = -1;
		int year = -1;

		HecTime startDate = new HecTime();
		HecTime endDate = new HecTime();
		
		String[] split = date.split(" - ");
		startDate.setDate(split[0]); 
		startDate.setTime("0100");

		endDate.setDate(split[1]); 
		endDate.setTime("2400");
		month = endDate.month() + 1;
		year = endDate.year();
		if (month == 13) {
			year = startDate.year() + 1;
		}
		String monthName = TimeOperation.getMonthText(month);
		endDate.setDate(monthName + year);
		endDate.add(-1440);
		
		HecTime ht = new HecTime();
		HecTime hecStartTime = new HecTime();
		HecTime hecEndTime = new HecTime();

		for (int i=0; i<4; i++){
			HashMap<String, Double> altAverage = new HashMap<String, Double>();
			termAverage.add(altAverage);
			
			HashMap<String, HecMath> altSchematicVariableData = DssPluginCore.allSchematicVariableData[i];
			Set<String> keys=names.keySet();
			Iterator<String> it = keys.iterator();
			while (it.hasNext()){
				String name=it.next();
				if (altSchematicVariableData.containsKey(name)){
					HecMath dataSet=altSchematicVariableData.get(name);
					boolean isStorage=false;
					try {
						TimeSeriesContainer tsc = (TimeSeriesContainer)dataSet.getData();
						if (DssPluginCore.allStorageNames.contains(name)) isStorage=true;
						boolean isTAFSelected = (DssPluginCore.units.equalsIgnoreCase("taf") ? true
								: false);
						tsc=unitsConversion(isStorage, tsc, isTAFSelected, i, name);
						ht.set(startDate);
						hecStartTime.set(tsc.startTime);
						hecEndTime.set(tsc.endTime);
						int count=0;
						double sum=0.0;
						boolean inRange=false;
						while (ht.compareTimes(hecEndTime)<=0 && ht.compareTimes(endDate)<=0){
							if (ht.compareTimes(hecStartTime) >= 0 && DssPluginCore.months.contains(ht.month())){
								int valueIndex = (ht.year() - hecStartTime
										.year())
										* 12
										+ (ht.month() - hecStartTime
												.month());
								sum += tsc.values[valueIndex];
								count++;
								inRange=true;
							}
							month = ht.month() + 1;
							if (month == 13) {
								month = 1;
								year = ht.year() + 1;
							} else {
								year = ht.year();
							}
							ht.setDate(TimeOperation.getMonthText(month)+
									+ year);
						}
						if (inRange) {
							altAverage.put(name, sum/count);
						}
					} catch (HecMathException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return termAverage;
	}	
	
	public Hashtable<String, String>[] retrieveLongTermAverage(int index, Hashtable<String, Object> names, boolean isCFS){
		
		int size=names.size();
		Hashtable<String, String>[] results = new Hashtable[4];

		for (int i = 0; i < 4; ++i) {
			results[i] = new Hashtable<String, String>();
		}

		int baseIndex=5;
		
		ArrayList<HashMap<String, Double>> termAverage;
		if (isCFS){
			termAverage=DssPluginCore.longTermAverageDataCFS.get(index);
		}else{
			termAverage=DssPluginCore.longTermAverageDataTAF.get(index);
		}
		
		for (int k = 0; k < 4; k++) {
			if (DebugCorePlugin.selectedStudies[k]){
				if (baseIndex>k){
					baseIndex=k;
				}
				Enumeration<String> variableEnum = names.keys();
				while (variableEnum.hasMoreElements()) {
					String name = variableEnum.nextElement();
					ArrayList<String[]> pathParts = new ArrayList<String[]>();
					CondensedReference found = null;
					boolean isStorage=false;
					if (DssPluginCore.allStorageNames.contains(name)) isStorage=true;
					String value = "";			
					HashMap<String, Double> altAverage = termAverage.get(k);
					if (altAverage.containsKey(name)){
						double data = altAverage.get(name);
						boolean isTAFSelected = (DssPluginCore.units.equalsIgnoreCase("taf") ? true
								: false);
						String units;
						if (isCFS){
							units=DssPluginCore.allSchematicVariableUnitsCFS[k].get(name);
						}else{
							units=DssPluginCore.allSchematicVariableUnitsTAF[k].get(name);
						}
						if (data>100000000) {
							value="N/A";
						} else {
							value = data + " " + units;
							if (DssPluginCore.units.equals("TAF") && !SchematicPluginCore.isTAFMonthly){
								value = data*12 + " " + units;
							}
						}
						if (DssPluginCore.mode.equals(DssPluginCore.diff)) {
							if (k > baseIndex) {
								if (results[baseIndex] == null || results[baseIndex].get(name) == null){
									results[k].put(name,"N/A");
									continue;
								}
								String[] baseFields = results[baseIndex].get(name)
										.split("\\s");
								String[] altFields = value.split("\\s");
								try {
									double baseVal = Double.parseDouble(baseFields[0]);
									double altVal = Double.parseDouble(altFields[0]);
									if (DssPluginCore.units.equals("TAF") && !SchematicPluginCore.isTAFMonthly){
										baseVal=baseVal*12;
										altVal=altVal*12;
									}
									results[k].put(name, (altVal - baseVal) + " " + baseFields[1]);
								} catch (NumberFormatException nfe) {
									results[k].put(name,"N/A");
								}
							}else {
								results[k].put(name, value);
							}
						}else {
							results[k].put(name, value);
						}
					}else{
						value="";
						results[k].put(name, value);
					}	
				}	
			}
		}
		return results;
	}
	
	public Hashtable<String, String>[] retrieveLongTermAverageSelectedMonths(String date, Hashtable<String, Object> names, boolean isCFS){
		
		int size=names.size();
		Hashtable<String, String>[] results = new Hashtable[4];

		for (int i = 0; i < 4; ++i) {
			results[i] = new Hashtable<String, String>();
		}

		int baseIndex=5;
		
		ArrayList<HashMap<String, Double>> termAverage = calculateLongTermAverageSelectedMonths(date, names, isCFS);
		
		for (int k = 0; k < 4; k++) {
			if (DebugCorePlugin.selectedStudies[k]){
				if (baseIndex>k){
					baseIndex=k;
				}
				HashMap<String, Double> altAverage = termAverage.get(k);
				Enumeration<String> variableEnum = names.keys();
				while (variableEnum.hasMoreElements()) {
					String name = variableEnum.nextElement();
					ArrayList<String[]> pathParts = new ArrayList<String[]>();
					CondensedReference found = null;
					boolean isStorage=false;
					if (DssPluginCore.allStorageNames.contains(name)) isStorage=true;
					String value = "";			
					if (altAverage.containsKey(name)){
						double data = altAverage.get(name);
						boolean isTAFSelected = (DssPluginCore.units.equalsIgnoreCase("taf") ? true
								: false);
						String units;
						if (isCFS){
							units=DssPluginCore.allSchematicVariableUnitsCFS[k].get(name);
						}else{
							units=DssPluginCore.allSchematicVariableUnitsTAF[k].get(name);
						}
						if (data>100000000) {
							value="N/A";
						} else {
							value = data + " " + units;
							if (DssPluginCore.units.equals("TAF") && !SchematicPluginCore.isTAFMonthly){
								value = data*12 + " " + units;
							}
						}
						if (DssPluginCore.mode.equals(DssPluginCore.diff)) {
							if (k > baseIndex) {
								if (results[baseIndex] == null || results[baseIndex].get(name) == null){
									results[k].put(name,"N/A");
									continue;
								}
								String[] baseFields = results[baseIndex].get(name)
										.split("\\s");
								String[] altFields = value.split("\\s");
								try {
									double baseVal = Double.parseDouble(baseFields[0]);
									double altVal = Double.parseDouble(altFields[0]);
									if (DssPluginCore.units.equals("TAF") && !SchematicPluginCore.isTAFMonthly){
										baseVal=baseVal*12;
										altVal=altVal*12;
									}
									results[k].put(name, (altVal - baseVal) + " " + baseFields[1]);
								} catch (NumberFormatException nfe) {
									results[k].put(name,"N/A");
								}
							}else {
								results[k].put(name, value);
							}
						}else {
							results[k].put(name, value);
						}
					}else{
						value="";
						results[k].put(name, value);
					}	
				}	
			}
		}
		return results;
	}
	
	public Hashtable<String, String>[] retrieveDebug(String date, Hashtable<String, Object> names){
		int size=names.size();
		Hashtable<String, String>[] results = new Hashtable[5];

		for (int i = 0; i < 5; ++i) {
			results[i] = new Hashtable<String, String>();
		}

		baseIndex=0;
		
		Enumeration<String> variableEnum = names.keys();
		for (int k = 0; k < 4; k++) {
			if (DebugCorePlugin.selectedStudies[k]){
				
				while (variableEnum.hasMoreElements()) {
					String name = variableEnum.nextElement();
					ArrayList<String[]> pathParts = new ArrayList<String[]>();
					boolean isStorage=false;
					String value = "";			
					if (DssPluginCore.allSchematicVariableData[k].containsKey(name)){
						try {
							HecMath dataSet = DssPluginCore.allSchematicVariableData[k].get(name);
							if (dataSet !=null) {
								if (DssPluginCore.allStorageNames.contains(name)) isStorage=true;
								TimeSeriesContainer tsc=(TimeSeriesContainer)dataSet.getData();
								boolean isTAFSelected = (DssPluginCore.units.equalsIgnoreCase("taf") ? true
										: false);
								tsc = unitsConversion(isStorage, tsc, isTAFSelected, k, name);
								if (DssPluginCore.units.equals("TAF") && tsc.units.equals("TAF") && !SchematicPluginCore.isTAFMonthly){
									tsc = convertAnnualTAFData(tsc); // CB added constant IV
								}
								HecTime hecStartTime = new HecTime();
								hecStartTime.set(tsc.startTime);
								HecTime ht = new HecTime();
							
								ht.setDate(date);
								int valueIndex = (ht.year() - hecStartTime.year())
											* 12 + (ht.month() - hecStartTime.month());
								if (valueIndex < 0 || valueIndex > tsc.values.length - 1) {
										value = "N/A";
								} else {
									Double value_temp=tsc.values[valueIndex];
									if (Math.abs(value_temp)>100000000) {//empty value
										value="N/A";
									} else {
										value = tsc.values[valueIndex] + " "
												+ tsc.units;
									}
								}
															
								if (DssPluginCore.mode.equals(DssPluginCore.diff)) {
									if (k > baseIndex) {
										if (results[baseIndex] == null || results[baseIndex].get(name) == null){
											results[k].put(name,"N/A");
											continue;
										}
										String[] baseFields = results[baseIndex].get(name)
											.split("\\s");
										String[] altFields = value.split("\\s");
										try {
											double baseVal = Double.parseDouble(baseFields[0]);
											double altVal = Double.parseDouble(altFields[0]);
											results[k].put(name, (altVal - baseVal) + " " + baseFields[1]);
										} catch (NumberFormatException nfe) {
											results[k].put(name,"N/A");
										}
									}else {
										results[k].put(name, value);
									}
								} else {
									results[k].put(name, value);
								}
							}else{
								results[k].put(name, value);
							}
						} catch (Exception e) {
							WPPException.handleException(e);
						}
						
					}
				}
			}
		}
		return results;
	}
	
	public void setValues(int index, Hashtable<String, Object> visibleNodes, Hashtable<String, String>[] values){
		try {
			diagramView.suspendRepaint();
			synchronized (diagramView) {
				for (int studyId = 0; studyId < values.length; studyId++) {
					Hashtable<String, String> valuesInStudy = values[studyId];
					for (String name : valuesInStudy.keySet()) {
						Object object = visibleNodes.get(name);
						if (object == null) {
							continue;
						}
						String value = valuesInStudy.get(name);
						value = truncateAfterDecimal(value, DssPluginCore._preferences.getInt(DECIMAL_PLACES, 0),
								true);
						if (object instanceof ShapeNode) {
							ShapeNode shapeNode = (ShapeNode) object;
							Group subordinateGroup = shapeNode
									.getSubordinateGroup();
							DiagramNode diagramNode = null;
							if (subordinateGroup != null) {
								DiagramNodeList attachedNodes = subordinateGroup
										.getAttachedNodes();
								if (studyId > attachedNodes.size() - 1) {
									diagramNode = createTextNodeWithIntermediates(
											studyId, attachedNodes.size(),
											shapeNode);
								} else {
									diagramNode = attachedNodes.get(studyId);
								}
							} else {
								diagramNode = createTextNodeWithIntermediates(
										studyId, 0, shapeNode);
//								diagramNode = createTextNode(studyId, shapeNode);
							}
							if (diagramNode != null) {
								((InplaceEditable)diagramNode).setEditedText(value);
								if (DssPluginCore.mode.equals(DssPluginCore.diff) && studyId > 0) {
									((ShapeNode) diagramNode)
											.getPen().setColor(Color.red);
								} else {
									((ShapeNode) diagramNode)
									.getPen().setColor(Color.black);
								}
								((ShapeNode) diagramNode)
										.setToolTip(getToolTip(index, studyId, DssPluginCore.mode.equals(DssPluginCore.diff)));
							}
						}
					}
				}
			}
		} finally {
			diagramView.resumeRepaint();
		}
	}
	
	public 	void clearValueBoxes(Float visibleRect) {
		if (visibleRect == null) {
			return;
		}
		Hashtable<String, Object> visibleNodes = getVisibleNodes(visibleRect);
		DiagramNodeList nodes = diagram.getNodes();
		for (Object o : visibleNodes.values()) {
			if (o instanceof ShapeNode) {
				ShapeNode shapeNode = (ShapeNode) o;
				Object id = shapeNode.getId();
				if (id != null && id.equals(VALUE_TEXT)) {
					shapeNode.setText("");
					// nodes.remove(shapeNode);
				}
			}
		}
	}

	public Hashtable<String, Object> getVisibleNodes(
			Rectangle2D.Float visibleRect) {
		Hashtable<String, Object> variables = new Hashtable<String, Object>();
		Iterable<ShapeNode> items = diagram.items(ShapeNode.class);
		for (ShapeNode item : items) {
			if (!item.getBounds().intersects(visibleRect))
				continue;
			if (item.getTransparent()) {// ignore transparent text nodes
				continue;
			}
			variables.put(((ShapeNode) item).getTextToEdit(), item);
		}
		return variables;

	}
	
	private ShapeNode createTextNodeWithIntermediates(int studyId,
			int startingWithId, ShapeNode shapeNode) {
		ShapeNode textNode = null;
		for (int id = startingWithId; id <= studyId; id++){
			textNode = createTextNode(id, shapeNode);
		}
		return textNode;
	}
	
	private ShapeNode createTextNode(int studyId, ShapeNode shapeNode) {
		Float r = shapeNode.getBounds();
		Rectangle2D.Float r2 = null;
		TextFormat tf = null;
		AttachToNode attachPos = AttachToNode.BottomLeft;
		switch (studyId) {
		case 0:
			attachPos = AttachToNode.BottomLeft;
			r2 = new Rectangle2D.Float(r.x, r.y + r.height / 2, r.width / 2,
					r.height / 2);
			tf = new TextFormat(Align.Near, Align.Far);
			break;
		case 1:
			attachPos = AttachToNode.BottomRight;
			r2 = new Rectangle2D.Float(r.x + r.width / 2, r.y + r.height / 2,
					r.width / 2, r.height / 2);
			tf = new TextFormat(Align.Far, Align.Far);
			break;
		case 2:
			attachPos = AttachToNode.TopLeft;
			r2 = new Rectangle2D.Float(r.x, r.y, r.width / 2, r.height / 2);
			tf = new TextFormat(Align.Near, Align.Near);
			break;
		case 3:
			attachPos = AttachToNode.TopRight;
			r2 = new Rectangle2D.Float(r.x + r.width / 2, r.y, r.width / 2,
					r.height / 2);
			tf = new TextFormat(Align.Far, Align.Near);
			break;
		default:
			attachPos = AttachToNode.TopCenter;
			r2 = new Rectangle2D.Float(r.x, r.y + r.height / 2, r.width / 2,
					r.height / 2);
			tf = new TextFormat(Align.Center, Align.Center);
			break;
		}

		ShapeNode transparentTextNode = diagram.getFactory()
				.createShapeNode(r2);
		transparentTextNode.setId(VALUE_TEXT);
		transparentTextNode.setTransparent(true);
		transparentTextNode.setLocked(true);
		transparentTextNode.setFont(shapeNode.getFont());
		transparentTextNode.setPen(shapeNode.getPen());
		transparentTextNode.setBrush(shapeNode.getBrush());
		transparentTextNode.setTextFormat(tf);
		transparentTextNode.attachTo(shapeNode, attachPos);
		return transparentTextNode;
	}
	
	private String truncateAfterDecimal(String value, int i,
			boolean displayUnits) {
		if (value == null) {
			return null;
		}
		String[] fields = value.split("\\s");
		if (fields.length < 2) {
			return value;
		}
		return String.format("%." + i + "f %s", Double.parseDouble(fields[0]),
				displayUnits ? fields[1] : "");
	}
	
	public String getToolTip(int index, int studyId, boolean isDiff) {
		if (index==0){
			if (studyId == baseIndex){
				return "Alt "+studyId;
			}else if (studyId>baseIndex){
				if (isDiff){
					return "Alt "+studyId+" - " +"Alt "+baseIndex;
				}else{
					return "Alt "+studyId;
				}
			}else{
				return "";
			}
		}else{	
			switch (studyId) {
			case 0:
				return "Debug";
			case 1:
				return isDiff ? "Alt 1 - Debug" : "Alt 1";
			case 2:
				return isDiff ? "Alt 2 - Debug" : "Alt 2";
			case 3:
				return isDiff ? "Alt 3 - Debug" : "Alt 3";
			case 4:
				return isDiff ? "Alt 4 - Debug" : "Alt 4";
			}
			return "";
		}
	}
	
	public TimeSeriesContainer unitsConversion(boolean isStorage, TimeSeriesContainer dataSet, boolean force, int index, String name) {

		if ((DssPluginCore.units.equals("TAF") || force || isStorage)
				&& dataSet.units.equals("CFS")) {
			// CB dataSet = adjustMonthlyData(dataSet, 1.9834631 / 1000.);
			dataSet = adjustMonthlyData(dataSet, true); // CB added constant IV FACTOR
			// so do not need to pass a
			// factor
			dataSet.units="TAF";
			DssPluginCore.allSchematicVariableUnitsTAF[index].put(name, "TAF");

		} else if (DssPluginCore.units.equals("CFS") && dataSet.units.equals("TAF")
				&& !force && !isStorage) { // CB added section
			dataSet = adjustMonthlyData(dataSet, false); // CB added constant IV
			// FACTOR so do not
			// need to pass a
			// factor
			dataSet.units="CFS";
			DssPluginCore.allSchematicVariableUnitsCFS[index].put(name, "CFS");
		} 
		return dataSet;
	}
	
	public TimeSeriesContainer adjustMonthlyData(TimeSeriesContainer tsc, boolean isCFStoTAF) { // CB added
		// the
		// boolean
		// and
		// made
		// a
		// constant
		// IV
		// factor
		HecTime ht = null;
		try {
			double[] nvalues = new double[tsc.values.length];
			int[] times = tsc.times;
			int ndays = 0;
			ht = new HecTime();
			for (int i = 0; i < times.length; i++) {
				ht.set(times[i]);
				// FIX: subtract 1 min
				ht.add(-1);
				ndays = ht.day();
				/*
				 * HecTime BUG: sometimes ht.day() returns a day later than *
				 * should???? I wasted a lot of time on this!!
				 * 
				 * returns ht.day() == 1, ht.hour() == 0 AS OPPOSED TO ht.day()
				 * == 28,30,31 (depending on month), ht.hour() == 24
				 */

				// if (DEBUG) System.out.println(ht.year()+" "+ht.month()+
				// " "+ht.day()+ht.hour()+" "+ht.minute()+" "+ht.second()+" "+times[i]+"
				// "+values[i]);
				// CB nvalues[i] = tsc.values[i] * ndays * factor;
				if (isCFStoTAF) {
					nvalues[i] = tsc.values[i] * ndays / FACTOR;
				} else {
					nvalues[i] = tsc.values[i] / ndays * FACTOR;
				}
			}
			tsc.values = nvalues;
		} catch (Exception e) {
			System.out.println("Exception adjustMonthlyData: " + e);
		}
		return tsc;
	}
	
	public TimeSeriesContainer convertAnnualTAFData(TimeSeriesContainer tsc) { // CB added
		// the
		// boolean
		// and
		// made
		// a
		// constant
		// IV
		// factor
		HecTime ht = null;
		try {
			double[] nvalues = new double[tsc.values.length];
			int[] times = tsc.times;
			int ndays = 0;
			ht = new HecTime();
			for (int i = 0; i < times.length; i++) {
				ht.set(times[i]);
				// FIX: subtract 1 min
				ht.add(-1);
				//ndays = ht.day();
				nvalues[i] = tsc.values[i] *12; 
			}
			tsc.values = nvalues;
		} catch (Exception e) {
			System.out.println("Exception convertAnnualTAFData: " + e);
		}
		return tsc;
	}
	
	public void collectAllSchematicVariables(){
		if (this instanceof SchematicView){
			Hashtable<String, Object> allNodes = getVisibleNodes(diagram.getBounds());
		
			DssPluginCore.allSchematicVariableNames = new ArrayList<String>();
			for (String v : allNodes.keySet()) {
				DssPluginCore.allSchematicVariableNames.add(v);
			}
		}
	}
	
	public void loadAllSchematicVariableData(){
		if (this instanceof SchematicView){
			DssPluginCore.allSchematicVariableUnitsCFS=new HashMap[4];
			DssPluginCore.allSchematicVariableUnitsTAF=new HashMap[4];
			DssPluginCore.longTermAverageDataCFS=new HashMap();
			DssPluginCore.longTermAverageDataTAF=new HashMap();
			DssPluginCore.allSchematicVariableData = new HashMap[4];
			for (int kk=0; kk<4; kk++){
				HashMap<String, HecMath> data= new HashMap<String, HecMath>();
				DssPluginCore.allSchematicVariableData[kk]=data;
				HashMap<String, String> cfsUnitsMap = new HashMap<String, String>();
				DssPluginCore.allSchematicVariableUnitsCFS[kk]=cfsUnitsMap;
				HashMap<String, String> tafUnitsMap = new HashMap<String, String>();
				DssPluginCore.allSchematicVariableUnitsTAF[kk]=tafUnitsMap;
			
				if (DebugCorePlugin.selectedStudies[kk]){
					if (DebugCorePlugin.dvDss[kk] !=null){
						DebugCorePlugin.dvDss[kk].setTimeWindow(DebugCorePlugin.timeWindow);
					}
					if (DebugCorePlugin.svDss[kk] !=null){
						DebugCorePlugin.svDss[kk].setTimeWindow(DebugCorePlugin.timeWindow);
					}
				}		
			}
		
			int size = DssPluginCore.allSchematicVariableNames.size();
			
			if (size>0){
							
				for (int j=0; j<size; j++) {
					String name = DssPluginCore.allSchematicVariableNames.get(j);
					if (DssPluginCore.allPathName.containsKey(name)){
						String pathName = DssPluginCore.allPathName.get(name);
						for (int i=0; i<4; i++){
							if (DebugCorePlugin.selectedStudies[i]){
								HecMath dataSet=null;
								HecDss dvFile = DebugCorePlugin.dvDss[i];
								HecDss svFile = DebugCorePlugin.svDss[i];
								if (dvFile != null){
									try {
										dataSet = dvFile.read(pathName);
										if (dataSet ==null){
											readFromSV(svFile, pathName, name, i);
											continue;
										}else{
											DssPluginCore.allSchematicVariableData[i].put(name, dataSet);
											DssPluginCore.allSchematicVariableUnitsCFS[i].put(name, dataSet.getUnits());
											DssPluginCore.allSchematicVariableUnitsTAF[i].put(name, dataSet.getUnits());
											continue;
										}
									} catch (Exception e) {
										readFromSV(svFile, pathName, name, i);
									}
								}else{
									readFromSV(svFile, pathName, name, i);
								}
							}
						}
					}else{
						for (int i=0; i<4; i++){
							if (DebugCorePlugin.selectedStudies[i]){
								HecMath dataSet=null;
								HecDss dvFile = DebugCorePlugin.dvDss[i];
								HecDss svFile = DebugCorePlugin.svDss[i];
							
								String pathName=DssPluginCore.dvPathnameMap[i].get(name);
								if (pathName !=null){
									try {
										dataSet= dvFile.read(pathName);
										DssPluginCore.allSchematicVariableData[i].put(name, dataSet);
										DssPluginCore.allSchematicVariableUnitsCFS[i].put(name, dataSet.getUnits());
										DssPluginCore.allSchematicVariableUnitsTAF[i].put(name, dataSet.getUnits());
									}catch (Exception e) {
									}
								}
								pathName=DssPluginCore.svPathnameMap[i].get(name);
								if (pathName !=null){
									try {
										dataSet= svFile.read(pathName);
										DssPluginCore.allSchematicVariableData[i].put(name, dataSet);
										DssPluginCore.allSchematicVariableUnitsCFS[i].put(name, dataSet.getUnits());
										DssPluginCore.allSchematicVariableUnitsTAF[i].put(name, dataSet.getUnits());
									}catch (Exception e) {
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	public void readFromSV(HecDss svFile, String pathName, String name, int i){
		try {
			HecMath dataSet = svFile.read(pathName);
			if (dataSet !=null){
				DssPluginCore.allSchematicVariableData[i].put(name, dataSet);
				DssPluginCore.allSchematicVariableUnitsCFS[i].put(name, dataSet.getUnits());
				DssPluginCore.allSchematicVariableUnitsTAF[i].put(name, dataSet.getUnits());
			}
		} catch (Exception e) {
		}
	}
	
	public static HashMap<String, String> generatePathnameMap(HecDss file){
		HashMap<String, String> pathnameMap=new HashMap<String, String> ();
		Vector<CondensedReference> v=file.getCondensedCatalog();
		for (int i=0; i<v.size(); i++){
			CondensedReference cr = v.get(i);
			String pathname=cr.getNominalPathname();
			DSSPathname dssPathname = new DSSPathname(pathname);
			String partB=dssPathname.bPart();
			pathnameMap.put(partB, pathname);
		}
		return pathnameMap;
	}
	
	public boolean findInView(String text) {
		if (diagram == null || text == null) {
			return false;
		}
		text = text.toLowerCase();
		DiagramNodeList nodes = diagram.getNodes();
		int n_node=nodes.size();
//		for (DiagramNode n : nodes) {	
//			String nodeText = n.getTextToEdit();
		for (int i_node = 1; i_node <= n_node; i_node++){
			final int index_currentnode = (i_node + index_lastnode) % n_node;
			final DiagramNode n = nodes.get(index_currentnode);
			String nodeText = ((InplaceEditable)n).getTextToEdit();
			if (nodeText != null && nodeText.toLowerCase().contains(text)) {
				SwingUtilities.invokeLater(new Runnable(){

					@Override
					public void run() {
						diagramView.bringIntoView(n);
						index_lastnode = index_currentnode;
					}
					
				}); 
				
				return true;
			}
		}
		return false;
	}
	
	public void save(final String filename) throws Exception {
		if (filename == null)
			return;
		Cursor previousCursor = diagramView.getCursor();
		diagramView.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
			if (filename.endsWith(".xml")) {
				diagram.saveToXml(filename);
			} else if (filename.endsWith(".pdf")) {
				final IWorkbench workbench=PlatformUI.getWorkbench();
				workbench.getDisplay().asyncExec(new Runnable(){
					public void run(){
						Shell shell=workbench.getActiveWorkbenchWindow().getShell();
						PDFOptionDialog dialog= new PDFOptionDialog(shell, filename, diagram);
						dialog.openDialog();
					}
				});
			} else if (filename.endsWith(".svg")) {
				SvgExporter svgExp = new SvgExporter(diagram, filename);
				svgExp.export();
			} else {
				diagram.saveTo(filename);
			}
		} finally {
			diagramView.setCursor(previousCursor);
		}
	}
	
	public Component getContentPane(){
		return contentPane;
	}
	
	public SchematicBase getSchematic(){
		return this;
	}
}
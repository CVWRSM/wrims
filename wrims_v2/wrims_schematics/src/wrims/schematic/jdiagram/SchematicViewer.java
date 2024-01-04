package wrims.schematic.jdiagram;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import wrims.schematic.MainFrame;
import wrims.schematic.element.Element;

import com.mindfusion.diagramming.Align;
import com.mindfusion.diagramming.AttachToNode;
import com.mindfusion.diagramming.Behavior;
import com.mindfusion.diagramming.Diagram;
import com.mindfusion.diagramming.DiagramAdapter;
import com.mindfusion.diagramming.DiagramItem;
import com.mindfusion.diagramming.DiagramItemList;
import com.mindfusion.diagramming.DiagramNode;
import com.mindfusion.diagramming.DiagramNodeList;
import com.mindfusion.diagramming.DiagramView;
import com.mindfusion.diagramming.DrawNodeEvent;
import com.mindfusion.diagramming.Group;
import com.mindfusion.diagramming.LinkValidationEvent;
import com.mindfusion.diagramming.NodeEvent;
import com.mindfusion.diagramming.NodeValidationEvent;
import com.mindfusion.diagramming.Overview;
import com.mindfusion.diagramming.Pen;
import com.mindfusion.diagramming.SelectionStyle;
import com.mindfusion.diagramming.ShapeNode;
import com.mindfusion.diagramming.TextFormat;
import com.mindfusion.diagramming.XmlException;
import com.mindfusion.diagramming.export.PdfExporter;
import com.mindfusion.diagramming.export.SvgExporter;

/**
 * View schematic
 * 
 * @author psandhu
 * 
 */
public class SchematicViewer extends JPanel {

	public static final String WRIMS_SCHEMATIC = "wrims.schematic";
	private static final Object VALUE_TEXT = "__VT__";
	private DiagramView diagramView;
	private Diagram diagram;
	private String filename;
	private Action zoomInAction, zoomOutAction;
	private Overview overview;
	private ElementTask clickTask;
	private boolean enableClick;
	private float zoomFactor = 1.15f;
	protected Rectangle2D.Float lastVisibleRect;
	protected ViewPosition lastViewPosition;
	private MainFrame schematic;
	private boolean showValueBoxes;
	private AbstractAction zoomNormalAction;
	private AbstractAction zoomBestFitAction;
	protected DiagramAdapter listener;
	private AbstractAction zoomRectAction;
	private LegendPanel legendPanel;
	private static ArrayList<ViewPosition> viewHistory = new ArrayList<ViewPosition>();
	private static int viewHistoryPointer = 0;
	private AbstractAction forwardViewAction;
	private AbstractAction backwardViewAction;
	private AbstractAction toggleLegendAction;
	private JSplitPane splitPane;
	private AbstractAction selectFontAction;
	private int precision = 2;
	private AbstractAction selectPrecisionAction;
	private boolean showUnits = true;
	private AbstractAction markViewAction;
	private JComboBox markViewComboBox;
	private AbstractAction deleteViewAction;
	private AbstractAction zoomMagnifier;
	private TitledBorder titledBorder;
	private int index_lastnode = 0;

	private static Preferences userPrefs;
	static {
		userPrefs = Preferences.userNodeForPackage(SchematicViewer.class);
		try {
			String[] childrenNames = userPrefs.keys();
			viewHistory = new ArrayList<ViewPosition>();
			for (String name : childrenNames) {
				try {
					viewHistory.add(toViewPosition(userPrefs.get(name, null)));
				} catch (Exception ex) {
					userPrefs.remove(name);
				}
			}
			viewHistoryPointer = viewHistory.size();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Add diagram viewer to a scrollpane
	 */
	public SchematicViewer() {
		PanelWithCollapsibleInsetPanel panel = new PanelWithCollapsibleInsetPanel(
				true);
		panel.collapse();
		this.setBorder(titledBorder = BorderFactory.createTitledBorder(" "));
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.add(panel);

		diagram = new Diagram();
		diagramView = new DiagramView();
		diagramView.setDiagram(diagram);
		diagramView.setBehavior(Behavior.Modify);

		legendPanel = new LegendPanel(this);
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				new JScrollPane(legendPanel), new JScrollPane(diagramView));
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(0);

		panel.getMainPanel().add(splitPane);

		overview = new Overview();
		overview.setDiagramView(diagramView);
		overview.setDoubleBuffered(true);
		overview.setFitAll(true);
		panel.getInsetPanel().add(overview);

		diagram.getSelection().setStyle(SelectionStyle.SelectionHandles);
		diagram.getSelection().setAllowMultipleSelection(true);
		diagram.addDiagramListener(listener = new DiagramAdapter() {
			private Timer refreshTimer = new Timer();
			private TimerTask refreshTask;
			private int delay = 250;

			@Override
			public void viewportChanged() {
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
									refreshValues(false);
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

			@Override
			/**
			 * Raised when a diagram node must be custom drawn.
			 */
			public void drawNode(DrawNodeEvent e) {
				Rectangle2D rect = e.getBounds();
				rect.setFrame(rect.getMinX() + 9, rect.getMinY() + 9, rect
						.getWidth() - 16, rect.getHeight() - 16);

				Graphics2D g = e.getGraphics();
				Pen pen = (Pen) e.getNode().getPen().clone();
				pen.setWidth(4);
				pen.applyTo(g);
				g.draw(rect);
			}

			@Override
			public void linkModifying(LinkValidationEvent arg0) {
				arg0.cancelDrag();
			}

			@Override
			public void nodeModifying(NodeValidationEvent arg0) {
				arg0.cancelDrag();
			}

			@Override
			public void nodeClicked(NodeEvent e) {
				if (!enableClick) {
					return;
				}
				DiagramNode node = e.getNode();
				ShapeNode shapeNode = (ShapeNode) node;
				String id = shapeNode.getShape().getId();
				Element el = createElement(node);
				final Element fel = el;
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						if (clickTask != null) {
							clickTask.run(fel);
						}
					}
				});
			}

			private Element createElement(DiagramNode node) {
				String name = node.getTextToEdit();
				if (!(node instanceof ShapeNode))
					return null;
				ShapeNode shapeNode = (ShapeNode) node;
				Color color = shapeNode.getPen().getColor();
				String shapeId = shapeNode.getShape().getId();
				Element el = new Element(name, 0);
				if (shapeId.equals("Alternative") && color.getBlue() == 255) {
					el = new Element(name, Element.RESERVOIR);
				} else if (shapeId.equals("Rectangle")
						&& color.getBlue() == 183 && color.getGreen() == 183) {
					el = new Element(name, Element.INFLOW);
				} else if (shapeId.equals("Rectangle")
						&& color.toString().equals(
								"java.awt.Color[r=62,g=79,b=206]")) {
					el = new Element(name, Element.RESERVOIR);
				} else if (shapeId.equals("Ellipse")
						&& color.toString().equals(
								"java.awt.Color[r=0,g=0,b=255]")) {
					el = new Element(name, Element.DEMAND);
				} else if (shapeId.equals("Rectangle")
						&& color.toString().equals(
								"java.awt.Color[r=255,g=0,b=0]")) {
					el = new Element(name, Element.DEMAND);
				} else if (shapeId.equals("Rectangle")
						&& color.toString().equals(
								"java.awt.Color[r=0,g=0,b=255]")) {
					el = new Element(name, Element.CHANNEL);
				}
				return el;
			}

		});

		createActions();
	}

	private static ViewPosition toViewPosition(String val) {
		String[] fields = val.split(",");
		ViewPosition vp = new ViewPosition();
		vp.name = fields[0];
		vp.scrollX = java.lang.Float.parseFloat(fields[1]);
		vp.scrollY = java.lang.Float.parseFloat(fields[2]);
		vp.zoomFactor = java.lang.Float.parseFloat(fields[3]);
		return vp;
	}

	private static String fromViewPosition(ViewPosition vp) {
		return vp.name + "," + vp.scrollX + "," + vp.scrollY + ","
				+ vp.zoomFactor;
	}

	private static void saveViewHistory() {
		userPrefs = Preferences.userNodeForPackage(SchematicViewer.class);
		try {
			userPrefs.clear();
			for (ViewPosition vp : viewHistory) {
				userPrefs.put(vp.name, fromViewPosition(vp));
			}
			userPrefs.sync();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setEnableClick(boolean click) {
		enableClick = true;
	}

	public boolean isEnableClick() {
		return enableClick;
	}

	public void setClickTask(ElementTask r) {
		this.clickTask = r;
	}

	public Diagram getDiagram() {
		return diagram;
	}

	@SuppressWarnings("serial")
	public void createActions() {
		zoomInAction = new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				zoomIn();
			}

		};
		zoomInAction.putValue(Action.NAME, "Zoom In");
		zoomInAction.putValue(Action.SHORT_DESCRIPTION, "zoom in");
		zoomInAction
				.putValue(
						Action.SMALL_ICON,
						ImageUtil
								.createImageIcon("/wrims/schematic/images/toolbar/zoom_in.png"));

		zoomOutAction = new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				zoomOut();
			}

		};
		zoomOutAction.putValue(Action.NAME, "Zoom Out");
		zoomOutAction.putValue(Action.SHORT_DESCRIPTION, "zoom out");
		zoomOutAction
				.putValue(
						Action.SMALL_ICON,
						ImageUtil
								.createImageIcon("/wrims/schematic/images/toolbar/zoom_out.png"));

		zoomNormalAction = new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				zoomNormal();
			}
		};
		zoomNormalAction.putValue(Action.NAME, "Zoom Normal");
		zoomNormalAction.putValue(Action.SHORT_DESCRIPTION, "zoom normal");
		zoomNormalAction
				.putValue(
						Action.SMALL_ICON,
						ImageUtil
								.createImageIcon("/wrims/schematic/images/toolbar/zoom_original.png"));

		zoomBestFitAction = new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				zoomToAll();
			}
		};
		zoomBestFitAction.putValue(Action.NAME, "Zoom Fit");
		zoomBestFitAction.putValue(Action.SHORT_DESCRIPTION, "Zoom Fit");
		zoomBestFitAction
				.putValue(
						Action.SMALL_ICON,
						ImageUtil
								.createImageIcon("/wrims/schematic/images/toolbar/zoom_best_fit.png"));

		zoomRectAction = new AbstractAction() {
			private int behavior = 0;

			@Override
			public void actionPerformed(ActionEvent evt) {
				Object source = evt.getSource();
				if (!(source instanceof JToggleButton)) {
					return;
				}
				JToggleButton toggleButton = (JToggleButton) source;
				if (toggleButton.isSelected()) {
					behavior = diagramView.getBehavior();
					diagramView.setCustomBehavior(new RectangleZoomBehavior(
							diagramView));
				} else {
					diagramView.setBehavior(behavior);
				}
			}
		};
		zoomRectAction.putValue(Action.NAME, "Zoom To Rectangle");
		zoomRectAction.putValue(Action.SHORT_DESCRIPTION, "zoom rectangle");
		zoomRectAction
				.putValue(
						Action.SMALL_ICON,
						ImageUtil
								.createImageIcon("/wrims/schematic/images/toolbar/zoom_region.png"));

		zoomMagnifier = new AbstractAction() {
			private MagnifierPanel magnifierPanel;

			@Override
			public void actionPerformed(ActionEvent evt) {
				Object source = evt.getSource();
				if (!(source instanceof JToggleButton)) {
					return;
				}
				if (magnifierPanel == null) {
					magnifierPanel = new MagnifierPanel(diagramView);
				}
				JToggleButton toggleButton = (JToggleButton) source;
				magnifierPanel.setShowing(toggleButton.isSelected());
			}
		};
		zoomMagnifier.putValue(Action.NAME, "Zoom Magnifier");
		zoomMagnifier.putValue(Action.SHORT_DESCRIPTION, "Zoom Magnifier");
		zoomMagnifier
				.putValue(
						Action.SMALL_ICON,
						ImageUtil
								.createImageIcon("/wrims/schematic/images/toolbar/zoom_magnifier.png"));

		forwardViewAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				forwardView();
			}
		};
		forwardViewAction.setEnabled(false);
		forwardViewAction.putValue(Action.NAME, "Forward View");
		forwardViewAction
				.putValue(
						Action.SMALL_ICON,
						ImageUtil
								.createImageIcon("/wrims/schematic/images/toolbar/forward.png"));
		backwardViewAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				backwardView();
			}
		};
		backwardViewAction.setEnabled(false);
		backwardViewAction.putValue(Action.NAME, "Forward View");
		backwardViewAction
				.putValue(
						Action.SMALL_ICON,
						ImageUtil
								.createImageIcon("/wrims/schematic/images/toolbar/backward.png"));

		markViewAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				markView();
			}
		};
		markViewAction.putValue(Action.NAME, "Mark View");
		markViewAction
				.putValue(
						Action.SMALL_ICON,
						ImageUtil
								.createImageIcon("/wrims/schematic/images/toolbar/mark_view.png"));
		deleteViewAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				deleteView();
			}
		};
		deleteViewAction.putValue(Action.NAME, "Delete View");
		deleteViewAction
				.putValue(
						Action.SMALL_ICON,
						ImageUtil
								.createImageIcon("/wrims/schematic/images/toolbar/delete_view.png"));

		markViewComboBox = new JComboBox();
		markViewComboBox.setEditable(true);
		for (ViewPosition vp : viewHistory) {
			markViewComboBox.addItem(vp.name);
		}
		markViewComboBox.setSelectedIndex(-1);

		markViewComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				updateToViewNamed(markViewComboBox.getSelectedItem().toString());
			}
		});

		toggleLegendAction = new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				Object source = evt.getSource();
				if (!(source instanceof JCheckBoxMenuItem)) {
					return;
				}

				JCheckBoxMenuItem cb = (JCheckBoxMenuItem) source;

				showLegend(cb.isSelected());
			}

		};
		toggleLegendAction.putValue(Action.NAME, "Show Legend");

		selectFontAction = new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				selectFont();
			}
		};
		selectFontAction.putValue(Action.NAME, "Select Font...");

		selectPrecisionAction = new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				selectPrecision();
			}

		};
		selectPrecisionAction.putValue(Action.NAME, "Change Precision...");

	}

	protected void deleteView() {
		for (int i = 0; i < viewHistory.size(); i++) {
			if (viewHistory.get(i).name.equals(markViewComboBox
					.getSelectedItem().toString())) {
				viewHistory.remove(i);
				markViewComboBox.removeItem(markViewComboBox.getSelectedItem());
				break;
			}
		}
	}

	protected ViewPosition getOrAddViewPosition(String name) {
		for (ViewPosition p : viewHistory) {
			if (p.name.equals(name)) {
				return p;
			}
		}
		ViewPosition vp = new ViewPosition();
		vp.name = name;
		viewHistory.add(vp);
		markViewComboBox.addItem(name);
		return vp;
	}

	protected void markView() {
		updateViewButtonState();
		// moving on from the point
		Object selectedItem = markViewComboBox.getSelectedItem();
		String text = selectedItem == null ? "" : markViewComboBox
				.getSelectedItem().toString();
		if (text == null || text.equals("")) {
			text = ViewPosition.generateUniqueName();
		}
		// if combo box does not contain name add a viewposition with that name
		// to history and to combo box
		ViewPosition viewPosition = getOrAddViewPosition(text);
		viewPosition.setPosition(diagramView.getScrollX(), diagramView
				.getScrollY(), diagramView.getZoomFactor());
		saveViewHistory();
		lastViewPosition = viewPosition;
	}

	private void updateViewButtonState() {
		if (!hasForwardView()) {
			forwardViewAction.setEnabled(false);
		} else {
			forwardViewAction.setEnabled(true);
		}
		if (!hasBackwardView()) {
			backwardViewAction.setEnabled(false);
		} else {
			backwardViewAction.setEnabled(true);
		}
	}

	protected void selectPrecision() {
		String value = JOptionPane.showInputDialog(this,
				"Select number of decimal places to show", precision);
		try {
			precision = Integer.parseInt(value);
			refreshValues(true);
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		}
	}

	protected void showLegend(boolean selected) {
		if (selected) {
			splitPane.setDividerLocation(650);
		} else {
			splitPane.setDividerLocation(0);
		}
	}

	protected void increaseFontSize() {
		Iterable<ShapeNode> items = diagram.items(ShapeNode.class);
		for (ShapeNode item : items) {
			TextFormat textFormat = item.getTextFormat();
		}
	}

	public Action getZoomInAction() {
		return zoomInAction;
	}

	public Action getZoomOutAction() {
		return zoomOutAction;
	}

	public Action getZoomNormalAction() {
		return zoomNormalAction;
	}

	public Action getZoomToFitAction() {
		return zoomBestFitAction;
	}

	public Action getZoomRectangleAction() {
		return zoomRectAction;
	}

	public Action getZoomMagnifierAction() {
		return zoomMagnifier;
	}

	private Action getForwardViewAction() {
		return forwardViewAction;
	}

	private Action getBackwardViewAction() {
		return backwardViewAction;
	}

	public Action getMarkViewAction() {
		return markViewAction;
	}

	public Action getDeleteViewAction() {
		return deleteViewAction;
	}

	public JComboBox getMarkViewComboBox() {
		return markViewComboBox;
	}

	public Action getToggleLegendAction() {
		return toggleLegendAction;
	}

	public Action getSelectFontAction() {
		return selectFontAction;
	}

	public Action getSelectPrecisionAction() {
		return selectPrecisionAction;
	}

	public void zoomIn() {
		overview.suspendRepaint();
		diagramView.suspendRepaint();
		Rectangle2D.Float rect = diagramView.deviceToDoc(diagramView
				.getVisibleRect());
		rect.setRect(rect.x + rect.width / 2 * (1 - 1 / zoomFactor), rect.y
				+ rect.height / 2 * (1 - 1 / zoomFactor), rect.width
				/ zoomFactor, rect.height / zoomFactor);
		diagramView.zoomToFit(rect);
		diagramView.resumeRepaint();
		overview.resumeRepaint();
	}

	public void zoomOut() {
		overview.suspendRepaint();
		diagramView.suspendRepaint();
		Rectangle2D.Float rect = diagramView.deviceToDoc(diagramView
				.getVisibleRect());
		rect.setRect(rect.x - rect.width / 2 * (zoomFactor - 1), rect.y
				- rect.height / 2 * (zoomFactor - 1), rect.width * zoomFactor,
				rect.height * zoomFactor);
		diagramView.zoomToFit(rect);
		diagramView.resumeRepaint();
		overview.resumeRepaint();
	}

	public void zoomToAll() {
		diagramView.zoomToFit();
	}

	public void zoomNormal() {
		diagramView.setZoomFactor(40.0f);
	}

	/**
	 * Load schematic from binary or xml format (if file name ends with .xml)
	 * 
	 * @param filename
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws XmlException
	 */
	public void load(String filename) throws FileNotFoundException,
			IOException, XmlException {
		long ti = System.currentTimeMillis();
		diagramView.suspendRepaint();
		if (filename.endsWith(".xml")) {
			diagram.loadFromXml(filename);
		} else {
			diagram.loadFrom(filename);
		}
		this.filename = filename;
		titledBorder.setTitle(this.filename);
		Logger.getLogger(WRIMS_SCHEMATIC).fine(
				"Time to load " + filename + ": "
						+ (System.currentTimeMillis() - ti));
		Rectangle2D rect = diagram.getBounds();
		diagramView.zoomToFit(rect);
		diagramView.resumeRepaint();
		this.repaint();//repaint to show the change to the border title
		/*
		 * if (markViewComboBox.getItemCount() > 0) {
		 * markViewComboBox.setSelectedItem(viewHistory.get(0).name); }
		 */
	}
	public void setTitle(String title){
		titledBorder.setTitle(title);
		this.repaint();
	}

	public static String getTruncatedFilename(String filename) {
		if (filename == null || filename.length() < 100) {
			return filename;
		}
		return filename.substring(0, 10) + "....."
				+ filename.substring(Math.min(filename.length() - 85, 85));
	}

	public static ArrayList<ViewPosition> getHistory() {
		return viewHistory;
	}

	public void save(String filename) throws Exception {
		if (filename == null)
			return;
		Cursor previousCursor = diagramView.getCursor();
		diagramView.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
			if (filename.endsWith(".xml")) {
				diagram.saveToXml(filename);
			} else if (filename.endsWith(".pdf")) {
				PdfExporter pdfExp = new PdfExporter();
				pdfExp.export(diagram, filename);
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

	public Hashtable<String, Object> getVisibleNodes() {
		Rectangle2D.Float visibleRect = diagramView.deviceToDoc(diagramView
				.getVisibleRect());
		return getVisibleNodes(visibleRect);
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

	public void refreshValues(boolean force) {
		refreshValues(force, true);
	}

	public void refreshValues(boolean force, boolean addToHistory) {
		if (!showValueBoxes || schematic == null) {
			return;
		}
		synchronized (diagramView) {
			Rectangle2D.Float visibleRect = diagramView.deviceToDoc(diagramView
					.getVisibleRect());
			if (!force && visibleRect.equals(lastVisibleRect)) {
				return;
			}
			if (lastVisibleRect == null) {
				lastVisibleRect = visibleRect;
			}
			clearValueBoxes(lastVisibleRect);
			lastVisibleRect = visibleRect;
		}
		schematic.updateValues();
	}

	public boolean hasForwardView() {
		return (viewHistoryPointer < viewHistory.size() - 1);
	}

	public void forwardView() {
		if (!hasForwardView()) {
			return;
		}
		viewHistoryPointer++;
		updateToView();
	}

	public boolean hasBackwardView() {
		return viewHistoryPointer > 0;
	}

	public void backwardView() {
		if (!hasBackwardView()) {
			return;
		}
		viewHistoryPointer--;
		updateToView();
	}

	void updateToView() {
		ViewPosition vp = viewHistory.get(viewHistoryPointer);
		diagramView.setZoomFactor(vp.zoomFactor);
		diagramView.scrollTo(vp.scrollX, vp.scrollY);
		if (vp.name != null) {
			markViewComboBox.setSelectedItem(vp.name);
		}
		refreshValues(false, false);
		updateViewButtonState();
	}

	void updateToViewNamed(String name) {
		ViewPosition vp = null;
		int i = 0;
		for (i = 0; i < viewHistory.size(); i++) {
			vp = viewHistory.get(i);
			if (vp.name != null && vp.name.equals(name)) {
				break;
			}
		}
		if (i == viewHistory.size()) {
			return;
		}
		viewHistoryPointer = i;
		diagramView.setZoomFactor(vp.zoomFactor);
		diagramView.scrollTo(vp.scrollX, vp.scrollY);
		if (vp.name != null) {
			markViewComboBox.setSelectedItem(vp.name);
		}
		refreshValues(false, false);
	}

	public void setShowValueBoxes(boolean show) {
		showValueBoxes = show;
		if (showValueBoxes) {
			refreshValues(true);
		} else {
			clearAllValueBoxes();
		}
	}

	protected void clearValueBoxes(Float visibleRect) {
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

	public void clearAllValueBoxes() {
		for (DiagramItem item : diagram.items(ShapeNode.class)) {
			if (item == null || !(item instanceof ShapeNode)) {
				continue;
			}
			ShapeNode snode = (ShapeNode) item;
			Object id = snode.getId();
			if (id != null && id.equals(VALUE_TEXT)) {
				snode.setText("");
			}
		}
		/*
		 * DiagramNode node; while ((node = diagram.findNodeById(VALUE_TEXT)) !=
		 * null) { //diagram.getNodes().remove(node); }
		 */
	}

	public Vector<String> getSelectedNames() {
		DiagramItemList items = diagram.getSelection().getItems();
		Vector<String> names = new Vector<String>();
		for (DiagramItem item : items) {
			if (item instanceof ShapeNode) {
				names.add(((ShapeNode) item).getTextToEdit());
			}
		}
		return names;
	}

	public void saveAs(String filename) {
		try {
			save(filename);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void save() {
		try {
			diagram.saveToXml(this.filename);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getProperties() {
		// TODO Auto-generated method stub

	}

	public Vector<String> getDisplayedElementNames() {
		Iterable<ShapeNode> items = diagram.items(ShapeNode.class);
		Vector<String> names = new Vector<String>();
		for (ShapeNode item : items) {
			names.add(item.getTextToEdit());
		}
		return names;
	}

	/**
	 * Set values based on the map of values of name and values
	 * 
	 * @param values
	 * @param isCompare
	 */
	public void setValues(Hashtable<String, String>[] values, boolean isDiff) {
		if (!showValueBoxes) {
			return;
		}
		try {
			diagramView.suspendRepaint();
			synchronized (diagramView) {
				Hashtable<String, Object> visibleNodes = getVisibleNodes();
				for (int studyId = 0; studyId < values.length; studyId++) {
					Hashtable<String, String> valuesInStudy = values[studyId];
					for (String name : valuesInStudy.keySet()) {
						Object object = visibleNodes.get(name);
						if (object == null) {
							continue;
						}
						String value = valuesInStudy.get(name);
						value = truncateAfterDecimal(value, precision,
								showUnits);
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
								diagramNode.setEditedText(value);
								if (isDiff && studyId > 0) {
									((ShapeNode) diagramNode)
											.setTextColor(Color.red);
								} else {
									((ShapeNode) diagramNode)
									.setTextColor(Color.black);
								}
								((ShapeNode) diagramNode)
										.setToolTip(getToolTip(studyId, isDiff));
							}
						}
					}
				}
			}
		} finally {
			diagramView.resumeRepaint();
		}
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
		int attachPos = AttachToNode.BottomLeft;
		switch (studyId) {
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
			attachPos = AttachToNode.BottomLeft;
			r2 = new Rectangle2D.Float(r.x, r.y + r.height / 2, r.width / 2,
					r.height / 2);
			tf = new TextFormat(Align.Near, Align.Far);
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

	public static String getToolTip(int studyId, boolean isDiff) {
		switch (studyId) {
		case 0:
			return "Base";
		case 1:
			return isDiff ? "Alt 1 - Base" : "Alt 1";
		case 2:
			return isDiff ? "Alt 2 - Base" :"Alt 2";
		case 3:
			return isDiff ? "Alt 3 - Base" :"Alt 3";
		}
		return "";
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

	public void setSchematic(MainFrame schematic) {
		this.schematic = schematic;
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
			int index_currentnode = (i_node + index_lastnode) % n_node;
			DiagramNode n = nodes.get(index_currentnode);
			String nodeText = n.getTextToEdit();
			if (nodeText != null && nodeText.toLowerCase().contains(text)) {
				diagramView.bringIntoView(n);
				index_lastnode = index_currentnode;
				return true;
			}
		}
		return false;
	}

	public void setPanMode(boolean selected) {
		if (selected) {
			diagramView.setBehavior(Behavior.Pan);
		} else {
			diagramView.setBehavior(Behavior.Modify);
		}
	}

	public void selectFont() {
		Font font = getDiagram().getFont();
		JFontChooser jFontChooser = new JFontChooser();
		jFontChooser.setSelectedFont(font);
		jFontChooser.showDialog(this);
		Font newFont = jFontChooser.getSelectedFont();
		getDiagramView().suspendRepaint();
		getDiagram().setFont(newFont);
		Diagram diagram = getDiagram();
		for (DiagramItem item : diagram.items(DiagramItem.class)) {
			item.setFont(newFont);
			if (item instanceof ShapeNode) {
				ShapeNode snode = (ShapeNode) item;
			}
		}
		getDiagramView().resumeRepaint();
	}

	public DiagramView getDiagramView() {
		return diagramView;
	}

	public static class ViewPosition {
		public String name;
		public float scrollX, scrollY, zoomFactor;
		public static int uniqueId = 0;

		public void setPosition(float x, float y, float z) {
			scrollX = x;
			scrollY = y;
			zoomFactor = z;
		}

		public static String generateUniqueName() {
			return String.format("View #%3d", uniqueId++);
		}

		public boolean isSameAs(ViewPosition lastViewPosition) {
			return lastViewPosition != null
					&& Math.abs(this.scrollX - lastViewPosition.scrollX) < 1e-03
					&& Math.abs(this.scrollY - lastViewPosition.scrollY) < 1e-03
					&& Math.abs(this.zoomFactor - lastViewPosition.zoomFactor) < 1e-03;
		}

		public String toString() {
			return "View Position: " + scrollX + "," + scrollY + ","
					+ zoomFactor;
		}
	}

}

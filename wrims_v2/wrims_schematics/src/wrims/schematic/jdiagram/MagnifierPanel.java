package wrims.schematic.jdiagram;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D.Float;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import com.mindfusion.diagramming.Diagram;
import com.mindfusion.diagramming.DiagramView;

/**
 * Creates a magnified view of the current diagram view by a default factor of
 * 4.
 * 
 * @author psandhu
 * 
 */
public class MagnifierPanel extends JPanel {
	private float zoomLevel = 50;
	private DiagramView magnifiedView;
	private DiagramView diagramView;
	private JScrollPane pane;
	private JLayeredPane layeredPane;

	public MagnifierPanel(DiagramView diagramView) {
		this.diagramView = diagramView;
	}

	public void setPosition(Point point) {
		Point panePoint = SwingUtilities.convertPoint(diagramView, point,
				layeredPane);
		int px = panePoint.x - pane.getWidth() > 0 ? panePoint.x
				- pane.getWidth() : panePoint.x;
		int py = panePoint.y - pane.getHeight() > 0 ? panePoint.y
				- pane.getHeight() : panePoint.y;
		pane.setLocation(px, py);
		Float deviceToDoc = diagramView.deviceToDoc(point);
		magnifiedView.setZoomFactor(zoomLevel);
		Point point2 = magnifiedView.docToDevice(deviceToDoc);
		Rectangle visibleRect = magnifiedView.getVisibleRect();
		point2.x -= visibleRect.width / 2;
		point2.y -= visibleRect.height / 2;
		Float deviceToDoc2 = magnifiedView.deviceToDoc(point2);
		magnifiedView.setScrollX(deviceToDoc2.x);
		magnifiedView.setScrollY(deviceToDoc2.y);
		magnifiedView.invalidate();
		magnifiedView.repaint();
	}

	public void setZoomLevel(float zoomLevel) {
		this.zoomLevel = zoomLevel;
	}

	public void setShowing(boolean showing) {

		if (magnifiedView == null && showing) {
			Diagram d = diagramView.getDiagram();
			magnifiedView = new DiagramView(d);
			diagramView.addMouseMotionListener(new MouseAdapter() {
				public void mouseMoved(MouseEvent evt) {
					Point point = evt.getPoint();
					MagnifierPanel.this.setPosition(point);
				}
			});
			layeredPane = (JLayeredPane) SwingUtilities.getAncestorOfClass(
					JLayeredPane.class, diagramView);
			pane = new JScrollPane(magnifiedView,
					JScrollPane.VERTICAL_SCROLLBAR_NEVER,
					JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			pane.setSize(300, 300);
			magnifiedView.addMouseMotionListener(new MouseAdapter() {
				public void mouseMoved(MouseEvent evt) {
					Point point = SwingUtilities.convertPoint(magnifiedView,
							evt.getPoint(), MagnifierPanel.this.diagramView);
					MagnifierPanel.this.setPosition(point);
				}
			});
			pane.setBorder(BorderFactory.createRaisedBevelBorder());
		}
		if (showing) {
			layeredPane.add(pane, new Integer(1), 0);
			layeredPane.moveToFront(pane);
		} else {
			layeredPane.moveToBack(pane);
		}

	}

}

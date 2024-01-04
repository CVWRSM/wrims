package wrims.schematic.jdiagram;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.SwingUtilities;

import com.mindfusion.diagramming.BehaviorBase;
import com.mindfusion.diagramming.CursorHint;
import com.mindfusion.diagramming.DiagramView;
import com.mindfusion.diagramming.InteractionState;

public class RectangleZoomBehavior extends BehaviorBase {

	//private Image backgroundImage;

	protected RectangleZoomBehavior(DiagramView flowChart) {
		super(flowChart);

	}

	protected CursorHint setMouseCursor(Point2D point, Boolean startInteraction) {
		startInteraction = false;
		Cursor zoomRectCursor = Toolkit.getDefaultToolkit().createCustomCursor(
				ImageUtil.createImageIcon(
						"/wrims/schematic/images/toolbar/zoom_in.png")
						.getImage(), new Point(0, 0), "zoom rectangle");
		getDiagramView().setCursor(zoomRectCursor);
		return CursorHint.DontChange;
	}

	protected InteractionState startDraw(Point2D point, MouseEvent e) {
		return null;
	}

	protected void mousePressed(Point mousePosition, MouseEvent e) {
		ptStartDragDev = mousePosition;
	}

	protected void mouseMoved(Point mousePosition) {
	}

	protected void mouseReleased(Point mousePosition, MouseEvent e) {
		getDiagramView().setCursor(getDiagramView().getPointerCursor());
		getDiagramView().recreateCacheImage();
		Rectangle r = createRectFromDiagonalPoints(ptStartDragDev,
				mousePosition);

		Rectangle2D rect = getDiagramView().deviceToDoc(r);
		getDiagramView().zoomToFit(rect);

	}

	protected void mouseDragged(Point mousePosition, MouseEvent e) {
		final DiagramView view = getDiagramView();
		final Rectangle r = createRectFromDiagonalPoints(ptStartDragDev,
				mousePosition);
		view.repaint(diagramView.getVisibleRect());
		SwingUtilities.invokeLater(new Runnable() { // done later as repaint is fired
			
			@Override
			public void run() {
				view.getGraphics().drawRect(r.x, r.y, r.width, r.height);
			}
		});
	}

	public static Rectangle createRectFromDiagonalPoints(Point p1, Point p2) {
		return createRectFromDiagonalPoints(p1.x, p1.y, p2.x, p2.y);
	}

	public static Rectangle createRectFromDiagonalPoints(int x1, int y1,
			int x2, int y2) {
		return new Rectangle(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2
				- x1), Math.abs(y2 - y1));
	}

	private Point ptStartDragDev;
}

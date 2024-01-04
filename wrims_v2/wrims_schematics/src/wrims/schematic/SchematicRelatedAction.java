package wrims.schematic;

import java.awt.Container;

import javax.swing.Icon;

import wrims.schematic.jdiagram.SchematicViewer;

//
// depending on the current context.
/**
 * Define an Action that knows about views and supports enabling/disabling
 * depending on the current context.
 * 
 * @author Tom Pruett
 * @author Clay Booher
 */
public abstract class SchematicRelatedAction extends ContainerRelatedAction {
	// public Schematic getApp() {
	// return (Schematic) myApp;
	// }

	public SchematicViewer getView() {
		if (getApp() != null && getApp() instanceof MainFrame) {
			return ((MainFrame) getApp()).getCurrentView();
		} else
			return null;
	}

	public SchematicRelatedAction(String name, Container app) {
		super(name, app);
	}

	public SchematicRelatedAction(String name, Icon icon, Container app) {
		super(name, icon, app);
	}

	// by default each AppAction is disabled if there's no current view
	public boolean canAct() {
		// System.out.println(toString() + "'s canAct() fired");
		return (getView() != null);
	}
}

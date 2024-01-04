package wrims.schematic;

import java.awt.Container;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Icon;

// Define an Action that knows about views and supports enabling/disabling
// depending on the current context.
public abstract class ContainerRelatedAction extends AbstractAction {
	private Container myApp;
	private static Vector myAllActions = new Vector();

	public Container getApp() {
		return myApp;
	}

	public ContainerRelatedAction(String name, Container app) {
		super(name);
		init(app);
	}

	public ContainerRelatedAction(String name, Container app, String desc) {
		super(name);
		init(app);
		putValue(SHORT_DESCRIPTION, desc);
	}

	public ContainerRelatedAction(String name, Icon icon, Container app) {
		super(name, icon);
		init(app);
	}

	private final void init(Container app) {
		myApp = app;
		myAllActions.add(this);
	}

	public String toString() {
		return (String) getValue(NAME);
	}

	// by default each AppAction is disabled if there's no current view
	public abstract boolean canAct();

	public void updateEnabled() {
		setEnabled(canAct());
	}

	public void free() {
		myAllActions.removeElement(this);
		myApp = null;
	}

	// keep track of all instances of AppAction
	public static void updateAllActions() {
		for (int i = 0; i < myAllActions.size(); i++) {
			ContainerRelatedAction act = (ContainerRelatedAction) myAllActions
					.elementAt(i);
			act.updateEnabled();
		}
	}

	public static Vector allActions() {
		return myAllActions;
	}
}

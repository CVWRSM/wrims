package wrims.schematic;

import java.awt.Container;

import javax.swing.Icon;

/**
 * Define an Action that knows about state of GUI and supports
 * enabling/disabling depending on the current context.
 * 
 * @author Tom Pruett
 * @author Clay Booher
 */
public abstract class AppAction extends ContainerRelatedAction {

	/*
	 * CB getApp() is null upon startup so i commented it out and renamed
	 * DssAppActions two corresponding methods TODO think about what is best
	 * when I have time LOL public FilterPanel getFilterPanel() { if (getApp()
	 * != null && getApp() instanceof MainPanel) return
	 * ((MainPanel)getApp()).getFilterPanel(); else return null; }
	 */

	/*
	 * CB public MessagePanel getMessagePanel() { if (getApp() != null &&
	 * getApp() instanceof MainPanel) return
	 * ((MainPanel)getApp()).getMessagePanel(); else return null; }
	 */

	public AppAction(String name, Container app, String desc) {
		super(name, app, desc);
	}

	public AppAction(String name, Container app) {
		super(name, app);
	}

	public AppAction(String name, Icon icon, Container app) {
		super(name, icon, app);
	}

	// by default each AppAction is enabled
	public boolean canAct() {
		// return (getView() != null);
		return true;
	}
}

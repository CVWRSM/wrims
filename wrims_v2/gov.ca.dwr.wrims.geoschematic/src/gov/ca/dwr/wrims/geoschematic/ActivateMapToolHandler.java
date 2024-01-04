package gov.ca.dwr.wrims.geoschematic;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.internal.tool.display.ToolManager;

//e4 doesn't seem to support command parameters using the dihandler so we default
//to old handler here
public class ActivateMapToolHandler extends AbstractHandler {

	@Override
	public final Object execute(final ExecutionEvent event)
			throws ExecutionException {
			// Get the view identifier, if any.
		final Map parameters = event.getParameters();
		String toolid = (String) parameters.get("gov.ca.dwr.wrims.geoschematic.maptool.activate.toolid"); //$NON-NLS-1$
		((ToolManager) ApplicationGIS.getToolManager()).findToolProxy((String)toolid).run();
		return null;
	}
	
}

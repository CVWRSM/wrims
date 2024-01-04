package gov.ca.dwr.calsimhydro.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.part.ViewPart;

import wrimsv2_plugin.debugger.exception.WPPException;


public class CalSimHydroView extends ViewPart {

	private String urlString="http://mrsbmapp20565.pr.water.ca.gov/calsim-cesium.html";
	public static String ID="gov.ca.dwr.calsimhydro.views.CalSimHydroView";

	@Override
	public void createPartControl(Composite parent) {
		final Shell shell= parent.getShell();
		Display display = shell.getDisplay();
		Browser browser = new Browser(shell, SWT.NONE);
		browser.addTitleListener(new TitleListener() {
		      public void changed(TitleEvent event) {
		         shell.setText(event.title);
		      }
		});
		browser.setBounds(0,0,600,400);
		browser.setUrl(urlString);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
}

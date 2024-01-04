package wrims.schematic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Label;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.mozilla.interfaces.nsIDOMWindow;
import org.mozilla.interfaces.nsIWebBrowser;

import com.jniwrapper.win32.ie.WebBrowser;
import com.teamdev.jxbrowser.Browser;
import com.teamdev.jxbrowser.BrowserFactory;
import com.teamdev.jxbrowser.BrowserType;
import com.teamdev.jxbrowser.events.StatusChangedEvent;
import com.teamdev.jxbrowser.events.StatusListener;
import com.teamdev.jxbrowser.events.TitleChangedEvent;
import com.teamdev.jxbrowser.events.TitleListener;
import com.teamdev.jxbrowser.mozilla.*;

public class WebMap {
	public WebMap(JTabbedPane jtp, final MainFrame mainFrame){
		final Browser browser = BrowserFactory.createBrowser(BrowserType.Mozilla);  
		
		if (isInternetReachable()){
			browser.navigate("http://calsimgrid.appspot.com");
			
			JPanel p = new JPanel();
		    p.setLayout(new BorderLayout());
			
			browser.getComponent().setMinimumSize(new Dimension(1280,640));
			browser.getComponent().setPreferredSize(new Dimension(1280,640));
			p.add(browser.getComponent(), BorderLayout.NORTH);

			Label userName=new Label("User Name: BDO.Hydrology");
			userName.setForeground(Color.RED);
			p.add(userName,BorderLayout.CENTER);
			Label password=new Label("Password : BDOisNo1");
			password.setForeground(Color.RED);
			p.add(password, BorderLayout.SOUTH);
			
			jtp.add("Web Map", p);

			
			jtp.setForegroundAt(jtp.getTabCount()-1, Color.BLACK);
			jtp.setBackgroundAt(jtp.getTabCount()-1, Color.WHITE );
			browser.addTitleListener(new TitleListener(){
				@Override
				public void titleChanged(TitleChangedEvent arg0) {
					String title=browser.getTitle();
					if (title.contains(":")) {
						String[] subtitles=title.split(":");
						if (subtitles.length==2){
							if (!subtitles[1].startsWith("AD_") && !subtitles[1].startsWith("I_")) {
								boolean variableFound=true;
							}
						}
					}
				}
			});
		}
	}
	
    public static boolean isInternetReachable() {                 
    	try {       
    		URL url = new URL("http://calsimgrid.appspot.com");     
    		HttpURLConnection urlConnect = (HttpURLConnection)url.openConnection();                
    		Object objData = urlConnect.getContent();              
    	} catch (UnknownHostException e) {              
    			return false;                
    	} catch (IOException e) {                                                       
    			return false;                
    	}                
    	return true;          
    }
}

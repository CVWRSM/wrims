import com.sun.install.*;
import com.sun.install.products.*;
import com.sun.wizards.core.*;
import com.sun.wizards.registry.*;
import com.sun.wizards.panels.*;
import java.util.*;
import java.io.*;

/**  
 * WebstartSDKBuilder is built on top of the InstallArchiveWriter
 * API to facilitate easy creation of an installer for the Webstart
 * Wizards API.  Take a look at InstallArchiveWriter to see the kinds
 * of things you can customize for an installer that uses the default
 * panels.
 *
 * @author James Falkner
 * @author Paul Lovvik
 * @see com.sun.install.products.InstallArchiveWriter 
 */
public class WebstartSDKBuilder extends InstallArchiveWriter
{


  /**
   * Creates a blank WebstartSDKBuilder.  Not very useful.
   */
  public WebstartSDKBuilder()
    {
    }

  /**
   *  Creates the client panel tree. The actual tree building takes
   * place in the superclass (InstallArchiveWriter).  This method
   * simply calls into other methods that gather user input in order
   * to customize the installer.  All custom builders will want to
   * override this method to create their own customized installer.
   */
  protected void createClientTree()
    {
       super.createClientTree();
      /** 
       * Sets the name of the product.  The name appears throughout the
       * installation to tell the user what they are installing.
       */
      setProductName("Webstart Wizards SDK 3.0");

      /*
       * The initial directory the user sees.  It can be changed, though,
       * to install to a different place.  If you don't supply one, the
       * default directory panel will select the system's normal program
       * directory ("/opt" for Solaris) for the initial directory.  Any
       * custom directory selection screens might want to do the same.
       */
      setDefaultDirectory("[userDir]/WebstartSDK3.0");

      /* 
       * The name of the resulting wizard archive.  For example,
       * setting this to "myInstaller" will result in a file called
       * "myInstaller.class" to be written.  Here, we check for a supplied
       * name from the command line first.
       */

      /**
       * Use the supplied directories for the components, if supplied.
       * Otherwise, ask user for base directory and assume the rest, if
       * supplied.
       */
      String sdkBaseDirectory = System.getProperty("wizard.sdkBase");
      if (sdkBaseDirectory == null)
	{
	  sdkBaseDirectory = "/tmp/sdk";
	}
      

      /**   
       * We are about to set the components into the wizard
       * archive.  Sometimes file attributes are not correctly
       * translated.  Force them to what they should be via the
       * passed-in hashtable "forcedPermissions" (see the
       * InstallArchiveWriter class API.  The filenames in the
       * forcedPermissions hashtable are specified relative to the
       * install base directory.  
       */
      Hashtable forcedClassPermissions = new Hashtable();


      /**
       * This is how you might force permissions onto a file at buildtime.  This
       * file is executable on Solaris, but not on Windows.  If you build the archive
       * on Windows, it will not be marked executable by default, so force it to be.
       *
       */
      forcedClassPermissions.put("classes" + File.separator + "native"+
       				 File.separator + "Unix" + File.separator+
				 "Solaris" + File.separator + "Sparc" +
				 File.separator + "SolarisNativeToolkit", 
				 "rx");
      /**
       * Add the components.  Optional ones, then required ones.
       */
      addComponent(new Msg("Core Classes"), sdkBaseDirectory, "classes", true, false, forcedClassPermissions);
      addComponent(new Msg("API Documentation"), sdkBaseDirectory, "docs", true, false, null);
      addComponent(new Msg("Java Samples"), sdkBaseDirectory, "samples"+File.separator+"generic", true, false, null);
      addComponent(new Msg("Windows Tutorial and Samples"), sdkBaseDirectory, "samples"+File.separator+"windows", true, false, null);
      addComponent(new Msg("Solaris Tutorial and Samples"), sdkBaseDirectory, "samples"+File.separator+"solaris", true, false, null);
      
      
      /**
       * Required components will be installed no matter the user
       * selection.  The user will not be able to 'de-select' these
       * components, so make sure they make sense on all supported platforms
       * for your installer.
       */
      addComponent(new Msg("Readme"),  sdkBaseDirectory, "README.txt", true, true, null);
      addComponent(new Msg("Roadmap"), sdkBaseDirectory, "Roadmap.html", true, true, null);
      addComponent(new Msg("License"), sdkBaseDirectory, "LICENSE.txt", true, true, null);
      
      /*
       * Add the DesktopUnit, which adds desktop icons to the user's desktop if they
       * so desire.
       */
      DesktopUnit desktop = new DesktopUnit();
      desktop.createDesktopItem(getProductName(), "Installation Notes", "{InstallLocation}/README.txt", null);
      desktop.createDesktopItem(getProductName(), "User Guides and Tutorials", "{InstallLocation}/docs/index.html", null);
      desktop.createDesktopItem(getProductName(), "SDK License", "{InstallLocation}/LICENSE.txt", null);
      desktop.createDesktopItem(getProductName(), "Future Product Roadmap", "{InstallLocation}/Roadmap.html", null);
      desktop.createDesktopItem(getProductName(), "Uninstall "+getProductName(), "{InstallLocation}/uninstall_"+getProductName().replace(' ', '_').replace('.', '_')+".class", null);
      addComponent(desktop);

      /*
       * Set the InstallShield image into the wizard
       */
      setImage("com.sun.install.install");

      /*
       * Could set a baseline image with this line, if desired
       * setBaselineImage("com.sun.wizards.baseline");
       */
      

      /*
       * Set the resulting file name for the archive
       */
      String className = System.getProperty("install.archiveName");
      if (className == null)
      {
	 className = "setup";
      }
      setArchiveName(className);

      /*   
       * Set *localized* "about..." text.  Note that creating a Msg
       * using the these arguments will create a new Msg.  This Msg
       * will, at runtime, look up the specified resource bundle (in
       * this case com.sun.install.InstallResources] bundle.  If it
       * finds it, it will look for the corresponding text indexed at
       * "AboutText".  This is what the user actually sees.  The user
       * never sees the words "AboutText" unless a translation error
       * occursas.  If you want to provide plain, untranslated text,
       * use the Msg(String) constructor.  For example,
       * 
       * setAboutMsg(new Msg("This is my untranslated About text")); 
       */
      setAboutMsg(new Msg("com.sun.install.Install", InstallResources.MSG_ABOUT_TEXT));
      
      /*
       * Set 'Cancel' Text in the same manner.  
       */
      setCancelMsg(new Msg("com.sun.install.Install", InstallResources.MSG_CANCEL_ARE_YOU_SURE));
      
      /*
       * Set exit dialog text.
       */
      setExitMsg(new Msg("com.sun.install.Install", InstallResources.MSG_EXIT_ARE_YOU_SURE));


      /*
       * Add a progress visual.  This will display information to the user
       * during the installation.  This should only be done if the tips are
       * available on the filesystem.
       */
      File tip1 = new File(new File("images"), "tip1.gif");
      if(tip1.exists())
      {
	 SlideshowVisual slideshow = new SlideshowVisual(new String[] {"images.tip3",
									  "images.tip1",
									  "images.tip5"});
	 setProgressVisual(slideshow);
      }
    }

  
  /** 
   * First method when instantiating this class.  This instantiates
   * one of these classes and runs the builder.
   *
   * @param args A string array representing the arguments to this class 
   */
  public static void main(String[] args)
    {
      WebstartSDKBuilder sampleBuilder = new WebstartSDKBuilder();
      sampleBuilder.writeArchive();
      System.exit(0);
    }
}

//package calsim.installer;  // comment out for external build

import com.sun.install.*;
import com.sun.install.products.*;
import com.sun.wizards.core.*;
//import com.sun.wizards.registry.*;
//import com.sun.wizards.panels.*;
import com.sun.wizards.tasks.*;
import java.util.*;
import java.io.*;
//import com.sun.*;
// import calsim.gui.GuiUtils; cannot find it due to default package here and no jar for GuiUtils

/**
 *
 */
public class CalsimBuilder extends InstallArchiveWriter
{
  /**
   * Creates a blank CalsimBuilder
   */
  public CalsimBuilder(){}

  /**
   *  Creates the client panel tree. The actual tree building takes
   * place in the superclass (InstallArchiveWriter).  This method
   * simply calls into other methods that gather user input in order
   * to customize the installer.  All custom builders will want to
   * override this method to create their own customized installer.
   */
  protected void createClientTree() {
       super.createClientTree();
      /**
       * Sets the name of the product.  The name appears throughout the
       * installation to tell the user what they are installing.
       */
      setProductName("WRIMS 1.3.4 Beta (XA16)");

      /*
       * The initial directory the user sees.  It can be changed, though,
       * to install to a different place.  If you don't supply one, the
       * default directory panel will select the system's normal program
       * directory ("/opt" for Solaris) for the initial directory.  Any
       * custom directory selection screens might want to do the same.
       */
//CB      setDefaultDirectory("[userDir]/calsim"); //CB - NOT WORKING UNDER Java 1.5.0
//      setDefaultDirectory(System.getProperty("user.dir") + "/calsim");  //CB - tried, not C: or R:, but a development directory
      setDefaultDirectory("c:/calsim_1.3.4beta_xa16"); //CB

      /**
       * Use the supplied directories for the components, if supplied.
       * Otherwise, ask user for base directory and assume the rest, if
       * supplied.
       */
      String sdkBaseDirectory = System.getProperty("wizard.sdkBase");
      if (sdkBaseDirectory == null) sdkBaseDirectory = "../";
      System.out.println("sdk base directory = " + sdkBaseDirectory);


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
       * Required components will be installed no matter the user
       * selection.  The user will not be able to 'de-select' these
       * components, so make sure they make sense on all supported platforms
       * for your installer.
       */
      addComponent(new Msg("Readme"),  sdkBaseDirectory, "0Readme.txt", true, true, null);
      addComponent(new Msg("Fixed Bugs"),  sdkBaseDirectory, "fixedbugs.txt", true, true, null);
      addComponent(new Msg("Changes"),  sdkBaseDirectory, "Changes.txt", true, true, null);
      addComponent(new Msg("Copyright"),  sdkBaseDirectory, "copyright.txt", true, true, null);
      addComponent(new Msg("License"),  sdkBaseDirectory, "license.txt", true, true, null);
      addComponent(new Msg("MSR-ReadMe"),  sdkBaseDirectory, "MSR-ReadMe.txt", true, true, null);
      //      addComponent(new Msg("Copyright"), sdkBaseDirectory, "COPYRIGHT", true, true, null);
      /**
       * Add the components.  required ones then optional ones
       */
      addComponent(new Msg("jre"), sdkBaseDirectory, "jre",true,true,forcedClassPermissions);
      addComponent(new Msg("Core Classes"), sdkBaseDirectory, "lib", true, true, forcedClassPermissions);
      addComponent(new Msg("Core Executables"), sdkBaseDirectory, "bin", true, true, forcedClassPermissions);
      //addComponent(new Msg("Sample Schematics"), sdkBaseDirectory, "sample-schematics",true,true,forcedClassPermissions);
      //CB added
      addComponent(new Msg("WRIMS Schematics"), sdkBaseDirectory, "svg",true,true,forcedClassPermissions);

      /*
       * Add the DesktopUnit, which adds desktop icons to the user's desktop if they
       * so desire.
       */
      //DesktopUnit desktop = new DesktopUnit();
//      desktop.createDesktopItem(getProductName(), "CALSIM-1.0", {InstallLocation}\\bin\\WRIMS.bat", null);
      //desktop.createDesktopItem(getProductName(), getProductName(), "{InstallLocation}\\bin\\WRIMS.bat", null);
      //desktop.createDesktopItem(getProductName(), "MS Runner", "{InstallLocation}\\bin\\msr.bat", null);
      //desktop.createDesktopItem(getProductName(), "Readme", "{InstallLocation}\\0Readme.txt", null);
      //desktop.createDesktopItem(getProductName(), "Fixed Bugs", "{InstallLocation}\\fixedbugs.txt", null);
      //desktop.createDesktopItem(getProductName(), "Copyright", "{InstallLocation}\\copyright.txt", null);
      //desktop.createDesktopItem(getProductName(), "License", "{InstallLocation}\\license.txt", null);
      //addComponent(desktop);

      /*
       * Set the InstallShield image into the wizard
       */
      setImage("com.sun.install.install");

      /*
       * Could set a baseline image with this line, if desired
       * setBaselineImage("com.sun.wizards.baseline");
       */

      //      setBaselineImage(dwrlogo);
      /*
       * Set the resulting file name for the archive
       */
      String className = System.getProperty("install.archiveName");
      if (className == null) className = "calsim";
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
      File tip1 = new File(new File("images"), "tip1.gif");
      if(tip1.exists())
      {
	 SlideshowVisual slideshow = new SlideshowVisual(new String[] {"images.tip3",
									  "images.tip1",
									  "images.tip5"});
	 setProgressVisual(slideshow);
      }
       */
    }
  /**
    *
    */
  public void createWizardTree(){
    super.createWizardTree();
    Sequence install_seq = getWizardState().getSequence(InstallConstants.installSequence);
    install_seq.addTask(new PlatformTask(new Platform(Platform.WINDOWS),new BatchFileConfigTask()));
//    install_seq.addTask(new PlatformTask(new Platform(Platform.SOLARIS),new ScriptFileConfigTask()));
  }

  /**
   * First method when instantiating this class.  This instantiates
   * one of these classes and runs the builder.
   *
   * @param args A string array representing the arguments to this class
   */
  public static void main(String[] args) {
      CalsimBuilder sampleBuilder = new CalsimBuilder();
      sampleBuilder.writeArchive();
      System.exit(0);
    }
}

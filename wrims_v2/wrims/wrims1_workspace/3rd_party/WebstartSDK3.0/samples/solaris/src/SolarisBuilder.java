import com.sun.install.panels.*;
import com.sun.install.products.*;
import com.sun.wizards.core.*;
import com.sun.wizards.builder.resolver.*;
import java.util.*;
import java.io.*;

/**
 * This sample builder illustrates how one might package a
 * product to be installed on Sun Solaris-based systems.  This class
 * simply builds the product representation; it does not build any
 * panels that the user would see.  That is taken care of in the super
 * class, InstallArchiveWriter.  
 *
 * This builder uses several features of the Wizard API, including:
 *
 * 1. Solaris SVR4 package installation
 * 2. Solaris patch installation
 * 3. Bare file installation
 * 4. Dynamic environment dependencies
 * 5. Optional and Required components
 *
 * The packages and patches used in this builder reside in the
 * samples/pkgs and samples/patches directory in the Webstart Wizard
 * SDK.  This sample is also referred to in the Solaris Tutorial,
 * located in the samples/solaris/html directory of the SDK.  If you have not done so,
 * please read the Tutorial to get a better understanding of the ideas
 * used in this sample.
 *
 * NOTE: For this installation to work, you must run the wizard as
 * 'root' if you are using Solaris, since this installer utilites the
 * 'pkgadd' Solaris utility, which requires root privileges.
 *
 * The product tree that this builder builds is illustrated below:
 *
 *                            [PRODUCT]
 *                               /\  \
 *                              /  \  \_______
 *                             /    \         \
 *                            /      \        [Demos]*
 *                           /        \              \
 *                          /          \              \
 *                         /            \           [Sun JDK 1.1.6 Dependency]
 *         [Required Patches]**  [Sun Web Product]*          |
 *                    /\                  |                  |
 *                   /  \                 |              [Java Source Demos]
 *                  /    \         [Package SUNWstuf]
 *                 /      \                  
 *                /        \                  
 *   {SPARC Dependency}   {x86 Dependency}
 *           |                  |
 *           |                  | 
 *           |                  |
 *       [102341-03]         [102342-04]
 *
 *
 *        * Indicates this component is "reportable", that is one that will
 *          show up on the "Component Selection" pane.
 *       ** Indicates this component is required, that is, one that the user
 *          will not be able to de-select.
 *
 * Step 1: Laying out the product tree is covered in the SolarisTutorial.
 *
 * @author James Falkner
 * @author Paul Lovvik
 */
public class SolarisBuilder extends InstallArchiveWriter
{

      /*
       * Step 2:Begin the Builder
       */

      /**
       * Creates a blank SolarisBuilder.  Not very useful.
       */
      public SolarisBuilder()
      {
      }

      /** 
       * Creates the client panel tree. The actual client panel tree
       * building takes place in the superclass (InstallArchiveWriter).
       */
      protected void createClientTree()
      {
	 /*
	  * Step 3: Set the Product Name and Default Directory
	  *
	  * The [userDir] key in the second line gets replaced at runtime 
	  * with the "home" directory for the user that is running the wizard.
	  */
	 setProductName("Solaris Example Product");
         setDefaultDirectory("[userDir]/myroduct");

	 /* Step 4: Set the Name of the Images to Display 
	  *
	  * The com.sun.install.install specification means that the
	  * image exists in the com/sun/install directory relative to
	  * your CLASSPATH (or current directory if no class path is
	  * set), and is called install.gif, install.jpg,
	  * install.jpeg, or install.jfif. You must also make sure
	  * that your image gets put into the archive by configuring a
	  * FileResolver to get the image, and adding the image to a
	  * ResourceCollection so that it gets resolved and put into
	  * the archive. To do this, add this code to the
	  * createClientTree() method: 
	  */
         setImage("com.sun.install.install");

	 FileResolver resolver = new FileResolver(System.getProperty("java.class.path"));
         ResourceCollection collection = new ResourceCollection();
         collection.addResource("Images", "com.sun.install.install", resolver);
         addCollection(collection);

	 /*
	  *Step 5: Set Any Configurable Messages
	  *
	  * This sets some configurable messages. If you do not
	  * specify an "About..." message, then the about button will
	  * not be displayed on the Welcome Panel. The others will
	  * default to default values. The About message is displayed
	  * when the user clicks the "About" button on the *Welcome
	  * Panel. The Cancel message is displayed when the *user
	  * cancels some operation such as install or disk space
	  * *checking, by clicking the "Cancel" button. The Exit
	  * Message displays when the user clicks the "Exit" button.
	  * * Note that these messages are localized. At runtime, the
	  * specified locale resource bundle is searched according to
	  * the runtime locale. In this case the resource bundle
	  * "com.sun.install.Install" will be searched for a message
	  * corresponding to the supplied keys.  
	  */
	 setAboutMsg(new Msg("com.sun.install.Install", "AboutText"));
         setCancelMsg(new Msg("com.sun.install.Install", "CancelAreYouSure"));
         setExitMsg(new Msg("com.sun.install.Install", "ExitAreYouSure"));

	 /*
	  * Step 6:Build the Product Components
	  *
	  * A SoftwareComponent represents a node in the product tree
	  * that can show up on the ComponentPanel as selectable by
	  * the user. Each one is given a name that will appear to the
	  * user on the Component Selection screen.
	  */
	 SoftwareComponent requiredPatches = new SoftwareComponent(new Msg("Required Patches"));
         SoftwareComponent webProduct = new SoftwareComponent(new Msg("Sun Web Product"));
         SoftwareComponent demos = new SoftwareComponent(new Msg("Java Source Demos"));
         
	 /*
	  * Step 7:Create Platform Objects
	  *
	  * These objects represent a specific platform.  The objects
	  * can be used to build Platform Dependencies. See the API
	  * Documentation for instructions on how to specify your own
	  * platform.  
	  */
	 Platform sparcPlatform = new Platform(Platform.ALL, Platform.SOLARIS, Platform.SPARC);
	 Platform x86Platform = new Platform(Platform.ALL, Platform.SOLARIS,Platform.X86);

	 /*
	  * Step 8: Create Platform Dependencies
	  *
	  * These Platform Dependencies are objects inserted into the
	  * product tree. They compare the the supplied Platform
	  * object to the current platform and "fail" if they do not
	  * match. You can also invert The dependency, meaning it will
	  * "pass" when the dependency "fails", by passing true as the
	  * second argument to the constructor.
	  *
	  * Note the specification of ">=1.1". This means Java version 1.1 or later. 
	  *
	  * These will be checked just before the installation takes
	  * place, and if the platforms do not match up, they will not
	  * install anything that is attached in the tree below that
	  * object.
	  */
	 PlatformDependency sparcDependency = new PlatformDependency(sparcPlatform, false);
         PlatformDependency x86Dependency = new PlatformDependency(x86Platform, false);
         SunJDKDependency JDK11Dependency = new SunJDKDependency(">=1.1", false);
         
	 /*
	  * Step 9: Create Solaris Patch Components	 
	  *
	  * Creates our SPARC Solaris patch. We pass it the relative
	  * path to this patch (which must exist at that relative path
	  * at runtime), and the revision number.
	  */
	 PatchUnit sparcUsrPatch = new PatchUnit("../sparc", "102341-03");
         PatchUnit x86UsrPatch = new PatchUnit("../sparc", "102342-04");

	 /*
	  * Step 10: Create Solaris Package Components
	  *
	  * Creates our SVR4 package units. 
	  *
	  * SEE THE SOLARIS TUTORIAL INCLUDED IN THE SDK FOR AN EXPLAINATION OF THESE
	  * LINES! It is too lengthy to be repeated here.
	  */
	 Hashtable variables = new Hashtable();
         variables.put("basedir", InstallConstants.currentInstallDirectory);
         PkgUnit webPkg1 = new PkgUnit("../samples/solaris/packages", "more", 
                                         "../samples/solaris/packages/admin", 
                                         "../samples/solaris/packages/response", 
                                         variables, PkgUnit.BUILD_TIME);
         PkgUnit webPkg2 = new PkgUnit("../samples/solaris/packages", "stuf", 
                                         "../samples/solaris/packages/admin", 
                                         "../samples/solaris/packages/response", 
                                         variables, PkgUnit.BUILD_TIME);
         PkgUnit sunDemoPkg = new PkgUnit("../samples/solaris/packages", "demo",
                                          "../samples/solaris/packages/admin", 
                                          "../samples/solaris/packages/response", 
                                          variables, PkgUnit.BUILD_TIME);
                   
	 
	 /*
	  * Step 11: Build the product tree
	  *
	  * These lines build the tree from the bottom-up. First we
	  * create the "Binaries" subtree, then add that subtree to
	  * the product. Next, we create the "Documentation" subtree,
	  * and add that to the product. Finally, we create the
	  * multi-level "NT Java Demos" subtree, and add the subtree
	  * to the product.  
	  */
	 sparcDependency.addComponent(sparcUsrPatch);
         x86Dependency.addComponent(x86UsrPatch);
         requiredPatches.addComponent(sparcDependency);
         requiredPatches.addComponent(x86Dependency);
         webProduct.addComponent(webPkg1);
         webProduct.addComponent(webPkg2);
         JDK11Dependency.addComponent(sunDemoPkg);
         demos.addComponent(JDK11Dependency);
	 
         addComponent(requiredPatches, true, true);
         addComponent(webProduct);
         addComponent(demos);
	 
	 /*
	  * Step 12: Create Configuration Panels
	  *
	  * This creates a single Directory Selection panel and places
	  * it as a post-install panel. Post-install panels are shown
	  * after installation. You can also place pre-install panels
	  * which are shown directly before installation. 
	  */
	 Object[] nameArgs = new Object[] {getProductName()};
         DirectorySelectionPanel postConfigurePanel = new DirectorySelectionPanel(getWizardState(),
                             "Post Configuration Panel", "myDefaultDir", 
                             "myDefaultDir", "/usr/lib");
         postConfigurePanel.addDescriptionText(new Msg("com.sun.install.Install", 
                                               "PostConfigDirectoryDescription", 
                                               nameArgs));
         postConfigurePanel.addLabelText(new Msg("com.sun.install.Install", 
                                               "PostConfigTextLabel"));
         addNode(POSTINSTALL, postConfigurePanel);
	 
	 
      }
      
      /*
       * Step 13: Finish the builder
       */
      public static void main(String[] args)
      {
	 SolarisBuilder sampleBuilder = new SolarisBuilder();

	 /* 
	  * You must include a call to writeArchive() in order to
	  * actually write the archive.  
	  */
	 sampleBuilder.writeArchive();
	 System.exit(0);
      }
}

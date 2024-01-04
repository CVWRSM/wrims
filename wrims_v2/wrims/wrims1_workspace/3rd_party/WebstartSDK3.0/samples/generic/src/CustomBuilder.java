import java.io.*;
import java.util.*;
import com.sun.wizards.builder.*;
import com.sun.wizards.builder.resolver.*;
import com.sun.wizards.panels.*;
import com.sun.wizards.core.*;
import com.sun.wizards.nodes.*;
import com.sun.wizards.tasks.*;

import com.sun.install.nodes.*;
import com.sun.install.products.*;
import com.sun.install.panels.*;
import com.sun.install.tasks.*;
import com.sun.install.*;


/**
 * This custom builder class creates a basic install with a basic
 * sequence of panels.  You might generate a wizard such as this one
 * if you use the InstallArchiveWriter API as well, but it is included
 * here in source form for those who prefer to see what's going on "under
 * the hood".  Feel free to copy and modify this class as you see fit.
 *
 *
 * The CustomPanel that goes along with this builder has been placed immediately
 * after the WelcomePanel.
 * 
 * @see com.sun.wizards.builder.ArchiveWriter
 *
 * @author James Falkner 
 */
public class CustomBuilder extends ArchiveWriter
{
      /**
       * The single resource resolver used to collect resources for
       * placing into the archive.  Configured with java's class path and
       * the wizard runtime class path.
       */
      ResourceResolver classResolver = null;
      
      /**
       * Default constructor.  Instantiates the wizard, and configures it.
       */
      public CustomBuilder()
      {
	 String classPath = System.getProperty("java.class.path");
 	 if (classPath == null)
	 {
	    classPath = ".";
	 }
	
	 /**
	  * The class resolver is configured with the classpath, and the path
	  * to the runtime classes
	  */
	 classResolver = new FileResolver(getWizardRuntimeClasspath()+
					  File.pathSeparator+
					  classPath);
	 /**
	  * Configure basic wizard properties
	  */
	 configureWizard();

	 /**
	  * Include localizable resources, and images.
	  */
	 addResources();

	 /**
	  * Build a simple example product tree with one component.
	  */
	 buildProductTree();

	 /**
	  * Build the default panel sequence.
	  */
	 buildPanelTree();
      }
      
      /**
       * This does some basic wizard configurations like frame title,
       * product title, etc.
       */
      private void configureWizard()
      {
	 /*
	  * Get a runtime wizard state reference to use multiple times
	  */
	 WizardState wizardState = getWizardState();

	 /*
	  * Add the command-line parser class as an initialization task.
	  */
	 addInitializationTask(new InstallCommandLineTask());
	 
	 /**
	  * Set Title of Frame.  Msg objects are actually localizeble
	  * messages.  We hard-code the english message here for
	  * brevity.  
	  */
	 wizardState.setData("frameTitle", 
			     new Msg("Web Start Example Install Wizard"));
	 
	 /** 
	  * Set the name of the WizardState, which must be different
	  * from other wizards if this wizard is to participate in a
	  * multi-product install.  
	  */
	 wizardState.setName("MyProduct");

	 /*
	  * The archive class name.  For instance, if this is set to
	  * "myProduct" then the resulting class file written out will
	  * be named "myProduct.class"
	  */
	 setArchiveName("myProduct");

	 /**
	  * Set the progress label for this wizard.  During
	  * installation, this label is shown.  During a multi-product
	  * install, this label is shown next to the progress bar that
	  * represents this product's installation progress.
	  */
	 wizardState.setProgressLabel("install", 
		     new Msg("My Product's Install Progress..."));
	 
	 /**
	  * This is our product name.  Used in several places, so
	  * don't forget it!
	  */
	 wizardState.setData(InstallConstants.productName, "My Product Name");
	 
	 /*
	  * Set the purpose of the WizardState.  The purpose is either
	  * "install" or "uninstall".  Used for wizards that are
	  * multi-purpose (i.e. The same wizard can be run in several
	  * modes).
	  */
	 wizardState.setPurpose("install");
      }
      
      /**
       * Adds a task into the initialization task.  The initialization task
       * is the first task run, and is run when the wizard is coming up for the
       * first time
       *
       * @param task The desired task to be added to the initialization 
       *    sequence 
       */
      private void addInitializationTask(Task task)
      {
	 WizardState wizardState = getWizardState();
	 Sequence wizardInitSequence = 
	    wizardState.getSequence("wizardStateInitialization");
	 if (wizardInitSequence == null)
	    wizardInitSequence = new Sequence();
	 wizardInitSequence.addTask(task);
	 wizardState.addSequence("wizardStateInitialization", 
				 wizardInitSequence);
      }
      
      
      /**
       * Returns filenames (without paths or extensions)
       * of all files with the specified extensions from the specified path
       *
       * @param path    The path to the files.
       * @param exts    The file extensions to look for.
       */
      public static String[] getNames(String path, String[] exts)
      {
         Vector names = new Vector();
         StringTokenizer st = new StringTokenizer(path, File.pathSeparator);
         while (st.hasMoreElements())
         {
            String name = st.nextToken();
            File file = new File(name);
            if (file.isDirectory())
            {
               String[] list = file.list();
               for(int index=0; index < list.length; index++)
               {
                  File nextFile = new File(file, list[index]);
                  if (nextFile.isFile())
                  {
                     for (int extIndex = 0; extIndex < exts.length; extIndex++)
                     {
                        if (nextFile.getName().endsWith(exts[extIndex]))
                        {
                           name = nextFile.getName().substring(0, 
			       nextFile.getName().lastIndexOf(exts[extIndex]));
                           names.addElement(name);
                        }
                     }
                  }
               }
            }
         }
	 String[] result = new String[names.size()];
	 for (int index=0; index<result.length; index++)
	 {
	    result[index] = (String)names.elementAt(index);
	 }
	 return result;
      }
      
      /** 
       * Adds the resources required by this wizard.  Simply
       * creates a resource collection and adds it to the archive.
       */
      private void addResources()
      {

	  /**
	   * Collection that holds resources gathered in this method
	   */
	 ResourceCollection collection = new ResourceCollection();
	 
	 /*
	  * Add localized resource bundles for install and uninstall.
	  * Since we don't know which localized files exist when we
	  * build the wizard (since we may have added a new
	  * translation but don't want to rebuild the builder), we
	  * just collect every class file from a certain location.
	  */
	 String path = getWizardRuntimeClasspath()+
	    File.separator+"com"+File.separator+"sun"+
	    File.separator+"install";
	 
	 String[] names = getNames(path, new String[] {".class"});
	 
	 for (int index=0; index < names.length; index++)
	 {
	    names[index] = "com.sun.install."+(names[index]);
	 }
	 collection.addResources("RuntimeClasses", names, classResolver);
	 
	 /*
	  * Add images for install and uninstall.  Again, we collect
	  * every *image* file from a certain location.
	  */
	 names = getNames(path, new String[] 
	    {".gif", ".jpeg", ".jpg", ".jfif"});
	 for (int index=0; index < names.length; index++)
	 {
	    names[index] = "com.sun.install."+(names[index]);
	 }
	 collection.addResources("Images", names, classResolver);
	 
	 /*
	  * Now add this resource collection to the archive to be written out
	  * later.  Otherwise, when we go to build the wizard, it won't include
	  * the files we have just gathered.
	  */
	 addCollection(collection);
      }
      


      /**
       * This method builds the product tree for this product. 
       */
      private void buildProductTree()
      {
	 WizardState wizardState = getWizardState();
	 /*
	  * Create product tree and initialize with install task
	  */
	 ProductTask productTask = new ProductTask();
	 Sequence installSequence = new Sequence(productTask);
	 Product product = new Product(productTask, wizardState);
	 productTask.setProduct(product);

	 /*
	  * Add the install sequence to the state.
	  */
	 wizardState.addSequence(InstallConstants.installSequence, 
				 installSequence);

	 /*
	  * Add a single component to tree.  Hardcode the Msg object with
	  * the english text for brevity.
	  */
	 String componentName = "The Documentation";
	 SoftwareComponent newComponent = new SoftwareComponent(
	    new Msg(componentName));
	 
	 /*
	  * As an illustration, suppose this component is not selected 
	  * by default.  This is how you do it.
	  */
	 newComponent.setProperty("isDefault", new Boolean(false));
          
	 FileUnit docs = new FileUnit("Component "+componentName.toString());
	 docs.addFile("..", "docs", null);
	 
	 newComponent.addComponent(docs);

	 product.addComponent(newComponent);
          
	 /*
	  * Make sure the files that the FileUnit represents are
	  * included into the archive.  Each FileUnit can generate a
	  * ResourceCollection for the files it will install when the
	  * wizard is run later.
	  */
	 addCollection(docs.getCollection());

	 InstallComponent uninstallUnit = 
	    new UninstallUnit(InstallConstants.currentInstallDirectory,
			      InstallConstants.currentInstallDirectory);

	 product.addComponent(uninstallUnit);

	 /*
	  * Inform the product tree (whos root is the product
	  * variable) about the directory to install into.  Each
	  * descendent of the root node will have this directory as a
	  * prefix, unless that descendant declares that it will
	  * install into an absolute path (as determined by
	  * java.io.File's isAbsolute() API).  The key used here comes
	  * from the InstallConstants class, which all classes can
	  * reference ( a global namespace for install variables).
	  */
	 product.setInstallDirKey(InstallConstants.currentInstallDirectory);

	 /*
	  * Set the product tree we just built into the wizard state
	  * itself, under a well-known key.  
	  */
	 wizardState.setData(InstallConstants.productTree, product);

	 /**
	  * Now query the product tree for the resources that each
	  * component needs (which are declared in the recursive
	  * "addRuntimeResources()" method).  Then add the resulting
	  * collection to the archive.
	  */
	 Vector resourcesRequired = new Vector();
	 product.addRuntimeResources(resourcesRequired);

	 if (resourcesRequired != null)
	 {
	    ResourceCollection productTreeCollection = new ResourceCollection();
	    productTreeCollection.addResourceVector(resourcesRequired, 
						    classResolver);
	    addCollection(productTreeCollection);
	 }
      }

      /**
       * This method is called when it is time to create the client tree
       * for a true wizard (i.e. not a wizard that controls other wizards).
       * It should be called *after* any customizations are to be done
       * (such as changing the Welcome screen).  
       */
      public void buildPanelTree()
      {
	 /*
	  * Get the WizardState that will exist at runtime, and configure it.
	  */
	 WizardState wizardState = getWizardState();
	 
	 /*
	  * Set default directory to standard product directory (For
	  * example, this will be set to "[WIN_DRIVE]\Program Files" on
	  * Windows, "/opt" on Solaris, etc).
	  */
	 String defaultDirectory = "[productDir]";
	 
	 /**
	  * Tell components in the wizard where they will install
	  * themselves, by default. The key used here is the same state
	  * variable that is modified by the DirectorySelectionPanel, and
	  * represents where the product will install itself.
	  */
	 wizardState.setData(InstallConstants.defaultInstallDirectory, 
			     defaultDirectory);
	 wizardState.setData(InstallConstants.currentInstallDirectory, 
			     defaultDirectory);

	 /*
	  * This is the task responsible for determining what
	  * "[productDir]" and other keys will map out to.  The task is
	  * run during wizard initialization.  For each registered wizard
	  * state key (registered with the task, as is done here), the
	  * String stored under that key is scanned, and any occurance of
	  * a magical keyword is replaced with its actual value for each
	  * platform.  In this example, we want to replace "[productDir]"
	  * with the actual standard product installation location, which
	  * is of course different on each platform.
	  *
	  * Other magical strings can be found in the DirectoryResolverTask
	  * API documentation.
	  */
	 DirectoryResolverTask directoryResolver = new DirectoryResolverTask();
	 directoryResolver.registerDirectoryKey(
	    InstallConstants.defaultInstallDirectory);
	 directoryResolver.registerDirectoryKey(
	    InstallConstants.currentInstallDirectory);
	 addInitializationTask(directoryResolver);


	 /*
	  * Tell the wizard that there are no selected components and no
	  * default components.  Only default components show up as being
	  * selected on the ComponentPanel.  Also, only default
	  * components are installed during a non-interactive
	  * command-line installation (unless overridden via the
	  * "-components" option).
	  */
	 Vector selectedComponentIDs = new Vector();
      
	 wizardState.setData(InstallConstants.selectedComponents, 
			     selectedComponentIDs);
	 wizardState.setData(InstallConstants.defaultComponents, 
			     selectedComponentIDs.clone());

	 /*
	  * Now create the actual panel tree.  These references are used
	  * for convienence.
	  */
	 WizardComposite node = null;
	 WizardComposite node2 = null;
	 WizardComposite node3 = null;
      
	 /*
	  * The root of the client tree.  Note that it has already been
	  * created for us.
	  */
	 WizardComposite installRoot = getRoot();
      
	 /*
	  * The welcome panel is the first panel to show
	  */
	 installRoot.addChild(new WelcomePanel(wizardState, "Welcome"));
     
	/**
	* Optional password Panel.  Uncomment to use
	*/
//	installRoot.addChild(
//	   new PasswordPanel(wizardState, "Password Panel","obi-wan"));
 
	 /**
	  * This is our custom panel that we are inserting.  The user will
	  * see this panel directly after the Welcome Panel is shown.
	  */
	 CustomPanel myPanel = new CustomPanel(wizardState, "My Custom Panel");
	 installRoot.addChild(myPanel);

	 /**
	  * Also add the CustomServerObject and CustomTask so that our panel can
	  * access it at runtime
	  */
	 CustomServerObject obj = new CustomServerObject();
	 wizardState.addChildObject(CustomServerObject.SERVER_OBJECT_NAME, obj);

	 /**
	  * Make the task take 15 seconds to complete
	  */
	 CustomTask task = new CustomTask(15);
         Sequence sequence = new Sequence(task);
         wizardState.addSequence(CustomTask.SEQUENCE_NAME, sequence);
	  
	 /*
	  * The install type panel is next panel to be shown.
	  */
	 installRoot.addChild(new GroupedInstallTypePanel(wizardState, 
					  "Select Installation Type"));
      
	 /* 
	  * The next panel Selects which locales to install.  The list of
	  * possible locales is gathered from several sources, including:
	  *
	  * o The product tree components that declare their supported
	  *   locales via the "locales" property.
	  * o Any pre-set locales in the builder
	  * o The command line (via the -locales option) 
	  *
	  * Also, we want to skip this panel if the user selected a
	  * "standard" install (i.e. does not want to customize the
	  * installation).
	  */
	 installRoot.addChild(node = new SkipNode(wizardState, "locale",
				  InstallConstants.standardInstall));
	 node.addChild(new LocalePanel(wizardState,"Locale Selection"));
      
	 /*
	  * Next is the 64-bit selection panel.  This is mostly used for
	  * Solaris installations currently.  a product on the Solaris
	  * platform might have both 32- and 64-bit pieces.  If the user
	  * does not want to install the 64-bit pieces, he can say so in
	  * this panel.  Note that this panel does not do anything specific
	  * for 64-bit platforms.  It simply sets a value into the
	  * WizardState, which the product tree subsequently examines to
	  * determine whether or not to install 64-bit product components
	  * or features.  
	  *
	  * The panel is skipped by default (by using a "SkipNode" as its
	  * parent).  To turn this off, uncomment the commented-out line below.
	  *
	  * Also, we want to skip this panel if the user selected a
	  * "standard" install (i.e. does not want to customize the
	  * installation).  Therefore we have a 2-level SkipNode
	  * structure, which effectively performs the boolean "AND"
	  * function.
	  */
	 installRoot.addChild(node = new SkipNode(wizardState, "64bit",
			  InstallConstants.standardInstall));
	 node.addChild(node2 = new SkipNode(wizardState, 
		    InstallConstants.skip64BitSelectionPanel,
		    InstallConstants.skip64BitSelectionPanel));
	 node2.addChild(new SixtyFourBitSelectionPanel(wizardState, 
			       "64BitSelectionPanel", "64Bit"));
      
	 // uncomment to use the 64-bit panel
	 // wizardState.setData(InstallConstants.skip64BitSelectionPanel, new Boolean(false));
      
	 /*
	  * skip 64-bit selection panel by default
	  */
	 Boolean skip64Bit = 
	    (Boolean)wizardState.getData(
	       InstallConstants.skip64BitSelectionPanel);
	 if (skip64Bit == null)
	 {
	    wizardState.setData(InstallConstants.skip64BitSelectionPanel, 
				new Boolean(true));
	 }
      
	 /*
	  * Panel used to selected directory to install to.  Only shown
	  * if user is doing a custom installation (i.e. chose "Custom"
	  * on the GroupedInstallTypePanel).  This panel must be
	  * configured with the right wizard state keys so that it can
	  * set them when the user selects a new directory.
	  *
	  * This particular skip node has two children, therefore the
	  * two children are either both shown or not.  This is
	  * different from the previous 3 panels which each had their
	  * own skip node, and could be shown or skipped independently
	  * of each other. This is done so that these panels can be
	  * used in a hierarchy.  A HierarchyNode in a different
	  * wizard which is named "custom" can locate this skip node
	  * and draw it and its children into a different panel tree.
	  * See the HierarchyTutorial for more info.
	  */
	 installRoot.addChild(node = new SkipNode(wizardState, "custom", 
				  InstallConstants.standardInstall));
	 node.addChild(new DirectorySelectionPanel(wizardState, 
				   "Select Install Directory",
				   InstallConstants.defaultInstallDirectory,
				   InstallConstants.currentInstallDirectory,
				   defaultDirectory));
	 /* 
	  * Component Selection Panel.  This panel allows the user to
	  * select components.  Only show this panel if the user is doing
	  * a custom install.
	  */
	 node.addChild(new ComponentPanel(wizardState, "Component Selection"));
      
	 /*
	  * Verify panel.   Shows user what is about to be installed.
	  */
	 installRoot.addChild(new VerifyPanel(wizardState, "Verify"));
      
	 /* 
	  * install node.  The children of this node represent the actual
	  * installation of the product, and can be used in a hierarchy
	  * install.  
	  */
	 installRoot.addChild(node = new WizardComposite(wizardState, 
							 "install"));
      
	 /*
	  * progress panel.  The ProgressPanel shows the actual
	  * installation.  In the ProgressPanel's "beginDisplay()"
	  * method, the actual install *sequence* (configured above) is
	  * run, and the progress is reported back to the ProgressPanel.
	  */
	 ProgressPanel panel = null;
	 node.addChild(panel = new ProgressPanel(
	    wizardState, "Installing...", "install"));
      
	 /*
	  * Tell the panel that if the user presses "cancel" (which
	  * actually reads "Stop" during installation), the panel is to
	  * go forward, rather that the default, which is to go
	  * backwards.
	  */
	 panel.setCancelDirection(true);
      
	 /*
	  * DesktopItem Panel..  This panel asks the user if they want to
	  * add desktop icons ("shortcuts" on Windows).  If they answer
	  * YES, then this panel kicks off its associated DesktopTask,
	  * which goes and actually does the shortcut creation.  In order
	  * to create desktop shortcuts, your product tree components
	  * must declare them using the "DesktopItem" property (see the
	  * DesktopTask class API documentation for more information on
	  * how to create desktop icons).
	  */
	 installRoot.addChild(new DesktopPanel(wizardState, "Desktop Shortcuts"));
      
      
	 /**
	  * The final panel shown is the SummaryPanel, which reports the
	  * status of each product's installation.
	  */
	 installRoot.addChild(new SummaryPanel(wizardState, "Summary"));
      }

      /**
       * The main method simply instantiates one of our builders, 
       * then tells it to write itself out.
       */
      public static void main(String[] args)
      {
	  /**
	   * Instantiate the builder
	   */
	 CustomBuilder builder = new CustomBuilder();

	 /**
	  * Write out the archive
	  */
	 builder.writeArchive();
      }
}

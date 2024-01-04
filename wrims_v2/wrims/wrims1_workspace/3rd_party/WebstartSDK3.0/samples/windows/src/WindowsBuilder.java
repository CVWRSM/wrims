import com.sun.install.panels.*;
import com.sun.install.products.*;
import com.sun.wizards.builder.resolver.*;
import com.sun.wizards.core.*;
import java.util.*;
import java.io.*;

/** 
 * The following example builds a fictional product that consists of
 * a set of files. The java source code will only be installed if the
 * user is running Windows NT.
 *
 * Step 1: Laying out the product tree is covered in the WindowsTutorial 
 * included in the SDK.
 */
public class WindowsBuilder extends InstallArchiveWriter 
{ 
      /*
       * Step 2: Begin the builder
       *
       */
      public WindowsBuilder() 
      { 
	 super(); 
      }
      
      protected void createClientTree()
      {
	 super.createClientTree();

	 /*
	  * Step 3: Set the Product Name and Default Directory
	  *
	  * The [userDir] key in the second line gets replaced at runtime 
	  * with the "home" directory for the user that is running the wizard.
	  */
	 setProductName("My Windows Sample Web Server");
         setDefaultDirectory("[userDir]\\http");

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
	 SoftwareComponent binaries = new SoftwareComponent(
	    new Msg("Binary Files"));
         SoftwareComponent documentation = new SoftwareComponent(
	    new Msg("Documentation"));
         SoftwareComponent NTjavademos = new SoftwareComponent(
	    new Msg("NT Java Demos"));

	 /*
	  * Step 7:Create a Platform Object
	  *
	  * This object represents a specific platform (Microsoft
	  * WindowsNT, running on any architecture). This object can
	  * be used to build Platform Dependencies. See the API
	  * Documentation for instructions on how to specify your own
	  * platform.  
	  */
	 Platform ntPlatform = new Platform(Platform.WINDOWS, 
					    Platform.WINNT, 
					    Platform.ALL);

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
	  * These will be checked just before the installation takes
	  * place, and if the platforms do not match up, they will not
	  * install anything that is attached in the tree below that
	  * object.
	  */
	 PlatformDependency ntDependency = new PlatformDependency(ntPlatform, 
								  false);

	 /*
	  * Step 9: Create File Components
	  *
	  * For each File collection, you must declare which files it
	  * will install (by using the addFile() API of FileUnit), and
	  * then add those files to the archive via the
	  * addCollection() API of InstallArchiveWriter.
	  */

	 FileUnit binFiles = new FileUnit("Bin Files");
         binFiles.addFile("\\sdk\\samples\\windows\\testfiles", "binfiles", 
			  null);
         addCollection(binFiles.getCollection());

         FileUnit docFiles = new FileUnit("Doc Files");
         docFiles.addFile("\\sdk\\samples\\windows\\testfiles", "docfiles", 
			  null);
         addCollection(docFiles.getCollection());

         FileUnit javaFiles = new FileUnit("Java Files");
         javaFiles.addFile("\\sdk\\samples\\windows\\testfiles", "javafiles", 
			   null);
         addCollection(javaFiles.getCollection());

	 /*
	  * Now all of the components of the Product Tree have been
	  * built. In the next step, they will all be pieced together
	  * to form the entire product tree.
	  */

	 /* 
	  * Step 10: Build the product tree
	  *
	  * These lines build the tree from the bottom-up. First we
	  * create the "Binaries" subtree, then add that subtree to
	  * the product. Next, we create the "Documentation" subtree,
	  * and add that to the product. Finally, we create the
	  * multi-level "NT Java Demos" subtree, and add the subtree
	  * to the product.  
	  */
	 binaries.addComponent(binFiles);
         addComponent(binaries);
	 
         documentation.addComponent(docFiles);
         addComponent(documentation);
	 
         ntDependency.addComponent(javaFiles);
         NTjavademos.addComponent(ntDependency);
         addComponent(NTjavademos);

	 /*
	  * Step 11: Create Configuration Panels
	  *
	  * This creates a single Directory Selection panel and places
	  * it as a post-install panel. Post-install panels are shown
	  * after installation. You can also place pre-install panels
	  * which are shown directly before installation. 
	  */
	 Object[] nameArgs = new Object[] {getProductName()};
         DirectorySelectionPanel postConfigurePanel = 
	    new DirectorySelectionPanel(
	       getWizardState(),
	       "Post Configuration Panel", "myDefaultDir", 
	       "myDefaultDir", "\\http");
         postConfigurePanel.addDescriptionText(
	    new Msg("com.sun.install.Install", 
		    "PostConfigDirectoryDescription", 
		    nameArgs));
         postConfigurePanel.addLabelText(new Msg("com.sun.install.Install", 
						 "PostConfigTextLabel"));
         addNode(POSTINSTALL, postConfigurePanel);
      }
      
      /*
       * Step 12: Finish the builder
       *
       * This is the main method of your builder. This simply
       * instantiates a builder and writes it out to the current
       * directory.
       * 
       *  After this step, you should have a complete, runnable builder.
       */
      public static void main(String[] args)
      {
	 WindowsBuilder sampleBuilder = new WindowsBuilder();
	 sampleBuilder.writeArchive();
	 System.exit(0);
      }
}

import com.sun.install.products.*;
import com.sun.wizards.core.Msg;
import java.util.*;
import java.io.*;

/*
 * This class is intended to be used with the HierarchyTutorial
 * included in the Web Start Wizards SDK
 * @author James Falkner
 */
public class LowerLevelHierarchyBuilder extends InstallArchiveWriter
{
      
      private String productName = null;
      private String componentName = null;
      private String componentPath = null;
               
      public static String usage = "Usage: LowerLevelHierarchyBuilder "+
      "{product name} {component name} "+
      "{component path}";
               
      public LowerLevelHierarchyBuilder(String productName,
				  String componentName, 
				  String componentPath)
      {
	 this.productName = productName;
	 this.componentName = componentName;
	 this.componentPath = componentPath;
      }
               
      private void configureWizard()
      {
	 if ((this.productName == null) || 
	     (this.componentPath == null) ||
	     (this.componentName == null))
                     
	 {
	    System.out.println(usage);
	    System.exit(-1);
	 }

	 super.createClientTree();
	 setProductName(productName);
	 setArchiveName(productName);
                  
	 /*
	  * Create Unit to hold files to install
	  */
	 FileUnit files = new FileUnit("Files for "+componentName);
                  
	 File file = new File(componentPath);
                  
	 if ((file == null) || (!file.exists()))
	 {
	    System.out.println(usage);
	    System.exit(-1);
	 }
                  
	 String filename = file.getName();
	 String parent = file.getParent();
	 /* 
	  * Add the files to the Unit
	  */
	 files.addFile(parent, filename, null);

	 /*
	  * Add the Unit's collection to the archive
	  */
	 addCollection(files.getCollection());

	 /*
	  * Create the selectable component for the user to click on
	  */
	 SoftwareComponent program = new SoftwareComponent(
	    new Msg(componentName));

	 /*
	  * Add the files to the selectable component
	  */
	 program.addComponent(files);
                  
	 /*
	  * Add the selectable component to the overall install
	  */
	 addComponent(program);
      }

      public static void main(String[] args)
      {
	 if (args.length != 3)
	 {
	    System.out.println(usage);
	    System.exit(-1);
	 }
	 else
	 {
	    LowerLevelHierarchyBuilder s = 
	       new LowerLevelHierarchyBuilder(args[0], args[1], args[2]);
                     
	    /*
	     * Configure my wizard
	     */
	    s.configureWizard();
                     
	    /*
	     * Write out the archive and exit with a 0 code, indicating
	     * success
	     */
	    s.writeArchive();
	    System.exit(0);
	 }
      }
}

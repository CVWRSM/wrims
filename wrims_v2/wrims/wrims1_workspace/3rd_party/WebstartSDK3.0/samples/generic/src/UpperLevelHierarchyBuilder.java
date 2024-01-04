import com.sun.install.products.*;
import com.sun.wizards.core.Msg;
import java.util.*;
import java.io.*;

/*
 * This class is intended to be used with the HierarchyTutorial
 * included in the Web Start Wizards SDK
 * @author James Falkner
 */
public class UpperLevelHierarchyBuilder extends InstallArchiveWriter
{
           
      /*
       * How to use this class
       */
      public static String usage = "Usage: UpperLevelHierarchyBuilder "+
      "{wizard1, wizard2, ..., wizardN}";
           
      /*
       * Names of wizards we will control
       */
      private String[] wizards = null;
           
      public UpperLevelHierarchyBuilder(String[] wizards)
      {
	 this.wizards = wizards;
      }
           
      private void configureWizard()
      {
              
	 /*
	  * Simple error checking
	  */
	 if ((this.wizards == null) || (wizards.length <= 0))
	 {
	    System.out.println(usage);
	    System.exit(-1);
	 }
	 else
	 {
	    /*
	     * Set name of resulting .class file
	     */
	    setArchiveName("connector");
                 
	    /* 
	     * Tell superclass to write connector wizard archive
	     */
	    setConnector(true);
                 
	    /*
	     * Add each wizard specified on command line to upper-level wizard
	     */
	    for (int index=0; index < wizards.length; index++)
	    {
	       String wizardToAdd = wizards[index];
	       System.out.println("Adding wizard: "+wizardToAdd);
	       addWizard(wizardToAdd, DEFAULT_INSTALL);
	    }
	 }
      }
           
      public static void main(String[] args)
      {
	 UpperLevelHierarchyBuilder s = 
	    new UpperLevelHierarchyBuilder(args);
              
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

import com.sun.install.*;
import com.sun.install.products.*;
import com.sun.wizards.core.*;
import com.sun.wizards.builder.resolver.*;
import java.util.*;
import java.io.*;

/** 
 * SampleBuilder is built on top of the InstallArchiveWriter API to
 * facilitate easy creation of an installer for a generic set of
 * product components and products.  You can use this class to create
 * a basic installer, while customizing certain aspects of the
 * installation.  Look at InstallArchiveWriter to see the API for
 * customizing an install wizard.
 * <p>
 * You can also use this builder at the command-line.  The usage is:
 * <pre><blockquote>
  *
 * example: java -DproductName=BlueMountain -DarchiveName=blue -DproductVersion=1.1 -DinstallDir=C:\program_files
 *		 -DcomponentName.1=Server -DcomponentPath.1=c:\product\build\server
 *		 -DcomponentName.2=Doc -DcomponentPath.2=c:\product\build\doc SampleBuilder
 *<ul>
 *<li>      -DproductName     -   Sets the product name
 *<li>      -DarchiveName     -   Sets the name of the arcive to be written
 *<li>      -DproductVersion  -   Sets the Version number of the product
 *<li>      -DinstallDir      -   Sets the install default directory
 *<li>      -DcomponentName.1 -   The first component Name
 *<li>      -DcomponentName.2 -   The second component Name
 *<li>      -DcomponentName.X -   The x th component Name
 *<li>      -DcomponentPath.1 -   The first component location of files to place in archive
 *<li>      -DcomponentPath.2 -   The second component location of files to place in archive
 *<li>      -DcomponentPath.X -   The x th component location of files to place in archive
 *</ul>
 * </blockquote></pre>
 *
 * @author James Falkner
 * @author Paul Lovvik
 * @see com.sun.install.products.InstallArchiveWriter 
 */
public class SampleBuilder extends InstallArchiveWriter
{
      private static boolean connector = false;
      /**
       * Creates a blank SampleBuilder
       */
      public SampleBuilder()
      {
      }



      /** 
       * Creates the client panel tree. The actual tree building takes
       * place in the superclass.  This method simply calls into other
       * methods that gather user input in order to customize the
       * installer.  
       */
      protected void createClientTree()
      {
	 /*
	  * Set the product up
	  */
	 if (connector)
	 {
	    createConnector();
	 }
	 else
	 {
	    createWizard();
	 }
      }

      /** Collects user input in order to create a client panel tree that
       * will actually install software, rather than controlling other
       * independent wizards (a wizard "connector").
       */
      public void createWizard()
      {
	 String productName = null;
	 String productVersion = null;
	 String installDir = null;

	 try
	 {
	    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	    String answer = null;
	
	    productName = System.getProperty("productName");
	    if ( productName == null )
	    {
	       System.out.println("-------------------------------------");
	       System.out.print("What is the product name: ");
	       productName = in.readLine();
	    }
	    else
	    {
	       System.out.println("Product Name: "+productName);
	    }
	    /*
	     * Hard-code the product version.  
	     */
	    //	productVersion = System.getProperty("productVersion");
	    productVersion = "1.0";
	    if ( productVersion == null )
	    {
	       System.out.print("What is the version of " + productName + ": ");
	       productVersion = in.readLine();
	    }
	    else
	    {
	       System.out.println("Product Version: "+productVersion);
	    }
	    String archiveName = System.getProperty("archiveName");
	    if ( archiveName == null )
	    {
	       System.out.print("What is the name of the install archive: ");
	       archiveName = in.readLine();
	    }
	    else
	    {
	       System.out.println("Archive name: "+archiveName);
	    }
	    /*
	     * Hard-code the installdir
	     */
	    //	installDir = System.getProperty("installDir");
	    installDir = "[productDir]/" + productName.replace(' ', '_');
	    if ( installDir == null )
	    {
	       System.out.print("What is the default install directory: ");
	       installDir = in.readLine();
	    }
	    else
	    {
	       System.out.println("Default Install Directory: "+installDir);
	    }
	
	    setProductName(productName);
	    setDefaultDirectory(installDir);
	    setArchiveName(archiveName);
	
	    Integer count = new Integer("0");
	    String componentName = new String("");
	    String componentPath = new String("");
	    boolean found_component = false;

	    /*
	     * Use a single product component, namely the product itself.
	     */
	    System.out.print("Where are the files that compose "+productName+": ");
	    String fileDir = in.readLine();
	    File component = new File(fileDir);
	    if (component.exists())
	    {
	       String parent = component.getParent();
	       if (parent == null)
	       {
		  parent = System.getProperty("user.dir");
	       }
	       addComponent(new Msg(archiveName), parent, component.getName(), true, false,  null);
	    }
	    else
	    {
	       System.out.println("Sorry, "+fileDir+" does not exist, quitting...");
	       System.exit(0);
	    }

	    /*
	     * This commented block could allow you to add more components, if desired.
	     */
	    /*
	    for ( int index = 1; (componentName != null) && (componentPath != null); index++ ) 
	    {
	       componentName = System.getProperty("componentName" + "." + index);
	       componentPath = System.getProperty("componentPath" + "." + index);
	       if (componentName != null) 
	       {
		  if (componentPath == null)
		  {
		     System.out.print("You must supply a path for component "+componentName+": ");
		     componentPath = in.readLine();
		  }
		  System.out.println("Component #"+index+": "+componentName + " " + componentPath);
		  component = new File(componentPath);
		  if (component.exists())
		  {
		     String parent = component.getParent();
		     if (parent == null)
		     {
			parent = System.getProperty("user.dir");
		     }
		     addComponent(new Msg(componentName), parent, component.getName(), true, false, null);
		     found_component = true;
		  }
	       }
	    }
	    
	    if ( found_component == false )
	    {
	       System.out.print("How many components does " + productName + " have: ");
	       int componentCount = new Integer(in.readLine()).intValue();
	       for(int index = 1; index < componentCount + 1; index++)
	       {
		  System.out.print("Enter the name for component " + index + ": ");
		  componentName = in.readLine();
		  
		  System.out.print("Enter the path for component "+componentName+": ");
		  componentPath = in.readLine();
		  component = new File(componentPath);
		  if (component.exists())
		  {
		     String parent = component.getParent();
		     if (parent == null)
		     {
			parent = System.getProperty("user.dir");
		     }
		     addComponent(new Msg(componentName), parent, component.getName(), true, false, null);
		  }
	       }
	    }
	    */
	       

	    /* 
	     *Set some images to appear.  This will give me
	     * "com/sun/install/install.gif" as my image.  
	     */
	    setImage("com.sun.install.install");
	
	    /*
	     * Set about.. text
	     */
	
	    setAboutMsg(new Msg("com.sun.install.Install", InstallResources.MSG_ABOUT_TEXT));

	    /*
	     * Set 'Cancel' Text
	     */
	    setCancelMsg(new Msg("com.sun.install.Install", InstallResources.MSG_CANCEL_ARE_YOU_SURE));
	
	    /*
	     * Set exit dialog text.
	     */
	    setExitMsg(new Msg("com.sun.install.Install", InstallResources.MSG_EXIT_ARE_YOU_SURE));
	 }
	 catch (IOException e)
	 {
	    System.out.println("IO Exception while prompting user, Quitting...");
	    e.printStackTrace();
	    System.exit(1);
	 }
      }

      /** 
       * Collects user input in order to build a wizard connector that
       * will, at runtime, call into other independent wizards to
       * facilitate a multi-product install.  
       */
      public void createConnector()
      {
	 BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	 String answer = null;
      
	 /**
	  * Get/set the archive name and connector name.
	  */
      
	 try
	 {
	    System.out.print("What is the connector name? ");
	    String productName = in.readLine();
	    System.out.print("What is the name of the connector archive? ");
	    String archiveName = in.readLine();
	    if(archiveName.endsWith(".class"))
	    {
	       archiveName = archiveName.substring(0, archiveName.length() - ".class".length());
	    }
	    setProductName(productName);
	    setArchiveName(archiveName);
	  
	    /**
	     * Get the information about children of this connector.
	     */
	    System.out.print("How many children does this wizard control?");
	    answer = in.readLine();
	    int count = Integer.parseInt(answer);
	    for(int index = 0; index < count; index++)
	    {
	       System.out.print("Enter the relative path to child #" + (index + 1) + ": ");
	       answer = in.readLine();
	       System.out.println("What is the default installation option for child " +
				  answer + " ");
	       System.out.println("0 = No Installation.");
	       System.out.println("1 = Default Installation.");
	       System.out.println("2 = Custom Installation.");
	       System.out.print(": ");
	       String installTypeString = in.readLine();
	       if(!answer.endsWith(".class"))
	       {
		  answer = answer + ".class";
	       }
	       int installType = Integer.parseInt(installTypeString);
	       addWizard(answer,installType); 
	    }

	    /*
	     * Set 'Cancel' Text.  
	     */
	    setCancelMsg(new Msg("com.sun.install.Install", InstallResources.MSG_CANCEL_ARE_YOU_SURE));
	  
	    /*
	     * Set exit dialog text.
	     */
	    setExitMsg(new Msg("com.sun.install.Install", InstallResources.MSG_EXIT_ARE_YOU_SURE));

	 }
	 catch (java.io.IOException e)
	 {
	    System.out.println("IO Exception while prompting user, Quitting...");
	    e.printStackTrace();
	    System.exit(1);
	 }
      }

      /**
       * First method when instantiating this class.  This fires off either a wizard connector builder
       * or a true wizard builder.  To create a wizard connector, use the <code>-connector</code> argument
       * to this class (i.e. java SampleBuilder -connector).
       *
       * @param args A string array representing the arguments to this class
       */
      public static void main(String[] args)
      {
	 String outputName = null;
	 for(int index = 0; index < args.length; index++)
	 {
	    String command = args[index];
	    if(command.equals("-connector"))
	    {
	       System.out.println("Creating connector wizard");
	       connector = true;
	    }
	    else if(command.equals("-o"))
	    {
	       if ((index+1) < args.length)
	       {
		  outputName = args[index+1];
		  index++;
	       }
	       else
	       {
		  System.out.println("-o requires an argument");
		  System.exit(-1);
	       }
	    }
	 }
	 if (outputName != null)
	 {
	    Properties properties = System.getProperties();
	    properties.put("archiveName", outputName);
	    System.setProperties(properties);
	 }
	 SampleBuilder sampleBuilder = new SampleBuilder();
	 sampleBuilder.setConnector(connector);
	 sampleBuilder.writeArchive();
	 System.exit(0);
      }
}

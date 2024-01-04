//package calsim.installer;  // comment out for external build

import java.io.*;
import java.util.*;
import com.sun.wizards.core.*;
//import com.sun.wizards.services.*;

/**
 * The generic task is a sample task that does
 * nothing.  The task is initialized with the
 * amount of time the task should take.  The
 * task merely waits for the specified time.
 */
public class ScriptFileConfigTask extends Task implements Serializable
{

      public static final String SEQUENCE_NAME = "Script File Configuration";
  /**
   * A flag indicating whether or not this task has been canceled.
   */
  private transient boolean canceled = false;

  /**
   * Creates a CustomTask that waits the specified
   * length of time, in seconds.
   *
   * @param completionTime	The number of seconds this task
   *				takes to complete.
   */
  public ScriptFileConfigTask()
    {
    }

  /**
   * Perform this task.  This method merely waits the amount
   * of time specified in the constructor.
   */
  public void perform()
    {
      setProgress(5);
      try {
	writeOutUnixScripts();
      }catch(IOException ioe){
	System.err.println("Error installing vista");
      }
      setProgress(100);
    }
  /**
    *
    */
  public void writeOutUnixScripts() throws IOException{
    String idir= com.sun.install.products.InstallConstants.currentInstallDirectory;
    WizardState ws = getWizardState();
    idir = (String) ws.getData(idir);
    PrintWriter pw = new PrintWriter(new FileWriter(idir+"/bin/calgui"));
    pw.println("#! /bin/ksh");
    pw.println("# set up default values");
    pw.println("# generated from installer...");
    pw.println("export CALSIMOAS_HOME");
    pw.println("CALSIMOAS_HOME="+idir);
    pw.println("PRG=`whence $0` >/dev/null 2>&1");
    pw.println("V_HOME=`dirname $PRG`/..");
    pw.println("progname=`basename $0`");
    pw.println("# if CALSIMOAS_HOME not set then use default");
    pw.println("if [ -z \"$CALSIMOAS_HOME\" ] ; then");
    pw.println("    export CALSIMOAS_HOME");
    pw.println("    CALSIMOAS_HOME=$V_HOME");
    pw.println("fi");
    pw.println("# set library path for local client");
    pw.println("if [ -z \"${LD_LIBRARY_PATH}\" ] ; then");
    pw.println("    LD_LIBRARY_PATH=$CALSIMOAS_HOME/lib");
    pw.println("else");
    pw.println("    LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$CALSIMOAS_HOME/lib");
    pw.println("fi");
    pw.println("export LD_LIBRARY_PATH");
    pw.println("#");
    pw.println("exec jre -mx32m -classpath $JAVA_HOME/lib/classes.zip:$CALSIMOAS_HOME/lib/vista.jar:$CALSIMOAS_HOME/lib/jpython.jar:$CALSIMOAS_HOME/lib/Acme.jar:$CALSIMOAS_HOME/lib/oro.jar:$CALSIMOAS_HOME/lib/COM.jar:$CALSIMOAS_HOME/lib/swingall.jar:$CALSIMOAS_HOME/lib/mail.jar:$CALSIMOAS_HOME/lib/activation.jar:$CALSIMOAS_HOME/lib/collections.jar:$CALSIMOAS_HOME/lib/mac.jar vista.app.MainGUI ");
    pw.close();
  }
  /**
   * Cancel this task.
   */
  public void cancel()
  {
    this.canceled = true;
  }

  /**
   * Add the runtime class requirements to the specified vector.
   * @param resourceVector The vector containing all runtime resources for this wizard.
   */
  public void addRuntimeResources(Vector resourceVector)
  {
    resourceVector.addElement(new String[] {null, "ScriptFileConfigTask"});
  }
}

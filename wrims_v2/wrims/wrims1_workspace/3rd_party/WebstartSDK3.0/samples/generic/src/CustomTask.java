import java.io.*;
import java.util.*;
import com.sun.wizards.core.*;
import com.sun.wizards.services.*;

/**
 * The generic task is a sample task that does
 * nothing.  The task is initialized with the
 * amount of time the task should take.  The
 * task merely waits for the specified time.
 */
public class CustomTask extends Task implements Serializable
{

      public static final String SEQUENCE_NAME = "CustomTask's Sequence";
  /**
   * The number of seconds this task takes to complete.
   */
  private int completionTime = 0;
  
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
  public CustomTask(int completionTime)
    {
      this.completionTime = completionTime;
    }

  /**
   * Perform this task.  This method merely waits the amount
   * of time specified in the constructor.
   */
  public void perform()
    {
      /*
       * This is the number of progress bar updates per second.
       */
      int ticksPerSecond = 4;

      /*
       * Calculate the progress per update.
       */
      double progressPerTick = ((double)100/((double)ticksPerSecond*(double)completionTime));


      /*
       * Update the progress bar <code>tick</code> times
       * per second.
       */
      for(int tick = 0; tick <= completionTime * ticksPerSecond;
	  tick++)
	{
	  if(canceled)
	    {
	      return;
	    }
	  try
	    {
	      Thread.sleep(1000/ticksPerSecond);
	    }
	  catch(InterruptedException e)
	    {
	    }
	  setProgress((int)(progressPerTick * tick));
	}
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
    resourceVector.addElement(new String[] {null, "CustomTask"});
  }
}

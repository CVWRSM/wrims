import java.io.*;
import java.util.*;
import com.sun.wizards.core.*;

/**
 * The CustomServerObject is an object that is used to see if other files
 * exist.
 */
public class CustomServerObject implements ServerObject, Serializable
{
      public static final String SERVER_OBJECT_NAME = "CustomServerObject";

      /**
       * A runtime handle of the WizardState that we belong to
       */
      private transient WizardState wizardState = null;

      /**
       * Create a new CustomServerObject 
       */
      public CustomServerObject()
      {
      }

      /**
       * This method sets the WizardState into the object at runtime
       */
      public void setWizardState(WizardState wizardState)
      {
	 this.wizardState = wizardState;
      }

      /**
       * Get the runtime classes required by this ServerObject.
       */
      public void addRuntimeResources(Vector resourceVector)
      {
	 resourceVector.addElement(new String[] {null, "CustomServerObject"});
      }

      /**
       * Sees if a file exists.
       *
       */
      public Boolean doesExist(String fileName)
      {
	 if (fileName == null)
	 {
	    return new Boolean(false);
	 }
	 
	 File file = new File(fileName);
	 return new Boolean(file.exists());
      }

}

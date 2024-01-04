import java.awt.*;
import java.awt.event.*;
import java.util.*;
import com.sun.wizards.*;
import com.sun.wizards.core.*;

/**
 * CustomPanel asks the user for a filename
 */
public class CustomPanel extends WizardLeaf
{

      /**
       * Set to true once user gives good file
       */
      private boolean passed = false;

      /**
       * Holds user answer
       */
      private TextField file = null;

      /**
       * Shows prompts, and overall progress during Task execution.
       */
      private Label label = null;

      /**
       * The prompt
       */
      public static final String PROMPT = "Enter Filename:";

      /**
       * Creates a CustomPanel with no name.
       */
      public CustomPanel()
      {
      }

      /**
       * Creates a CustomPanel with the specified name
       * that presents the specified application for
       * installation.
       */
      public CustomPanel(WizardState wizardState, String name)
      {
	 super(wizardState, name);
      }

      /**
       * Creates a CustomPanel with the specified name, the specified
       * route and  wizard manager.
       */
      public CustomPanel(String name, Route route, WizardTreeManager wizardManager)
      {
	 super(name, route, wizardManager);
      }

      /**
       * This method creates the user interface.
       */
      public void createUI()
      {
	 super.createUI();

	 file = new TextField(40);
	 label = new Label(PROMPT);
	 GridBagLayout gbl = new GridBagLayout();
	 Panel panel = new Panel(gbl);

	 GridBagConstraints gbc = new GridBagConstraints();
	 gbc.gridwidth = GridBagConstraints.REMAINDER;
	 gbc.insets = new Insets(20,10,20,10);
	 panel.add(label, gbc);
	 panel.add(file, gbc);

	 add(panel, "Center");
      }

      /**
       * Called automatically during sequence's progress
       */
      public void setProgress(int[] progress)
      {
	 if ((progress != null) && (progress.length >= 1))
	 {
	    label.setText("Progress: "+progress[0]+"%");
	 }
      }

      /**
       * Called when user presses "Next"
       */
      public boolean isDisplayComplete()
      {
	 WizardTreeManager manager = getTreeManager();
	 if (passed)
	 {
	    label.setText("Disabled");
	    file.setEnabled(false);
	    return true;
	 }
	 else
	 {
	    Route serverObjectRoute = getRoute().getChildServerRoute(CustomServerObject.SERVER_OBJECT_NAME);
	    
	    /**
	     * Use server object
	     */
	    Boolean exists = (Boolean)
	       manager.callServerObjectMethod(
		  serverObjectRoute.copy(),
		  "doesExist",
		  new String[] {"java.lang.String.class"},
		  new Object[] {file.getText()});
	    
	    if (exists.booleanValue())
	    {
	       
	       /**
		* User Task/Sequence
		*/
	       manager.setButtonEnabled("next", false);
	       manager.setButtonEnabled("back", false);
	       
	       manager.callServerObjectMethod(
		  getRoute(),
		  "performSequence",
		  new String[] {"com.sun.wizards.core.Route.class",
				   "java.lang.String.class",
				   "java.lang.Boolean.TYPE",
				   "java.lang.String.class"},
		  new Object[] {getRoute(null), CustomTask.SEQUENCE_NAME,
				   new Boolean(false), "sequenceComplete"});
	       
	    }
	    else
	    {
	       manager.displayQuery(
		  this, 
		  "File does not exist.  Cannot run CustomTask", 
		  new String[] {"Dismiss"}, 
		  null);
	    }
	    return false;
	 }
      }
      /**
       * Called when sequence is complete.  We tell user and advance automatically.
       */
      public void sequenceComplete()
      {
	 WizardTreeManager manager = getTreeManager();
	 manager.displayQuery(
	    this, 
	    "Sequence Complete", 
	    new String[] {"Go To Next Panel"}, 
	    null);

	 passed = true;

	 manager.setButtonEnabled("next", true);
	 manager.setButtonEnabled("back", true);
	 manager.nextButtonPressed();

      }

      /**
       * Get the runtime classes required by this panel.
       */
      public void addRuntimeResources(Vector resourceVector)
      {
	 super.addRuntimeResources(resourceVector);
	 resourceVector.addElement(new String[] {null, "CustomPanel"});
      }
}


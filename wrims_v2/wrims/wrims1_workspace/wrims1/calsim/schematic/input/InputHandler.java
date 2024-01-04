package calsim.schematic.input;
import calsim.schematic.RequestHandler;
import java.awt.event.*;
/**
 * It listens for double click. When this handler gets a double click
 * it opens a property dialog for the appropriate type of object.
 * E.g. if a channel object is double clicked then the dialog box with
 * channel property panel will be opened.
 */
public class InputHandler implements RequestHandler{
  public InputHandler(){
  }
  /**
   * ignore
   */
  public void clickedOn(Object obj, MouseEvent e){
  }
  /**
   * called when a symbol with reference object obj is double clicked on
   */
  public void doubleClickedOn(Object obj, MouseEvent e){
    // if type == channel show channel properties
    // if type == reservoir show reservoir properties
    // if type == delivery show delivery properties
    // if type == return show return properties
    // if type == inflow show input arc properties
  }
  /**
   * called when a symbol with reference object obj is pressed on
   */
  public void pressedOn(Object obj, MouseEvent e){
  }
  /**
   * called when a symbol with reference object obj is released on
   */
  public void releasedOn(Object obj, MouseEvent e){
  }
  /**
   * called when a symbol with reference object obj is moved over
   */
  public void movedOver(Object obj, MouseEvent e){
  }
  /**
   * called when a symbol with reference object obj is dragged 
   */
  public void draggedTo(Object obj, MouseEvent e){
  }
}

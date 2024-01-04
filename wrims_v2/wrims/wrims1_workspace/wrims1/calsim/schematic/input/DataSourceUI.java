package calsim.schematic.input;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import vista.gui.*;
/**
 * Data source UI is a panel to display data source. 
 *
 * @author Yan-Ping Zuo
 * @version $Id: DataSourceUI.java, v 1.0 11/04/1999
 */
public class DataSourceUI extends JPanel {
  public static boolean DEBUG = true;
  private DataSource _ds;
  private JComboBox _sourceComb, _unitsComb;
  private JTextField _numField;
  /**
   * Constructor
   */
  public DataSourceUI(DataSource ds){
    _ds = ds;
    setLayout(new XYGridLayout(30, 10));
    _sourceComb = createSourceComb();
    _numField = createNumField();
    _unitsComb = createUnitsComb();
    add(_sourceComb, new Rectangle(1,1,10,2));
    add(_numField, new Rectangle(12,1,10,2));
    add(_unitsComb, new Rectangle(23,1,5,2));
    
  }
  /**
   * Create a combo box which contains the list of data types. 
   * When an item is selected, the data type in the data source model will be set.
   */
  public JComboBox createSourceComb(){
    JComboBox sourceComb = new JComboBox(DataSource.sourceList);
    sourceComb.setEditable(false);
    sourceComb.setSelectedItem(_ds.getDataType());
    sourceComb.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
	selectSource();
      }
    });
    return sourceComb;
  }
  /**
   * Create the text field for entering number, it's only editable for number data source.  
   * When user press enter, the number in the data source model will be set.
   */
  public JTextField createNumField() {
    _numField = new JTextField(10);
    if (!_ds.getDataType().equals(DataSource.NUMBER))
      _numField.setEditable(false);    
    else	      
      _numField.setText(String.valueOf(_ds.getNumber()));
    _numField.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
	numberEntered();
      }
    });
    return _numField;
  }
  /**
   * Create a combo box which contains the list of units. 
   * When an item is selected, the units in the data source model will be set.
   */
  public JComboBox createUnitsComb(){
    JComboBox unitsComb = new JComboBox(DataSource.unitsList);
    unitsComb.setEditable(true);
    unitsComb.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
	selectUnits();
      }
    });
    return unitsComb;
  }
  /**
   * Event handler of data source combo box, it set the data type of the data source and set the 
   * editable property of the number text field.
   */
  public void selectSource() {
    String dataType = (String) _sourceComb.getSelectedItem();
    _ds.setDataType(dataType);
    if (DEBUG)
      System.out.print("Data type = " + dataType);
    if (dataType.equals(DataSource.NUMBER))
      _numField.setEditable(true);
    else {
      _numField.setText("");
      _numField.setEditable(false);
    }
  }
  /**
   * Event handler of number text field, it validate the number field, and  set the number of 
   * the data source.
   */
  public void numberEntered() {
    try {
      Double db = new Double(_numField.getText());
      double number = db.doubleValue();
      _ds.setNumber(number);
    if (DEBUG)
      System.out.print("number = " + number);
    }catch (Exception e){
      VistaUtils.displayException(this, e);
    }
  }
  /**
   * Event handler of units combo box, it set the units of the data source.
   */
  public void selectUnits() {
    String units = (String) _sourceComb.getSelectedItem();
    _ds.setUnits(units);
    if (DEBUG)
      System.out.print("units = " + units);
  }

}

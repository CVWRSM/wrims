package calsim.schematic.input;
/**
 * Data source type could be Number, Lookup, Timeseries, WRESL, or None.
 * This class handles the manipulating of the values of these data sources.
 *
 * @author Yan-Ping Zuo
 * @version $Id: DataSource.java, v 1.0 11/02/1999
 */
public class DataSource {
  public static String LOOKUP = "LOOKUP";
  public static String TS = "TIMESERIES";
  public static String NUMBER = "NUMBER";
  public static String WRESL = "WRESL";
  public static String NONE = "NONE";
  public static String TAF = "TAF";
  public static String CFS = "CFS";
  public static String [] sourceList = { "NUMBER", "LOOKUP", "TIMESERIES", "WRESL", "NONE" };
  public static String [] unitsList = { "TAF", "CFS" };
  private String _dataType;
  private double _number;
  private String _units;
  /*
   * Constructor
   */
  public DataSource(){
    _dataType = NUMBER;
    _number = 0;
    _units = TAF;
  }
  /*
   * Constructor
   */
  public DataSource(String dataType){
    _dataType = dataType.toUpperCase();
    _units = TAF;
  }
  /*
   * Constructor
   */
  public DataSource(String dataType, String units){
    _dataType = dataType.toUpperCase();
    _units = units.toUpperCase();
  }
  /*
   * Constructor
   */
  public DataSource(String dataType, double number, String units){
    _dataType = dataType.toUpperCase();
    _number = number;
    _units = units.toUpperCase();
  }

  /**
   * Set the data type for the data source.
   */
  public void setDataType(String dataType){
    _dataType = dataType.toUpperCase();
  } 
  /**
   * Set the number when the data type is "NUMBER", otherwise throw an exception.
   */
  public void setNumber(double number){
    if (!_dataType.equals(NUMBER))
      throw new RuntimeException("Can't set number, because the data type is not NUMBER");
    else 
      _number = number;
  }
  /**
   * Set the units.
   */
  public void setUnits(String units){
    _units = units.toUpperCase();
  }
  /**
   * Return the data type of the data source.
   */
  public String getDataType(){
    return _dataType;
  }
  /**
   * Return the number when the data type is "NUMBER", otherwise throw an exception.
   */
  public double getNumber(){
    if (!_dataType.equals(NUMBER))
      throw new RuntimeException("Can't get number, because the data type is not NUMBER");
    else 
      return _number;
  }
  /**
   * Return units.
   */
  public String getUnits(){
    return _units;
  }
}

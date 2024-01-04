package calsim.schematic.input;
/**
 * This class is the superclass of all the property classes of objects
 * in the schematic.
 */
public interface ObjectProperties{
  /**
   * returns the name of this object
   */
  public String getName();
  /**
   * returns the description
   */
  public String getDescription();
  /**
   * returns a data source for the given property name.
   * E.g. for the channel property name = "MINIMUM FLOW" this method
   * returns the data source object representing that property.
   */
  public DataSource getDataSource(String propertyName);
  /**
   *
   */
  public String [] getAllPropertyNames();
}





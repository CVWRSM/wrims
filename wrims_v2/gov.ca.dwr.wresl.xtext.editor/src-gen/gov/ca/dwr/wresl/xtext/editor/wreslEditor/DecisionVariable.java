/**
 */
package gov.ca.dwr.wresl.xtext.editor.wreslEditor;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Decision Variable</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.DecisionVariable#getTa <em>Ta</em>}</li>
 * </ul>
 * </p>
 *
 * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEditorPackage#getDecisionVariable()
 * @model
 * @generated
 */
public interface DecisionVariable extends Variable
{
  /**
   * Returns the value of the '<em><b>Ta</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Ta</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Ta</em>' containment reference.
   * @see #setTa(TimeArraySize)
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEditorPackage#getDecisionVariable_Ta()
   * @model containment="true"
   * @generated
   */
  TimeArraySize getTa();

  /**
   * Sets the value of the '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.DecisionVariable#getTa <em>Ta</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Ta</em>' containment reference.
   * @see #getTa()
   * @generated
   */
  void setTa(TimeArraySize value);

} // DecisionVariable

/**
 */
package gov.ca.dwr.wresl.xtext.editor.wreslEditor;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>SVar Sum</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.SVarSum#getSumContent <em>Sum Content</em>}</li>
 * </ul>
 * </p>
 *
 * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEditorPackage#getSVarSum()
 * @model
 * @generated
 */
public interface SVarSum extends SVar
{
  /**
   * Returns the value of the '<em><b>Sum Content</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Sum Content</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Sum Content</em>' containment reference.
   * @see #setSumContent(SumContent)
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEditorPackage#getSVarSum_SumContent()
   * @model containment="true"
   * @generated
   */
  SumContent getSumContent();

  /**
   * Sets the value of the '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.SVarSum#getSumContent <em>Sum Content</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Sum Content</em>' containment reference.
   * @see #getSumContent()
   * @generated
   */
  void setSumContent(SumContent value);

} // SVarSum

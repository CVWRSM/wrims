/**
 */
package gov.ca.dwr.wresl.xtext.editor.wreslEditor;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Lhs Lt Rhs</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.LhsLtRhs#getPenalty <em>Penalty</em>}</li>
 * </ul>
 * </p>
 *
 * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEditorPackage#getLhsLtRhs()
 * @model
 * @generated
 */
public interface LhsLtRhs extends EObject
{
  /**
   * Returns the value of the '<em><b>Penalty</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Penalty</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Penalty</em>' containment reference.
   * @see #setPenalty(Penalty)
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEditorPackage#getLhsLtRhs_Penalty()
   * @model containment="true"
   * @generated
   */
  Penalty getPenalty();

  /**
   * Sets the value of the '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.LhsLtRhs#getPenalty <em>Penalty</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Penalty</em>' containment reference.
   * @see #getPenalty()
   * @generated
   */
  void setPenalty(Penalty value);

} // LhsLtRhs

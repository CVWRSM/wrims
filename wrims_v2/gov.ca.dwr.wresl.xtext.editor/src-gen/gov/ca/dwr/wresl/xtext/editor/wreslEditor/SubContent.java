/**
 */
package gov.ca.dwr.wresl.xtext.editor.wreslEditor;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Sub Content</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.SubContent#getGt <em>Gt</em>}</li>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.SubContent#getLt <em>Lt</em>}</li>
 * </ul>
 * </p>
 *
 * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEditorPackage#getSubContent()
 * @model
 * @generated
 */
public interface SubContent extends EObject
{
  /**
   * Returns the value of the '<em><b>Gt</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Gt</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Gt</em>' containment reference.
   * @see #setGt(LhsGtRhs)
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEditorPackage#getSubContent_Gt()
   * @model containment="true"
   * @generated
   */
  LhsGtRhs getGt();

  /**
   * Sets the value of the '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.SubContent#getGt <em>Gt</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Gt</em>' containment reference.
   * @see #getGt()
   * @generated
   */
  void setGt(LhsGtRhs value);

  /**
   * Returns the value of the '<em><b>Lt</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Lt</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Lt</em>' containment reference.
   * @see #setLt(LhsLtRhs)
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEditorPackage#getSubContent_Lt()
   * @model containment="true"
   * @generated
   */
  LhsLtRhs getLt();

  /**
   * Sets the value of the '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.SubContent#getLt <em>Lt</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Lt</em>' containment reference.
   * @see #getLt()
   * @generated
   */
  void setLt(LhsLtRhs value);

} // SubContent

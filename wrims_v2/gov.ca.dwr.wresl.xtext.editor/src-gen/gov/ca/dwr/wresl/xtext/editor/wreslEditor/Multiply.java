/**
 */
package gov.ca.dwr.wresl.xtext.editor.wreslEditor;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Multiply</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.Multiply#getU1 <em>U1</em>}</li>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.Multiply#getU2 <em>U2</em>}</li>
 * </ul>
 * </p>
 *
 * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEditorPackage#getMultiply()
 * @model
 * @generated
 */
public interface Multiply extends EObject
{
  /**
   * Returns the value of the '<em><b>U1</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>U1</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>U1</em>' containment reference.
   * @see #setU1(Unary)
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEditorPackage#getMultiply_U1()
   * @model containment="true"
   * @generated
   */
  Unary getU1();

  /**
   * Sets the value of the '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.Multiply#getU1 <em>U1</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>U1</em>' containment reference.
   * @see #getU1()
   * @generated
   */
  void setU1(Unary value);

  /**
   * Returns the value of the '<em><b>U2</b></em>' containment reference list.
   * The list contents are of type {@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.Unary}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>U2</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>U2</em>' containment reference list.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEditorPackage#getMultiply_U2()
   * @model containment="true"
   * @generated
   */
  EList<Unary> getU2();

} // Multiply

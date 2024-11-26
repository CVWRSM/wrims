/**
 */
package gov.ca.dwr.wresl.xtext.editor.wreslEditor;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>If Term</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.IfTerm#getElseifterm <em>Elseifterm</em>}</li>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.IfTerm#getElseterm <em>Elseterm</em>}</li>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.IfTerm#getLogical <em>Logical</em>}</li>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.IfTerm#getPattern <em>Pattern</em>}</li>
 * </ul>
 * </p>
 *
 * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEditorPackage#getIfTerm()
 * @model
 * @generated
 */
public interface IfTerm extends IfIncItems
{
  /**
   * Returns the value of the '<em><b>Elseifterm</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Elseifterm</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Elseifterm</em>' containment reference.
   * @see #setElseifterm(ElseIfTerm)
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEditorPackage#getIfTerm_Elseifterm()
   * @model containment="true"
   * @generated
   */
  ElseIfTerm getElseifterm();

  /**
   * Sets the value of the '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.IfTerm#getElseifterm <em>Elseifterm</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Elseifterm</em>' containment reference.
   * @see #getElseifterm()
   * @generated
   */
  void setElseifterm(ElseIfTerm value);

  /**
   * Returns the value of the '<em><b>Elseterm</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Elseterm</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Elseterm</em>' containment reference.
   * @see #setElseterm(ElseTerm)
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEditorPackage#getIfTerm_Elseterm()
   * @model containment="true"
   * @generated
   */
  ElseTerm getElseterm();

  /**
   * Sets the value of the '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.IfTerm#getElseterm <em>Elseterm</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Elseterm</em>' containment reference.
   * @see #getElseterm()
   * @generated
   */
  void setElseterm(ElseTerm value);

  /**
   * Returns the value of the '<em><b>Logical</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Logical</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Logical</em>' containment reference.
   * @see #setLogical(LogicalExpression)
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEditorPackage#getIfTerm_Logical()
   * @model containment="true"
   * @generated
   */
  LogicalExpression getLogical();

  /**
   * Sets the value of the '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.IfTerm#getLogical <em>Logical</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Logical</em>' containment reference.
   * @see #getLogical()
   * @generated
   */
  void setLogical(LogicalExpression value);

  /**
   * Returns the value of the '<em><b>Pattern</b></em>' containment reference list.
   * The list contents are of type {@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.Pattern}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Pattern</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Pattern</em>' containment reference list.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEditorPackage#getIfTerm_Pattern()
   * @model containment="true"
   * @generated
   */
  EList<Pattern> getPattern();

} // IfTerm

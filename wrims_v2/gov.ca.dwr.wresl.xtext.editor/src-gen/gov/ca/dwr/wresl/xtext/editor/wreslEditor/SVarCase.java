/**
 */
package gov.ca.dwr.wresl.xtext.editor.wreslEditor;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>SVar Case</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.SVarCase#getCaseContent <em>Case Content</em>}</li>
 * </ul>
 * </p>
 *
 * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEditorPackage#getSVarCase()
 * @model
 * @generated
 */
public interface SVarCase extends SVar
{
  /**
   * Returns the value of the '<em><b>Case Content</b></em>' containment reference list.
   * The list contents are of type {@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.CaseContent}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Case Content</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Case Content</em>' containment reference list.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEditorPackage#getSVarCase_CaseContent()
   * @model containment="true"
   * @generated
   */
  EList<CaseContent> getCaseContent();

} // SVarCase

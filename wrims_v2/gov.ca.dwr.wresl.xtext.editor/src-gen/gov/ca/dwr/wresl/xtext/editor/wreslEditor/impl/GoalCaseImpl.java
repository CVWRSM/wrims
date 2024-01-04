/**
 */
package gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl;

import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Expression;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.GoalCase;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.GoalCaseContent;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.GoalNoCaseContent;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEditorPackage;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Goal Case</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.GoalCaseImpl#getLhs <em>Lhs</em>}</li>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.GoalCaseImpl#getContent <em>Content</em>}</li>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.GoalCaseImpl#getCaseContent <em>Case Content</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class GoalCaseImpl extends MinimalEObjectImpl.Container implements GoalCase
{
  /**
   * The cached value of the '{@link #getLhs() <em>Lhs</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLhs()
   * @generated
   * @ordered
   */
  protected Expression lhs;

  /**
   * The cached value of the '{@link #getContent() <em>Content</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getContent()
   * @generated
   * @ordered
   */
  protected GoalNoCaseContent content;

  /**
   * The cached value of the '{@link #getCaseContent() <em>Case Content</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getCaseContent()
   * @generated
   * @ordered
   */
  protected EList<GoalCaseContent> caseContent;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected GoalCaseImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return WreslEditorPackage.Literals.GOAL_CASE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Expression getLhs()
  {
    return lhs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetLhs(Expression newLhs, NotificationChain msgs)
  {
    Expression oldLhs = lhs;
    lhs = newLhs;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WreslEditorPackage.GOAL_CASE__LHS, oldLhs, newLhs);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setLhs(Expression newLhs)
  {
    if (newLhs != lhs)
    {
      NotificationChain msgs = null;
      if (lhs != null)
        msgs = ((InternalEObject)lhs).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.GOAL_CASE__LHS, null, msgs);
      if (newLhs != null)
        msgs = ((InternalEObject)newLhs).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.GOAL_CASE__LHS, null, msgs);
      msgs = basicSetLhs(newLhs, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, WreslEditorPackage.GOAL_CASE__LHS, newLhs, newLhs));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public GoalNoCaseContent getContent()
  {
    return content;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetContent(GoalNoCaseContent newContent, NotificationChain msgs)
  {
    GoalNoCaseContent oldContent = content;
    content = newContent;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WreslEditorPackage.GOAL_CASE__CONTENT, oldContent, newContent);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setContent(GoalNoCaseContent newContent)
  {
    if (newContent != content)
    {
      NotificationChain msgs = null;
      if (content != null)
        msgs = ((InternalEObject)content).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.GOAL_CASE__CONTENT, null, msgs);
      if (newContent != null)
        msgs = ((InternalEObject)newContent).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.GOAL_CASE__CONTENT, null, msgs);
      msgs = basicSetContent(newContent, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, WreslEditorPackage.GOAL_CASE__CONTENT, newContent, newContent));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<GoalCaseContent> getCaseContent()
  {
    if (caseContent == null)
    {
      caseContent = new EObjectContainmentEList<GoalCaseContent>(GoalCaseContent.class, this, WreslEditorPackage.GOAL_CASE__CASE_CONTENT);
    }
    return caseContent;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
      case WreslEditorPackage.GOAL_CASE__LHS:
        return basicSetLhs(null, msgs);
      case WreslEditorPackage.GOAL_CASE__CONTENT:
        return basicSetContent(null, msgs);
      case WreslEditorPackage.GOAL_CASE__CASE_CONTENT:
        return ((InternalEList<?>)getCaseContent()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case WreslEditorPackage.GOAL_CASE__LHS:
        return getLhs();
      case WreslEditorPackage.GOAL_CASE__CONTENT:
        return getContent();
      case WreslEditorPackage.GOAL_CASE__CASE_CONTENT:
        return getCaseContent();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case WreslEditorPackage.GOAL_CASE__LHS:
        setLhs((Expression)newValue);
        return;
      case WreslEditorPackage.GOAL_CASE__CONTENT:
        setContent((GoalNoCaseContent)newValue);
        return;
      case WreslEditorPackage.GOAL_CASE__CASE_CONTENT:
        getCaseContent().clear();
        getCaseContent().addAll((Collection<? extends GoalCaseContent>)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case WreslEditorPackage.GOAL_CASE__LHS:
        setLhs((Expression)null);
        return;
      case WreslEditorPackage.GOAL_CASE__CONTENT:
        setContent((GoalNoCaseContent)null);
        return;
      case WreslEditorPackage.GOAL_CASE__CASE_CONTENT:
        getCaseContent().clear();
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case WreslEditorPackage.GOAL_CASE__LHS:
        return lhs != null;
      case WreslEditorPackage.GOAL_CASE__CONTENT:
        return content != null;
      case WreslEditorPackage.GOAL_CASE__CASE_CONTENT:
        return caseContent != null && !caseContent.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //GoalCaseImpl

/**
 */
package gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl;

import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Expression;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.GoalNoCaseContent;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.SubContent;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEditorPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Goal No Case Content</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.GoalNoCaseContentImpl#getRhs <em>Rhs</em>}</li>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.GoalNoCaseContentImpl#getSubContent <em>Sub Content</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class GoalNoCaseContentImpl extends MinimalEObjectImpl.Container implements GoalNoCaseContent
{
  /**
   * The cached value of the '{@link #getRhs() <em>Rhs</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRhs()
   * @generated
   * @ordered
   */
  protected Expression rhs;

  /**
   * The cached value of the '{@link #getSubContent() <em>Sub Content</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSubContent()
   * @generated
   * @ordered
   */
  protected SubContent subContent;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected GoalNoCaseContentImpl()
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
    return WreslEditorPackage.Literals.GOAL_NO_CASE_CONTENT;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Expression getRhs()
  {
    return rhs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetRhs(Expression newRhs, NotificationChain msgs)
  {
    Expression oldRhs = rhs;
    rhs = newRhs;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WreslEditorPackage.GOAL_NO_CASE_CONTENT__RHS, oldRhs, newRhs);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setRhs(Expression newRhs)
  {
    if (newRhs != rhs)
    {
      NotificationChain msgs = null;
      if (rhs != null)
        msgs = ((InternalEObject)rhs).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.GOAL_NO_CASE_CONTENT__RHS, null, msgs);
      if (newRhs != null)
        msgs = ((InternalEObject)newRhs).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.GOAL_NO_CASE_CONTENT__RHS, null, msgs);
      msgs = basicSetRhs(newRhs, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, WreslEditorPackage.GOAL_NO_CASE_CONTENT__RHS, newRhs, newRhs));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SubContent getSubContent()
  {
    return subContent;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetSubContent(SubContent newSubContent, NotificationChain msgs)
  {
    SubContent oldSubContent = subContent;
    subContent = newSubContent;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WreslEditorPackage.GOAL_NO_CASE_CONTENT__SUB_CONTENT, oldSubContent, newSubContent);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setSubContent(SubContent newSubContent)
  {
    if (newSubContent != subContent)
    {
      NotificationChain msgs = null;
      if (subContent != null)
        msgs = ((InternalEObject)subContent).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.GOAL_NO_CASE_CONTENT__SUB_CONTENT, null, msgs);
      if (newSubContent != null)
        msgs = ((InternalEObject)newSubContent).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.GOAL_NO_CASE_CONTENT__SUB_CONTENT, null, msgs);
      msgs = basicSetSubContent(newSubContent, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, WreslEditorPackage.GOAL_NO_CASE_CONTENT__SUB_CONTENT, newSubContent, newSubContent));
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
      case WreslEditorPackage.GOAL_NO_CASE_CONTENT__RHS:
        return basicSetRhs(null, msgs);
      case WreslEditorPackage.GOAL_NO_CASE_CONTENT__SUB_CONTENT:
        return basicSetSubContent(null, msgs);
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
      case WreslEditorPackage.GOAL_NO_CASE_CONTENT__RHS:
        return getRhs();
      case WreslEditorPackage.GOAL_NO_CASE_CONTENT__SUB_CONTENT:
        return getSubContent();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case WreslEditorPackage.GOAL_NO_CASE_CONTENT__RHS:
        setRhs((Expression)newValue);
        return;
      case WreslEditorPackage.GOAL_NO_CASE_CONTENT__SUB_CONTENT:
        setSubContent((SubContent)newValue);
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
      case WreslEditorPackage.GOAL_NO_CASE_CONTENT__RHS:
        setRhs((Expression)null);
        return;
      case WreslEditorPackage.GOAL_NO_CASE_CONTENT__SUB_CONTENT:
        setSubContent((SubContent)null);
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
      case WreslEditorPackage.GOAL_NO_CASE_CONTENT__RHS:
        return rhs != null;
      case WreslEditorPackage.GOAL_NO_CASE_CONTENT__SUB_CONTENT:
        return subContent != null;
    }
    return super.eIsSet(featureID);
  }

} //GoalNoCaseContentImpl

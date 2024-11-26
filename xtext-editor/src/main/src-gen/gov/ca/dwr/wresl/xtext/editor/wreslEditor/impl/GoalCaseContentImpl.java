/**
 */
package gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl;

import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Condition;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Expression;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.GoalCaseContent;
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
 * An implementation of the model object '<em><b>Goal Case Content</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.GoalCaseContentImpl#getCaseName <em>Case Name</em>}</li>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.GoalCaseContentImpl#getCondition <em>Condition</em>}</li>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.GoalCaseContentImpl#getRhs <em>Rhs</em>}</li>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.GoalCaseContentImpl#getSubContent <em>Sub Content</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class GoalCaseContentImpl extends MinimalEObjectImpl.Container implements GoalCaseContent
{
  /**
   * The default value of the '{@link #getCaseName() <em>Case Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getCaseName()
   * @generated
   * @ordered
   */
  protected static final String CASE_NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getCaseName() <em>Case Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getCaseName()
   * @generated
   * @ordered
   */
  protected String caseName = CASE_NAME_EDEFAULT;

  /**
   * The cached value of the '{@link #getCondition() <em>Condition</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getCondition()
   * @generated
   * @ordered
   */
  protected Condition condition;

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
  protected GoalCaseContentImpl()
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
    return WreslEditorPackage.Literals.GOAL_CASE_CONTENT;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getCaseName()
  {
    return caseName;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setCaseName(String newCaseName)
  {
    String oldCaseName = caseName;
    caseName = newCaseName;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, WreslEditorPackage.GOAL_CASE_CONTENT__CASE_NAME, oldCaseName, caseName));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Condition getCondition()
  {
    return condition;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetCondition(Condition newCondition, NotificationChain msgs)
  {
    Condition oldCondition = condition;
    condition = newCondition;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WreslEditorPackage.GOAL_CASE_CONTENT__CONDITION, oldCondition, newCondition);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setCondition(Condition newCondition)
  {
    if (newCondition != condition)
    {
      NotificationChain msgs = null;
      if (condition != null)
        msgs = ((InternalEObject)condition).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.GOAL_CASE_CONTENT__CONDITION, null, msgs);
      if (newCondition != null)
        msgs = ((InternalEObject)newCondition).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.GOAL_CASE_CONTENT__CONDITION, null, msgs);
      msgs = basicSetCondition(newCondition, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, WreslEditorPackage.GOAL_CASE_CONTENT__CONDITION, newCondition, newCondition));
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
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WreslEditorPackage.GOAL_CASE_CONTENT__RHS, oldRhs, newRhs);
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
        msgs = ((InternalEObject)rhs).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.GOAL_CASE_CONTENT__RHS, null, msgs);
      if (newRhs != null)
        msgs = ((InternalEObject)newRhs).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.GOAL_CASE_CONTENT__RHS, null, msgs);
      msgs = basicSetRhs(newRhs, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, WreslEditorPackage.GOAL_CASE_CONTENT__RHS, newRhs, newRhs));
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
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WreslEditorPackage.GOAL_CASE_CONTENT__SUB_CONTENT, oldSubContent, newSubContent);
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
        msgs = ((InternalEObject)subContent).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.GOAL_CASE_CONTENT__SUB_CONTENT, null, msgs);
      if (newSubContent != null)
        msgs = ((InternalEObject)newSubContent).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.GOAL_CASE_CONTENT__SUB_CONTENT, null, msgs);
      msgs = basicSetSubContent(newSubContent, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, WreslEditorPackage.GOAL_CASE_CONTENT__SUB_CONTENT, newSubContent, newSubContent));
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
      case WreslEditorPackage.GOAL_CASE_CONTENT__CONDITION:
        return basicSetCondition(null, msgs);
      case WreslEditorPackage.GOAL_CASE_CONTENT__RHS:
        return basicSetRhs(null, msgs);
      case WreslEditorPackage.GOAL_CASE_CONTENT__SUB_CONTENT:
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
      case WreslEditorPackage.GOAL_CASE_CONTENT__CASE_NAME:
        return getCaseName();
      case WreslEditorPackage.GOAL_CASE_CONTENT__CONDITION:
        return getCondition();
      case WreslEditorPackage.GOAL_CASE_CONTENT__RHS:
        return getRhs();
      case WreslEditorPackage.GOAL_CASE_CONTENT__SUB_CONTENT:
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
      case WreslEditorPackage.GOAL_CASE_CONTENT__CASE_NAME:
        setCaseName((String)newValue);
        return;
      case WreslEditorPackage.GOAL_CASE_CONTENT__CONDITION:
        setCondition((Condition)newValue);
        return;
      case WreslEditorPackage.GOAL_CASE_CONTENT__RHS:
        setRhs((Expression)newValue);
        return;
      case WreslEditorPackage.GOAL_CASE_CONTENT__SUB_CONTENT:
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
      case WreslEditorPackage.GOAL_CASE_CONTENT__CASE_NAME:
        setCaseName(CASE_NAME_EDEFAULT);
        return;
      case WreslEditorPackage.GOAL_CASE_CONTENT__CONDITION:
        setCondition((Condition)null);
        return;
      case WreslEditorPackage.GOAL_CASE_CONTENT__RHS:
        setRhs((Expression)null);
        return;
      case WreslEditorPackage.GOAL_CASE_CONTENT__SUB_CONTENT:
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
      case WreslEditorPackage.GOAL_CASE_CONTENT__CASE_NAME:
        return CASE_NAME_EDEFAULT == null ? caseName != null : !CASE_NAME_EDEFAULT.equals(caseName);
      case WreslEditorPackage.GOAL_CASE_CONTENT__CONDITION:
        return condition != null;
      case WreslEditorPackage.GOAL_CASE_CONTENT__RHS:
        return rhs != null;
      case WreslEditorPackage.GOAL_CASE_CONTENT__SUB_CONTENT:
        return subContent != null;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy()) return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (caseName: ");
    result.append(caseName);
    result.append(')');
    return result.toString();
  }

} //GoalCaseContentImpl

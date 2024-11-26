/**
 */
package gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl;

import gov.ca.dwr.wresl.xtext.editor.wreslEditor.ConditionalUnary;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.LogicalExpression;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEditorPackage;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Logical Expression</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.LogicalExpressionImpl#getC1 <em>C1</em>}</li>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.LogicalExpressionImpl#getC2 <em>C2</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class LogicalExpressionImpl extends ConditionalTermImpl implements LogicalExpression
{
  /**
   * The cached value of the '{@link #getC1() <em>C1</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getC1()
   * @generated
   * @ordered
   */
  protected ConditionalUnary c1;

  /**
   * The cached value of the '{@link #getC2() <em>C2</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getC2()
   * @generated
   * @ordered
   */
  protected EList<ConditionalUnary> c2;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected LogicalExpressionImpl()
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
    return WreslEditorPackage.Literals.LOGICAL_EXPRESSION;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ConditionalUnary getC1()
  {
    return c1;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetC1(ConditionalUnary newC1, NotificationChain msgs)
  {
    ConditionalUnary oldC1 = c1;
    c1 = newC1;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WreslEditorPackage.LOGICAL_EXPRESSION__C1, oldC1, newC1);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setC1(ConditionalUnary newC1)
  {
    if (newC1 != c1)
    {
      NotificationChain msgs = null;
      if (c1 != null)
        msgs = ((InternalEObject)c1).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.LOGICAL_EXPRESSION__C1, null, msgs);
      if (newC1 != null)
        msgs = ((InternalEObject)newC1).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.LOGICAL_EXPRESSION__C1, null, msgs);
      msgs = basicSetC1(newC1, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, WreslEditorPackage.LOGICAL_EXPRESSION__C1, newC1, newC1));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<ConditionalUnary> getC2()
  {
    if (c2 == null)
    {
      c2 = new EObjectContainmentEList<ConditionalUnary>(ConditionalUnary.class, this, WreslEditorPackage.LOGICAL_EXPRESSION__C2);
    }
    return c2;
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
      case WreslEditorPackage.LOGICAL_EXPRESSION__C1:
        return basicSetC1(null, msgs);
      case WreslEditorPackage.LOGICAL_EXPRESSION__C2:
        return ((InternalEList<?>)getC2()).basicRemove(otherEnd, msgs);
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
      case WreslEditorPackage.LOGICAL_EXPRESSION__C1:
        return getC1();
      case WreslEditorPackage.LOGICAL_EXPRESSION__C2:
        return getC2();
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
      case WreslEditorPackage.LOGICAL_EXPRESSION__C1:
        setC1((ConditionalUnary)newValue);
        return;
      case WreslEditorPackage.LOGICAL_EXPRESSION__C2:
        getC2().clear();
        getC2().addAll((Collection<? extends ConditionalUnary>)newValue);
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
      case WreslEditorPackage.LOGICAL_EXPRESSION__C1:
        setC1((ConditionalUnary)null);
        return;
      case WreslEditorPackage.LOGICAL_EXPRESSION__C2:
        getC2().clear();
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
      case WreslEditorPackage.LOGICAL_EXPRESSION__C1:
        return c1 != null;
      case WreslEditorPackage.LOGICAL_EXPRESSION__C2:
        return c2 != null && !c2.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //LogicalExpressionImpl

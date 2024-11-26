/**
 */
package gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl;

import gov.ca.dwr.wresl.xtext.editor.wreslEditor.DecisionVariable;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.TimeArraySize;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEditorPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Decision Variable</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.DecisionVariableImpl#getTa <em>Ta</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DecisionVariableImpl extends VariableImpl implements DecisionVariable
{
  /**
   * The cached value of the '{@link #getTa() <em>Ta</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTa()
   * @generated
   * @ordered
   */
  protected TimeArraySize ta;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected DecisionVariableImpl()
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
    return WreslEditorPackage.Literals.DECISION_VARIABLE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TimeArraySize getTa()
  {
    return ta;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetTa(TimeArraySize newTa, NotificationChain msgs)
  {
    TimeArraySize oldTa = ta;
    ta = newTa;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WreslEditorPackage.DECISION_VARIABLE__TA, oldTa, newTa);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setTa(TimeArraySize newTa)
  {
    if (newTa != ta)
    {
      NotificationChain msgs = null;
      if (ta != null)
        msgs = ((InternalEObject)ta).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.DECISION_VARIABLE__TA, null, msgs);
      if (newTa != null)
        msgs = ((InternalEObject)newTa).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.DECISION_VARIABLE__TA, null, msgs);
      msgs = basicSetTa(newTa, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, WreslEditorPackage.DECISION_VARIABLE__TA, newTa, newTa));
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
      case WreslEditorPackage.DECISION_VARIABLE__TA:
        return basicSetTa(null, msgs);
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
      case WreslEditorPackage.DECISION_VARIABLE__TA:
        return getTa();
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
      case WreslEditorPackage.DECISION_VARIABLE__TA:
        setTa((TimeArraySize)newValue);
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
      case WreslEditorPackage.DECISION_VARIABLE__TA:
        setTa((TimeArraySize)null);
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
      case WreslEditorPackage.DECISION_VARIABLE__TA:
        return ta != null;
    }
    return super.eIsSet(featureID);
  }

} //DecisionVariableImpl

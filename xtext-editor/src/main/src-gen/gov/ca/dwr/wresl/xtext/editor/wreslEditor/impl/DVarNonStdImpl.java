/**
 */
package gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl;

import gov.ca.dwr.wresl.xtext.editor.wreslEditor.DVarNonStd;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.LowerAndOrUpper;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEditorPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>DVar Non Std</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.DVarNonStdImpl#getLowerUpper <em>Lower Upper</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DVarNonStdImpl extends DVarImpl implements DVarNonStd
{
  /**
   * The cached value of the '{@link #getLowerUpper() <em>Lower Upper</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLowerUpper()
   * @generated
   * @ordered
   */
  protected LowerAndOrUpper lowerUpper;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected DVarNonStdImpl()
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
    return WreslEditorPackage.Literals.DVAR_NON_STD;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public LowerAndOrUpper getLowerUpper()
  {
    return lowerUpper;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetLowerUpper(LowerAndOrUpper newLowerUpper, NotificationChain msgs)
  {
    LowerAndOrUpper oldLowerUpper = lowerUpper;
    lowerUpper = newLowerUpper;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WreslEditorPackage.DVAR_NON_STD__LOWER_UPPER, oldLowerUpper, newLowerUpper);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setLowerUpper(LowerAndOrUpper newLowerUpper)
  {
    if (newLowerUpper != lowerUpper)
    {
      NotificationChain msgs = null;
      if (lowerUpper != null)
        msgs = ((InternalEObject)lowerUpper).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.DVAR_NON_STD__LOWER_UPPER, null, msgs);
      if (newLowerUpper != null)
        msgs = ((InternalEObject)newLowerUpper).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.DVAR_NON_STD__LOWER_UPPER, null, msgs);
      msgs = basicSetLowerUpper(newLowerUpper, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, WreslEditorPackage.DVAR_NON_STD__LOWER_UPPER, newLowerUpper, newLowerUpper));
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
      case WreslEditorPackage.DVAR_NON_STD__LOWER_UPPER:
        return basicSetLowerUpper(null, msgs);
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
      case WreslEditorPackage.DVAR_NON_STD__LOWER_UPPER:
        return getLowerUpper();
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
      case WreslEditorPackage.DVAR_NON_STD__LOWER_UPPER:
        setLowerUpper((LowerAndOrUpper)newValue);
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
      case WreslEditorPackage.DVAR_NON_STD__LOWER_UPPER:
        setLowerUpper((LowerAndOrUpper)null);
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
      case WreslEditorPackage.DVAR_NON_STD__LOWER_UPPER:
        return lowerUpper != null;
    }
    return super.eIsSet(featureID);
  }

} //DVarNonStdImpl

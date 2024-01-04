/**
 */
package gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl;

import gov.ca.dwr.wresl.xtext.editor.wreslEditor.LhsGtRhs;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.LhsLtRhs;
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
 * An implementation of the model object '<em><b>Sub Content</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.SubContentImpl#getGt <em>Gt</em>}</li>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.SubContentImpl#getLt <em>Lt</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SubContentImpl extends MinimalEObjectImpl.Container implements SubContent
{
  /**
   * The cached value of the '{@link #getGt() <em>Gt</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getGt()
   * @generated
   * @ordered
   */
  protected LhsGtRhs gt;

  /**
   * The cached value of the '{@link #getLt() <em>Lt</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLt()
   * @generated
   * @ordered
   */
  protected LhsLtRhs lt;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected SubContentImpl()
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
    return WreslEditorPackage.Literals.SUB_CONTENT;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public LhsGtRhs getGt()
  {
    return gt;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetGt(LhsGtRhs newGt, NotificationChain msgs)
  {
    LhsGtRhs oldGt = gt;
    gt = newGt;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WreslEditorPackage.SUB_CONTENT__GT, oldGt, newGt);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setGt(LhsGtRhs newGt)
  {
    if (newGt != gt)
    {
      NotificationChain msgs = null;
      if (gt != null)
        msgs = ((InternalEObject)gt).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.SUB_CONTENT__GT, null, msgs);
      if (newGt != null)
        msgs = ((InternalEObject)newGt).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.SUB_CONTENT__GT, null, msgs);
      msgs = basicSetGt(newGt, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, WreslEditorPackage.SUB_CONTENT__GT, newGt, newGt));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public LhsLtRhs getLt()
  {
    return lt;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetLt(LhsLtRhs newLt, NotificationChain msgs)
  {
    LhsLtRhs oldLt = lt;
    lt = newLt;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WreslEditorPackage.SUB_CONTENT__LT, oldLt, newLt);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setLt(LhsLtRhs newLt)
  {
    if (newLt != lt)
    {
      NotificationChain msgs = null;
      if (lt != null)
        msgs = ((InternalEObject)lt).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.SUB_CONTENT__LT, null, msgs);
      if (newLt != null)
        msgs = ((InternalEObject)newLt).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.SUB_CONTENT__LT, null, msgs);
      msgs = basicSetLt(newLt, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, WreslEditorPackage.SUB_CONTENT__LT, newLt, newLt));
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
      case WreslEditorPackage.SUB_CONTENT__GT:
        return basicSetGt(null, msgs);
      case WreslEditorPackage.SUB_CONTENT__LT:
        return basicSetLt(null, msgs);
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
      case WreslEditorPackage.SUB_CONTENT__GT:
        return getGt();
      case WreslEditorPackage.SUB_CONTENT__LT:
        return getLt();
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
      case WreslEditorPackage.SUB_CONTENT__GT:
        setGt((LhsGtRhs)newValue);
        return;
      case WreslEditorPackage.SUB_CONTENT__LT:
        setLt((LhsLtRhs)newValue);
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
      case WreslEditorPackage.SUB_CONTENT__GT:
        setGt((LhsGtRhs)null);
        return;
      case WreslEditorPackage.SUB_CONTENT__LT:
        setLt((LhsLtRhs)null);
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
      case WreslEditorPackage.SUB_CONTENT__GT:
        return gt != null;
      case WreslEditorPackage.SUB_CONTENT__LT:
        return lt != null;
    }
    return super.eIsSet(featureID);
  }

} //SubContentImpl
